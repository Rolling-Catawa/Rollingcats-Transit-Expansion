package cn.jstxjf_.rte.mechanism;

public record RailPose(double x1, double y1, double z1, double x2, double y2, double z2) {

    public double midX() {
        return (x1 + x2) / 2;
    }

    public double midY() {
        return (y1 + y2) / 2;
    }

    public double midZ() {
        return (z1 + z2) / 2;
    }

    public double yawDegrees() {
        return Math.toDegrees(Math.atan2(z2 - z1, x2 - x1));
    }

    public double length() {
        final double dx = x2 - x1;
        final double dy = y2 - y1;
        final double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
