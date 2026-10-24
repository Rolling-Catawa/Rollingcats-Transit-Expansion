package cn.jstxjf_.rte.scripting;

import cn.jstxjf_.rte.RTE;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptFailureIsolation {

    private static final int FAILURES_BEFORE_BACKOFF = 3;
    private static final long BACKOFF_MILLIS = 10000;
    private static final long TIMEOUT_BACKOFF_MILLIS = 30000;

    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();
    private static final Map<Object, int[]> FAILURE_COUNTS = new ConcurrentHashMap<>();
    private static final Map<Object, Long> SUPPRESSED_UNTIL = new ConcurrentHashMap<>();

    private ScriptFailureIsolation() {
    }

    public static boolean shouldSkip(Object function) {
        if (function == null) {
            return false;
        }
        final Long until = SUPPRESSED_UNTIL.get(function);
        if (until == null) {
            return false;
        }
        if (System.currentTimeMillis() < until) {
            return true;
        }
        SUPPRESSED_UNTIL.remove(function);
        FAILURE_COUNTS.remove(function);
        return false;
    }

    public static void noteSuccess(Object function) {
        if (function == null) {
            return;
        }
        FAILURE_COUNTS.remove(function);
        SUPPRESSED_UNTIL.remove(function);
    }

    public static void note(Object function, Throwable failure) {
        if (failure != null) {
            final String key = failure.getClass().getName() + '|' + String.valueOf(failure.getMessage());
            if (REPORTED.add(key)) {
                RTE.LOGGER.warn("[Script] a render function failed and was skipped; other scripts continue: {}",
                    failure.getMessage());
            }
        }
        if (function == null) {
            return;
        }
        final int[] count = FAILURE_COUNTS.computeIfAbsent(function, ignored -> new int[1]);
        if (++count[0] >= FAILURES_BEFORE_BACKOFF) {
            SUPPRESSED_UNTIL.put(function, System.currentTimeMillis() + BACKOFF_MILLIS);
        }
    }

    public static void noteTimeout(Object function, long limitMillis, String scriptStack) {
        final String stack = scriptStack == null ? "" : scriptStack.trim();
        if (REPORTED.add("timeout|" + stack)) {
            RTE.LOGGER.warn("[Script] a render function ran longer than {} ms and was stopped; it is paused for {} s\n{}",
                limitMillis, TIMEOUT_BACKOFF_MILLIS / 1000, stack);
        }
        if (function != null) {
            FAILURE_COUNTS.remove(function);
            SUPPRESSED_UNTIL.put(function, System.currentTimeMillis() + TIMEOUT_BACKOFF_MILLIS);
        }
    }

    public static void note(Throwable failure) {
        note(null, failure);
    }

    public static void reset() {
        REPORTED.clear();
        FAILURE_COUNTS.clear();
        SUPPRESSED_UNTIL.clear();
    }
}
