package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = VehicleResource.class, remap = false)
public abstract class VehicleResourceCarFilterMixin {

    private static final int SYNTHETIC_CAR_COUNT = 3;
    private static final java.util.Set<String> LOGGED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    @Shadow
    public abstract String getId();

    @ModifyVariable(method = "getCachedVehicleResource", at = @At("HEAD"), index = 1)
    private int rte$variationCarIndex(int value, int carIndex, int carCount, boolean blocking) {
        if (!rte$shouldRemap(carIndex, carCount)) {
            return value;
        }
        final int realIndex = cn.jstxjf_.rte.render.train.CurrentVehicleTracker.getCar();
        final int realCount = cn.jstxjf_.rte.render.train.CurrentVehicleTracker.getCarCount();
        if (realIndex >= 0 && realCount > 0 && realIndex < realCount) {
            final org.mtr.mod.data.VehicleExtension vehicle = cn.jstxjf_.rte.render.train.CurrentVehicleTracker.get();
            return vehicle == null ? realIndex
                : realIndex - cn.jstxjf_.rte.render.train.TrainUnits.layout(vehicle).start(realIndex);
        }
        final int synthetic = rte$syntheticCarIndex(carIndex, carCount);
        return synthetic < 0 ? value : synthetic;
    }

    @ModifyVariable(method = "getCachedVehicleResource", at = @At("HEAD"), index = 2)
    private int rte$variationCarCount(int value, int carIndex, int carCount, boolean blocking) {
        if (!rte$shouldRemap(carIndex, carCount)) {
            return value;
        }
        final int realIndex = cn.jstxjf_.rte.render.train.CurrentVehicleTracker.getCar();
        final int realCount = cn.jstxjf_.rte.render.train.CurrentVehicleTracker.getCarCount();
        if (realIndex >= 0 && realCount > 0 && realIndex < realCount) {
            final org.mtr.mod.data.VehicleExtension vehicle = cn.jstxjf_.rte.render.train.CurrentVehicleTracker.get();
            return vehicle == null ? realCount
                : cn.jstxjf_.rte.render.train.TrainUnits.layout(vehicle).count(realIndex);
        }
        return rte$syntheticCarIndex(carIndex, carCount) < 0 ? value : SYNTHETIC_CAR_COUNT;
    }

    private boolean rte$shouldRemap(int carIndex, int carCount) {
        return ClientConfig.legacyVariationCarFilter && carCount == 1 && carIndex == 0;
    }

    private int rte$syntheticCarIndex(int carIndex, int carCount) {
        if (!ClientConfig.legacyVariationCarFilter || carCount != 1 || carIndex != 0) {
            return -1;
        }
        final String id = getId();
        if (id == null) {
            return -1;
        }
        final int synthetic;
        if (id.endsWith("_cab_2")) {
            synthetic = SYNTHETIC_CAR_COUNT - 1;
        } else if (id.endsWith("_trailer")) {
            synthetic = 1;
        } else if (id.endsWith("_cab_1") || id.endsWith("_cab_3")) {
            synthetic = 0;
        } else {
            return -1;
        }
        return synthetic;
    }
}
