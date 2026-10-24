package cn.jstxjf_.rte.scripting.compat;

public final class LegacyConnectionCache {
    private LegacyConnectionCache() {
    }

    public static boolean matches(String script) {
        return script.contains("ConnInfo") && script.contains("pathDatas")
            && script.contains("pathLength") && script.contains("getRailIndex");
    }

    public static final String FUNCTION_NAME = "__rteRefreshLegacyConnectionPath";
    public static final String SOURCE = """
        function __rteRefreshLegacyConnectionPath(ctx, state, train) {
            var conn = state && state.ConnInfo;
            if (!conn) return;
            var path = train.path();
            var size = typeof path.size === "function" ? path.size() : path.length;
            if (!size) return;

            conn.pathDatas = path;
            var source = train.rte$getPathSource();
            if (conn.__rtePathSource && source.equals(conn.__rtePathSource)) return;
            var lengths = [];
            for (var i = 0; i < size; i++) {
                var segment = typeof path.get === "function" ? path.get(i) : path[i];
                lengths.push(segment.endDistance);
            }
            conn.pathLength = lengths;
            conn.__rtePathSource = source;
        }
        """;
}
