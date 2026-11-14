package cn.jstxjf_.rte.mapping.holder;

public final class Fluids extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.material.Fluids> {

    public Fluids(net.minecraft.world.level.material.Fluids p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Fluids cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Fluids((net.minecraft.world.level.material.Fluids) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.material.Fluids;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public Fluids() {
        super(new net.minecraft.world.level.material.Fluids());
    }

    public static cn.jstxjf_.rte.mapping.holder.FlowableFluid getLavaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.FlowableFluid(net.minecraft.world.level.material.Fluids.LAVA);
    }

    public static cn.jstxjf_.rte.mapping.holder.FlowableFluid getFlowingWaterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.FlowableFluid(net.minecraft.world.level.material.Fluids.FLOWING_WATER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Fluid getEmptyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Fluid(net.minecraft.world.level.material.Fluids.EMPTY);
    }

    public static cn.jstxjf_.rte.mapping.holder.FlowableFluid getFlowingLavaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.FlowableFluid(net.minecraft.world.level.material.Fluids.FLOWING_LAVA);
    }

    public static cn.jstxjf_.rte.mapping.holder.FlowableFluid getWaterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.FlowableFluid(net.minecraft.world.level.material.Fluids.WATER);
    }
}
