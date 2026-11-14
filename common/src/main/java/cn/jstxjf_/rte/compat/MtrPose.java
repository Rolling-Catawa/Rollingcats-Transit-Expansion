package cn.jstxjf_.rte.compat;

import org.mtr.core.data.VehicleCar;
import org.mtr.core.tool.Vector;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.PositionAndRotation;
import org.mtr.mod.render.RenderVehicles;

import javax.annotation.Nullable;

public final class MtrPose {

    private MtrPose() {
    }

    public static Vector forwards(PositionAndRotation positionAndRotation, Vector vector) {
        //#if MC>=12101
        return positionAndRotation.transformForwards(vector, Vector::rotateX, Vector::rotateY, Vector::rotateZ, Vector::add);
        //#else
        return positionAndRotation.transformForwards(vector, Vector::rotateX, Vector::rotateY, Vector::add);
        //#endif
    }

    @Nullable
    public static PositionAndRotation carPositionAndRotation(VehicleExtension vehicle, int carIndex) {
        try {
            final boolean hasPitch = vehicle.getTransportMode().hasPitchAscending || vehicle.getTransportMode().hasPitchDescending;
            final ObjectArrayList<PositionAndRotation> bogiePositions = new ObjectArrayList<>();
            //#if MC>=12101
            final var pair = vehicle.getVehicleCarsAndPositions().get(carIndex);
            final int tiltMultiplier = vehicle.getReversed() ? -1 : 1;
            pair.right().forEach(bogie -> bogiePositions.add(new PositionAndRotation(
                bogie.positionAndTiltAngle1().position(), bogie.positionAndTiltAngle2().position(),
                bogie.positionAndTiltAngle1().tiltAngle() * tiltMultiplier, bogie.positionAndTiltAngle2().tiltAngle() * tiltMultiplier, true)));
            final VehicleCar vehicleCar = pair.left();
            //#else
            final var pair = vehicle.getVehicleCarsAndPositions().get(carIndex);
            pair.right().forEach(bogie -> bogiePositions.add(new PositionAndRotation(bogie.left(), bogie.right(), true)));
            final VehicleCar vehicleCar = pair.left();
            //#endif
            return new PositionAndRotation(bogiePositions, vehicleCar, hasPitch);
        } catch (Exception e) {
            return null;
        }
    }

    public static PositionAndRotation renderPositionAndRotation(@Nullable Vector3d offsetVector, @Nullable Double offsetRotation, @Nullable PositionAndRotation ridingCarPositionAndRotation, PositionAndRotation absolutePositionAndRotation, Vector3d cameraShakeOffset) {
        //#if MC>=12101
        return RenderVehicles.getRenderPositionAndRotation(offsetVector == null ? null : offsetVector.data, offsetRotation, ridingCarPositionAndRotation, absolutePositionAndRotation, cameraShakeOffset.data);
        //#else
        return RenderVehicles.getRenderPositionAndRotation(offsetVector, offsetRotation, ridingCarPositionAndRotation, absolutePositionAndRotation, cameraShakeOffset);
        //#endif
    }
}
