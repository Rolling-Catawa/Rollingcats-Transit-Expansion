package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.Depot;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Route;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Station;
import org.mtr.mod.client.MinecraftClientData;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class DataCacheCompat {

    public final ScriptLongMap<StationCompat> stationIdMap = new ScriptLongMap<>();
    public final ScriptLongMap<PlatformCompat> platformIdMap = new ScriptLongMap<>();
    public final ScriptLongMap<SidingCompat> sidingIdMap = new ScriptLongMap<>();
    public final ScriptLongMap<RouteCompat> routeIdMap = new ScriptLongMap<>();
    public final ScriptLongMap<StationCompat> platformIdToStation = new ScriptLongMap<>();
    public final ScriptLongMap<DepotCompat> sidingIdToDepot = new ScriptLongMap<>();
    public final Map<StationCompat, Set<StationCompat>> stationIdToConnectingStations = new java.util.concurrent.ConcurrentHashMap<>();
    public final ScriptLongMap<Map<Integer, ColorNameCompat>> stationIdToRoutes = new ScriptLongMap<>();

    public void ensureStationEntries(StationCompat station) {
        if (station != null) {
            stationIdToConnectingStations.putIfAbsent(station, new LinkedHashSet<>());
        }
    }

    public void refresh() {
        MinecraftClientData data = MinecraftClientData.getInstance();

        for (var entry : data.stationIdMap.long2ObjectEntrySet()) {
            stationIdMap.put(entry.getLongKey(), StationCompat.of(entry.getValue()));
        }

        for (var entry : data.stationIdMap.long2ObjectEntrySet()) {
            final Station station = entry.getValue();
            final Set<StationCompat> connected = new LinkedHashSet<>();
            for (Station connectedStation : station.connectedStations) {
                connected.add(StationCompat.of(connectedStation));
            }
            stationIdToConnectingStations.put(StationCompat.of(station), connected);
        }
        for (final StationCompat internedStation : StationCompat.internValues()) {
            ensureStationEntries(internedStation);
        }

        for (var entry : data.platformIdMap.long2ObjectEntrySet()) {
            platformIdMap.put(entry.getLongKey(), new PlatformCompat(entry.getValue()));
        }

        for (final org.mtr.core.data.SimplifiedRoute simplifiedRoute : data.simplifiedRouteIdMap.values()) {
            for (final org.mtr.core.data.SimplifiedRoutePlatform simplifiedRoutePlatform : simplifiedRoute.getPlatforms()) {
                final long stationId = simplifiedRoutePlatform.getStationId();
                if (stationId == 0) {
                    continue;
                }
                final StationCompat station = StationCompat.ofFallback(stationId, simplifiedRoutePlatform.getStationName());
                stationIdMap.putIfAbsent(stationId, station);
                platformIdToStation.putIfAbsent(simplifiedRoutePlatform.getPlatformId(), station);
            }
        }

        for (var entry : data.platformIdMap.long2ObjectEntrySet()) {
            final Platform platform = entry.getValue();
            if (platform.area != null) {
                platformIdToStation.put(entry.getLongKey(), StationCompat.of(platform.area));
            } else if (!platformIdToStation.containsKey(entry.getLongKey())) {
                platformIdToStation.put(entry.getLongKey(),
                    StationCompat.ofFallback(entry.getLongKey(), platform.getName()));
            }
        }

        for (Platform platform : data.platforms) {
            if (platform.area != null) {
                platformIdToStation.put(platform.getId(), StationCompat.of(platform.area));
            }
        }

        for (var entry : data.sidingIdMap.long2ObjectEntrySet()) {
            sidingIdMap.put(entry.getLongKey(), new SidingCompat(entry.getValue()));
            if (entry.getValue().area != null) {
                sidingIdToDepot.put(entry.getLongKey(), new DepotCompat(entry.getValue()));
            }
        }

        for (final Siding siding : data.sidings) {
            sidingIdMap.put(siding.getId(), new SidingCompat(siding));
            if (siding.area != null) {
                sidingIdToDepot.put(siding.getId(), new DepotCompat(siding));
            }
        }

        for (final Depot depot : data.depots) {
            final DepotCompat depotCompat = new DepotCompat(depot);
            for (final Siding siding : depot.savedRails) {
                sidingIdToDepot.put(siding.getId(), depotCompat);
                sidingIdMap.put(siding.getId(), new SidingCompat(siding));
            }
        }


        for (var entry : data.routeIdMap.long2ObjectEntrySet()) {
            routeIdMap.put(entry.getLongKey(), new RouteCompat(entry.getValue()));
        }

        {
            for (var entry : data.routeIdMap.long2ObjectEntrySet()) {
                final Route route = entry.getValue();
                if (route.getHidden()) {
                    continue;
                }
                final String routeName = route.getName().split("\\|\\|")[0];
                for (RoutePlatformData routePlatformData : route.getRoutePlatforms()) {
                    final Platform platform = routePlatformData.getPlatform();
                    if (platform == null) {
                        continue;
                    }
                    if (platform.area == null) {
                        platformIdToStation.putIfAbsent(platform.getId(),
                            StationCompat.ofFallback(platform.getId(), platform.getName()));
                        continue;
                    }
                    platformIdToStation.putIfAbsent(platform.getId(), StationCompat.of(platform.area));
                    stationIdToRoutes
                        .computeIfAbsent(platform.area.getId(), ignored -> new LinkedHashMap<>())
                        .put(route.getColor(), new ColorNameCompat(route.getColor(), routeName));
                }
            }
        }
    }

    public StationCompat getStationByPlatformId(long platformId) {
        StationCompat cached = platformIdToStation.get(platformId);
        if (cached != null) return cached;
        MinecraftClientData data = MinecraftClientData.getInstance();
        Platform platform = data.platformIdMap.get(platformId);
        if (platform != null && platform.area != null) {
            return StationCompat.of(platform.area);
        }
        return null;
    }

    public static <T, U> T tryGet(Map<?, Map<?, T>> map, Object key1, Object key2) {
        Map<?, T> inner = map.get(key1);
        return inner != null ? inner.get(key2) : null;
    }
}
