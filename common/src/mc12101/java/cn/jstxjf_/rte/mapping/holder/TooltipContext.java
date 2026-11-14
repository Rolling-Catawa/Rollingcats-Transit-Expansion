package cn.jstxjf_.rte.mapping.holder;

public final class TooltipContext extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.item.TooltipFlag> {

    public TooltipContext(net.minecraft.world.item.TooltipFlag p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.TooltipContext cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new TooltipContext((net.minecraft.world.item.TooltipFlag) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.item.TooltipFlag;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isAdvanced() {
        return ((net.minecraft.world.item.TooltipFlag) this.data).isAdvanced();
    }
}
