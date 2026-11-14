package cn.jstxjf_.rte.mapping.holder;

public final class Fluid extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.material.Fluid> {

    public Fluid(net.minecraft.world.level.material.Fluid p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Fluid cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Fluid((net.minecraft.world.level.material.Fluid) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.material.Fluid;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int getLevel(cn.jstxjf_.rte.mapping.holder.FluidState p0) {
        return ((net.minecraft.world.level.material.Fluid) this.data).getAmount(((net.minecraft.world.level.material.FluidState) p0.data));
    }

    public boolean isStill(cn.jstxjf_.rte.mapping.holder.FluidState p0) {
        return ((net.minecraft.world.level.material.Fluid) this.data).isSource(((net.minecraft.world.level.material.FluidState) p0.data));
    }

    public boolean matchesType(cn.jstxjf_.rte.mapping.holder.Fluid p0) {
        return ((net.minecraft.world.level.material.Fluid) this.data).isSame(((net.minecraft.world.level.material.Fluid) p0.data));
    }

    public float getHeight(cn.jstxjf_.rte.mapping.holder.FluidState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return ((net.minecraft.world.level.material.Fluid) this.data).getHeight(((net.minecraft.world.level.material.FluidState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public float getHeight(cn.jstxjf_.rte.mapping.holder.FluidState p0) {
        return ((net.minecraft.world.level.material.Fluid) this.data).getOwnHeight(((net.minecraft.world.level.material.FluidState) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getShape(cn.jstxjf_.rte.mapping.holder.FluidState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.material.Fluid) this.data).getShape(((net.minecraft.world.level.material.FluidState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data)));
    }

    public final cn.jstxjf_.rte.mapping.holder.FluidState getDefaultState() {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.material.Fluid) this.data).defaultFluidState());
    }

    public cn.jstxjf_.rte.mapping.holder.Item getBucketItem() {
        return new cn.jstxjf_.rte.mapping.holder.Item(((net.minecraft.world.level.material.Fluid) this.data).getBucket());
    }
}
