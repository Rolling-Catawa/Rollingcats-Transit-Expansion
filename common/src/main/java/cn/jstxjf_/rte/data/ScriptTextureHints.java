package cn.jstxjf_.rte.data;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScriptTextureHints {

    private static final Set<String> REPLACED = ConcurrentHashMap.newKeySet();
    private static final Pattern REPLACE_CALL =
        Pattern.compile("replaceTexture\\s*\\(\\s*[\"']([^\"']+)[\"']");

    private ScriptTextureHints() {
    }

    public static void reset() {
        REPLACED.clear();
    }

    public static void scan(String source) {
        if (source == null || source.isEmpty() || !source.contains("replaceTexture")) {
            return;
        }
        final Matcher matcher = REPLACE_CALL.matcher(source);
        while (matcher.find()) {
            final String name = matcher.group(1);
            final int slash = name.lastIndexOf('/');
            REPLACED.add((slash >= 0 ? name.substring(slash + 1) : name).toLowerCase(Locale.ROOT));
        }
    }

    public static boolean isScriptReplaced(String fileName) {
        return fileName != null && REPLACED.contains(fileName.toLowerCase(Locale.ROOT));
    }

    public static int size() {
        return REPLACED.size();
    }
}
