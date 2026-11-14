package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.Vertex;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class ScriptedLamps {

    private static final String LIGHT_SHADER = "rendertype_beacon_beam";
    private static final float CELL = 0.15F;
    private static final float MAX_EXTENT = 0.9F;
    private static final int MIN_POINTS = 3;
    private static final float SCRIPT_TO_LAMP_Y = 1;

    private static final Map<ModelCluster, List<float[]>> BLOBS = new WeakHashMap<>();
    private static final Map<Long, List<float[]>> LATEST = new HashMap<>();
    private static final Set<Long> REPORTED = new HashSet<>();
    private static final ThreadLocal<Frame> CURRENT = new ThreadLocal<>();

    private record Frame(long key, org.joml.Matrix4f inverseBase, List<float[]> lamps) {
    }

    private ScriptedLamps() {
    }

    public static void reset() {
        synchronized (BLOBS) {
            BLOBS.clear();
        }
        synchronized (LATEST) {
            LATEST.clear();
            REPORTED.clear();
        }
    }

    public static void begin(long vehicleId, int car, Matrix4f basePose) {
        try {
            CURRENT.set(new Frame(key(vehicleId, car), new org.joml.Matrix4f(basePose.asMoj()).invert(), new ArrayList<>()));
        } catch (Exception e) {
            CURRENT.remove();
        }
    }

    public static void add(ModelCluster cluster, Matrix4f finalPose) {
        final Frame frame = CURRENT.get();
        if (frame == null || cluster == null || finalPose == null) {
            return;
        }
        final List<float[]> blobs = blobsOf(cluster);
        if (blobs.isEmpty()) {
            return;
        }
        final org.joml.Matrix4f carLocal = new org.joml.Matrix4f(frame.inverseBase).mul(finalPose.asMoj());
        for (final float[] blob : blobs) {
            final Vector3f centre = carLocal.transformPosition(new Vector3f(blob[0], blob[1], blob[2]));
            frame.lamps.add(new float[]{centre.x, centre.y + SCRIPT_TO_LAMP_Y, centre.z, blob[3]});
        }
    }

    public static void end() {
        final Frame frame = CURRENT.get();
        CURRENT.remove();
        if (frame == null) {
            return;
        }
        synchronized (LATEST) {
            LATEST.put(frame.key, List.copyOf(frame.lamps));
            if (!frame.lamps.isEmpty() && REPORTED.add(frame.key)) {
                final StringBuilder summary = new StringBuilder();
                for (final float[] lamp : frame.lamps) {
                    summary.append(String.format(Locale.ROOT, " (%.2f,%.2f,%.2f)", lamp[0], lamp[1], lamp[2]));
                }
                RTE.LOGGER.debug("[VehicleLights] scripted car {} lamps:{}", frame.key, summary.length() == 0 ? " none" : summary);
            }
        }
    }

    static List<float[]> lampsFor(long vehicleId, int car) {
        synchronized (LATEST) {
            return LATEST.get(key(vehicleId, car));
        }
    }

    private static long key(long vehicleId, int car) {
        return vehicleId * 1024 + car;
    }

    private static List<float[]> blobsOf(ModelCluster cluster) {
        synchronized (BLOBS) {
            final List<float[]> cached = BLOBS.get(cluster);
            if (cached != null) {
                return cached;
            }
        }
        final List<float[]> points = new ArrayList<>();
        collect(cluster.opaqueParts, points);
        collect(cluster.translucentParts, points);
        final List<float[]> blobs = cluster(points);
        synchronized (BLOBS) {
            BLOBS.put(cluster, blobs);
        }
        return blobs;
    }

    private static void collect(RawModel model, List<float[]> points) {
        if (model == null || model.meshList == null) {
            return;
        }
        for (final RawMesh mesh : model.meshList.values()) {
            if (mesh == null || mesh.materialProp == null || !LIGHT_SHADER.equals(mesh.materialProp.shaderName)) {
                continue;
            }
            for (final Vertex vertex : mesh.vertices) {
                points.add(new float[]{vertex.position.x(), vertex.position.y(), vertex.position.z()});
            }
        }
    }

    private static List<float[]> cluster(List<float[]> points) {
        final Map<Long, List<float[]>> cells = new HashMap<>();
        for (final float[] point : points) {
            cells.computeIfAbsent(cellKey(cellOf(point[0]), cellOf(point[1]), cellOf(point[2])), key -> new ArrayList<>()).add(point);
        }
        final Set<Long> visited = new HashSet<>();
        final List<float[]> blobs = new ArrayList<>();
        for (final Long start : cells.keySet()) {
            if (!visited.add(start)) {
                continue;
            }
            final List<Long> queue = new ArrayList<>();
            queue.add(start);
            final float[] min = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
            final float[] max = {-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
            for (int i = 0; i < queue.size(); i++) {
                final long cell = queue.get(i);
                for (final float[] point : cells.get(cell)) {
                    for (int axis = 0; axis < 3; axis++) {
                        min[axis] = Math.min(min[axis], point[axis]);
                        max[axis] = Math.max(max[axis], point[axis]);
                    }
                }
                final int cx = unpack(cell, 42);
                final int cy = unpack(cell, 21);
                final int cz = unpack(cell, 0);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            final long next = cellKey(cx + dx, cy + dy, cz + dz);
                            if (cells.containsKey(next) && visited.add(next)) {
                                queue.add(next);
                            }
                        }
                    }
                }
            }
            int count = 0;
            for (final Long cell : queue) {
                count += cells.get(cell).size();
            }
            final float extent = Math.max(max[0] - min[0], Math.max(max[1] - min[1], max[2] - min[2]));
            if (extent <= MAX_EXTENT && count >= MIN_POINTS) {
                blobs.add(new float[]{(min[0] + max[0]) / 2, (min[1] + max[1]) / 2, (min[2] + max[2]) / 2, Math.max(0.15F, extent)});
            }
        }
        return List.copyOf(blobs);
    }

    private static int cellOf(float value) {
        return (int) Math.floor(value / CELL);
    }

    private static long cellKey(int x, int y, int z) {
        return ((long) (x & 0x1FFFFF) << 42) | ((long) (y & 0x1FFFFF) << 21) | (z & 0x1FFFFF);
    }

    private static int unpack(long key, int shift) {
        final int raw = (int) ((key >>> shift) & 0x1FFFFF);
        return raw >= 0x100000 ? raw - 0x200000 : raw;
    }
}
