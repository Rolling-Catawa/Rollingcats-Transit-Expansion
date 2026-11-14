package cn.jstxjf_.rte.mapping.holder;

public final class Box extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.phys.AABB> {

    public Box(net.minecraft.world.phys.AABB p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Box cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Box((net.minecraft.world.phys.AABB) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.phys.AABB;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean intersects(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return ((net.minecraft.world.phys.AABB) this.data).intersects(((net.minecraft.world.phys.AABB) p0.data));
    }

    public boolean intersects(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        return ((net.minecraft.world.phys.AABB) this.data).intersects(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public boolean intersects(double p0, double p1, double p2, double p3, double p4, double p5) {
        return ((net.minecraft.world.phys.AABB) this.data).intersects(p0, p1, p2, p3, p4, p5);
    }

    public int hashCode() {
        return ((net.minecraft.world.phys.AABB) this.data).hashCode();
    }

    public cn.jstxjf_.rte.mapping.holder.Box offset(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.Box(((net.minecraft.world.phys.AABB) this.data).move(((net.minecraft.core.BlockPos) p0.data)));
    }

    public boolean contains(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return ((net.minecraft.world.phys.AABB) this.data).contains(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public boolean contains(double p0, double p1, double p2) {
        return ((net.minecraft.world.phys.AABB) this.data).contains(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getCenter() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.AABB) this.data).getCenter());
    }

    public Box(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        super(new net.minecraft.world.phys.AABB(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data)));
    }

    public Box(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        super(new net.minecraft.world.phys.AABB(((net.minecraft.core.BlockPos) p0.data)));
    }

    public Box(double p0, double p1, double p2, double p3, double p4, double p5) {
        super(new net.minecraft.world.phys.AABB(p0, p1, p2, p3, p4, p5));
    }

    public boolean isNaN() {
        return ((net.minecraft.world.phys.AABB) this.data).hasNaN();
    }

    public static cn.jstxjf_.rte.mapping.holder.Box from(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Box(net.minecraft.world.phys.AABB.unitCubeFromLowerCorner(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public double getMaxXMapped() {
        return ((net.minecraft.world.phys.AABB) this.data).maxX;
    }

    public double getMaxYMapped() {
        return ((net.minecraft.world.phys.AABB) this.data).maxY;
    }

    public double getMinXMapped() {
        return ((net.minecraft.world.phys.AABB) this.data).minX;
    }

    public double getMaxZMapped() {
        return ((net.minecraft.world.phys.AABB) this.data).maxZ;
    }

    public double getMinYMapped() {
        return ((net.minecraft.world.phys.AABB) this.data).minY;
    }

    public double getMinZMapped() {
        return ((net.minecraft.world.phys.AABB) this.data).minZ;
    }
}
