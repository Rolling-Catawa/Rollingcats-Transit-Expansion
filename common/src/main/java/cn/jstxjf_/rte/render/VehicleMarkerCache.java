package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.network.PacketVehicleMarkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class VehicleMarkerCache {

    private static final long STALE_MILLIS = 3000;
    private static final long REQUEST_INTERVAL_MILLIS = 100;
    private static final long MAX_INTERPOLATION_MILLIS = 600;

    private static final Map<Long, Sample> PREVIOUS = new HashMap<>();
    private static final Map<Long, Sample> CURRENT = new HashMap<>();

    private static volatile List<PacketVehicleMarkers.Marker> markers = List.of();
    private static volatile long receivedMillis;
    private static long lastRequestMillis;

    private VehicleMarkerCache() {
    }

    private static final class Sample {

        private final float x;
        private final float z;
        private final long millis;

        private Sample(float x, float z, long millis) {
            this.x = x;
            this.z = z;
            this.millis = millis;
        }
    }

    public static synchronized void accept(List<PacketVehicleMarkers.Marker> incoming) {
        final long now = System.currentTimeMillis();
        final HashSet<Long> alive = new HashSet<>(incoming.size());
        for (final PacketVehicleMarkers.Marker marker : incoming) {
            alive.add(marker.id);
            final Sample current = CURRENT.get(marker.id);
            if (current != null) {
                PREVIOUS.put(marker.id, current);
            }
            CURRENT.put(marker.id, new Sample(marker.x, marker.z, now));
        }
        PREVIOUS.keySet().retainAll(alive);
        CURRENT.keySet().retainAll(alive);
        markers = List.copyOf(incoming);
        receivedMillis = now;
    }

    public static synchronized List<PacketVehicleMarkers.Marker> current() {
        final long now = System.currentTimeMillis();
        if (now - receivedMillis > STALE_MILLIS) {
            return List.of();
        }
        final List<PacketVehicleMarkers.Marker> result = new ArrayList<>(markers.size());
        for (final PacketVehicleMarkers.Marker marker : markers) {
            result.add(interpolate(marker, now));
        }
        return result;
    }

    private static PacketVehicleMarkers.Marker interpolate(PacketVehicleMarkers.Marker marker, long now) {
        final Sample current = CURRENT.get(marker.id);
        final Sample previous = PREVIOUS.get(marker.id);
        if (current == null || previous == null) {
            return marker;
        }
        final long span = current.millis - previous.millis;
        if (span <= 0 || span > MAX_INTERPOLATION_MILLIS) {
            return marker;
        }
        final double ahead = Math.min(now - current.millis, span) / (double) span;
        if (ahead <= 0) {
            return marker;
        }
        final float x = (float) (current.x + (current.x - previous.x) * ahead);
        final float z = (float) (current.z + (current.z - previous.z) * ahead);
        return new PacketVehicleMarkers.Marker(marker.id, x, z, marker.color);
    }

    public static void requestIfDue(double minX, double minZ, double maxX, double maxZ) {
        if (!cn.jstxjf_.rte.RTEConfig.allowVehicleMarkerSync) {
            return;
        }
        if (!Double.isFinite(minX) || !Double.isFinite(minZ)
                || !Double.isFinite(maxX) || !Double.isFinite(maxZ)) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (now - lastRequestMillis < REQUEST_INTERVAL_MILLIS) {
            return;
        }
        lastRequestMillis = now;
        try {
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(
                new cn.jstxjf_.rte.network.PacketRequestVehicleMarkers(minX, minZ, maxX, maxZ));
        } catch (Exception ignored) {
        }
    }
}
