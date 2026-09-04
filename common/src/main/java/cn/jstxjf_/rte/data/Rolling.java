package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Quaternionf;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;

public class Rolling {
    private static Rotation rotation = Rotation.IDENTITY;
    private static Rotation tempRotation = null;
    private static long lastFedTime = 0;
    private static Quaternionf frameRoll = null;

    public static void refreshFrame() {
        try {
            final long vehicleId = cn.jstxjf_.rte.mixin.VehicleRidingMovementAccessor.rte$getRidingVehicleId();
            if (vehicleId == 0) {
                rotation = Rotation.IDENTITY;
                return;
            }
            final int carNumber = cn.jstxjf_.rte.mixin.VehicleRidingMovementAccessor.rte$getRidingVehicleCarNumber();
            for (final org.mtr.mod.data.VehicleExtension vehicle : org.mtr.mod.client.MinecraftClientData.getInstance().vehicles) {
                if (vehicle.getId() != vehicleId || !(vehicle instanceof TrainExtraSupplier supplier)) {
                    continue;
                }
                final float roll = TrainExtraSupplier.getRollAngleAt(vehicle, carNumber);
                if (Math.abs(roll) < 1E-6F) {
                    rotation = Rotation.IDENTITY;
                    return;
                }
                final org.mtr.mod.render.PositionAndRotation pose =
                    cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers.getCarPositionAndRotation(vehicle, carNumber);
                if (pose == null) {
                    return;
                }
                final boolean hasPitch = pose.pitch < 0
                    ? vehicle.getTransportMode().hasPitchAscending
                    : vehicle.getTransportMode().hasPitchDescending;
                rotation = new Rotation(pose.position.x, pose.position.y, pose.position.z,
                    (float) pose.yaw, hasPitch ? (float) pose.pitch : 0, roll, supplier.getVehicleReversed());
                lastFedTime = System.currentTimeMillis();
                return;
            }
        } catch (Throwable ignored) {
        }
    }

    public static void setRotation(Rotation rotation) {
        tempRotation = rotation;
    }

    public static void update() {
        if (tempRotation != null) {
            rotation = tempRotation;
            tempRotation = null;
            lastFedTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - lastFedTime > 250) {
            if (!rotation.isIdentity()) {
            }
            rotation = Rotation.IDENTITY;
        }
    }

    public static void applyRolling(PoseStack poseStack) {
        if (!ClientConfig.enableRolling || rotation.isIdentity()) {
            frameRoll = null;
            return;
        }
        frameRoll = getRollQuaternion(true);
        poseStack.mulPose(frameRoll.asMoj());
    }

    public static Quaternionf frameRoll() {
        return frameRoll;
    }

    public static boolean isActive() {
        return ClientConfig.enableRolling && !rotation.isIdentity();
    }

    public static Vector3f applyRolling(Vector3f pos, float eyeHeight) {
        if (rotation.isIdentity() || !isFinite(pos) || !Float.isFinite(eyeHeight)) return pos;

        final Vector3f original = pos;
        pos = pos.copy();
        Rotation rot = rotation;

        Matrix4f mat = new Matrix4f();
        mat.rotateY(rot.yaw);
        mat.rotateX(-rot.pitch);
        mat.rotateZ(rot.reversed? rot.roll : -rot.roll);

        pos.add(0, -eyeHeight, 0);
        Vector3f ep = mat.transform(new Vector3f(0, eyeHeight, 0));
        pos.add(ep);
        return isFinite(pos) ? pos : original;
    }

    public static Quaternionf getRollQuaternion() {
        return getRollQuaternion(false);
    }

    public static Quaternionf getRollQuaternion(boolean reversed) {
        if (rotation.isIdentity()) return new Quaternionf(new Vector3f(0, 0, 0), 0);

        Rotation rot = rotation;

        float roll = reversed != rot.reversed ? rot.roll : -rot.roll;
        float pitch = reversed ? rot.pitch : -rot.pitch;

        if (ClientConfig.enableRolling) {
            return new Quaternionf()
            .rotateY(rot.yaw)
               .rotateX(pitch)
               .rotateZ(roll)
            .rotateY(-rot.yaw);
        } else {
            return new Quaternionf().rotateY(rot.yaw)
                .rotateX(pitch)
            .rotateY(-rot.yaw);
        }
    }

    public static class Rotation {
        public final Vector3f pos;
        public final float yaw;
        public final float pitch;
        public final float roll;
        public final boolean reversed;

        public static final Rotation IDENTITY = new Rotation(0, 0, 0, 0, 0, 0, false);

        public Rotation(double x, double y, double z, float yaw, float pitch, float roll, boolean reversed) {
            this.pos = new Vector3f(finiteFloat(x), finiteFloat(y), finiteFloat(z));
            this.yaw = Float.isFinite(yaw) ? yaw : 0;
            this.pitch = Float.isFinite(pitch) ? pitch : 0;
            this.roll = Float.isFinite(roll) ? roll : 0;
            this.reversed = reversed;
        }

        public boolean isIdentity() {
            return yaw == 0 && pitch == 0 && roll == 0;
        }

        public Rotation copy() {
            return new Rotation(pos.x(), pos.y(), pos.z(), yaw, pitch, roll, reversed);
        }
    }

    private static boolean isFinite(Vector3f vector) {
        return Float.isFinite(vector.x()) && Float.isFinite(vector.y()) && Float.isFinite(vector.z());
    }

    private static float finiteFloat(double value) {
        return Double.isFinite(value) && value >= -Float.MAX_VALUE && value <= Float.MAX_VALUE ? (float) value : 0;
    }
}
