package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.ClientConfig;
import org.mtr.core.data.VehicleCar;

public final class CouplingPaddingPolicy {

    private CouplingPaddingPolicy() {
    }

    public static double getEffectivePadding(VehicleCar vehicleCar, double declared) {
        if (declared != 0 || ClientConfig.legacyCouplingGap <= 0) {
            return declared;
        }
        try {
            if (GangwayPolicy.isLegacyFormat(vehicleCar.getVehicleId())) {
                return ClientConfig.legacyCouplingGap;
            }
        } catch (Exception ignored) {
        }
        return declared;
    }
}
