package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.data.GangwayPolicy;
import org.mtr.core.data.VehicleCar;
import org.mtr.mod.data.VehicleExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class TrainUnits {

    private static final Map<Long, Layout> LAYOUTS = new ConcurrentHashMap<>();

    private TrainUnits() {
    }

    public static final class Layout {
        private final Object cars;
        private final int[] start;
        private final int[] count;

        private Layout(Object cars, int[] start, int[] count) {
            this.cars = cars;
            this.start = start;
            this.count = count;
        }

        public int start(int car) {
            return car >= 0 && car < start.length ? start[car] : -1;
        }

        public int count(int car) {
            return car >= 0 && car < count.length ? count[car] : 0;
        }
    }

    public static Layout layout(VehicleExtension vehicle) {
        final List<VehicleCar> cars = vehicle.vehicleExtraData.immutableVehicleCars;
        final Layout cached = LAYOUTS.get(vehicle.getId());
        if (cached != null && cached.cars == cars) {
            return cached;
        }
        final int size = cars.size();
        final int[] start = new int[size];
        final int[] count = new int[size];
        String previousId = null;
        String previousBase = null;
        int currentStart = 0;
        for (int i = 0; i < size; i++) {
            final String id = cars.get(i).getVehicleId();
            final String base = id == null ? "" : GangwayPolicy.normalise(id);
            if (i == 0 || isHeadCar(id) || isTailCar(previousId) || !base.equals(previousBase)) {
                currentStart = i;
            }
            start[i] = currentStart;
            count[currentStart]++;
            previousId = id;
            previousBase = base;
        }
        for (int i = 0; i < size; i++) {
            count[i] = count[start[i]];
        }
        final Layout layout = new Layout(cars, start, count);
        LAYOUTS.put(vehicle.getId(), layout);
        return layout;
    }

    public static boolean isHeadCar(String vehicleTypeId) {
        return vehicleTypeId != null && (vehicleTypeId.endsWith("_cab_1") || vehicleTypeId.endsWith("_cab_3"));
    }

    public static boolean isTailCar(String vehicleTypeId) {
        return vehicleTypeId != null && (vehicleTypeId.endsWith("_cab_2") || vehicleTypeId.endsWith("_cab_3"));
    }

    public static void retainVehicles(Set<Long> aliveVehicleIds) {
        LAYOUTS.keySet().removeIf(id -> !aliveVehicleIds.contains(id));
    }

    public static void clear() {
        LAYOUTS.clear();
    }
}
