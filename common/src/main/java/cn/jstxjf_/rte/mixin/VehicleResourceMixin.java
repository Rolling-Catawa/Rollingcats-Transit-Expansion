package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RenderUtil;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.StoredMatrixTransformations;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VehicleResource.class, remap = false)
public class VehicleResourceMixin {

    @Inject(method = "queue", at = @At("HEAD"), cancellable = true)
    private void rte$skipQueuedTrainRender(
        StoredMatrixTransformations storedMatrixTransformations,
        VehicleExtension vehicle,
        int carNumber,
        int totalCars,
        int light,
        boolean noOpenDoorways,
        CallbackInfo ci
    ) {
        if (RenderUtil.shouldSkipRenderTrain(vehicle)
                || cn.jstxjf_.rte.render.train.LegacyVehicleRenderer.willTakeOver(vehicle, carNumber)) {
            ci.cancel();
        }
    }

    @Inject(method = "queueBogie", at = @At("HEAD"), cancellable = true)
    private void rte$skipQueuedBogieRender(
        int bogieIndex,
        StoredMatrixTransformations storedMatrixTransformations,
        VehicleExtension vehicle,
        int light,
        CallbackInfo ci
    ) {
        if (RenderUtil.shouldSkipRenderTrain(vehicle)) {
            ci.cancel();
        }
    }
}
