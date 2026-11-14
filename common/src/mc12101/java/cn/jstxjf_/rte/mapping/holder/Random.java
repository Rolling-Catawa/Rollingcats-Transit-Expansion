package cn.jstxjf_.rte.mapping.holder;

public final class Random extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.util.RandomSource> {

    public Random(net.minecraft.util.RandomSource p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Random cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Random((net.minecraft.util.RandomSource) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.util.RandomSource;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }
}
