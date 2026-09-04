package cn.jstxjf_.rte.render.train;

import org.mtr.mod.data.VehicleExtension;

public final class CurrentVehicleTracker {

    private static final ThreadLocal<VehicleExtension> CURRENT = new ThreadLocal<>();
    private static final ThreadLocal<Integer> CURRENT_CAR = new ThreadLocal<>();

    private CurrentVehicleTracker() {
    }

    public static void set(VehicleExtension vehicle) {
        CURRENT.set(vehicle);
    }

    public static VehicleExtension get() {
        return CURRENT.get();
    }

    public static void setCar(int carNumber) {
        CURRENT_CAR.set(carNumber);
    }

    public static int getCar() {
        final Integer car = CURRENT_CAR.get();
        return car == null ? -1 : car;
    }

    public static String currentVehicleId() {
        final VehicleExtension vehicle = CURRENT.get();
        final int car = getCar();
        if (vehicle == null || car < 0) {
            return null;
        }
        try {
            final var cars = vehicle.vehicleExtraData.immutableVehicleCars;
            return car < cars.size() ? cars.get(car).getVehicleId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean frontIsCabEnd() {
        final String id = currentVehicleId();
        return id != null && (id.endsWith("_cab_1") || id.endsWith("_cab_3"));
    }

    public static boolean rearIsCabEnd() {
        final String id = currentVehicleId();
        return id != null && (id.endsWith("_cab_2") || id.endsWith("_cab_3"));
    }

    public static int getCarCount() {
        final VehicleExtension vehicle = CURRENT.get();
        if (vehicle == null) {
            return -1;
        }
        try {
            return vehicle.vehicleExtraData.immutableVehicleCars.size();
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean hasCarBefore() {
        return getCar() > 0;
    }

    public static boolean hasCarAfter() {
        final VehicleExtension vehicle = CURRENT.get();
        final int car = getCar();
        if (vehicle == null || car < 0) {
            return false;
        }
        try {
            return car < vehicle.vehicleExtraData.immutableVehicleCars.size() - 1;
        } catch (Exception e) {
            return false;
        }
    }
}
