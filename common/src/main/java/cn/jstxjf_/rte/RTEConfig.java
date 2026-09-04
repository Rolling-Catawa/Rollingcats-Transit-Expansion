package cn.jstxjf_.rte;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;

public class RTEConfig {

    private static Path path;

    public static boolean enableRailExtras = true;
    public static boolean enableAutoTangent = false;
    public static int manualNotchMultiplier = 1;
    public static boolean allowVehiclePathSync = true;
    public static boolean allowVehicleMarkerSync = true;
    public static boolean allowFullRailSync = true;
    public static boolean enableNetworkDiagnostics = false;

    public static void load(MinecraftServer server) {
        path = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("rte_config.json");
        cn.jstxjf_.rte.data.DepotRailRules.bindStorage(
            server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("rte_depot_rules.json"));
        if (!Files.exists(path)) {
            save();
            return;
        }
        try {
            JsonObject configObject = RTE.JSON_PARSER.parse(Files.readString(path)).getAsJsonObject();
            enableRailExtras = readBoolean(configObject, "enableRailExtras", true);
            enableAutoTangent = readBoolean(configObject, "enableAutoTangent", false);
            manualNotchMultiplier = configObject.has("manualNotchMultiplier")
                ? Math.max(1, Math.min(10, configObject.get("manualNotchMultiplier").getAsInt()))
                : 1;
            allowVehiclePathSync = readBoolean(configObject, "allowVehiclePathSync", true);
            allowVehicleMarkerSync = readBoolean(configObject, "allowVehicleMarkerSync", true);
            allowFullRailSync = readBoolean(configObject, "allowFullRailSync", true);
            enableNetworkDiagnostics = readBoolean(configObject, "enableNetworkDiagnostics", false);
        } catch (Exception ex) {
            RTE.LOGGER.warn("Failed loading RTE server config:", ex);
            save();
        }
    }

    public static void save() {
        try {
            if (path == null) return;
            JsonObject configObject = new JsonObject();
            configObject.addProperty("enableRailExtras", enableRailExtras);
            configObject.addProperty("enableAutoTangent", enableAutoTangent);
            configObject.addProperty("manualNotchMultiplier", manualNotchMultiplier);
            configObject.addProperty("allowVehiclePathSync", allowVehiclePathSync);
            configObject.addProperty("allowVehicleMarkerSync", allowVehicleMarkerSync);
            configObject.addProperty("allowFullRailSync", allowFullRailSync);
            configObject.addProperty("enableNetworkDiagnostics", enableNetworkDiagnostics);
            cn.jstxjf_.rte.util.AtomicFileWriter.writeString(path,
                new GsonBuilder().setPrettyPrinting().create().toJson(configObject));
        } catch (Exception ex) {
            RTE.LOGGER.warn("Failed saving RTE server config:", ex);
        }
    }

    private static boolean readBoolean(JsonObject configObject, String key, boolean fallback) {
        return configObject.has(key) ? configObject.get(key).getAsBoolean() : fallback;
    }
}
