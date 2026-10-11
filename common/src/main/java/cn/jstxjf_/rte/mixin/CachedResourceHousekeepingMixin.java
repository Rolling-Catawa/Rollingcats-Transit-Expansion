package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.AsyncOptimizedModelBuilder;
import cn.jstxjf_.rte.render.train.ExpiringCachedResource;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mod.resource.CachedResource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(value = CachedResource.class, remap = false)
public abstract class CachedResourceHousekeepingMixin<T> implements ExpiringCachedResource {

    @Shadow
    private T data;

    @Shadow
    private long expiry;

    @Shadow
    private static boolean canFetchCache;

    @Shadow
    @Final
    private static ObjectArrayList<CachedResource<?>> CACHED_RESOURCES;

    @Unique
    private boolean rte$tracked = true;

    @Inject(method = "getData", at = @At("HEAD"), require = 0)
    private void rte$releaseBeforeRefetch(boolean force, CallbackInfoReturnable<T> cir) {
        if (data != null && (force || canFetchCache) && System.currentTimeMillis() > expiry) {
            AsyncOptimizedModelBuilder.releaseIfOwned(data);
        }
    }

    @Inject(method = "getData", at = @At("RETURN"), require = 0)
    private void rte$retrackAfterFetch(boolean force, CallbackInfoReturnable<T> cir) {
        if (data != null && !rte$tracked) {
            rte$tracked = true;
            CACHED_RESOURCES.add((CachedResource<?>) (Object) this);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;forEach(Ljava/util/function/Consumer;)V"), require = 0)
    private static void rte$expireAndPrune(ObjectArrayList<CachedResource<?>> resources, Consumer<? super CachedResource<?>> originalExpiry) {
        final long now = System.currentTimeMillis();
        int write = 0;
        final int size = resources.size();
        for (int read = 0; read < size; read++) {
            final CachedResource<?> resource = resources.get(read);
            if (((ExpiringCachedResource) (Object) resource).rte$expire(now)) {
                resources.set(write++, resource);
            }
        }
        resources.removeElements(write, size);
    }

    @Override
    public boolean rte$expire(long now) {
        if (data != null && now > expiry) {
            AsyncOptimizedModelBuilder.releaseIfOwned(data);
            data = null;
        }
        if (data == null) {
            rte$tracked = false;
            return false;
        }
        return true;
    }
}
