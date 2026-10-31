package cn.jstxjf_.rte.scripting.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.data.ScriptedCustomTrains;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.render.RenderUtil;
import cn.jstxjf_.rte.scripting.ScriptHolderBase;
import cn.jstxjf_.rte.scripting.util.client.MinecraftClientUtil;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import org.mtr.core.data.VehicleCar;
import org.mtr.core.tool.Vector;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.LightType;
import org.mtr.mapping.holder.LightmapTextureManager;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.Init;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.PositionAndRotation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class ScriptedTrainRenderers {

    private static final Map<Long, TrainScriptContext> CONTEXTS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final Map<Long, CarFrameState> CAR_FRAME_STATES = new HashMap<>();
    private static final Set<Long> CONNECTIONS_RENDERED_THIS_PASS = new java.util.HashSet<>();
    private static final Map<Long, DoorSideState> DOOR_SIDE_STATES = new HashMap<>();
    private static final ThreadLocal<ConnectionFrameState> CONNECTION_FRAME_STATE = new ThreadLocal<>();
    private static int currentRenderPass;
    private static final Set<String> RENDER_TRACE = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private static void trace(String vehicleId, String stage) {
        if (ClientConfig.enableRenderDiagnostics && RENDER_TRACE.add(vehicleId + '|' + stage)) {
            RTE.LOGGER.info("[ScriptedTrainRender] {} -> {}", vehicleId, stage);
        }
    }

    private ScriptedTrainRenderers() {
    }

    public static void beginFrame() {
        cn.jstxjf_.rte.util.JcmCompat.beginFrame();
        currentRenderPass++;
        CONNECTIONS_RENDERED_THIS_PASS.clear();
        if (currentRenderPass == Integer.MAX_VALUE) {
            currentRenderPass = 1;
            CAR_FRAME_STATES.clear();
        }
    }

    public static void clear() {
        CONTEXTS.clear();
        UNIT_LAYOUTS.clear();
        cn.jstxjf_.rte.render.train.TrainUnits.clear();
        CAR_FRAME_STATES.clear();
        CONNECTIONS_RENDERED_THIS_PASS.clear();
        DOOR_SIDE_STATES.clear();
        CONNECTION_FRAME_STATE.remove();
        RENDER_TRACE.clear();
        currentRenderPass = 0;
        TrainWrapper.clearGlobalCaches();
        cn.jstxjf_.rte.render.train.VehiclePathCache.clear();
        cn.jstxjf_.rte.scripting.compat.ClientDataCompat.clearVehicleSchedules();
    }

    public static void beginConnectionFrame(VehicleExtension vehicle, int carIndex) {
        CONNECTION_FRAME_STATE.set(new ConnectionFrameState(vehicle, carIndex));
    }

    public static void endConnectionFrame() {
        CONNECTION_FRAME_STATE.remove();
    }

    public static boolean shouldSuppressNativeConnection(boolean canHaveLight) {
        if (!canHaveLight) {
            return false;
        }
        final ConnectionFrameState state = CONNECTION_FRAME_STATE.get();
        if (state == null || state.vehicle == null || state.vehicle.vehicleExtraData == null) {
            return false;
        }
        try {
            final int car = Math.max(0, Math.min(state.carIndex,
                state.vehicle.vehicleExtraData.immutableVehicleCars.size() - 1));
            final String vehicleId = state.vehicle.vehicleExtraData.immutableVehicleCars
                .get(car).getVehicleId();
            final UnitLayout layout = unitLayout(state.vehicle);
            if (car > 0 && layout.start[car] >= 0 && layout.start[car - 1] >= 0 && !layout.sameUnit(car - 1, car)) {
                return true;
            }
            return cn.jstxjf_.rte.ClientConfig.autoGangwayPolicy
                && cn.jstxjf_.rte.data.GangwayPolicy.isSelfHandled(vehicleId);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static double rte$rollOf(VehicleExtension vehicle, int carIndex) {
        if (!ClientConfig.enableRolling || vehicle == null || carIndex < 0) {
            return 0;
        }
        try {
            final float roll = cn.jstxjf_.rte.data.TrainExtraSupplier.getRollAngleAt(vehicle, carIndex);
            final boolean reversed = ((cn.jstxjf_.rte.data.TrainExtraSupplier) vehicle).getVehicleReversed();
            return reversed ? roll : -roll;
        } catch (Throwable e) {
            return 0;
        }
    }

    public static void renderConnectionIfScripted(boolean canHaveLight,
                                                  Vector prevPos1, Vector prevPos2, Vector prevPos3, Vector prevPos4,
                                                  PositionAndRotation positionAndRotation, boolean useOffset,
                                                  double vehicleLength, double width, double height, double yOffset, double zOffset,
                                                  double oscillationAmount, boolean isOnRoute) {
        final ConnectionFrameState frameState = CONNECTION_FRAME_STATE.get();
        if (frameState == null || frameState.vehicle == null || frameState.carIndex <= 0 || RenderUtil.shouldSkipRenderTrain(frameState.vehicle)) {
            return;
        }
        if (prevPos1 == null || prevPos2 == null || prevPos3 == null || prevPos4 == null) {
            return;
        }

        final VehicleExtension connVehicle = frameState.vehicle;
        if (connVehicle.vehicleExtraData == null || frameState.carIndex >= connVehicle.vehicleExtraData.immutableVehicleCars.size()) {
            return;
        }
        final String connVehicleId = connVehicle.vehicleExtraData.immutableVehicleCars.get(frameState.carIndex).getVehicleId();
        final UnitLayout layout = unitLayout(connVehicle);
        if (!layout.sameUnit(frameState.carIndex - 1, frameState.carIndex)) {
            return;
        }
        final TrainScriptContext context = contextForCar(connVehicle, frameState.carIndex);
        if (context == null) {
            return;
        }
        final int localConn = context.localCar(frameState.carIndex - 1);

        final long connectionKey = getStateKey(connVehicle.getId(), frameState.carIndex - 1);
        if (!CONNECTIONS_RENDERED_THIS_PASS.add(connectionKey)) {
            return;
        }

        final double halfLength = vehicleLength / 2;
        final double newOscillationAmount = -Math.toRadians(oscillationAmount);
        final Vector thisPos1 = positionAndRotation.transformForwards(new Vector(-width / 2, yOffset + IGui.SMALL_OFFSET, zOffset - halfLength).rotateZ(newOscillationAmount), Vector::rotateX, Vector::rotateY, Vector::add);
        final Vector thisPos2 = positionAndRotation.transformForwards(new Vector(-width / 2, height + yOffset + IGui.SMALL_OFFSET, zOffset - halfLength).rotateZ(newOscillationAmount), Vector::rotateX, Vector::rotateY, Vector::add);
        final Vector thisPos3 = positionAndRotation.transformForwards(new Vector(width / 2, height + yOffset + IGui.SMALL_OFFSET, zOffset - halfLength).rotateZ(newOscillationAmount), Vector::rotateX, Vector::rotateY, Vector::add);
        final Vector thisPos4 = positionAndRotation.transformForwards(new Vector(width / 2, yOffset + IGui.SMALL_OFFSET, zOffset - halfLength).rotateZ(newOscillationAmount), Vector::rotateX, Vector::rotateY, Vector::add);

        final ClientWorld clientWorld = MinecraftClient.getInstance().getWorldMapped();
        if (clientWorld == null) {
            return;
        }

        final BlockPos blockPosConnection = Init.newBlockPos(thisPos1.x, thisPos1.y + 1, thisPos1.z);
        final int exteriorLight = LightmapTextureManager.pack(clientWorld.getLightLevel(LightType.getBlockMapped(), blockPosConnection), clientWorld.getLightLevel(LightType.getSkyMapped(), blockPosConnection));
        final int interiorLight = canHaveLight && isOnRoute ? org.mtr.mapping.mapper.GraphicsHolder.getDefaultLight() : exteriorLight;
        synchronized (context) {
            context.commitConn(frameState.carIndex - 1, RTEClient.drawScheduler,
                rte$createConnectionPose(connVehicle, useOffset,
                    prevPos1, prevPos4, thisPos1, thisPos4),
                createViewPose(), exteriorLight);
            context.scriptResult.commitConnStretchTexture(localConn, useOffset, Vector3d.getZeroMapped(), exteriorLight, interiorLight, prevPos1, prevPos2, prevPos3, prevPos4, thisPos1, thisPos2, thisPos3, thisPos4);
            if (!cn.jstxjf_.rte.data.GangwayPolicy.hasDeclaredConnection(connVehicleId)
                    && (!cn.jstxjf_.rte.ClientConfig.autoGangwayPolicy
                    || !cn.jstxjf_.rte.data.GangwayPolicy.isSelfHandled(connVehicleId))) {
                cn.jstxjf_.rte.render.train.BuiltinGangwayRenderer.renderFromCorners(
                    prevPos1, prevPos2, prevPos3, prevPos4,
                    thisPos1, thisPos2, thisPos3, thisPos4,
                    vehicleLength, rte$rollOf(connVehicle, frameState.carIndex - 1),
                    rte$rollOf(connVehicle, frameState.carIndex),
                    useOffset, exteriorLight, interiorLight);
            }
        }
    }

    private static Matrix4f rte$createConnectionPose(VehicleExtension vehicle, boolean useOffset,
                                                      Vector prevPos1, Vector prevPos4,
                                                      Vector thisPos1, Vector thisPos4) {
        final double prevX = (prevPos1.x + prevPos4.x) * 0.5;
        final double prevY = (prevPos1.y + prevPos4.y) * 0.5;
        final double prevZ = (prevPos1.z + prevPos4.z) * 0.5;
        final double thisX = (thisPos1.x + thisPos4.x) * 0.5;
        final double thisY = (thisPos1.y + thisPos4.y) * 0.5;
        final double thisZ = (thisPos1.z + thisPos4.z) * 0.5;

        final Matrix4f pose;
        if (useOffset) {
            pose = createViewPose();
        } else {
            pose = new Matrix4f();
            final Vector3d mtrOffset = RenderUtil.mtrRenderOffset;
            if (mtrOffset != null) {
                final net.minecraft.world.phys.Vec3 camera =
                    net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                pose.translate(
                    (float) (mtrOffset.getXMapped() - camera.x),
                    (float) (mtrOffset.getYMapped() - camera.y),
                    (float) (mtrOffset.getZMapped() - camera.z));
            }
        }

        pose.translate((float) ((prevX + thisX) * 0.5),
            (float) ((prevY + thisY) * 0.5),
            (float) ((prevZ + thisZ) * 0.5));
        final double dx = thisX - prevX;
        final double dy = thisY - prevY;
        final double dz = thisZ - prevZ;
        final double horizontal = Math.hypot(dx, dz);
        final double distance = Math.hypot(horizontal, dy);
        if (distance > 1.0e-6 && Double.isFinite(distance)) {
            pose.rotateY((float) (Math.PI + Math.atan2(dx, dz)));
            final float pitch = (float) Math.atan2(dy, horizontal);
            final boolean hasPitch = pitch < 0
                ? vehicle.getTransportMode().hasPitchAscending
                : vehicle.getTransportMode().hasPitchDescending;
            pose.rotateX(hasPitch ? pitch : 0);
        }
        return pose;
    }

    private static final java.util.Set<String> RTE_DIAG_SEEN =
        java.util.concurrent.ConcurrentHashMap.newKeySet();

    private static long rte$nextGapProbe = 0;

    private static void rte$probeCarGaps(VehicleExtension vehicle, int carIndex) {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics || carIndex != 0) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now < rte$nextGapProbe) {
            return;
        }
        rte$nextGapProbe = now + 2000;
        try {
            final int cars = vehicle.vehicleExtraData.immutableVehicleCars.size();
            final StringBuilder line = new StringBuilder();
            PositionAndRotation previous = null;
            for (int c = 0; c < cars; c++) {
                final PositionAndRotation par = getCarPositionAndRotation(vehicle, c);
                if (par == null) {
                    line.append("car").append(c).append("=null ");
                    continue;
                }
                if (previous != null) {
                    final double dx = par.position.x - previous.position.x;
                    final double dy = par.position.y - previous.position.y;
                    final double dz = par.position.z - previous.position.z;
                    line.append(String.format(java.util.Locale.ROOT,
                        "gap%d-%d=%.3f yawDelta=%.1f  ", c - 1, c,
                        Math.sqrt(dx * dx + dy * dy + dz * dz),
                        Math.toDegrees(par.yaw - previous.yaw)));
                }
                previous = par;
            }
            cn.jstxjf_.rte.RTE.LOGGER.info("[GapProbe] {} cars={} {}",
                vehicle.vehicleExtraData.immutableVehicleCars.get(0).getVehicleId(),
                cars, line.toString().trim());
        } catch (Throwable ignored) {
        }
    }

    private static long rte$nextSideProbe = 0;

    private static void rte$probeDoorSides(String vehicleId, int carIndex, float doorValue,
                                           DoorSideState resolved, TrainWrapper visibleToScript) {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now < rte$nextSideProbe) {
            return;
        }
        rte$nextSideProbe = now + 1000;
        try {
            cn.jstxjf_.rte.RTE.LOGGER.info(
                "[SideProbe] {} car{} doorValue={} resolved=L{}/R{} scriptSees=L{}/R{} stale={}",
                vehicleId, carIndex,
                String.format(java.util.Locale.ROOT, "%.3f", doorValue),
                resolved.leftOpen, resolved.rightOpen,
                java.util.Arrays.toString(visibleToScript.doorLeftOpen),
                java.util.Arrays.toString(visibleToScript.doorRightOpen),
                visibleToScript == null);
        } catch (Throwable ignored) {
        }
    }

    private static long rte$nextDoorProbe = 0;

    private static void rte$probeDoor(VehicleExtension vehicle, String vehicleId) {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now < rte$nextDoorProbe) {
            return;
        }
        rte$nextDoorProbe = now + 1000;
        try {
            cn.jstxjf_.rte.RTE.LOGGER.info(
                "[DoorProbe] {} doorValue={} elapsed={} runningSeconds={}",
                vehicleId,
                String.format(java.util.Locale.ROOT, "%.3f",
                    vehicle.persistentVehicleData.getDoorValue()),
                String.format(java.util.Locale.ROOT, "%.2f",
                    cn.jstxjf_.rte.scripting.util.TimingUtil.elapsed()),
                String.format(java.util.Locale.ROOT, "%.2f",
                    cn.jstxjf_.rte.scripting.util.TimingUtil.runningSeconds));
        } catch (Throwable ignored) {
        }
    }

    private static void rte$diagOnce(String vehicleId, String what) {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            return;
        }
        if (RTE_DIAG_SEEN.add(vehicleId + "|" + what)) {
            cn.jstxjf_.rte.RTE.LOGGER.info("[RteDiag] {} -> {}", vehicleId, what);
        }
    }

    public static void renderIfScripted(VehicleExtension vehicle, int carIndex, int light) {
        renderIfScripted(vehicle, carIndex, light, null);
    }

    public static void renderIfScripted(VehicleExtension vehicle, int carIndex, int light, cn.jstxjf_.rte.render.train.RenderVehicleCarFrameState frameState) {
        if (cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            rte$traceSpacing(vehicle);
        }
        if (vehicle == null || vehicle.vehicleExtraData == null || carIndex < 0 || carIndex >= vehicle.vehicleExtraData.immutableVehicleCars.size()) {
            return;
        }
        final String vehicleId = vehicle.vehicleExtraData.immutableVehicleCars.get(carIndex).getVehicleId();
        if (cn.jstxjf_.rte.util.JcmCompat.shouldAutoDedupe()
                && cn.jstxjf_.rte.util.JcmCompat.isVehicleTakenByJcm(vehicle.getHexId())) {
            rte$diagOnce(vehicleId, "SKIPPED: deduped away to JCM");
            return;
        }

        final ScriptHolderBase holder = ScriptedCustomTrains.getScript(vehicleId);
        if (holder == null) {
            rte$diagOnce(vehicleId, "SKIPPED: RTE has no script holder for this vehicle id");
            trace(vehicleId, "no script holder");
            return;
        }
        if (ClientConfig.enableRenderDiagnostics) {
            rte$probeDoor(vehicle, vehicleId);
            rte$diagOnce(vehicleId, "RTE will render; engine=" + holder.engineType
                + " functions=" + holder.functions.keySet()
                + (holder.failException != null ? " parseOrRunFailure=" + holder.failException : " noFailureRecorded"));
            try {
                final org.mtr.core.data.VehicleCar c0 = vehicle.vehicleExtraData.immutableVehicleCars.get(0);
                final StringBuilder lens = new StringBuilder();
                for (final org.mtr.core.data.VehicleCar c : vehicle.vehicleExtraData.immutableVehicleCars) {
                    lens.append(String.format(java.util.Locale.ROOT, "%.2f ", c.getLength()));
                }
                trace(vehicleId, String.format(java.util.Locale.ROOT,
                    "cars=%d car0Len=%.2f width=%.2f lens=[%s]",
                    vehicle.vehicleExtraData.immutableVehicleCars.size(), c0.getLength(), c0.getWidth(), lens.toString().trim()));
            } catch (Exception ignored) {
            }
            trace(vehicleId, "holder engine=" + holder.engineType
                + " functions=" + holder.functions.keySet()
                + (holder.failException != null ? " FAILED=" + holder.failException : ""));
        }

        final CarFrameState carFrameState = CAR_FRAME_STATES.computeIfAbsent(getStateKey(vehicle.getId(), carIndex), ignored -> new CarFrameState());
        if (carFrameState.renderPass == currentRenderPass) {
            return;
        }
        carFrameState.renderPass = currentRenderPass;

        final UnitLayout layout = unitLayout(vehicle);
        final int unitStart = layout.start[carIndex];
        final int unitCount = layout.count[carIndex];
        if (unitStart < 0) {
            return;
        }
        final int localCar = carIndex - unitStart;
        final long scriptContextKey = contextKey(vehicle.getId(), unitStart, unitCount);
        TrainScriptContext context = CONTEXTS.computeIfAbsent(scriptContextKey, ignored -> new TrainScriptContext(vehicle, unitStart, unitCount));
        if (context.disposed) {
            context.releaseResources();
            context = new TrainScriptContext(vehicle, unitStart, unitCount);
            CONTEXTS.put(scriptContextKey, context);
        }
        context.rebind(vehicle);
        final PositionAndRotation positionAndRotation = frameState != null && frameState.absolutePositionAndRotation != null
            ? frameState.absolutePositionAndRotation
            : getCarPositionAndRotation(vehicle, carIndex);
        if (positionAndRotation == null) {
            trace(vehicleId, "no position");
            return;
        }

        final boolean offsetMode = frameState != null && frameState.offsetVector != null && frameState.ridingCarPositionAndRotation != null;
        final Matrix4f carPose = createBasePose(vehicle, carIndex, positionAndRotation);
        final Matrix4f worldPose = createViewPose();
        final Matrix4f basePose;
        if (offsetMode) {
            final PositionAndRotation renderPositionAndRotation = org.mtr.mod.render.RenderVehicles.getRenderPositionAndRotation(
                frameState.offsetVector, frameState.offsetRotation, frameState.ridingCarPositionAndRotation,
                positionAndRotation,
                frameState.cameraShakeOffset != null ? frameState.cameraShakeOffset : Vector3d.getZeroMapped());
            final Vector3d mtrOffset = RenderUtil.mtrRenderOffset;
            basePose = new Matrix4f();
            if (mtrOffset != null) {
                final net.minecraft.world.phys.Vec3 camera = net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                basePose.translate(
                    (float) (mtrOffset.getXMapped() - camera.x),
                    (float) (mtrOffset.getYMapped() - camera.y),
                    (float) (mtrOffset.getZMapped() - camera.z));
            }
            basePose.multiply(createBasePose(vehicle, carIndex, renderPositionAndRotation));
        } else {
            basePose = worldPose.copy();
            basePose.multiply(carPose);
        }
        final TrainWrapper currentWrapper = context.trainExtra;
        final TrainWrapper writingWrapper = context.getTrainExtraWriting();
        final float currentDoorValue = (float) vehicle.persistentVehicleData.getDoorValue();

        writingWrapper.previousDoorValue = currentWrapper.currentDoorValue;
        writingWrapper.currentDoorValue = currentDoorValue;
        writingWrapper.shouldRender = !RenderUtil.shouldSkipRenderTrain(vehicle);
        if (!writingWrapper.shouldRender) {
            trace(vehicleId, "skipped by shouldSkipRenderTrain");
        }
        writingWrapper.isInDetailDistance = true;
        if (ClientConfig.traceDoorSides) {
            rte$traceSpacing(vehicle);
            rte$traceCarPosition(vehicle, carIndex, positionAndRotation);
        }
        final DoorSideState doorSideState = resolveDoorSides(vehicle, carIndex, positionAndRotation, currentDoorValue);
        writingWrapper.doorLeftOpen[localCar] = doorSideState.leftOpen;
        writingWrapper.doorRightOpen[localCar] = doorSideState.rightOpen;
        rte$probeDoorSides(vehicleId, carIndex, currentDoorValue, doorSideState, currentWrapper);
        rte$probeCarGaps(vehicle, carIndex);
        if (localCar == 0) {
            currentWrapper.rte$probeConnAngles();
        }
        writingWrapper.lastWorldPose[localCar] = carPose.copy();
        writingWrapper.lastCarPosition[localCar] = carPose.getTranslationPart();
        writingWrapper.lastCarRotation[localCar] = new Vector3f(0, (float) positionAndRotation.yaw, (float) positionAndRotation.pitch);

        synchronized (context) {
            if (writingWrapper.shouldRender) {
                trace(vehicleId, "commitCar");
                context.commitCar(carIndex, RTEClient.drawScheduler, basePose, worldPose, light);
            }
        }

        if (carIndex == layout.lastScripted) {
            for (int c = 0; c < layout.start.length; c++) {
                if (layout.start[c] != c) {
                    continue;
                }
                final ScriptHolderBase carHolder = ScriptedCustomTrains.getScript(
                    vehicle.vehicleExtraData.immutableVehicleCars.get(c).getVehicleId());
                if (carHolder == null) {
                    continue;
                }
                final TrainScriptContext carContext = CONTEXTS.get(contextKey(vehicle.getId(), c, layout.count[c]));
                if (carContext != null) {
                    rte$reinitOnPathChange(carContext);
                    carContext.extraFinished();
                    carHolder.tryCallRenderFunctionAsync(carContext);
                }
            }
        }
    }

    private static void rte$reinitOnPathChange(TrainScriptContext context) {
        if (context == null || context.trainExtra == null) {
            return;
        }
        if (!context.trainExtra.rte$consumePathChanged()) {
            return;
        }
        context.trainExtra.rte$invalidatePathSnapshot();
        cn.jstxjf_.rte.RTE.LOGGER.debug(
            "[Script] vehicle {} path changed, snapshot invalidated",
            context.train == null ? "?" : context.train.getHexId());
    }

    public static void dropDeadContexts() {
        final java.util.Set<Long> aliveVehicleIds = new java.util.HashSet<>();
        org.mtr.mod.client.MinecraftClientData.getInstance().vehicles.forEach(vehicle -> aliveVehicleIds.add(vehicle.getId()));
        UNIT_LAYOUTS.keySet().removeIf(id -> !aliveVehicleIds.contains(id));
        cn.jstxjf_.rte.render.train.TrainUnits.retainVehicles(aliveVehicleIds);
        synchronized (CONTEXTS) {
            CONTEXTS.entrySet().removeIf(entry -> {
                final TrainScriptContext context = entry.getValue();
                if (context == null) {
                    return true;
                }
                if (context.disposed) {
                    if (context.scriptStatus != null && !context.scriptStatus.isDone()) {
                        return false;
                    }
                } else {
                    final boolean alive;
                    try {
                        alive = context.isBearerAlive();
                    } catch (Exception e) {
                        return false;
                    }
                    if (!context.rte$deadFor(cn.jstxjf_.rte.scripting.ScriptContextManager.DISPOSE_GRACE_NANOS * 2, alive, System.nanoTime())) {
                        return false;
                    }
                }
                try {
                    context.releaseResources();
                } catch (Exception ignored) {
                }
                return true;
            });
        }
    }

    public static boolean hasUnit(VehicleExtension vehicle, int unitStart, int unitCount) {
        final UnitLayout layout = unitLayout(vehicle);
        return unitStart >= 0 && unitStart < layout.start.length
            && layout.start[unitStart] == unitStart && layout.count[unitStart] == unitCount;
    }

    public static TrainScriptContext contextForCar(VehicleExtension vehicle, int carIndex) {
        final UnitLayout layout = unitLayout(vehicle);
        if (carIndex < 0 || carIndex >= layout.start.length || layout.start[carIndex] < 0) {
            return null;
        }
        return CONTEXTS.get(contextKey(vehicle.getId(), layout.start[carIndex], layout.count[carIndex]));
    }

    private static long contextKey(long vehicleId, int unitStart, int unitCount) {
        return vehicleId * 1000003L + unitStart * 1009L + unitCount;
    }

    private static final Map<Long, UnitLayout> UNIT_LAYOUTS = new java.util.concurrent.ConcurrentHashMap<>();

    static final class UnitLayout {
        final Object cars;
        final int[] start;
        final int[] count;
        final int lastScripted;

        private UnitLayout(Object cars, int[] start, int[] count) {
            this.cars = cars;
            this.start = start;
            this.count = count;
            int last = -1;
            for (int i = 0; i < start.length; i++) {
                if (start[i] >= 0) {
                    last = i;
                }
            }
            this.lastScripted = last;
        }

        boolean sameUnit(int carA, int carB) {
            return carA >= 0 && carB >= 0 && carA < start.length && carB < start.length
                && start[carA] >= 0 && start[carA] == start[carB];
        }
    }

    static UnitLayout unitLayout(VehicleExtension vehicle) {
        final java.util.List<VehicleCar> cars = vehicle.vehicleExtraData.immutableVehicleCars;
        final UnitLayout cached = UNIT_LAYOUTS.get(vehicle.getId());
        if (cached != null && cached.cars == cars) {
            return cached;
        }
        final int size = cars.size();
        final int[] start = new int[size];
        final int[] count = new int[size];
        ScriptHolderBase previousHolder = null;
        String previousId = null;
        int currentStart = -1;
        for (int i = 0; i < size; i++) {
            final String id = cars.get(i).getVehicleId();
            final ScriptHolderBase holder = ScriptedCustomTrains.getScript(id);
            if (holder == null) {
                start[i] = -1;
                currentStart = -1;
                previousHolder = null;
                previousId = null;
                continue;
            }
            if (currentStart < 0 || holder != previousHolder || rte$isHeadCar(id) || rte$isTailCar(previousId)) {
                currentStart = i;
            }
            start[i] = currentStart;
            previousHolder = holder;
            previousId = id;
        }
        for (int i = 0; i < size; i++) {
            if (start[i] >= 0) {
                count[start[i]]++;
            }
        }
        for (int i = 0; i < size; i++) {
            count[i] = start[i] >= 0 ? count[start[i]] : 0;
        }
        final UnitLayout layout = new UnitLayout(cars, start, count);
        UNIT_LAYOUTS.put(vehicle.getId(), layout);
        return layout;
    }

    private static boolean rte$isHeadCar(String vehicleTypeId) {
        return vehicleTypeId != null && (vehicleTypeId.endsWith("_cab_1") || vehicleTypeId.endsWith("_cab_3"));
    }

    private static boolean rte$isTailCar(String vehicleTypeId) {
        return vehicleTypeId != null && (vehicleTypeId.endsWith("_cab_2") || vehicleTypeId.endsWith("_cab_3"));
    }

    public static Matrix4f createBasePose(VehicleExtension vehicle, int carIndex, PositionAndRotation positionAndRotation) {
        final Matrix4f pose = new Matrix4f();
        pose.translate((float) positionAndRotation.position.x, (float) positionAndRotation.position.y, (float) positionAndRotation.position.z);
        pose.rotateY((float) (Math.PI + positionAndRotation.yaw));
        final boolean hasPitch = positionAndRotation.pitch < 0 ? vehicle.getTransportMode().hasPitchAscending : vehicle.getTransportMode().hasPitchDescending;
        pose.rotateX(hasPitch ? (float) positionAndRotation.pitch : 0);
        final float roll = TrainExtraSupplier.getRollAngleAt(vehicle, carIndex);
        final boolean reversed = ((TrainExtraSupplier) vehicle).getVehicleReversed();
        pose.translate(0, -1, 0);
        pose.rotateZ(reversed ? -roll : roll);
        pose.translate(0, 2, 0);
        return pose;
    }

    public static Matrix4f renderPoseFor(VehicleExtension vehicle, int carIndex,
                                        PositionAndRotation positionAndRotation,
                                        cn.jstxjf_.rte.render.train.RenderVehicleCarFrameState frameState) {
        final boolean offsetMode = frameState != null && frameState.offsetVector != null
            && frameState.ridingCarPositionAndRotation != null;
        if (!offsetMode) {
            final Matrix4f basePose = createViewPose();
            basePose.multiply(createBasePose(vehicle, carIndex, positionAndRotation));
            return basePose;
        }
        final PositionAndRotation renderPositionAndRotation = org.mtr.mod.render.RenderVehicles.getRenderPositionAndRotation(
            frameState.offsetVector, frameState.offsetRotation, frameState.ridingCarPositionAndRotation,
            positionAndRotation,
            frameState.cameraShakeOffset != null ? frameState.cameraShakeOffset : Vector3d.getZeroMapped());
        final Vector3d mtrOffset = RenderUtil.mtrRenderOffset;
        final Matrix4f basePose = new Matrix4f();
        if (mtrOffset != null) {
            final net.minecraft.world.phys.Vec3 camera =
                net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            basePose.translate(
                (float) (mtrOffset.getXMapped() - camera.x),
                (float) (mtrOffset.getYMapped() - camera.y),
                (float) (mtrOffset.getZMapped() - camera.z));
        }
        basePose.multiply(createBasePose(vehicle, carIndex, renderPositionAndRotation));
        return basePose;
    }

    public static Matrix4f createViewPose() {
        final net.minecraft.world.phys.Vec3 camera = net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        return Matrix4f.translation(-(float) camera.x, -(float) camera.y, -(float) camera.z);
    }

    public static PositionAndRotation getCarPositionAndRotation(VehicleExtension vehicle, int carIndex) {
        try {
            final ObjectObjectImmutablePair<VehicleCar, ObjectArrayList<ObjectObjectImmutablePair<Vector, Vector>>> pair =
                vehicle.getVehicleCarsAndPositions().get(carIndex);
            final ObjectArrayList<PositionAndRotation> bogiePositions = new ObjectArrayList<>();
            pair.right().forEach(bogiePositionPair -> bogiePositions.add(new PositionAndRotation(bogiePositionPair.left(), bogiePositionPair.right(), true)));
            return new PositionAndRotation(bogiePositions, pair.left(), vehicle.getTransportMode().hasPitchAscending || vehicle.getTransportMode().hasPitchDescending);
        } catch (Exception ex) {
            return null;
        }
    }

    private static long getStateKey(long vehicleId, int carIndex) {
        return (vehicleId << 8) ^ carIndex;
    }

    public static boolean[] doorSidesOf(VehicleExtension vehicle, int carIndex, PositionAndRotation positionAndRotation) {
        try {
            final float doorValue = (float) vehicle.persistentVehicleData.getDoorValue();
            final DoorSideState state = resolveDoorSides(vehicle, carIndex, positionAndRotation, doorValue);
            return new boolean[]{state.leftOpen && doorValue > 0, state.rightOpen && doorValue > 0};
        } catch (Throwable e) {
            return new boolean[]{false, false};
        }
    }

    private static DoorSideState resolveDoorSides(VehicleExtension vehicle, int carIndex,
                                                   PositionAndRotation positionAndRotation,
                                                   float doorValue) {
        final long stateKey = getStateKey(vehicle.getId(), carIndex);
        if (doorValue <= 0) {
            DOOR_SIDE_STATES.remove(stateKey);
            return DoorSideState.CLOSED;
        }

        final long now = System.nanoTime();
        final DoorSideState state = DOOR_SIDE_STATES.computeIfAbsent(stateKey, ignored -> new DoorSideState());
        if (now < state.nextScanNanos) {
            return state;
        }

        try {
            final double halfLength = Math.max(1, rte$doorScanSpacing(vehicle, carIndex) / 2);
            final double yaw = positionAndRotation.yaw;
            final double pitch = positionAndRotation.pitch;
            final double horizontal = Math.cos(pitch) * halfLength;
            final double deltaX = Math.sin(yaw) * horizontal;
            final double deltaY = Math.sin(pitch) * halfLength;
            final double deltaZ = Math.cos(yaw) * horizontal;
            final Vector center = positionAndRotation.position;
            final boolean[] openSides = MinecraftClientUtil.canOpenDoorsAt(
                new Vector3f((float) (center.x - deltaX), (float) (center.y - deltaY), (float) (center.z - deltaZ)),
                new Vector3f((float) (center.x + deltaX), (float) (center.y + deltaY), (float) (center.z + deltaZ))
            );
            state.leftOpen = openSides[0];
            state.rightOpen = openSides[1];
            rte$traceDoorSides(vehicle, carIndex, positionAndRotation, doorValue, state);
        } catch (Throwable ignored) {
            state.leftOpen = false;
            state.rightOpen = false;
        }
        state.nextScanNanos = state.leftOpen || state.rightOpen ? Long.MAX_VALUE : now + 250_000_000L;
        return state;
    }

    public static boolean legacyDoorsOpen(VehicleExtension vehicle, int carIndex) {
        if (vehicle == null || vehicle.persistentVehicleData.getDoorValue() <= 0) {
            return false;
        }
        try {
            final String vehicleId = vehicle.vehicleExtraData.immutableVehicleCars.get(carIndex).getVehicleId();
            if (!cn.jstxjf_.rte.data.GangwayPolicy.isLegacyFormat(vehicleId)) {
                return false;
            }
            final PositionAndRotation position = getCarPositionAndRotation(vehicle, carIndex);
            if (position == null) {
                return false;
            }
            final boolean[] sides = doorSidesOf(vehicle, carIndex, position);
            return sides[0] || sides[1];
        } catch (Throwable e) {
            return false;
        }
    }

    private static double rte$doorScanSpacing(VehicleExtension vehicle, int carIndex) {
        final java.util.List<VehicleCar> cars = vehicle.vehicleExtraData.immutableVehicleCars;
        if (cars.isEmpty()) {
            return 2;
        }
        final int current = Math.max(0, Math.min(carIndex, cars.size() - 1));
        if (cars.size() == 1 || cn.jstxjf_.rte.ClientConfig.closeCouplingGap) {
            return cars.get(current).getLength();
        }
        final int first = current + 1 < cars.size() ? current : current - 1;
        final VehicleCar a = cars.get(first);
        final VehicleCar b = cars.get(first + 1);
        try {
            final cn.jstxjf_.rte.mixin.VehicleCarAccessor aa =
                (cn.jstxjf_.rte.mixin.VehicleCarAccessor) (Object) a;
            final cn.jstxjf_.rte.mixin.VehicleCarAccessor bb =
                (cn.jstxjf_.rte.mixin.VehicleCarAccessor) (Object) b;
            return a.getLength() / 2 + b.getLength() / 2
                + cn.jstxjf_.rte.data.CouplingPaddingPolicy.getEffectivePadding(
                    a, aa.rte$getCouplingPadding2())
                + cn.jstxjf_.rte.data.CouplingPaddingPolicy.getEffectivePadding(
                    b, bb.rte$getCouplingPadding1());
        } catch (Throwable ignored) {
            return cars.get(current).getLength();
        }
    }

    private static final java.util.Map<Long, Long> RTE_DOOR_TRACE_NEXT = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Set<String> rte$tracedSpacing = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private static final java.util.Map<Long, String> rte$lastCarPos = new java.util.concurrent.ConcurrentHashMap<>();

    private static void rte$traceCarPosition(VehicleExtension vehicle, int carIndex, PositionAndRotation par) {
        if (!ClientConfig.traceDoorSides) {
            return;
        }
        try {
            final long key = vehicle.getId();
            final String id = vehicle.vehicleExtraData.immutableVehicleCars.get(carIndex).getVehicleId();
            if (!id.contains("crt333")) {
                return;
            }
            final String line = String.format(java.util.Locale.ROOT,
                "car%d pos=(%.2f,%.2f,%.2f) yaw=%.3f", carIndex,
                par.position.x, par.position.y, par.position.z, par.yaw);
            final String prev = rte$lastCarPos.get(key * 100 + carIndex);
            if (line.equals(prev)) {
                return;
            }
            rte$lastCarPos.put(key * 100 + carIndex, line);
            cn.jstxjf_.rte.RTE.LOGGER.info("[CarPos] {} {}", id, line);
        } catch (Throwable ignored) {
        }
    }

    private static void rte$traceSpacing(VehicleExtension vehicle) {
        try {
            final java.util.List<VehicleCar> cars = vehicle.vehicleExtraData.immutableVehicleCars;
            if (cars.isEmpty() || !rte$tracedSpacing.add(cars.get(0).getVehicleId())) {
                return;
            }
            final StringBuilder sb = new StringBuilder();
            double total = 0;
            for (int i = 0; i < cars.size(); i++) {
                final VehicleCar car = cars.get(i);
                final double pad1 = ((cn.jstxjf_.rte.mixin.VehicleCarAccessor) (Object) car).rte$getCouplingPadding1();
                final double pad2 = ((cn.jstxjf_.rte.mixin.VehicleCarAccessor) (Object) car).rte$getCouplingPadding2();
                total += car.getLength() + (i == 0 ? 0 : pad1) + pad2;
                sb.append(String.format("[%d] id=%s len=%.2f pad1=%.2f pad2=%.2f%n",
                        i, car.getVehicleId(), car.getLength(), pad1, pad2));
            }
            cn.jstxjf_.rte.RTE.LOGGER.info("[Spacing] cars={} totalLength={}\n{}",
                    cars.size(), String.format("%.2f", total), sb);
        } catch (Throwable ignored) {
        }
    }

    private static void rte$traceDoorSides(VehicleExtension vehicle, int carIndex,
                                           PositionAndRotation positionAndRotation,
                                           float doorValue, DoorSideState state) {
        if (!cn.jstxjf_.rte.ClientConfig.traceDoorSides) return;
        final long now = System.currentTimeMillis();
        final long traceKey = getStateKey(vehicle.getId(), carIndex);
        final Long next = RTE_DOOR_TRACE_NEXT.get(traceKey);
        if (next != null && now < next) return;
        RTE_DOOR_TRACE_NEXT.put(traceKey, now + 1000);
        try {
            final int multiplier = vehicle.persistentVehicleData
                    .getAdjustedDoorMultiplier(vehicle.vehicleExtraData);
            cn.jstxjf_.rte.RTE.LOGGER.info(
                    "[DoorSide] vehicle={} type={} car={} doorValue={} scanLeft={} scanRight={} mtrMultiplier={} yaw={}",
                    vehicle.getId(), vehicle.vehicleExtraData.immutableVehicleCars.get(carIndex).getVehicleId(),
                    carIndex, doorValue, state.leftOpen, state.rightOpen,
                    multiplier, (float) positionAndRotation.yaw);
        } catch (Throwable ignored) {
        }
    }

    private static final class CarFrameState {
        private int renderPass;
    }

    private static final class DoorSideState {
        private static final DoorSideState CLOSED = new DoorSideState();
        private boolean leftOpen;
        private boolean rightOpen;
        private long nextScanNanos;
    }

    private static final class ConnectionFrameState {
        private final VehicleExtension vehicle;
        private final int carIndex;

        private ConnectionFrameState(VehicleExtension vehicle, int carIndex) {
            this.vehicle = vehicle;
            this.carIndex = carIndex;
        }
    }
}
