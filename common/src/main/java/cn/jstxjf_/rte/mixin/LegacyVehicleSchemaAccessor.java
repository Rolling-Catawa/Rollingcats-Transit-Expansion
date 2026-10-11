package cn.jstxjf_.rte.mixin;

import org.mtr.legacy.generated.resource.VehicleResourceSchema;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleResourceSchema.class, remap = false)
public interface LegacyVehicleSchemaAccessor {
    @Accessor("model")
    String rte$getLegacyModel();
}
