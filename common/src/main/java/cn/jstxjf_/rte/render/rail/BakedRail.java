package cn.jstxjf_.rte.render.rail;

import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.data.RailModelProperties;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.util.AttrUtil;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.core.data.Rail;
import org.mtr.mod.data.RailType;
import net.minecraft.util.Mth;
import cn.jstxjf_.rte.RTE;
import java.util.ArrayList;
import java.util.HashMap;

public class BakedRail {

    public HashMap<Long, ArrayList<Matrix4f>> coveredChunks = new HashMap<>();
    public final Rail rail;

    public static final int POS_SHIFT = 1;

    private static final double ACCEPT_THRESHOLD = 1E-2;
    private static final double HALF_ACCEPT_THRESHOLD = ACCEPT_THRESHOLD / 2;
    private static final int MAX_SEGMENTS_PER_RAIL = 4096;

    public String modelKey;
    public int color;
    public final long geometryFingerprint;

    public static long computeFingerprint(Rail rail) {
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) return 0;
        long h = 1469598103934665603L;
        h = mixFingerprint(h, extra.getPathMode());
        h = mixFingerprint(h, Double.doubleToLongBits(extra.getLength()));
        final cn.jstxjf_.rte.data.BezierCurve bezier = extra.getBezierCurve();
        h = mixFingerprint(h, bezier == null ? 0 : Double.doubleToLongBits(bezier.getLength()));
        h = mixFingerprint(h, Double.doubleToLongBits(extra.getExtraAngle1Rad()));
        h = mixFingerprint(h, Double.doubleToLongBits(extra.getExtraAngle2Rad()));
        h = mixFingerprint(h, extra.getRenderReversed() ? 1 : 0);
        h = mixFingerprint(h, Float.floatToIntBits(extra.getRollingOffset()));
        h = mixFingerprint(h, Float.floatToIntBits(extra.getVerticalCurveRadius()));
        final java.util.Map<Double, Float> rollAngleMap = extra.getRollAngleMap();
        h = mixFingerprint(h, rollAngleMap == null ? 0 : rollAngleMap.hashCode());
        final java.util.List<String> keys = extra.getModelKeys();
        h = mixFingerprint(h, keys == null ? 0 : keys.hashCode());
        h = mixFingerprint(h, Double.doubleToLongBits(rail.railMath.getLength()));
        return h;
    }

    private static long mixFingerprint(long h, long value) {
        h ^= value;
        h *= 1099511628211L;
        return h;
    }

    public BakedRail(Rail rail) {
        this(rail, true);
    }

    public BakedRail(Rail rail, boolean genChunkMap) {
        this(rail, RailRenderDispatcher.getModelKeyForRender(rail), genChunkMap);
    }

    public BakedRail(Rail rail, String explicitModelKey, boolean genChunkMap) {
        this.rail = rail;
        this.geometryFingerprint = computeFingerprint(rail);
        modelKey = explicitModelKey;

        RailModelProperties prop = getProperties();
        color = AttrUtil.argbToBgr(RailType.getRailColor(rail) | 0xFF000000); // Fixed

        if (!modelKey.equals("null") && genChunkMap) {
            final boolean reverse = ((RailExtraSupplier) (Object)rail).getRenderReversed();

            final float interval = prop.repeatInterval;
            final float yOffset = prop.yOffset;

            RailExtraSupplier supplier = (RailExtraSupplier)(Object)rail;
            final double length = supplier.getLength() - ACCEPT_THRESHOLD;

            if (!Double.isFinite(length) || length <= 0 || interval <= 0) {
                return;
            }

            final long segCount = Math.round(length / interval);
            if (segCount > MAX_SEGMENTS_PER_RAIL) {
                RTE.LOGGER.warn("[BakedRail] Rail too long: length={}, interval={}, segments={}, capped at {}",
                    length, interval, segCount, MAX_SEGMENTS_PER_RAIL);
            }
            final long cappedSegCount = Math.min(Math.max(1, segCount), MAX_SEGMENTS_PER_RAIL);
            final double ins = length / cappedSegCount;

            Vector3d pre = supplier.getPosition(HALF_ACCEPT_THRESHOLD);
            Vec3 preVec = new Vec3(pre.getXMapped(), pre.getYMapped(), pre.getZMapped());
            if (!isFiniteVec(preVec)) return;

            int count = 0;
            for (double i = ins; i <= length + ACCEPT_THRESHOLD; i += ins) {
                if (++count > MAX_SEGMENTS_PER_RAIL) break;
                Vector3d thi = supplier.getPosition(i);
                Vec3 thiVec = new Vec3(thi.getXMapped(), thi.getYMapped(), thi.getZMapped());
                if (!isFiniteVec(thiVec)) {
                    preVec = thiVec;
                    continue;
                }
                if (!isFiniteVec(preVec)) {
                    preVec = thiVec;
                    continue;
                }

                Vec3 mid = preVec.add(thiVec).scale(0.5F);

                float roll = RailExtraSupplier.getRollAngle(rail, i - interval / 2);

                coveredChunks
                    .computeIfAbsent(chunkIdFromWorldPos((int) mid.x, (int) mid.z), ignored -> new ArrayList<>())
                    .add(getLookAtMat(mid, preVec, thiVec, roll, yOffset, reverse, interval));
                preVec = thiVec;
            }
        }
    }

    public void dispose() {
        coveredChunks.clear();
    }

    public RailModelProperties getProperties() {
        return RailModelRegistry.getProperty(modelKey);
    }

    private static boolean isFiniteVec(Vec3 v) {
        return Double.isFinite(v.x) && Double.isFinite(v.y) && Double.isFinite(v.z);
    }

    public static long chunkIdFromWorldPos(float bpX, float bpZ) {
        return ((long)((int)bpX >> (4 + POS_SHIFT)) << 32) | ((long)((int)bpZ >> (4 + POS_SHIFT)) & 0xFFFFFFFFL);
    }

    public static long chunkIdFromSectPos(int spX, int spZ) {
        return ((long)(spX >> POS_SHIFT) << 32) | ((long)(spZ >> POS_SHIFT) & 0xFFFFFFFFL);
    }

    public static Matrix4f getLookAtMat(Vec3 pos, Vec3 last, Vec3 next, float roll, float yOffset, boolean reverse, float interval) {
        Matrix4f result = getLookAtMat(pos, last, next, roll, yOffset, reverse);
        float len = (float) last.distanceTo(next);
        result.scale(1, 1, len / interval);
        return result;
    }

    public static Matrix4f getLookAtMat(Vec3 pos, Vec3 last, Vec3 next, float roll, float yOffset, boolean reverse) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate((float) pos.x, (float) pos.y, (float) pos.z);

        if (reverse) {
            Vec3 temp = last;
            last = next;
            next = temp;
        }

        final float yaw = (float) Mth.atan2(next.x - last.x, next.z - last.z);
        final float pitch = (float) Mth.atan2(next.y - last.y, (float) Math.sqrt((next.x - last.x) * (next.x - last.x) + (next.z - last.z) * (next.z - last.z)));

        matrix4f.rotateY(yaw);
        matrix4f.rotateX(-pitch);
        matrix4f.translate(0, yOffset, 0);
        matrix4f.rotateZ(reverse? -roll : roll);

        return matrix4f;
    }
}