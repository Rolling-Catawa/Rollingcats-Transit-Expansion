package cn.jstxjf_.rte.util;

import org.mtr.libraries.com.google.gson.JsonArray;
import org.mtr.libraries.com.google.gson.JsonElement;
import org.mtr.libraries.com.google.gson.JsonObject;

import java.util.ArrayDeque;

public final class LegacyModelSegments {

    private static final ThreadLocal<State> STATE = new ThreadLocal<>();

    private LegacyModelSegments() {
    }

    public static void begin(String model, int currentCar, int totalCars) {
        STATE.remove();
        if (model == null || model.indexOf('|') < 0) {
            return;
        }
        final String[] segments = model.split("\\|", -1);
        final ArrayDeque<Boolean> flags = new ArrayDeque<>();
        boolean anyReversed = false;
        for (int i = 0; i < segments.length; i += 2) {
            final String[] conditions = i + 1 < segments.length ? segments[i + 1].split(";", -1) : new String[0];
            if (conditions.length < 2
                    || matchesFilter(conditions[1].split(","), currentCar, totalCars) <= matchesFilter(conditions[0].split(","), currentCar, totalCars)) {
                final boolean reversed = conditions.length >= 3 && conditions[2].equals("reversed");
                flags.add(reversed);
                anyReversed |= reversed;
            }
        }
        if (anyReversed && flags.contains(false)) {
            STATE.set(new State(flags, anyReversed));
        }
    }

    public static void end() {
        STATE.remove();
    }

    public static JsonObject nextPositionDefinitions(JsonObject positionDefinitionsObject) {
        final State state = STATE.get();
        if (state == null || state.flags.isEmpty() || positionDefinitionsObject == null) {
            return positionDefinitionsObject;
        }
        final boolean ownReversed = state.flags.poll();
        if (ownReversed == state.carReversed) {
            return positionDefinitionsObject;
        }
        final JsonObject copy = positionDefinitionsObject.deepCopy();
        if (!copy.has("positionDefinitions") || !copy.get("positionDefinitions").isJsonArray()) {
            return positionDefinitionsObject;
        }
        for (final JsonElement element : copy.getAsJsonArray("positionDefinitions")) {
            if (!element.isJsonObject()) {
                continue;
            }
            final JsonObject definition = element.getAsJsonObject();
            final JsonElement positions = definition.get("positions");
            final JsonElement positionsFlipped = definition.get("positionsFlipped");
            definition.add("positions", negateZ(positionsFlipped));
            definition.add("positionsFlipped", negateZ(positions));
        }
        return copy;
    }

    public static JsonObject filterPartsForModel(JsonObject modelPropertiesObject, JsonObject modelObject) {
        if (modelPropertiesObject == null || modelObject == null || !modelObject.has("modelResource")
                || !modelPropertiesObject.has("parts") || !modelPropertiesObject.get("parts").isJsonArray()) {
            return modelPropertiesObject;
        }
        final JsonArray parts = modelPropertiesObject.getAsJsonArray("parts");
        if (!hasModelScopedNames(parts)) {
            return modelPropertiesObject;
        }
        final String modelResource = modelObject.get("modelResource").getAsString();
        final String modelFileName = modelResource.substring(modelResource.lastIndexOf('/') + 1);
        final JsonObject copy = modelPropertiesObject.deepCopy();
        final JsonArray filteredParts = new JsonArray();
        for (final JsonElement element : copy.getAsJsonArray("parts")) {
            if (!element.isJsonObject()) {
                continue;
            }
            final JsonObject part = element.getAsJsonObject();
            final JsonElement names = part.get("names");
            if (names == null || !names.isJsonArray()) {
                filteredParts.add(part);
                continue;
            }
            final JsonArray filteredNames = new JsonArray();
            for (final JsonElement nameElement : names.getAsJsonArray()) {
                final String name = nameElement.getAsString();
                final String scope = modelScopeOf(name);
                if (scope == null) {
                    filteredNames.add(name);
                } else if (scope.equals(modelFileName)) {
                    String localName = name.substring(scope.length() + 1);
                    if (localName.startsWith(REVERSED_SCOPE)) {
                        localName = localName.substring(REVERSED_SCOPE.length());
                    }
                    filteredNames.add(localName);
                }
            }
            if (filteredNames.size() > 0) {
                part.add("names", filteredNames);
                filteredParts.add(part);
            }
        }
        copy.add("parts", filteredParts);
        return copy;
    }

    private static final String REVERSED_SCOPE = "reversed/";

    private static boolean hasModelScopedNames(JsonArray parts) {
        for (final JsonElement element : parts) {
            if (!element.isJsonObject()) {
                continue;
            }
            final JsonElement names = element.getAsJsonObject().get("names");
            if (names == null || !names.isJsonArray()) {
                continue;
            }
            for (final JsonElement nameElement : names.getAsJsonArray()) {
                if (modelScopeOf(nameElement.getAsString()) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String modelScopeOf(String name) {
        final int slash = name.indexOf('/');
        if (slash <= 0) {
            return null;
        }
        final String scope = name.substring(0, slash).toLowerCase(java.util.Locale.ROOT);
        return scope.endsWith(".obj") || scope.endsWith(".bbmodel") ? name.substring(0, slash) : null;
    }

    private static JsonArray negateZ(JsonElement source) {
        final JsonArray result = new JsonArray();
        if (source == null || !source.isJsonArray()) {
            return result;
        }
        for (final JsonElement element : source.getAsJsonArray()) {
            if (element.isJsonObject()) {
                final JsonObject position = element.getAsJsonObject().deepCopy();
                if (position.has("z")) {
                    position.addProperty("z", -position.get("z").getAsDouble());
                }
                result.add(position);
            } else {
                result.add(element);
            }
        }
        return result;
    }

    private static int matchesFilter(String[] filters, int currentCar, int totalCars) {
        int strength = filters.length == 0 ? 1 : 0;
        for (final String filter : filters) {
            if (filter.isEmpty()) {
                continue;
            }
            if (filter.contains("%")) {
                try {
                    final String[] filterSplit = filter.split("\\+");
                    final int multiple = Integer.parseInt(filterSplit[0].replace("%", ""));
                    final int additional = filterSplit.length == 1 ? 0 : Integer.parseInt(filterSplit[1]);
                    if ((currentCar + 1 + additional) % multiple == 0) {
                        strength = 2;
                    }
                } catch (Exception ignored) {
                }
            } else {
                try {
                    final int car = Integer.parseInt(filter);
                    if (car == currentCar + 1 || car == currentCar - totalCars) {
                        return 3;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return strength;
    }

    private record State(ArrayDeque<Boolean> flags, boolean carReversed) {
    }
}
