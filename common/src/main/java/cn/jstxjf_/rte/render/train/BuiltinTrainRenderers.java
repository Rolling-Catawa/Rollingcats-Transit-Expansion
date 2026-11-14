package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import org.mtr.core.data.VehicleCar;
import org.mtr.core.tool.Vector;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectDoubleImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.Box;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.PositionAndRotation;
import net.minecraft.server.packs.resources.ResourceManager;

public final class BuiltinTrainRenderers {

    private static int currentRenderPass;

    private BuiltinTrainRenderers() {
    }

    public static void beginFrame() {
        currentRenderPass++;
        if (currentRenderPass == Integer.MAX_VALUE) {
            currentRenderPass = 1;
        }
    }

    public static void reload(ResourceManager resourceManager) {
    }

    public static boolean renderIfBuiltin(VehicleExtension vehicle, int carIndex, int light, ObjectArrayList<ObjectDoubleImmutablePair<Box>> openDoorways) {
        return false;
    }

    static Matrix4f createBasePose(VehicleExtension vehicle, int carIndex, PositionAndRotation positionAndRotation) {
        final Matrix4f pose = new Matrix4f();
        pose.translate((float) positionAndRotation.position.x(), (float) (positionAndRotation.position.y() - 1), (float) positionAndRotation.position.z());
        pose.rotateY((float) (Math.PI + positionAndRotation.yaw));
        final boolean hasPitch = positionAndRotation.pitch < 0 ? vehicle.getTransportMode().hasPitchAscending : vehicle.getTransportMode().hasPitchDescending;
        pose.rotateX(hasPitch ? (float) positionAndRotation.pitch : 0);

        final float roll = TrainExtraSupplier.getRollAngleAt(vehicle, carIndex);
        final boolean reversed = ((TrainExtraSupplier) vehicle).getVehicleReversed();
        pose.translate(0, 1, 0);
        pose.rotateZ(reversed ? roll : -roll);
        pose.translate(0, -1, 0);
        return pose;
    }

    static PositionAndRotation getCarPositionAndRotation(VehicleExtension vehicle, int carIndex) {
        return cn.jstxjf_.rte.compat.MtrPose.carPositionAndRotation(vehicle, carIndex);
    }
}
