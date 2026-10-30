package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailModelProperties;
import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.data.RoadMarkingLine;
import cn.jstxjf_.rte.mixin.LevelRendererAccessor;
import cn.jstxjf_.rte.render.rail.BakedRail;
import cn.jstxjf_.rte.render.rail.RailRenderDispatcher;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RoadMarkingRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("rte", "textures/misc/road_marking.png");
    private static final double SAMPLE_STEP = 0.5;
    private static final double TANGENT_EPSILON = 0.05;
    private static final float LIFT = 0.012F;
    private static final int SEARCH_DOWN = 4;
    private static final int REBUILDS_PER_FRAME = 12;
    private static final long SCAN_INTERVAL_MILLIS = 250;
    private static final long RETRY_MILLIS = 1000;
    private static final double SCAN_MOVE_THRESHOLD = 8;
    private static final double NO_SURFACE = Double.NaN;
    private static final double UNLOADED = Double.NEGATIVE_INFINITY;
    private static final double EDGE_EPSILON = 1e-6;
    private static final double MIN_DROP = 0.02;
    private static final byte NORMAL_UP = 0;
    private static final byte NORMAL_EAST = 1;
    private static final byte NORMAL_WEST = 2;
    private static final byte NORMAL_SOUTH = 3;
    private static final byte NORMAL_NORTH = 4;
    private static final float[][] NORMALS = {{0, 1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};

    private static final Map<String, Mesh> MESHES = new HashMap<>();
    private static final Map<Long, Set<String>> SECTION_INDEX = new HashMap<>();
    private static final Map<Block, Boolean> NODE_BLOCKS = new IdentityHashMap<>();
    private static Level lastLevel;
    private static long nextScanMillis;
    private static double lastScanX = Double.NaN;
    private static double lastScanZ = Double.NaN;
    private static boolean forceScan = true;

    private RoadMarkingRenderer() {
    }

    public static void clear() {
        MESHES.clear();
        SECTION_INDEX.clear();
        NODE_BLOCKS.clear();
        forceScan = true;
    }

    public static boolean hasMarking(Rail rail) {
        return !styleKeys(rail).isEmpty();
    }

    public static List<String> styleKeys(Rail rail) {
        final List<String> result = new ArrayList<>();
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
            return result;
        }
        final List<String> declared = extra.getModelKeys();
        if (declared != null) {
            for (final String key : declared) {
                if (RailModelRegistry.isRoadMarking(key) && !result.contains(key)) {
                    result.add(key);
                }
            }
        }
        if (result.isEmpty()) {
            final String single = extra.getModelKey();
            if (RailModelRegistry.isRoadMarking(single)) {
                result.add(single);
            }
        }
        return result;
    }

    public static void onSectionDirty(int sectionX, int sectionZ) {
        final Set<String> ids = SECTION_INDEX.get(sectionKey(sectionX, sectionZ));
        if (ids == null) {
            return;
        }
        for (final String id : ids) {
            final Mesh mesh = MESHES.get(id);
            if (mesh != null) {
                mesh.dirty = true;
            }
        }
    }

    public static void render(Level level, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 camera) {
        if (level == null) {
            return;
        }
        if (level != lastLevel) {
            clear();
            lastLevel = level;
        }
        scan(camera);
        if (MESHES.isEmpty()) {
            return;
        }
        rebuildDirty(level);

        Frustum frustum = null;
        try {
            frustum = ((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getCullingFrustum();
        } catch (Exception ignored) {
        }
        final double maxDistance = (Minecraft.getInstance().options.renderDistance().get() + 1) * 16;
        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        final PoseStack.Pose pose = poseStack.last();
        final Matrix4f matrix = pose.pose();
        final Matrix3f normal = pose.normal();
        for (final Mesh mesh : MESHES.values()) {
            if (mesh.vertexCount == 0 || mesh.bounds == null) {
                continue;
            }
            if (horizontalDistance(mesh.bounds, camera) > maxDistance) {
                continue;
            }
            if (frustum != null && !frustum.isVisible(mesh.bounds)) {
                continue;
            }
            final float baseX = (float) (mesh.originX - camera.x);
            final float baseY = (float) (mesh.originY - camera.y);
            final float baseZ = (float) (mesh.originZ - camera.z);
            for (int i = 0; i < mesh.vertexCount; i++) {
                final int argb = mesh.colors[i];
                final float[] direction = NORMALS[mesh.normals[i]];
                buffer.vertex(matrix, baseX + mesh.positions[i * 3], baseY + mesh.positions[i * 3 + 1], baseZ + mesh.positions[i * 3 + 2])
                    .color((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >>> 24) & 0xFF)
                    .uv(0.5F, 0.5F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(mesh.lights[i])
                    .normal(normal, direction[0], direction[1], direction[2])
                    .endVertex();
            }
        }
    }

    private static double horizontalDistance(AABB box, Vec3 camera) {
        final double dx = Math.max(0, Math.max(box.minX - camera.x, camera.x - box.maxX));
        final double dz = Math.max(0, Math.max(box.minZ - camera.z, camera.z - box.maxZ));
        return Math.max(dx, dz);
    }

    private static void scan(Vec3 camera) {
        final long now = System.currentTimeMillis();
        if (!forceScan && now < nextScanMillis
            && Math.abs(camera.x - lastScanX) + Math.abs(camera.z - lastScanZ) <= SCAN_MOVE_THRESHOLD) {
            return;
        }
        forceScan = false;
        nextScanMillis = now + SCAN_INTERVAL_MILLIS;
        lastScanX = camera.x;
        lastScanZ = camera.z;

        final MinecraftClientData data = MinecraftClientData.getInstance();
        if (data == null || data.positionsToRail == null) {
            return;
        }
        final int maxDistance = (Minecraft.getInstance().options.renderDistance().get() + 3) * 16;
        final Set<String> visited = new HashSet<>();
        final Set<String> seen = new HashSet<>();
        try {
            for (final var entryStart : data.positionsToRail.entrySet()) {
                final Position node = entryStart.getKey();
                if (Math.abs(camera.x - node.getX()) + Math.abs(camera.z - node.getZ()) > maxDistance) {
                    continue;
                }
                for (final var entryEnd : entryStart.getValue().entrySet()) {
                    final Rail rail = RailRenderDispatcher.resolveRenderCarrier(entryEnd.getValue());
                    if (rail == null) {
                        continue;
                    }
                    final String id = rail.getHexId();
                    if (!visited.add(id)) {
                        continue;
                    }
                    final List<String> keys = styleKeys(rail);
                    if (keys.isEmpty()) {
                        continue;
                    }
                    seen.add(id);
                    final long fingerprint = BakedRail.computeFingerprint(rail);
                    final Mesh existing = MESHES.get(id);
                    if (existing == null) {
                        MESHES.put(id, new Mesh(id, rail, keys, fingerprint));
                    } else if (existing.fingerprint != fingerprint || !existing.keys.equals(keys)) {
                        existing.rail = rail;
                        existing.keys = keys;
                        existing.fingerprint = fingerprint;
                        existing.dirty = true;
                    } else {
                        existing.rail = rail;
                    }
                }
            }
        } catch (Exception ignored) {
            return;
        }
        for (final Iterator<Map.Entry<String, Mesh>> iterator = MESHES.entrySet().iterator(); iterator.hasNext(); ) {
            final Map.Entry<String, Mesh> entry = iterator.next();
            if (!seen.contains(entry.getKey())) {
                unindex(entry.getValue());
                iterator.remove();
            }
        }
    }

    private static void rebuildDirty(Level level) {
        final long now = System.currentTimeMillis();
        int budget = REBUILDS_PER_FRAME;
        for (final Mesh mesh : MESHES.values()) {
            if (budget <= 0) {
                return;
            }
            if (!mesh.dirty || now < mesh.retryAtMillis) {
                continue;
            }
            budget--;
            unindex(mesh);
            final boolean complete = build(mesh, level);
            index(mesh);
            mesh.dirty = !complete;
            mesh.retryAtMillis = complete ? 0 : now + RETRY_MILLIS;
        }
    }

    private static void index(Mesh mesh) {
        for (final long section : mesh.sections) {
            SECTION_INDEX.computeIfAbsent(section, key -> new HashSet<>()).add(mesh.id);
        }
    }

    private static void unindex(Mesh mesh) {
        for (final long section : mesh.sections) {
            final Set<String> ids = SECTION_INDEX.get(section);
            if (ids != null) {
                ids.remove(mesh.id);
                if (ids.isEmpty()) {
                    SECTION_INDEX.remove(section);
                }
            }
        }
    }

    private static boolean build(Mesh mesh, Level level) {
        final Builder builder = new Builder(level);
        if (!(((Object) mesh.rail) instanceof RailExtraSupplier extra)) {
            builder.finish(mesh);
            return true;
        }
        final double length;
        try {
            length = extra.getLength();
        } catch (Exception e) {
            builder.finish(mesh);
            return true;
        }
        if (!Double.isFinite(length) || length <= 0.01) {
            builder.finish(mesh);
            return true;
        }
        final boolean reversed = extra.getRenderReversed();
        for (final String key : mesh.keys) {
            final RailModelProperties properties = RailModelRegistry.getProperty(key);
            if (properties == null || properties.roadMarking == null) {
                continue;
            }
            for (final RoadMarkingLine line : properties.roadMarking) {
                final double offset = reversed ? -line.offset() : line.offset();
                for (final double[] interval : line.intervals(length)) {
                    strip(builder, extra, length, interval[0], interval[1], offset, line.width(), line.argb());
                }
            }
        }
        builder.finish(mesh);
        return !builder.incomplete;
    }

    private static void strip(Builder builder, RailExtraSupplier extra, double length,
                              double from, double to, double offset, double width, int argb) {
        final int steps = Math.max(1, (int) Math.ceil((to - from) / SAMPLE_STEP));
        final double inner = offset - width / 2;
        final double outer = offset + width / 2;
        double[] previous = null;
        for (int i = 0; i <= steps; i++) {
            final double distance = from + (to - from) * i / steps;
            final double[] sample = sample(extra, length, distance, inner, outer);
            if (sample == null) {
                previous = null;
                continue;
            }
            if (previous != null) {
                final double[][] polygon = {
                    {previous[0], previous[1]},
                    {previous[2], previous[3]},
                    {sample[2], sample[3]},
                    {sample[0], sample[1]}
                };
                builder.project(polygon, (previous[4] + sample[4]) / 2, argb);
            }
            previous = sample;
        }
    }

    private static double[] sample(RailExtraSupplier extra, double length, double distance, double inner, double outer) {
        final double clamped = Math.max(0, Math.min(length, distance));
        final Vec3 center = extra.getPosition(clamped);
        final Vec3 ahead = extra.getPosition(Math.min(length, clamped + TANGENT_EPSILON));
        final Vec3 behind = extra.getPosition(Math.max(0, clamped - TANGENT_EPSILON));
        if (center == null || ahead == null || behind == null) {
            return null;
        }
        double tx = ahead.x - behind.x;
        double tz = ahead.z - behind.z;
        final double horizontal = Math.hypot(tx, tz);
        if (horizontal < 1e-9) {
            return null;
        }
        tx /= horizontal;
        tz /= horizontal;
        final double nx = -tz;
        final double nz = tx;
        return new double[]{
            center.x + nx * inner, center.z + nz * inner,
            center.x + nx * outer, center.z + nz * outer,
            center.y
        };
    }

    private static long sectionKey(int sectionX, int sectionZ) {
        return ((long) sectionX << 32) | (sectionZ & 0xFFFFFFFFL);
    }

    private static boolean isNodeBlock(Block block) {
        return NODE_BLOCKS.computeIfAbsent(block, key -> {
            final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(key);
            if (id == null) {
                return false;
            }
            final String namespace = id.getNamespace();
            return (namespace.equals("mtr") || namespace.equals("rte")) && id.getPath().contains("node");
        });
    }

    private static final class Mesh {
        final String id;
        Rail rail;
        List<String> keys;
        long fingerprint;
        boolean dirty = true;
        long retryAtMillis;
        double originX;
        double originY;
        double originZ;
        float[] positions = new float[0];
        int[] colors = new int[0];
        int[] lights = new int[0];
        byte[] normals = new byte[0];
        int vertexCount;
        AABB bounds;
        long[] sections = new long[0];

        Mesh(String id, Rail rail, List<String> keys, long fingerprint) {
            this.id = id;
            this.rail = rail;
            this.keys = keys;
            this.fingerprint = fingerprint;
        }
    }

    private static final class Builder {
        private final Level level;
        private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        private final Map<Long, double[]> surfaceCache = new HashMap<>();
        private final Set<Long> sections = new HashSet<>();
        private float[] positions = new float[384];
        private int[] colors = new int[128];
        private int[] lights = new int[128];
        private byte[] normals = new byte[128];
        private int count;
        private boolean hasOrigin;
        private double originX;
        private double originY;
        private double originZ;
        private double minX = Double.POSITIVE_INFINITY;
        private double minY = Double.POSITIVE_INFINITY;
        private double minZ = Double.POSITIVE_INFINITY;
        private double maxX = Double.NEGATIVE_INFINITY;
        private double maxY = Double.NEGATIVE_INFINITY;
        private double maxZ = Double.NEGATIVE_INFINITY;
        boolean incomplete;

        Builder(Level level) {
            this.level = level;
        }

        void project(double[][] polygon, double referenceY, int argb) {
            double lowX = Double.POSITIVE_INFINITY;
            double lowZ = Double.POSITIVE_INFINITY;
            double highX = Double.NEGATIVE_INFINITY;
            double highZ = Double.NEGATIVE_INFINITY;
            for (final double[] point : polygon) {
                lowX = Math.min(lowX, point[0]);
                lowZ = Math.min(lowZ, point[1]);
                highX = Math.max(highX, point[0]);
                highZ = Math.max(highZ, point[1]);
            }
            final int fromX = Mth.floor(lowX);
            final int toX = Mth.floor(highX);
            final int fromZ = Mth.floor(lowZ);
            final int toZ = Mth.floor(highZ);
            for (int blockX = fromX; blockX <= toX; blockX++) {
                for (int blockZ = fromZ; blockZ <= toZ; blockZ++) {
                    final List<double[]> piece = clip(polygon, blockX, blockZ);
                    if (piece.size() < 3) {
                        continue;
                    }
                    sections.add(sectionKey(blockX >> 4, blockZ >> 4));
                    final double[] surface = surface(blockX, blockZ, referenceY);
                    if (surface == null) {
                        continue;
                    }
                    emit(piece, (float) (surface[0] + LIFT), (int) surface[1], argb);
                    if (highX > blockX + 1 + EDGE_EPSILON) {
                        riser(piece, blockX, blockZ, surface[0], referenceY, argb, 0, blockX + 1, 1, NORMAL_EAST);
                    }
                    if (lowX < blockX - EDGE_EPSILON) {
                        riser(piece, blockX, blockZ, surface[0], referenceY, argb, 0, blockX, -1, NORMAL_WEST);
                    }
                    if (highZ > blockZ + 1 + EDGE_EPSILON) {
                        riser(piece, blockX, blockZ, surface[0], referenceY, argb, 1, blockZ + 1, 1, NORMAL_SOUTH);
                    }
                    if (lowZ < blockZ - EDGE_EPSILON) {
                        riser(piece, blockX, blockZ, surface[0], referenceY, argb, 1, blockZ, -1, NORMAL_NORTH);
                    }
                }
            }
        }

        private void riser(List<double[]> piece, int blockX, int blockZ, double top, double referenceY, int argb,
                           int axis, int bound, int side, byte normal) {
            final int other = 1 - axis;
            double from = Double.POSITIVE_INFINITY;
            double to = Double.NEGATIVE_INFINITY;
            for (final double[] point : piece) {
                if (Math.abs(point[axis] - bound) < EDGE_EPSILON) {
                    from = Math.min(from, point[other]);
                    to = Math.max(to, point[other]);
                }
            }
            if (!(to - from > EDGE_EPSILON)) {
                return;
            }
            final int neighborX = axis == 0 ? blockX + side : blockX;
            final int neighborZ = axis == 1 ? blockZ + side : blockZ;
            sections.add(sectionKey(neighborX >> 4, neighborZ >> 4));
            final double[] below = surface(neighborX, neighborZ, referenceY);
            if (below == null || top - below[0] < MIN_DROP) {
                return;
            }
            final double bottom = solidBottom(blockX, blockZ, top, below[0], axis == 0 ? Direction.Axis.X : Direction.Axis.Z, side);
            if (top - bottom < MIN_DROP) {
                return;
            }
            cursor.set(neighborX, Mth.floor((top + bottom) / 2), neighborZ);
            final int light = LevelRenderer.getLightColor(level, cursor);
            final double face = bound + side * LIFT;
            final double upper = top + LIFT;
            final double lower = bottom + LIFT;
            if (axis == 0) {
                vertex(face, upper, from, light, argb, normal);
                vertex(face, upper, to, light, argb, normal);
                vertex(face, lower, to, light, argb, normal);
                vertex(face, lower, from, light, argb, normal);
            } else {
                vertex(from, upper, face, light, argb, normal);
                vertex(to, upper, face, light, argb, normal);
                vertex(to, lower, face, light, argb, normal);
                vertex(from, lower, face, light, argb, normal);
            }
        }

        private double solidBottom(int blockX, int blockZ, double top, double floor, Direction.Axis axis, int side) {
            double bottom = top;
            for (int y = Mth.floor(top - 1e-4); y + 1 > floor + EDGE_EPSILON && top - y <= SEARCH_DOWN + 1; y--) {
                cursor.set(blockX, y, blockZ);
                final BlockState state = level.getBlockState(cursor);
                if (state.isAir()) {
                    break;
                }
                final VoxelShape shape = state.getCollisionShape(level, cursor);
                if (shape.isEmpty()) {
                    break;
                }
                final boolean reachesFace = side > 0 ? shape.max(axis) >= 1 - 1e-4 : shape.min(axis) <= 1e-4;
                if (!reachesFace || y + shape.max(Direction.Axis.Y) < bottom - 1e-4) {
                    break;
                }
                final double shapeBottom = shape.min(Direction.Axis.Y);
                bottom = Math.max(floor, y + shapeBottom);
                if (shapeBottom > 1e-4) {
                    break;
                }
            }
            return bottom;
        }

        private List<double[]> clip(double[][] polygon, int blockX, int blockZ) {
            List<double[]> points = new ArrayList<>(polygon.length + 4);
            for (final double[] point : polygon) {
                points.add(point);
            }
            points = clipAxis(points, 0, blockX, true);
            points = clipAxis(points, 0, blockX + 1, false);
            points = clipAxis(points, 1, blockZ, true);
            points = clipAxis(points, 1, blockZ + 1, false);
            return points;
        }

        private static List<double[]> clipAxis(List<double[]> points, int axis, double bound, boolean keepAbove) {
            final List<double[]> result = new ArrayList<>(points.size() + 2);
            final int size = points.size();
            for (int i = 0; i < size; i++) {
                final double[] current = points.get(i);
                final double[] next = points.get((i + 1) % size);
                final boolean currentIn = keepAbove ? current[axis] >= bound : current[axis] <= bound;
                final boolean nextIn = keepAbove ? next[axis] >= bound : next[axis] <= bound;
                if (currentIn) {
                    result.add(current);
                }
                if (currentIn != nextIn) {
                    final double t = (bound - current[axis]) / (next[axis] - current[axis]);
                    result.add(new double[]{
                        current[0] + (next[0] - current[0]) * t,
                        current[1] + (next[1] - current[1]) * t
                    });
                }
            }
            return result;
        }

        private double[] surface(int blockX, int blockZ, double referenceY) {
            final int baseY = Mth.floor(referenceY);
            final long key = ((long) (blockX & 0x3FFFFF) << 42) | ((long) (blockZ & 0x3FFFFF) << 20) | (baseY & 0xFFFFF);
            final double[] cached = surfaceCache.get(key);
            if (cached != null) {
                return cached.length == 0 ? null : cached;
            }
            final double top = findSurface(blockX, blockZ, referenceY);
            final double[] result;
            if (top == UNLOADED) {
                incomplete = true;
                result = new double[0];
            } else if (Double.isNaN(top)) {
                result = new double[0];
            } else {
                cursor.set(blockX, Mth.floor(top - 1e-4) + 1, blockZ);
                result = new double[]{top, LevelRenderer.getLightColor(level, cursor)};
            }
            surfaceCache.put(key, result);
            return result.length == 0 ? null : result;
        }

        private double findSurface(int blockX, int blockZ, double referenceY) {
            if (!level.hasChunk(blockX >> 4, blockZ >> 4)) {
                return UNLOADED;
            }
            final int startY = Mth.floor(referenceY + 0.9);
            final int endY = Mth.floor(referenceY) - SEARCH_DOWN;
            for (int y = startY; y >= endY; y--) {
                cursor.set(blockX, y, blockZ);
                final BlockState state = level.getBlockState(cursor);
                if (state.isAir() || isNodeBlock(state.getBlock())) {
                    continue;
                }
                final VoxelShape shape = state.getCollisionShape(level, cursor);
                if (shape.isEmpty()) {
                    continue;
                }
                return y + shape.max(Direction.Axis.Y);
            }
            return NO_SURFACE;
        }

        private void emit(List<double[]> piece, float y, int light, int argb) {
            final double[] first = piece.get(0);
            int i = 1;
            while (i + 2 < piece.size()) {
                vertex(first[0], y, first[1], light, argb, NORMAL_UP);
                vertex(piece.get(i)[0], y, piece.get(i)[1], light, argb, NORMAL_UP);
                vertex(piece.get(i + 1)[0], y, piece.get(i + 1)[1], light, argb, NORMAL_UP);
                vertex(piece.get(i + 2)[0], y, piece.get(i + 2)[1], light, argb, NORMAL_UP);
                i += 2;
            }
            if (i + 1 < piece.size()) {
                vertex(first[0], y, first[1], light, argb, NORMAL_UP);
                vertex(piece.get(i)[0], y, piece.get(i)[1], light, argb, NORMAL_UP);
                vertex(piece.get(i + 1)[0], y, piece.get(i + 1)[1], light, argb, NORMAL_UP);
                vertex(piece.get(i + 1)[0], y, piece.get(i + 1)[1], light, argb, NORMAL_UP);
            }
        }

        private void vertex(double x, double y, double z, int light, int argb, byte normal) {
            if (!hasOrigin) {
                hasOrigin = true;
                originX = Math.floor(x);
                originY = Math.floor(y);
                originZ = Math.floor(z);
            }
            if (count == colors.length) {
                positions = java.util.Arrays.copyOf(positions, positions.length * 2);
                colors = java.util.Arrays.copyOf(colors, colors.length * 2);
                lights = java.util.Arrays.copyOf(lights, lights.length * 2);
                normals = java.util.Arrays.copyOf(normals, normals.length * 2);
            }
            positions[count * 3] = (float) (x - originX);
            positions[count * 3 + 1] = (float) (y - originY);
            positions[count * 3 + 2] = (float) (z - originZ);
            colors[count] = argb;
            lights[count] = light;
            normals[count] = normal;
            count++;
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);
        }

        void finish(Mesh mesh) {
            mesh.originX = originX;
            mesh.originY = originY;
            mesh.originZ = originZ;
            mesh.positions = positions;
            mesh.colors = colors;
            mesh.lights = lights;
            mesh.normals = normals;
            mesh.vertexCount = count;
            mesh.bounds = count == 0 ? null : new AABB(minX, minY - 0.1, minZ, maxX, maxY + 0.1, maxZ);
            final long[] sectionArray = new long[sections.size()];
            int index = 0;
            for (final long section : sections) {
                sectionArray[index++] = section;
            }
            mesh.sections = sectionArray;
        }
    }
}
