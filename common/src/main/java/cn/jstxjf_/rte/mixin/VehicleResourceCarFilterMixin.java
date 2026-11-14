package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.train.LegacyCarFilter;
import cn.jstxjf_.rte.render.train.VehicleBuildContext;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = VehicleResource.class, remap = false)
public abstract class VehicleResourceCarFilterMixin {

    @Unique
    private static final ThreadLocal<LegacyCarFilter.Resolution> RTE_PENDING = new ThreadLocal<>();

    @Shadow
    @Final
    private VehicleResource.LegacyVehicleSupplier<?> extraModelsSupplier;

    @Shadow
    public abstract String getId();

    @ModifyVariable(method = "getCachedVehicleResource", at = @At("HEAD"), index = 1)
    private int rte$variationCarIndex(int value, int carIndex, int carCount, boolean blocking) {
        return rte$resolve(carIndex, carCount, true).index;
    }

    @ModifyVariable(method = "getCachedVehicleResource", at = @At("HEAD"), index = 2)
    private int rte$variationCarCount(int value, int carIndex, int carCount, boolean blocking) {
        return rte$resolve(carIndex, carCount, true).count;
    }

    @ModifyVariable(method = "iterateModels(IILorg/mtr/mod/resource/VehicleResource$ModelConsumer;)V", at = @At("HEAD"), index = 1, require = 0)
    private int rte$variationModelIndex(int value, int carIndex, int carCount, VehicleResource.ModelConsumer consumer) {
        return rte$resolve(carIndex, carCount, false).index;
    }

    @ModifyVariable(method = "iterateModels(IILorg/mtr/mod/resource/VehicleResource$ModelConsumer;)V", at = @At("HEAD"), index = 2, require = 0)
    private int rte$variationModelCount(int value, int carIndex, int carCount, VehicleResource.ModelConsumer consumer) {
        return rte$resolve(carIndex, carCount, false).count;
    }

    @Unique
    private LegacyCarFilter.Resolution rte$resolve(int carIndex, int carCount, boolean beginBuildContext) {
        final LegacyCarFilter.Resolution pending = RTE_PENDING.get();
        if (pending != null && pending.owner == this && pending.isHalfApplied(carIndex, carCount)) {
            RTE_PENDING.remove();
            if (beginBuildContext) {
                final boolean collapsed = extraModelsSupplier == null;
                VehicleBuildContext.begin((VehicleResource) (Object) this, collapsed ? 0 : pending.index, collapsed ? 0 : pending.count);
            }
            return pending;
        }
        final String id = getId();
        final LegacyCarFilter.Resolution resolution = new LegacyCarFilter.Resolution(this, carIndex, carCount,
            LegacyCarFilter.mapIndex(id, carIndex, carCount), LegacyCarFilter.mapCount(id, carIndex, carCount));
        RTE_PENDING.set(resolution);
        return resolution;
    }
}
