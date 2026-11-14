package cn.jstxjf_.rte.mapping.holder;

public final class Vector3d extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.phys.Vec3> {

    public Vector3d(net.minecraft.world.phys.Vec3 p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Vector3d((net.minecraft.world.phys.Vec3) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.phys.Vec3;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d normalize() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).normalize());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d relativize(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).vectorTo(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public Vector3d(cn.jstxjf_.rte.mapping.holder.Vector3f p0) {
        super(new net.minecraft.world.phys.Vec3(((org.joml.Vector3f) p0.data)));
    }

    public Vector3d(double p0, double p1, double p2) {
        super(new net.minecraft.world.phys.Vec3(p0, p1, p2));
    }

    public double squaredDistanceTo(double p0, double p1, double p2) {
        return ((net.minecraft.world.phys.Vec3) this.data).distanceToSqr(p0, p1, p2);
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return ((net.minecraft.world.phys.Vec3) this.data).distanceToSqr(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d crossProduct(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).cross(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d ofBottomCenter(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.phys.Vec3.atBottomCenterOf(((net.minecraft.core.Vec3i) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d subtract(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).subtract(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d subtract(double p0, double p1, double p2) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).subtract(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d add(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).add(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d add(double p0, double p1, double p2) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).add(p0, p1, p2));
    }

    public double distanceTo(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return ((net.minecraft.world.phys.Vec3) this.data).distanceTo(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public int hashCode() {
        return ((net.minecraft.world.phys.Vec3) this.data).hashCode();
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d of(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.phys.Vec3.atLowerCornerOf(((net.minecraft.core.Vec3i) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d unpackRgb(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.phys.Vec3.fromRGB24(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d multiply(double p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).scale(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d multiply(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).multiply(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d multiply(double p0, double p1, double p2) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).multiply(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d rotateX(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).xRot(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d rotateY(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).yRot(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d rotateZ(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).zRot(p0));
    }

    public double getComponentAlongAxis(cn.jstxjf_.rte.mapping.holder.Axis p0) {
        return ((net.minecraft.world.phys.Vec3) this.data).get(p0.data);
    }

    public double dotProduct(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return ((net.minecraft.world.phys.Vec3) this.data).dot(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d fromPolar(float p0, float p1) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.phys.Vec3.directionFromRotation(p0, p1));
    }

    public boolean isInRange(cn.jstxjf_.rte.mapping.holder.Position p0, double p1) {
        return ((net.minecraft.world.phys.Vec3) this.data).closerThan(((net.minecraft.core.Position) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d negate() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.Vec3) this.data).reverse());
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d ofCenter(cn.jstxjf_.rte.mapping.holder.Vector3i p0, double p1) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.phys.Vec3.upFromBottomCenterOf(((net.minecraft.core.Vec3i) p0.data), p1));
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d ofCenter(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.phys.Vec3.atCenterOf(((net.minecraft.core.Vec3i) p0.data)));
    }

    public double lengthSquared() {
        return ((net.minecraft.world.phys.Vec3) this.data).lengthSqr();
    }

    public double length() {
        return ((net.minecraft.world.phys.Vec3) this.data).length();
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d getZeroMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.phys.Vec3.ZERO);
    }

    public double getXMapped() {
        return ((net.minecraft.world.phys.Vec3) this.data).x;
    }

    public double getYMapped() {
        return ((net.minecraft.world.phys.Vec3) this.data).y;
    }

    public double getZMapped() {
        return ((net.minecraft.world.phys.Vec3) this.data).z;
    }
}
