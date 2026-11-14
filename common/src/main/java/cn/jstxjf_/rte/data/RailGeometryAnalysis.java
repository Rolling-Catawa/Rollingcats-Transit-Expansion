package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.data.BezierCurve.Vec3;
import cn.jstxjf_.rte.track.RailTrackPath;
import org.mtr.core.data.Rail;

import java.util.ArrayList;
import java.util.List;

public final class RailGeometryAnalysis {

    public static final int RATING_GREEN = 0;
    public static final int RATING_YELLOW = 1;
    public static final int RATING_RED = 2;

    private static final double STRAIGHT_RADIUS_THRESHOLD = 5000;
    private static final double COMFORT_LATERAL_ACCEL = 1.5;
    private static final double MAX_SUGGESTED_KPH = 350;

    public record Analysis(double lengthMeters, Double minRadiusMeters, double maxGradePermille, double suggestedSpeedKph, int rating) {
    }

    public static Analysis analyze(Rail rail) {
        try {
            final RailTrackPath path = new RailTrackPath(rail);
            final double length = path.length();
            if (!Double.isFinite(length) || length < 1) {
                return null;
            }

            final double step = Math.max(0.5, Math.min(3, length / 256));
            final List<Vec3> points = new ArrayList<>();
            for (double distance = 0; distance <= length + 1e-9; distance += step) {
                points.add(path.sampleByDistance(Math.min(distance, length)).position());
            }
            if (points.size() < 3) {
                return null;
            }

            Double minRadius = null;
            for (int i = 1; i < points.size() - 1; i++) {
                final Double radius = circumradiusXZ(points.get(i - 1), points.get(i), points.get(i + 1));
                if (radius != null && radius < STRAIGHT_RADIUS_THRESHOLD && (minRadius == null || radius < minRadius)) {
                    minRadius = radius;
                }
            }

            double maxGrade = 0;
            for (int i = 1; i < points.size(); i++) {
                final Vec3 a = points.get(i - 1);
                final Vec3 b = points.get(i);
                final double dx = b.x - a.x;
                final double dz = b.z - a.z;
                final double horizontal = Math.sqrt(dx * dx + dz * dz);
                if (horizontal > 0.05) {
                    maxGrade = Math.max(maxGrade, Math.abs(b.y - a.y) / horizontal * 1000);
                }
            }

            final double suggestedSpeed = minRadius == null
                ? MAX_SUGGESTED_KPH
                : Math.min(MAX_SUGGESTED_KPH, Math.sqrt(COMFORT_LATERAL_ACCEL * minRadius) * 3.6);

            final int radiusRating = minRadius == null ? RATING_GREEN
                : minRadius >= 120 ? RATING_GREEN
                : minRadius >= 35 ? RATING_YELLOW
                : RATING_RED;
            final int gradeRating = maxGrade <= 45 ? RATING_GREEN
                : maxGrade <= 90 ? RATING_YELLOW
                : RATING_RED;

            return new Analysis(length, minRadius, maxGrade, suggestedSpeed, Math.max(radiusRating, gradeRating));
        } catch (Exception e) {
            return null;
        }
    }

    static Double circumradiusXZ(Vec3 p1, Vec3 p2, Vec3 p3) {
        final double ax = p1.x, az = p1.z;
        final double bx = p2.x, bz = p2.z;
        final double cx = p3.x, cz = p3.z;
        final double a = Math.hypot(bx - cx, bz - cz);
        final double b = Math.hypot(ax - cx, az - cz);
        final double c = Math.hypot(ax - bx, az - bz);
        final double cross = (bx - ax) * (cz - az) - (bz - az) * (cx - ax);
        final double area = Math.abs(cross) / 2;
        if (area < 1e-6) {
            return null;
        }
        return a * b * c / (4 * area);
    }

    private RailGeometryAnalysis() {
    }
}
