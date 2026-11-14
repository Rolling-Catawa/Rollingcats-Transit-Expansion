package cn.jstxjf_.rte.mapping.holder;

public final class BlockRenderView extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.BlockAndTintGetter> {

    public BlockRenderView(net.minecraft.world.level.BlockAndTintGetter p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRenderView cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BlockRenderView((net.minecraft.world.level.BlockAndTintGetter) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.BlockAndTintGetter;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public float getBrightness(cn.jstxjf_.rte.mapping.holder.Direction p0, boolean p1) {
        return ((net.minecraft.world.level.BlockAndTintGetter) this.data).getShade(p0.data, p1);
    }

    public int getLuminance(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.BlockAndTintGetter) this.data).getLightEmission(((net.minecraft.core.BlockPos) p0.data));
    }

    public int getMaxLightLevel() {
        return ((net.minecraft.world.level.BlockAndTintGetter) this.data).getMaxLightLevel();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.BlockAndTintGetter) this.data).getBlockState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public double getDismountHeight(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.BlockAndTintGetter) this.data).getBlockFloorHeight(((net.minecraft.core.BlockPos) p0.data));
    }

    public int getLightLevel(cn.jstxjf_.rte.mapping.holder.LightType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.BlockAndTintGetter) this.data).getBrightness(p0.data, ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.BlockAndTintGetter) this.data).getFluidState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockEntity getBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.world.level.BlockAndTintGetter) this.data).getBlockEntity(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(v0));
    }

    public int getBaseLightLevel(cn.jstxjf_.rte.mapping.holder.BlockPos p0, int p1) {
        return ((net.minecraft.world.level.BlockAndTintGetter) this.data).getRawBrightness(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycastBlock(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.VoxelShape p3, cn.jstxjf_.rte.mapping.holder.BlockState p4) {
        final var v0 = ((net.minecraft.world.level.BlockAndTintGetter) this.data).clipWithInteractionOverride(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.VoxelShape) p3.data), ((net.minecraft.world.level.block.state.BlockState) p4.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }
}
