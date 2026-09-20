package cn.jstxjf_.rte.mixin;

import org.mtr.legacy.resource.LegacyVehicleResource;
import org.mtr.libraries.com.google.gson.JsonArray;
import org.mtr.libraries.com.google.gson.JsonElement;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = LegacyVehicleResource.class, remap = false)
public class LegacyDoorwayMixin {

    private static int rte$synthesisedModels = 0;

    @ModifyArg(
        method = "lambda$convert$3",
        at = @At(
            value = "INVOKE",
            target = "Lorg/mtr/core/serializer/JsonReader;<init>(Lorg/mtr/libraries/com/google/gson/JsonElement;)V",
            ordinal = 1
        ),
        index = 0,
        require = 0
    )
    private static JsonElement rte$prepareModelProperties(JsonElement modelProperties) {
        try {
            if (modelProperties != null && modelProperties.isJsonObject()) {
                rte$normaliseDisplayTypes(modelProperties.getAsJsonObject());
                rte$reportDisplayParts(modelProperties.getAsJsonObject());
                RTE_PENDING_PARTS = modelProperties.getAsJsonObject();
            }
        } catch (Throwable e) {
            cn.jstxjf_.rte.RTE.LOGGER.warn("[LegacyDoorway] preparation failed", e);
        }
        return modelProperties;
    }


    private static final java.util.Map<String, String> RTE_DISPLAY_TYPE_ALIASES = java.util.Map.of(
        "NEXT_STATION_PLAIN", "NEXT_STATION",
        "NEXT_STATION_NORMAL", "NEXT_STATION",
        "NEXT_STATION_HK", "NEXT_STATION_MTR");

    private static int rte$displayAliasCount = 0;

    private static void rte$normaliseDisplayTypes(JsonObject root) {
        if (root == null || !root.has("parts") || !root.get("parts").isJsonArray()) {
            return;
        }
        for (final JsonElement element : root.getAsJsonArray("parts")) {
            if (!element.isJsonObject()) {
                continue;
            }
            final JsonObject part = element.getAsJsonObject();
            if (!part.has("displayType")) {
                continue;
            }
            final String raw = rte$string(part, "displayType");
            if (raw == null) {
                continue;
            }
            final String alias = RTE_DISPLAY_TYPE_ALIASES.get(raw.toUpperCase(java.util.Locale.ROOT));
            if (alias == null) {
                continue;
            }
            part.addProperty("displayType", alias);
            if (++rte$displayAliasCount <= 8) {
                cn.jstxjf_.rte.RTE.LOGGER.info("[LegacyDisplay] displayType {} -> {}", raw, alias);
            }
        }
    }

    private static int rte$displayReports = 0;

    private static void rte$reportDisplayParts(JsonObject root) {
        if (rte$displayReports >= 3 || !root.has("parts") || !root.get("parts").isJsonArray()) {
            return;
        }
        int shown = 0;
        int displayCount = 0;
        final StringBuilder builder = new StringBuilder();
        for (final JsonElement element : root.getAsJsonArray("parts")) {
            if (!element.isJsonObject()) {
                continue;
            }
            final JsonObject part = element.getAsJsonObject();
            final boolean looksLikeDisplay = part.has("displayType") || part.has("display")
                || "DISPLAY".equals(rte$string(part, "type"));
            if (!looksLikeDisplay) {
                continue;
            }
            displayCount++;
            if (shown < 3) {
                shown++;
                builder.append(part).append(System.lineSeparator());
            }
        }
        if (displayCount == 0) {
            return;
        }
        rte$displayReports++;
        cn.jstxjf_.rte.RTE.LOGGER.info("[LegacyDisplay] converted parts with display: {}{}{}",
            displayCount, System.lineSeparator(), builder.toString().trim());
    }

    private static int rte$otherArgReports = 0;

