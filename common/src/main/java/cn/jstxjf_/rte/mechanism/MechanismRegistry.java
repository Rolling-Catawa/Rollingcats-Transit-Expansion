package cn.jstxjf_.rte.mechanism;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class MechanismRegistry {

    public record RailInfo(String hexId, String group, String scriptId, RailPose pose) {
    }

    private final Map<String, Mechanism> mechanisms = new TreeMap<>();
    private final Map<String, Mechanism> byRail = new HashMap<>();

    public synchronized void rebuild(Iterable<RailInfo> rails) {
        final Map<String, TreeMap<String, RailInfo>> grouped = new TreeMap<>();
        for (final RailInfo rail : rails) {
            if (rail == null || rail.group() == null || rail.group().isEmpty() || rail.hexId() == null) {
                continue;
            }
            grouped.computeIfAbsent(rail.group(), ignored -> new TreeMap<>()).put(rail.hexId(), rail);
        }
        mechanisms.keySet().removeIf(group -> !grouped.containsKey(group));
        byRail.clear();
        grouped.forEach((group, members) -> {
            final Mechanism mechanism = mechanisms.computeIfAbsent(group, Mechanism::new);
            mechanism.candidates.clear();
            String scriptId = "";
            for (final RailInfo info : members.values()) {
                mechanism.candidates.put(info.hexId(), info.pose());
                byRail.put(info.hexId(), mechanism);
                if (scriptId.isEmpty() && info.scriptId() != null && !info.scriptId().isEmpty()) {
                    scriptId = info.scriptId();
                }
            }
            mechanism.scriptId = scriptId;
            final List<String> ids = mechanism.candidateIds();
            if (!mechanism.candidates.containsKey(mechanism.activeRailId)) {
                mechanism.activeRailId = ids.get(0);
            }
            if (!mechanism.candidates.containsKey(mechanism.deckRailId)) {
                mechanism.deckRailId = mechanism.activeRailId;
            }
            if (!mechanism.candidates.containsKey(mechanism.targetRailId)) {
                mechanism.targetRailId = mechanism.activeRailId;
            }
        });
    }

    public synchronized Mechanism get(String group) {
        return mechanisms.get(group);
    }

    public synchronized Mechanism byRail(String hexId) {
        return hexId == null ? null : byRail.get(hexId);
    }

    public synchronized Collection<Mechanism> all() {
        return List.copyOf(mechanisms.values());
    }

    public synchronized String toJson() {
        final JsonObject root = new JsonObject();
        mechanisms.forEach((group, mechanism) -> {
            final JsonObject entry = new JsonObject();
            entry.addProperty("script", mechanism.scriptId);
            entry.addProperty("active", mechanism.targetRailId.isEmpty() ? mechanism.activeRailId : mechanism.targetRailId);
            entry.addProperty("deck", mechanism.deckRailId);
            entry.addProperty("transform", mechanism.target.encode());
            entry.addProperty("locked", mechanism.locked);
            entry.addProperty("state", mechanism.stateJson);
            entry.addProperty("approach", mechanism.approachRange);
            root.add(group, entry);
        });
        return root.toString();
    }

    public synchronized void loadJson(String json) {
        if (json == null || json.isBlank()) {
            return;
        }
        final JsonObject root;
        try {
            root = JsonParser.parseString(json).getAsJsonObject();
        } catch (Exception e) {
            return;
        }
        root.entrySet().forEach(entry -> {
            if (!entry.getValue().isJsonObject()) {
                return;
            }
            final JsonObject object = entry.getValue().getAsJsonObject();
            final Mechanism mechanism = mechanisms.computeIfAbsent(entry.getKey(), Mechanism::new);
            mechanism.scriptId = string(object, "script");
            mechanism.activeRailId = string(object, "active");
            mechanism.targetRailId = mechanism.activeRailId;
            mechanism.deckRailId = string(object, "deck");
            mechanism.setImmediate(MechanismTransform.decode(string(object, "transform")));
            mechanism.locked = object.has("locked") && object.get("locked").getAsBoolean();
            final String state = string(object, "state");
            mechanism.stateJson = state.isEmpty() ? "{}" : state;
            if (object.has("approach")) {
                mechanism.approachRange = object.get("approach").getAsDouble();
            }
        });
    }

    private static String string(JsonObject object, String key) {
        return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : "";
    }
}
