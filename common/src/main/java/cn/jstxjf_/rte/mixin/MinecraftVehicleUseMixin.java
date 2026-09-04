package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.VehicleInteraction;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftVehicleUseMixin {

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void rte$vehicleUse(CallbackInfo ci) {
        if (VehicleInteraction.tryUse()) {
            ci.cancel();
        }
    }
}
