package cn.jstxjf_.rte.scripting;

public final class ScriptUseBox {

    public final String id;
    public final int car;
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public ScriptUseBox(String id, int car,
                         double x1, double y1, double z1,
                         double x2, double y2, double z2) {
        this.id = id;
        this.car = car;
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public double intersect(double ox, double oy, double oz,
                            double dx, double dy, double dz, double maxDistance) {
        double near = 0;
        double far = maxDistance;

        final double[] origin = {ox, oy, oz};
        final double[] direction = {dx, dy, dz};
        final double[] lower = {minX, minY, minZ};
        final double[] upper = {maxX, maxY, maxZ};

        for (int axis = 0; axis < 3; axis++) {
            if (Math.abs(direction[axis]) < 1.0E-8) {
                if (origin[axis] < lower[axis] || origin[axis] > upper[axis]) {
                    return -1;
                }
                continue;
            }
            final double inverse = 1.0 / direction[axis];
            double t1 = (lower[axis] - origin[axis]) * inverse;
            double t2 = (upper[axis] - origin[axis]) * inverse;
            if (t1 > t2) {
                final double swap = t1;
                t1 = t2;
                t2 = swap;
            }
            if (t1 > near) {
                near = t1;
            }
            if (t2 < far) {
                far = t2;
            }
            if (near > far) {
                return -1;
            }
        }
        return near;
    }
}
