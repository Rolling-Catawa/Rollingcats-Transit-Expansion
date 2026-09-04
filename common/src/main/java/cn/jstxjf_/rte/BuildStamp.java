package cn.jstxjf_.rte;

import java.io.InputStream;
import java.util.Properties;

public final class BuildStamp {

    private static final Properties VALUES = new Properties();
    private static boolean loaded;
    private static boolean logged;

    private BuildStamp() {
    }

    private static synchronized void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        try (InputStream stream = BuildStamp.class.getResourceAsStream("/rte/rte-build.properties")) {
            if (stream != null) {
                VALUES.load(stream);
            }
        } catch (Throwable ignored) {
        }
    }

    private static String get(String key) {
        load();
        final String value = VALUES.getProperty(key);
        return value == null || value.isEmpty() ? "unknown" : value;
    }

    public static String version() {
        return get("version");
    }

    public static String buildId() {
        return get("buildId");
    }

    public static String commit() {
        return get("commit");
    }

    public static String branch() {
        return get("branch");
    }

    public static String buildTime() {
        return get("buildTime");
    }

    public static String platform() {
        return get("platform");
    }

    public static boolean cleanTree() {
        return "true".equals(get("clean"));
    }

    public static String describe() {
        return version() + " build " + buildId()
            + " (" + commit() + (cleanTree() ? "" : "+dirty") + ", " + platform() + ")";
    }

    public static synchronized void logOnce() {
        if (logged) {
            return;
        }
        logged = true;
        RTE.LOGGER.info("Rollingcats Transit Expansion {} | branch {} | built {} | (c) Rolling-Catawa, All Rights Reserved",
            describe(), branch(), buildTime());
    }
}
