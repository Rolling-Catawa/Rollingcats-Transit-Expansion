package cn.jstxjf_.rte.mixin;

import org.mtr.mod.client.Oscillation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Oscillation.class, remap = false)
public class OscillationMixin {

    @Inject(method = "getAmount", at = @At("HEAD"), cancellable = true)
    private void rte$disableBodyOscillation(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(0D);
    }
}
