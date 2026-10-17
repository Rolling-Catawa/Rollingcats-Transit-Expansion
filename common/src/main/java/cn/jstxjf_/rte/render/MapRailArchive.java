package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import net.minecraft.client.Minecraft;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import net.minecraft.world.phys.Vec3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class MapRailArchive {

    private static final int FORMAT = 1;
    private static final int MAX_SAMPLES = 64;
    private static final int MAX_ARCHIVE_ENTRIES = 1_000_000;
    private static final long SAVE_INTERVAL_MILLIS = 3000;

    private static final Map<String, Entry> ENTRIES = new ConcurrentHashMap<>();

    private static final java.util.Set<String> FULL_SYNC_SEEN =
        java.util.concurrent.ConcurrentHashMap.newKeySet();

    private static Path currentFile;
    private static final AtomicLong revision = new AtomicLong();
    private static volatile long savedRevision;
    private static long lastSaveMillis;

    static {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(MapRailArchive::flush, "rte-mapcache-flush"));
        } catch (Throwable ignored) {
        }
    }

    private MapRailArchive() {
    }

    public static final class Entry {

        public final float[] xs;
        public final float[] zs;
        public final float minX;
        public final float maxX;
        public final float minZ;
        public final float maxZ;
        public int color;

        Entry(float[] xs, float[] zs, int color) {
            this.xs = xs;
            this.zs = zs;
            this.color = color;
            float lowX = Float.POSITIVE_INFINITY;
            float highX = Float.NEGATIVE_INFINITY;
            float lowZ = Float.POSITIVE_INFINITY;
            float highZ = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < xs.length; i++) {
                lowX = Math.min(lowX, xs[i]);
                highX = Math.max(highX, xs[i]);
                lowZ = Math.min(lowZ, zs[i]);
                highZ = Math.max(highZ, zs[i]);
            }
            minX = lowX;
            maxX = highX;
            minZ = lowZ;
            maxZ = highZ;
        }
    }

    public static synchronized void bind(String worldKey) {
        final Path file = directory().resolve(sanitize(worldKey) + ".rtemap");
        if (file.equals(currentFile)) {
            return;
        }
        flush();
        ENTRIES.clear();
        revision.set(0);
        savedRevision = 0;
        currentFile = file;
        fullSyncDone = false;
        syncInProgress = false;
        FULL_SYNC_SEEN.clear();
        lastFullRequestMillis = 0;
        load();
    }

    public static void record(Rail rail, Integer routeColor) {
        if (currentFile == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
            return;
        }
        final String id;
        try {
            id = rail.getHexId();
        } catch (Exception e) {
            return;
        }
        final Entry existing = ENTRIES.get(id);
        if (existing != null) {
            if (routeColor != null && existing.color != routeColor) {
                existing.color = routeColor;
                markDirty();
            }
            return;
        }
        final Position p1 = extra.getExtraPosition1();
        final Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) {
            return;
        }
        final double length = extra.getLength();
        final boolean straight = !Double.isFinite(length) || length <= 0;
        final int samples = straight ? 1 : (int) Math.max(1, Math.min(MAX_SAMPLES, Math.ceil(length / 4)));
        final float[] xs = new float[samples + 1];
        final float[] zs = new float[samples + 1];
        for (int i = 0; i <= samples; i++) {
            double x;
            double z;
            if (straight) {
                x = (i == 0 ? p1.getX() : p2.getX()) + 0.5;
                z = (i == 0 ? p1.getZ() : p2.getZ()) + 0.5;
            } else {
                try {
                    final Vec3 position = extra.getPosition(length * i / samples);
                    x = position.x;
                    z = position.z;
                } catch (Exception e) {
                    final double ratio = (double) i / samples;
                    x = p1.getX() + 0.5 + (p2.getX() - p1.getX()) * ratio;
                    z = p1.getZ() + 0.5 + (p2.getZ() - p1.getZ()) * ratio;
                }
            }
            if (!Double.isFinite(x) || !Double.isFinite(z)) {
                return;
            }
            xs[i] = (float) x;
            zs[i] = (float) z;
        }
        ENTRIES.put(id, new Entry(xs, zs, routeColor == null ? 0 : routeColor));
        markDirty();
    }

    public static void forEachOutside(java.util.Set<String> liveIds, double minX, double minZ,
                                      double maxX, double maxZ, Consumer<Entry> consumer) {
        for (final Map.Entry<String, Entry> mapEntry : ENTRIES.entrySet()) {
            if (liveIds.contains(mapEntry.getKey())) {
                continue;
            }
            final Entry entry = mapEntry.getValue();
            if (!intersects(entry, minX, minZ, maxX, maxZ)) {
                continue;
            }
            consumer.accept(entry);
        }
    }

    private static boolean intersects(Entry entry, double minX, double minZ, double maxX, double maxZ) {
        return entry.maxX >= minX && entry.minX <= maxX && entry.maxZ >= minZ && entry.minZ <= maxZ;
    }

    private static long lastFullRequestMillis;
    private static boolean fullSyncDone;
    private static volatile boolean syncInProgress;
    private static final long RESYNC_INTERVAL_MILLIS = 60000;
    private static final long SYNC_STALL_MILLIS = 30000;

    public static void requestFullSyncIfDue() {
        if (currentFile == null || !cn.jstxjf_.rte.RTEConfig.allowFullRailSync) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (syncInProgress) {
            if (now - lastFullRequestMillis < SYNC_STALL_MILLIS) {
                return;
            }
            syncInProgress = false;
            FULL_SYNC_SEEN.clear();
            RTE.LOGGER.warn("[MapArchive] full sync stalled, retrying");
        }
        final long interval = fullSyncDone ? RESYNC_INTERVAL_MILLIS : 30000;
        if (now - lastFullRequestMillis < interval) {
            return;
        }
        syncInProgress = true;
        lastFullRequestMillis = now;
        requestPage(0);
    }

    private static void requestPage(int page) {
        try {
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(
                new cn.jstxjf_.rte.network.PacketRequestAllRails(page));
        } catch (Exception ignored) {
        }
    }

    public static void acceptServerRails(int page, boolean hasMore,
                                         List<cn.jstxjf_.rte.network.PacketAllRails.RailLine> lines) {
        if (currentFile == null) {
            return;
        }
        if (page == 0) {
            FULL_SYNC_SEEN.clear();
        }
        int added = 0;
        for (final cn.jstxjf_.rte.network.PacketAllRails.RailLine line : lines) {
            if (line.id == null || line.xs.length == 0 || line.xs.length != line.zs.length
                || line.xs.length > MAX_SAMPLES + 1) {
                continue;
            }
            FULL_SYNC_SEEN.add(line.id);
            final Entry existing = ENTRIES.get(line.id);
            if (existing == null) {
                ENTRIES.put(line.id, new Entry(line.xs, line.zs, line.color));
                added++;
                markDirty();
            }
        }
        if (added > 0) {
            RTE.LOGGER.info("[MapArchive] full sync page {} added {} rails (total {})", page, added, ENTRIES.size());
        }
        if (hasMore) {
            requestPage(page + 1);
        } else {
            final int removed = pruneToServerList();
            fullSyncDone = true;
            syncInProgress = false;
            flush();
            RTE.LOGGER.info("[MapArchive] full sync complete, {} rails archived, {} stale removed",
                ENTRIES.size(), removed);
        }
    }

    private static int pruneToServerList() {
        if (FULL_SYNC_SEEN.isEmpty()) {
            return 0;
        }
        int removed = 0;
        for (final String id : new java.util.ArrayList<>(ENTRIES.keySet())) {
            if (!FULL_SYNC_SEEN.contains(id)) {
                ENTRIES.remove(id);
                removed++;
            }
        }
        FULL_SYNC_SEEN.clear();
        if (removed > 0) {
            markDirty();
        }
        return removed;
    }

    public static void forgetRail(String id) {
        if (id != null && ENTRIES.remove(id) != null) {
            markDirty();
        }
    }

    public static int size() {
        return ENTRIES.size();
    }

    public static int pendingCount() {
        return isDirty() ? ENTRIES.size() : 0;
    }

    public static void tickSave() {
        if (!isDirty() || currentFile == null) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now - lastSaveMillis < SAVE_INTERVAL_MILLIS) {
            return;
        }
        lastSaveMillis = now;
        flush();
    }

    public static synchronized void flush() {
        if (!isDirty() || currentFile == null) {
            return;
        }
        final long snapshotRevision = revision.get();
        final List<Map.Entry<String, Entry>> snapshot = new ArrayList<>(ENTRIES.entrySet());
        final Path target = currentFile;
        final Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        try {
            Files.createDirectories(target.getParent());
            try (OutputStream raw = Files.newOutputStream(temporary);
                 DataOutputStream out = new DataOutputStream(new GZIPOutputStream(raw))) {
                out.writeInt(FORMAT);
                out.writeInt(snapshot.size());
                for (final Map.Entry<String, Entry> mapEntry : snapshot) {
                    final Entry entry = mapEntry.getValue();
                    out.writeUTF(mapEntry.getKey());
                    out.writeInt(entry.color);
                    out.writeShort(entry.xs.length);
                    for (int i = 0; i < entry.xs.length; i++) {
                        out.writeFloat(entry.xs[i]);
                        out.writeFloat(entry.zs[i]);
                    }
                }
            }
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
            savedRevision = snapshotRevision;
            RTE.LOGGER.info("[MapArchive] saved {} rails to {}", snapshot.size(), target.getFileName());
        } catch (Exception e) {
            RTE.LOGGER.warn("[MapArchive] save failed", e);
        } finally {
            try {
                Files.deleteIfExists(temporary);
            } catch (Exception ignored) {
            }
        }
    }

    private static void markDirty() {
        revision.incrementAndGet();
    }

    private static boolean isDirty() {
        return revision.get() != savedRevision;
    }

    private static void load() {
        if (currentFile == null || !Files.isRegularFile(currentFile)) {
            return;
        }
        try (InputStream raw = Files.newInputStream(currentFile);
             DataInputStream in = new DataInputStream(new GZIPInputStream(raw))) {
            if (in.readInt() != FORMAT) {
                return;
            }
            final int count = in.readInt();
            if (count < 0 || count > MAX_ARCHIVE_ENTRIES) {
                throw new IOException("Invalid map archive entry count: " + count);
            }
            for (int i = 0; i < count; i++) {
                final String id = in.readUTF();
                final int color = in.readInt();
                final int points = in.readUnsignedShort();
                if (points < 1 || points > MAX_SAMPLES + 1) {
                    throw new IOException("Invalid map archive point count: " + points);
                }
                final float[] xs = new float[points];
                final float[] zs = new float[points];
                for (int p = 0; p < points; p++) {
                    xs[p] = in.readFloat();
                    zs[p] = in.readFloat();
                }
                ENTRIES.put(id, new Entry(xs, zs, color));
            }
            RTE.LOGGER.info("[MapArchive] loaded {} rails from {}", ENTRIES.size(), currentFile.getFileName());
        } catch (Exception e) {
            // Never retain a partially read archive after corruption or truncation.
            ENTRIES.clear();
            RTE.LOGGER.warn("[MapArchive] load failed", e);
        }
    }

    private static Path directory() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("rte").resolve("mapcache");
    }

    private static String sanitize(String value) {
        if (value == null || value.isEmpty()) {
            return "unknown";
        }
        final StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            builder.append(Character.isLetterOrDigit(c) || c == '-' || c == '_' ? c : '_');
        }
        return builder.length() > 64 ? builder.substring(0, 64) : builder.toString();
    }
}
