package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class VehicleLightOverrides {

    private static final Path DIRECTORY = Path.of("config", "rte", "vehicle_lights");
    private static final Path EXPORT_FILE = Path.of("config", "rte", "vehicle_lights_export", "assets", "rte", VehicleLightRegistry.RESOURCE_PATH);
    private static final Map<String, JsonArray> RAW = new ConcurrentHashMap<>();
    private static final Map<String, List<VehicleLightRegistry.Light>> EXPANDED = new ConcurrentHashMap<>();

    private VehicleLightOverrides() {
    }

    public static void load() {
        RAW.clear();
        EXPANDED.clear();
        if (!Files.isDirectory(DIRECTORY)) {
            return;
        }
        try (Stream<Path> files = Files.list(DIRECTORY)) {
            files.filter(file -> file.getFileName().toString().endsWith(".json")).forEach(file -> {
                try {
                    final JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
                    final String vehicle = root.get("vehicle").getAsString();
                    final JsonArray lights = root.getAsJsonArray("lights");
                    RAW.put(key(vehicle), lights);
                    EXPANDED.put(key(vehicle), expand(lights));
                } catch (Exception e) {
                    RTE.LOGGER.warn("[VehicleLights] Failed to read override {}: {}", file, e.toString());
                }
            });
        } catch (IOException e) {
            RTE.LOGGER.warn("[VehicleLights] Failed to list {}: {}", DIRECTORY, e.toString());
        }
        if (!RAW.isEmpty()) {
            RTE.LOGGER.info("[VehicleLights] Loaded local light overrides for {} vehicles", RAW.size());
        }
    }

    public static List<VehicleLightRegistry.Light> lightsFor(String vehicleId) {
        return vehicleId == null ? null : EXPANDED.get(key(vehicleId));
    }

    public static JsonArray rawFor(String vehicleId) {
        final JsonArray raw = vehicleId == null ? null : RAW.get(key(vehicleId));
        return raw == null ? null : raw.deepCopy();
    }

    public static void save(String vehicleId, JsonArray lights) throws IOException {
        Files.createDirectories(DIRECTORY);
        final JsonObject root = new JsonObject();
        root.addProperty("vehicle", vehicleId);
        root.add("lights", lights.deepCopy());
        Files.writeString(fileOf(vehicleId), new GsonBuilder().setPrettyPrinting().create().toJson(root), StandardCharsets.UTF_8);
        RAW.put(key(vehicleId), lights.deepCopy());
        EXPANDED.put(key(vehicleId), expand(lights));
        VehicleLightRegistry.clearCache();
    }

    public static void remove(String vehicleId) throws IOException {
        Files.deleteIfExists(fileOf(vehicleId));
        RAW.remove(key(vehicleId));
        EXPANDED.remove(key(vehicleId));
        VehicleLightRegistry.clearCache();
    }

    public static Path export() throws IOException {
        final JsonArray all = new JsonArray();
        final Map<String, JsonArray> sorted = new TreeMap<>(RAW);
        for (final Map.Entry<String, JsonArray> entry : sorted.entrySet()) {
            for (final JsonElement element : entry.getValue()) {
                final JsonObject light = element.getAsJsonObject().deepCopy();
                final JsonArray vehicles = new JsonArray();
                vehicles.add(entry.getKey());
                light.add("vehicles", vehicles);
                all.add(light);
            }
        }
        final JsonObject root = new JsonObject();
        root.add("lights", all);
        Files.createDirectories(EXPORT_FILE.getParent());
        Files.writeString(EXPORT_FILE, new GsonBuilder().setPrettyPrinting().create().toJson(root), StandardCharsets.UTF_8);
        return EXPORT_FILE.toAbsolutePath();
    }

    private static List<VehicleLightRegistry.Light> expand(JsonArray lights) {
        final List<VehicleLightRegistry.Light> result = new ArrayList<>();
        for (final JsonElement element : lights) {
            if (!element.isJsonObject()) {
                continue;
            }
            final JsonObject object = element.getAsJsonObject();
            final VehicleLightRegistry.Light light = VehicleLightRegistry.readLight(object);
            result.add(light);
            if (object.has("mirrorX") && object.get("mirrorX").getAsBoolean()) {
                result.add(light.mirrored());
            }
        }
        return List.copyOf(result);
    }

    private static String key(String vehicleId) {
        return vehicleId.toLowerCase(Locale.ROOT);
    }

    private static Path fileOf(String vehicleId) {
        return DIRECTORY.resolve(key(vehicleId).replaceAll("[^a-z0-9_.-]", "_") + ".json");
    }
}
