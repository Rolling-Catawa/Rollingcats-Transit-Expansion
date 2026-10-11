package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RidingRollTransform;
import cn.jstxjf_.rte.mixin.PreviousConnectionPositionsAccessor;
import cn.jstxjf_.rte.render.RenderUtil;
import cn.jstxjf_.rte.render.train.BuiltinTrainRenderers;
import cn.jstxjf_.rte.render.train.RenderVehicleCarFrameState;
import cn.jstxjf_.rte.scripting.ScriptContextManager;
import cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers;
import org.mtr.core.tool.Vector;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectBooleanImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.Box;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.RenderVehicles;
import org.mtr.mod.render.PositionAndRotation;
import org.mtr.mod.render.StoredMatrixTransformations;
import org.mtr.mod.render.DynamicVehicleModel;
import org.mtr.mod.resource.VehicleResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderVehicles.class, remap = false)
public class RenderVehiclesMixin {

    private static final ThreadLocal<RenderVehicleCarFrameState> RTE_CAR_FRAME_STATE = new ThreadLocal<>();

    /**
     * MTR uses an open doorway twice: its full-height box is needed for platform
     * detection and door-side selection, but the same box is also inserted into
     * the player's riding-floor list.  Legacy doorway synthesis is based on the
     * animated door leaf, so that box is normally about two blocks tall.  Feeding
     * it to riding collision makes its top act as the floor and launches the
     * player above the car.  Preserve the original doorway everywhere else and
     * replace only the riding collision argument with a thin threshold at the
     * doorway's bottom edge.
     */
    @ModifyArg(
        method = "lambda$render$11",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectBooleanImmutablePair;<init>(Ljava/lang/Object;Z)V"
        ),
        index = 0,
        require = 0
    )
    private static Object rte$flattenDoorwayRidingCollision(Object value) {
        if (!(value instanceof Box box)) {
            return value;
        }
        final double minY = box.getMinYMapped();
        final double maxY = box.getMaxYMapped();
        if (!Double.isFinite(minY) || !Double.isFinite(maxY) || maxY - minY <= 0.5) {
            return value;
        }
        return new Box(
            box.getMinXMapped(), minY - 0.125, box.getMinZMapped(),
            box.getMaxXMapped(), minY, box.getMaxZMapped()
        );
    }


    @Inject(method = "render", at = @At("HEAD"))
    private static void rte$renderHead(long millisElapsed, Vector3d cameraShakeOffset, CallbackInfo ci) {
        ScriptContextManager.disposeDeadContexts();
        BuiltinTrainRenderers.beginFrame();
        ScriptedTrainRenderers.beginFrame();
        cn.jstxjf_.rte.render.train.LegacyVehicleRenderer.beginFrame();
    }

    @Inject(
        method = "lambda$render$5",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private static void rte$skipVehicleWhenHidden(
            Vector3d cameraShakeOffset, Double aDouble, PositionAndRotation posRot,
            Vector3d offset, VehicleResource vehicleResource, VehicleExtension vehicle,
            PositionAndRotation posRot2, int idx, int[] arr, boolean flag,
            int count, PositionAndRotation posRot3, CallbackInfo ci) {
        cn.jstxjf_.rte.render.train.CurrentVehicleTracker.set(vehicle);
        if (RenderUtil.shouldSkipRenderTrain(vehicle)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "lambda$render$4",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private static void rte$skipDynamicVehicleWhenHidden(
            StoredMatrixTransformations smt, VehicleExtension vehicle,
            int idx, int[] arr, PositionAndRotation posRot,
            boolean flag, int count, DynamicVehicleModel dvm, CallbackInfo ci) {
        if (RenderUtil.shouldSkipRenderTrain(vehicle)) {
            ci.cancel();
        }
    }
    private static String rte$lastBodySignature = "";
    private static long rte$lastBogieLog = 0;

    @Redirect(
        method = "lambda$render$5",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mod/resource/VehicleResource;queueBogie(ILorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mod/data/VehicleExtension;I)V"),
        require = 0
    )
    private static void rte$queueBogieWithRoll(
            VehicleResource vehicleResource, int bogieIndex, StoredMatrixTransformations smt,
            VehicleExtension vehicle, int light) {
        StoredMatrixTransformations effectiveSmt = smt;
        try {
            final RenderVehicleCarFrameState state = RTE_CAR_FRAME_STATE.get();
            if (state != null && state.vehicle instanceof cn.jstxjf_.rte.data.TrainExtraSupplier) {
                final float roll = cn.jstxjf_.rte.ClientConfig.vanillaVehicleMode ? 0 : cn.jstxjf_.rte.data.TrainExtraSupplier.getRollAngleAt(state.vehicle, state.carNumber);
                if (Math.abs(roll) > 1e-5F) {
                    final boolean reversed = ((cn.jstxjf_.rte.data.TrainExtraSupplier) state.vehicle).getVehicleReversed();
                    final float degrees = (float) Math.toDegrees(reversed ? roll : -roll);
                    effectiveSmt = smt.copy();
                    effectiveSmt.add(graphicsHolder -> {
                        graphicsHolder.translate(0, 1, 0);
                        graphicsHolder.rotateZDegrees(degrees);
                        graphicsHolder.translate(0, -1, 0);
                    });
                }
            }
        } catch (Throwable ignored) {
        }
        vehicleResource.queueBogie(bogieIndex, effectiveSmt, vehicle, light);
    }

    @Redirect(
        method = "lambda$render$14",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mod/resource/VehicleResource;queue(Lorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mod/data/VehicleExtension;IIIZ)V"),
        require = 0
    )
    private static void rte$queueBodyWithRoll(
            VehicleResource vehicleResource, StoredMatrixTransformations smt, VehicleExtension vehicle,
            int carNumber, int totalCars, int light, boolean fromResourcePackCreator) {
        StoredMatrixTransformations effectiveSmt = smt;
        try {
            if (vehicle instanceof cn.jstxjf_.rte.data.TrainExtraSupplier) {
                final float roll = cn.jstxjf_.rte.ClientConfig.vanillaVehicleMode ? 0 : cn.jstxjf_.rte.data.TrainExtraSupplier.getRollAngleAt(vehicle, carNumber);
                if (Math.abs(roll) > 1e-5F) {
                    final boolean reversed = ((cn.jstxjf_.rte.data.TrainExtraSupplier) vehicle).getVehicleReversed();
                    final float degrees = (float) Math.toDegrees(reversed ? roll : -roll);
                    effectiveSmt = smt.copy();
                    effectiveSmt.add(graphicsHolder -> {
                        graphicsHolder.translate(0, 1, 0);
                        graphicsHolder.rotateZDegrees(degrees);
                        graphicsHolder.translate(0, -1, 0);
                    });
                }
            }
        } catch (Throwable ignored) {
        }
        vehicleResource.queue(effectiveSmt, vehicle, carNumber, totalCars, light, fromResourcePackCreator);
    }

    @Redirect(
        method = "lambda$render$12",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/DynamicVehicleModel;render(Lorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mod/data/VehicleExtension;I[IILorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Z)V"),
        require = 0
    )
    private static void rte$renderNativeBodyWithRoll(
            DynamicVehicleModel model, StoredMatrixTransformations smt, VehicleExtension vehicle,
            int light, int[] arr, int index,
            ObjectArrayList<org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectDoubleImmutablePair<org.mtr.mapping.holder.Box>> list,
            boolean flag) {
        StoredMatrixTransformations effectiveSmt = smt;
        try {
            final RenderVehicleCarFrameState state = RTE_CAR_FRAME_STATE.get();
            if (state != null && state.vehicle instanceof cn.jstxjf_.rte.data.TrainExtraSupplier) {
                final float roll = cn.jstxjf_.rte.ClientConfig.vanillaVehicleMode ? 0 : cn.jstxjf_.rte.data.TrainExtraSupplier.getRollAngleAt(state.vehicle, state.carNumber);
                if (Math.abs(roll) > 1e-5F) {
                    final boolean reversed = ((cn.jstxjf_.rte.data.TrainExtraSupplier) state.vehicle).getVehicleReversed();
                    final float degrees = (float) Math.toDegrees(reversed ? roll : -roll);
                    effectiveSmt = smt.copy();
                    effectiveSmt.add(graphicsHolder -> {
                        graphicsHolder.translate(0, 1, 0);
                        graphicsHolder.rotateZDegrees(degrees);
                        graphicsHolder.translate(0, -1, 0);
                    });
                }
            }
        } catch (Throwable ignored) {
        }
        model.render(effectiveSmt, vehicle, light, arr, index, list, flag);
    }



    @Inject(method = "lambda$render$14", at = @At("HEAD"), require = 0)
    private static void rte$beginCarFrame(
            VehicleExtension vehicle, ObjectObjectImmutablePair<?, ?> vehicleCarDetails,
            Vector3d offsetVector, Double offsetRotation, PositionAndRotation ridingCarPositionAndRotation,
            Vector3d cameraShakeOffset, int carNumber, ClientPlayerEntity clientPlayerEntity,
            int ridingCarNumber, boolean canRide, ObjectArrayList<?> previousGangwayPositionsList,
            ObjectArrayList<?> previousBarrierPositionsList, MinecraftClient minecraftClient,
            ClientWorld clientWorld, long millisElapsed, @Coerce Object previousGangwayMovementPositions,
            ObjectBooleanImmutablePair<?> vehicleResourcePair, CallbackInfo ci) {
        PositionAndRotation absolutePositionAndRotation = null;
        try {
            final Object right = vehicleCarDetails.right();
            if (right instanceof ObjectObjectImmutablePair<?, ?> rightPair && rightPair.right() instanceof PositionAndRotation positionAndRotation) {
                absolutePositionAndRotation = positionAndRotation;
            }
        } catch (Exception ignored) {
        }
        cn.jstxjf_.rte.render.train.CurrentVehicleTracker.setCar(carNumber);
        RTE_CAR_FRAME_STATE.set(new RenderVehicleCarFrameState(vehicle, carNumber,
            offsetVector, offsetRotation, ridingCarPositionAndRotation, cameraShakeOffset, absolutePositionAndRotation));
        RidingRollTransform.begin(vehicle, carNumber);
    }

    private static final java.util.HashMap<Long, double[]> RTE_LAST_DOOR_CHECK = new java.util.HashMap<>();
    private static final long RTE_DOOR_CHECK_IDLE_REFRESH_MILLIS = 1000;
    private static final long RTE_DOOR_CHECK_ACTIVE_REFRESH_MILLIS = 40;

    @Redirect(
        method = "lambda$render$9",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/RenderVehicleHelper;canOpenDoors(Lorg/mtr/mapping/holder/Box;Lorg/mtr/mod/render/PositionAndRotation;D)Z"),
        require = 0
    )
    private static boolean rte$skipIdleDoorCheck(Box doorway, PositionAndRotation positionAndRotation, double doorValue) {
        final RenderVehicleCarFrameState state = RTE_CAR_FRAME_STATE.get();
        if (state == null || state.vehicle == null) {
            return org.mtr.mod.render.RenderVehicleHelper.canOpenDoors(doorway, positionAndRotation, doorValue);
        }
        final long key = (state.vehicle.getId() * 31 + state.carNumber) * 31 + System.identityHashCode(doorway);
        final double[] previous = RTE_LAST_DOOR_CHECK.get(key);
        final long now = System.currentTimeMillis();
        final double x = positionAndRotation.position.x;
        final double z = positionAndRotation.position.z;
        if (previous != null && previous[0] == doorValue) {
            if (doorValue == 0 && !state.vehicle.persistentVehicleData.checkCanOpenDoors()) {
                return false;
            }
            final long refresh = previous[1] != 0 ? RTE_DOOR_CHECK_ACTIVE_REFRESH_MILLIS : RTE_DOOR_CHECK_IDLE_REFRESH_MILLIS;
            if (previous[2] == x && previous[3] == z && now - (long) previous[4] < refresh) {
                return previous[1] != 0;
            }
        }
        if (RTE_LAST_DOOR_CHECK.size() > 8192) {
            RTE_LAST_DOOR_CHECK.clear();
        }
        final boolean result = org.mtr.mod.render.RenderVehicleHelper.canOpenDoors(doorway, positionAndRotation, doorValue);
        RTE_LAST_DOOR_CHECK.put(key, new double[]{doorValue, result ? 1 : 0, x, z, now});
        return result;
    }

    @Inject(method = "lambda$render$14", at = @At("RETURN"), require = 0)
    private static void rte$endCarFrame(
            VehicleExtension vehicle, ObjectObjectImmutablePair<?, ?> vehicleCarDetails,
            Vector3d offsetVector, Double offsetRotation, PositionAndRotation ridingCarPositionAndRotation,
            Vector3d cameraShakeOffset, int carNumber, ClientPlayerEntity clientPlayerEntity,
            int ridingCarNumber, boolean canRide, ObjectArrayList<?> previousGangwayPositionsList,
            ObjectArrayList<?> previousBarrierPositionsList, MinecraftClient minecraftClient,
            ClientWorld clientWorld, long millisElapsed, @Coerce Object previousGangwayMovementPositions,
            ObjectBooleanImmutablePair<?> vehicleResourcePair, CallbackInfo ci) {
        RTE_CAR_FRAME_STATE.remove();
        RidingRollTransform.end();
    }

    @Redirect(
        method = "lambda$render$14",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mod/resource/VehicleResource;iterateModels(IILorg/mtr/mod/resource/VehicleResource$ModelConsumer;)V"),
        require = 0
    )
    private static void rte$iterateModelsAndRenderScript(VehicleResource vehicleResource, int carNumber, int totalCars, VehicleResource.ModelConsumer modelConsumer) {
        final RenderVehicleCarFrameState state = RTE_CAR_FRAME_STATE.get();
        if (state != null) {
            ScriptedTrainRenderers.renderIfScripted(state.vehicle, state.carNumber, org.mtr.mapping.mapper.GraphicsHolder.getDefaultLight(), state);
            if (cn.jstxjf_.rte.render.train.LegacyVehicleRenderer.renderIfLegacy(
                    state.vehicle, state.carNumber, org.mtr.mapping.mapper.GraphicsHolder.getDefaultLight(), state)) {
                return;
            }
        }
        vehicleResource.iterateModels(carNumber, totalCars, modelConsumer);
    }

    @Inject(method = "lambda$render$12", at = @At("HEAD"), require = 0)
    private static void rte$beginScriptedConnectionFrame(
            StoredMatrixTransformations storedMatrixTransformations, VehicleExtension vehicle, int carNumber,
            int[] scrollingDisplayIndexTracker, PositionAndRotation absoluteVehicleCarPositionAndRotation,
            ObjectArrayList<?> openDoorways, boolean fromResourcePackCreator,
            ObjectArrayList<?> previousGangwayPositionsList, ObjectArrayList<?> previousBarrierPositionsList,
            VehicleResource vehicleResource, PositionAndRotation vehicleCarRenderingPositionAndRotation,
            Vector3d offsetVector, ObjectObjectImmutablePair<?, ?> vehicleCarDetails,
        double oscillationAmount, int modelIndex, DynamicVehicleModel model, CallbackInfo ci) {
        ScriptedTrainRenderers.beginConnectionFrame(vehicle, carNumber);
    }

    @Inject(method = "lambda$render$12", at = @At("RETURN"), require = 0)
    private static void rte$endScriptedConnectionFrame(
            StoredMatrixTransformations storedMatrixTransformations, VehicleExtension vehicle, int carNumber,
            int[] scrollingDisplayIndexTracker, PositionAndRotation absoluteVehicleCarPositionAndRotation,
            ObjectArrayList<?> openDoorways, boolean fromResourcePackCreator,
            ObjectArrayList<?> previousGangwayPositionsList, ObjectArrayList<?> previousBarrierPositionsList,
            VehicleResource vehicleResource, PositionAndRotation vehicleCarRenderingPositionAndRotation,
            Vector3d offsetVector, ObjectObjectImmutablePair<?, ?> vehicleCarDetails,
            double oscillationAmount, int modelIndex, DynamicVehicleModel model, CallbackInfo ci) {
        ScriptedTrainRenderers.endConnectionFrame();
    }

    @Redirect(
        method = "renderPlayer",
        at = @At(value = "INVOKE", target = "Lorg/mtr/core/tool/Vector;rotateX(D)Lorg/mtr/core/tool/Vector;"),
        require = 0
    )
    private static Vector rte$rollRidingPlayerOffset(Vector vector, double pitch) {
        return RidingRollTransform.rollLocalForwards(vector).rotateX(pitch);
    }

    @Inject(method = "renderConnection", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$renderScriptedConnectionTexture(
            boolean shouldRender1, boolean shouldRender2, boolean canHaveLight,
            RenderVehicles.PreviousConnectionPositions previousConnectionPositions,
            Identifier innerSideTexture, Identifier innerTopTexture, Identifier innerBottomTexture,
            Identifier outerSideTexture, Identifier outerTopTexture, Identifier outerBottomTexture,
            PositionAndRotation positionAndRotation, boolean useOffset,
            double vehicleLength, double width, double height, double yOffset, double zOffset,
            double oscillationAmount, boolean isOnRoute, CallbackInfo ci) {
        // canHaveLight distinguishes MTR's gangway call from the barrier call.
        // ANTE/NTE commits drawConnModel only from renderConnection, never from
        // renderBarrier.
        if (canHaveLight && shouldRender1 && previousConnectionPositions instanceof PreviousConnectionPositionsAccessor accessor) {
            final Vector prevPos1 = accessor.rte$getPosition1();
            final Vector prevPos2 = accessor.rte$getPosition2();
            final Vector prevPos3 = accessor.rte$getPosition3();
            final Vector prevPos4 = accessor.rte$getPosition4();
            ScriptedTrainRenderers.renderConnectionIfScripted(canHaveLight, prevPos1, prevPos2, prevPos3, prevPos4, positionAndRotation, useOffset, vehicleLength, width, height, yOffset, zOffset, oscillationAmount, isOnRoute);
        }

        // A legacy scripted connector is the complete visual, not an overlay.
        // Keep the script commit above, then prevent MTR 4's connector material
        // from being rendered over it.
        if (ScriptedTrainRenderers.shouldSuppressNativeConnection(canHaveLight)) {
            ci.cancel();
        }
    }
}