    @ModifyArg(
        method = "lambda$convert$3",
        at = @At(value = "INVOKE",
            target = "Lorg/mtr/core/serializer/JsonReader;<init>(Lorg/mtr/libraries/com/google/gson/JsonElement;)V",
            ordinal = 0),
        index = 0,
        require = 0
    )
    private static JsonElement rte$peekArg0(JsonElement element) {
        rte$peek("ordinal0", element);
        return element;
    }

    @ModifyArg(
        method = "lambda$convert$3",
        at = @At(value = "INVOKE",
            target = "Lorg/mtr/core/serializer/JsonReader;<init>(Lorg/mtr/libraries/com/google/gson/JsonElement;)V",
            ordinal = 2),
        index = 0,
        require = 0
    )
    private static JsonElement rte$peekArg2(JsonElement element) {
        rte$fillFlippedPositions(element);
        rte$captureDefs(element);
        if (RTE_PENDING_PARTS != null) {
            final JsonObject pending = RTE_PENDING_PARTS;
            RTE_PENDING_PARTS = null;
            rte$synthesiseDoorways(pending, element);
            rte$reportPartOffsets(pending);
        }
        rte$peek("ordinal2", element);
        return element;
    }

    private static void rte$synthesiseDoorways(JsonObject modelProperties, JsonElement definitionsElement) {
        try {
            if (!cn.jstxjf_.rte.ClientConfig.legacyDoorways || modelProperties == null
                    || !modelProperties.has("parts") || !modelProperties.get("parts").isJsonArray()) {
                return;
            }
            final JsonArray parts = modelProperties.getAsJsonArray("parts");
            for (final JsonElement existing : parts) {
                if (existing.isJsonObject() && "DOORWAY".equals(rte$string(existing.getAsJsonObject(), "type"))) {
                    return;
                }
            }

            final java.util.Map<String, String> definitions = rte$definitionSignatures(definitionsElement);
            final java.util.Map<String, JsonObject> byOpening = new java.util.LinkedHashMap<>();
            for (final JsonElement element : parts) {
                if (!element.isJsonObject()) {
                    continue;
                }
                final JsonObject part = element.getAsJsonObject();
                final double doorX = rte$number(part, "doorXMultiplier");
                final double doorZ = rte$number(part, "doorZMultiplier");
                if (doorX == 0 && doorZ == 0) {
                    continue;
                }
                final String type = rte$string(part, "type");
                if (type != null && !"NORMAL".equals(type)) {
                    continue;
                }
                final String key = rte$openingKey(part, doorX, doorZ, definitions);
                final JsonObject previous = byOpening.get(key);
                if (previous == null || rte$stagePriority(part) < rte$stagePriority(previous)) {
                    byOpening.put(key, part);
                }
            }

            int added = 0;
            for (final JsonObject part : byOpening.values()) {
                if (added >= 64) {
                    break;
                }
                parts.add(rte$derive(part, "DOORWAY"));
                added++;
            }
            if (added > 0 && ++rte$synthesisedModels <= 20) {
                cn.jstxjf_.rte.RTE.LOGGER.info(
                    "[LegacyDoorway] synthesised {} deduplicated DOORWAY parts (total parts now {})",
                    added, parts.size());
            }
        } catch (Throwable e) {
            cn.jstxjf_.rte.RTE.LOGGER.warn("[LegacyDoorway] synthesis failed", e);
        }
    }

    private static java.util.Map<String, String> rte$definitionSignatures(JsonElement definitionsElement) {
        final java.util.Map<String, String> result = new java.util.HashMap<>();
        if (definitionsElement == null || !definitionsElement.isJsonObject()) {
            return result;
        }
        final JsonObject root = definitionsElement.getAsJsonObject();
        if (!root.has("positionDefinitions") || !root.get("positionDefinitions").isJsonArray()) {
            return result;
        }
        for (final JsonElement element : root.getAsJsonArray("positionDefinitions")) {
            if (!element.isJsonObject()) {
                continue;
            }
            final JsonObject definition = element.getAsJsonObject();
            final String name = rte$string(definition, "name");
            if (name == null) {
                continue;
            }
            final String positions = definition.has("positions") ? definition.get("positions").toString() : "[]";
            final String flipped = definition.has("positionsFlipped") ? definition.get("positionsFlipped").toString() : "[]";
            result.put(name, positions + "/flip" + flipped);
        }
        return result;
    }

