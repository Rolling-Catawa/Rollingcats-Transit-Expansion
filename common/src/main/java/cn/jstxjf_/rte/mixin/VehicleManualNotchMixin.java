package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.RTEConfig;
import org.mtr.core.data.Vehicle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = Vehicle.class, remap = false)
public abstract class VehicleManualNotchMixin {

    @ModifyConstant(method = "lambda$updateRidingEntities$1", constant = @Constant(intValue = 7), require = 0)
    private int rte$maxNotchUp(int value) {
        return 7 * RTEConfig.manualNotchMultiplier;
    }

    @ModifyConstant(method = "lambda$updateRidingEntities$1", constant = @Constant(intValue = -8), require = 0)
    private int rte$minNotchDown(int value) {
        return -7 * RTEConfig.manualNotchMultiplier - 1;
    }

    @ModifyConstant(method = "simulateMoving", constant = @Constant(intValue = 5), require = 0)
    private int rte$ratio(int value) {
        return 5 * RTEConfig.manualNotchMultiplier;
    }

    @ModifyConstant(method = "simulateMoving", constant = @Constant(intValue = -5), require = 0)
    private int rte$ratioNegative(int value) {
        return -5 * RTEConfig.manualNotchMultiplier;
    }

    @ModifyConstant(method = "simulateMoving", constant = @Constant(intValue = -7), require = 0)
    private int rte$emergencyThreshold(int value) {
        return -7 * RTEConfig.manualNotchMultiplier;
    }

    @ModifyConstant(method = "simulateMoving", constant = @Constant(intValue = -8), require = 0)
    private int rte$forcedEmergency(int value) {
        return -7 * RTEConfig.manualNotchMultiplier - 1;
    }

    @ModifyConstant(method = "simulateMoving", constant = @Constant(doubleValue = 5.0), require = 0)
    private double rte$ratioDouble(double value) {
        return 5.0 * RTEConfig.manualNotchMultiplier;
    }
}
