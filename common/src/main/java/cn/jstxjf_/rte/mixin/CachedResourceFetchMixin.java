package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.AsyncLoadGate;
import org.mtr.mod.resource.CachedResource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;

@Mixin(value = CachedResource.class, remap = false)
public abstract class CachedResourceFetchMixin<T> {

    @Unique
    private static final int RTE_MAX_FETCH_DEPTH = 64;

    @Unique
    private static final ThreadLocal<ArrayDeque<Boolean>> RTE_FORCED_FETCHES = ThreadLocal.withInitial(ArrayDeque::new);

    @Shadow
    private T data;

    @Shadow
    private static boolean canFetchCache;

    @Inject(method = "getData", at = @At("HEAD"), require = 0)
    private void rte$resetPendingFlag(boolean force, CallbackInfoReturnable<T> cir) {
        AsyncLoadGate.reset();
        final ArrayDeque<Boolean> forcedFetches = RTE_FORCED_FETCHES.get();
        if (forcedFetches.size() >= RTE_MAX_FETCH_DEPTH) {
            forcedFetches.clear();
        }
        forcedFetches.push(force);
    }

    @Inject(method = "getData", at = @At("RETURN"), require = 0)
    private void rte$popForcedFlag(boolean force, CallbackInfoReturnable<T> cir) {
        RTE_FORCED_FETCHES.get().poll();
    }

    @Redirect(
        method = "getData",
        at = @At(value = "FIELD", target = "Lorg/mtr/mod/resource/CachedResource;canFetchCache:Z", opcode = Opcodes.PUTSTATIC),
        require = 0
    )
    private void rte$keepTokenWhilePending(boolean value) {
        if (!value && Boolean.TRUE.equals(RTE_FORCED_FETCHES.get().peek())) {
            return;
        }
        if (value || data != null || !AsyncLoadGate.isPending()) {
            canFetchCache = value;
        }
    }
}
