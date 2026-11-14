package cn.jstxjf_.rte.mapping.holder;

public final class ItemSettings extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.item.Item.Properties> {

    public ItemSettings(net.minecraft.world.item.Item.Properties p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ItemSettings cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ItemSettings((net.minecraft.world.item.Item.Properties) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.item.Item.Properties;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemSettings fireproof() {
        return new cn.jstxjf_.rte.mapping.holder.ItemSettings(((net.minecraft.world.item.Item.Properties) this.data).fireResistant());
    }

    public cn.jstxjf_.rte.mapping.holder.ItemSettings maxCount(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemSettings(((net.minecraft.world.item.Item.Properties) this.data).stacksTo(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemSettings maxDamage(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemSettings(((net.minecraft.world.item.Item.Properties) this.data).durability(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemSettings recipeRemainder(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemSettings(((net.minecraft.world.item.Item.Properties) this.data).craftRemainder(((net.minecraft.world.item.Item) p0.data)));
    }

    public ItemSettings() {
        super(new net.minecraft.world.item.Item.Properties());
    }
}
