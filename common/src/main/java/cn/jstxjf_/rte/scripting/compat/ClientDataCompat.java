package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.TransportMode;
import org.mtr.mod.client.MinecraftClientData;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ClientDataCompat {

    private static final ClientDataCompat INSTANCE = new ClientDataCompat();
    private static volatile long lastRefreshTime = 0;
    private static volatile long lastDataSignature = Long.MIN_VALUE;
    private static volatile MinecraftClientData lastDataInstance;
    private static final long REFRESH_INTERVAL_MS = 500;

    public static final Set<StationCompat> STATIONS = new LinkedHashSet<>();
    public static final Set<PlatformCompat> PLATFORMS = new LinkedHashSet<>();
    public static final Set<SidingCompat> SIDINGS = new LinkedHashSet<>();
    public static final Set<RouteCompat> ROUTES = new LinkedHashSet<>();
    public static final Set<DepotCompat> DEPOTS = new LinkedHashSet<>();

    public static final DataCacheCompat DATA_CACHE = new DataCacheCompat();

    /** MTR 3 schedule ABI, reconstructed from live MTR 4 vehicle timetables. */
    public static final ScriptLongMap<Set<ScheduleEntryCompat>> SCHEDULES_FOR_PLATFORM = new ScriptLongMap<>();
    private static final Map<Long, List<PlatformSchedule>> VEHICLE_SCHEDULES = new ConcurrentHashMap<>();

    public static String DASHBOARD_SEARCH = "";
    public static String ROUTES_PLATFORMS_SEARCH = "";
    public static String ROUTES_PLATFORMS_SELECTED_SEARCH = "";
    public static String TRAINS_SEARCH = "";
    public static String EXIT_PARENTS_SEARCH = "";
    public static String EXIT_DESTINATIONS_SEARCH = "";

    public static synchronized void ensureFresh() {
        long now = System.currentTimeMillis();
        if (now - lastRefreshTime < REFRESH_INTERVAL_MS) return;
        lastRefreshTime = now;

        try {
            final MinecraftClientData data = MinecraftClientData.getInstance();
            if (lastDataInstance != null && data != lastDataInstance) {
                clearVehicleSchedules();
                lastDataSignature = Long.MIN_VALUE;
            }
            lastDataInstance = data;
            final long signature = dataSignature(data);
            if (signature == lastDataSignature) {
                return;
            }

            final Set<StationCompat> stations = data.stations.stream()
                .map(StationCompat::of)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            final Set<PlatformCompat> platforms = data.platforms.stream()
                .map(PlatformCompat::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            final Set<SidingCompat> sidings = data.sidings.stream()
                .map(SidingCompat::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            final Set<RouteCompat> routes = data.routes.stream()
                .map(RouteCompat::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            final Set<DepotCompat> depots = data.depots.stream()
                .map(DepotCompat::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));

            DATA_CACHE.refresh();
            replaceContents(STATIONS, stations);
            replaceContents(PLATFORMS, platforms);
            replaceContents(SIDINGS, sidings);
            replaceContents(ROUTES, routes);
            replaceContents(DEPOTS, depots);
            lastDataSignature = signature;
        } catch (Exception | Error e) {
            cn.jstxjf_.rte.RTE.LOGGER.error("[DataCache] refresh failed", e);
        }
    }

    private static <T> void replaceContents(Set<T> target, Set<T> source) {
        target.clear();
        target.addAll(source);
    }

    private static long dataSignature(MinecraftClientData data) {
        long hash = System.identityHashCode(data);
        hash = signature(hash, data.stations);
        hash = signature(hash, data.platforms);
        hash = signature(hash, data.sidings);
        hash = signature(hash, data.routes);
        hash = signature(hash, data.depots);
        hash = 31 * hash + data.simplifiedRouteIdMap.size();
        for (final org.mtr.core.data.SimplifiedRoute route : data.simplifiedRouteIdMap.values()) {
            hash = 31 * hash + System.identityHashCode(route);
            hash = 31 * hash + route.getId();
            hash = 31 * hash + route.getColor();
            hash = 31 * hash + route.getPlatforms().size();
        }
        return hash;
    }

    private static long signature(long seed,
                                  Iterable<? extends org.mtr.core.data.NameColorDataBase> values) {
        long hash = seed;
        int size = 0;
        for (final org.mtr.core.data.NameColorDataBase value : values) {
            size++;
            hash = 31 * hash + System.identityHashCode(value);
            hash = 31 * hash + value.getId();
            hash = 31 * hash + value.getColor();
            hash = 31 * hash + value.getName().hashCode();
        }
        return 31 * hash + size;
    }

    public static ClientDataCompat getInstance() {
        ensureFresh();
        return INSTANCE;
    }

    public Set<StationCompat> getStations() {
        return STATIONS;
    }

    public Set<PlatformCompat> getPlatforms() {
        return PLATFORMS;
    }

    public Set<SidingCompat> getSidings() {
        return SIDINGS;
    }

    public Set<RouteCompat> getRoutes() {
        return ROUTES;
    }

    public Set<DepotCompat> getDepots() {
        return DEPOTS;
    }

    public DataCacheCompat getDataCache() {
        return DATA_CACHE;
    }

    public static <T> Set<T> getFilteredDataSet(TransportMode transportMode, Set<T> dataSet) {
        return dataSet;
    }

    public static boolean hasPermission() {
        return MinecraftClientData.hasPermission();
    }

    public static void updateVehicleSchedules(long vehicleId, List<Long> platformIds,
                                              List<ScheduleEntryCompat> entries) {
        final int size = Math.min(platformIds.size(), entries.size());
        final java.util.ArrayList<PlatformSchedule> snapshot = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final long platformId = platformIds.get(i);
            if (platformId != 0 && entries.get(i) != null) {
                snapshot.add(new PlatformSchedule(platformId, entries.get(i)));
            }
        }
        final List<PlatformSchedule> immutable = List.copyOf(snapshot);
        final List<PlatformSchedule> old = immutable.isEmpty()
            ? VEHICLE_SCHEDULES.remove(vehicleId)
            : VEHICLE_SCHEDULES.put(vehicleId, immutable);
        if (!java.util.Objects.equals(old, immutable)) {
            rebuildSchedules();
        }
    }

    public static void retainVehicleSchedules(Set<Long> liveVehicleIds) {
        if (VEHICLE_SCHEDULES.keySet().retainAll(liveVehicleIds)) {
            rebuildSchedules();
        }
    }

    public static void clearVehicleSchedules() {
        if (!VEHICLE_SCHEDULES.isEmpty() || !SCHEDULES_FOR_PLATFORM.isEmpty()) {
            VEHICLE_SCHEDULES.clear();
            SCHEDULES_FOR_PLATFORM.clear();
        }
    }

    private static synchronized void rebuildSchedules() {
        final Map<Long, Set<ScheduleEntryCompat>> rebuilt = new java.util.LinkedHashMap<>();
        for (final List<PlatformSchedule> vehicleEntries : VEHICLE_SCHEDULES.values()) {
            for (final PlatformSchedule platformSchedule : vehicleEntries) {
                rebuilt.computeIfAbsent(platformSchedule.platformId, ignored -> new TreeSet<>())
                    .add(platformSchedule.entry);
            }
        }
        SCHEDULES_FOR_PLATFORM.clear();
        rebuilt.forEach((platformId, schedules) ->
            SCHEDULES_FOR_PLATFORM.put(platformId, java.util.Collections.unmodifiableSet(schedules)));
    }

    private record PlatformSchedule(long platformId, ScheduleEntryCompat entry) {
    }
}
