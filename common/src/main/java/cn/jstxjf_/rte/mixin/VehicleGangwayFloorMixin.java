package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VehicleResource.class, remap = false)
public class VehicleGangwayFloorMixin {

    @Inject(method = "hasGangway1", at = @At("HEAD"), cancellable = true)
    private void rte$forceGangway1(CallbackInfoReturnable<Boolean> cir) {
        if (!ClientConfig.forceGangwayOnAllCars) {
            return;
        }
        if (cn.jstxjf_.rte.render.train.CurrentVehicleTracker.frontIsCabEnd()) {
            return;
        }
        if (cn.jstxjf_.rte.render.train.CurrentVehicleTracker.getCar() < 0
                || cn.jstxjf_.rte.render.train.CurrentVehicleTracker.hasCarBefore()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasGangway2", at = @At("HEAD"), cancellable = true)
    private void rte$forceGangway2(CallbackInfoReturnable<Boolean> cir) {
        if (!ClientConfig.forceGangwayOnAllCars) {
            return;
        }
        if (cn.jstxjf_.rte.render.train.CurrentVehicleTracker.rearIsCabEnd()) {
            return;
        }
        if (cn.jstxjf_.rte.render.train.CurrentVehicleTracker.getCar() < 0
                || cn.jstxjf_.rte.render.train.CurrentVehicleTracker.hasCarAfter()) {
            cir.setReturnValue(true);
        }
    }
}
