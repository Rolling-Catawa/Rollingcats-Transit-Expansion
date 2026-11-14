package cn.jstxjf_.rte.mapping.holder;

public final class ItemConvertible extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.ItemLike> {

    public ItemConvertible(net.minecraft.world.level.ItemLike p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ItemConvertible cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ItemConvertible((net.minecraft.world.level.ItemLike) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.ItemLike;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Item asItem() {
        return new cn.jstxjf_.rte.mapping.holder.Item(((net.minecraft.world.level.ItemLike) this.data).asItem());
    }
}
