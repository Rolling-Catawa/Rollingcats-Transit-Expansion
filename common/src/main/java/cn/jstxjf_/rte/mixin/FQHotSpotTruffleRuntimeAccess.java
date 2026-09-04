package cn.jstxjf_.rte.mixin;

import com.oracle.truffle.api.TruffleRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.oracle.truffle.runtime.hotspot.HotSpotTruffleRuntimeAccess", remap = false)
public abstract class FQHotSpotTruffleRuntimeAccess {

    @Inject(method = "createRuntime", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onCreateRuntime(CallbackInfoReturnable<TruffleRuntime> cir) {
        try {
            Class<?> cls = Class.forName("com.oracle.truffle.api.impl.DefaultTruffleRuntime");
            TruffleRuntime runtime;
            try {
                runtime = (TruffleRuntime) cls.getDeclaredConstructor(String.class).newInstance("RTE Shield");
            } catch (NoSuchMethodException e) {
                runtime = (TruffleRuntime) cls.getDeclaredConstructor().newInstance();
            }
            cir.setReturnValue(runtime);
            cir.cancel();
        } catch (Exception ignored) {
        }
    }
}
