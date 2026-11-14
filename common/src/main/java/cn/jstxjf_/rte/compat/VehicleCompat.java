package cn.jstxjf_.rte.compat;

import org.mtr.core.data.Vehicle;
import org.mtr.core.data.VehicleCar;
import org.mtr.core.tool.Vector;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;

public final class VehicleCompat {

    private VehicleCompat() {
    }

    public static Vector headPosition(Vehicle vehicle) {
        //#if MC>=12101
        return vehicle.getHeadPositionAndTiltAngle().position();
        //#else
        return vehicle.getHeadPosition();
        //#endif
    }

    public static ObjectArrayList<ObjectObjectImmutablePair<VehicleCar, ObjectArrayList<ObjectObjectImmutablePair<Vector, Vector>>>> carsAndBogies(Vehicle vehicle) {
        //#if MC>=12101
        final ObjectArrayList<ObjectObjectImmutablePair<VehicleCar, ObjectArrayList<ObjectObjectImmutablePair<Vector, Vector>>>> result = new ObjectArrayList<>();
        vehicle.getVehicleCarsAndPositions().forEach(car -> {
            final ObjectArrayList<ObjectObjectImmutablePair<Vector, Vector>> bogies = new ObjectArrayList<>();
            car.right().forEach(bogie -> bogies.add(new ObjectObjectImmutablePair<>(bogie.positionAndTiltAngle1().position(), bogie.positionAndTiltAngle2().position())));
            result.add(new ObjectObjectImmutablePair<>(car.left(), bogies));
        });
        return result;
        //#else
        return vehicle.getVehicleCarsAndPositions();
        //#endif
    }
}
