package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.Vehicle;
import org.mtr.core.data.VehicleExtraData;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Vehicle.class, remap = false)
public abstract class VehicleCarsCacheMixin {

    @Unique
    private static final long RTE_CARS_CACHE_LIFETIME_MILLIS = 250;

    @Shadow
    @Final
    public VehicleExtraData vehicleExtraData;

    @Shadow
    @Final
    private boolean isClientside;

    @Unique
    private volatile Object[] rte$carsCache;

    @Inject(method = "getVehicleCarsAndPositions", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$returnCachedCars(CallbackInfoReturnable<ObjectArrayList<?>> cir) {
        if (!isClientside) {
            return;
        }
        final Object[] cache = rte$carsCache;
        if (cache != null && rte$matches(cache, System.currentTimeMillis())) {
            cir.setReturnValue((ObjectArrayList<?>) cache[0]);
        }
    }

    @Inject(method = "getVehicleCarsAndPositions", at = @At("RETURN"), require = 0)
    private void rte$storeCachedCars(CallbackInfoReturnable<ObjectArrayList<?>> cir) {
        if (!isClientside) {
            return;
        }
        final VehicleAccessor accessor = (VehicleAccessor) (Object) this;
        rte$carsCache = new Object[]{
            cir.getReturnValue(),
            accessor.getRailProgress(),
            accessor.getIsReversed(),
            vehicleExtraData.immutableVehicleCars,
            vehicleExtraData.immutablePath,
            System.currentTimeMillis() + RTE_CARS_CACHE_LIFETIME_MILLIS
        };
    }

    @Unique
    private boolean rte$matches(Object[] cache, long now) {
        final VehicleAccessor accessor = (VehicleAccessor) (Object) this;
        return now < (long) cache[5]
            && (double) cache[1] == accessor.getRailProgress()
            && (boolean) cache[2] == accessor.getIsReversed()
            && cache[3] == vehicleExtraData.immutableVehicleCars
            && cache[4] == vehicleExtraData.immutablePath;
    }
}
