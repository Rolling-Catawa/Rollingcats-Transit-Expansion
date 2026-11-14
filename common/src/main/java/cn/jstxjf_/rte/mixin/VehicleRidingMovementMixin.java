package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RidingRollTransform;
import cn.jstxjf_.rte.data.Rolling;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectBooleanImmutablePair;
import org.mtr.mapping.holder.Box;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.client.GangwayMovementPositions;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.client.VehicleRidingMovement;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.PositionAndRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VehicleRidingMovement.class, remap = false)
public class VehicleRidingMovementMixin {

    @Shadow private static long ridingVehicleId;
    @Shadow private static int ridingVehicleCarNumber;

    @Inject(method = "movePlayer(JJILorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lorg/mtr/mod/client/GangwayMovementPositions;Lorg/mtr/mod/client/GangwayMovementPositions;Lorg/mtr/mod/client/GangwayMovementPositions;Lorg/mtr/mod/render/PositionAndRotation;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$movePlayerHead(
            long millisElapsed, long vehicleId, int carNumber,
            ObjectArrayList<ObjectBooleanImmutablePair<Box>> floorsAndDoorways,
            GangwayMovementPositions previousCarGangwayMovementPositions,
            GangwayMovementPositions thisCarGangwayMovementPositions1,
            GangwayMovementPositions thisCarGangwayMovementPositions2,
            PositionAndRotation positionAndRotation, CallbackInfo ci) {
        if (positionAndRotation == null || positionAndRotation.position == null
                || !rte$finitePosition(positionAndRotation.position.x(), positionAndRotation.position.y(), positionAndRotation.position.z())
                || !Double.isFinite(positionAndRotation.yaw) || !Double.isFinite(positionAndRotation.pitch)) {
            RidingRollTransform.end();
            rte$reportInvalidMovement();
            ci.cancel();
            return;
        }
        RidingRollTransform.begin(vehicleId, carNumber);
        rte$updateCameraRolling(vehicleId, carNumber, positionAndRotation);
    }

    private static void rte$updateCameraRolling(long vehicleId, int carNumber, PositionAndRotation positionAndRotation) {
        if (positionAndRotation == null || vehicleId == 0
                || vehicleId != ridingVehicleId || carNumber != ridingVehicleCarNumber) {
            return;
        }
        for (final VehicleExtension vehicle : MinecraftClientData.getInstance().vehicles) {
            if (vehicle.getId() == vehicleId && vehicle instanceof TrainExtraSupplier) {
                final float roll = TrainExtraSupplier.getRollAngleAt(vehicle, carNumber);
                final boolean reversed = ((VehicleAccessor) (Object) vehicle).getIsReversed();
                final boolean hasPitch = positionAndRotation.pitch < 0
                    ? vehicle.getTransportMode().hasPitchAscending
                    : vehicle.getTransportMode().hasPitchDescending;
                Rolling.setRotation(new Rolling.Rotation(
                    positionAndRotation.position.x(), positionAndRotation.position.y(), positionAndRotation.position.z(),
                    (float) positionAndRotation.yaw, hasPitch ? (float) positionAndRotation.pitch : 0, roll, reversed));
                return;
            }
        }
    }

    @Inject(method = "movePlayer", at = @At("RETURN"), require = 0)
    private static void rte$movePlayerReturn(
            long millisElapsed, long vehicleId, int carNumber,
            ObjectArrayList<ObjectBooleanImmutablePair<Box>> floorsAndDoorways,
            GangwayMovementPositions previousCarGangwayMovementPositions,
            GangwayMovementPositions thisCarGangwayMovementPositions1,
            GangwayMovementPositions thisCarGangwayMovementPositions2,
            PositionAndRotation positionAndRotation, CallbackInfo ci) {
        RidingRollTransform.end();
    }

    @Redirect(
        method = "movePlayer",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/PositionAndRotation;transformForwards(Ljava/lang/Object;Lorg/mtr/mod/render/PositionAndRotation$Rotate;Lorg/mtr/mod/render/PositionAndRotation$Rotate;Lorg/mtr/mod/render/PositionAndRotation$Translate;)Ljava/lang/Object;"),
        require = 0
    )
    private static Object rte$transformForwardsWithRoll(PositionAndRotation positionAndRotation, Object value, PositionAndRotation.Rotate rotateX, PositionAndRotation.Rotate rotateY, PositionAndRotation.Translate translate) {
        if (value instanceof Vector3d vector) {
            return positionAndRotation.transformForwards(RidingRollTransform.rollLocalForwards(vector), rotateX, rotateY, translate);
        }
        return positionAndRotation.transformForwards(value, rotateX, rotateY, translate);
    }

    @Redirect(
        method = "movePlayer",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/PositionAndRotation;transformBackwards(Ljava/lang/Object;Lorg/mtr/mod/render/PositionAndRotation$Rotate;Lorg/mtr/mod/render/PositionAndRotation$Rotate;Lorg/mtr/mod/render/PositionAndRotation$Translate;)Ljava/lang/Object;", ordinal = 1),
        require = 0
    )
    private static Object rte$transformBackwardsWithRoll(PositionAndRotation positionAndRotation, Object value, PositionAndRotation.Rotate rotateX, PositionAndRotation.Rotate rotateY, PositionAndRotation.Translate translate) {
        final Object result = positionAndRotation.transformBackwards(value, rotateX, rotateY, translate);
        if (result instanceof Vector3d vector) {
            return RidingRollTransform.rollLocalBackwards(vector);
        }
        return result;
    }

    @org.spongepowered.asm.mixin.Unique
    private static long rte$nextInvalidMovementLog;

    @Inject(method = "movePlayer(DDD)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$guardPlayerPosition(double x, double y, double z, CallbackInfo ci) {
        if (!rte$finitePosition(x, y, z)) {
            rte$reportInvalidMovement();
            ci.cancel();
        }
    }

    @org.spongepowered.asm.mixin.Unique
    private static boolean rte$finitePosition(double x, double y, double z) {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }

    @org.spongepowered.asm.mixin.Unique
    private static void rte$reportInvalidMovement() {
        final long now = System.nanoTime();
        if (now - rte$nextInvalidMovementLog >= 0) {
            rte$nextInvalidMovementLog = now + 5_000_000_000L;
            cn.jstxjf_.rte.RTE.LOGGER.warn("[Riding] skipped invalid vehicle movement; retaining player position");
        }
    }
}
