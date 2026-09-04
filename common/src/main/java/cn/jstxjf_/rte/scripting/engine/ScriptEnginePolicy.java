package cn.jstxjf_.rte.scripting.engine;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ScriptEnginePolicy {

    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    private ScriptEnginePolicy() {
    }

    public static void restore(Map<String, String> stored) {
        OVERRIDES.clear();
        if (stored == null) {
            return;
        }
        for (final Map.Entry<String, String> entry : stored.entrySet()) {
            final ScriptEngineType type = ScriptEngineType.fromString(entry.getValue());
            if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                OVERRIDES.put(entry.getKey(), type.name().toLowerCase());
            }
        }
    }

    public static Map<String, String> snapshot() {
        return new LinkedHashMap<>(OVERRIDES);
    }

    public static void set(String pattern, ScriptEngineType type) {
        if (pattern == null || pattern.isEmpty()) {
            return;
        }
        if (type == null || type == ScriptEngineType.AUTO) {
            OVERRIDES.remove(pattern);
        } else {
            OVERRIDES.put(pattern, type.name().toLowerCase());
        }
    }

    public static void clear() {
        OVERRIDES.clear();
    }

    public static ScriptEngineType overrideFor(String key) {
        if (key == null || OVERRIDES.isEmpty()) {
            return ScriptEngineType.AUTO;
        }
        final String exact = OVERRIDES.get(key);
        if (exact != null) {
            return ScriptEngineType.fromString(exact);
        }
        String bestPattern = null;
        String bestValue = null;
        for (final Map.Entry<String, String> entry : OVERRIDES.entrySet()) {
            final String pattern = entry.getKey();
            if (!pattern.endsWith("*")) {
                continue;
            }
            final String prefix = pattern.substring(0, pattern.length() - 1);
            if (!key.startsWith(prefix)) {
                continue;
            }
            if (bestPattern == null || prefix.length() > bestPattern.length() - 1) {
                bestPattern = pattern;
                bestValue = entry.getValue();
            }
        }
        return bestValue == null ? ScriptEngineType.AUTO : ScriptEngineType.fromString(bestValue);
    }

    public static ScriptEngineType resolve(String key, String globalName, String packEngineName) {
        final ScriptEngineType perKey = overrideFor(key);
        if (perKey != ScriptEngineType.AUTO) {
            return perKey;
        }
        final ScriptEngineType global = ScriptEngineType.fromString(globalName);
        if (global != ScriptEngineType.AUTO) {
            return global;
        }
        return ScriptEngineType.fromString(packEngineName);
    }
}
