package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.VehicleExtraData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = VehicleExtraData.class, remap = false)
public interface VehicleExtraDataAccessor {

    @Invoker("getManualToAutomaticTime")
    long invokeGetManualToAutomaticTime();
}
