package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import org.mtr.core.tool.Vector;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VehicleExtension.class, remap = false)
public class VehicleExtensionClientMixin {

    @Inject(method = "playMotorSound", at = @At("HEAD"), cancellable = true)
    private void rte$skipMotorSound(VehicleResource vehicleResource, int carNumber, Vector bogiePosition, CallbackInfo ci) {
        if (!ClientConfig.enableTrainSound) {
            ci.cancel();
        }
    }

    @Inject(method = "playDoorSound", at = @At("HEAD"), cancellable = true)
    private void rte$skipDoorSound(VehicleResource vehicleResource, int carNumber, Vector vehiclePosition, CallbackInfo ci) {
        if (!ClientConfig.enableTrainSound) {
            ci.cancel();
        }
    }
}
