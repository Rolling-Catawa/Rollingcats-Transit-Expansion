package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.RTE;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Prevents malformed camera/frustum data from trapping the render thread forever. */
@Mixin(Frustum.class)
public class FrustumSafetyMixin {

    @Unique private int rte$frustumOffsetIterations;
    @Unique private static long rte$lastFrustumWarning;

    @Inject(method = "offsetToFullyIncludeCameraCube", at = @At("HEAD"))
    private void rte$resetFrustumOffsetGuard(int gridSize, CallbackInfoReturnable<Frustum> cir) {
        rte$frustumOffsetIterations = 0;
    }

    @Redirect(
        method = "offsetToFullyIncludeCameraCube",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/FrustumIntersection;intersectAab(FFFFFF)I",
            remap = false
        )
    )
    private int rte$boundedFrustumIntersection(
            FrustumIntersection intersection,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ) {
        final boolean finite = Float.isFinite(minX) && Float.isFinite(minY) && Float.isFinite(minZ)
            && Float.isFinite(maxX) && Float.isFinite(maxY) && Float.isFinite(maxZ);
        if (!finite || ++rte$frustumOffsetIterations > 1024) {
            final long now = System.currentTimeMillis();
            if (now - rte$lastFrustumWarning > 10_000) {
                rte$lastFrustumWarning = now;
                RTE.LOGGER.warn("[FrustumSafety] aborted invalid camera-frustum offset loop (finite={}, iterations={})",
                    finite, rte$frustumOffsetIterations);
            }
            // FrustumIntersection.INSIDE. Vanilla stops offsetting on this value.
            return -2;
        }
        return intersection.intersectAab(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
