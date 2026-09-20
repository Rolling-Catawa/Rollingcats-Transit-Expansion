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
    private static final long RELEASE_GRACE_MILLIS = 60000;
    private static final java.util.Set<GraphicsTexture> LIVE =
        java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());

    public GraphicsTexture(int width, int height, ResourceLocation path) {
        this.width = width;
        this.height = height;
        dynamicTexture = new DynamicTexture(new NativeImage(width, height, false));
        identifier = path;
        Minecraft.getInstance().execute(() ->
            Minecraft.getInstance().getTextureManager().register(identifier, dynamicTexture));
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        LIVE.add(this);
        enforceLiveLimit();
    }

    private static final long EVICT_IDLE_MILLIS = 60000;
    private static final int HARD_CEILING_FACTOR = 4;
    private static final long EVICT_LOG_INTERVAL_MILLIS = 10000;
    private static long nextEvictLogMillis = 0;
    private static long evictedSinceLastLog = 0;
    private static long skippedSinceLastLog = 0;

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
        final int limit = cn.jstxjf_.rte.ClientConfig.scriptTextureLimit;
        final long budget = (long) cn.jstxjf_.rte.ClientConfig.scriptTextureHeapBudgetMb * 1024L * 1024L;
        long heapBytes = liveHeapBytes();
        final boolean overBudget = budget > 0 && heapBytes > budget;
        if (!overBudget && (limit <= 0 || LIVE.size() <= limit)) {
            return;
        }
        final java.util.List<GraphicsTexture> ordered = new java.util.ArrayList<>(LIVE);
        ordered.sort(java.util.Comparator.comparingLong(t -> t.lastUploadMillis));
        final long now = System.currentTimeMillis();
        final boolean overCeiling = limit > 0 && ordered.size() > (long) limit * HARD_CEILING_FACTOR;
        final Runtime runtime = Runtime.getRuntime();
        final long usedHeap = runtime.totalMemory() - runtime.freeMemory();
        final long headroom = runtime.maxMemory() - usedHeap;
        final boolean memoryEmergency = headroom < Math.max(256L * 1024 * 1024, runtime.maxMemory() / 20);
        int excess = limit <= 0 ? 0 : ordered.size() - limit;
        for (GraphicsTexture texture : ordered) {
            final boolean stillOverBudget = budget > 0 && heapBytes > budget;
            if (excess <= 0 && !stillOverBudget) break;
            if (texture.isClosed) continue;
            if (!overCeiling && !memoryEmergency && now - texture.lastUploadMillis < EVICT_IDLE_MILLIS) {
                skippedSinceLastLog++;
                continue;
            }
            if (now - texture.lastUploadMillis < EVICT_IDLE_MILLIS) {
                evictedInUse++;
            }
            heapBytes -= texture.heapBytes();
            excess--;
            evictedSinceLastLog++;
            texture.close(0);
        }
        if (budget > 0 && heapBytes > budget && evictedSinceLastLog == 0 && now >= nextEvictLogMillis) {
            RTE.LOGGER.warn("[GraphicsTexture] {}MB of script textures exceeds the {}MB budget but all are in use; "
                    + "lower maxTextureSize or raise scriptTextureHeapBudgetMb",
                    heapBytes / (1024L * 1024L), cn.jstxjf_.rte.ClientConfig.scriptTextureHeapBudgetMb);
        }
        if (now >= nextEvictLogMillis) {
            nextEvictLogMillis = now + EVICT_LOG_INTERVAL_MILLIS;
            if (evictedSinceLastLog > 0 || skippedSinceLastLog > 0) {
                RTE.LOGGER.warn("[GraphicsTexture] live={} limit={} heap={}MB budget={}MB, evicted {}, kept {} in-use",
                        LIVE.size(), limit, liveHeapBytes() / (1024L * 1024L),
                        cn.jstxjf_.rte.ClientConfig.scriptTextureHeapBudgetMb,
                        evictedSinceLastLog, skippedSinceLastLog);
            }
            evictedSinceLastLog = 0;
            skippedSinceLastLog = 0;
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
        if (RenderSystem.isOnRenderThread()) {
            release.run();
        } else {
            Minecraft.getInstance().execute(release);
        }
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
            final DynamicTexture revived = dynamicTexture;
            Minecraft.getInstance().execute(() ->
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

        IntBuffer imgData = IntBuffer.wrap(((DataBufferInt) dataBuffer).getData());
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

            ByteBuffer target = MemoryUtil.memByteBuffer(pixelAddr, width * height * 4);
            if (target == null) {
                return;
            }

            int nonTransparent = 0;
            for (int i = 0; i < width * height; i++) {
                int pixel = imgData.get();
                if ((pixel >>> 24) != 0) nonTransparent++;
                target.put((byte) ((pixel >> 16) & 0xFF));
                target.put((byte) ((pixel >> 8) & 0xFF));
                target.put((byte) (pixel & 0xFF));
                target.put((byte) ((pixel >> 24) & 0xFF));
            }

            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> {
                    try {
                        dynamicTexture.upload();
                    } catch (Exception e) {
                        RTE.LOGGER.error("Failed uploading DynamicTexture {}", identifier, e);
                    }
                });
            } else {
                try {
                    dynamicTexture.upload();
                } catch (Exception e) {
                    RTE.LOGGER.error("Failed uploading DynamicTexture {}", identifier, e);
                }
            }
        } catch (Exception e) {
            RTE.LOGGER.error("Failed to upload GraphicsTexture", e);
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
        Minecraft.getInstance().execute(() -> {
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
