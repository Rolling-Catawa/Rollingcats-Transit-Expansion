package cn.jstxjf_.rte.mapping.holder;

public final class BlockHitResult extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.phys.BlockHitResult> {

    public BlockHitResult(net.minecraft.world.phys.BlockHitResult p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockHitResult cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BlockHitResult((net.minecraft.world.phys.BlockHitResult) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.phys.BlockHitResult;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getSide() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.phys.BlockHitResult) this.data).getDirection());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.BlockHitResult) this.data).getLocation());
    }

    public BlockHitResult(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Direction p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, boolean p3) {
        super(new net.minecraft.world.phys.BlockHitResult(((net.minecraft.world.phys.Vec3) p0.data), p1.data, ((net.minecraft.core.BlockPos) p2.data), p3));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.phys.BlockHitResult) this.data).getBlockPos());
    }

    public boolean isInsideBlock() {
        return ((net.minecraft.world.phys.BlockHitResult) this.data).isInside();
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockHitResult createMissed(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Direction p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return new cn.jstxjf_.rte.mapping.holder.BlockHitResult(net.minecraft.world.phys.BlockHitResult.miss(((net.minecraft.world.phys.Vec3) p0.data), p1.data, ((net.minecraft.core.BlockPos) p2.data)));
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.phys.BlockHitResult) this.data).distanceTo(((net.minecraft.world.entity.Entity) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult withBlockPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockHitResult(((net.minecraft.world.phys.BlockHitResult) this.data).withPosition(((net.minecraft.core.BlockPos) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult withSide(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockHitResult(((net.minecraft.world.phys.BlockHitResult) this.data).withDirection(p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.HitResultType getType() {
        return cn.jstxjf_.rte.mapping.holder.HitResultType.convert(((net.minecraft.world.phys.BlockHitResult) this.data).getType());
    }
}
