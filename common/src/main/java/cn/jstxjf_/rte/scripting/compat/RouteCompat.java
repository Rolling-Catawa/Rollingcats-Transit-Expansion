package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.Route;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.data.RouteType;
import org.mtr.core.data.SimplifiedRoute;
import org.mtr.core.data.SimplifiedRoutePlatform;
import org.mtr.core.data.TransportMode;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RouteCompat {

    public final double id;
    public final String name;
    public final int color;
    public final TransportMode transportMode;
    public final Route.CircularState circularState;
    public final String routeNumber;
    public final boolean isHidden;
    public final RouteType routeType;
    public final boolean isLightRailRoute;
    public final boolean disableNextStationAnnouncements;
    public final List<RoutePlatformCompat> platformIds;

    private final Route delegate;

    public RouteCompat(Route delegate) {
        this.delegate = delegate;
        this.id = delegate.getId();
        this.name = delegate.getName();
        this.color = delegate.getColor();
        this.transportMode = delegate.getTransportMode();
        this.circularState = delegate.getCircularState();
        this.routeNumber = delegate.getRouteNumber();
        this.isHidden = delegate.getHidden();
        this.routeType = delegate.getRouteType();
        this.isLightRailRoute = this.routeType == RouteType.LIGHT_RAIL;
        this.disableNextStationAnnouncements = false;
        List<RoutePlatformCompat> pids = new ArrayList<>();
        for (RoutePlatformData rpd : delegate.getRoutePlatforms()) {
            pids.add(new RoutePlatformCompat(rpd));
        }
        this.platformIds = pids;
    }

    public RouteCompat(SimplifiedRoute simplified) {
        this.delegate = null;
        this.id = simplified.getId();
        this.name = simplified.getName();
        this.color = simplified.getColor();
        this.transportMode = TransportMode.TRAIN;
        this.circularState = simplified.getCircularState();
        this.routeNumber = "";
        this.isHidden = false;
        this.routeType = RouteType.NORMAL;
        this.isLightRailRoute = false;
        this.disableNextStationAnnouncements = false;
        List<RoutePlatformCompat> pids = new ArrayList<>();
        for (SimplifiedRoutePlatform simplifiedRoutePlatform : simplified.getPlatforms()) {
            pids.add(new RoutePlatformCompat(simplifiedRoutePlatform.getPlatformId()));
        }
        this.platformIds = pids;
    }

    public Route getMtr4Route() {
        return delegate;
    }

    public String getDestination(int index) {
        return delegate != null ? delegate.getDestination(index) : null;
    }

    public long getFirstPlatformId() {
        if (platformIds.isEmpty()) return 0;
        return platformIds.get(0).platformId;
    }

    public long getLastPlatformId() {
        if (platformIds.isEmpty()) return 0;
        return platformIds.get(platformIds.size() - 1).platformId;
    }

    public int getPlatformIdIndex(long platformId) {
        for (int i = 0; i < platformIds.size(); i++) {
            if (platformIds.get(i).platformId == platformId) return i;
        }
        return -1;
    }

    public boolean containsPlatformId(long platformId) {
        return getPlatformIdIndex(platformId) >= 0;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public double getId() {
        return id;
    }
}
