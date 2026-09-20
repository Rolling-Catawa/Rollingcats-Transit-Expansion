package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.PathData;
import org.mtr.core.data.Position;
import org.mtr.core.tool.Angle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    /** MTR 3 field names, retaining the actual traversal direction. */
    public final Position startingPos;
    public final Position endingPos;

    public PathDataCompat(PathData pathData) {
        this.data = pathData;
        this.rail = new RailCompat(pathData.getRail(), pathData.reversePositions);
        this.reversePositions = pathData.reversePositions;
        this.dwellTimeMillis = pathData.getDwellTime();
        // MTR 3 stores this value in half-seconds (10 game ticks).
        this.dwellTime = this.dwellTimeMillis / 500;
        this.savedRailBaseIdLong = pathData.getSavedRailBaseId();
        this.savedRailBaseId = this.savedRailBaseIdLong;
        this.stopIndex = pathData.getStopIndex();
        this.startDistance = pathData.getStartDistance();
        this.endDistance = pathData.getEndDistance();
        final Position ordered1 = pathData.getOrderedPosition1();
        final Position ordered2 = pathData.getOrderedPosition2();
        this.startingPos = pathData.reversePositions ? ordered2 : ordered1;
        this.endingPos = pathData.reversePositions ? ordered1 : ordered2;
    }

    private PathDataCompat(double railLength, long speedLimitKilometersPerHour, boolean platform,
                           long dwellTime, long savedRailBaseId, int stopIndex,
                           double startDistance, double endDistance) {
        this.data = null;
        this.rail = RailCompat.synthetic(railLength, speedLimitKilometersPerHour, platform);
        this.reversePositions = false;
        this.dwellTimeMillis = dwellTime;
        this.dwellTime = dwellTime / 500;
        this.savedRailBaseIdLong = savedRailBaseId;
        this.savedRailBaseId = savedRailBaseId;
        this.stopIndex = stopIndex;
        this.startDistance = startDistance;
        this.endDistance = endDistance;
        this.startingPos = new Position(0, 0, 0);
        this.endingPos = new Position(0, 0, 0);
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
                if (current != cachedSource || cachedWrappers.length != current.size()) {
                    cachedSource = current;
                    cachedWrappers = new PathDataCompat[current.size()];
                }
                if (index < 0 || index >= cachedWrappers.length) {
                    throw new IndexOutOfBoundsException(index);
                }
                if (cachedWrappers[index] == null || cachedWrappers[index].data != current.get(index)) {
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
        return rail.getPosition(value);
    }

    public double getRailLength() {
        return data == null ? rail.getLength() : data.getRailLength();
    }

    public double getSpeedLimitMetersPerMillisecond() {
        return data == null ? rail.getSpeedLimitMetersPerMillisecond(reversePositions) : data.getSpeedLimitMetersPerMillisecond();
    }

    public long getSpeedLimitKilometersPerHour() {
        return data == null ? rail.getSpeedLimitKilometersPerHour(reversePositions) : data.getSpeedLimitKilometersPerHour();
    }

    public boolean isDescending() {
        return data != null && data.isDescending();
    }

    public boolean isSameRail(PathDataCompat pathData) {
        return pathData != null && startingPos.equals(pathData.startingPos) && endingPos.equals(pathData.endingPos);
    }

    public boolean isOppositeRail(PathDataCompat pathData) {
        return pathData != null && startingPos.equals(pathData.endingPos) && endingPos.equals(pathData.startingPos);
    }

    /** Deterministic equivalent of MTR 3 PathData#getRailProduct. */
    public UUID getRailProduct() {
        final long start = blockPosAsLong(startingPos);
        final long end = blockPosAsLong(endingPos);
        return new UUID(Math.min(start, end), Math.max(start, end));
    }

    private static long blockPosAsLong(Position position) {
        return (position.getX() & 0x3FFFFFFL) << 38
            | (position.getZ() & 0x3FFFFFFL) << 12
            | position.getY() & 0xFFFL;
    }

    public boolean matchesCondition(double value) {
        return value >= startDistance;
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
        return data == null ? "MetadataPath[" + startDistance + ", " + endDistance + "]" : data.toString();
    }
}
