package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class GangwayPolicy {

    private static final Set<String> SELF_HANDLED = Collections.synchronizedSet(new LinkedHashSet<>());
    private static final Set<String> LEGACY_FORMAT = Collections.synchronizedSet(new LinkedHashSet<>());
    private static final java.util.Map<String, String> CONNECTION_IDS =
        new java.util.concurrent.ConcurrentHashMap<>();

    private GangwayPolicy() {
    }

    public static void reset() {
        SELF_HANDLED.clear();
        LEGACY_FORMAT.clear();
        CONNECTION_IDS.clear();
        LOOKUP_CACHE.clear();
        LEGACY_CACHE.clear();
    }

    public static boolean isLegacyFormat(String trainId) {
        if (trainId == null || LEGACY_FORMAT.isEmpty()) {
            return false;
        }
        final Boolean cached = LEGACY_CACHE.get(trainId);
        if (cached != null) {
            return cached;
        }
        final boolean result = LEGACY_FORMAT.contains(trainId.toLowerCase(Locale.ROOT))
            || LEGACY_FORMAT.contains(normalise(trainId));
        LEGACY_CACHE.put(trainId, result);
        return result;
    }

    private static final java.util.Map<String, Boolean> LEGACY_CACHE =
        new java.util.concurrent.ConcurrentHashMap<>();

    public static void record(String trainId, JsonObject vehicle) {
        if (trainId == null || vehicle == null) {
            return;
        }
        LEGACY_FORMAT.add(trainId.toLowerCase(Locale.ROOT));
        rte$recordConnectionId(trainId, vehicle);
        if (!declaresOwnGangway(vehicle)) {
            return;
        }
        SELF_HANDLED.add(trainId.toLowerCase(Locale.ROOT));
    }

    private static void rte$recordConnectionId(String trainId, JsonObject vehicle) {
        if (!vehicle.has("gangway_connection_id")) {
            return;
        }
        try {
            final String id = vehicle.get("gangway_connection_id").getAsString();
            if (id != null && !id.trim().isEmpty()) {
                CONNECTION_IDS.put(trainId.toLowerCase(Locale.ROOT),
                    stripPng(id.trim().toLowerCase(Locale.ROOT)));
            }
        } catch (Exception ignored) {
        }
    }

    private static String stripPng(String value) {
        final int idx = value.indexOf(".png");
        return idx < 0 ? value : value.substring(0, idx);
    }

    public static String connectionIdOf(String trainId) {
        if (trainId == null || CONNECTION_IDS.isEmpty()) {
            return null;
        }
        final String direct = CONNECTION_IDS.get(trainId.toLowerCase(Locale.ROOT));
        if (direct != null) {
            return direct;
        }
        final String viaNormalised = CONNECTION_IDS.get(normalise(trainId));
        if (viaNormalised != null) {
            return viaNormalised;
        }
        for (final String alias : ScriptedCustomTrains.getTrainIdAliases(trainId)) {
            final String hit = CONNECTION_IDS.get(normalise(alias));
            if (hit != null) {
                return hit;
            }
        }
        return null;
    }

    private static boolean declaresOwnGangway(JsonObject vehicle) {
        try {
            if (vehicle.has("script_files") && vehicle.get("script_files").isJsonArray()) {
                for (final com.google.gson.JsonElement element : vehicle.getAsJsonArray("script_files")) {
                    final String name = element.getAsString().toLowerCase(Locale.ROOT);
                    if (name.contains("gangway") || name.contains("connection") || name.contains("conn_")) {
                        return true;
                    }
                }
            }
            if (vehicle.has("script_texts") && vehicle.get("script_texts").isJsonArray()) {
                for (final com.google.gson.JsonElement element : vehicle.getAsJsonArray("script_texts")) {
                    final String source = element.getAsString();
                    if (source.contains("drawConnModel") || source.contains("drawConnStretchTexture")) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean hasDeclaredConnection(String trainId) {
        return connectionIdOf(trainId) != null;
    }

    public static void recordScriptSources(String trainId, Iterable<String> sources) {
        if (trainId == null || sources == null) {
            return;
        }
        for (final String source : sources) {
            if (source != null && (source.contains("drawConnModel")
                    || source.contains("drawConnStretchTexture"))) {
                SELF_HANDLED.add(trainId.toLowerCase(Locale.ROOT));
                LOOKUP_CACHE.clear();
                return;
            }
        }
    }

    private static final String CUSTOM_PREFIX = "mtr_custom_train_";
    private static final String[] VARIATION_SUFFIXES =
        {"_cab_1", "_cab_2", "_cab_3", "_trailer", "_cab1", "_cab2", "_cab3"};

    public static String normalise(String trainId) {
        String key = trainId.toLowerCase(Locale.ROOT);
        if (key.startsWith(CUSTOM_PREFIX)) {
            key = key.substring(CUSTOM_PREFIX.length());
        }
        for (final String suffix : VARIATION_SUFFIXES) {
            if (key.endsWith(suffix)) {
                key = key.substring(0, key.length() - suffix.length());
                break;
            }
        }
        return key;
    }

    private static final java.util.Map<String, Boolean> LOOKUP_CACHE =
        new java.util.concurrent.ConcurrentHashMap<>();

    public static boolean isSelfHandled(String trainId) {
        if (trainId == null || SELF_HANDLED.isEmpty()) {
            return false;
        }
        final Boolean cached = LOOKUP_CACHE.get(trainId);
        if (cached != null) {
            return cached;
        }
        final boolean result = computeSelfHandled(trainId);
        LOOKUP_CACHE.put(trainId, result);
        return result;
    }

    private static boolean computeSelfHandled(String trainId) {
        if (SELF_HANDLED.contains(trainId.toLowerCase(Locale.ROOT))
                || SELF_HANDLED.contains(normalise(trainId))) {
            return true;
        }
        for (final String alias : ScriptedCustomTrains.getTrainIdAliases(trainId)) {
            if (SELF_HANDLED.contains(alias.toLowerCase(Locale.ROOT))
                    || SELF_HANDLED.contains(normalise(alias))) {
                return true;
            }
        }
        return false;
    }

    public static boolean currentVehicleSelfHandled() {
        if (!cn.jstxjf_.rte.ClientConfig.autoGangwayPolicy) {
            return false;
        }
        try {
            final org.mtr.mod.data.VehicleExtension vehicle =
                cn.jstxjf_.rte.render.train.CurrentVehicleTracker.get();
            if (vehicle == null || vehicle.vehicleExtraData == null) {
                return false;
            }
            final int car = Math.max(0, cn.jstxjf_.rte.render.train.CurrentVehicleTracker.getCar());
            if (car >= vehicle.vehicleExtraData.immutableVehicleCars.size()) {
                return false;
            }
            return isSelfHandled(vehicle.vehicleExtraData.immutableVehicleCars.get(car).getVehicleId());
        } catch (Throwable e) {
            return false;
        }
    }

    public static void report() {
        if (SELF_HANDLED.isEmpty()) {
            return;
        }
        final java.util.List<String> sample = new java.util.ArrayList<>();
        synchronized (SELF_HANDLED) {
            for (final String id : SELF_HANDLED) {
                sample.add(id);
                if (sample.size() >= 8) {
                    break;
                }
            }
        }
        RTE.LOGGER.info("[GangwayPolicy] {} vehicle(s) declare their own gangway, builtin gangway suppressed for them; e.g. {}",
            SELF_HANDLED.size(), sample);
    }
}
