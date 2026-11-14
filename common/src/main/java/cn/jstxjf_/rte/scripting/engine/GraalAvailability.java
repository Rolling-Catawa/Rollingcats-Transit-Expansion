package cn.jstxjf_.rte.scripting.engine;

public final class GraalAvailability {

    private static final int FIRST_UNSUPPORTED_JAVA = 22;

    private static Boolean usable;
    private static String reason = "";

    private GraalAvailability() {
    }

    public static boolean isUsable() {
        if (usable == null) {
            final int feature = Runtime.version().feature();
            if (feature >= FIRST_UNSUPPORTED_JAVA) {
                usable = false;
                reason = "Java " + feature + " removed sun.misc.Unsafe.ensureClassInitialized, "
                    + "which the bundled GraalJS 23.0.1 requires";
            } else {
                usable = true;
            }
        }
        return usable;
    }

    public static String reason() {
        return reason;
    }
}
