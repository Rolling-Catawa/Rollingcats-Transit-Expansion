package cn.jstxjf_.rte.mixin;

import org.mtr.libraries.com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import org.mtr.libraries.com.logisticscraft.occlusionculling.util.Vec3d;
import org.mtr.mod.render.RenderRails;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderRails.class, remap = false)
public class RenderRailsOcclusionMixin {

    @Unique
    private static final double RTE_MAX_CULLED_RAIL_VOXELS = 4096;

    @Redirect(
        method = "lambda$render$1",
        at = @At(value = "INVOKE", target = "Lorg/mtr/libraries/com/logisticscraft/occlusionculling/OcclusionCullingInstance;isAABBVisible(Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;)Z"),
        require = 0
    )
    private static boolean rte$skipHugeRailBoxes(OcclusionCullingInstance instance, Vec3d start, Vec3d end, Vec3d camera) {
        if (cn.jstxjf_.rte.ClientConfig.getRailRenderLevel() >= 2) {
            return true;
        }
        final double volume = (Math.abs(end.x - start.x) + 2) * (Math.abs(end.y - start.y) + 2) * (Math.abs(end.z - start.z) + 2);
        if (volume > RTE_MAX_CULLED_RAIL_VOXELS) {
            return true;
        }
        return instance.isAABBVisible(start, end, camera);
    }
}
