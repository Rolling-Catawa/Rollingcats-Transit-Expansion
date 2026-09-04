package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;

public final class PreloadBudget {


    private static int granted;
    private static boolean exhausted;

    private PreloadBudget() {
    }

    public static void reset() {
        granted = 0;
        exhausted = false;
    }

    public static boolean tryReserve() {
        if (exhausted) {
            return false;
        }
        final Runtime runtime = Runtime.getRuntime();
        final long used = runtime.totalMemory() - runtime.freeMemory();
        final boolean overHeap = used > runtime.maxMemory() * (ClientConfig.preloadHeapCeilingPercent / 100.0);
        if (overHeap || granted >= ClientConfig.bbModelPreloadLimit) {
            exhausted = true;
            RTE.LOGGER.info("[Preload] stopped after {} vehicles ({}), heap {} MB of {} MB",
                granted, overHeap ? "heap ceiling" : "vehicle limit",
                used / 1048576, runtime.maxMemory() / 1048576);
            return false;
        }
        granted++;
        return true;
    }
}
