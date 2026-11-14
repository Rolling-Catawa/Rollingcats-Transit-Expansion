package cn.jstxjf_.rte.mixin;

import org.mtr.mod.generated.resource.VehicleModelSchema;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleModelSchema.class, remap = false)
public interface VehicleModelSourceAccessor {

    @Accessor("modelResource")
    String rte$getModelResource();

    @Accessor("textureResource")
    String rte$getTextureResource();

    @Accessor("modelPropertiesResource")
    String rte$getModelPropertiesResource();

    @Accessor("positionDefinitionsResource")
    String rte$getPositionDefinitionsResource();

    @Accessor("flipTextureV")
    boolean rte$getFlipTextureV();
}
