package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.PathData;
import org.mtr.core.data.Position;
import org.mtr.core.tool.Angle;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class PathDataCompat {

    public final PathData data;
    public final RailCompat rail;
    public final boolean reversePositions;
    public final long dwellTime;
    public final long dwellTimeMillis;
    public final double savedRailBaseId;
    public final long savedRailBaseIdLong;
    public final int stopIndex;
    public final double startDistance;
    public final double endDistance;

    public PathDataCompat(PathData pathData) {
        this.data = pathData;
        this.rail = new RailCompat(pathData.getRail());
        this.reversePositions = pathData.reversePositions;
        this.dwellTimeMillis = pathData.getDwellTime();
        this.dwellTime = this.dwellTimeMillis / 1000;
        this.savedRailBaseIdLong = pathData.getSavedRailBaseId();
        this.savedRailBaseId = this.savedRailBaseIdLong;
        this.stopIndex = pathData.getStopIndex();
        this.startDistance = pathData.getStartDistance();
        this.endDistance = pathData.getEndDistance();
    }

    private PathDataCompat(double railLength, long speedLimitKilometersPerHour, boolean platform,
                           long dwellTime, long savedRailBaseId, int stopIndex,
                           double startDistance, double endDistance) {
        this.data = null;
        this.rail = RailCompat.synthetic(railLength, speedLimitKilometersPerHour, platform);
        this.reversePositions = false;
        this.dwellTimeMillis = dwellTime;
        this.dwellTime = dwellTime / 1000;
        this.savedRailBaseIdLong = savedRailBaseId;
        this.savedRailBaseId = savedRailBaseId;
        this.stopIndex = stopIndex;
        this.startDistance = startDistance;
        this.endDistance = endDistance;
    }

    public static PathDataCompat synthetic(double railLength, long speedLimitKilometersPerHour, boolean platform,
                                           long dwellTime, long savedRailBaseId, int stopIndex,
                                           double startDistance, double endDistance) {
        return new PathDataCompat(railLength, speedLimitKilometersPerHour, platform, dwellTime,
            savedRailBaseId, stopIndex, startDistance, endDistance);
    }

    public static List<PathDataCompat> liveView(java.util.function.Supplier<List<PathData>> source) {
        return new java.util.AbstractList<>() {

            private List<PathData> cachedSource;
            private PathDataCompat[] cachedWrappers;

            @Override
            public PathDataCompat get(int index) {
                final List<PathData> current = source.get();
                if (current != cachedSource) {
                    cachedSource = current;
                    cachedWrappers = new PathDataCompat[current.size()];
                }
                if (index < 0 || index >= cachedWrappers.length) {
                    throw new IndexOutOfBoundsException(index);
                }
                if (cachedWrappers[index] == null) {
                    cachedWrappers[index] = new PathDataCompat(current.get(index));
                }
                return cachedWrappers[index];
            }

            @Override
            public int size() {
                return source.get().size();
            }
        };
    }

    public RailCompat getRail() {
        return rail;
    }

    public long getDwellTime() {
        return dwellTime;
    }

    public long getDwellTimeMillis() {
        return dwellTimeMillis;
    }

    public double getSavedRailBaseId() {
        return savedRailBaseId;
    }

    public long getSavedRailBaseIdLong() {
        return savedRailBaseIdLong;
    }

    public int getStopIndex() {
        return stopIndex;
    }

    public double getStartDistance() {
        return startDistance;
    }

    public double getEndDistance() {
        return endDistance;
    }

    public ScriptVector getPosition(double value) {
        return ScriptVector.of(data.getPosition(value));
    }

    public double getRailLength() {
        return data.getRailLength();
    }

    public double getSpeedLimitMetersPerMillisecond() {
        return data.getSpeedLimitMetersPerMillisecond();
    }

    public long getSpeedLimitKilometersPerHour() {
        return data.getSpeedLimitKilometersPerHour();
    }

    public boolean isDescending() {
        return data.isDescending();
    }

    public boolean matchesCondition(double value) {
        return data.matchesCondition(value);
    }

    public Position getOrderedPosition1() {
        return data.getOrderedPosition1();
    }

    public Position getOrderedPosition2() {
        return data.getOrderedPosition2();
    }

    public Angle getFacingStart() {
        return data.getFacingStart();
    }

    public String getHexId(boolean reverse) {
        return data.getHexId(reverse);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
