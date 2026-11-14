package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RenderUtil;
import org.mtr.data.VehicleExtension;
import org.mtr.render.StoredMatrixTransformations;
import org.mtr.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VehicleResource.class, remap = false)
public class VehicleResourceMixin {

    @Inject(method = "queueBogie", at = @At("HEAD"), cancellable = true)
    private void rte$skipQueuedBogieRender(
        int bogieIndex,
        StoredMatrixTransformations storedMatrixTransformations,
        VehicleExtension vehicle,
        boolean isWithinHalfRenderDistance,
        int light,
        CallbackInfo ci
    ) {
        if (RenderUtil.shouldSkipRenderTrain(vehicle)) {
            ci.cancel();
        }
    }
}
