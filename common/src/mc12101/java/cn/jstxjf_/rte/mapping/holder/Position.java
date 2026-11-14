package cn.jstxjf_.rte.mapping.holder;

public final class Position extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.core.Position> {

    public Position(net.minecraft.core.Position p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Position cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Position((net.minecraft.core.Position) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.core.Position;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }
}
