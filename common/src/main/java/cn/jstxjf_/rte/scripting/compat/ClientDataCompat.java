package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.TransportMode;
import org.mtr.mod.client.MinecraftClientData;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ClientDataCompat {

    private static final ClientDataCompat INSTANCE = new ClientDataCompat();
    private static long lastRefreshTime = 0;
    private static final long REFRESH_INTERVAL_MS = 500;

    public static Set<StationCompat> STATIONS = new LinkedHashSet<>();
    public static Set<PlatformCompat> PLATFORMS = new LinkedHashSet<>();
    public static Set<SidingCompat> SIDINGS = new LinkedHashSet<>();
    public static Set<RouteCompat> ROUTES = new LinkedHashSet<>();

    public static final DataCacheCompat DATA_CACHE = new DataCacheCompat();

    public static String DASHBOARD_SEARCH = "";
    public static String ROUTES_PLATFORMS_SEARCH = "";
    public static String ROUTES_PLATFORMS_SELECTED_SEARCH = "";
    public static String TRAINS_SEARCH = "";
    public static String EXIT_PARENTS_SEARCH = "";
    public static String EXIT_DESTINATIONS_SEARCH = "";

    public static void ensureFresh() {
        long now = System.currentTimeMillis();
        if (now - lastRefreshTime < REFRESH_INTERVAL_MS) return;
        lastRefreshTime = now;

        MinecraftClientData data = MinecraftClientData.getInstance();

        STATIONS = data.stations.stream()
                .map(StationCompat::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        PLATFORMS = data.platforms.stream()
                .map(PlatformCompat::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        SIDINGS = data.sidings.stream()
                .map(SidingCompat::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        ROUTES = data.routes.stream()
                .map(RouteCompat::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        try {
            DATA_CACHE.refresh();
        } catch (Exception | Error e) {
            cn.jstxjf_.rte.RTE.LOGGER.error("[DataCache] refresh failed", e);
        }
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

    public DataCacheCompat getDataCache() {
        return DATA_CACHE;
    }

    public static <T> Set<T> getFilteredDataSet(TransportMode transportMode, Set<T> dataSet) {
        return dataSet;
    }

    public static boolean hasPermission() {
        return MinecraftClientData.hasPermission();
    }
}
