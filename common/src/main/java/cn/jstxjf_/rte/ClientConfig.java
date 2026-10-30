package cn.jstxjf_.rte;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.render.ShadersModHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientConfig {

    private static Path path;

    public static boolean enableOptimization = true;
    public static boolean enableRailDeform = true;
    public static boolean enableRail3D = true;
    public static boolean enableRailRender = true;
    public static boolean enableTrainRender = true;
    public static boolean enableTrainSound = true;
    public static boolean enableSmoke = true;
    public static boolean enableRolling = true;
    public static int railDistanceRendererInterval = 5;
    public static int railDistanceRendererMaxDistanceSqr = 256;
    public static boolean hideRidingTrain = false;
    public static boolean translucentSort = true;
    public static boolean gpuTranslucentRendering = true;
    public static boolean shaderPackGpuRendering = true;
    public static boolean instancedVehicleRendering = true;
    public static boolean enableBbModelPreload = false;
    public static int modelDedupeFaceLimit = 2000;
    public static int mtrMeshDedupeFaceLimit = 2000;
    public static int scriptTextureLimit = 512;
    public static boolean traceDoorSides = false;
    public static boolean traceStationTiming = false;
    public static int maxTextureSize = 2048;
    public static boolean asyncVehicleModelLoading = true;
    public static int scriptTextureHeapBudgetMb = defaultTextureHeapBudgetMb();

    private static int defaultTextureHeapBudgetMb() {
        final long maxHeapMb = Runtime.getRuntime().maxMemory() / (1024L * 1024L);
        return (int) Math.max(192, Math.min(384, maxHeapMb / 16));
    }
    public static boolean acceptLegacyPackFormats = true;
    public static boolean legacyDefaultTextureFallback = true;
    public static boolean autoGangwayPolicy = true;
    public static String legacyFlippedPositionFix = "off";
    public static boolean legacyVehicleTakeover = false;
    public static int preloadHeapCeilingPercent = 35;
    public static boolean fixLegacyPlugDoors = false;
    public static int bbModelPreloadLimit = 24;
    public static boolean vehicleMeshDiskCache = true;
    public static int vehicleMeshCacheMaxMb = 2048;
    public static int preloadIdleReleaseMinutes = 10;
    public static boolean useEditBoxSetRailRolling = true;
    public static int jcmJsChoice = 0;
    public static boolean enableRenderDiagnostics = false;
    public static boolean scriptDebugOverlay = false;
    public static boolean vanillaVehicleMode = false;
    public static boolean legacyVariationCarFilter = true;
    public static String scriptEngine = "auto";
    public static boolean closeCouplingGap = false;
    public static boolean overrideMtrScreens = false;
    public static boolean compatAsked = false;
    public static boolean rteBrushEditing = true;
    public static boolean reportMissingResourcePacks = false;
    public static double legacyCouplingGap = 0.0;
    public static boolean builtinGangway = true;
    public static boolean forceGangwayOnAllCars = true;
    public static boolean mirrorReversedDoorways = false;
    public static boolean legacyDoorways = false;
    public static boolean legacyWalkways = false;
    public static boolean trainCollision = false;
    public static double trainCollisionHeight = 4.0;

    public static final EyecandyScreenGroup eyecandyScreenGroup = new EyecandyScreenGroup();
    public static final RollAnglesListEntryGroup rollAnglesListEntryGroup = new RollAnglesListEntryGroup();
    public static final Entry directNodeScreenGroup = new Entry("direct_node_screen", "rotation", 0, 180F, 180, 1, 1);
    private static final Map<String, String> customConfigs = new HashMap<>();
    private static final Map<String, ConfigResponder> customResponders = new HashMap<>();

    public static void load(Path path) {
        ClientConfig.path = path;
        if (!Files.exists(path)) {
            save();
            return;
        }
        try {
            JsonObject configObject = RTE.JSON_PARSER.parse(Files.readString(path)).getAsJsonObject();
            enableOptimization = !getOrDefault(configObject, "shaderCompatMode", JsonElement::getAsBoolean, false);
            enableRailDeform = getOrDefault(configObject, "enableRailDeform", JsonElement::getAsBoolean, true);
            enableRail3D = getOrDefault(configObject, "enableRail3D", JsonElement::getAsBoolean, true);
            enableRailRender = getOrDefault(configObject, "enableRailRender", JsonElement::getAsBoolean, true);
            enableTrainRender = getOrDefault(configObject, "enableTrainRender", JsonElement::getAsBoolean, true);
            enableTrainSound = getOrDefault(configObject, "enableTrainSound", JsonElement::getAsBoolean, true);
            enableSmoke = getOrDefault(configObject, "enableSmoke", JsonElement::getAsBoolean, true);
            enableRolling = getOrDefault(configObject, "enableRolling", JsonElement::getAsBoolean, true);
            railDistanceRendererInterval = clamp(getOrDefault(configObject, "railDistanceRendererInterval", JsonElement::getAsInt, 5), 1, 120);
            railDistanceRendererMaxDistanceSqr = clamp(getOrDefault(configObject, "railDistanceRendererMaxDistanceSqr", JsonElement::getAsInt, 256), 0, 16_777_216);
            hideRidingTrain = getOrDefault(configObject, "hideRidingTrain", JsonElement::getAsBoolean, false);
            translucentSort = getOrDefault(configObject, "translucentSort", JsonElement::getAsBoolean, true);
            gpuTranslucentRendering = getOrDefault(configObject, "gpuTranslucentRendering", JsonElement::getAsBoolean, true);
            shaderPackGpuRendering = getOrDefault(configObject, "shaderPackGpuRendering", JsonElement::getAsBoolean, true);
            instancedVehicleRendering = getOrDefault(configObject, "instancedVehicleRendering", JsonElement::getAsBoolean, true);
            enableBbModelPreload = getOrDefault(configObject, "enableBbModelPreload", JsonElement::getAsBoolean, false);
            modelDedupeFaceLimit = clamp(getOrDefault(configObject, "modelDedupeFaceLimit", JsonElement::getAsInt, 2000), 0, 2_000_000);
            mtrMeshDedupeFaceLimit = getOrDefault(configObject, "mtrMeshDedupeFaceLimit", JsonElement::getAsInt, 2000);
            scriptTextureLimit = getOrDefault(configObject, "scriptTextureLimit", JsonElement::getAsInt, 512);
            traceDoorSides = getOrDefault(configObject, "traceDoorSides", JsonElement::getAsBoolean, false);
            traceStationTiming = getOrDefault(configObject, "traceStationTiming", JsonElement::getAsBoolean, false);
            maxTextureSize = getOrDefault(configObject, "maxTextureSize", JsonElement::getAsInt, 2048);
            asyncVehicleModelLoading = getOrDefault(configObject, "asyncVehicleModelLoading", JsonElement::getAsBoolean, true);
            scriptTextureHeapBudgetMb = clamp(getOrDefault(configObject, "scriptTextureHeapBudgetMb", JsonElement::getAsInt, defaultTextureHeapBudgetMb()), 128, 4096);
            acceptLegacyPackFormats = getOrDefault(configObject, "acceptLegacyPackFormats", JsonElement::getAsBoolean, true);
            legacyDefaultTextureFallback = getOrDefault(configObject, "legacyDefaultTextureFallback", JsonElement::getAsBoolean, true);
            autoGangwayPolicy = getOrDefault(configObject, "autoGangwayPolicy", JsonElement::getAsBoolean, true);
            legacyFlippedPositionFix = getOrDefault(configObject, "legacyFlippedPositionFix", JsonElement::getAsString, "off");
            legacyVehicleTakeover = getOrDefault(configObject, "legacyVehicleTakeover", JsonElement::getAsBoolean, false);
            preloadHeapCeilingPercent = clamp(getOrDefault(configObject, "preloadHeapCeilingPercent", JsonElement::getAsInt, 35), 5, 80);
            fixLegacyPlugDoors = getOrDefault(configObject, "fixLegacyPlugDoorX", JsonElement::getAsBoolean, false);
            bbModelPreloadLimit = clamp(getOrDefault(configObject, "bbModelPreloadLimit", JsonElement::getAsInt, 24), 0, 4096);
            vehicleMeshDiskCache = getOrDefault(configObject, "vehicleMeshDiskCache", JsonElement::getAsBoolean, true);
            vehicleMeshCacheMaxMb = clamp(getOrDefault(configObject, "vehicleMeshCacheMaxMb", JsonElement::getAsInt, 2048), 64, 65536);
            preloadIdleReleaseMinutes = clamp(getOrDefault(configObject, "preloadIdleReleaseMinutes", JsonElement::getAsInt, 10), 0, 1440);
            useEditBoxSetRailRolling = getOrDefault(configObject, "useEditBoxSetRailRolling", JsonElement::getAsBoolean, true);
            jcmJsChoice = clamp(getOrDefault(configObject, "jcmJsChoice", JsonElement::getAsInt, 0), 0, 3);
            compatAsked = getOrDefault(configObject, "compatAsked", JsonElement::getAsBoolean, false);
            final java.util.Map<String, String> compatOwners = new java.util.LinkedHashMap<>();
            if (configObject.has("compat") && configObject.get("compat").isJsonObject()) {
                final JsonObject compatObject = configObject.getAsJsonObject("compat");
                for (Map.Entry<String, JsonElement> entry : compatObject.entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        compatOwners.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            }
            cn.jstxjf_.rte.util.CompatArbiter.restore(compatOwners);
            cn.jstxjf_.rte.util.CompatArbiter.adoptLegacyVehicleChoice();
            final java.util.Map<String, String> engineOverrides = new java.util.LinkedHashMap<>();
            if (configObject.has("scriptEngineOverrides") && configObject.get("scriptEngineOverrides").isJsonObject()) {
                final JsonObject overrideObject = configObject.getAsJsonObject("scriptEngineOverrides");
                for (Map.Entry<String, JsonElement> entry : overrideObject.entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        engineOverrides.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            }
            cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy.restore(engineOverrides);
            enableRenderDiagnostics = getOrDefault(configObject, "enableRenderDiagnostics", JsonElement::getAsBoolean, false);
            scriptDebugOverlay = getOrDefault(configObject, "scriptDebugOverlay", JsonElement::getAsBoolean, false);
            vanillaVehicleMode = getOrDefault(configObject, "vanillaVehicleMode", JsonElement::getAsBoolean, false);
            legacyVariationCarFilter = getOrDefault(configObject, "legacyVariationCarFilter", JsonElement::getAsBoolean, true);
            scriptEngine = normalizeScriptEngine(getOrDefault(configObject, "scriptEngine", JsonElement::getAsString, "auto"));
            closeCouplingGap = getOrDefault(configObject, "closeCouplingGap", JsonElement::getAsBoolean, false);
            overrideMtrScreens = getOrDefault(configObject, "overrideMtrScreens", JsonElement::getAsBoolean, false);
            rteBrushEditing = getOrDefault(configObject, "rteBrushEditing", JsonElement::getAsBoolean, true);
            reportMissingResourcePacks = getOrDefault(configObject, "reportMissingResourcePacks", JsonElement::getAsBoolean, false);
            legacyCouplingGap = clamp(getOrDefault(configObject, "legacyCouplingGap", JsonElement::getAsDouble, 0.0), 0.0, 4.0);
            builtinGangway = getOrDefault(configObject, "builtinGangway", JsonElement::getAsBoolean, true);
            forceGangwayOnAllCars = getOrDefault(configObject, "forceGangwayOnAllCars", JsonElement::getAsBoolean, true);
            mirrorReversedDoorways = getOrDefault(configObject, "mirrorReversedDoorways", JsonElement::getAsBoolean, false);
            legacyDoorways = getOrDefault(configObject, "legacyDoorways", JsonElement::getAsBoolean, false);
            legacyWalkways = getOrDefault(configObject, "legacyWalkways", JsonElement::getAsBoolean, false);
            trainCollision = getOrDefault(configObject, "trainCollision", JsonElement::getAsBoolean, false);
            trainCollisionHeight = clamp(getOrDefault(configObject, "trainCollisionHeight", JsonElement::getAsDouble, 4.0), 1.0, 8.0);

            eyecandyScreenGroup.init(configObject);
            directNodeScreenGroup.init(configObject);
            rollAnglesListEntryGroup.init(configObject);
            customConfigs.clear();
            if (configObject.has("custom")) {
                JsonObject customObject = configObject.getAsJsonObject("custom");
                for (Map.Entry<String, JsonElement> entry : customObject.entrySet()) {
                    customConfigs.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
            if (vanillaVehicleMode) {
                builtinGangway = false;
                forceGangwayOnAllCars = false;
                mirrorReversedDoorways = false;
                legacyDoorways = false;
                closeCouplingGap = false;
                legacyVariationCarFilter = false;
                legacyWalkways = false;
                RTE.LOGGER.info("[Config] vanillaVehicleMode is on: builtinGangway / mirrorReversedDoorways / closeCouplingGap / legacyVariationCarFilter forced off");
            }
        } catch (Exception ex) {
            RTE.LOGGER.warn("Failed loading client config:", ex);
            save();
        }
    }

    static <T> T getOrDefault(JsonObject jsonObject, String key, Function<JsonElement, T> getter, T defaultValue) {
        if (jsonObject == null) return defaultValue;
        if (jsonObject.has(key)) {
            return getter.apply(jsonObject.get(key));
        } else {
            return defaultValue;
        }
    }

    static <T> T getOrDefault(JsonArray jsonArray, int index, Function<JsonElement, T> getter, T defaultValue) {
        if (jsonArray == null) return defaultValue;
        if (jsonArray.size() > index) {
            return getter.apply(jsonArray.get(index));
        } else {
            return defaultValue;
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static String normalizeScriptEngine(String value) {
        if (value == null) {
            return "auto";
        }
        final String normalized = value.trim().toLowerCase(java.util.Locale.ROOT);
        if (normalized.equals("graal") || normalized.equals("graaljs")) {
            return "graal";
        }
        return normalized.equals("auto") ? "auto" : "rhino";
    }

    public static int getRailRenderLevel() {
        if (!useRenderOptimization()) {
            return enableRailRender ? 1 : 0;
        } else {
            return enableRailRender
                    ? (enableRail3D ? (canInstance() && !enableRailDeform ? 3 : 2) : 1)
                    : 0;
        }
    }

    public static boolean getEnableRailDeform() {
        return enableRailDeform;
    }

    public static boolean useRenderOptimization() {
        return enableOptimization && canDrawWithBuffer();
    }

    private static boolean canInstance() {
        return ShadersModHandler.canInstance();
    }

    private static boolean canDrawWithBuffer() {
        return ShadersModHandler.canDrawWithBuffer();
    }

    public static void save() {
        try {
            if (path == null) return;
            JsonObject configObject = new JsonObject();
            configObject.addProperty("shaderCompatMode", !enableOptimization);
            configObject.addProperty("jcmJsChoice", jcmJsChoice);
            configObject.addProperty("enableRenderDiagnostics", enableRenderDiagnostics);
            configObject.addProperty("scriptDebugOverlay", scriptDebugOverlay);
            configObject.addProperty("enableRailDeform", enableRailDeform);
            configObject.addProperty("enableRail3D", enableRail3D);
            configObject.addProperty("enableRailRender", enableRailRender);
            configObject.addProperty("enableTrainRender", enableTrainRender);
            configObject.addProperty("enableTrainSound", enableTrainSound);
            configObject.addProperty("enableSmoke", enableSmoke);
            configObject.addProperty("enableRolling", enableRolling);
            configObject.addProperty("railDistanceRendererInterval", railDistanceRendererInterval);
            configObject.addProperty("railDistanceRendererMaxDistanceSqr", railDistanceRendererMaxDistanceSqr);
            configObject.addProperty("hideRidingTrain", hideRidingTrain);
            configObject.addProperty("translucentSort", translucentSort);
            configObject.addProperty("gpuTranslucentRendering", gpuTranslucentRendering);
            configObject.addProperty("shaderPackGpuRendering", shaderPackGpuRendering);
            configObject.addProperty("instancedVehicleRendering", instancedVehicleRendering);
            configObject.addProperty("enableBbModelPreload", enableBbModelPreload);
            configObject.addProperty("modelDedupeFaceLimit", modelDedupeFaceLimit);
            configObject.addProperty("mtrMeshDedupeFaceLimit", mtrMeshDedupeFaceLimit);
            configObject.addProperty("scriptTextureLimit", scriptTextureLimit);
            configObject.addProperty("traceDoorSides", traceDoorSides);
            configObject.addProperty("traceStationTiming", traceStationTiming);
            configObject.addProperty("maxTextureSize", maxTextureSize);
            configObject.addProperty("asyncVehicleModelLoading", asyncVehicleModelLoading);
            configObject.addProperty("scriptTextureHeapBudgetMb", scriptTextureHeapBudgetMb);
            configObject.addProperty("acceptLegacyPackFormats", acceptLegacyPackFormats);
            configObject.addProperty("legacyDefaultTextureFallback", legacyDefaultTextureFallback);
            configObject.addProperty("autoGangwayPolicy", autoGangwayPolicy);
            configObject.addProperty("legacyFlippedPositionFix", legacyFlippedPositionFix);
            configObject.addProperty("legacyVehicleTakeover", legacyVehicleTakeover);
            configObject.addProperty("preloadHeapCeilingPercent", preloadHeapCeilingPercent);
            configObject.addProperty("fixLegacyPlugDoorX", fixLegacyPlugDoors);
            configObject.addProperty("bbModelPreloadLimit", bbModelPreloadLimit);
            configObject.addProperty("vehicleMeshDiskCache", vehicleMeshDiskCache);
            configObject.addProperty("vehicleMeshCacheMaxMb", vehicleMeshCacheMaxMb);
            configObject.addProperty("preloadIdleReleaseMinutes", preloadIdleReleaseMinutes);
            configObject.addProperty("useEditBoxSetRailRolling", useEditBoxSetRailRolling);
            configObject.addProperty("vanillaVehicleMode", vanillaVehicleMode);
            configObject.addProperty("legacyVariationCarFilter", legacyVariationCarFilter);
            configObject.addProperty("scriptEngine", scriptEngine);
            configObject.addProperty("closeCouplingGap", closeCouplingGap);
            configObject.addProperty("overrideMtrScreens", overrideMtrScreens);
            configObject.addProperty("rteBrushEditing", rteBrushEditing);
            configObject.addProperty("reportMissingResourcePacks", reportMissingResourcePacks);
            configObject.addProperty("compatAsked", compatAsked);
            final JsonObject compatObject = new JsonObject();
            for (Map.Entry<String, String> entry : cn.jstxjf_.rte.util.CompatArbiter.snapshot().entrySet()) {
                compatObject.addProperty(entry.getKey(), entry.getValue());
            }
            configObject.add("compat", compatObject);
            final JsonObject engineOverrideObject = new JsonObject();
            for (Map.Entry<String, String> entry : cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy.snapshot().entrySet()) {
                engineOverrideObject.addProperty(entry.getKey(), entry.getValue());
            }
            configObject.add("scriptEngineOverrides", engineOverrideObject);
            configObject.addProperty("legacyCouplingGap", legacyCouplingGap);
            configObject.addProperty("builtinGangway", builtinGangway);
            configObject.addProperty("forceGangwayOnAllCars", forceGangwayOnAllCars);
            configObject.addProperty("mirrorReversedDoorways", mirrorReversedDoorways);
            configObject.addProperty("legacyDoorways", legacyDoorways);
            configObject.addProperty("legacyWalkways", legacyWalkways);
            configObject.addProperty("trainCollision", trainCollision);
            configObject.addProperty("trainCollisionHeight", trainCollisionHeight);
            eyecandyScreenGroup.save(configObject);
            directNodeScreenGroup.save(configObject);
            rollAnglesListEntryGroup.save(configObject);
            JsonObject customObject = new JsonObject();
            for (Map.Entry<String, String> entry : customConfigs.entrySet()) {
                customObject.addProperty(entry.getKey(), entry.getValue());
            }
            configObject.add("custom", customObject);
            cn.jstxjf_.rte.util.AtomicFileWriter.writeString(path,
                new GsonBuilder().setPrettyPrinting().create().toJson(configObject));
        } catch (Exception ex) {
            RTE.LOGGER.warn("Failed saving client config:", ex);
        }
    }

    public static void load() {
        load(Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("rte.json"));
    }

    public static void init() {
        load();
    }

    public static void register(ConfigResponder responder) {
        ConfigResponder.registerInto(customConfigs, customResponders, responder);
    }

    public static String get(String key) {
        return customConfigs.get(key);
    }

    public static Map<String, String> getCustomConfigs() {
        return customConfigs;
    }

    public static Map<String, ConfigResponder> getCustomResponders() {
        return customResponders;
    }

    public static void clearCustomResponders() {
        customResponders.clear();
    }

    public interface ConfigGroup {
        String key();
        void init(JsonObject configObject);
        void save(JsonObject configObject);
    }

    public static abstract class EntryGroup implements ConfigGroup {
        public final String key;
        public final Entry[] entries;

        public EntryGroup(String key, Entry... entries) {
            this.key = key;
            this.entries = entries;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public void init(JsonObject configObject) {
            JsonObject entryObject = configObject.getAsJsonObject(key());
            if (entryObject == null) return;
            for (Entry entry : entries) {
                entry.init(entryObject);
            }
        }

        @Override
        public void save(JsonObject configObject) {
            JsonObject entryObject = new JsonObject();
            for (Entry entry : entries) {
                entry.save(entryObject);
            }
            configObject.add(key(), entryObject);
        }

    }

    public static class RollAnglesListEntryGroup extends EntryGroup {
        public RollAnglesListEntryGroup() {
            super(
                "roll_angles_list_entry",
                new Entry("rotation", -20F, 20F, 80, 1, 2),
                new Entry("offset", 0F, 1F, 10000, 1, 2)
            );
        }
    }

    public static class EyecandyScreenGroup extends EntryGroup {
        public EyecandyScreenGroup() {
            super(
                "eyecandy_screen",
                new Entry("translation", -1.0F, 1.0F, 40, 1, 3),
                new Entry("rotation", -180F, 180F, 36, 1, 3),
                new Entry("scale", -2.0F, 2.0F, 40, 1, 3)
            );
        }
    }

    public static class Entry implements ConfigGroup {
        public float defaultMin, defaultMax;
        public int defaultStep, defaultMode, quantity;

        public float min, max;
        public int step;
        public int[] modes;
        public String key, tooltipKey;

        public Entry(String key, float defaultMin, float defaultMax, int defaultStep, int defaultMode, int quantity) {
            this(key, key, defaultMin, defaultMax, defaultStep, defaultMode, quantity);
        }

        public Entry(String key, String tooltipKey, float defaultMin, float defaultMax, int defaultStep, int defaultMode, int quantity) {
            this.defaultMin = defaultMin;
            this.defaultMax = defaultMax;
            this.defaultStep = defaultStep;
            this.defaultMode = defaultMode;
            this.quantity = quantity;
            min = defaultMin;
            max = defaultMax;
            step = defaultStep;
            modes = new int[quantity];
            Arrays.fill(modes, defaultMode);
            this.key = key;
            this.tooltipKey = tooltipKey;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public void init(JsonObject configObject) {
            JsonObject entryObject = configObject.getAsJsonObject(key);
            if (entryObject == null) return;
            min = getOrDefault(entryObject, "min", JsonElement::getAsFloat, min);
            max = getOrDefault(entryObject, "max", JsonElement::getAsFloat, max);
            step = getOrDefault(entryObject, "step", JsonElement::getAsInt, step);
            JsonArray modesArray = entryObject.getAsJsonArray("modes");
            if (modesArray == null) return;
            int[] modes = new int[quantity];
            for (int i = 0; i < quantity; i++) {
                modes[i] = getOrDefault(modesArray, i, JsonElement::getAsInt, defaultMode);
            }
            this.modes = modes;
        }

        @Override
        public void save(JsonObject configObject) {
            JsonObject entryObject = new JsonObject();
            entryObject.addProperty("min", min);
            entryObject.addProperty("max", max);
            entryObject.addProperty("step", step);
            JsonArray modesArray = new JsonArray();
            for (int i = 0; i < quantity; i++) {
                modesArray.add(modes[i]);
            }
            entryObject.add("modes", modesArray);
            configObject.add(key, entryObject);
        }

    }
}
