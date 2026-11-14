package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.ClientConfig;
import org.mtr.mod.data.VehicleExtension;

public final class LegacyCarFilter {

    private static final int SYNTHETIC_CAR_COUNT = 3;

    private LegacyCarFilter() {
    }

    public static int mapIndex(String resourceId, int carIndex, int carCount) {
        if (!ClientConfig.legacyVariationCarFilter) {
            return carIndex;
        }
        final VehicleExtension vehicle = currentCarVehicle(carIndex, carCount);
        if (vehicle != null) {
            final int realIndex = CurrentVehicleTracker.getCar();
            return realIndex - TrainUnits.layout(vehicle).start(realIndex);
        }
        final int synthetic = syntheticCarIndex(resourceId, carIndex, carCount);
        return synthetic < 0 ? carIndex : synthetic;
    }

    public static int mapCount(String resourceId, int carIndex, int carCount) {
        if (!ClientConfig.legacyVariationCarFilter) {
            return carCount;
        }
        final VehicleExtension vehicle = currentCarVehicle(carIndex, carCount);
        if (vehicle != null) {
            return TrainUnits.layout(vehicle).count(CurrentVehicleTracker.getCar());
        }
        return syntheticCarIndex(resourceId, carIndex, carCount) < 0 ? carCount : SYNTHETIC_CAR_COUNT;
    }

    private static VehicleExtension currentCarVehicle(int carIndex, int carCount) {
        final VehicleExtension vehicle = CurrentVehicleTracker.get();
        final int realIndex = CurrentVehicleTracker.getCar();
        final int realCount = CurrentVehicleTracker.getCarCount();
        if (vehicle == null || realIndex < 0 || realCount <= 0 || realIndex >= realCount) {
            return null;
        }
        final boolean currentCar = carIndex == realIndex && carCount == realCount;
        final boolean singleCarRequest = carIndex == 0 && carCount == 1;
        return currentCar || singleCarRequest ? vehicle : null;
    }

    private static int syntheticCarIndex(String resourceId, int carIndex, int carCount) {
        if (carCount != 1 || carIndex != 0 || resourceId == null) {
            return -1;
        }
        if (resourceId.endsWith("_cab_2")) {
            return SYNTHETIC_CAR_COUNT - 1;
        }
        if (resourceId.endsWith("_trailer")) {
            return 1;
        }
        if (resourceId.endsWith("_cab_1") || resourceId.endsWith("_cab_3")) {
            return 0;
        }
        return -1;
    }

    public static final class Resolution {

        public final Object owner;
        public final int rawIndex;
        public final int rawCount;
        public final int index;
        public final int count;

        public Resolution(Object owner, int rawIndex, int rawCount, int index, int count) {
            this.owner = owner;
            this.rawIndex = rawIndex;
            this.rawCount = rawCount;
            this.index = index;
            this.count = count;
        }

        public boolean isHalfApplied(int carIndex, int carCount) {
            return (carIndex == index && carCount == rawCount) || (carIndex == rawIndex && carCount == count);
        }
    }
}
