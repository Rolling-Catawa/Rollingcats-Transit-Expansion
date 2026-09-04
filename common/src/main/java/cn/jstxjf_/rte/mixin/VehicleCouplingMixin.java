package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
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
        return rte$pad(vehicleCar, ((VehicleCarAccessor) (Object) vehicleCar).rte$getCouplingPadding1());
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
        return rte$pad(vehicleCar, ((VehicleCarAccessor) (Object) vehicleCar).rte$getCouplingPadding2());
    }

    private static double rte$pad(VehicleCar vehicleCar, double declared) {
        if (declared != 0 || ClientConfig.legacyCouplingGap <= 0) {
            return declared;
        }
        try {
            if (cn.jstxjf_.rte.data.GangwayPolicy.isLegacyFormat(vehicleCar.getVehicleId())) {
                return ClientConfig.legacyCouplingGap;
            }
        } catch (Exception ignored) {
        }
        return declared;
    }
}
