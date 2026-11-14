package cn.jstxjf_.rte.mixin;

import org.mtr.mod.resource.VehicleModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleModel.class, remap = false)
public interface VehicleModelAccessor {
    @Accessor("shouldPreload")
    void rte$setShouldPreload(boolean shouldPreload);
}
