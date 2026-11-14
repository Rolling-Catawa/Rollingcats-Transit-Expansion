package cn.jstxjf_.rte.scripting.engine;

public enum ScriptEngineType {
    GRAAL,
    RHINO,
    AUTO;

    public static ScriptEngineType fromString(String value) {
        if (value == null) {
            return AUTO;
        }
        final String normalized = value.trim().toLowerCase();
        if (normalized.equals("rhino")) {
            return RHINO;
        }
        if (normalized.equals("graal") || normalized.equals("graaljs")) {
            return GRAAL;
        }
        return AUTO;
    }
}
