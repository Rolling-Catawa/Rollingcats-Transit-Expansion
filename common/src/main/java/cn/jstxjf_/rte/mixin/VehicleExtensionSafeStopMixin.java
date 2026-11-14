package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.RTEConfig;
import org.mtr.mod.data.VehicleExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = VehicleExtension.class, remap = false)
public abstract class VehicleExtensionSafeStopMixin {

    @ModifyConstant(method = "isVehiclePastSafeStoppingDistance", constant = @Constant(doubleValue = 5.0), require = 0)
    private double rte$safeStopRatio(double value) {
        return 5.0 * RTEConfig.manualNotchMultiplier;
    }
}
