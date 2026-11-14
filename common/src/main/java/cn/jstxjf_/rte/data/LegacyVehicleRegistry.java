package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LegacyVehicleRegistry {

    public static final class Part {

        public final List<String> names = new ArrayList<>();
        public final List<float[]> positions = new ArrayList<>();
        public String renderStage = "EXTERIOR";
        public String whitelistedCars = "";
        public String blacklistedCars = "";
        public boolean mirror;
        public double doorXMultiplier;
        public double doorZMultiplier;
        public String doorOffset = "NONE";
        public String renderCondition = "ALL";
        public boolean skipIfTooFar;
    }

    public static final class Definition {

        public final String trainId;
        public final String modelLocation;
        public final String textureId;
        public final boolean flipV;
        public final double length;
        public final double width;
        public final String doorAnimationType;
        public final List<Part> parts = new ArrayList<>();

        Definition(String trainId, String modelLocation, String textureId, boolean flipV, double length, double width, String doorAnimationType) {
            this.trainId = trainId;
            this.modelLocation = modelLocation;
            this.textureId = textureId;
            this.flipV = flipV;
            this.length = length;
            this.width = width;
            this.doorAnimationType = doorAnimationType;
        }
    }

    private static final Map<String, Definition> DEFINITIONS = new ConcurrentHashMap<>();

    private LegacyVehicleRegistry() {
    }

    public static void reset() {
        DEFINITIONS.clear();
    }

    public static int size() {
        return DEFINITIONS.size();
    }

    public static Definition get(String trainId) {
        if (trainId == null) {
            return null;
        }
        final Definition direct = DEFINITIONS.get(trainId.toLowerCase(Locale.ROOT));
        if (direct != null) {
            return direct;
        }
        return DEFINITIONS.get(GangwayPolicy.normalise(trainId));
    }

    public static void record(String trainId, JsonObject vehicle, JsonObject modelProperties) {
        if (trainId == null || vehicle == null || modelProperties == null) {
            return;
        }
        try {
            final String model = string(vehicle, "model");
            if (model == null || !model.toLowerCase(Locale.ROOT).endsWith(".obj")) {
                return;
            }
            final Definition definition = new Definition(
                trainId.toLowerCase(Locale.ROOT), model, string(vehicle, "texture_id"),
                vehicle.has("flipV") && vehicle.get("flipV").getAsBoolean(),
                number(modelProperties, "length", 0), number(modelProperties, "width", 0),
                orDefault(string(vehicle, "door_animation_type"), "STANDARD"));
            if (!modelProperties.has("parts") || !modelProperties.get("parts").isJsonArray()) {
                return;
            }
            for (final JsonElement element : modelProperties.getAsJsonArray("parts")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                final JsonObject partObject = element.getAsJsonObject();
                final Part part = new Part();
                final String name = string(partObject, "name");
                if (name != null) {
                    part.names.add(name);
                }
                if (partObject.has("names") && partObject.get("names").isJsonArray()) {
                    for (final JsonElement nameElement : partObject.getAsJsonArray("names")) {
                        part.names.add(nameElement.getAsString());
                    }
                }
                if (part.names.isEmpty()) {
                    continue;
                }
                if (partObject.has("positions") && partObject.get("positions").isJsonArray()) {
                    for (final JsonElement posElement : partObject.getAsJsonArray("positions")) {
                        if (!posElement.isJsonArray() || posElement.getAsJsonArray().size() < 2) {
                            continue;
                        }
                        final JsonArray pos = posElement.getAsJsonArray();
                        part.positions.add(new float[]{pos.get(0).getAsFloat(), pos.get(1).getAsFloat()});
                    }
                }
                if (part.positions.isEmpty()) {
                    part.positions.add(new float[]{0, 0});
                }
                part.renderStage = orDefault(string(partObject, "stage"), "EXTERIOR").toUpperCase(Locale.ROOT);
                part.whitelistedCars = orDefault(string(partObject, "whitelisted_cars"), "");
                part.blacklistedCars = orDefault(string(partObject, "blacklisted_cars"), "");
                part.mirror = partObject.has("mirror") && partObject.get("mirror").getAsBoolean();
                part.doorXMultiplier = number(partObject, "doorXMultiplier", 0);
                part.doorZMultiplier = number(partObject, "doorZMultiplier", 0);
                part.doorOffset = orDefault(string(partObject, "door_offset"), "NONE").toUpperCase(Locale.ROOT);
                part.renderCondition = orDefault(string(partObject, "render_condition"), "ALL").toUpperCase(Locale.ROOT);
                part.skipIfTooFar = partObject.has("skip_rendering_if_too_far")
                    && partObject.get("skip_rendering_if_too_far").getAsBoolean();
                definition.parts.add(part);
            }
            if (!definition.parts.isEmpty()) {
                DEFINITIONS.put(definition.trainId, definition);
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("[LegacyVehicle] failed reading {}: {}", trainId, e.toString());
        }
    }

    public static void report() {
        if (!DEFINITIONS.isEmpty()) {
            RTE.LOGGER.info("[LegacyVehicle] captured {} obj-based legacy vehicle definition(s)", DEFINITIONS.size());
        }
    }

    private static String string(JsonObject object, String key) {
        try {
            return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static double number(JsonObject object, String key, double fallback) {
        try {
            return object.has(key) ? object.get(key).getAsDouble() : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static String orDefault(String value, String fallback) {
        return value == null ? fallback : value;
    }
}
