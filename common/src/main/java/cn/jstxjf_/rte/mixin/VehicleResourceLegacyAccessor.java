package cn.jstxjf_.rte.mixin;

import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleResource.class, remap = false)
public interface VehicleResourceLegacyAccessor {

    @Accessor("extraModelsSupplier")
    VehicleResource.LegacyVehicleSupplier<?> rte$getExtraModelsSupplier();
}
