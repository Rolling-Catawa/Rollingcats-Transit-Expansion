package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.data.LegacyVehicleRegistry;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.loader.ObjModelLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.mtr.mod.data.VehicleExtension;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LegacyVehicleRenderer {

    private static final Map<String, Map<String, RawModel>> GROUP_CACHE = new ConcurrentHashMap<>();
    public static final class DoorPiece {

        final ModelCluster cluster;
        final String doorOffset;
        final boolean mirror;

        DoorPiece(ModelCluster cluster, String doorOffset, boolean mirror) {
            this.cluster = cluster;
            this.doorOffset = doorOffset;
            this.mirror = mirror;
        }
    }

    public static final class ConditionalCluster {

        final ModelCluster cluster;
        final String condition;
        final boolean skipIfTooFar;

        ConditionalCluster(ModelCluster cluster, String condition, boolean skipIfTooFar) {
            this.cluster = cluster;
            this.condition = condition;
            this.skipIfTooFar = skipIfTooFar;
        }
    }

    public static final class CarModels {

        final java.util.List<ConditionalCluster> clusters;
        final java.util.List<DoorPiece> doors;

        CarModels(java.util.List<ConditionalCluster> clusters, java.util.List<DoorPiece> doors) {
            this.clusters = clusters;
            this.doors = doors;
        }

        void close() {
            clusters.forEach(entry -> entry.cluster.close());
            doors.forEach(piece -> piece.cluster.close());
        }
    }

    private static final double FAR_DISTANCE_SQUARED = 64 * 64;

    static boolean conditionSkips(String condition, boolean leftOpen, boolean rightOpen, boolean movingForwards) {
        switch (condition) {
            case "DOORS_OPEN":
                return !leftOpen && !rightOpen;
            case "DOORS_CLOSED":
                return leftOpen || rightOpen;
            case "DOOR_LEFT_OPEN":
                return !leftOpen;
            case "DOOR_RIGHT_OPEN":
                return !rightOpen;
            case "DOOR_LEFT_CLOSED":
                return leftOpen;
            case "DOOR_RIGHT_CLOSED":
                return rightOpen;
            case "MOVING_FORWARDS":
                return !movingForwards;
            case "MOVING_BACKWARDS":
                return movingForwards;
            default:
                return false;
        }
    }

    private static final Map<String, CarModels> CAR_CACHE = new ConcurrentHashMap<>();
    private static final java.util.Set<String> FAILED = ConcurrentHashMap.newKeySet();
    private static final java.util.Set<String> ENTRY_TRACE = ConcurrentHashMap.newKeySet();
    private static int reported = 0;
    private static final Map<Long, Long> LAST_FRAME = new ConcurrentHashMap<>();
    private static volatile long frameId = 0;

    public static void beginFrame() {
        frameId++;
    }

    private LegacyVehicleRenderer() {
    }

    public static void reset() {
        CAR_CACHE.values().forEach(CarModels::close);
        CAR_CACHE.clear();
        GROUP_CACHE.clear();
        FAILED.clear();
        reported = 0;
    }

    public static boolean willTakeOver(VehicleExtension vehicle, int carIndex) {
        if (!ClientConfig.legacyVehicleTakeover || vehicle == null || vehicle.vehicleExtraData == null) {
            return false;
        }
        final int carCount = vehicle.vehicleExtraData.immutableVehicleCars.size();
        if (carIndex < 0 || carIndex >= carCount) {
            return false;
        }
        final LegacyVehicleRegistry.Definition definition = LegacyVehicleRegistry.get(
            vehicle.vehicleExtraData.immutableVehicleCars.get(carIndex).getVehicleId());
        return definition != null && !FAILED.contains(definition.trainId + '|' + carIndex + '|' + carCount);
    }

    public static boolean renderIfLegacy(VehicleExtension vehicle, int carIndex, int light,
                                        RenderVehicleCarFrameState frameState) {
        if (!ClientConfig.legacyVehicleTakeover || vehicle == null || vehicle.vehicleExtraData == null) {
            return false;
        }
        final int carCount = vehicle.vehicleExtraData.immutableVehicleCars.size();
        if (carIndex < 0 || carIndex >= carCount) {
            return false;
        }
        final String vehicleId = vehicle.vehicleExtraData.immutableVehicleCars.get(carIndex).getVehicleId();
        final LegacyVehicleRegistry.Definition definition = LegacyVehicleRegistry.get(vehicleId);
        if (ENTRY_TRACE.add(vehicleId)) {
            RTE.LOGGER.info("[LegacyVehicle] entry vehicleId={} matched={}", vehicleId, definition != null);
        }
        if (definition == null) {
            return false;
        }
        final String key = definition.trainId + '|' + carIndex + '|' + carCount;
        if (FAILED.contains(key)) {
            return false;
        }
        CarModels models = CAR_CACHE.get(key);
        if (models == null) {
            models = buildProtected(definition, carIndex, carCount, key);
            if (models == null) {
                return false;
            }
            final CarModels existing = CAR_CACHE.putIfAbsent(key, models);
            if (existing != null) {
                models.close();
                models = existing;
            }
        }

        final long guardKey = vehicle.getId() * 64L + carIndex;
        final Long seen = LAST_FRAME.get(guardKey);
        if (seen != null && seen == frameId) {
            return true;
        }
        final org.mtr.mod.render.PositionAndRotation positionAndRotation =
            frameState != null && frameState.absolutePositionAndRotation != null
                ? frameState.absolutePositionAndRotation
                : BuiltinTrainRenderers.getCarPositionAndRotation(vehicle, carIndex);
        if (positionAndRotation == null) {
            return false;
        }
        final Matrix4f basePose = cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers
            .renderPoseFor(vehicle, carIndex, positionAndRotation, frameState);
        final boolean[] doorSides = doorSidesOf(vehicle, carIndex, positionAndRotation);
        final boolean movingForwards = !isCarReversed(vehicle);
        final double distanceSquared = cameraDistanceSquared(positionAndRotation);
        final boolean tooFar = distanceSquared > FAR_DISTANCE_SQUARED;
        for (final ConditionalCluster entry : models.clusters) {
            if (entry.skipIfTooFar && tooFar) {
                continue;
            }
            if (conditionSkips(entry.condition, doorSides[0], doorSides[1], movingForwards)) {
                continue;
            }
            RTEClient.drawScheduler.enqueue(entry.cluster, basePose.copy(), light);
        }
        if (!models.doors.isEmpty()) {
            final org.mtr.mod.resource.DoorAnimationType animationType = doorAnimationTypeOf(definition);
            final double doorValue = vehicle.persistentVehicleData.getDoorValue();
            for (final DoorPiece piece : models.doors) {
                final Matrix4f doorPose = basePose.copy();
                try {
                    final boolean leftSide = piece.doorOffset.startsWith("LEFT");
                    final double zSign = piece.doorOffset.endsWith("NEGATIVE") ? -1 : 1;
                    final double dx = animationType.getDoorAnimationX(doorValue, piece.mirror, leftSide ? -1 : 1);
                    final double dz = animationType.getDoorAnimationZ(doorValue, piece.mirror, zSign, false);
                    final double sign = piece.mirror ? -1 : 1;
                    doorPose.translate((float) (dx * sign), 0, (float) (dz * sign));
                } catch (Throwable ignored) {
                }
                RTEClient.drawScheduler.enqueue(piece.cluster, doorPose, light);
            }
        }
        LAST_FRAME.put(guardKey, frameId);
        rte$reportDrawn(models, basePose);
        return true;
    }

    private static int drawnReports = 0;

    private static void rte$reportDrawn(CarModels models, Matrix4f pose) {
        if (drawnReports >= 3) {
            return;
        }
        drawnReports++;
        int staticMeshes = 0;
        for (final ConditionalCluster entry : models.clusters) {
            if (entry.cluster.uploadedOpaqueParts != null) {
                staticMeshes += entry.cluster.uploadedOpaqueParts.meshList.size();
            }
        }
        int doorMeshes = 0;
        for (final DoorPiece piece : models.doors) {
            if (piece.cluster.uploadedOpaqueParts != null) {
                doorMeshes += piece.cluster.uploadedOpaqueParts.meshList.size();
            }
        }
        final cn.jstxjf_.rte.gfx.math.Vector3f t = pose.getTranslationPart();
        RTE.LOGGER.info("[LegacyVehicle] enqueued staticVao={} doorVao={} poseTranslation=({}, {}, {})",
            staticMeshes, doorMeshes,
            String.format(java.util.Locale.ROOT, "%.2f", t.x()),
            String.format(java.util.Locale.ROOT, "%.2f", t.y()),
            String.format(java.util.Locale.ROOT, "%.2f", t.z()));
    }

    private static org.mtr.mod.resource.DoorAnimationType doorAnimationTypeOf(LegacyVehicleRegistry.Definition definition) {
        try {
            return org.mtr.mod.resource.DoorAnimationType.valueOf(definition.doorAnimationType);
        } catch (Throwable e) {
            return org.mtr.mod.resource.DoorAnimationType.STANDARD;
        }
    }

    private static CarModels buildProtected(LegacyVehicleRegistry.Definition definition, int carIndex, int carCount, String key) {
        final boolean needCapture = !cn.jstxjf_.rte.gfx.util.GlStateTracker.isStateProtected;
        if (needCapture) {
            cn.jstxjf_.rte.gfx.util.GlStateTracker.capture();
        }
        try {
            return build(definition, carIndex, carCount, key);
        } finally {
            if (needCapture) {
                cn.jstxjf_.rte.gfx.util.GlStateTracker.restore();
            }
        }
    }

    private static CarModels build(LegacyVehicleRegistry.Definition definition, int carIndex, int carCount, String key) {
        try {
            final Map<String, RawModel> groups = GROUP_CACHE.computeIfAbsent(definition.modelLocation, location -> {
                try {
                    return ObjModelLoader.loadModels(Minecraft.getInstance().getResourceManager(),
                        cn.jstxjf_.rte.data.ScriptedCustomTrains.parseLegacyLocation(location), RTEClient.atlasManager);
                } catch (Exception e) {
                    RTE.LOGGER.warn("[LegacyVehicle] cannot load {}: {}", location, e.toString());
                    return java.util.Collections.emptyMap();
                }
            });
            if (groups.isEmpty()) {
                FAILED.add(key);
                return null;
            }
            final java.util.Map<String, RawModel> byCondition = new java.util.LinkedHashMap<>();
            final java.util.List<DoorPiece> doors = new java.util.ArrayList<>();
            int used = 0;
            for (final LegacyVehicleRegistry.Part part : definition.parts) {
                if (skip(part, carIndex, carCount)) {
                    continue;
                }
                final boolean isDoor = !"NONE".equals(part.doorOffset)
                    || part.doorXMultiplier != 0 || part.doorZMultiplier != 0;
                final RawModel doorModel = isDoor ? new RawModel() : null;
                final String groupKey = part.renderCondition + '|' + part.skipIfTooFar;
                final RawModel target = isDoor ? doorModel
                    : byCondition.computeIfAbsent(groupKey, ignored -> new RawModel());
                for (final String name : part.names) {
                    final RawModel source = groups.get(name);
                    if (source == null) {
                        continue;
                    }
                    for (final float[] position : part.positions) {
                        final RawModel piece = source.copy();
                        if (definition.flipV) {
                            piece.applyUVMirror(false, true);
                        }
                        if (part.mirror) {
                            piece.applyRotation(new cn.jstxjf_.rte.gfx.math.Vector3f(0, 1, 0), 180f);
                            piece.applyTranslation(-position[0] / 16f, 0, position[1] / 16f);
                        } else {
                            piece.applyTranslation(position[0] / 16f, 0, position[1] / 16f);
                        }
                        piece.setAllRenderType(renderTypeOf(part.renderStage));
                        for (final RawMesh mesh : piece.meshList.values()) {
                            target.append(mesh);
                        }
                        used++;
                    }
                }
                if (isDoor && !doorModel.meshList.isEmpty()) {
                    applyTexture(definition, doorModel);
                    doors.add(new DoorPiece(
                        new ModelCluster(doorModel, cn.jstxjf_.rte.gfxext.reuse.ModelManager.DEFAULT_MAPPING, RTEClient.modelManager),
                        part.doorOffset, part.mirror));
                }
            }
            if (used == 0) {
                FAILED.add(key);
                return null;
            }
            final java.util.List<ConditionalCluster> clusters = new java.util.ArrayList<>();
            for (final java.util.Map.Entry<String, RawModel> entry : byCondition.entrySet()) {
                final RawModel model = entry.getValue();
                if (model.meshList.isEmpty()) {
                    continue;
                }
                applyTexture(definition, model);
                final String[] parts = entry.getKey().split("\\|");
                clusters.add(new ConditionalCluster(
                    new ModelCluster(model, cn.jstxjf_.rte.gfxext.reuse.ModelManager.DEFAULT_MAPPING, RTEClient.modelManager),
                    parts[0], parts.length > 1 && Boolean.parseBoolean(parts[1])));
            }
            if (reported < 6) {
                reported++;
                final StringBuilder mirrored = new StringBuilder();
            for (final LegacyVehicleRegistry.Part part : definition.parts) {
                if (part.mirror && !skip(part, carIndex, carCount)) {
                    mirrored.append(part.names.isEmpty() ? "?" : part.names.get(0)).append(' ');
                }
            }
            RTE.LOGGER.info("[LegacyVehicle] built {} car {}/{}: {} part instance(s), {} condition group(s), {} door piece(s)",
                    definition.trainId, carIndex, carCount, used, byCondition.size(), doors.size());
            }
            return new CarModels(clusters, doors);
        } catch (Exception e) {
            RTE.LOGGER.warn("[LegacyVehicle] build failed for {}: {}", key, e.toString());
            FAILED.add(key);
            return null;
        }
    }

    private static double cameraDistanceSquared(org.mtr.mod.render.PositionAndRotation positionAndRotation) {
        try {
            final net.minecraft.world.phys.Vec3 camera =
                net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            final double dx = positionAndRotation.position.x() - camera.x;
            final double dy = positionAndRotation.position.y() - camera.y;
            final double dz = positionAndRotation.position.z() - camera.z;
            return dx * dx + dy * dy + dz * dz;
        } catch (Throwable e) {
            return 0;
        }
    }

    private static boolean[] doorSidesOf(VehicleExtension vehicle, int carIndex,
                                         org.mtr.mod.render.PositionAndRotation positionAndRotation) {
        return cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers
            .doorSidesOf(vehicle, carIndex, positionAndRotation);
    }

    private static boolean isCarReversed(VehicleExtension vehicle) {
        try {
            return ((cn.jstxjf_.rte.data.TrainExtraSupplier) vehicle).getVehicleReversed();
        } catch (Throwable e) {
            return false;
        }
    }

    private static int boundsReports = 0;

    private static void rte$reportBounds(LegacyVehicleRegistry.Definition definition, int carIndex, int carCount, RawModel model) {
        if (boundsReports >= 6) {
            return;
        }
        boundsReports++;
        float lo = Float.MAX_VALUE;
        float hi = -Float.MAX_VALUE;
        int verts = 0;
        for (final RawMesh mesh : model.meshList.values()) {
            for (final cn.jstxjf_.rte.gfxext.model.Vertex vertex : mesh.vertices) {
                final float z = vertex.position.z();
                if (z < lo) {
                    lo = z;
                }
                if (z > hi) {
                    hi = z;
                }
                verts++;
            }
        }
        if (verts == 0) {
            return;
        }
        RTE.LOGGER.info("[LegacyVehicle] bounds {} car {}/{} Z {} ~ {} (center {}, length {})",
            definition.trainId, carIndex, carCount,
            String.format(java.util.Locale.ROOT, "%.2f", lo),
            String.format(java.util.Locale.ROOT, "%.2f", hi),
            String.format(java.util.Locale.ROOT, "%.2f", (lo + hi) / 2),
            String.format(java.util.Locale.ROOT, "%.1f", definition.length));
    }

    private static void applyTexture(LegacyVehicleRegistry.Definition definition, RawModel model) {
        if (definition.textureId == null || definition.textureId.isEmpty()) {
            return;
        }
        model.replaceTexture("default.png",
            cn.jstxjf_.rte.data.ScriptedCustomTrains.parseLegacyLocation(
                definition.textureId.endsWith(".png") ? definition.textureId : definition.textureId + ".png"));
    }

    private static String renderTypeOf(String stage) {
        switch (stage.toUpperCase(Locale.ROOT)) {
            case "INTERIOR":
                return "interior";
            case "LIGHTS":
            case "LIGHT":
                return "light";
            case "INTERIOR_TRANSLUCENT":
                return "interiortranslucent";
            case "EXTERIOR_TRANSLUCENT":
                return "exteriortranslucent";
            default:
                return "exterior";
        }
    }

    static boolean skip(LegacyVehicleRegistry.Part part, int carIndex, int carCount) {
        return matches(part.blacklistedCars, carIndex, carCount) > matches(part.whitelistedCars, carIndex, carCount);
    }

    private static int matches(String filters, int currentCar, int trainCars) {
        final String[] split = filters.split(",");
        int strength = 0;
        for (final String filter : split) {
            if (filter.isEmpty()) {
                continue;
            }
            if (filter.contains("%")) {
                try {
                    final String[] parts = filter.split("\\+");
                    final int multiple = Integer.parseInt(parts[0].replace("%", ""));
                    final int additional = parts.length == 1 ? 0 : Integer.parseInt(parts[1]);
                    if (multiple != 0 && (currentCar + 1 + additional) % multiple == 0) {
                        strength = 2;
                    }
                } catch (Exception ignored) {
                }
            } else {
                try {
                    final int car = Integer.parseInt(filter.trim());
                    if (car == currentCar + 1 || car == currentCar - trainCars) {
                        return 3;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return strength;
    }
}
