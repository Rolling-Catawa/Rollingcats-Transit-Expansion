package cn.jstxjf_.rte.mixin;

import org.mtr.mod.generated.resource.MinecraftModelResourceSchema;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MinecraftModelResourceSchema.class, remap = false)
public interface MinecraftModelResourceAccessor {

    @Accessor("modelResource")
    String rte$getModelResource();
}
