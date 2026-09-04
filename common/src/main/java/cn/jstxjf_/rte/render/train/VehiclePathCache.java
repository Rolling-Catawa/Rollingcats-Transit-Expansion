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
    private static final Map<Long, Long> RECEIVED = new ConcurrentHashMap<>();
    private static final Map<Long, Long> REQUESTED = new ConcurrentHashMap<>();

    private VehiclePathCache() {
    }

    public static void accept(long vehicleId, List<PacketVehiclePath.Segment> segments) {
        final List<PathDataCompat> converted = new ArrayList<>(segments.size());
        int stopIndex = 0;
        for (final PacketVehiclePath.Segment segment : segments) {
            if (segment.dwellTime != 0) {
                stopIndex++;
            }
            converted.add(PathDataCompat.synthetic(
                segment.railLength, segment.speedLimitKilometersPerHour, segment.platform,
                segment.dwellTime, segment.savedRailBaseId, stopIndex,
                segment.startDistance, segment.endDistance));
        }
        PATHS.put(vehicleId, converted);
        RECEIVED.put(vehicleId, System.currentTimeMillis());
    }

    public static List<PathDataCompat> get(long vehicleId) {
        final Long received = RECEIVED.get(vehicleId);
        if (received == null || System.currentTimeMillis() - received > STALE_MILLIS) {
            return null;
        }
        return PATHS.get(vehicleId);
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
            org.mtr.mod.InitClient.REGISTRY_CLIENT.sendPacketToServer(
                new cn.jstxjf_.rte.network.PacketRequestVehiclePath(vehicleId));
        } catch (Exception ignored) {
        }
    }

    public static void retainAll(java.util.Set<Long> alive) {
        PATHS.keySet().retainAll(alive);
        RECEIVED.keySet().retainAll(alive);
        REQUESTED.keySet().retainAll(alive);
    }
}
