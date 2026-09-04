package cn.jstxjf_.rte.mixin;

import org.mtr.core.generated.data.VehicleCarSchema;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleCarSchema.class, remap = false)
public interface VehicleCarAccessor {

    @Accessor("couplingPadding1")
    double rte$getCouplingPadding1();

    @Accessor("couplingPadding2")
    double rte$getCouplingPadding2();
}
