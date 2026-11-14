package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.VehicleInteraction;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftVehicleUseMixin {

    @Shadow private int rightClickDelay;

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void rte$vehicleUse(CallbackInfo ci) {
        if (rightClickDelay > 0) {
            return;
        }
        if (VehicleInteraction.tryUse()) {
            rightClickDelay = 4;
            ci.cancel();
        }
    }
}
