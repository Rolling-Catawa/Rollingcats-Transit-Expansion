package cn.jstxjf_.rte.render.rail;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailModelProperties;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import cn.jstxjf_.rte.mechanism.MechanismTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.mtr.core.data.Rail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MechanismRailRenderer {

    private record Baked(long fingerprint, List<BakedRail> rails) {
    }

    private static final Map<String, Baked> CACHE = new HashMap<>();

    private MechanismRailRenderer() {
    }

    public static boolean isMechanismRail(Rail rail) {
        return ((Object) rail) instanceof RailExtraSupplier extra && !extra.getMechanismGroup().isEmpty();
    }

    public static void draw(Level level, DrawScheduler drawScheduler, Matrix4f viewMatrix, Iterable<Rail> rails) {
        final Set<String> seen = new HashSet<>();
        for (final Rail rail : rails) {
            if (!isMechanismRail(rail)) {
                continue;
            }
            final RailExtraSupplier extra = (RailExtraSupplier) (Object) rail;
            final String hexId = rail.getHexId();
            if (!seen.add(hexId) || extra.isMechanismHidden()) {
                continue;
            }
            final Baked baked = bake(rail);
            final Matrix4f pose = viewMatrix.copy();
            final MechanismTransform transform = extra.getMechanismTransform();
            if (!transform.isIdentity()) {
                pose.multiply(toMatrix(transform));
            }
            for (final BakedRail bakedRail : baked.rails()) {
                final RailModelProperties prop = bakedRail.getProperties();
                if (prop == null || prop.rawModel == null) {
                    continue;
                }
                for (final List<Matrix4f> segments : bakedRail.coveredChunks.values()) {
                    for (final Matrix4f segment : segments) {
                        final Matrix4f segmentPose = pose.copy();
                        segmentPose.multiply(segment);
                        drawScheduler.enqueue(prop.rawModel, segmentPose, light(level, transform, segment));
                    }
                }
            }
        }
        CACHE.keySet().retainAll(seen);
    }

    public static void clear() {
        CACHE.clear();
    }

    private static Baked bake(Rail rail) {
        final long fingerprint = BakedRail.computeFingerprint(rail);
        final Baked cached = CACHE.get(rail.getHexId());
        if (cached != null && cached.fingerprint() == fingerprint) {
            return cached;
        }
        final List<BakedRail> rails = new ArrayList<>();
        MechanismTransform.SUPPRESS.set(true);
        try {
            for (final String modelKey : RailRenderDispatcher.getModelKeysForRender(rail)) {
                rails.add(new BakedRail(rail, modelKey, true));
            }
        } finally {
            MechanismTransform.SUPPRESS.set(false);
        }
        final Baked baked = new Baked(fingerprint, rails);
        CACHE.put(rail.getHexId(), baked);
        return baked;
    }

    public static Matrix4f toMatrix(MechanismTransform transform) {
        final double[] m = transform.matrixColumns();
        return new Matrix4f(new org.joml.Matrix4f(
            (float) m[0], (float) m[1], (float) m[2], 0,
            (float) m[3], (float) m[4], (float) m[5], 0,
            (float) m[6], (float) m[7], (float) m[8], 0,
            (float) m[9], (float) m[10], (float) m[11], 1));
    }

    private static int light(Level level, MechanismTransform transform, Matrix4f segment) {
        final org.joml.Vector3f origin = segment.asMoj().getTranslation(new org.joml.Vector3f());
        final double[] world = transform.apply(origin.x, origin.y, origin.z);
        final BlockPos pos = cn.jstxjf_.rte.compat.McCompat.blockPos(world[0], world[1] + 0.5, world[2]);
        return cn.jstxjf_.rte.util.LightSampler.packed(level, pos);
    }
}
