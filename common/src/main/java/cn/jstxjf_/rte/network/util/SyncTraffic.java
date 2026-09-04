package cn.jstxjf_.rte.network.util;

import cn.jstxjf_.rte.RTE;

import java.util.concurrent.atomic.AtomicLong;

public final class SyncTraffic {

    private static final long REPORT_INTERVAL_MILLIS = 60000;

    private static final AtomicLong railBytes = new AtomicLong();
    private static final AtomicLong markerBytes = new AtomicLong();
    private static final AtomicLong pathBytes = new AtomicLong();
    private static volatile long lastReportMillis;

    private SyncTraffic() {
    }

    public static void addRail(long bytes) {
        if (!cn.jstxjf_.rte.RTEConfig.enableNetworkDiagnostics) return;
        railBytes.addAndGet(bytes);
        report();
    }

    public static void addMarker(long bytes) {
        if (!cn.jstxjf_.rte.RTEConfig.enableNetworkDiagnostics) return;
        markerBytes.addAndGet(bytes);
        report();
    }

    public static void addPath(long bytes) {
        if (!cn.jstxjf_.rte.RTEConfig.enableNetworkDiagnostics) return;
        pathBytes.addAndGet(bytes);
        report();
    }

    private static synchronized void report() {
        final long now = System.currentTimeMillis();
        if (lastReportMillis == 0) {
            lastReportMillis = now;
            return;
        }
        if (now - lastReportMillis < REPORT_INTERVAL_MILLIS) {
            return;
        }
        final long elapsed = now - lastReportMillis;
        lastReportMillis = now;
        final long rail = railBytes.getAndSet(0);
        final long marker = markerBytes.getAndSet(0);
        final long path = pathBytes.getAndSet(0);
        if (rail + marker + path == 0) {
            return;
        }
        RTE.LOGGER.info("[SyncTraffic] {}s: rail={}, marker={}, path={} (total {}, {}/s)",
            elapsed / 1000, format(rail), format(marker), format(path),
            format(rail + marker + path), format((rail + marker + path) * 1000 / Math.max(1, elapsed)));
    }

    private static String format(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1fKB", bytes / 1024.0);
        }
        return String.format("%.1fMB", bytes / (1024.0 * 1024.0));
    }
}
