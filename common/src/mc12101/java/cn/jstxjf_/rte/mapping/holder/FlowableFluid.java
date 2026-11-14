package cn.jstxjf_.rte.mapping.holder;

public final class FlowableFluid extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.material.FlowingFluid> {

    public FlowableFluid(net.minecraft.world.level.material.FlowingFluid p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.FlowableFluid cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new FlowableFluid((net.minecraft.world.level.material.FlowingFluid) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.material.FlowingFluid;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFlowing(int p0, boolean p1) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.material.FlowingFluid) this.data).getFlowing(p0, p1));
    }

    public cn.jstxjf_.rte.mapping.holder.Fluid getFlowing() {
        return new cn.jstxjf_.rte.mapping.holder.Fluid(((net.minecraft.world.level.material.FlowingFluid) this.data).getFlowing());
    }

    public boolean isStill(cn.jstxjf_.rte.mapping.holder.FluidState p0) {
        return ((net.minecraft.world.level.material.FlowingFluid) this.data).isSource(((net.minecraft.world.level.material.FluidState) p0.data));
    }

    public void onScheduledTick(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.FluidState p2) {
        ((net.minecraft.world.level.material.FlowingFluid) this.data).tick(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.material.FluidState) p2.data));
    }

    public final cn.jstxjf_.rte.mapping.holder.FluidState getDefaultState() {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.material.FlowingFluid) this.data).defaultFluidState());
    }

    public cn.jstxjf_.rte.mapping.holder.Item getBucketItem() {
        return new cn.jstxjf_.rte.mapping.holder.Item(((net.minecraft.world.level.material.FlowingFluid) this.data).getBucket());
    }

    public cn.jstxjf_.rte.mapping.holder.Fluid getStill() {
        return new cn.jstxjf_.rte.mapping.holder.Fluid(((net.minecraft.world.level.material.FlowingFluid) this.data).getSource());
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getStill(boolean p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.material.FlowingFluid) this.data).getSource(p0));
    }

    public float getHeight(cn.jstxjf_.rte.mapping.holder.FluidState p0) {
        return ((net.minecraft.world.level.material.FlowingFluid) this.data).getOwnHeight(((net.minecraft.world.level.material.FluidState) p0.data));
    }

    public float getHeight(cn.jstxjf_.rte.mapping.holder.FluidState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return ((net.minecraft.world.level.material.FlowingFluid) this.data).getHeight(((net.minecraft.world.level.material.FluidState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getShape(cn.jstxjf_.rte.mapping.holder.FluidState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.material.FlowingFluid) this.data).getShape(((net.minecraft.world.level.material.FluidState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getVelocity(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.FluidState p2) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.level.material.FlowingFluid) this.data).getFlow(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.material.FluidState) p2.data)));
    }

    public boolean matchesType(cn.jstxjf_.rte.mapping.holder.Fluid p0) {
        return ((net.minecraft.world.level.material.FlowingFluid) this.data).isSame(((net.minecraft.world.level.material.Fluid) p0.data));
    }

    public int getLevel(cn.jstxjf_.rte.mapping.holder.FluidState p0) {
        return ((net.minecraft.world.level.material.FlowingFluid) this.data).getAmount(((net.minecraft.world.level.material.FluidState) p0.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.IntegerProperty getLevelMapped() {
        return new cn.jstxjf_.rte.mapping.holder.IntegerProperty(net.minecraft.world.level.material.FlowingFluid.LEVEL);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanProperty getFallingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanProperty(net.minecraft.world.level.material.FlowingFluid.FALLING);
    }
}
