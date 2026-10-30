package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.bridge.MtrBridge;
import cn.jstxjf_.rte.mixin.SidingAccessor;
import net.minecraft.server.level.ServerPlayer;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;
import org.mtr.core.data.VehicleCar;
import org.mtr.core.simulation.Simulator;
import org.mtr.core.tool.Vector;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;

public final class TrainCollisionServer {

    private static final double HORIZONTAL_MARGIN = 1.5;
    private static final double BELOW_MARGIN = 1;

    private TrainCollisionServer() {
    }

    public static boolean isStandingOnTrain(ServerPlayer player) {
        try {
            final Simulator simulator = MtrBridge.simulator(player.serverLevel());
            if (simulator == null) {
                return false;
            }
            for (final Siding siding : simulator.sidings) {
                for (final Vehicle vehicle : ((SidingAccessor) (Object) siding).rte$getVehicles()) {
                    if (isAboveVehicle(vehicle, player.getX(), player.getY(), player.getZ())) {
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean isAboveVehicle(Vehicle vehicle, double x, double y, double z) {
        for (final ObjectObjectImmutablePair<VehicleCar, ObjectArrayList<ObjectObjectImmutablePair<Vector, Vector>>> car : vehicle.getVehicleCarsAndPositions()) {
            double sumX = 0;
            double sumY = 0;
            double sumZ = 0;
            int count = 0;
            for (final ObjectObjectImmutablePair<Vector, Vector> bogie : car.right()) {
                sumX += bogie.left().x + bogie.right().x;
                sumY += bogie.left().y + bogie.right().y;
                sumZ += bogie.left().z + bogie.right().z;
                count += 2;
            }
            if (count == 0) {
                continue;
            }
            final double reach = car.left().getLength() / 2 + HORIZONTAL_MARGIN;
            final double dx = sumX / count - x;
            final double dz = sumZ / count - z;
            final double baseY = sumY / count;
            if (dx * dx + dz * dz <= reach * reach && y >= baseY - BELOW_MARGIN && y <= baseY + TrainCollisionManager.MAX_HEIGHT + BELOW_MARGIN) {
                return true;
            }
        }
        return false;
    }
}
