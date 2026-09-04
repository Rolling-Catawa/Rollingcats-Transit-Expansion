package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.block.BlockDirectNode;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import org.mtr.mod.block.BlockNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CurveFitting {

    public static final int MODE = 2;
    private static final double MAX_TURN_DEGREES = 135;
    private static final double EPSILON = 1E-6;

    private CurveFitting() {
    }

    public static Result fit(World world, Rail source, List<DrawPoint> xzPoints, List<DrawPoint> yPoints) {
        if (!(((Object) source) instanceof RailExtraSupplier extra)) {
            return Result.reject("No RTE rail data");
        }
        final Position p1 = extra.getExtraPosition1();
        final Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) {
            return Result.reject("Missing rail endpoints");
        }
        if (xzPoints == null || xzPoints.size() < 2 || yPoints == null || yPoints.size() < 2) {
            return Result.reject("Draw both XZ and Y curves first");
        }


        final ArrayList<DrawPoint> xz = mergeNearbyT(sortedCopy(xzPoints), 0.01);
        final ArrayList<DrawPoint> y = mergeNearbyT(sortedCopy(yPoints), 0.01);
        final ArrayList<DrawPoint> samples = buildFitSamples(p1, p2, xz, y);
        final BezierCurve.Vec3 start = new BezierCurve.Vec3(p1.getX(), p1.getY(), p1.getZ());
        final BezierCurve.Vec3 end = new BezierCurve.Vec3(p2.getX(), p2.getY(), p2.getZ());
        final double distance = Math.max(start.distanceTo(end), 1);
        final ArrayList<DrawPoint> knots = simplifySamples(samples, Math.max(0.35, distance / 120D), 24);


        final double maxTurn = maxTurnDegrees(knots);
        if (maxTurn > MAX_TURN_DEGREES) {


            return Result.reject(String.format(java.util.Locale.ROOT, "Turn angle %.1f deg is too large", maxTurn));
        }
        final ArrayList<BezierCurve.Vec3> controls = buildCompositeControls(world, p1, p2, knots, xz);
        for (int i = 0; i < controls.size(); i++) {
            final BezierCurve.Vec3 c = controls.get(i);
            controls.set(i, new BezierCurve.Vec3(c.x - 0.5, c.y, c.z - 0.5));
        }
        final BezierCurve curve = new BezierCurve(0.1, controls);
        if (!Double.isFinite(curve.getLength()) || curve.getLength() < 1) {


            return Result.reject("Fitted curve has invalid length");
        }
        double maxAllowed = Math.max(100, distance * 10);
        if (curve.getLength() > maxAllowed) {


            return Result.reject(String.format(java.util.Locale.ROOT, "Curve too long: %.0f blocks (max %.0f)", curve.getLength(), maxAllowed));
        }
        final BezierCurve.Vec3 c1 = controls.size() > 1 ? controls.get(1) : start;
        final BezierCurve.Vec3 c2 = controls.size() > 2 ? controls.get(controls.size() - 2) : end;
        double angleStart = Math.atan2(c1.z - start.z, c1.x - start.x);
        double angleEnd = Math.atan2(end.z - c2.z, end.x - c2.x);


        return Result.accept(curve, angleStart, angleEnd);
    }

    private static ArrayList<DrawPoint> sortedCopy(List<DrawPoint> points) {
        final ArrayList<DrawPoint> copy = new ArrayList<>(points);
        copy.sort(Comparator.comparingDouble(point -> point.t));
        return copy;
    }

    private static ArrayList<DrawPoint> mergeNearbyT(ArrayList<DrawPoint> points, double gap) {
        final ArrayList<DrawPoint> result = new ArrayList<>();
        int i = 0;
        while (i < points.size()) {
            final double baseT = points.get(i).t;
            double sumT = 0, sumX = 0, sumY = 0, sumZ = 0;
            int count = 0;
            while (i < points.size() && points.get(i).t - baseT <= gap) {
                final DrawPoint p = points.get(i);
                sumT += p.t;
                sumX += p.x;
                sumY += p.y;
                sumZ += p.z;
                count++;
                i++;
            }
            result.add(new DrawPoint(sumT / count, sumX / count, sumY / count, sumZ / count));
        }
        return result;
    }

    private static double maxTurnDegrees(List<DrawPoint> points) {
        double max = 0;
        for (int i = 1; i < points.size() - 1; i++) {
            final DrawPoint a = points.get(i - 1);
            final DrawPoint b = points.get(i);
            final DrawPoint c = points.get(i + 1);
            final double ax = b.x - a.x;
            final double az = b.z - a.z;
            final double bx = c.x - b.x;
            final double bz = c.z - b.z;
            final double al = Math.sqrt(ax * ax + az * az);
            final double bl = Math.sqrt(bx * bx + bz * bz);
            if (al < 0.5 || bl < 0.5) {
                continue;
            }
            double dot = (ax * bx + az * bz) / (al * bl);
            dot = Math.max(-1, Math.min(1, dot));
            max = Math.max(max, Math.toDegrees(Math.acos(dot)));
        }
        return max;
    }

    private static ArrayList<DrawPoint> buildFitSamples(Position p1, Position p2, List<DrawPoint> xz, List<DrawPoint> y) {
        final ArrayList<Double> ts = new ArrayList<>();
        ts.add(0D);
        ts.add(1D);
        for (final DrawPoint point : xz) {
            ts.add(clamp(point.t, 0, 1));
        }
        for (final DrawPoint point : y) {
            ts.add(clamp(point.t, 0, 1));
        }
        ts.sort(Double::compare);

        final ArrayList<DrawPoint> samples = new ArrayList<>();
        double previous = Double.NaN;
        for (final double t : ts) {
            if (Double.isFinite(previous) && Math.abs(t - previous) < 1E-4) {
                continue;
            }
            final DrawPoint xzPoint = samplePoint(xz, t, p1, p2);
            final DrawPoint yPoint = samplePoint(y, t, p1, p2);
            samples.add(new DrawPoint(t, xzPoint.x, yPoint.y, xzPoint.z));
            previous = t;
        }
        return samples;
    }

    private static DrawPoint samplePoint(List<DrawPoint> points, double t, Position p1, Position p2) {
        final DrawPoint start = new DrawPoint(0, p1.getX() + 0.5, p1.getY(), p1.getZ() + 0.5);
        final DrawPoint end = new DrawPoint(1, p2.getX() + 0.5, p2.getY(), p2.getZ() + 0.5);
        if (points.isEmpty()) {
            return lerp(start, end, t);
        }
        DrawPoint previous = start;
        for (final DrawPoint next : points) {
            if (t <= next.t) {
                final double local = (t - previous.t) / Math.max(EPSILON, next.t - previous.t);
                return lerp(previous, next, local);
            }
            previous = next;
        }
        final double local = (t - previous.t) / Math.max(EPSILON, end.t - previous.t);
        return lerp(previous, end, local);
    }

    private static DrawPoint lerp(DrawPoint a, DrawPoint b, double t) {
        final double clamped = clamp(t, 0, 1);
        return new DrawPoint(
            a.t + (b.t - a.t) * clamped,
            a.x + (b.x - a.x) * clamped,
            a.y + (b.y - a.y) * clamped,
            a.z + (b.z - a.z) * clamped
        );
    }

    private static ArrayList<DrawPoint> simplifySamples(List<DrawPoint> samples, double tolerance, int maxSegments) {
        final ArrayList<DrawPoint> ordered = dedupeSamples(sortedCopy(samples));
        if (ordered.size() <= 2) {
            return ordered;
        }
        ArrayList<DrawPoint> simplified = ordered;
        double currentTolerance = tolerance;
        for (int attempt = 0; attempt < 8; attempt++) {
            final boolean[] keep = new boolean[ordered.size()];
            keep[0] = true;
            keep[ordered.size() - 1] = true;
            simplifyRange(ordered, 0, ordered.size() - 1, currentTolerance, keep);
            simplified = collectKept(ordered, keep);
            if (simplified.size() <= maxSegments + 1) {
                return simplified;
            }
            currentTolerance *= 1.6;
        }
        return reduceKnots(simplified, maxSegments + 1);
    }

    private static ArrayList<DrawPoint> dedupeSamples(List<DrawPoint> samples) {
        final ArrayList<DrawPoint> result = new ArrayList<>();
        DrawPoint previous = null;
        for (final DrawPoint point : samples) {
            if (previous == null || Math.abs(point.t - previous.t) > 1E-4 || distance(point, previous) > 0.2) {
                result.add(point);
                previous = point;
            }
        }
        return result;
    }

    private static void simplifyRange(List<DrawPoint> points, int start, int end, double tolerance, boolean[] keep) {
        if (end <= start + 1) {
            return;
        }
        double maxScore = 0;
        int index = -1;
        final DrawPoint a = points.get(start);
        final DrawPoint b = points.get(end);
        final double yFloor = 0.25;
        for (int i = start + 1; i < end; i++) {
            final double distance = pointDistanceToSegment(points.get(i), a, b);
            final double yDist = yDistanceToSegment(points.get(i), a, b);
            final double score = Math.max(distance / tolerance, yDist / yFloor);
            if (score > maxScore) {
                maxScore = score;
                index = i;
            }
        }
        if (index >= 0 && maxScore > 1.0) {
            keep[index] = true;
            simplifyRange(points, start, index, tolerance, keep);
            simplifyRange(points, index, end, tolerance, keep);
        }
    }

    private static double yDistanceToSegment(DrawPoint point, DrawPoint a, DrawPoint b) {
        final double segLen = b.t - a.t;
        if (segLen < EPSILON) {
            return Math.abs(point.y - a.y);
        }
        final double t = clamp((point.t - a.t) / segLen, 0, 1);
        final double interpolatedY = a.y + (b.y - a.y) * t;
        return Math.abs(point.y - interpolatedY);
    }

    private static ArrayList<DrawPoint> collectKept(List<DrawPoint> points, boolean[] keep) {
        final ArrayList<DrawPoint> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (keep[i]) {
                result.add(points.get(i));
            }
        }
        return result;
    }

    private static ArrayList<DrawPoint> reduceKnots(ArrayList<DrawPoint> points, int targetSize) {
        final ArrayList<DrawPoint> result = new ArrayList<>(points);
        while (result.size() > Math.max(2, targetSize)) {
            double minImportance = Double.MAX_VALUE;
            int removeIndex = -1;
            for (int i = 1; i < result.size() - 1; i++) {
                final double importance = pointDistanceToSegment(result.get(i), result.get(i - 1), result.get(i + 1));
                if (importance < minImportance) {
                    minImportance = importance;
                    removeIndex = i;
                }
            }
            if (removeIndex < 0) {
                break;
            }
            result.remove(removeIndex);
        }
        return result;
    }

    private static double pointDistanceToSegment(DrawPoint point, DrawPoint a, DrawPoint b) {
        final double ax = point.x - a.x;
        final double ay = point.y - a.y;
        final double az = point.z - a.z;
        final double bx = b.x - a.x;
        final double by = b.y - a.y;
        final double bz = b.z - a.z;
        final double lengthSquared = bx * bx + by * by + bz * bz;
        if (lengthSquared < EPSILON) {
            return distance(point, a);
        }
        final double t = clamp((ax * bx + ay * by + az * bz) / lengthSquared, 0, 1);
        final double px = a.x + bx * t;
        final double py = a.y + by * t;
        final double pz = a.z + bz * t;
        final double dx = point.x - px;
        final double dy = point.y - py;
        final double dz = point.z - pz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static double distance(DrawPoint a, DrawPoint b) {
        final double dx = b.x - a.x;
        final double dy = b.y - a.y;
        final double dz = b.z - a.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static ArrayList<BezierCurve.Vec3> buildCompositeControls(World world, Position p1, Position p2, List<DrawPoint> knots, List<DrawPoint> xz) {
        final ArrayList<BezierCurve.Vec3> controls = new ArrayList<>();
        if (knots.size() < 2) {
            controls.add(new BezierCurve.Vec3(p1.getX(), p1.getY(), p1.getZ()));
            controls.add(new BezierCurve.Vec3(p1.getX(), p1.getY(), p1.getZ()));
            controls.add(new BezierCurve.Vec3(p2.getX(), p2.getY(), p2.getZ()));
            controls.add(new BezierCurve.Vec3(p2.getX(), p2.getY(), p2.getZ()));
            return controls;
        }

        final ArrayList<BezierCurve.Vec3> points = new ArrayList<>();
        for (final DrawPoint knot : knots) {
            points.add(new BezierCurve.Vec3(knot.x, knot.y, knot.z));
        }
        controls.add(points.get(0));
        for (int i = 0; i < points.size() - 1; i++) {
            final BezierCurve.Vec3 start = points.get(i);
            final BezierCurve.Vec3 end = points.get(i + 1);
            final double length = Math.max(0.25, start.distanceTo(end));
            final BezierCurve.Vec3 tangentStart = tangentAt(world, p1, p2, knots, xz, i);
            final BezierCurve.Vec3 tangentEnd = tangentAt(world, p1, p2, knots, xz, i + 1);
            controls.add(start.add(tangentStart.scale(length / 3D)));
            controls.add(end.subtract(tangentEnd.scale(length / 3D)));
            controls.add(end);
        }
        return controls;
    }

    private static BezierCurve.Vec3 tangentAt(World world, Position p1, Position p2, List<DrawPoint> knots, List<DrawPoint> xz, int index) {
        if (index <= 0) {
            return withVerticalSlope(endpointTangent(world, p1, p2, xz, true), knots.get(0), knots.get(1));
        }
        if (index >= knots.size() - 1) {
            return withVerticalSlope(endpointTangent(world, p2, p1, xz, false), knots.get(knots.size() - 2), knots.get(knots.size() - 1));
        }
        final DrawPoint previous = knots.get(index - 1);
        final DrawPoint next = knots.get(index + 1);
        return normalize(new BezierCurve.Vec3(next.x - previous.x, next.y - previous.y, next.z - previous.z));
    }

    private static BezierCurve.Vec3 withVerticalSlope(BezierCurve.Vec3 horizontal, DrawPoint a, DrawPoint b) {
        final double dx = b.x - a.x;
        final double dz = b.z - a.z;
        final double horizontalLength = Math.max(1, Math.sqrt(dx * dx + dz * dz));
        return normalize(new BezierCurve.Vec3(horizontal.x, (b.y - a.y) / horizontalLength, horizontal.z));
    }

    private static BezierCurve.Vec3 endpointTangent(World world, Position from, Position to, List<DrawPoint> points, boolean start) {
        final Double nodeAngle = getBoundNodeAngle(world, from);
        final BezierCurve.Vec3 vector;
        if (nodeAngle != null) {
            vector = new BezierCurve.Vec3(Math.cos(nodeAngle), 0, Math.sin(nodeAngle));
            return orientEndpointTangent(normalize(vector), from, to, points, start);
        }
        if (points.size() >= 2) {
            final DrawPoint a = start ? points.get(0) : points.get(points.size() - 2);
            final DrawPoint b = start ? points.get(1) : points.get(points.size() - 1);
            vector = new BezierCurve.Vec3(b.x - a.x, 0, b.z - a.z);
            return orientEndpointTangent(normalize(vector), from, to, points, start);
        }
        vector = start
            ? new BezierCurve.Vec3(to.getX() - from.getX(), 0, to.getZ() - from.getZ())
            : new BezierCurve.Vec3(from.getX() - to.getX(), 0, from.getZ() - to.getZ());
        return normalize(vector);
    }

    private static BezierCurve.Vec3 orientEndpointTangent(BezierCurve.Vec3 vector, Position from, Position to, List<DrawPoint> points, boolean start) {
        BezierCurve.Vec3 desired = null;
        if (points.size() >= 2) {
            final DrawPoint a = start ? points.get(0) : points.get(points.size() - 2);
            final DrawPoint b = start ? points.get(1) : points.get(points.size() - 1);
            final double dx = b.x - a.x;
            final double dz = b.z - a.z;
            if (Math.abs(dx) > EPSILON || Math.abs(dz) > EPSILON) {
                desired = normalize(new BezierCurve.Vec3(dx, 0, dz));
            }
        }
        if (desired == null) {
            desired = start
                ? normalize(new BezierCurve.Vec3(to.getX() - from.getX(), 0, to.getZ() - from.getZ()))
                : normalize(new BezierCurve.Vec3(from.getX() - to.getX(), 0, from.getZ() - to.getZ()));
        }
        return vector.dot(desired) < 0 ? vector.scale(-1) : vector;
    }

    @Nullable
    private static Double getBoundNodeAngle(World world, Position position) {
        if (world == null || position == null) {
            return null;
        }
        final BlockPos blockPos = new BlockPos((int) position.getX(), (int) position.getY(), (int) position.getZ());
        try {
            final BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity != null && blockEntity.data instanceof BlockDirectNode.BlockEntityDirectNode directNode && directNode.isBound()) {
                return directNode.getAngleRadians();
            }
            final org.mtr.mapping.holder.BlockState state = world.getBlockState(blockPos);
            if (state != null && state.getBlock().data instanceof BlockNode) {
                return Math.toRadians(BlockNode.getAngle(state));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static BezierCurve.Vec3 normalize(BezierCurve.Vec3 vector) {
        final double length = Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
        if (length < EPSILON) {
            return new BezierCurve.Vec3(1, 0, 0);
        }
        return new BezierCurve.Vec3(vector.x / length, vector.y / length, vector.z / length);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public record DrawPoint(double t, double x, double y, double z) {
    }

    public record Result(boolean accepted, @Nullable BezierCurve curve, double angleStart, double angleEnd, String message) {
        public static Result accept(BezierCurve curve, double angleStart, double angleEnd) {
            return new Result(true, curve, angleStart, angleEnd, "");
        }

        public static Result reject(String message) {
            return new Result(false, null, Double.NaN, Double.NaN, message);
        }
    }
}
