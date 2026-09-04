package cn.jstxjf_.rte.track;

import cn.jstxjf_.rte.data.BezierCurve.Vec3;

import java.util.ArrayList;
import java.util.List;

public interface TrackPath {

    Vec3 WORLD_UP = new Vec3(0, 1, 0);

    double length();

    TrackPose sampleByDistance(double distance);

    default TrackPose sampleByProgress(double progress) {
        final double len = length();
        return sampleByDistance(len <= 0 ? 0 : progress * len);
    }

    default List<TrackPose> sampleAtInterval(double interval) {
        final List<TrackPose> poses = new ArrayList<>();
        if (interval <= 0) {
            return poses;
        }
        final double len = length();
        for (double distance = 0; distance <= len + 1e-9; distance += interval) {
            poses.add(sampleByDistance(Math.min(distance, len)));
        }
        return poses;
    }

    static Vec3 normalize(Vec3 vector) {
        final double len = vector.length();
        return len < 1e-9 ? new Vec3(0, 0, 1) : vector.scale(1.0 / len);
    }

    static TrackPose poseFromTangent(Vec3 position, Vec3 rawTangent) {
        final Vec3 tangent = normalize(rawTangent);
        Vec3 right = tangent.cross(WORLD_UP);
        if (right.length() < 1e-6) {
            right = tangent.cross(new Vec3(1, 0, 0));
        }
        right = normalize(right);
        final Vec3 normal = normalize(right.cross(tangent));
        return new TrackPose(position, tangent, normal, right);
    }
}
