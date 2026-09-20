package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.Station;
import org.mtr.core.data.StationExit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class StationCompat {

    private static final Map<Long, StationCompat> INTERN = new ConcurrentHashMap<>();

    public static StationCompat of(Station delegate) {
        if (delegate == null) {
            return null;
        }
        return registerLookups(INTERN.compute(delegate.getId(), (ignored, current) ->
            current != null && current.matches(delegate) ? current : new StationCompat(delegate)));
    }

    public static StationCompat ofFallback(long id, String name) {
        return registerLookups(INTERN.computeIfAbsent(id, ignored -> new StationCompat(id, name)));
    }

    private boolean matches(Station station) {
        return delegate == station || delegate != null
            && id == station.getId()
            && color == station.getColor()
            && zone == (int) station.getZone1()
            && java.util.Objects.equals(name, station.getName());
    }

    private static StationCompat registerLookups(StationCompat station) {
        ClientDataCompat.DATA_CACHE.ensureStationEntries(station);
        return station;
    }

    public static java.util.Collection<StationCompat> internValues() {
        return INTERN.values();
    }

    public final double id;
    public final String name;
    public final int color;
    public final int zone;
    public final Map<String, List<String>> exits;

    private final Station delegate;

    public StationCompat(Station delegate) {
        this.delegate = delegate;
        this.id = delegate.getId();
        this.name = delegate.getName();
        this.color = delegate.getColor();
        this.zone = (int) delegate.getZone1();
        this.exits = buildExitsMap(delegate);
    }

    private StationCompat(long id, String name) {
        this.delegate = null;
        this.id = id;
        this.name = name == null ? "" : name;
        this.color = 0;
        this.zone = 0;
        this.exits = new LinkedHashMap<>();
    }

    private static Map<String, List<String>> buildExitsMap(Station station) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (station.getExits() == null) return result;
        for (StationExit exit : station.getExits()) {
            List<String> destinations = new ArrayList<>();
            if (exit.getDestinations() != null) {
                destinations.addAll(exit.getDestinations());
            }
            result.put(exit.getName(), destinations);
        }
        return result;
    }

    public Station getMtr4Station() {
        return delegate;
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

    public int getZone() {
        return zone;
    }

    public long getZone1() {
        return delegate != null ? delegate.getZone1() : 0;
    }

    public long getZone2() {
        return delegate != null ? delegate.getZone2() : 0;
    }

    public long getZone3() {
        return delegate != null ? delegate.getZone3() : 0;
    }

    public Map<String, List<String>> getExits() {
        return exits;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StationCompat)) {
            return false;
        }
        return id == ((StationCompat) other).id;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(id);
    }
}
