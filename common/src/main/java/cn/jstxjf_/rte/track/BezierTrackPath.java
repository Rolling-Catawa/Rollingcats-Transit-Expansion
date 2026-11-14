package cn.jstxjf_.rte.track;

import cn.jstxjf_.rte.data.BezierCurve;
import cn.jstxjf_.rte.data.BezierCurve.Vec3;

public class BezierTrackPath implements TrackPath {

    private final BezierCurve curve;
    private final double length;

    public BezierTrackPath(BezierCurve curve) {
        this.curve = curve;
        this.length = curve.getLength();
    }

    @Override
    public double length() {
        return length;
    }

    private double clamp(double distance) {
        return Math.max(0, Math.min(length, distance));
    }

    @Override
    public TrackPose sampleByDistance(double distance) {
        final double d = clamp(distance);
        final double epsilon = Math.max(1e-3, length * 1e-3);
        final Vec3 position = curve.getPosition(d);
        final Vec3 tangent = d + epsilon <= length
            ? curve.getPosition(d + epsilon).subtract(position)
            : position.subtract(curve.getPosition(d - epsilon));
        return TrackPath.poseFromTangent(position, tangent);
    }
}
