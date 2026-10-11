package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.AsyncLoadGate;
import org.mtr.mod.resource.CachedResource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CachedResource.class, remap = false)
public abstract class CachedResourceFetchMixin<T> {

    @Shadow
    private T data;

    @Shadow
    private static boolean canFetchCache;

    @Inject(method = "getData", at = @At("HEAD"), require = 0)
    private void rte$resetPendingFlag(boolean force, CallbackInfoReturnable<T> cir) {
        AsyncLoadGate.reset();
    }

    @Redirect(
        method = "getData",
        at = @At(value = "FIELD", target = "Lorg/mtr/mod/resource/CachedResource;canFetchCache:Z", opcode = Opcodes.PUTSTATIC),
        require = 0
    )
    private void rte$keepTokenWhilePending(boolean value) {
        if (value || data != null || !AsyncLoadGate.isPending()) {
            canFetchCache = value;
        }
    }
}
