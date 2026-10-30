package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.mixin.VehicleRidingMovementAccessor;
import cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.mtr.core.data.VehicleCar;
import org.mtr.core.tool.Vector;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.PositionAndRotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class TrainCollisionManager {

    public static final double MIN_HEIGHT = 1;
    public static final double MAX_HEIGHT = 8;
    public static final int MAX_LAYOUT_BOXES = 256;
    private static final double STEP = 0.5;
    private static final double WALL = 0.25;
    private static final double ROOF = 0.25;
    private static final double STAND_TOLERANCE = 0.15;
    private static final double STAND_MARGIN = 0.3;
    private static final double MAX_CARRY_DISTANCE = 8;
    private static final int PRUNE_INTERVAL_TICKS = 100;

    private static final Map<Long, Map<Integer, CarOverride>> OVERRIDES = new ConcurrentHashMap<>();

    private static long carriedVehicleId;
    private static int carriedCar = -1;
    private static PositionAndRotation carriedPose;
    private static int ticksSincePrune;

    private TrainCollisionManager() {
    }

    public record LocalBox(double x, double y, double z, double width, double height, double length) {

        private boolean containsFootprint(double localX, double localZ, double margin) {
            return Math.abs(localX - x) <= width / 2 + margin && Math.abs(localZ - z) <= length / 2 + margin;
        }

        private double top() {
            return y + height;
        }
    }

    private static final class CarOverride {
        private volatile Double height;
        private volatile List<LocalBox> layout;
        private volatile boolean disabled;

        private boolean isEmpty() {
            return height == null && layout == null && !disabled;
        }
    }

    private record CarPose(VehicleExtension vehicle, int car, VehicleCar data, PositionAndRotation pose) {
    }

    public static List<VoxelShape> getClientShapes(Entity entity, AABB query) {
        if (!ClientConfig.trainCollision || !(entity instanceof LocalPlayer) || isRiding()) {
            return Collections.emptyList();
        }
        try {
            final List<VoxelShape> shapes = new ArrayList<>();
            for (final CarPose carPose : carsNear(query)) {
                final double[] range = localRange(carPose.pose, query);
                for (final LocalBox box : boxes(carPose)) {
                    addSampledBox(shapes, carPose.pose, box, range);
                }
            }
            return shapes;
        } catch (Throwable ignored) {
            return Collections.emptyList();
        }
    }

    public static void clientTick() {
        if (++ticksSincePrune >= PRUNE_INTERVAL_TICKS) {
            ticksSincePrune = 0;
            prune();
        }
        final LocalPlayer player = Minecraft.getInstance().player;
        if (!ClientConfig.trainCollision || player == null || player.isSpectator() || player.noPhysics || isRiding()) {
            clearCarry();
            return;
        }
        try {
            carryPlayer(player);
            updateStanding(player);
        } catch (Throwable ignored) {
            clearCarry();
        }
    }

    public static void setLayout(long vehicleId, int car, List<LocalBox> layout) {
        update(vehicleId, car, override -> override.layout = layout == null || layout.isEmpty() ? null : List.copyOf(layout));
    }

    public static void setHeight(long vehicleId, int car, Double height) {
        update(vehicleId, car, override -> override.height = height == null ? null : clamp(height, MIN_HEIGHT, MAX_HEIGHT));
    }

    public static void setEnabled(long vehicleId, int car, boolean enabled) {
        update(vehicleId, car, override -> override.disabled = !enabled);
    }

    public static List<LocalBox> parseLayout(String value) {
        final List<LocalBox> boxes = new ArrayList<>();
        if (value == null || value.isBlank()) {
            return boxes;
        }
        for (final String entry : value.split(";")) {
            if (boxes.size() >= MAX_LAYOUT_BOXES) {
                break;
            }
            final String[] parts = entry.trim().split(",");
            if (parts.length != 6) {
                continue;
            }
            try {
                final double[] values = new double[6];
                for (int i = 0; i < 6; i++) {
                    values[i] = Double.parseDouble(parts[i].trim());
                    if (!Double.isFinite(values[i])) {
                        throw new NumberFormatException();
                    }
                }
                boxes.add(new LocalBox(
                    clamp(values[0], -32, 32), clamp(values[1], -8, 16), clamp(values[2], -64, 64),
                    clamp(values[3], 0.05, 32), clamp(values[4], 0.05, 16), clamp(values[5], 0.05, 128)));
            } catch (NumberFormatException ignored) {
            }
        }
        return boxes;
    }

    public static String formatBox(double x, double y, double z, double width, double height, double length) {
        return String.format(Locale.ROOT, "%.4f,%.4f,%.4f,%.4f,%.4f,%.4f", x, y, z, width, height, length);
    }

    private static void update(long vehicleId, int car, java.util.function.Consumer<CarOverride> change) {
        final Map<Integer, CarOverride> cars = OVERRIDES.computeIfAbsent(vehicleId, ignored -> new ConcurrentHashMap<>());
        final CarOverride override = cars.computeIfAbsent(car, ignored -> new CarOverride());
        change.accept(override);
        if (override.isEmpty()) {
            cars.remove(car);
            if (cars.isEmpty()) {
                OVERRIDES.remove(vehicleId);
            }
        }
    }

    private static void prune() {
        if (OVERRIDES.isEmpty()) {
            return;
        }
        final Set<Long> alive = new HashSet<>();
        for (final VehicleExtension vehicle : MinecraftClientData.getInstance().vehicles) {
            alive.add(vehicle.getId());
        }
        OVERRIDES.keySet().removeIf(id -> !alive.contains(id));
    }

    private static boolean isRiding() {
        try {
            return VehicleRidingMovementAccessor.rte$getRidingVehicleId() != 0;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void carryPlayer(LocalPlayer player) {
        if (carriedPose == null) {
            return;
        }
        final CarPose now = findCar(carriedVehicleId, carriedCar);
        if (now == null) {
            clearCarry();
            return;
        }
        final Vector local = toLocal(carriedPose, player.getX(), player.getY(), player.getZ());
        final Vector world = toWorld(now.pose, local);
        final double dx = world.x - player.getX();
        final double dy = world.y - player.getY();
        final double dz = world.z - player.getZ();
        if (dx * dx + dy * dy + dz * dz > MAX_CARRY_DISTANCE * MAX_CARRY_DISTANCE) {
            clearCarry();
            return;
        }
        player.setPos(world.x, world.y, world.z);
        final float yawChange = (float) -Math.toDegrees(wrapRadians(now.pose.yaw - carriedPose.yaw));
        player.setYRot(player.getYRot() + yawChange);
        player.setYHeadRot(player.getYHeadRot() + yawChange);
        player.yRotO += yawChange;
        player.yHeadRotO += yawChange;
    }

    private static void updateStanding(LocalPlayer player) {
        if (!player.onGround()) {
            clearCarry();
            return;
        }
        final AABB feet = player.getBoundingBox();
        final AABB query = new AABB(feet.minX, feet.minY - 1, feet.minZ, feet.maxX, feet.minY + 0.5, feet.maxZ);
        for (final CarPose carPose : carsNear(query)) {
            final Vector local = toLocal(carPose.pose, player.getX(), player.getY(), player.getZ());
            for (final LocalBox box : boxes(carPose)) {
                if (box.containsFootprint(local.x, local.z, STAND_MARGIN) && Math.abs(local.y - box.top()) <= STAND_TOLERANCE) {
                    carriedVehicleId = carPose.vehicle.getId();
                    carriedCar = carPose.car;
                    carriedPose = carPose.pose;
                    return;
                }
            }
        }
        clearCarry();
    }

    private static void clearCarry() {
        carriedVehicleId = 0;
        carriedCar = -1;
        carriedPose = null;
    }

    private static CarPose findCar(long vehicleId, int car) {
        for (final VehicleExtension vehicle : MinecraftClientData.getInstance().vehicles) {
            if (vehicle.getId() != vehicleId) {
                continue;
            }
            final List<VehicleCar> cars = carsOf(vehicle);
            if (car < 0 || car >= cars.size()) {
                return null;
            }
            final PositionAndRotation pose = ScriptedTrainRenderers.getCarPositionAndRotation(vehicle, car);
            return pose == null ? null : new CarPose(vehicle, car, cars.get(car), pose);
        }
        return null;
    }

    private static List<CarPose> carsNear(AABB query) {
        final List<CarPose> result = new ArrayList<>();
        final double centerX = (query.minX + query.maxX) / 2;
        final double centerZ = (query.minZ + query.maxZ) / 2;
        final double queryRadius = Math.hypot(query.maxX - query.minX, query.maxZ - query.minZ) / 2;
        for (final VehicleExtension vehicle : MinecraftClientData.getInstance().vehicles) {
            final Map<Integer, CarOverride> overrides = OVERRIDES.get(vehicle.getId());
            final List<VehicleCar> cars = carsOf(vehicle);
            for (int car = 0; car < cars.size(); car++) {
                final CarOverride override = overrides == null ? null : overrides.get(car);
                if (override != null && override.disabled) {
                    continue;
                }
                final VehicleCar data = cars.get(car);
                final PositionAndRotation pose = ScriptedTrainRenderers.getCarPositionAndRotation(vehicle, car);
                if (pose == null) {
                    continue;
                }
                final double reach = Math.hypot(data.getLength() / 2, data.getWidth() / 2) + queryRadius + 2;
                final double dx = pose.position.x - centerX;
                final double dz = pose.position.z - centerZ;
                if (dx * dx + dz * dz > reach * reach) {
                    continue;
                }
                if (query.maxY < pose.position.y - 2 || query.minY > pose.position.y + MAX_HEIGHT + 2) {
                    continue;
                }
                result.add(new CarPose(vehicle, car, data, pose));
            }
        }
        return result;
    }

    private static List<VehicleCar> carsOf(VehicleExtension vehicle) {
        final List<VehicleCar> cars = new ArrayList<>();
        try {
            vehicle.getVehicleCarsAndPositions().forEach(pair -> cars.add(pair.left()));
        } catch (Throwable ignored) {
        }
        return cars;
    }

    private static List<LocalBox> boxes(CarPose carPose) {
        final Map<Integer, CarOverride> overrides = OVERRIDES.get(carPose.vehicle.getId());
        final CarOverride override = overrides == null ? null : overrides.get(carPose.car);
        if (override != null && override.layout != null) {
            return override.layout;
        }
        final double height = override != null && override.height != null
            ? override.height
            : clamp(ClientConfig.trainCollisionHeight, MIN_HEIGHT, MAX_HEIGHT);
        final double width = Math.max(0.5, carPose.data.getWidth());
        final double length = Math.max(0.5, carPose.data.getLength());
        final double wallHeight = Math.max(0.05, height - ROOF);
        final List<LocalBox> boxes = new ArrayList<>(5);
        boxes.add(new LocalBox(0, height - ROOF, 0, width, ROOF, length));
        boxes.add(new LocalBox(0, 0, length / 2 - WALL / 2, width, wallHeight, WALL));
        boxes.add(new LocalBox(0, 0, -length / 2 + WALL / 2, width, wallHeight, WALL));
        if (!doorsOpen(carPose.vehicle)) {
            boxes.add(new LocalBox(width / 2 - WALL / 2, 0, 0, WALL, wallHeight, length));
            boxes.add(new LocalBox(-width / 2 + WALL / 2, 0, 0, WALL, wallHeight, length));
        }
        return boxes;
    }

    private static boolean doorsOpen(VehicleExtension vehicle) {
        try {
            return vehicle.persistentVehicleData.getDoorValue() > 0;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static double[] localRange(PositionAndRotation pose, AABB query) {
        final double[] range = {
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
        };
        for (int corner = 0; corner < 8; corner++) {
            final Vector local = toLocal(pose,
                (corner & 1) == 0 ? query.minX : query.maxX,
                (corner & 2) == 0 ? query.minY : query.maxY,
                (corner & 4) == 0 ? query.minZ : query.maxZ);
            range[0] = Math.min(range[0], local.x);
            range[1] = Math.max(range[1], local.x);
            range[2] = Math.min(range[2], local.y);
            range[3] = Math.max(range[3], local.y);
            range[4] = Math.min(range[4], local.z);
            range[5] = Math.max(range[5], local.z);
        }
        return range;
    }

    private static void addSampledBox(List<VoxelShape> shapes, PositionAndRotation pose, LocalBox box, double[] range) {
        if (box.y > range[3] + STEP || box.top() < range[2] - STEP) {
            return;
        }
        final int countX = Math.max(1, (int) Math.ceil(box.width / STEP));
        final int countZ = Math.max(1, (int) Math.ceil(box.length / STEP));
        final double cellX = box.width / countX;
        final double cellZ = box.length / countZ;
        final double spread = Math.abs(Math.cos(pose.yaw)) + Math.abs(Math.sin(pose.yaw));
        final double halfHorizontal = Math.max(cellX, cellZ) / 2 * spread;
        final double pitchPad = cellZ / 2 * Math.abs(Math.sin(pose.pitch));
        for (int i = 0; i < countX; i++) {
            final double localX = box.x - box.width / 2 + (i + 0.5) * cellX;
            if (localX < range[0] - STEP || localX > range[1] + STEP) {
                continue;
            }
            for (int k = 0; k < countZ; k++) {
                final double localZ = box.z - box.length / 2 + (k + 0.5) * cellZ;
                if (localZ < range[4] - STEP || localZ > range[5] + STEP) {
                    continue;
                }
                final Vector bottom = toWorld(pose, new Vector(localX, box.y, localZ));
                final Vector top = toWorld(pose, new Vector(localX, box.top(), localZ));
                final double centerX = (bottom.x + top.x) / 2;
                final double centerZ = (bottom.z + top.z) / 2;
                shapes.add(Shapes.create(
                    centerX - halfHorizontal, Math.min(bottom.y, top.y) - pitchPad, centerZ - halfHorizontal,
                    centerX + halfHorizontal, Math.max(bottom.y, top.y) + pitchPad, centerZ + halfHorizontal));
            }
        }
    }

    private static Vector toLocal(PositionAndRotation pose, double x, double y, double z) {
        return pose.transformBackwards(new Vector(x, y, z), Vector::rotateX, Vector::rotateY, Vector::add);
    }

    private static Vector toWorld(PositionAndRotation pose, Vector local) {
        return pose.transformForwards(local, Vector::rotateX, Vector::rotateY, Vector::add);
    }

    private static double wrapRadians(double value) {
        double result = value % (Math.PI * 2);
        if (result > Math.PI) {
            result -= Math.PI * 2;
        } else if (result < -Math.PI) {
            result += Math.PI * 2;
        }
        return result;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
