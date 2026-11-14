package cn.jstxjf_.rte.track;

import cn.jstxjf_.rte.data.BezierCurve.Vec3;

public record TrackPose(Vec3 position, Vec3 tangent, Vec3 normal, Vec3 right) {
}
