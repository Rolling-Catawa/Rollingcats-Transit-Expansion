package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.data.CouplingPaddingPolicy;
import org.mtr.core.data.Vehicle;
import org.mtr.core.data.VehicleCar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Vehicle.class, remap = false)
public class VehicleCouplingMixin {

    @Redirect(
        method = "getVehicleCarsAndPositions",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/core/data/VehicleCar;getCouplingPadding1(Z)D"
        )
    )
    private double rte$closeCouplingGap(VehicleCar vehicleCar, boolean isFirstCar) {
        if (ClientConfig.closeCouplingGap) {
            return 0;
        }
        if (isFirstCar) {
            return 0;
        }
        return CouplingPaddingPolicy.getEffectivePadding(vehicleCar, ((VehicleCarAccessor) (Object) vehicleCar).rte$getCouplingPadding1());
    }

    @Redirect(
        method = "getVehicleCarsAndPositions",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/core/data/VehicleCar;getCouplingPadding2(Z)D"
        ),
        require = 0
    )
    private double rte$closeCouplingGap2(VehicleCar vehicleCar, boolean isLastCar) {
        if (ClientConfig.closeCouplingGap) {
            return 0;
        }
        if (isLastCar) {
            return 0;
        }
        return CouplingPaddingPolicy.getEffectivePadding(vehicleCar, ((VehicleCarAccessor) (Object) vehicleCar).rte$getCouplingPadding2());
    }
}
