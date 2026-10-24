package cn.jstxjf_.rte.scripting.compat;

@SuppressWarnings("unused")
public final class ScheduleEntryCompat implements Comparable<ScheduleEntryCompat> {

    public final long arrivalMillis;
    public final int trainCars;
    public final long routeId;
    public final int currentStationIndex;

    public ScheduleEntryCompat(long arrivalMillis, int trainCars, long routeId, int currentStationIndex) {
        this.arrivalMillis = arrivalMillis;
        this.trainCars = trainCars;
        this.routeId = routeId;
        this.currentStationIndex = currentStationIndex;
    }

    @Override
    public int compareTo(ScheduleEntryCompat other) {
        final int byTime = Long.compare(arrivalMillis, other.arrivalMillis);
        if (byTime != 0) {
            return byTime;
        }
        final int byRoute = Long.compare(routeId, other.routeId);
        return byRoute != 0 ? byRoute : Integer.compare(currentStationIndex, other.currentStationIndex);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ScheduleEntryCompat entry)) {
            return false;
        }
        return arrivalMillis == entry.arrivalMillis && trainCars == entry.trainCars
            && routeId == entry.routeId && currentStationIndex == entry.currentStationIndex;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(arrivalMillis, trainCars, routeId, currentStationIndex);
    }
}
