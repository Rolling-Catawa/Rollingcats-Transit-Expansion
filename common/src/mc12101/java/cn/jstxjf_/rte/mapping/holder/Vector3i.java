package cn.jstxjf_.rte.mapping.holder;

public final class Vector3i extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.core.Vec3i> {

    public Vector3i(net.minecraft.core.Vec3i p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3i cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Vector3i((net.minecraft.core.Vec3i) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.core.Vec3i;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3i up(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(((net.minecraft.core.Vec3i) this.data).above(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3i up() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(((net.minecraft.core.Vec3i) this.data).above());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3i crossProduct(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(((net.minecraft.core.Vec3i) this.data).cross(((net.minecraft.core.Vec3i) p0.data)));
    }

    public Vector3i(int p0, int p1, int p2) {
        super(new net.minecraft.core.Vec3i(p0, p1, p2));
    }

    public int compareTo(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return ((net.minecraft.core.Vec3i) this.data).compareTo(((net.minecraft.core.Vec3i) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3i down(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(((net.minecraft.core.Vec3i) this.data).below(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3i down() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(((net.minecraft.core.Vec3i) this.data).below());
    }

    public boolean isWithinDistance(cn.jstxjf_.rte.mapping.holder.Vector3i p0, double p1) {
        return ((net.minecraft.core.Vec3i) this.data).closerThan(((net.minecraft.core.Vec3i) p0.data), p1);
    }

    public boolean isWithinDistance(cn.jstxjf_.rte.mapping.holder.Position p0, double p1) {
        return ((net.minecraft.core.Vec3i) this.data).closerToCenterThan(((net.minecraft.core.Position) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3i offset(cn.jstxjf_.rte.mapping.holder.Direction p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(((net.minecraft.core.Vec3i) this.data).relative(p0.data, p1));
    }

    public java.lang.String toShortString() {
        return ((net.minecraft.core.Vec3i) this.data).toShortString();
    }

    public int hashCode() {
        return ((net.minecraft.core.Vec3i) this.data).hashCode();
    }

    public int getComponentAlongAxis(cn.jstxjf_.rte.mapping.holder.Axis p0) {
        return ((net.minecraft.core.Vec3i) this.data).get(p0.data);
    }

    public int getY() {
        return ((net.minecraft.core.Vec3i) this.data).getY();
    }

    public int getX() {
        return ((net.minecraft.core.Vec3i) this.data).getX();
    }

    public int getZ() {
        return ((net.minecraft.core.Vec3i) this.data).getZ();
    }

    public double getSquaredDistance(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return ((net.minecraft.core.Vec3i) this.data).distSqr(((net.minecraft.core.Vec3i) p0.data));
    }

    public int getManhattanDistance(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return ((net.minecraft.core.Vec3i) this.data).distManhattan(((net.minecraft.core.Vec3i) p0.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3i getZeroMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(net.minecraft.core.Vec3i.ZERO);
    }
}
