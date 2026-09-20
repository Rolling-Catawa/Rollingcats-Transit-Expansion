package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.ClientConfig;
import org.mtr.core.data.VehicleCar;

/**
 * Computes the coupling padding shared by MTR positioning and RTE's legacy
 * script adapters. This deliberately lives outside a mixin: ordinary helper
 * methods declared on a mixin must be private because they are merged into the
 * target class.
 */
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
