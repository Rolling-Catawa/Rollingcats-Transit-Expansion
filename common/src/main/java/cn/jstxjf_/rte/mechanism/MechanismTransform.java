package cn.jstxjf_.rte.mechanism;

import java.util.Locale;

public final class MechanismTransform {

    public static final double ALIGN_DISTANCE = 0.05;
    public static final double ALIGN_DEGREES = 0.5;
    public static final MechanismTransform IDENTITY = new MechanismTransform(0, 0, 0, 0, 0, 0, 0, 0, 0);
    public static final ThreadLocal<Boolean> SUPPRESS = ThreadLocal.withInitial(() -> false);

    public final double pivotX;
    public final double pivotY;
    public final double pivotZ;
    public final double yaw;
    public final double pitch;
    public final double roll;
    public final double dx;
    public final double dy;
    public final double dz;
    public final double heading;

    public MechanismTransform(double pivotX, double pivotY, double pivotZ, double yaw, double pitch, double roll, double dx, double dy, double dz) {
        this(pivotX, pivotY, pivotZ, yaw, pitch, roll, dx, dy, dz, 0);
    }

    public MechanismTransform(double pivotX, double pivotY, double pivotZ, double yaw, double pitch, double roll, double dx, double dy, double dz, double heading) {
        this.pivotX = finite(pivotX);
        this.pivotY = finite(pivotY);
        this.pivotZ = finite(pivotZ);
        this.yaw = normalizeDegrees(finite(yaw));
        this.pitch = normalizeDegrees(finite(pitch));
        this.roll = normalizeDegrees(finite(roll));
        this.dx = finite(dx);
        this.dy = finite(dy);
        this.dz = finite(dz);
        this.heading = normalizeDegrees(finite(heading));
    }

    public MechanismTransform withHeading(double heading) {
        return new MechanismTransform(pivotX, pivotY, pivotZ, yaw, pitch, roll, dx, dy, dz, heading);
    }

    public boolean isIdentity() {
        return yaw == 0 && pitch == 0 && roll == 0 && dx == 0 && dy == 0 && dz == 0;
    }

    public double[] apply(double x, double y, double z) {
        if (isIdentity()) {
            return new double[]{x, y, z};
        }
        final double[] rotated = rotate(x - pivotX, y - pivotY, z - pivotZ);
        return new double[]{rotated[0] + pivotX + dx, rotated[1] + pivotY + dy, rotated[2] + pivotZ + dz};
    }

    public double[] rotate(double x, double y, double z) {
        final boolean tilted = pitch != 0 || roll != 0;
        final double headingRad = tilted ? Math.toRadians(heading) : 0;
        final double x0 = Math.cos(headingRad) * x + Math.sin(headingRad) * z;
        final double z0 = -Math.sin(headingRad) * x + Math.cos(headingRad) * z;
        final double rollRad = Math.toRadians(roll);
        final double y1 = Math.cos(rollRad) * y - Math.sin(rollRad) * z0;
        final double z1 = Math.sin(rollRad) * y + Math.cos(rollRad) * z0;
        final double pitchRad = Math.toRadians(pitch);
        final double x2 = Math.cos(pitchRad) * x0 - Math.sin(pitchRad) * y1;
        final double y2 = Math.sin(pitchRad) * x0 + Math.cos(pitchRad) * y1;
        final double yawRad = Math.toRadians(yaw) + headingRad;
        final double x3 = Math.cos(yawRad) * x2 - Math.sin(yawRad) * z1;
        final double z3 = Math.sin(yawRad) * x2 + Math.cos(yawRad) * z1;
        return new double[]{x3, y2, z3};
    }

    public double[] matrixColumns() {
        final double[] ex = rotate(1, 0, 0);
        final double[] ey = rotate(0, 1, 0);
        final double[] ez = rotate(0, 0, 1);
        final double[] rp = rotate(pivotX, pivotY, pivotZ);
        return new double[]{
            ex[0], ex[1], ex[2],
            ey[0], ey[1], ey[2],
            ez[0], ez[1], ez[2],
            pivotX + dx - rp[0], pivotY + dy - rp[1], pivotZ + dz - rp[2]
        };
    }

    public MechanismTransform lerp(MechanismTransform to, double t) {
        final double clamped = Math.max(0, Math.min(1, t));
        return new MechanismTransform(
            to.pivotX, to.pivotY, to.pivotZ,
            yaw + shortestDelta(yaw, to.yaw) * clamped,
            pitch + shortestDelta(pitch, to.pitch) * clamped,
            roll + shortestDelta(roll, to.roll) * clamped,
            dx + (to.dx - dx) * clamped,
            dy + (to.dy - dy) * clamped,
            dz + (to.dz - dz) * clamped,
            to.heading);
    }

    public static MechanismTransform between(RailPose from, RailPose to) {
        final double direct = normalizeDegrees(to.yawDegrees() - from.yawDegrees());
        final double reversed = normalizeDegrees(direct + 180);
        final double yaw = Math.abs(reversed) < Math.abs(direct) ? reversed : direct;
        return new MechanismTransform(
            from.midX(), from.midY(), from.midZ(),
            yaw, 0, 0,
            to.midX() - from.midX(), to.midY() - from.midY(), to.midZ() - from.midZ());
    }

    public boolean isAligned() {
        return Math.sqrt(dx * dx + dy * dy + dz * dz) <= ALIGN_DISTANCE
            && Math.abs(yaw) <= ALIGN_DEGREES && Math.abs(pitch) <= ALIGN_DEGREES && Math.abs(roll) <= ALIGN_DEGREES;
    }

    public double angularDistance(MechanismTransform other) {
        return Math.max(Math.abs(shortestDelta(yaw, other.yaw)),
            Math.max(Math.abs(shortestDelta(pitch, other.pitch)), Math.abs(shortestDelta(roll, other.roll))));
    }

    public double linearDistance(MechanismTransform other) {
        final double x = other.dx - dx;
        final double y = other.dy - dy;
        final double z = other.dz - dz;
        return Math.sqrt(x * x + y * y + z * z);
    }

    public String encode() {
        return String.format(Locale.ROOT, "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", pivotX, pivotY, pivotZ, yaw, pitch, roll, dx, dy, dz, heading);
    }

    public static MechanismTransform decode(String text) {
        if (text == null || text.isEmpty()) {
            return IDENTITY;
        }
        final String[] parts = text.split(",");
        if (parts.length != 9 && parts.length != 10) {
            return IDENTITY;
        }
        final double[] values = new double[10];
        try {
            for (int i = 0; i < parts.length; i++) {
                values[i] = Double.parseDouble(parts[i]);
            }
        } catch (NumberFormatException e) {
            return IDENTITY;
        }
        return new MechanismTransform(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8], values[9]);
    }

    public static double normalizeDegrees(double degrees) {
        double value = degrees % 360;
        if (value > 180) {
            value -= 360;
        } else if (value <= -180) {
            value += 360;
        }
        return value == -0.0 ? 0 : value;
    }

    public static double shortestDelta(double from, double to) {
        return normalizeDegrees(to - from);
    }

    private static double finite(double value) {
        return Double.isFinite(value) ? value : 0;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MechanismTransform transform && encode().equals(transform.encode());
    }

    @Override
    public int hashCode() {
        return encode().hashCode();
    }
}
