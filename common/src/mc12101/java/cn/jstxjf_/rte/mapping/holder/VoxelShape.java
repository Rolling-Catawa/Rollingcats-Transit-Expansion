package cn.jstxjf_.rte.mapping.holder;

public final class VoxelShape extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.phys.shapes.VoxelShape> {

    public VoxelShape(net.minecraft.world.phys.shapes.VoxelShape p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new VoxelShape((net.minecraft.world.phys.shapes.VoxelShape) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.phys.shapes.VoxelShape;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getFace(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.phys.shapes.VoxelShape) this.data).getFaceShape(p0.data));
    }

    public boolean isEmpty() {
        return ((net.minecraft.world.phys.shapes.VoxelShape) this.data).isEmpty();
    }

    public double calculateMaxDistance(cn.jstxjf_.rte.mapping.holder.Axis p0, cn.jstxjf_.rte.mapping.holder.Box p1, double p2) {
        return ((net.minecraft.world.phys.shapes.VoxelShape) this.data).collide(p0.data, ((net.minecraft.world.phys.AABB) p1.data), p2);
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape offset(double p0, double p1, double p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.phys.shapes.VoxelShape) this.data).move(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycast(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        final var v0 = ((net.minecraft.world.phys.shapes.VoxelShape) this.data).clip(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.Box getBoundingBox() {
        return new cn.jstxjf_.rte.mapping.holder.Box(((net.minecraft.world.phys.shapes.VoxelShape) this.data).bounds());
    }
}
