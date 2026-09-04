package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.mixin.RailAccessor;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.RailMath;
import org.mtr.core.tool.Angle;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEConfig;

public class RailAngleExtra {

    private static Unsafe unsafe;
    private static long offsetAngleDegrees;
    private static long offsetAngleRadians;
    private static long offsetSin;
    private static long offsetCos;
    private static long offsetTan;
    private static long offsetHalfTan;
    private static long offsetRailMath;
    private static long offsetRailMathH1;
    private static long offsetRailMathK1;
    private static long offsetRailMathR1;
    private static long offsetRailMathTStart1;
    private static long offsetRailMathTEnd1;
    private static long offsetRailMathReverseT1;
    private static long offsetRailMathIsStraight1;
    private static long offsetRailMathH2;
    private static long offsetRailMathK2;
    private static long offsetRailMathR2;
    private static long offsetRailMathTStart2;
    private static long offsetRailMathTEnd2;
    private static long offsetRailMathReverseT2;
    private static long offsetRailMathIsStraight2;
    private static long offsetRailSpeed1Mpm;
    private static long offsetRailSpeed2Mpm;
    private static volatile boolean invokerWarned;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            offsetAngleDegrees = unsafe.objectFieldOffset(Angle.class.getDeclaredField("angleDegrees"));
            offsetAngleRadians = unsafe.objectFieldOffset(Angle.class.getDeclaredField("angleRadians"));
            offsetSin = unsafe.objectFieldOffset(Angle.class.getDeclaredField("sin"));
            offsetCos = unsafe.objectFieldOffset(Angle.class.getDeclaredField("cos"));
            offsetTan = unsafe.objectFieldOffset(Angle.class.getDeclaredField("tan"));
            offsetHalfTan = unsafe.objectFieldOffset(Angle.class.getDeclaredField("halfTan"));
            offsetRailMath = unsafe.objectFieldOffset(Rail.class.getDeclaredField("railMath"));
            offsetRailMathH1 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("h1"));
            offsetRailMathK1 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("k1"));
            offsetRailMathR1 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("r1"));
            offsetRailMathTStart1 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("tStart1"));
            offsetRailMathTEnd1 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("tEnd1"));
            offsetRailMathReverseT1 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("reverseT1"));
            offsetRailMathIsStraight1 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("isStraight1"));
            offsetRailMathH2 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("h2"));
            offsetRailMathK2 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("k2"));
            offsetRailMathR2 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("r2"));
            offsetRailMathTStart2 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("tStart2"));
            offsetRailMathTEnd2 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("tEnd2"));
            offsetRailMathReverseT2 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("reverseT2"));
            offsetRailMathIsStraight2 = unsafe.objectFieldOffset(RailMath.class.getDeclaredField("isStraight2"));
            offsetRailSpeed1Mpm = unsafe.objectFieldOffset(Rail.class.getDeclaredField("speedLimit1MetersPerMillisecond"));
            offsetRailSpeed2Mpm = unsafe.objectFieldOffset(Rail.class.getDeclaredField("speedLimit2MetersPerMillisecond"));
            try {
                ((AngleExtra)(Object)Angle.S).rte$fromRadians(1.0);
            } catch (Throwable t) {
                RTE.LOGGER.error("[RailAngleExtra] @Invoker test FAILED: {}", t.toString());
            }
        } catch (Exception e) {
            unsafe = null;
            RTE.LOGGER.error("[RailAngleExtra] Unsafe init FAILED", e);
        }
    }

    public static Angle fromDegrees(double degrees) {
        return fromRadians(Math.toRadians(degrees));
    }

    public static double wrapRadians(double radians) {
        radians = radians % (2 * Math.PI);
        if (radians < 0) radians += 2 * Math.PI;
        return radians;
    }

    public static Angle fromRadians(double radians) {
        if (!Double.isFinite(radians)) {
            return Angle.fromAngle(0);
        }
        radians = wrapRadians(radians);
        try {
            return ((AngleExtra)(Object)Angle.S).rte$fromRadians(radians);
        } catch (Throwable t) {
            if (!invokerWarned) {
                invokerWarned = true;
                RTE.LOGGER.warn("[RailAngleExtra] @Invoker fallback to Unsafe: {}", t.toString());
            }
        }
        Angle closest = Angle.fromAngle((float) Math.toDegrees(radians));
        if (unsafe == null) return closest;

        try {
            float deg = (float) Math.toDegrees(radians);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            double tan = cos == 0 ? Double.MAX_VALUE : sin / cos;
            double halfRad = radians / 2.0;
            double halfTan = Math.cos(halfRad) == 0 ? Double.MAX_VALUE : Math.sin(halfRad) / Math.cos(halfRad);

            unsafe.putFloat(closest, offsetAngleDegrees, deg);
            unsafe.putDouble(closest, offsetAngleRadians, radians);
            unsafe.putDouble(closest, offsetSin, sin);
            unsafe.putDouble(closest, offsetCos, cos);
            unsafe.putDouble(closest, offsetTan, tan);
            unsafe.putDouble(closest, offsetHalfTan, halfTan);
        } catch (Exception ignored) {}

        return closest;
    }

    public static void restore(Angle angle) {
        if (unsafe == null || angle == null) return;
        try {
            int ordinal = angle.ordinal();
            if (ordinal < 0) return;
            double radians = ordinal * Math.PI / 8.0;
            float deg = ordinal * 22.5f;
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            double tan = cos == 0 ? Double.MAX_VALUE : sin / cos;
            double halfRad = radians / 2.0;
            double halfTan = Math.cos(halfRad) == 0 ? Double.MAX_VALUE : Math.sin(halfRad) / Math.cos(halfRad);

            unsafe.putFloat(angle, offsetAngleDegrees, deg);
            unsafe.putDouble(angle, offsetAngleRadians, radians);
            unsafe.putDouble(angle, offsetSin, sin);
            unsafe.putDouble(angle, offsetCos, cos);
            unsafe.putDouble(angle, offsetTan, tan);
            unsafe.putDouble(angle, offsetHalfTan, halfTan);
        } catch (Exception ignored) {}
    }

    public static void withAngle(double radians, java.util.function.Consumer<Angle> action) {
        Angle angle = fromRadians(radians);
        try {
            action.accept(angle);
        } finally {
            restore(angle);
        }
    }

    public static void withAngles(double rad1, double rad2, BiConsumer<Angle, Angle> action) {
        Angle a1 = fromRadians(rad1);
        Angle a2Raw = Angle.fromAngle((float) Math.toDegrees(rad2));
        Angle a2;
        if (a1 == a2Raw) {
            double diff = (rad1 - rad2) % (2 * Math.PI);
            if (diff < 0) diff += 2 * Math.PI;
            if (diff > Math.PI) diff = 2 * Math.PI - diff;

            if (diff < 0.01) {
                a2 = a1;
            } else if (Math.abs(diff - Math.PI) < 0.01) {
                a2 = a1.getOpposite();
                injectValues(a2, rad2);
            } else {
                int nextOrdinal = (a2Raw.ordinal() + 1) % Angle.values().length;
                a2 = Angle.values()[nextOrdinal];
                injectValues(a2, rad2);
            }
        } else {
            a2 = fromRadians(rad2);
        }
        try {
            action.accept(a1, a2);
        } finally {
            restore(a2);
            if (a1 != a2) {
                restore(a1);
            }
        }
    }

    private static void injectValues(Angle target, double radians) {
        if (unsafe == null) return;
        radians = wrapRadians(radians);
        try {
            float deg = (float) Math.toDegrees(radians);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            double tan = cos == 0 ? Double.MAX_VALUE : sin / cos;
            double halfRad = radians / 2.0;
            double halfTan = Math.cos(halfRad) == 0 ? Double.MAX_VALUE : Math.sin(halfRad) / Math.cos(halfRad);

            unsafe.putFloat(target, offsetAngleDegrees, deg);
            unsafe.putDouble(target, offsetAngleRadians, radians);
            unsafe.putDouble(target, offsetSin, sin);
            unsafe.putDouble(target, offsetCos, cos);
            unsafe.putDouble(target, offsetTan, tan);
            unsafe.putDouble(target, offsetHalfTan, halfTan);
        } catch (Exception ignored) {}
    }

    public static Angle getOpposite(Angle angle) {
        return fromRadians(angle.angleRadians + Math.PI);
    }

    public static Angle add(Angle a, Angle b) {
        return fromRadians(a.angleRadians + b.angleRadians);
    }

    public static Angle subtract(Angle a, Angle b) {
        return fromRadians(a.angleRadians - b.angleRadians);
    }

    public static boolean isParallel(Angle a, Angle b) {
        float f1 = b.angleDegrees, f2 = a.angleDegrees;
        float f3 = Math.abs(f1 - f2) % 180;
        return f3 < 0.001;
    }

    public static boolean swapOneWaySpeeds(Rail rail) {
        if (unsafe == null || rail == null) return false;
        final cn.jstxjf_.rte.mixin.RailAccessor acc = (cn.jstxjf_.rte.mixin.RailAccessor) (Object) rail;
        final long kph1 = acc.rte$getSpeedLimit1();
        final long kph2 = acc.rte$getSpeedLimit2();
        if ((kph1 <= 0) == (kph2 <= 0)) return false;
        acc.rte$setSpeedLimit1(kph2);
        acc.rte$setSpeedLimit2(kph1);
        final double mpm1 = unsafe.getDouble(rail, offsetRailSpeed1Mpm);
        final double mpm2 = unsafe.getDouble(rail, offsetRailSpeed2Mpm);
        unsafe.putDouble(rail, offsetRailSpeed1Mpm, mpm2);
        unsafe.putDouble(rail, offsetRailSpeed2Mpm, mpm1);
        return true;
    }

    public static void rebuildRailMathBestFit(Rail rail, double rad1, double rad2) {
        if (!Double.isFinite(rad1) || !Double.isFinite(rad2)) {
            return;
        }
        final RailAccessor accessor = (RailAccessor) (Object) rail;
        final Position p1 = accessor.rte$getPosition1();
        final Position p2 = accessor.rte$getPosition2();
        if (p1 == null || p2 == null) {
            rebuildRailMath(rail, rad1, rad2);
            return;
        }
        double bestError = Double.MAX_VALUE;
        double bestA = rad1;
        double bestB = rad2;
        for (int variant = 0; variant < 4; variant++) {
            final double a = (variant & 1) == 0 ? rad1 : wrapRadians(rad1 + Math.PI);
            final double b = (variant & 2) == 0 ? rad2 : wrapRadians(rad2 + Math.PI);
            rebuildRailMath(rail, a, b);
            final double error = endpointError(rail, p1, p2, rad1, rad2);
            if (error < bestError) {
                bestError = error;
                bestA = a;
                bestB = b;
            }
        }
        rebuildRailMath(rail, bestA, bestB);
    }

    private static double endpointError(Rail rail, Position p1, Position p2, double expectedRad1, double expectedRad2) {
        try {
            final cn.jstxjf_.rte.track.RailTrackPath path = new cn.jstxjf_.rte.track.RailTrackPath(rail);
            final double length = path.length();
            if (!Double.isFinite(length) || length < 1) {
                return Double.MAX_VALUE;
            }
            final double step = Math.min(2, length / 2);
            final var startPoint = path.sampleByDistance(0).position();
            final var startInner = path.sampleByDistance(step).position();
            final var endPoint = path.sampleByDistance(length).position();
            final var endInner = path.sampleByDistance(length - step).position();

            final double startDir = Math.atan2(startInner.z - startPoint.z, startInner.x - startPoint.x);
            final double endDir = Math.atan2(endInner.z - endPoint.z, endInner.x - endPoint.x);

            final double d1 = squaredXZ(startPoint, p1);
            final double d2 = squaredXZ(startPoint, p2);
            final boolean startAtP1 = d1 <= d2;
            final double actualAtP1 = startAtP1 ? startDir : endDir;
            final double actualAtP2 = startAtP1 ? endDir : startDir;

            return Math.abs(angleDelta(actualAtP1, expectedRad1))
                + Math.abs(angleDelta(actualAtP2, wrapRadians(expectedRad2 + Math.PI)));
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }

    private static double squaredXZ(BezierCurve.Vec3 point, Position node) {
        final double dx = point.x - (node.getX() + 0.5);
        final double dz = point.z - (node.getZ() + 0.5);
        return dx * dx + dz * dz;
    }

    private static double angleDelta(double a, double b) {
        double delta = (a - b) % (2 * Math.PI);
        if (delta > Math.PI) {
            delta -= 2 * Math.PI;
        } else if (delta < -Math.PI) {
            delta += 2 * Math.PI;
        }
        return delta;
    }

    public static void rebuildRailMath(Rail rail, double rad1, double rad2) {
        if (unsafe == null || !Double.isFinite(rad1) || !Double.isFinite(rad2)) {
            return;
        }
        final RailAccessor accessor = (RailAccessor)(Object)rail;
        final Position p1 = accessor.rte$getPosition1();
        final Position p2 = accessor.rte$getPosition2();
        final Rail.Shape shape = accessor.rte$getShape();
        if (p1 == null || p2 == null || shape == null) return;
        final Object mathObj = unsafe.getObject(rail, offsetRailMath);
        if (!(mathObj instanceof RailMath math)) return;
        final boolean reversePositions = p1.compareTo(p2) > 0;
        final double mtrA1 = rad1;
        final double mtrA2 = wrapRadians(rad2 + Math.PI);
        try {
            final double oldLen = math.getLength();
            RailCalculator.Group group = reversePositions
                ? RailCalculator.calculate(p2.getX(), p2.getZ(), p1.getX(), p1.getZ(), mtrA2, mtrA1)
                : RailCalculator.calculate(p1.getX(), p1.getZ(), p2.getX(), p2.getZ(), mtrA1, mtrA2);
            if (group == null || !group.first.isValid()) {
                group = reversePositions
                    ? RailCalculator.segment(p2.getX(), p2.getZ(), p1.getX(), p1.getZ())
                    : RailCalculator.segment(p1.getX(), p1.getZ(), p2.getX(), p2.getZ());
            }
            writeSection(math, group.first, false);
            writeSection(math, group.second, true);
        } catch (Exception e) {
            RTE.LOGGER.warn("[RailMath] rebuildRailMath failed id={}: {}", rail.getHexId(), e.getMessage());
        }
    }

    private static void writeSection(RailMath math, RailCalculator.Section section, boolean second) {
        if (section == null) {
            section = new RailCalculator.Section();
        }
        if (second) {
            unsafe.putDouble(math, offsetRailMathH2, section.h);
            unsafe.putDouble(math, offsetRailMathK2, section.k);
            unsafe.putDouble(math, offsetRailMathR2, section.r);
            unsafe.putDouble(math, offsetRailMathTStart2, section.tStart);
            unsafe.putDouble(math, offsetRailMathTEnd2, section.tEnd);
            unsafe.putBoolean(math, offsetRailMathReverseT2, section.reverseT);
            unsafe.putBoolean(math, offsetRailMathIsStraight2, section.isStraight);
        } else {
            unsafe.putDouble(math, offsetRailMathH1, section.h);
            unsafe.putDouble(math, offsetRailMathK1, section.k);
            unsafe.putDouble(math, offsetRailMathR1, section.r);
            unsafe.putDouble(math, offsetRailMathTStart1, section.tStart);
            unsafe.putDouble(math, offsetRailMathTEnd1, section.tEnd);
            unsafe.putBoolean(math, offsetRailMathReverseT1, section.reverseT);
            unsafe.putBoolean(math, offsetRailMathIsStraight1, section.isStraight);
        }
    }

    public static double currentTangentMismatch(Rail rail, double rad1, double rad2) {
        if (rail == null || rail.railMath == null || !Double.isFinite(rad1) || !Double.isFinite(rad2)) {
            return Double.MAX_VALUE;
        }
        return tangentMismatch(rail.railMath, rad1, rad2);
    }

    private static double tangentMismatch(RailMath math, double rad1, double rad2) {
        final double length = math.getLength();
        if (!Double.isFinite(length) || length <= 0.01) return Double.MAX_VALUE;
        double eps = Math.min(0.5, length * 0.01);
        if (eps < 0.001) eps = 0.001;
        final org.mtr.core.tool.Vector s0 = math.getPosition(0, false);
        final org.mtr.core.tool.Vector s1 = math.getPosition(eps, false);
        final org.mtr.core.tool.Vector e0 = math.getPosition(length - eps, false);
        final org.mtr.core.tool.Vector e1 = math.getPosition(length, false);
        final double startDx = s1.x - s0.x;
        final double startDz = s1.z - s0.z;
        final double endDx = e1.x - e0.x;
        final double endDz = e1.z - e0.z;
        if ((Math.abs(startDx) < 1e-9 && Math.abs(startDz) < 1e-9)
            || (Math.abs(endDx) < 1e-9 && Math.abs(endDz) < 1e-9)) {
            return Double.MAX_VALUE;
        }
        final double startAngle = Math.atan2(startDz, startDx);
        final double endAngle = Math.atan2(endDz, endDx);
        return angleDeltaAbs(startAngle, rad1) + angleDeltaAbs(endAngle, rad2);
    }

    private static double angleDeltaAbs(double a, double b) {
        return Math.abs(Math.atan2(Math.sin(a - b), Math.cos(a - b)));
    }

    public static double probeRebuildLength(Rail rail, double rad1, double rad2) {
        if (unsafe == null || !Double.isFinite(rad1) || !Double.isFinite(rad2)) {
            return Double.NaN;
        }
        final RailAccessor accessor = (RailAccessor)(Object)rail;
        final Position p1 = accessor.rte$getPosition1();
        final Position p2 = accessor.rte$getPosition2();
        final Rail.Shape shape = accessor.rte$getShape();
        if (p1 == null || p2 == null || shape == null) return Double.NaN;
        final boolean reversePositions = p1.compareTo(p2) > 0;
        final double mtrA1 = rad1;
        final double mtrA2 = wrapRadians(rad2 + Math.PI);
        RailCalculator.Group group = reversePositions
            ? RailCalculator.calculate(p2.getX(), p2.getZ(), p1.getX(), p1.getZ(), mtrA2, mtrA1)
            : RailCalculator.calculate(p1.getX(), p1.getZ(), p2.getX(), p2.getZ(), mtrA1, mtrA2);
        if (group == null || !group.first.isValid()) {
            group = reversePositions
                ? RailCalculator.segment(p2.getX(), p2.getZ(), p1.getX(), p1.getZ())
                : RailCalculator.segment(p1.getX(), p1.getZ(), p2.getX(), p2.getZ());
        }
        final double len = group.first.getLength() + (group.second.isValid() ? group.second.getLength() : 0);
        return Double.isFinite(len) && len > 0.01 ? len : Double.NaN;
    }

    public static void rebuildRailMathFromBezier(Rail rail, BezierCurve bezier) {
        if (bezier == null) return;
        try {
            double length = bezier.getLength();
            if (!Double.isFinite(length) || length < 0.1) return;
            double epsilon = Math.min(0.5, length * 0.01);
            if (epsilon < 0.001) epsilon = 0.001;
            BezierCurve.Vec3 p0 = bezier.getPosition(0);
            BezierCurve.Vec3 p1 = bezier.getPosition(epsilon);
            BezierCurve.Vec3 pe0 = bezier.getPosition(length - epsilon);
            BezierCurve.Vec3 pe1 = bezier.getPosition(length);
            if (p0 == null || p1 == null || pe0 == null || pe1 == null) return;
            double dx1 = p1.x - p0.x, dz1 = p1.z - p0.z;
            double dx2 = pe1.x - pe0.x, dz2 = pe1.z - pe0.z;
            if (Math.abs(dx1) < 1e-9 && Math.abs(dz1) < 1e-9) return;
            if (Math.abs(dx2) < 1e-9 && Math.abs(dz2) < 1e-9) return;
            double angle1 = Math.atan2(dz1, dx1);
            double angle2 = Math.atan2(dz2, dx2);
            rebuildRailMathBestFit(rail, angle1, angle2);
        } catch (Exception e) {
            RTE.LOGGER.warn("[RailMath] rebuildRailMathFromBezier failed: {}", e.getMessage());
        }
    }

    public static void correctStraightRailMathIfApplicable(Rail rail, double rad1, double rad2) {
        if (!isStraightRailMathApplicable(rail, rad1, rad2)) {
            return;
        }
        correctStraightRailMath(rail, rad1);
    }

    public static void correctStraightRailMath(Rail rail, double rad1) {
        if (unsafe == null || !Double.isFinite(rad1)) return;
        final RailAccessor accessor = (RailAccessor)(Object)rail;
        final Position p1 = accessor.rte$getPosition1();
        final Position p2 = accessor.rte$getPosition2();
        if (p1 == null || p2 == null) return;
        final Object math = unsafe.getObject(rail, offsetRailMath);
        if (!(math instanceof RailMath)) return;

        final double h = Math.cos(rad1);
        final double k = Math.sin(rad1);
        final long x1 = p1.getX();
        final long z1 = p1.getZ();
        final long x2 = p2.getX();
        final long z2 = p2.getZ();

        final double tStart;
        final double tEnd;
        final double r;
        if (Math.abs(h) >= 0.5 && Math.abs(k) >= 0.5) {
            tStart = x1 / h;
            tEnd = x2 / h;
            r = (h * z1 - k * x1) / (h * h);
        } else {
            final double div = h * h - k * k;
            tStart = (h * x1 - k * z1) / div;
            tEnd = (h * x2 - k * z2) / div;
            r = (h * z1 - k * x1) / div;
        }

        try {
            unsafe.putDouble(math, offsetRailMathH1, h);
            unsafe.putDouble(math, offsetRailMathK1, k);
            unsafe.putDouble(math, offsetRailMathR1, r);
            unsafe.putDouble(math, offsetRailMathTStart1, tStart);
            unsafe.putDouble(math, offsetRailMathTEnd1, tEnd);
            unsafe.putBoolean(math, offsetRailMathReverseT1, tStart > tEnd);
            unsafe.putBoolean(math, offsetRailMathIsStraight1, true);
        } catch (Exception ignored) {
        }
    }

    private static boolean isStraightRailMathApplicable(Rail rail, double rad1, double rad2) {
        if (!Double.isFinite(rad1) || !Double.isFinite(rad2)) return false;
        final RailAccessor accessor = (RailAccessor)(Object)rail;
        final Position p1 = accessor.rte$getPosition1();
        final Position p2 = accessor.rte$getPosition2();
        if (p1 == null || p2 == null) return false;
        final double direction = Math.atan2(p2.getZ() - p1.getZ(), p2.getX() - p1.getX());
        return parallelDiff(rad1, rad2) < 0.02 && parallelDiff(rad1, direction) < 0.02;
    }

    private static double parallelDiff(double a, double b) {
        double diff = (a - b) % Math.PI;
        if (diff <= -Math.PI / 2) {
            diff += Math.PI;
        } else if (diff > Math.PI / 2) {
            diff -= Math.PI;
        }
        return Math.abs(diff);
    }
}
