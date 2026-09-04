package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.data.BezierCurve.Vec3;
import cn.jstxjf_.rte.track.RailTrackPath;
import cn.jstxjf_.rte.track.TrackPose;
import org.mtr.core.data.Rail;

import java.util.ArrayList;
import java.util.List;

public final class RailPresets {

    public enum Preset {
        STRAIGHT,
        SOFT,
        TIGHT,
        KEEP_START,
        KEEP_END
    }

    public record PresetResult(BezierCurve curve, double angleStartRad, double angleEndRad) {
    }

    public static PresetResult compute(Rail rail, Preset preset) {
        final RailTrackPath path = new RailTrackPath(rail);
        final double length = path.length();
        if (!Double.isFinite(length) || length < 1) {
            return null;
        }

        final TrackPose startPose = path.sampleByDistance(0);
        final TrackPose endPose = path.sampleByDistance(length);
        final Vec3 start = startPose.position();
        final Vec3 end = endPose.position();

        final double chordX = end.x - start.x;
        final double chordZ = end.z - start.z;
        final double chordLength = Math.sqrt(chordX * chordX + chordZ * chordZ);
        if (chordLength < 0.5) {
            return null;
        }
        final double chordRad = Math.atan2(chordZ, chordX);

        final double startTangentRad = horizontalRad(startPose.tangent(), chordRad);
        final double endInwardRad = horizontalRad(endPose.tangent().scale(-1), chordRad + Math.PI);

        final double softArm = chordLength / 2;
        final double tightArm = chordLength / 4;
        final double chordArm = chordLength / 3;

        final Vec3 c1;
        final Vec3 c2;
        switch (preset) {
            case STRAIGHT -> {
                c1 = start.lerpTo(end, 1.0 / 3.0);
                c2 = start.lerpTo(end, 2.0 / 3.0);
            }
            case SOFT -> {
                c1 = arm(start, startTangentRad, softArm);
                c2 = arm(end, endInwardRad, softArm);
            }
            case TIGHT -> {
                c1 = arm(start, startTangentRad, tightArm);
                c2 = arm(end, endInwardRad, tightArm);
            }
            case KEEP_START -> {
                c1 = arm(start, startTangentRad, softArm);
                c2 = arm(end, chordRad + Math.PI, chordArm);
            }
            case KEEP_END -> {
                c1 = arm(start, chordRad, chordArm);
                c2 = arm(end, endInwardRad, softArm);
            }
            default -> {
                return null;
            }
        }

        final List<Vec3> controls = new ArrayList<>(4);
        controls.add(toBezierSpace(start));
        controls.add(toBezierSpace(c1));
        controls.add(toBezierSpace(c2));
        controls.add(toBezierSpace(end));
        final BezierCurve curve = new BezierCurve(0.1, controls);
        if (!Double.isFinite(curve.getLength()) || curve.getLength() < 1) {
            return null;
        }

        final double angleStart = Math.atan2(c1.z - start.z, c1.x - start.x);
        final double angleEnd = Math.atan2(end.z - c2.z, end.x - c2.x);
        return new PresetResult(curve, angleStart, angleEnd);
    }

    private static double horizontalRad(Vec3 tangent, double fallbackRad) {
        final double horizontal = Math.sqrt(tangent.x * tangent.x + tangent.z * tangent.z);
        return horizontal < 1e-6 ? fallbackRad : Math.atan2(tangent.z, tangent.x);
    }

    private static Vec3 arm(Vec3 base, double rad, double armLength) {
        return new Vec3(base.x + Math.cos(rad) * armLength, base.y, base.z + Math.sin(rad) * armLength);
    }

    static Vec3 toBezierSpace(Vec3 worldPoint) {
        return new Vec3(worldPoint.x - 0.5, worldPoint.y, worldPoint.z - 0.5);
    }

    private RailPresets() {
    }
}
