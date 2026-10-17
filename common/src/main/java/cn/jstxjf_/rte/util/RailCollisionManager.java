package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.simulation.Simulator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class RailCollisionManager {

    public static final String CONFIG_COLUMNS = "rte_collision_columns";
    public static final String CONFIG_LAYOUT = "rte_collision_layout";
    public static final float MIN_SPACING = 0.01F;
    private static final int MAX_BOXES_PER_RAIL = 8192;
    private static final int MAX_COLUMNS = 32;
    private static final AtomicLong REVISION = new AtomicLong();
    private static final Map<Level, Cache> CACHES = Collections.synchronizedMap(new WeakHashMap<>());

    private RailCollisionManager() {
    }

    public static void markDirty() {
        REVISION.incrementAndGet();
    }

    public static List<VoxelShape> getShapes(Level level, AABB query) {
        if (level == null || query == null) {
            return Collections.emptyList();
        }
        try {
            final long revision = REVISION.get();
            final Cache cache;
            synchronized (CACHES) {
                cache = CACHES.computeIfAbsent(level, ignored -> new Cache());
            }
            final long gameTick = level.getGameTime();
            if (cache.revision != revision || gameTick - cache.lastCountCheckTick >= 20 || gameTick < cache.lastCountCheckTick) {
                final Collection<Rail> rails = rails(level);
                cache.lastCountCheckTick = gameTick;
                final long sourceSignature = sourceSignature(rails);
                if (cache.revision != revision || cache.sourceSignature != sourceSignature) {
                    cache.rebuild(new ArrayList<>(rails), revision);
                }
            }
            return cache.query(query);
        } catch (Throwable ignored) {
            return Collections.emptyList();
        }
    }

    private static Collection<Rail> rails(Level level) throws Exception {
        if (level.isClientSide()) {
            return MinecraftClientData.getInstance().railIdMap.values();
        }
        if (level instanceof net.minecraft.server.level.ServerLevel) {
            final Simulator simulator = BlockDirectNode.BlockEntityDirectNode.findSimulator(cn.jstxjf_.rte.bridge.MtrTypes.world(level));
            return simulator == null ? Collections.emptyList() : simulator.rails;
        }
        return Collections.emptyList();
    }

    private static String physicalRailKey(RailExtraSupplier extra) {
        final Position p1 = extra.getExtraPosition1();
        final Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) {
            return null;
        }
        final String a = p1.getX() + "," + p1.getY() + "," + p1.getZ();
        final String b = p2.getX() + "," + p2.getY() + "," + p2.getZ();
        return a.compareTo(b) <= 0 ? a + "|" + b : b + "|" + a;
    }

    private static long sourceSignature(Collection<Rail> rails) {
        long sum = 0;
        long xor = 0;
        int count = 0;
        for (final Rail rail : rails) {
            if (!(((Object) rail) instanceof RailExtraSupplier extra) || !extra.isCollisionEnabled()) {
                continue;
            }
            long value = System.identityHashCode(rail);
            final String key = physicalRailKey(extra);
            value = 31 * value + (key == null ? 0 : key.hashCode());
            value = 31 * value + Double.doubleToLongBits(extra.getLength());
            value = 31 * value + Float.floatToIntBits(extra.getCollisionWidth());
            value = 31 * value + Float.floatToIntBits(extra.getCollisionHeight());
            value = 31 * value + Float.floatToIntBits(extra.getCollisionLength());
            value = 31 * value + Float.floatToIntBits(extra.getCollisionSpacing());
            value = 31 * value + Float.floatToIntBits(extra.getCollisionYOffset());
            value = 31 * value + Float.floatToIntBits(extra.getCollisionSideOffset());
            value = 31 * value + collisionConfig(extra, CONFIG_COLUMNS).hashCode();
            value = 31 * value + collisionConfig(extra, CONFIG_LAYOUT).hashCode();
            value = 31 * value + Double.doubleToLongBits(extra.getNodeXOffset1());
            value = 31 * value + Double.doubleToLongBits(extra.getNodeYOffset1());
            value = 31 * value + Double.doubleToLongBits(extra.getNodeZOffset1());
            value = 31 * value + Double.doubleToLongBits(extra.getNodeXOffset2());
            value = 31 * value + Double.doubleToLongBits(extra.getNodeYOffset2());
            value = 31 * value + Double.doubleToLongBits(extra.getNodeZOffset2());
            value = 31 * value + Double.doubleToLongBits(extra.getNodePitch1());
            value = 31 * value + Double.doubleToLongBits(extra.getNodePitch2());
            value = 31 * value + Float.floatToIntBits(extra.getVerticalCurveRadius());
            value = 31 * value + Float.floatToIntBits(extra.getRollingOffset());
            value = 31 * value + (extra.getRollAngleMap() == null ? 0 : extra.getRollAngleMap().hashCode());
            value = 31 * value + Double.doubleToLongBits(extra.getExtraAngle1Rad());
            value = 31 * value + Double.doubleToLongBits(extra.getExtraAngle2Rad());
            value = 31 * value + extra.getPathMode();
            value = 31 * value + System.identityHashCode(extra.getBezierCurve());
            sum += value;
            xor ^= Long.rotateLeft(value, (int) (value & 63));
            count++;
        }
        return 31 * (31 * sum + xor) + count;
    }

    public static List<AABB> createBoxes(RailExtraSupplier extra) {
        final List<AABB> boxes = new ArrayList<>();
        if (extra == null || !extra.isCollisionEnabled()) {
            return boxes;
        }
        final double railLength = extra.getLength();
        if (!Double.isFinite(railLength) || railLength <= 0.001) {
            return boxes;
        }
        final double spacing = clamp(extra.getCollisionSpacing(), MIN_SPACING, 16, 0.5);
        final double width = clamp(extra.getCollisionWidth(), 0.05, 32, 3);
        final double height = clamp(extra.getCollisionHeight(), 0.05, 16, 0.25);
        final double boxLength = clamp(extra.getCollisionLength(), 0.05, 16, 0.75);
        final double yOffset = clamp(extra.getCollisionYOffset(), -32, 32, -0.25);
        final double sideOffset = clamp(extra.getCollisionSideOffset(), -32, 32, 0);
        final List<BoxSpec> explicit = parseLayout(collisionConfig(extra, CONFIG_LAYOUT), railLength);
        if (!explicit.isEmpty()) {
            for (final BoxSpec spec : explicit) {
                addBox(boxes, extra, spec.distance, spec.sideOffset, spec.yOffset,
                    spec.width, spec.height, spec.length, Math.max(0.05, spec.length));
                if (boxes.size() >= MAX_BOXES_PER_RAIL) break;
            }
            return boxes;
        }

        final List<Double> columns = parseColumns(collisionConfig(extra, CONFIG_COLUMNS));
        final int regularCount = Math.max(1, (int) Math.floor(railLength / spacing) + 1);
        final boolean needsEndpoint = Math.abs((regularCount - 1) * spacing - railLength) > 1E-6;
        final int requestedCount = regularCount + (needsEndpoint ? 1 : 0);
        final int maxSamples = Math.max(1, MAX_BOXES_PER_RAIL / columns.size());
        final int count = Math.min(maxSamples, requestedCount);
        final boolean capped = count < requestedCount;
        final double sampleStep = capped && count > 1 ? railLength / (count - 1) : spacing;

        for (int i = 0; i < count; i++) {
            final boolean endpoint = i == count - 1 && (needsEndpoint || capped);
            final double distance = endpoint ? railLength : Math.min(railLength, i * sampleStep);
            for (final double column : columns) {
                addBox(boxes, extra, distance, sideOffset + column, yOffset,
                    width, height, boxLength, sampleStep);
            }
        }
        return boxes;
    }

    public static String getColumns(RailExtraSupplier extra) {
        return normalizeColumns(collisionConfig(extra, CONFIG_COLUMNS));
    }

    public static String normalizeColumns(String value) {
        final List<Double> columns = parseColumns(value);
        final List<String> result = new ArrayList<>(columns.size());
        for (final double column : columns) result.add(format(column));
        return String.join(",", result);
    }

    public static String mirrorColumns(String value) {
        final List<Double> columns = parseColumns(value);
        final List<String> result = new ArrayList<>(columns.size());
        for (int i = columns.size() - 1; i >= 0; i--) result.add(format(-columns.get(i)));
        return String.join(",", result);
    }

    public static String normalizeLayout(String value, double railLength) {
        final List<BoxSpec> specs = parseLayout(value, railLength);
        final List<String> result = new ArrayList<>(specs.size());
        for (final BoxSpec spec : specs) result.add(spec.serialize());
        return String.join(";", result);
    }

    public static String mirrorLayout(String value, double railLength) {
        final List<BoxSpec> specs = parseLayout(value, railLength);
        final List<String> result = new ArrayList<>(specs.size());
        for (int i = specs.size() - 1; i >= 0; i--) {
            final BoxSpec spec = specs.get(i);
            result.add(new BoxSpec(railLength - spec.distance, -spec.sideOffset, spec.yOffset,
                spec.width, spec.height, spec.length).serialize());
        }
        return String.join(";", result);
    }

    private static String collisionConfig(RailExtraSupplier extra, String key) {
        if (extra == null || extra.getCustomConfigs() == null) return "";
        final String value = extra.getCustomConfigs().get(key);
        return value == null ? "" : value;
    }

    private static List<Double> parseColumns(String value) {
        final List<Double> result = new ArrayList<>();
        if (value != null && !value.isBlank()) {
            for (final String part : value.split("[,;\\s]+")) {
                if (result.size() >= MAX_COLUMNS) break;
                try {
                    final double parsed = clamp(Double.parseDouble(part), -32, 32, 0);
                    boolean duplicate = false;
                    for (final double existing : result) {
                        if (Math.abs(existing - parsed) < 1E-6) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) result.add(parsed);
                } catch (Exception ignored) {
                }
            }
        }
        if (result.isEmpty()) result.add(0D);
        return result;
    }

    private static List<BoxSpec> parseLayout(String value, double railLength) {
        final List<BoxSpec> result = new ArrayList<>();
        if (value == null || value.isBlank()) return result;
        for (final String entry : value.split(";")) {
            if (result.size() >= MAX_BOXES_PER_RAIL) break;
            final String[] fields = entry.trim().split("[,\\s]+");
            if (fields.length < 6) continue;
            try {
                result.add(new BoxSpec(
                    clamp(Double.parseDouble(fields[0]), 0, railLength, 0),
                    clamp(Double.parseDouble(fields[1]), -32, 32, 0),
                    clamp(Double.parseDouble(fields[2]), -32, 32, 0),
                    clamp(Double.parseDouble(fields[3]), 0.05, 32, 1),
                    clamp(Double.parseDouble(fields[4]), 0.05, 16, 0.25),
                    clamp(Double.parseDouble(fields[5]), 0.05, 16, 0.75)
                ));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private static void addBox(List<AABB> boxes, RailExtraSupplier extra, double distance,
                               double sideOffset, double yOffset, double width, double height,
                               double boxLength, double sampleStep) {
        final double railLength = extra.getLength();
        final Vec3 center = extra.getPosition(Math.max(0, Math.min(railLength, distance)));
        if (center == null) return;
        final double epsilon = Math.max(0.01, Math.min(0.25, sampleStep * 0.5));
        final Vec3 before = extra.getPosition(Math.max(0, distance - epsilon));
        final Vec3 after = extra.getPosition(Math.min(railLength, distance + epsilon));
        if (before == null || after == null) return;
        double tx = after.x - before.x;
        double tz = after.z - before.z;
        final double horizontal = Math.hypot(tx, tz);
        if (horizontal <= 1E-7) {
            tx = 0;
            tz = 1;
        } else {
            tx /= horizontal;
            tz /= horizontal;
        }
        final double nx = -tz;
        final double nz = tx;
        final double x = center.x + nx * sideOffset;
        final double y = center.y + yOffset;
        final double z = center.z + nz * sideOffset;
        final double halfWidth = width * 0.5;
        final double halfLength = boxLength * 0.5;
        final double extentX = Math.abs(nx) * halfWidth + Math.abs(tx) * halfLength;
        final double extentZ = Math.abs(nz) * halfWidth + Math.abs(tz) * halfLength;
        boxes.add(new AABB(x - extentX, y, z - extentZ, x + extentX, y + height, z + extentZ));
    }

    private static String format(double value) {
        if (Math.abs(value) < 1E-9) value = 0;
        return java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private record BoxSpec(double distance, double sideOffset, double yOffset,
                           double width, double height, double length) {
        private String serialize() {
            return format(distance) + "," + format(sideOffset) + "," + format(yOffset) + ","
                + format(width) + "," + format(height) + "," + format(length);
        }
    }

    private static double clamp(double value, double min, double max, double fallback) {
        return Double.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
    }

    private static long chunkKey(int x, int z) {
        return (x & 0xFFFFFFFFL) | ((z & 0xFFFFFFFFL) << 32);
    }

    private static final class Entry {

        private final AABB box;
        private final VoxelShape shape;

        private Entry(AABB box) {
            this.box = box;
            this.shape = Shapes.create(box);
        }
    }

    private static final class Cache {

        private final Map<Long, List<Entry>> chunks = new HashMap<>();
        private long revision = Long.MIN_VALUE;
        private long sourceSignature = Long.MIN_VALUE;
        private long lastCountCheckTick = Long.MIN_VALUE;

        private void rebuild(Collection<Rail> rails, long newRevision) {
            chunks.clear();
            final Set<String> physicalRails = new HashSet<>();
            for (final Rail rail : rails) {
                if (!(((Object) rail) instanceof RailExtraSupplier extra) || !extra.isCollisionEnabled()) {
                    continue;
                }
                final String key = physicalRailKey(extra);
                if (key == null || !physicalRails.add(key)) {
                    continue;
                }
                for (final AABB box : createBoxes(extra)) {
                    add(box);
                }
            }
            revision = newRevision;
            sourceSignature = sourceSignature(rails);
        }

        private void add(AABB box) {
            final Entry entry = new Entry(box);
            final int minChunkX = ((int) Math.floor(box.minX)) >> 4;
            final int maxChunkX = ((int) Math.floor(box.maxX)) >> 4;
            final int minChunkZ = ((int) Math.floor(box.minZ)) >> 4;
            final int maxChunkZ = ((int) Math.floor(box.maxZ)) >> 4;
            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                    chunks.computeIfAbsent(chunkKey(chunkX, chunkZ), ignored -> new ArrayList<>()).add(entry);
                }
            }
        }

        private List<VoxelShape> query(AABB query) {
            final int minChunkX = ((int) Math.floor(query.minX)) >> 4;
            final int maxChunkX = ((int) Math.floor(query.maxX)) >> 4;
            final int minChunkZ = ((int) Math.floor(query.minZ)) >> 4;
            final int maxChunkZ = ((int) Math.floor(query.maxZ)) >> 4;
            final Set<Entry> seen = Collections.newSetFromMap(new IdentityHashMap<>());
            final List<VoxelShape> result = new ArrayList<>();
            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                    final List<Entry> entries = chunks.get(chunkKey(chunkX, chunkZ));
                    if (entries == null) {
                        continue;
                    }
                    for (final Entry entry : entries) {
                        if (seen.add(entry) && entry.box.intersects(query)) {
                            result.add(entry.shape);
                        }
                    }
                }
            }
            return result;
        }
    }
}
