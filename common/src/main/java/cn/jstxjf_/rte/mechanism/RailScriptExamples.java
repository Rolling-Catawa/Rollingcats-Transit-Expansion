package cn.jstxjf_.rte.mechanism;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class RailScriptExamples {

    private static final String BASE = "/assets/rte/rail_script_examples/";
    private static final String[][] FILES = {
        {"turntable", "turntable.json", "turntable.server.js"},
        {"drawbridge", "drawbridge.json", "drawbridge.server.js"}
    };

    private RailScriptExamples() {
    }

    public static String read(String name) {
        try (InputStream stream = RailScriptExamples.class.getResourceAsStream(BASE + name)) {
            return stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    public static void installIfMissing(RailScriptStore store) {
        if (Files.exists(store.root())) {
            return;
        }
        for (final String[] entry : FILES) {
            try {
                store.write(entry[0], RailScriptStore.Part.META, read(entry[1]));
                store.write(entry[0], RailScriptStore.Part.SERVER, read(entry[2]));
            } catch (Exception e) {
                cn.jstxjf_.rte.RTE.LOGGER.warn("[RailScript] failed installing example {}: {}", entry[0], e.toString());
            }
        }
    }
}
