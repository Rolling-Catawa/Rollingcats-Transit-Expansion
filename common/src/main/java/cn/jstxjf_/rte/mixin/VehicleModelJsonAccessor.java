package cn.jstxjf_.rte.mixin;

import org.mtr.core.serializer.JsonReader;
import org.mtr.mod.resource.VehicleModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleModel.class, remap = false)
public interface VehicleModelJsonAccessor {

    @Accessor("modelPropertiesJsonReader")
    JsonReader rte$getModelPropertiesJsonReader();

    @Accessor("positionDefinitionsJsonReader")
    JsonReader rte$getPositionDefinitionsJsonReader();
}
