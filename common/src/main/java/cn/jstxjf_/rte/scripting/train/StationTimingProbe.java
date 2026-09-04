package cn.jstxjf_.rte.scripting.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.scripting.compat.PathDataCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StationTimingProbe {

    private static final Map<Long, String> LAST = new HashMap<>();
    private static final Map<Long, Long> LAST_MILLIS = new HashMap<>();
    private static final long MIN_INTERVAL_MILLIS = 250;

    private StationTimingProbe() {
    }

    public static void sample(TrainWrapper train) {
        if (!ClientConfig.traceStationTiming || train == null) {
            return;
        }
        try {
            rte$sample(train);
        } catch (Throwable ignored) {
        }
    }

    private static void rte$sample(TrainWrapper train) {
        final long id = train.id();
        final long now = System.currentTimeMillis();
        final Long previous = LAST_MILLIS.get(id);
        if (previous != null && now - previous < MIN_INTERVAL_MILLIS) {
            return;
        }
        LAST_MILLIS.put(id, now);

        final Object rawPath = train.path();
        if (!(rawPath instanceof List)) {
            return;
        }
        @SuppressWarnings("unchecked")
        final List<PathDataCompat> path = (List<PathDataCompat>) rawPath;

        final List<TrainWrapper.PlatformInfo> platforms = train.getAllPlatforms();
        final int nextIndex = train.getAllPlatformsNextIndex();
        final int cars = train.trainCars();

        final double headProgress = train.getRailProgress(0);
        final double tailProgress = train.getRailProgress(Math.max(0, cars - 1));
        final int headIndex = train.getRailIndex(headProgress, false);
        final int tailIndex = train.getRailIndex(tailProgress, true);

        final PathDataCompat head = entry(path, headIndex);
        final PathDataCompat tail = entry(path, tailIndex);

        double nextPlatformId = -1;
        String nextStation = "-";
        if (nextIndex >= 0 && nextIndex < platforms.size()) {
            final TrainWrapper.PlatformInfo info = platforms.get(nextIndex);
            nextStation = String.valueOf(info.stationName());
            if (info.platform != null) {
                nextPlatformId = info.platform.id;
            }
        }

        final boolean headMatch = head != null && head.dwellTime != 0 && head.savedRailBaseId == nextPlatformId;
        final boolean tailMatch = tail != null && tail.dwellTime != 0 && tail.savedRailBaseId == nextPlatformId;
        final boolean onPlatformRail = headMatch || tailMatch;

        final String state = String.format(
            "onRoute=%s pathSize=%d nextIdx=%d/%d nextId=%.0f arrived=%s head[%d dwell=%d rail=%.0f] tail[%d dwell=%d rail=%.0f]",
            train.isOnRoute(), path.size(), nextIndex, platforms.size(), nextPlatformId, onPlatformRail,
            headIndex, head == null ? -1 : head.dwellTime, head == null ? -1 : head.savedRailBaseId,
            tailIndex, tail == null ? -1 : tail.dwellTime, tail == null ? -1 : tail.savedRailBaseId);

        if (state.equals(LAST.get(id))) {
            return;
        }
        LAST.put(id, state);

        RTE.LOGGER.info("[StationTiming] {} {} next={} progress head={} tail={} speed={}km/h door={}",
            train.trainTypeId(), state, nextStation,
            String.format("%.2f", headProgress), String.format("%.2f", tailProgress),
            String.format("%.1f", train.speedKmh()), String.format("%.2f", train.doorValue()));
    }

    private static PathDataCompat entry(List<PathDataCompat> path, int index) {
        if (index < 0 || index >= path.size()) {
            return null;
        }
        return path.get(index);
    }

    public static void reset() {
        LAST.clear();
        LAST_MILLIS.clear();
    }
}
