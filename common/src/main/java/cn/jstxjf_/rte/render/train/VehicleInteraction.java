package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.data.ScriptedCustomTrains;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.scripting.ScriptHolderBase;
import cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers;
import cn.jstxjf_.rte.scripting.train.TrainScriptContext;
import cn.jstxjf_.rte.scripting.ScriptUseBox;
import cn.jstxjf_.rte.scripting.ScriptUseEvent;
import cn.jstxjf_.rte.scripting.util.WrappedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.data.VehicleCar;
import org.mtr.mod.client.VehicleRidingMovement;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.PositionAndRotation;

public final class VehicleInteraction {

    private static final double MAX_REACH = 5.0;

    private VehicleInteraction() {
    }

    public static boolean tryUse() {
        try {
            return dispatch();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean dispatch() {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        if (player == null) {
            return false;
        }

        final VehicleExtension vehicle = CurrentVehicleTracker.get();
        if (vehicle == null || vehicle.vehicleExtraData == null) {
            return false;
        }
        if (!VehicleRidingMovement.isRiding(vehicle.getId())) {
            return false;
        }

        final Vec3 eye = player.getEyePosition();
        final Vec3 look = player.getViewVector(1.0F);

        ScriptUseBox bestBox = null;
        TrainScriptContext bestContext = null;
        ScriptHolderBase bestHolder = null;
        double bestDistance = Double.MAX_VALUE;
        double bestX = 0;
        double bestY = 0;
        double bestZ = 0;
        double bestDirX = 0;
        double bestDirY = 0;
        double bestDirZ = 0;

        final int carCount = vehicle.vehicleExtraData.immutableVehicleCars.size();
        for (int car = 0; car < carCount; car++) {
            final VehicleCar vehicleCar = vehicle.vehicleExtraData.immutableVehicleCars.get(car);
            final String vehicleTypeId = vehicleCar.getVehicleId();
            final TrainScriptContext context = ScriptedTrainRenderers.contextForCar(vehicle, car);
            if (context == null || context.useBoxes.isEmpty()) {
                continue;
            }
            final int localCar = context.localCar(car);
            final ScriptHolderBase holder = ScriptedCustomTrains.getScript(vehicleTypeId);
            if (holder == null) {
                continue;
            }

            final PositionAndRotation positionAndRotation = ScriptedTrainRenderers.getCarPositionAndRotation(vehicle, car);
            if (positionAndRotation == null) {
                continue;
            }
            final Matrix4f basePose = ScriptedTrainRenderers.createBasePose(vehicle, car, positionAndRotation);
            final org.joml.Matrix4f inverse = new org.joml.Matrix4f(basePose.asMoj()).invert();

            final org.joml.Vector3f localOrigin = inverse.transformPosition(
                new org.joml.Vector3f((float) eye.x, (float) eye.y, (float) eye.z));
            final org.joml.Vector3f localDirection = inverse.transformDirection(
                new org.joml.Vector3f((float) look.x, (float) look.y, (float) look.z)).normalize();

            for (final ScriptUseBox box : context.useBoxes.values()) {
                if (box.car != localCar) {
                    continue;
                }
                final double distance = box.intersect(
                    localOrigin.x, localOrigin.y, localOrigin.z,
                    localDirection.x, localDirection.y, localDirection.z, MAX_REACH);
                if (distance < 0 || distance >= bestDistance) {
                    continue;
                }
                bestDistance = distance;
                bestBox = box;
                bestContext = context;
                bestHolder = holder;
                bestDirX = localDirection.x;
                bestDirY = localDirection.y;
                bestDirZ = localDirection.z;
                bestX = localOrigin.x + localDirection.x * distance;
                bestY = localOrigin.y + localDirection.y * distance;
                bestZ = localOrigin.z + localDirection.z * distance;
            }
        }

        if (bestBox == null) {
            return false;
        }

        final ScriptUseEvent event = new ScriptUseEvent(
            bestBox.id, bestBox.car,
            bestX, bestY, bestZ, bestDistance,
            bestDirX, bestDirY, bestDirZ,
            new WrappedEntity(player),
            itemId(player.getMainHandItem()),
            itemId(player.getOffhandItem()),
            player.isShiftKeyDown());

        bestHolder.tryCallFunctionAsync("use", bestContext, null, event);
        return true;
    }

    private static String itemId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "minecraft:air";
        }
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }
}
