package cn.jstxjf_.rte.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(com.oracle.truffle.api.TruffleLanguage.class)
public abstract class FQTruffleLanguage {

    @Inject(method = "isThreadAccessAllowed", at = @At("HEAD"), cancellable = true, remap = false)
    protected void isThreadAccessAllowed(CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        cir.setReturnValue(true);
    }
}
