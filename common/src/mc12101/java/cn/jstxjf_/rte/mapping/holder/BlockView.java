package cn.jstxjf_.rte.mapping.holder;

public final class BlockView extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.BlockGetter> {

    public BlockView(net.minecraft.world.level.BlockGetter p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockView cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BlockView((net.minecraft.world.level.BlockGetter) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.BlockGetter;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int getLuminance(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.BlockGetter) this.data).getLightEmission(((net.minecraft.core.BlockPos) p0.data));
    }

    public int getMaxLightLevel() {
        return ((net.minecraft.world.level.BlockGetter) this.data).getMaxLightLevel();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.BlockGetter) this.data).getBlockState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public double getDismountHeight(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.BlockGetter) this.data).getBlockFloorHeight(((net.minecraft.core.BlockPos) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.BlockGetter) this.data).getFluidState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockEntity getBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.world.level.BlockGetter) this.data).getBlockEntity(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycastBlock(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.VoxelShape p3, cn.jstxjf_.rte.mapping.holder.BlockState p4) {
        final var v0 = ((net.minecraft.world.level.BlockGetter) this.data).clipWithInteractionOverride(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.VoxelShape) p3.data), ((net.minecraft.world.level.block.state.BlockState) p4.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }
}
