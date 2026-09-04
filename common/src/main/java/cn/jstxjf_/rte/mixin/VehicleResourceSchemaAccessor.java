package cn.jstxjf_.rte.mixin;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mod.generated.resource.VehicleResourceSchema;
import org.mtr.mod.resource.VehicleModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleResourceSchema.class, remap = false)
public interface VehicleResourceSchemaAccessor {
    @Accessor("models")
    ObjectArrayList<VehicleModel> rte$getModels();
}
