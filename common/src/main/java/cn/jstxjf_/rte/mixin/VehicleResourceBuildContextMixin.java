package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.render.train.VehicleBuildContext;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VehicleResource.class, remap = false)
public abstract class VehicleResourceBuildContextMixin {

    @Inject(method = "getCachedVehicleResource", at = @At("RETURN"), require = 0)
    private void rte$endBuildContext(int carNumber, int totalCars, boolean force, CallbackInfoReturnable<?> cir) {
        VehicleBuildContext.end();
    }

    @ModifyConstant(method = "cachedVehicleResourceInitializer", constant = @Constant(intValue = Integer.MAX_VALUE), require = 0)
    private int rte$idleReleasePreloadedVehicles(int neverExpire) {
        final int minutes = ClientConfig.preloadIdleReleaseMinutes;
        return minutes > 0 ? minutes * 60_000 : neverExpire;
    }
}
