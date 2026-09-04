package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.RailMath;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;

@SuppressWarnings("unused")
public final class RailCompat {

    public final Rail data;
    public final RailMath railMath;
    public final double speedLimit1MetersPerMillisecond;
    public final double speedLimit2MetersPerMillisecond;

    private final double syntheticLength;
    private final boolean synthetic;

    private RailCompat(double length, long speedLimitKilometersPerHour, boolean platform) {
        this.data = null;
        this.railMath = null;
        this.speedLimit1MetersPerMillisecond = speedLimitKilometersPerHour / 3600.0;
        this.speedLimit2MetersPerMillisecond = this.speedLimit1MetersPerMillisecond;
        this.railType = new RailTypeCompat(speedLimitKilometersPerHour, platform);
        this.syntheticLength = length;
        this.synthetic = true;
    }

    public static RailCompat synthetic(double length, long speedLimitKilometersPerHour, boolean platform) {
        return new RailCompat(length, speedLimitKilometersPerHour, platform);
    }

    public RailCompat(Rail rail) {
        this.data = rail;
        this.railMath = rail.railMath;
        this.speedLimit1MetersPerMillisecond = rail.speedLimit1MetersPerMillisecond;
        this.speedLimit2MetersPerMillisecond = rail.speedLimit2MetersPerMillisecond;
        this.railType = new RailTypeCompat(rail);
        this.syntheticLength = 0;
        this.synthetic = false;
    }

    public final RailTypeCompat railType;

    public static final class RailTypeCompat {
        public final long speedLimit;
        public final boolean isSavedRail;
        public final String name;

        RailTypeCompat(long speedLimitKilometersPerHour, boolean platform) {
            this.speedLimit = speedLimitKilometersPerHour;
            this.isSavedRail = platform;
            this.name = platform ? "PLATFORM" : "NORMAL";
        }

        RailTypeCompat(Rail rail) {
            long limit;
            try {
                limit = rail.getSpeedLimitKilometersPerHour(false);
                if (limit <= 0) {
                    limit = rail.getSpeedLimitKilometersPerHour(true);
                }
            } catch (Exception e) {
                limit = 0;
            }
            this.speedLimit = limit;
            this.isSavedRail = rail.isPlatform() || rail.isSiding();
            this.name = this.isSavedRail ? (rail.isPlatform() ? "PLATFORM" : "SIDING") : "NORMAL";
        }

        public long getSpeedLimit() {
            return speedLimit;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public ScriptVector getPosition(double value) {
        return getPosition(value, false);
    }

    public ScriptVector getPosition(double value, boolean reverse) {
        return synthetic ? new ScriptVector(0, 0, 0) : ScriptVector.of(railMath.getPosition(value, reverse));
    }

    public double getLength() {
        return synthetic ? syntheticLength : railMath.getLength();
    }

    public String getShape() {
        if (synthetic) {
            return "STRAIGHT";
        }
        final Rail.Shape shape = railMath.getShape();
        return shape == null ? "" : shape.name();
    }

    public String getHexId() {
        return synthetic ? "" : data.getHexId();
    }

    public boolean isValid() {
        return synthetic || data.isValid();
    }

    public boolean isPlatform() {
        return synthetic ? railType.isSavedRail : data.isPlatform();
    }

    public boolean isSiding() {
        return !synthetic && data.isSiding();
    }

    public boolean canTurnBack() {
        return !synthetic && data.canTurnBack();
    }

    public boolean canAccelerate() {
        return synthetic || data.canAccelerate();
    }

    public TransportMode getTransportMode() {
        return synthetic ? TransportMode.TRAIN : data.getTransportMode();
    }

    public int getOpeningDirection() {
        return synthetic ? 0 : ((cn.jstxjf_.rte.data.RailExtraSupplier) (Object) data).getOpeningDirection();
    }

    public int getOpeningDirectionRaw() {
        return synthetic ? 0 : ((cn.jstxjf_.rte.data.RailExtraSupplier) (Object) data).getOpeningDirectionRaw();
    }

    public Angle getStartAngle(boolean reverse) {
        return synthetic ? Angle.N : data.getStartAngle(reverse);
    }

    public double getSpeedLimitMetersPerMillisecond(boolean reverse) {
        return synthetic ? speedLimit1MetersPerMillisecond : data.getSpeedLimitMetersPerMillisecond(reverse);
    }

    public long getSpeedLimitKilometersPerHour(boolean reverse) {
        return synthetic ? railType.speedLimit : data.getSpeedLimitKilometersPerHour(reverse);
    }

    public boolean closeTo(Position position, double radius) {
        return !synthetic && data.closeTo(position, radius);
    }

    @Override
    public String toString() {
        return synthetic ? "SyntheticRail" : data.toString();
    }
}
