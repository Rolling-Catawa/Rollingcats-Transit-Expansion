package cn.jstxjf_.rte.scripting.compat;

import cn.jstxjf_.rte.scripting.train.TrainWrapper;
import cn.jstxjf_.rte.util.TrainCollisionManager;
import org.mtr.core.data.Vehicle;

import java.util.List;

@SuppressWarnings("unused")
public final class TrainCollisionScriptApi {

    private TrainCollisionScriptApi() {
    }

    public static String box(double x, double y, double z, double width, double height, double length) {
        return TrainCollisionManager.formatBox(x, y, z, width, height, length);
    }

    public static boolean setCarLayout(Object train, int car, String layout) {
        final Unit unit = resolve(train);
        if (unit == null || car < 0 || car >= unit.count) return false;
        final List<TrainCollisionManager.LocalBox> boxes = TrainCollisionManager.parseLayout(layout);
        TrainCollisionManager.setLayout(unit.vehicleId, unit.start + car, boxes);
        return true;
    }

    public static boolean clearCarLayout(Object train, int car) {
        final Unit unit = resolve(train);
        if (unit == null || car < 0 || car >= unit.count) return false;
        TrainCollisionManager.setLayout(unit.vehicleId, unit.start + car, null);
        return true;
    }

    public static boolean setHeight(Object train, double height) {
        final Unit unit = resolve(train);
        if (unit == null || !Double.isFinite(height)) return false;
        for (int car = 0; car < unit.count; car++) {
            TrainCollisionManager.setHeight(unit.vehicleId, unit.start + car, height);
        }
        return true;
    }

    public static boolean resetHeight(Object train) {
        final Unit unit = resolve(train);
        if (unit == null) return false;
        for (int car = 0; car < unit.count; car++) {
            TrainCollisionManager.setHeight(unit.vehicleId, unit.start + car, null);
        }
        return true;
    }

    public static boolean setEnabled(Object train, boolean enabled) {
        final Unit unit = resolve(train);
        if (unit == null) return false;
        for (int car = 0; car < unit.count; car++) {
            TrainCollisionManager.setEnabled(unit.vehicleId, unit.start + car, enabled);
        }
        return true;
    }

    private static Unit resolve(Object value) {
        if (value instanceof TrainWrapper wrapper) {
            return new Unit(wrapper.id(), wrapper.unitStart(), wrapper.unitCount());
        }
        if (value instanceof Vehicle vehicle) {
            return new Unit(vehicle.getId(), 0, vehicle.vehicleExtraData.immutableVehicleCars.size());
        }
        return null;
    }

    private record Unit(long vehicleId, int start, int count) {
    }
}
