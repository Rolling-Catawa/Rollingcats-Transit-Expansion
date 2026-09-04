package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DepotRailRules {

    public enum Rule {
        NONE,
        VIA,
        BANNED
    }

    private static final Map<Long, Map<String, Rule>> RULES = new ConcurrentHashMap<>();
    private static Path storagePath;

    private DepotRailRules() {
    }

    public static Rule get(long depotId, String railId) {
        if (railId == null) {
            return Rule.NONE;
        }
        final Map<String, Rule> depotRules = RULES.get(depotId);
        if (depotRules == null) {
            return Rule.NONE;
        }
        return depotRules.getOrDefault(railId, Rule.NONE);
    }

    public static boolean isBanned(long depotId, String railId) {
        return get(depotId, railId) == Rule.BANNED;
    }

    public static boolean isVia(long depotId, String railId) {
        return get(depotId, railId) == Rule.VIA;
    }

    public static void set(long depotId, String railId, Rule rule) {
        if (railId == null) {
            return;
        }
        if (rule == Rule.NONE) {
            final Map<String, Rule> depotRules = RULES.get(depotId);
            if (depotRules != null) {
                depotRules.remove(railId);
                if (depotRules.isEmpty()) {
                    RULES.remove(depotId);
                }
            }
        } else {
            RULES.computeIfAbsent(depotId, ignored -> new ConcurrentHashMap<>()).put(railId, rule);
        }
    }

    public static Rule cycle(long depotId, String railId) {
        final Rule next = switch (get(depotId, railId)) {
            case NONE -> Rule.VIA;
            case VIA -> Rule.BANNED;
            case BANNED -> Rule.NONE;
        };
        set(depotId, railId, next);
        return next;
    }

    public static void clearDepot(long depotId) {
        RULES.remove(depotId);
    }

    public static Set<Long> depotsWithRules() {
        return Collections.unmodifiableSet(new HashSet<>(RULES.keySet()));
    }

    public static Map<String, Rule> rulesOf(long depotId) {
        final Map<String, Rule> depotRules = RULES.get(depotId);
        return depotRules == null ? Map.of() : Collections.unmodifiableMap(new HashMap<>(depotRules));
    }

    public static void replaceAll(Map<Long, Map<String, Rule>> source) {
        RULES.clear();
        source.forEach((depotId, depotRules) -> {
            if (depotRules != null && !depotRules.isEmpty()) {
                RULES.put(depotId, new ConcurrentHashMap<>(depotRules));
            }
        });
    }

    public static void bindStorage(Path path) {
        storagePath = path;
        load();
    }

    public static void unbindStorage() {
        storagePath = null;
        RULES.clear();
    }

    private static void load() {
        RULES.clear();
        if (storagePath == null || !Files.isRegularFile(storagePath)) {
            return;
        }
        try {
            final JsonObject root = JsonParser.parseString(
                Files.readString(storagePath, StandardCharsets.UTF_8)).getAsJsonObject();
            for (final String depotKey : root.keySet()) {
                final long depotId;
                try {
                    depotId = Long.parseLong(depotKey);
                } catch (NumberFormatException ignored) {
                    continue;
                }
                final Map<String, Rule> depotRules = new ConcurrentHashMap<>();
                final JsonObject entry = root.getAsJsonObject(depotKey);
                for (final Rule rule : new Rule[]{Rule.VIA, Rule.BANNED}) {
                    final String key = rule.name().toLowerCase(java.util.Locale.ROOT);
                    if (!entry.has(key)) {
                        continue;
                    }
                    final JsonArray array = entry.getAsJsonArray(key);
                    for (int i = 0; i < array.size(); i++) {
                        depotRules.put(array.get(i).getAsString(), rule);
                    }
                }
                if (!depotRules.isEmpty()) {
                    RULES.put(depotId, depotRules);
                }
            }
            RTE.LOGGER.info("[DepotRailRules] loaded rules for {} depots", RULES.size());
        } catch (Exception e) {
            RTE.LOGGER.error("[DepotRailRules] failed to load {}", storagePath, e);
        }
    }

    public static void save() {
        if (storagePath == null) {
            return;
        }
        final JsonObject root = new JsonObject();
        RULES.forEach((depotId, depotRules) -> {
            final JsonArray via = new JsonArray();
            final JsonArray banned = new JsonArray();
            depotRules.forEach((railId, rule) -> {
                if (rule == Rule.VIA) {
                    via.add(railId);
                } else if (rule == Rule.BANNED) {
                    banned.add(railId);
                }
            });
            if (via.isEmpty() && banned.isEmpty()) {
                return;
            }
            final JsonObject entry = new JsonObject();
            if (!via.isEmpty()) {
                entry.add("via", via);
            }
            if (!banned.isEmpty()) {
                entry.add("banned", banned);
            }
            root.add(Long.toString(depotId), entry);
        });
        try {
            final Path parent = storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(storagePath, root.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            RTE.LOGGER.error("[DepotRailRules] failed to save {}", storagePath, e);
        }
    }
}
