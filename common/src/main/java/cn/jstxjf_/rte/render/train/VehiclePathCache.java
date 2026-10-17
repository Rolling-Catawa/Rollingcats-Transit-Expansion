package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.network.PacketVehiclePath;
import cn.jstxjf_.rte.scripting.compat.PathDataCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class VehiclePathCache {

    private static final long REQUEST_INTERVAL_MILLIS = 10000;
    private static final long STALE_MILLIS = 120000;

    private static final Map<Long, List<PathDataCompat>> PATHS = new ConcurrentHashMap<>();
    private static final Map<Long, List<PathDataCompat>> GEOMETRY_PATHS = new ConcurrentHashMap<>();
    private static final Map<Long, List<String>> GEOMETRY_KEYS = new ConcurrentHashMap<>();
    private static final Map<Long, Long> RECEIVED = new ConcurrentHashMap<>();
    private static final Map<Long, Long> REQUESTED = new ConcurrentHashMap<>();

    private VehiclePathCache() {
    }

    public static void accept(long vehicleId, List<PacketVehiclePath.Segment> segments) {
        final List<String> geometryKeys = segments.stream().map(segment -> segment.geometry).toList();
        if (!geometryKeys.isEmpty() && geometryKeys.stream().noneMatch(String::isEmpty)
                && geometryKeys.equals(GEOMETRY_KEYS.get(vehicleId))) {
            RECEIVED.put(vehicleId, System.currentTimeMillis());
            return;
        }
        final List<PathDataCompat> converted = new ArrayList<>(segments.size());
        boolean completeGeometry = !segments.isEmpty();
        int stopIndex = 0;
        for (final PacketVehiclePath.Segment segment : segments) {
            if (segment.dwellTime != 0) {
                stopIndex++;
            }
            if (!segment.geometry.isEmpty()) {
                try {
                    converted.add(new PathDataCompat(
                        cn.jstxjf_.rte.scripting.compat.PathGeometryCodec.decode(segment.geometry)));
                    continue;
                } catch (RuntimeException invalidGeometry) {
                    cn.jstxjf_.rte.RTE.LOGGER.debug("Invalid vehicle path geometry for {}", vehicleId, invalidGeometry);
                }
            }
            completeGeometry = false;
            converted.add(PathDataCompat.synthetic(
                segment.railLength, segment.speedLimitKilometersPerHour, segment.platform,
                segment.dwellTime, segment.savedRailBaseId, stopIndex,
                segment.startDistance, segment.endDistance));
        }
        final List<PathDataCompat> snapshot = List.copyOf(converted);
        PATHS.put(vehicleId, snapshot);
        if (completeGeometry) {
            GEOMETRY_PATHS.put(vehicleId, snapshot);
            GEOMETRY_KEYS.put(vehicleId, geometryKeys);
        } else {
            GEOMETRY_PATHS.remove(vehicleId);
            GEOMETRY_KEYS.remove(vehicleId);
        }
        RECEIVED.put(vehicleId, System.currentTimeMillis());
    }

    public static List<PathDataCompat> get(long vehicleId) {
        final Long received = RECEIVED.get(vehicleId);
        if (received == null || System.currentTimeMillis() - received > STALE_MILLIS) {
            return null;
        }
        return PATHS.get(vehicleId);
    }

    public static List<PathDataCompat> getGeometry(long vehicleId) {
        return get(vehicleId) == null ? null : GEOMETRY_PATHS.get(vehicleId);
    }

    public static void requestIfDue(long vehicleId) {
        if (!cn.jstxjf_.rte.RTEConfig.allowVehiclePathSync) {
            return;
        }
        final long now = System.currentTimeMillis();
        final Long last = REQUESTED.get(vehicleId);
        if (last != null && now - last < REQUEST_INTERVAL_MILLIS) {
            return;
        }
        REQUESTED.put(vehicleId, now);
        try {
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(
                new cn.jstxjf_.rte.network.PacketRequestVehiclePath(vehicleId));
        } catch (Exception ignored) {
        }
    }

    public static void retainAll(java.util.Set<Long> alive) {
        PATHS.keySet().retainAll(alive);
        GEOMETRY_PATHS.keySet().retainAll(alive);
        GEOMETRY_KEYS.keySet().retainAll(alive);
        RECEIVED.keySet().retainAll(alive);
        REQUESTED.keySet().retainAll(alive);
    }

    public static void clear() {
        PATHS.clear();
        GEOMETRY_PATHS.clear();
        GEOMETRY_KEYS.clear();
        RECEIVED.clear();
        REQUESTED.clear();
    }
}
