package cn.jstxjf_.rte.data;

import org.mtr.core.tool.Vector;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.VehicleExtension;

public final class RidingRollTransform {

    private static final ThreadLocal<State> STATE = new ThreadLocal<>();

    private RidingRollTransform() {
    }

    public static void begin(VehicleExtension vehicle, int carIndex) {
        if (!(vehicle instanceof TrainExtraSupplier) || carIndex < 0) {
            STATE.remove();
            return;
        }
        final float roll = TrainExtraSupplier.getRollAngleAt(vehicle, carIndex);
        if (!Float.isFinite(roll) || Math.abs(roll) < 1E-6F) {
            STATE.remove();
            return;
        }
        STATE.set(new State(((TrainExtraSupplier) vehicle).getVehicleReversed() ? roll : -roll));
    }

    public static void begin(long vehicleId, int carIndex) {
        VehicleExtension found = null;
        for (final VehicleExtension vehicle : MinecraftClientData.getInstance().vehicles) {
            if (vehicle.getId() == vehicleId) {
                found = vehicle;
                break;
            }
        }
        begin(found, carIndex);
    }

    public static void end() {
        STATE.remove();
    }

    public static Vector rollLocalForwards(Vector vector) {
        final State state = STATE.get();
        return state == null ? vector : rollAroundPivot(vector, state.roll);
    }

    public static Vector3d rollLocalForwards(Vector3d vector) {
        final State state = STATE.get();
        return state == null ? vector : toVector3d(rollAroundPivot(toVector(vector), state.roll));
    }

    public static Vector3d rollLocalBackwards(Vector3d vector) {
        final State state = STATE.get();
        return state == null ? vector : toVector3d(rollAroundPivot(toVector(vector), -state.roll));
    }

    private static Vector rollAroundPivot(Vector vector, double roll) {
        return vector.add(0, -1, 0).rotateZ(roll).add(0, 1, 0);
    }

    private static Vector toVector(Vector3d vector) {
        return new Vector(vector.getXMapped(), vector.getYMapped(), vector.getZMapped());
    }

    private static Vector3d toVector3d(Vector vector) {
        return new Vector3d(vector.x, vector.y, vector.z);
    }

    private static final class State {
        private final double roll;

        private State(double roll) {
            this.roll = roll;
        }
    }
}