    private static int rte$flippedFilled = 0;

    private static void rte$fillFlippedPositions(JsonElement element) {
        final String mode = cn.jstxjf_.rte.ClientConfig.legacyFlippedPositionFix;
        if (mode == null || "off".equalsIgnoreCase(mode) || element == null || !element.isJsonObject()) {
            return;
        }
        final boolean mirror = "mirror".equalsIgnoreCase(mode);
        final JsonObject root = element.getAsJsonObject();
        if (!root.has("positionDefinitions") || !root.get("positionDefinitions").isJsonArray()) {
            return;
        }
        int filled = 0;
        for (final JsonElement defElement : root.getAsJsonArray("positionDefinitions")) {
            if (!defElement.isJsonObject()) {
                continue;
            }
            final JsonObject definition = defElement.getAsJsonObject();
            if (!definition.has("positions") || !definition.get("positions").isJsonArray()) {
                continue;
            }
            final JsonArray positions = definition.getAsJsonArray("positions");
            final boolean flippedMissing = !definition.has("positionsFlipped")
                || !definition.get("positionsFlipped").isJsonArray()
                || definition.getAsJsonArray("positionsFlipped").isEmpty();
            if (!flippedMissing || positions.isEmpty()) {
                continue;
            }
            final JsonArray flipped = positions.deepCopy();
            if (mirror) {
                for (final JsonElement posElement : flipped) {
                    if (!posElement.isJsonObject()) {
                        continue;
                    }
                    final JsonObject pos = posElement.getAsJsonObject();
                    for (final String axis : new String[]{"x", "z"}) {
                        if (pos.has(axis)) {
                            try {
                                pos.addProperty(axis, -pos.get(axis).getAsDouble());
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
            definition.add("positionsFlipped", flipped);
            filled++;
        }
        if (filled > 0 && ++rte$flippedFilled <= 8) {
            cn.jstxjf_.rte.RTE.LOGGER.info("[LegacyFlipFix] mode={} filled positionsFlipped for {} position definitions", mode, filled);
        }
    }

    private static final java.util.Map<String, String> RTE_DEFS = new java.util.HashMap<>();
    private static JsonObject RTE_PENDING_PARTS = null;

    private static void rte$captureDefs(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        final JsonObject root = element.getAsJsonObject();
        if (!root.has("positionDefinitions") || !root.get("positionDefinitions").isJsonArray()) {
            return;
        }
        for (final JsonElement d : root.getAsJsonArray("positionDefinitions")) {
            if (!d.isJsonObject()) {
                continue;
            }
            final JsonObject def = d.getAsJsonObject();
            if (!def.has("name")) {
                continue;
            }
            RTE_DEFS.put(def.get("name").getAsString(),
                (def.has("positions") ? def.get("positions").toString() : "?")
                + "/flip" + (def.has("positionsFlipped") ? def.get("positionsFlipped").toString() : "?"));
        }
    }

    private static void rte$peek(String label, JsonElement element) {
        if (rte$otherArgReports >= 6 || element == null) {
            return;
        }
        final String text = element.toString();
        if (!text.contains("224")) {
            return;
        }
        rte$otherArgReports++;
        cn.jstxjf_.rte.RTE.LOGGER.info("[LegacyArg] {} contains 224, length {} chars, key={} fragment={}",
            label, text.length(),
            element.isJsonObject() ? element.getAsJsonObject().keySet() : "(not an object)",
            text.length() > 900 ? text.substring(0, 900) + "…" : text);
    }

    private static int rte$offsetReports = 0;

    private static void rte$reportPartOffsets(JsonObject root) {
        if (rte$offsetReports >= 8 || !root.has("parts") || !root.get("parts").isJsonArray()) {
            return;
        }
        final JsonObject definitions = new JsonObject();
        for (final java.util.Map.Entry<String, JsonElement> entry : root.entrySet()) {
            if (!"parts".equals(entry.getKey())) {
                definitions.add(entry.getKey(), entry.getValue());
            }
        }
        final StringBuilder builder = new StringBuilder();
        int shown = 0;
        for (final JsonElement element : root.getAsJsonArray("parts")) {
            if (!element.isJsonObject() || shown >= 12) {
                continue;
            }
            final JsonObject part = element.getAsJsonObject();
            final String names = part.has("names") ? part.get("names").toString() : "";
            if (!names.contains("out1") && !names.contains("crt3")) {
                continue;
            }
            final String stage = rte$string(part, "renderStage");
            shown++;
            final StringBuilder resolved = new StringBuilder();
            if (part.has("positionDefinitions") && part.get("positionDefinitions").isJsonArray()) {
                for (final JsonElement ref : part.getAsJsonArray("positionDefinitions")) {
                    try {
                        resolved.append(RTE_DEFS.getOrDefault(ref.getAsString(), "<unresolved>")).append(' ');
                    } catch (Exception ignored) {
                    }
                }
            }
            builder.append("   names=").append(part.has("names") ? part.get("names") : "?")
                .append(" stage=").append(stage)
                .append(" offset=").append(resolved.toString().trim())
                .append(System.lineSeparator());
        }
        if (shown == 0) {
            return;
        }
        rte$offsetReports++;
        final String definitionText = definitions.size() == 0
            ? "(empty)"
            : definitions.toString();
        cn.jstxjf_.rte.RTE.LOGGER.info("[LegacyOffsets] length={} width={}{}{}{}rootProps={}",
            root.has("length") ? root.get("length") : "?",
            root.has("width") ? root.get("width") : "?",
            System.lineSeparator(), builder.toString().trim(),
            System.lineSeparator(),
            definitionText.length() > 1200 ? definitionText.substring(0, 1200) + "…" : definitionText);
    }

    private static JsonObject rte$derive(JsonObject source, String type) {
        final JsonObject derived = source.deepCopy();
        derived.addProperty("type", type);
        derived.addProperty("condition", "NORMAL");
        derived.addProperty("renderStage", "EXTERIOR");
        derived.addProperty("doorXMultiplier", 0);
        derived.addProperty("doorZMultiplier", 0);
        derived.remove("displayType");
        derived.remove("displayOptions");
        derived.remove("displayDefaultText");
        return derived;
    }

    private static String rte$openingKey(JsonObject part, double doorX, double doorZ,
            java.util.Map<String, String> definitions) {
        final StringBuilder builder = new StringBuilder();
        if (part.has("positionDefinitions") && part.get("positionDefinitions").isJsonArray()) {
            for (final JsonElement element : part.getAsJsonArray("positionDefinitions")) {
                try {
                    final String name = element.getAsString();
                    builder.append(definitions.getOrDefault(name, name)).append('|');
                } catch (Exception ignored) {
                    builder.append(element).append('|');
                }
            }
        }
        if (builder.length() == 0 && part.has("names") && part.get("names").isJsonArray()) {
            for (final JsonElement element : part.getAsJsonArray("names")) {
                builder.append(element).append('|');
            }
        }
        builder.append(Math.signum(doorX)).append('/').append(Math.signum(doorZ));
        return builder.toString();
    }

    private static int rte$stagePriority(JsonObject part) {
        final String stage = rte$string(part, "renderStage");
        if ("EXTERIOR".equals(stage)) {
            return 0;
        }
        if ("INTERIOR".equals(stage)) {
            return 1;
        }
        return 2;
    }

    private static String rte$string(JsonObject object, String key) {
        try {
            return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static double rte$number(JsonObject object, String key) {
        try {
            return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsDouble() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
