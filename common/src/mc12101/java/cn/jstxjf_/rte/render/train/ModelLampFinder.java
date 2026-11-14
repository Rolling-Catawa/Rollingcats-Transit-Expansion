package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.data.VehicleLightRegistry.Light;
import org.mtr.resource.VehicleResource;

import java.util.ArrayList;
import java.util.List;

public final class ModelLampFinder {

    private ModelLampFinder() {
    }

    static void reset() {
    }

    static List<Light> find(VehicleResource vehicleResource, int carNumber, int carCount) {
        return new ArrayList<>();
    }

    public static List<float[]> bodyBoxes(VehicleResource vehicleResource, int carNumber, int carCount) {
        return new ArrayList<>();
    }
}
