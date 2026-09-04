package cn.jstxjf_.rte.mixin;

import org.mtr.core.generated.data.VehicleSchema;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VehicleSchema.class, remap = false)
public interface VehicleAccessor {
    @Accessor(value = "reversed", remap = false)
    boolean getIsReversed();

    @Accessor(value = "railProgress", remap = false)
    double getRailProgress();

    @Accessor(value = "speed", remap = false)
    double getSpeed();
}
