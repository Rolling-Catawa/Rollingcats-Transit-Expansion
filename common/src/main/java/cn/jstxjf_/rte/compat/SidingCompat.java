package cn.jstxjf_.rte.compat;

import cn.jstxjf_.rte.mixin.SidingAccessor;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;

import java.util.Collection;

public final class SidingCompat {

    private SidingCompat() {
    }

    public static Collection<Vehicle> vehicles(Siding siding) {
        //#if MC>=12101
        return ((SidingAccessor) (Object) siding).rte$getVehicleIdMap().values();
        //#else
        return ((SidingAccessor) (Object) siding).rte$getVehicles();
        //#endif
    }
}
