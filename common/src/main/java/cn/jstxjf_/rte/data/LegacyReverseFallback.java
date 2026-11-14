package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.ClientConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.mtr.mod.resource.VehicleResource;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class LegacyReverseFallback {

    private static final Set<String> ELIGIBLE = Collections.synchronizedSet(new LinkedHashSet<>());
    private static final ThreadLocal<Boolean> ACTIVE = new ThreadLocal<>();

    private LegacyReverseFallback() {
    }

    public static void reset() {
        ELIGIBLE.clear();
    }

    public static void record(String trainId, JsonObject vehicle, JsonObject modelProperties) {
        if (trainId == null || vehicle == null || modelProperties == null) {
            return;
        }
        if (authorHandlesOrientation(vehicle, modelProperties)) {
            ELIGIBLE.remove(trainId.toLowerCase(Locale.ROOT));
        } else {
            ELIGIBLE.add(trainId.toLowerCase(Locale.ROOT));
        }
    }

    public static boolean isEligible(String vehicleId) {
        if (vehicleId == null || ClientConfig.vanillaVehicleMode || ELIGIBLE.isEmpty()) {
            return false;
        }
        return vehicleId.toLowerCase(Locale.ROOT).endsWith("_cab_2") && ELIGIBLE.contains(GangwayPolicy.normalise(vehicleId));
    }

    public static boolean active() {
        return Boolean.TRUE.equals(ACTIVE.get());
    }

    public static <T> VehicleResource.LegacyVehicleSupplier<T> wrap(String vehicleId, VehicleResource.LegacyVehicleSupplier<T> supplier) {
        return (carIndex, carCount) -> {
            if (!isEligible(vehicleId)) {
                return supplier.apply(carIndex, carCount);
            }
            final Boolean previous = ACTIVE.get();
            ACTIVE.set(true);
            try {
                return supplier.apply(carIndex, carCount);
            } finally {
                if (previous == null) {
                    ACTIVE.remove();
                } else {
                    ACTIVE.set(previous);
                }
            }
        };
    }

    private static boolean authorHandlesOrientation(JsonObject vehicle, JsonObject modelProperties) {
        try {
            if (!vehicle.has("model")) {
                return true;
            }
            final String model = vehicle.get("model").getAsString();
            if (model.toLowerCase(Locale.ROOT).contains("reversed")) {
                return true;
            }
            final String[] segments = model.split("\\|", -1);
            for (int i = 1; i < segments.length; i += 2) {
                if (!segments[i].replace(";", "").trim().isEmpty()) {
                    return true;
                }
            }
            for (final Map.Entry<String, JsonElement> entry : modelProperties.entrySet()) {
                final String key = entry.getKey().toLowerCase(Locale.ROOT);
                if (!key.startsWith("parts") || !entry.getValue().isJsonArray()) {
                    continue;
                }
                if (!key.equals("parts") && !key.equals("parts_normal") && entry.getValue().getAsJsonArray().size() > 0) {
                    return true;
                }
                for (final JsonElement element : entry.getValue().getAsJsonArray()) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    final JsonObject part = element.getAsJsonObject();
                    if (hasText(part, "whitelisted_cars") || hasText(part, "blacklisted_cars")) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private static boolean hasText(JsonObject object, String key) {
        return object.has(key) && object.get(key).isJsonPrimitive() && !object.get(key).getAsString().trim().isEmpty();
    }
}
