package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import cn.jstxjf_.rte.compat.McCompat;

public final class VehicleLightRegistry {

    public static final String RESOURCE_PATH = "rte_vehicle_lights.json";

    public enum Type {
        HEADLIGHT, TAILLIGHT, BRAKE, DOOR, RUNNING, ALWAYS;

        static Type parse(String value) {
            try {
                return valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                return ALWAYS;
            }
        }
    }

    public record Light(Type type, int rgb, float brightness,
                        float x, float y, float z,
                        float axisX, float axisY, float axisZ,
                        float flareWidth, float flareHeight,
                        float beamDiameter, float beamLength,
                        int blinkOnMillis, int blinkOffMillis) {

        public boolean atFrontEnd() {
            return z >= 0;
        }

        Light mirrored() {
            return new Light(type, rgb, brightness, -x, y, z, -axisX, axisY, axisZ,
                flareWidth, flareHeight, beamDiameter, beamLength, blinkOnMillis, blinkOffMillis);
        }
    }

    private record Entry(List<String> patterns, Light light) {
    }

    private static final List<Entry> ENTRIES = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, List<Light>> CACHE = new ConcurrentHashMap<>();

    private VehicleLightRegistry() {
    }

    public static void clearCache() {
        CACHE.clear();
    }

    public static void reload(ResourceManager resourceManager) {
        ENTRIES.clear();
        CACHE.clear();
        VehicleLightOverrides.load();
        if (resourceManager == null) {
            return;
        }
        int files = 0;
        for (final String namespace : resourceManager.getNamespaces().stream().sorted().toList()) {
            final ResourceLocation location = McCompat.id(namespace, RESOURCE_PATH);
            for (final Resource resource : resourceManager.getResourceStack(location)) {
                try (BufferedReader reader = resource.openAsReader()) {
                    parseFile(JsonParser.parseReader(reader).getAsJsonObject());
                    files++;
                } catch (Exception e) {
                    RTE.LOGGER.warn("[VehicleLights] Failed to read {} from {}: {}", location, resource.sourcePackId(), e.toString());
                }
            }
        }
        if (files > 0) {
            RTE.LOGGER.info("[VehicleLights] Loaded {} light definitions from {} files", ENTRIES.size(), files);
        }
    }

    public static List<Light> lightsFor(String vehicleId) {
        final List<Light> overridden = VehicleLightOverrides.lightsFor(vehicleId);
        if (overridden != null) {
            return overridden;
        }
        if (vehicleId == null || ENTRIES.isEmpty()) {
            return null;
        }
        final List<Light> cached = CACHE.get(vehicleId);
        if (cached != null) {
            return cached.isEmpty() ? null : cached;
        }
        final String id = vehicleId.toLowerCase(Locale.ROOT);
        final List<Light> result = new ArrayList<>();
        synchronized (ENTRIES) {
            for (final Entry entry : ENTRIES) {
                for (final String pattern : entry.patterns) {
                    if (matches(pattern, id)) {
                        result.add(entry.light);
                        break;
                    }
                }
            }
        }
        final List<Light> frozen = List.copyOf(result);
        CACHE.put(vehicleId, frozen);
        return frozen.isEmpty() ? null : frozen;
    }

    private static boolean matches(String pattern, String id) {
        if (pattern.endsWith("*")) {
            return id.startsWith(pattern.substring(0, pattern.length() - 1));
        }
        return pattern.equals(id) || GangwayPolicy.normalise(id).equals(pattern);
    }

    private static void parseFile(JsonObject root) {
        if (!root.has("lights") || !root.get("lights").isJsonArray()) {
            return;
        }
        for (final JsonElement element : root.getAsJsonArray("lights")) {
            if (!element.isJsonObject()) {
                continue;
            }
            final JsonObject object = element.getAsJsonObject();
            final List<String> patterns = readPatterns(object);
            if (patterns.isEmpty()) {
                continue;
            }
            final Light light = readLight(object);
            ENTRIES.add(new Entry(patterns, light));
            if (object.has("mirrorX") && object.get("mirrorX").getAsBoolean()) {
                ENTRIES.add(new Entry(patterns, light.mirrored()));
            }
        }
    }

    private static List<String> readPatterns(JsonObject object) {
        final List<String> patterns = new ArrayList<>();
        final JsonElement vehicles = object.has("vehicles") ? object.get("vehicles") : object.get("vehicle");
        if (vehicles == null) {
            return patterns;
        }
        if (vehicles.isJsonArray()) {
            for (final JsonElement vehicle : vehicles.getAsJsonArray()) {
                patterns.add(vehicle.getAsString().trim().toLowerCase(Locale.ROOT));
            }
        } else {
            patterns.add(vehicles.getAsString().trim().toLowerCase(Locale.ROOT));
        }
        patterns.removeIf(String::isEmpty);
        return patterns;
    }

    public static Light readLight(JsonObject object) {
        final Type type = object.has("type") ? Type.parse(object.get("type").getAsString()) : Type.ALWAYS;
        final float[] pos = readVector(object, "pos", 0, 1, 0);
        final float[] axis = readVector(object, "axis", 0, 0, pos[2] < 0 ? -1 : 1);
        final float[] blink = readVector(object, "blink", 0, 0, 0);
        final float flareSize = readFloat(object, "flareSize", type == Type.HEADLIGHT ? 1.1F : 0.8F);
        return new Light(
            type,
            readColor(object, type),
            Math.max(0, readFloat(object, "brightness", 1)),
            pos[0], pos[1], pos[2],
            axis[0], axis[1], axis[2],
            Math.max(0, readFloat(object, "flareWidth", flareSize)),
            Math.max(0, readFloat(object, "flareHeight", flareSize)),
            Math.max(0, readFloat(object, "beamDiameter", 0)),
            Math.max(0, readFloat(object, "beamLength", 0)),
            Math.max(0, (int) blink[0]),
            Math.max(0, (int) blink[1])
        );
    }

    private static int readColor(JsonObject object, Type type) {
        final int fallback = switch (type) {
            case TAILLIGHT, BRAKE -> 0xFF2A1A;
            case DOOR -> 0xFFB020;
            default -> 0xFFF3DC;
        };
        if (!object.has("color")) {
            return fallback;
        }
        try {
            String text = object.get("color").getAsString().trim();
            if (text.startsWith("#")) {
                text = text.substring(1);
            }
            return Integer.parseInt(text, 16) & 0xFFFFFF;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static float readFloat(JsonObject object, String key, float fallback) {
        try {
            return object.has(key) ? object.get(key).getAsFloat() : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static float[] readVector(JsonObject object, String key, float x, float y, float z) {
        final float[] result = {x, y, z};
        if (!object.has(key) || !object.get(key).isJsonArray()) {
            return result;
        }
        final JsonArray array = object.getAsJsonArray(key);
        for (int i = 0; i < Math.min(3, array.size()); i++) {
            try {
                result[i] = array.get(i).getAsFloat();
            } catch (Exception ignored) {
            }
        }
        return result;
    }
}
