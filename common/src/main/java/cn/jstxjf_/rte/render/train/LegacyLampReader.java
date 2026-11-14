package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.GangwayPolicy;
import cn.jstxjf_.rte.data.LegacyVehicleRegistry;
import cn.jstxjf_.rte.data.VehicleLightRegistry.Light;
import cn.jstxjf_.rte.data.VehicleLightRegistry.Type;
import cn.jstxjf_.rte.gfxext.util.ResourceUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class LegacyLampReader {

    private static final float CELL = 0.2F;
    private static final float MAX_LAMP_SIZE = 0.9F;
    private static final float OBJ_TO_LAMP_Y = 1;

    private record LampPart(boolean forwards, String file, String group, List<float[]> positions, boolean mirror,
                            String whitelistedCars, String blacklistedCars) {
    }

    private record Lamp(boolean headWhenFront, float x, float y, float z, float size, String whitelistedCars, String blacklistedCars) {
    }

    private static final Map<String, List<Lamp>> LAMPS = new ConcurrentHashMap<>();
    private static final Map<String, List<Light>> CAR_CACHE = new ConcurrentHashMap<>();
    private static int generation;

    private LegacyLampReader() {
    }

    public static void reset() {
        generation++;
        LAMPS.clear();
        CAR_CACHE.clear();
    }

    public static void record(ResourceManager resourceManager, String trainId, JsonObject vehicle, JsonObject modelProperties) {
        try {
            if (trainId == null || vehicle == null || modelProperties == null || !vehicle.has("model") || !modelProperties.has("parts")) {
                return;
            }
            final List<String> files = modelFiles(vehicle.get("model").getAsString());
            if (files.isEmpty()) {
                return;
            }
            final List<LampPart> parts = new ArrayList<>();
            for (final JsonElement element : modelProperties.getAsJsonArray("parts")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                final JsonObject part = element.getAsJsonObject();
                final String stage = text(part, "stage").toUpperCase(Locale.ROOT);
                final String condition = text(part, "render_condition").toUpperCase(Locale.ROOT);
                if (!stage.startsWith("LIGHT") && !stage.startsWith("ALWAYS_ON_LIGHT")) {
                    continue;
                }
                final boolean forwards;
                if (condition.equals("MOVING_FORWARDS") || condition.equals("ON_ROUTE_FORWARDS")) {
                    forwards = true;
                } else if (condition.equals("MOVING_BACKWARDS") || condition.equals("ON_ROUTE_BACKWARDS")) {
                    forwards = false;
                } else {
                    continue;
                }
                final String name = text(part, "name");
                if (name.isEmpty()) {
                    continue;
                }
                final int slash = name.lastIndexOf('/');
                final String file = slash < 0 ? files.get(0) : resolveFile(files, name.substring(0, slash));
                if (file == null) {
                    continue;
                }
                final List<float[]> positions = new ArrayList<>();
                if (part.has("positions") && part.get("positions").isJsonArray()) {
                    for (final JsonElement position : part.getAsJsonArray("positions")) {
                        final JsonArray array = position.getAsJsonArray();
                        positions.add(new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat()});
                    }
                }
                if (positions.isEmpty()) {
                    positions.add(new float[]{0, 0});
                }
                parts.add(new LampPart(forwards, file, slash < 0 ? name : name.substring(slash + 1), positions,
                    part.has("mirror") && part.get("mirror").getAsBoolean(), text(part, "whitelisted_cars"), text(part, "blacklisted_cars")));
            }
            if (parts.isEmpty()) {
                return;
            }
            final Map<String, String> sources = new HashMap<>();
            for (final LampPart part : parts) {
                if (!sources.containsKey(part.file)) {
                    final ResourceLocation location = cn.jstxjf_.rte.data.ScriptedCustomTrains.parseLegacyLocation(part.file);
                    sources.put(part.file, ResourceUtil.readResource(resourceManager, location));
                }
            }
            final String key = trainId.toLowerCase(Locale.ROOT);
            final int expected = generation;
            CompletableFuture.runAsync(() -> {
                final List<Lamp> lamps = build(parts, sources);
                if (expected == generation) {
                    LAMPS.put(key, lamps);
                    RTE.LOGGER.debug("[VehicleLights] legacy lamps {}: {}", key, describe(lamps));
                }
            });
        } catch (Exception e) {
            RTE.LOGGER.warn("[VehicleLights] legacy lamp read failed for {}: {}", trainId, e.toString());
        }
    }

    static List<Light> lightsFor(String vehicleId, int carIndex, int carCount) {
        if (vehicleId == null || LAMPS.isEmpty()) {
            return null;
        }
        final String cacheKey = vehicleId + "|" + carIndex + "|" + carCount;
        final List<Light> cached = CAR_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        List<Lamp> lamps = LAMPS.get(vehicleId.toLowerCase(Locale.ROOT));
        if (lamps == null) {
            lamps = LAMPS.get(GangwayPolicy.normalise(vehicleId));
        }
        if (lamps == null) {
            return null;
        }
        final LegacyVehicleRegistry.Part filter = new LegacyVehicleRegistry.Part();
        final List<Light> lights = new ArrayList<>();
        for (final Lamp lamp : lamps) {
            filter.whitelistedCars = lamp.whitelistedCars;
            filter.blacklistedCars = lamp.blacklistedCars;
            if (LegacyVehicleRenderer.skip(filter, carIndex, carCount)) {
                continue;
            }
            final boolean front = lamp.z > 0;
            final int direction = front ? 1 : -1;
            if (lamp.headWhenFront == front) {
                final float flare = Math.max(0.8F, lamp.size * 4);
                lights.add(new Light(Type.HEADLIGHT, 0xFFF3DC, 1, lamp.x, lamp.y, lamp.z, 0, -0.035F, direction, flare, flare, 3.2F, 20, 0, 0));
            } else {
                final float flare = Math.max(0.6F, lamp.size * 3);
                lights.add(new Light(Type.TAILLIGHT, 0xFF2A1A, 0.9F, lamp.x, lamp.y, lamp.z, 0, 0, direction, flare, flare, 0, 0, 0, 0));
            }
        }
        final List<Light> frozen = List.copyOf(lights);
        CAR_CACHE.put(cacheKey, frozen);
        return frozen;
    }

    private static List<Lamp> build(List<LampPart> parts, Map<String, String> sources) {
        final Map<String, Map<String, List<float[]>>> groupsByFile = new HashMap<>();
        for (final LampPart part : parts) {
            groupsByFile.computeIfAbsent(part.file, file -> new HashMap<>()).put(part.group, null);
        }
        groupsByFile.forEach((file, groups) -> readGroups(sources.get(file), groups));
        final List<Lamp> lamps = new ArrayList<>();
        for (final LampPart part : parts) {
            final List<float[]> vertices = groupsByFile.get(part.file).get(part.group);
            if (vertices == null || vertices.isEmpty()) {
                continue;
            }
            for (final float[] blob : clusters(vertices)) {
                for (final float[] position : part.positions) {
                    if (part.mirror) {
                        addLamp(lamps, part, blob, -position[0] / 16, position[1] / 16, true);
                    } else {
                        addLamp(lamps, part, blob, position[0] / 16, position[1] / 16, false);
                    }
                }
            }
        }
        return List.copyOf(lamps);
    }

    private static void addLamp(List<Lamp> lamps, LampPart part, float[] blob, float offsetX, float offsetZ, boolean mirrored) {
        float x = blob[0] + offsetX;
        float z = blob[2] + offsetZ;
        if (mirrored) {
            x = -x;
            z = -z;
        }
        final boolean headWhenFront = part.forwards;
        lamps.add(new Lamp(headWhenFront, x, blob[1] + OBJ_TO_LAMP_Y, z, blob[3], part.whitelistedCars, part.blacklistedCars));
    }

    private static void readGroups(String source, Map<String, List<float[]>> groups) {
        if (source == null) {
            return;
        }
        final List<float[]> vertices = new ArrayList<>();
        Set<String> active = Set.of();
        for (final String raw : source.split("\n")) {
            final String line = raw.trim();
            if (line.startsWith("v ")) {
                final String[] parts = line.split("\\s+");
                vertices.add(new float[]{Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])});
            } else if (line.startsWith("g ") || line.startsWith("o ")) {
                final Set<String> names = new HashSet<>(List.of(line.substring(2).trim().split("\\s+")));
                names.retainAll(groups.keySet());
                active = names;
            } else if (line.startsWith("f ") && !active.isEmpty()) {
                final String[] parts = line.split("\\s+");
                for (int i = 1; i < parts.length; i++) {
                    final int index = Integer.parseInt(parts[i].split("/")[0]);
                    final float[] vertex = vertices.get(index > 0 ? index - 1 : vertices.size() + index);
                    for (final String name : active) {
                        List<float[]> list = groups.get(name);
                        if (list == null) {
                            list = new ArrayList<>();
                            groups.put(name, list);
                        }
                        list.add(vertex);
                    }
                }
            }
        }
    }

    private static List<float[]> clusters(List<float[]> vertices) {
        final Map<Long, List<float[]>> cells = new HashMap<>();
        for (final float[] vertex : vertices) {
            cells.computeIfAbsent(((long) (int) Math.floor(vertex[0] / CELL) << 32) | ((int) Math.floor(vertex[1] / CELL) & 0xFFFFFFFFL), key -> new ArrayList<>()).add(vertex);
        }
        final Set<Long> visited = new HashSet<>();
        final List<float[]> blobs = new ArrayList<>();
        for (final Long start : cells.keySet()) {
            if (!visited.add(start)) {
                continue;
            }
            final List<Long> queue = new ArrayList<>();
            queue.add(start);
            final float[] min = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
            final float[] max = {-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
            for (int i = 0; i < queue.size(); i++) {
                final long cell = queue.get(i);
                for (final float[] vertex : cells.get(cell)) {
                    for (int axis = 0; axis < 3; axis++) {
                        min[axis] = Math.min(min[axis], vertex[axis]);
                        max[axis] = Math.max(max[axis], vertex[axis]);
                    }
                }
                final int cx = (int) (cell >> 32);
                final int cy = (int) cell;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        final long next = ((long) (cx + dx) << 32) | ((cy + dy) & 0xFFFFFFFFL);
                        if (cells.containsKey(next) && visited.add(next)) {
                            queue.add(next);
                        }
                    }
                }
            }
            final float size = Math.max(max[0] - min[0], max[1] - min[1]);
            if (size <= MAX_LAMP_SIZE) {
                blobs.add(new float[]{(min[0] + max[0]) / 2, (min[1] + max[1]) / 2, Math.abs(max[2]) > Math.abs(min[2]) ? max[2] : min[2], Math.max(0.15F, size)});
            }
        }
        return blobs;
    }

    private static List<String> modelFiles(String model) {
        final List<String> files = new ArrayList<>();
        final String[] segments = model.split("\\|", -1);
        for (int i = 0; i < segments.length; i += 2) {
            final String file = segments[i].trim();
            if (file.toLowerCase(Locale.ROOT).endsWith(".obj")) {
                files.add(file);
            }
        }
        return files;
    }

    private static String resolveFile(List<String> files, String prefix) {
        final String lower = prefix.toLowerCase(Locale.ROOT);
        for (final String file : files) {
            if (file.toLowerCase(Locale.ROOT).endsWith("/" + lower) || file.toLowerCase(Locale.ROOT).endsWith(":" + lower) || file.equalsIgnoreCase(lower)) {
                return file;
            }
        }
        return null;
    }

    private static String text(JsonObject object, String key) {
        try {
            return object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private static String describe(List<Lamp> lamps) {
        final StringBuilder builder = new StringBuilder();
        for (final Lamp lamp : lamps) {
            builder.append(String.format(Locale.ROOT, " %s(%.2f,%.2f,%.2f)[%s|%s]", lamp.headWhenFront ? "F" : "B", lamp.x, lamp.y, lamp.z, lamp.whitelistedCars, lamp.blacklistedCars));
        }
        return builder.length() == 0 ? "none" : builder.toString();
    }
}
