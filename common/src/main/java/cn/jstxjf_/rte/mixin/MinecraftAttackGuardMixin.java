package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.DepotRailMarking;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftAttackGuardMixin {

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void rte$blockStartAttack(CallbackInfoReturnable<Boolean> cir) {
        if (DepotRailMarking.isActive()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void rte$blockContinueAttack(boolean breaking, CallbackInfo ci) {
        if (DepotRailMarking.isActive()) {
            ci.cancel();
        }
    }
}
