package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.ClientConfig;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CompatArbiter {

    public static final String OWNER_RTE = "rte";
    public static final String OWNER_OTHER = "other";
    public static final String OWNER_BOTH = "both";
    public static final String OWNER_DEDUPE = "dedupe";

    public static final String MOD_JCM = "jsblock";
    public static final String MOD_FANGSU = "fangsu";
    public static final String MOD_S1 = "s1mtraddon";

    public enum Domain {
        RAIL_ANGLE("railAngle", OWNER_RTE, MOD_FANGSU),
        NODE_PLACEMENT("nodePlacement", OWNER_RTE, MOD_FANGSU),
        RAIL_PREVIEW_ANGLE("railPreviewAngle", OWNER_RTE, MOD_FANGSU, MOD_JCM),
        RAIL_ONEWAY_ARROW("railOneWayArrow", OWNER_RTE, MOD_FANGSU, MOD_JCM),
        MESH_OPTIMIZE("meshOptimize", OWNER_OTHER, MOD_JCM),
        SCRIPT_VEHICLE("scriptVehicle", OWNER_DEDUPE, MOD_JCM),
        SCRIPT_EYECANDY("scriptEyecandy", OWNER_DEDUPE, MOD_JCM);

        public final String key;
        public final String defaultOwner;
        private final String[] rivals;

        Domain(String key, String defaultOwner, String... rivals) {
            this.key = key;
            this.defaultOwner = defaultOwner;
            this.rivals = rivals;
        }

        public String[] rivals() {
            return rivals.clone();
        }
    }

    private static final EnumMap<Domain, String> OWNERS = new EnumMap<>(Domain.class);
    private static final Map<String, Boolean> LOADED = new LinkedHashMap<>();
    private static final Map<String, String> PER_MOD = new LinkedHashMap<>();

    private CompatArbiter() {
    }

    public static boolean isModLoaded(String modId) {
        final Boolean cached = LOADED.get(modId);
        if (cached != null) {
            return cached;
        }
        boolean present;
        try {
            final Class<?> platform = Class.forName("dev.architectury.platform.Platform");
            present = (Boolean) platform.getMethod("isModLoaded", String.class).invoke(null, modId);
        } catch (Throwable e) {
            present = false;
        }
        LOADED.put(modId, present);
        return present;
    }

    public static boolean hasRival(Domain domain) {
        for (final String modId : domain.rivals) {
            if (isModLoaded(modId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean anyRivalPresent() {
        return isModLoaded(MOD_JCM) || isModLoaded(MOD_FANGSU) || isModLoaded(MOD_S1);
    }

    public static String ownerOf(Domain domain) {
        final String stored = OWNERS.get(domain);
        return stored == null ? domain.defaultOwner : stored;
    }

    public static String ownerOf(Domain domain, String modId) {
        final String perMod = PER_MOD.get(domain.key + "@" + modId);
        return perMod == null ? ownerOf(domain) : perMod;
    }

    public static void setOwner(Domain domain, String owner) {
        OWNERS.put(domain, normalize(owner, domain.defaultOwner));
        for (final String modId : domain.rivals) {
            PER_MOD.remove(domain.key + "@" + modId);
        }
    }

    public static void setOwner(Domain domain, String modId, String owner) {
        PER_MOD.put(domain.key + "@" + modId, normalize(owner, domain.defaultOwner));
    }

    public static boolean hasPerModChoice(Domain domain) {
        for (final String modId : domain.rivals) {
            if (PER_MOD.containsKey(domain.key + "@" + modId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean owns(Domain domain) {
        for (final String modId : domain.rivals) {
            if (!isModLoaded(modId)) {
                continue;
            }
            if (OWNER_OTHER.equals(ownerOf(domain, modId))) {
                return false;
            }
        }
        return true;
    }

    public static void applyDefaults() {
        PER_MOD.clear();
        OWNERS.clear();
        for (final Domain domain : Domain.values()) {
            OWNERS.put(domain, domain.defaultOwner);
        }
    }

    public static Map<String, String> snapshot() {
        final Map<String, String> out = new LinkedHashMap<>();
        for (final Domain domain : Domain.values()) {
            out.put(domain.key, ownerOf(domain));
        }
        out.putAll(PER_MOD);
        return out;
    }

    public static void restore(Map<String, String> stored) {
        OWNERS.clear();
        PER_MOD.clear();
        if (stored == null) {
            return;
        }
        for (final Domain domain : Domain.values()) {
            final String value = stored.get(domain.key);
            if (value != null) {
                OWNERS.put(domain, normalize(value, domain.defaultOwner));
            }
            for (final String modId : domain.rivals) {
                final String key = domain.key + "@" + modId;
                final String perMod = stored.get(key);
                if (perMod != null) {
                    PER_MOD.put(key, normalize(perMod, domain.defaultOwner));
                }
            }
        }
    }

    public static void syncVehicleChoiceToLegacy() {
        final String owner = ownerOf(Domain.SCRIPT_VEHICLE, MOD_JCM);
        if (OWNER_OTHER.equals(owner)) {
            ClientConfig.jcmJsChoice = JcmCompat.CHOICE_DISABLE_RTE_JS;
        } else if (OWNER_DEDUPE.equals(owner)) {
            ClientConfig.jcmJsChoice = JcmCompat.CHOICE_AUTO_DEDUPE;
        } else {
            ClientConfig.jcmJsChoice = JcmCompat.CHOICE_IGNORE;
        }
    }

    public static void adoptLegacyVehicleChoice() {
        if (OWNERS.containsKey(Domain.SCRIPT_VEHICLE) || hasPerModChoice(Domain.SCRIPT_VEHICLE)) {
            syncVehicleChoiceToLegacy();
            return;
        }
        switch (ClientConfig.jcmJsChoice) {
            case JcmCompat.CHOICE_DISABLE_RTE_JS -> setOwner(Domain.SCRIPT_VEHICLE, OWNER_OTHER);
            case JcmCompat.CHOICE_AUTO_DEDUPE -> setOwner(Domain.SCRIPT_VEHICLE, OWNER_DEDUPE);
            case JcmCompat.CHOICE_IGNORE -> setOwner(Domain.SCRIPT_VEHICLE, OWNER_BOTH);
            default -> { }
        }
    }

    public static boolean shouldPrompt() {
        return !ClientConfig.compatAsked && anyRivalPresent();
    }

    private static String normalize(String owner, String fallback) {
        if (owner == null) {
            return fallback;
        }
        final String trimmed = owner.trim().toLowerCase(java.util.Locale.ROOT);
        if (OWNER_RTE.equals(trimmed) || OWNER_OTHER.equals(trimmed)
            || OWNER_BOTH.equals(trimmed) || OWNER_DEDUPE.equals(trimmed)) {
            return trimmed;
        }
        return fallback;
    }
}
