package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.render.integration.MtrModelRegistryUtil;
import cn.jstxjf_.rte.scripting.ScriptHolderBase;
import cn.jstxjf_.rte.scripting.ScriptHolderClient;
import cn.jstxjf_.rte.gfxext.util.ResourceUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class ScriptedCustomTrains {

    private static final String CUSTOM_RESOURCES = "mtr_custom_resources.json";
    private static final String CUSTOM_RESOURCES_PENDING_MIGRATION = "mtr_custom_resources_pending_migration.json";
    private static final String CUSTOM_TRAIN_ID_PREFIX = "mtr_custom_train_";
    private static final String[] MTR4_LEGACY_VARIATIONS = {"trailer", "cab_1", "cab_2", "cab_3"};
    private static final Map<String, String> MTR4_VARIATION_TO_DEFINITION_VARIATIONS = Map.of(
        "trailer", "trailer",
        "cab_1", "cab_1",
        "cab_2", "cab_2",
        "cab_3", "cab_3"
    );
    private static final Map<String, String> MTR4_PROPERTY_TO_DEFINITION_VARIATIONS = Map.of(
        "common", "trailer",
        "head_1", "cab_1",
        "head_2", "cab_2",
        "end_1", "cab_2",
        "end_2", "cab_3"
    );
    public static final Map<String, ScriptHolderBase> SCRIPTED_TRAINS = new LinkedHashMap<>();

    private static final Map<ResourceLocation, String> LEGACY_RAW_PATHS = new java.util.concurrent.ConcurrentHashMap<>();

    public static String rawPathOf(ResourceLocation sanitized) {
        return LEGACY_RAW_PATHS.get(sanitized);
    }

    public static ResourceLocation parseLegacyLocation(String raw) {
        try {
            return new ResourceLocation(raw);
        } catch (Exception e) {
            final String lowered = raw.toLowerCase(java.util.Locale.ROOT).trim();
            try {
                final ResourceLocation location = new ResourceLocation(lowered);
                LEGACY_RAW_PATHS.put(location, raw.trim());
                RTE.LOGGER.info("[ScriptedCustomTrains] Legacy resource path '{}' contains invalid characters, using '{}' instead", raw, location);
                return location;
            } catch (Exception e2) {
                final int colon = lowered.indexOf(':');
                final String namespace = colon > 0 ? lowered.substring(0, colon) : "minecraft";
                final String path = (colon > 0 ? lowered.substring(colon + 1) : lowered).replaceAll("[^a-z0-9/._-]", "_");
                final ResourceLocation location = new ResourceLocation(namespace.replaceAll("[^a-z0-9._-]", "_"), path);
                LEGACY_RAW_PATHS.put(location, raw.trim());
                RTE.LOGGER.warn("[ScriptedCustomTrains] Legacy resource path '{}' heavily sanitized to '{}'", raw, location);
                return location;
            }
        }
    }

    public static void init(ResourceManager resourceManager) {
        SCRIPTED_TRAINS.clear();
        GangwayPolicy.reset();
        LegacyVehicleRegistry.reset();
        if (resourceManager == null) return;
        if (cn.jstxjf_.rte.util.JcmCompat.shouldDisableRteJs()) {
            RTE.LOGGER.info("[ScriptedCustomTrains] RTE JS disabled by user choice (JCM detected), skipping custom train scripts");
            return;
        }

        List<Map.Entry<ResourceLocation, Resource>> officialResources = getResourceStacks(resourceManager, CUSTOM_RESOURCES);
        List<Map.Entry<ResourceLocation, Resource>> pendingResources = getResourceStacks(resourceManager, CUSTOM_RESOURCES_PENDING_MIGRATION);

        Set<String> loadedTrainIds = new LinkedHashSet<>();
        loadCustomTrains(resourceManager, officialResources, loadedTrainIds, false);
        loadCustomTrains(resourceManager, pendingResources, loadedTrainIds, true);

        GangwayPolicy.report();
        LegacyVehicleRegistry.report();
        RTE.LOGGER.info("[ScriptedCustomTrains] namespaces={} officialStacks={} pendingStacks={} loadedTrainIds={} aliasCount={}",
            resourceManager.getNamespaces().size(), officialResources.size(), pendingResources.size(),
            loadedTrainIds.size(), SCRIPTED_TRAINS.size());
        RTE.LOGGER.debug("[ScriptedCustomTrains] loaded ids={}", loadedTrainIds);
    }

    private static void rte$recordLegacyVehicle(ResourceManager resourceManager, String trainId, JsonObject vehicle) {
        try {
            if (!vehicle.has("model_properties")) {
                return;
            }
            final ResourceLocation location = parseLegacyLocation(vehicle.get("model_properties").getAsString());
            final String text = ResourceUtil.readResource(resourceManager, location);
            if (text == null || text.isEmpty()) {
                return;
            }
            LegacyVehicleRegistry.record(trainId, vehicle,
                JsonParser.parseString(text).getAsJsonObject());
        } catch (Exception ignored) {
        }
    }

    private static List<Map.Entry<ResourceLocation, Resource>> getResourceStacks(ResourceManager resourceManager, String path) {
        List<Map.Entry<ResourceLocation, Resource>> resources = new ArrayList<>();
        resourceManager.getNamespaces().stream()
            .sorted()
            .forEach(namespace -> {
                ResourceLocation location = new ResourceLocation(namespace, path);
                resourceManager.getResourceStack(location).forEach(resource ->
                    resources.add(new AbstractMap.SimpleImmutableEntry<>(location, resource))
                );
            });
        return resources;
    }

    private static void loadMtr4VehicleScripts(ResourceManager resourceManager, JsonObject root, Set<String> loadedTrainIds) {
        final Map<String, List<String>> scriptIdToLocations = new LinkedHashMap<>();
        try {
            for (JsonElement element : root.get("vehicleScripts").getAsJsonArray()) {
                final JsonObject scriptEntry = element.getAsJsonObject();
                if (!scriptEntry.has("id") || !scriptEntry.has("scriptLocations")) {
                    continue;
                }
                final List<String> locations = new ArrayList<>();
                for (JsonElement location : scriptEntry.get("scriptLocations").getAsJsonArray()) {
                    locations.add(location.getAsString());
                }
                if (!locations.isEmpty()) {
                    scriptIdToLocations.put(scriptEntry.get("id").getAsString(), locations);
                }
            }
        } catch (Exception ex) {
            RTE.LOGGER.error("[ScriptedCustomTrains] Failed reading vehicleScripts", ex);
            return;
        }
        if (scriptIdToLocations.isEmpty()) {
            return;
        }

        for (JsonElement element : root.get("vehicles").getAsJsonArray()) {
            final String vehicleId;
            final List<String> locations;
            try {
                final JsonObject vehicle = element.getAsJsonObject();
                if (!vehicle.has("id") || !vehicle.has("scriptId")) {
                    continue;
                }
                vehicleId = vehicle.get("id").getAsString();
                locations = scriptIdToLocations.get(vehicle.get("scriptId").getAsString());
            } catch (Exception ex) {
                continue;
            }
            if (locations == null || loadedTrainIds.contains(vehicleId)) {
                continue;
            }
            try {
                final cn.jstxjf_.rte.scripting.engine.ScriptEngineType configuredEngine =
                    cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy.resolve(
                        vehicleId, cn.jstxjf_.rte.ClientConfig.scriptEngine, "");
                final ScriptHolderBase scriptContext = new ScriptHolderClient(configuredEngine);
                final Map<ResourceLocation, String> scripts = new LinkedHashMap<>();
                for (String location : locations) {
                    final ResourceLocation scriptLocation = parseLegacyLocation(location);
                    scripts.put(scriptLocation,
                        cn.jstxjf_.rte.scripting.util.ScriptResourceUtil.readScriptSource(resourceManager, scriptLocation));
                }
                scriptContext.load("Train " + vehicleId, "Train", resourceManager, scripts, new JsonObject(), vehicleId, "create", "render", "dispose", "use");
                registerScript(vehicleId, scriptContext);
                loadedTrainIds.add(vehicleId);
                RTE.LOGGER.debug("[ScriptedCustomTrains] MTR4 vehicleScript bound: {} -> {}", vehicleId, locations);
            } catch (Exception ex) {
                RTE.LOGGER.error("Reading MTR4 scripted vehicle: " + vehicleId, ex);
            }
        }
    }

    private static void loadCustomTrains(
        ResourceManager resourceManager,
        List<Map.Entry<ResourceLocation, Resource>> resources,
        Set<String> loadedTrainIds,
        boolean skipDuplicates
    ) {
        for (Map.Entry<ResourceLocation, Resource> entry : resources) {
            try (InputStream is = entry.getValue().open()) {
                JsonObject root = JsonParser.parseString(IOUtils.toString(is, StandardCharsets.UTF_8)).getAsJsonObject();
                if (root.has("vehicles") && root.has("vehicleScripts")) {
                    loadMtr4VehicleScripts(resourceManager, root, loadedTrainIds);
                }
                if (!root.has("custom_trains")) continue;

                root.get("custom_trains").getAsJsonObject().entrySet().forEach(trainEntry -> {
                    try {
                        JsonObject jsonObject = trainEntry.getValue().getAsJsonObject();
                        GangwayPolicy.record(trainEntry.getKey(), jsonObject);
                        rte$recordLegacyVehicle(resourceManager, trainEntry.getKey(), jsonObject);
                        if (!(jsonObject.has("script_files") || jsonObject.has("script_texts"))) return;

                        String trainId = trainEntry.getKey();
                        if (loadedTrainIds.contains(trainId)) {
                            return;
                        }

                        final String packEngineName = jsonObject.has("engine") ? jsonObject.get("engine").getAsString() : "";
                        ScriptHolderBase scriptContext = new ScriptHolderClient(
                            cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy.resolve(
                                trainEntry.getKey(), cn.jstxjf_.rte.ClientConfig.scriptEngine, packEngineName));
                        Map<ResourceLocation, String> scripts = new LinkedHashMap<>();
                        if (jsonObject.has("script_texts")) {
                            JsonArray scriptTexts = jsonObject.get("script_texts").getAsJsonArray();
                            for (int i = 0; i < scriptTexts.size(); i++) {
                                scripts.put(new ResourceLocation(entry.getKey().getNamespace(), "script_texts/" + trainId + "/" + i),
                                    scriptTexts.get(i).getAsString());
                            }
                        }
                        if (jsonObject.has("script_files")) {
                            JsonArray scriptFiles = jsonObject.get("script_files").getAsJsonArray();
                            for (int i = 0; i < scriptFiles.size(); i++) {
                                ResourceLocation scriptLocation = parseLegacyLocation(scriptFiles.get(i).getAsString());
                                scripts.put(scriptLocation,
                                    cn.jstxjf_.rte.scripting.util.ScriptResourceUtil.readScriptSource(resourceManager, scriptLocation));
                            }
                        }
                        scriptContext.load("Train " + trainEntry.getKey(), "Train", resourceManager, scripts, jsonObject, trainEntry.getKey(), "create", "render", "dispose", "use");
                        registerScript(trainId, scriptContext);
                        loadedTrainIds.add(trainId);
                    } catch (Exception ex) {
                        RTE.LOGGER.error("Reading scripted custom train: " + trainEntry.getKey(), ex);
                        MtrModelRegistryUtil.recordLoadingError("Failed loading scripted custom train " + trainEntry.getKey(), ex);
                    }
                });
            } catch (Exception ex) {
                RTE.LOGGER.error("Failed reading custom resources: " + entry.getKey(), ex);
                MtrModelRegistryUtil.recordLoadingError("Failed reading custom resources " + entry.getKey(), ex);
            }
        }
    }

    private static void registerScript(String trainId, ScriptHolderBase scriptContext) {
        final Set<String> aliases = getTrainIdAliases(trainId);
        aliases.forEach(alias -> SCRIPTED_TRAINS.put(alias, scriptContext));
    }

    public static Set<String> getTrainIdAliases(String trainId) {
        final Set<String> aliases = new LinkedHashSet<>();
        addTrainIdAlias(aliases, trainId);
        if (trainId == null) {
            return aliases;
        }
        final String lowerTrainId = trainId.toLowerCase(Locale.ROOT);
        addTrainIdAlias(aliases, lowerTrainId);
        getDefinitionBaseAliases(lowerTrainId).forEach(alias -> addTrainIdAlias(aliases, alias));
        getMtr4DefinitionAliases(lowerTrainId).forEach(alias -> addTrainIdAlias(aliases, alias));

        final int separatorIndex = lowerTrainId.indexOf(':');
        if (separatorIndex >= 0 && separatorIndex < lowerTrainId.length() - 1) {
            final String unnamespacedTrainId = lowerTrainId.substring(separatorIndex + 1);
            addTrainIdAlias(aliases, unnamespacedTrainId);
            getDefinitionBaseAliases(unnamespacedTrainId).forEach(alias -> addTrainIdAlias(aliases, alias));
            getMtr4DefinitionAliases(unnamespacedTrainId).forEach(alias -> addTrainIdAlias(aliases, alias));
        } else if (!lowerTrainId.isEmpty()) {
            addTrainIdAlias(aliases, "mtr:" + lowerTrainId);
        }

        if (lowerTrainId.startsWith(CUSTOM_TRAIN_ID_PREFIX)) {
            final String unprefixedTrainId = lowerTrainId.substring(CUSTOM_TRAIN_ID_PREFIX.length());
            if (!unprefixedTrainId.isEmpty()) {
                addTrainIdAlias(aliases, unprefixedTrainId);
                getDefinitionBaseAliases(unprefixedTrainId).forEach(alias -> addTrainIdAlias(aliases, alias));
                getMtr4DefinitionAliases(unprefixedTrainId).forEach(alias -> addTrainIdAlias(aliases, alias));
            }
        } else {
            final String customTrainId = CUSTOM_TRAIN_ID_PREFIX + lowerTrainId;
            addTrainIdAlias(aliases, customTrainId);
            for (String variation : MTR4_LEGACY_VARIATIONS) {
                addTrainIdAlias(aliases, customTrainId + "_" + variation);
            }
            getMtr4DefinitionAliases(customTrainId).forEach(alias -> addTrainIdAlias(aliases, alias));
        }
        return aliases;
    }

    private static Set<String> getDefinitionBaseAliases(String trainId) {
        final Set<String> aliases = new LinkedHashSet<>();
        final String marker = "@definition@_";
        final int markerIndex = trainId.indexOf(marker);
        if (markerIndex < 0) {
            return aliases;
        }
        final String base = trainId.substring(0, markerIndex);
        final String variation = trainId.substring(markerIndex + marker.length());
        addTrainIdAlias(aliases, base);
        addTrainIdAlias(aliases, CUSTOM_TRAIN_ID_PREFIX + base);
        addTrainIdAlias(aliases, base + "_" + variation);
        addTrainIdAlias(aliases, CUSTOM_TRAIN_ID_PREFIX + base + "_" + variation);
        MTR4_PROPERTY_TO_DEFINITION_VARIATIONS.forEach((propertySuffix, definitionVariation) -> {
            if (definitionVariation.equals(variation)) {
                addTrainIdAlias(aliases, base + "_" + propertySuffix);
                addTrainIdAlias(aliases, CUSTOM_TRAIN_ID_PREFIX + base + "_" + propertySuffix);
            }
        });
        return aliases;
    }

    private static void addTrainIdAlias(Set<String> aliases, String trainId) {
        if (trainId == null || trainId.isEmpty()) {
            return;
        }
        aliases.add(trainId);
        final String lowerTrainId = trainId.toLowerCase(Locale.ROOT);
        aliases.add(lowerTrainId);
        final int separatorIndex = lowerTrainId.indexOf(':');
        if (separatorIndex >= 0 && separatorIndex < lowerTrainId.length() - 1) {
            aliases.add(lowerTrainId.substring(separatorIndex + 1));
        }
    }

    private static Set<String> getMtr4DefinitionAliases(String trainId) {
        final Set<String> aliases = new LinkedHashSet<>();
        if (trainId.contains("@definition@")) {
            return aliases;
        }
        for (String variation : MTR4_LEGACY_VARIATIONS) {
            aliases.add(trainId + "@definition@_" + variation);
        }
        MTR4_VARIATION_TO_DEFINITION_VARIATIONS.forEach((legacySuffix, variation) -> {
            final String suffix = "_" + legacySuffix;
            if (trainId.endsWith(suffix)) {
                aliases.add(trainId.substring(0, trainId.length() - suffix.length()) + "@definition@_" + variation);
            }
        });
        MTR4_PROPERTY_TO_DEFINITION_VARIATIONS.forEach((propertySuffix, variation) -> {
            final String suffix = "_" + propertySuffix;
            if (trainId.endsWith(suffix)) {
                aliases.add(trainId.substring(0, trainId.length() - suffix.length()) + "@definition@_" + variation);
            }
        });
        return aliases;
    }

    public static ScriptHolderBase getScript(String trainId) {
        for (String alias : getTrainIdAliases(trainId)) {
            final ScriptHolderBase scriptHolder = SCRIPTED_TRAINS.get(alias);
            if (scriptHolder != null) {
                return scriptHolder;
            }
        }
        return null;
    }

    public static int getScriptCount() {
        return SCRIPTED_TRAINS.size();
    }

    public static Set<String> getRegisteredIdsSnapshot() {
        return new LinkedHashSet<>(SCRIPTED_TRAINS.keySet());
    }

    public static <T> T getOrDefault(JsonObject jsonObject, String key, T defaultValue, Function<JsonElement, T> function) {
        if (jsonObject.has(key)) {
            return function.apply(jsonObject.get(key));
        }
        return defaultValue;
    }
}
