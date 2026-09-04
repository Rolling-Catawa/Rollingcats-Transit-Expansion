package cn.jstxjf_.rte.track;

import cn.jstxjf_.rte.data.BezierCurve.Vec3;
import org.mtr.core.data.Rail;

public class RailTrackPath implements TrackPath {

    private final Rail rail;
    private final double length;

    public RailTrackPath(Rail rail) {
        this.rail = rail;
        this.length = rail.railMath.getLength();
    }

    @Override
    public double length() {
        return length;
    }

    private double clamp(double distance) {
        return Math.max(0, Math.min(length, distance));
    }

    private Vec3 positionAt(double distance) {
        final org.mtr.core.tool.Vector vector = rail.railMath.getPosition(clamp(distance), false);
        return new Vec3(vector.x, vector.y, vector.z);
    }

    @Override
    public TrackPose sampleByDistance(double distance) {
        final double d = clamp(distance);
        final double epsilon = Math.max(1e-3, length * 1e-3);
        final Vec3 position = positionAt(d);
        final Vec3 tangent = d + epsilon <= length
            ? positionAt(d + epsilon).subtract(position)
            : position.subtract(positionAt(d - epsilon));
        return TrackPath.poseFromTangent(position, tangent);
    }
}
