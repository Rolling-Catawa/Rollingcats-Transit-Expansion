package cn.jstxjf_.rte.scripting.util.client;

import cn.jstxjf_.rte.RTE;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.*;
import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.UUID;
import java.util.concurrent.*;

@SuppressWarnings("unused")
public class GraphicsTexture implements Closeable {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private static Field pixelsField;

    static {
        pixelsField = resolvePixelsField();
        if (pixelsField != null) {
            pixelsField.setAccessible(true);
        } else {
            RTE.LOGGER.error("Failed to locate NativeImage pixel pointer field");
        }
    }

    private static Field resolvePixelsField() {
        try {
            return NativeImage.class.getDeclaredField("pixels");
        } catch (NoSuchFieldException ignored) {
        }
        for (Field field : NativeImage.class.getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (field.getType() == long.class && !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                return field;
            }
        }
        return null;
    }

    private volatile DynamicTexture dynamicTexture;
    public final ResourceLocation identifier;
    public BufferedImage bufferedImage;
    public Graphics2D graphics;
    public final int width, height;
    private boolean isClosed = false;
    private volatile long lastUploadMillis = System.currentTimeMillis();
    private volatile long closeRequestedMillis = 0;
    private volatile boolean released = false;
    private boolean forceUpload = true;
    private final java.util.concurrent.atomic.AtomicBoolean uploadQueued = new java.util.concurrent.atomic.AtomicBoolean();
    private int dirtyMinRow = Integer.MAX_VALUE;
    private int dirtyMaxRow = -1;
    private volatile long lastGpuUploadNanos;
    private static final long RELEASE_GRACE_MILLIS = 60000;
    private static final java.util.Set<GraphicsTexture> LIVE =
        java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());

    private static final java.util.Map<String, java.util.concurrent.atomic.AtomicInteger> CREATIONS = new java.util.concurrent.ConcurrentHashMap<>();
    private static volatile long nextCreationReportMillis = 0;
    private static final long CREATION_REPORT_INTERVAL_MILLIS = 10000;
    private static final int CREATION_REPORT_THRESHOLD = 20;
    private static final int RUNAWAY_CREATION_LIMIT = 2048;
    private static final int PER_CALL_CREATION_LIMIT = 512;

    private static void recordCreation() {
        final long now = System.currentTimeMillis();
        if (nextCreationReportMillis == 0) {
            nextCreationReportMillis = now + CREATION_REPORT_INTERVAL_MILLIS;
        } else if (now >= nextCreationReportMillis) {
            nextCreationReportMillis = now + CREATION_REPORT_INTERVAL_MILLIS;
            reportCreations();
        }
        final String script = cn.jstxjf_.rte.scripting.ScriptHolderBase.currentScriptName();
        final cn.jstxjf_.rte.scripting.AbstractScriptContext owner = cn.jstxjf_.rte.scripting.ScriptHolderBase.currentContext();
        if (owner != null && ++owner.rte$texturesThisCall > PER_CALL_CREATION_LIMIT) {
            throw new IllegalStateException("Script " + script + " created more than " + PER_CALL_CREATION_LIMIT
                + " GraphicsTexture objects in a single call; refusing further creation (likely an endless loop in the script)");
        }
        final int created = CREATIONS.computeIfAbsent(script, k -> new java.util.concurrent.atomic.AtomicInteger()).incrementAndGet();
        if (created > RUNAWAY_CREATION_LIMIT && !"?".equals(script)) {
            throw new IllegalStateException("Script " + script + " created more than " + RUNAWAY_CREATION_LIMIT
                + " GraphicsTexture objects within " + CREATION_REPORT_INTERVAL_MILLIS / 1000 + " s; refusing further creation (likely an endless loop in the script)");
        }
    }

    private static void reportCreations() {
        final java.util.Map<String, Integer> snapshot = new java.util.TreeMap<>();
        int total = 0;
        for (final java.util.Map.Entry<String, java.util.concurrent.atomic.AtomicInteger> entry : CREATIONS.entrySet()) {
            final int count = entry.getValue().getAndSet(0);
            if (count > 0) {
                snapshot.put(entry.getKey(), count);
                total += count;
            }
        }
        if (total > CREATION_REPORT_THRESHOLD) {
            RTE.LOGGER.warn("[GraphicsTexture] {} textures created in the last {} s: {}", total, CREATION_REPORT_INTERVAL_MILLIS / 1000, snapshot);
        }
    }

    public GraphicsTexture(int width, int height, ResourceLocation path) {
        recordCreation();
        this.width = width;
        this.height = height;
        dynamicTexture = new DynamicTexture(new NativeImage(width, height, false));
        identifier = path;
        final DynamicTexture created = dynamicTexture;
        RenderSystem.recordRenderCall(() ->
            Minecraft.getInstance().getTextureManager().register(identifier, created));
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        LIVE.add(this);
        final cn.jstxjf_.rte.scripting.AbstractScriptContext owner = cn.jstxjf_.rte.scripting.ScriptHolderBase.currentContext();
        if (owner != null) {
            owner.rte$own(this::releaseIfIdle);
        }
        enforceLiveLimit();
    }

    private static final long OWNED_RELEASE_DELAY_MILLIS = 5000;
    private static final long OWNER_GONE_IDLE_MILLIS = 15000;

    private void releaseIfIdle() {
        if (isClosed) {
            return;
        }
        final long markedAt = System.currentTimeMillis();
        executor.schedule(() -> {
            if (!isClosed && lastUploadMillis <= markedAt) {
                close(0);
            }
        }, OWNER_GONE_IDLE_MILLIS, TimeUnit.MILLISECONDS);
    }

    public static void releaseOwnedLater(cn.jstxjf_.rte.scripting.AbstractScriptContext context) {
        executor.schedule(context::rte$releaseOwned, OWNED_RELEASE_DELAY_MILLIS, TimeUnit.MILLISECONDS);
    }

    private static final long EVICT_LOG_INTERVAL_MILLIS = 10000;
    private static long nextEvictLogMillis = 0;

    public long heapBytes() {
        return (long) width * height * 4L;
    }

    private static long evictedInUse = 0;

    public static long evictedInUseCount() {
        return evictedInUse;
    }

    public static long liveHeapBytes() {
        long total = 0;
        for (final GraphicsTexture texture : LIVE) {
            if (!texture.isClosed) {
                total += texture.heapBytes();
            }
        }
        return total;
    }

    private static void enforceLiveLimit() {
        final long now = System.currentTimeMillis();
        if (now < nextEvictLogMillis) {
            return;
        }
        nextEvictLogMillis = now + EVICT_LOG_INTERVAL_MILLIS;
        final int limit = cn.jstxjf_.rte.ClientConfig.scriptTextureLimit;
        final long budget = (long) cn.jstxjf_.rte.ClientConfig.scriptTextureHeapBudgetMb * 1024L * 1024L;
        final long heapBytes = liveHeapBytes();
        if ((budget > 0 && heapBytes > budget) || (limit > 0 && LIVE.size() > limit)) {
            RTE.LOGGER.warn("[GraphicsTexture] {} live textures use {}MB (limit {}, budget {}MB); in-use textures are never evicted, "
                    + "lower maxTextureSize if memory is tight",
                    LIVE.size(), heapBytes / (1024L * 1024L), limit, cn.jstxjf_.rte.ClientConfig.scriptTextureHeapBudgetMb);
        }
    }

    public static String diagnoseLive() {
        int closed = 0;
        int releasedCount = 0;
        int unregistered = 0;
        int stale = 0;
        final long now = System.currentTimeMillis();
        final net.minecraft.client.renderer.texture.TextureManager manager =
            Minecraft.getInstance().getTextureManager();
        for (final GraphicsTexture texture : LIVE) {
            if (texture.isClosed) {
                closed++;
            }
            if (texture.released) {
                releasedCount++;
            }
            if (now - texture.lastUploadMillis > 10000) {
                stale++;
            }
            try {
                if (manager.getTexture(texture.identifier, null) == null) {
                    unregistered++;
                }
            } catch (Throwable ignored) {
            }
        }
        return String.format(java.util.Locale.ROOT,
            "closed=%d released=%d unregisteredInTextureManager=%d noUploadFor10s=%d",
            closed, releasedCount, unregistered, stale);
    }

    public static int liveCount() {
        return LIVE.size();
    }

    public static void disposeAll() {
        for (final GraphicsTexture texture : new java.util.ArrayList<>(LIVE)) {
            texture.disposeImmediately();
        }
        LIVE.clear();
    }

    private synchronized void disposeImmediately() {
        if (released) {
            return;
        }
        isClosed = true;
        released = true;
        closeRequestedMillis = 0;
        LIVE.remove(this);
        releaseCpuImage();
        final DynamicTexture doomed = dynamicTexture;
        final Runnable release = () -> {
            try {
                final DynamicTexture placeholder = new DynamicTexture(new NativeImage(1, 1, false));
                Minecraft.getInstance().getTextureManager().register(identifier, placeholder);
                dynamicTexture = placeholder;
                if (doomed != null) {
                    doomed.close();
                }
            } catch (Exception e) {
                RTE.LOGGER.error("Failed to dispose GraphicsTexture {}", identifier, e);
            }
        };
        RenderSystem.recordRenderCall(release::run);
    }

    public GraphicsTexture(int width, int height) {
        this(width, height, new ResourceLocation(RTE.MOD_ID,
            String.format("dynamic/graphics/%s", UUID.randomUUID().toString().replace("-", "_"))));
    }

    public static BufferedImage createArgbBufferedImage(BufferedImage src) {
        BufferedImage newImage = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public void upload() { upload(bufferedImage); }

    public synchronized void upload(BufferedImage image) {
        if (image == null) {
            return;
        }
        if (isClosed || released) {
            isClosed = false;
            released = false;
            closeRequestedMillis = 0;
            dynamicTexture = new DynamicTexture(new NativeImage(width, height, false));
            forceUpload = true;
            final DynamicTexture revived = dynamicTexture;
            RenderSystem.recordRenderCall(() ->
                Minecraft.getInstance().getTextureManager().register(identifier, revived));
            LIVE.add(this);
        }
        lastUploadMillis = System.currentTimeMillis();
        enforceLiveLimit();
        if (isClosed) {
            return;
        }
        if (image.getWidth() != width || image.getHeight() != height) {
            return;
        }
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) image = createArgbBufferedImage(image);

        DataBuffer dataBuffer = image.getRaster().getDataBuffer();
        if (!(dataBuffer instanceof DataBufferInt)) {
            return;
        }

        final int[] source = ((DataBufferInt) dataBuffer).getData();
        final int pixelCount = width * height;
        if (source.length < pixelCount) {
            return;
        }
        NativeImage nativeImage = dynamicTexture.getPixels();
        if (nativeImage == null) {
            return;
        }
        if (nativeImage.getWidth() != width || nativeImage.getHeight() != height) {
            return;
        }

        try {
            long pixelAddr = pixelsField != null ? (long) pixelsField.get(nativeImage) : 0;
            if (pixelAddr == 0) {
                return;
            }

            int changedMinRow = forceUpload ? 0 : Integer.MAX_VALUE;
            int changedMaxRow = forceUpload ? height - 1 : -1;
            for (int row = 0; row < height; row++) {
                final int rowStart = row * width;
                boolean rowChanged = false;
                for (int i = rowStart; i < rowStart + width; i++) {
                    final int pixel = source[i];
                    final int abgr = (pixel & 0xFF00FF00) | ((pixel >>> 16) & 0xFF) | ((pixel & 0xFF) << 16);
                    final long address = pixelAddr + ((long) i << 2);
                    if (MemoryUtil.memGetInt(address) != abgr) {
                        MemoryUtil.memPutInt(address, abgr);
                        rowChanged = true;
                    }
                }
                if (rowChanged) {
                    changedMinRow = Math.min(changedMinRow, row);
                    changedMaxRow = Math.max(changedMaxRow, row);
                }
            }
            if (changedMaxRow < changedMinRow) {
                return;
            }
            forceUpload = false;
            dirtyMinRow = Math.min(dirtyMinRow, changedMinRow);
            dirtyMaxRow = Math.max(dirtyMaxRow, changedMaxRow);
            if (uploadQueued.compareAndSet(false, true)) {
                PENDING_UPLOADS.add(this);
            }
        } catch (Exception e) {
            RTE.LOGGER.error("Failed to upload GraphicsTexture", e);
        }
    }



    private static final long GPU_UPLOAD_INTERVAL_NANOS = 33_000_000L;
    private static final java.util.Set<GraphicsTexture> PENDING_UPLOADS =
        java.util.concurrent.ConcurrentHashMap.newKeySet();

    public static void flushPendingUploads() {
        if (PENDING_UPLOADS.isEmpty()) {
            return;
        }
        final long now = System.nanoTime();
        for (final GraphicsTexture texture : PENDING_UPLOADS) {
            if (now - texture.lastGpuUploadNanos < GPU_UPLOAD_INTERVAL_NANOS) {
                continue;
            }
            PENDING_UPLOADS.remove(texture);
            texture.uploadDirtyRows(now);
        }
    }

    private void uploadDirtyRows(long now) {
        final int minRow;
        final int maxRow;
        final DynamicTexture texture;
        synchronized (this) {
            minRow = dirtyMinRow;
            maxRow = dirtyMaxRow;
            dirtyMinRow = Integer.MAX_VALUE;
            dirtyMaxRow = -1;
            uploadQueued.set(false);
            texture = dynamicTexture;
        }
        if (maxRow < minRow || texture == null) {
            return;
        }
        final NativeImage pixels = texture.getPixels();
        if (pixels == null || pixels.getWidth() != width || pixels.getHeight() != height) {
            return;
        }
        lastGpuUploadNanos = now;
        try {
            texture.bind();
            pixels.upload(0, 0, minRow, 0, minRow, width, maxRow - minRow + 1, false, false);
        } catch (Exception e) {
            RTE.LOGGER.error("Failed uploading DynamicTexture {}", identifier, e);
        }
    }

    public boolean isClosed() { return isClosed; }

    @Override
    public void close() { close(10000); }

    public void close(int delay) {
        if (isClosed) return;
        isClosed = true;
        closeRequestedMillis = System.currentTimeMillis();
        LIVE.remove(this);
        releaseCpuImage();
        executor.schedule(this::releaseIfUnused, Math.max(delay, RELEASE_GRACE_MILLIS), TimeUnit.MILLISECONDS);
    }

    private synchronized void releaseCpuImage() {
        try {
            if (graphics != null) graphics.dispose();
        } catch (Exception ignored) {
        }
        graphics = null;
        bufferedImage = null;
    }

    private static boolean isGamePaused() {
        try {
            final Minecraft minecraft = Minecraft.getInstance();
            return minecraft == null || minecraft.level == null || minecraft.isPaused();
        } catch (Exception e) {
            return true;
        }
    }

    private void releaseIfUnused() {
        final long requestedAt = closeRequestedMillis;
        if (!isClosed || requestedAt == 0 || lastUploadMillis > requestedAt) {
            return;
        }
        if (System.currentTimeMillis() - lastUploadMillis < RELEASE_GRACE_MILLIS || isGamePaused()) {
            executor.schedule(this::releaseIfUnused, RELEASE_GRACE_MILLIS, TimeUnit.MILLISECONDS);
            return;
        }
        RenderSystem.recordRenderCall(() -> {
            synchronized (this) {
                if (!isClosed || lastUploadMillis > closeRequestedMillis) {
                    return;
                }
                final DynamicTexture doomed = dynamicTexture;
                released = true;
                LIVE.remove(this);
                releaseCpuImage();
                try {
                    final DynamicTexture placeholder = new DynamicTexture(new NativeImage(1, 1, false));
                    Minecraft.getInstance().getTextureManager().register(identifier, placeholder);
                    dynamicTexture = placeholder;
                    doomed.close();
                } catch (Exception e) {
                    RTE.LOGGER.error("Failed to close GraphicsTexture", e);
                }
            }
        });
    }
}
