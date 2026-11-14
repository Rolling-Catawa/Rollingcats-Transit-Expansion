package cn.jstxjf_.rte.mapping.holder;

public interface ItemColorProvider extends net.minecraft.client.color.item.ItemColor {

    public int getColor2(cn.jstxjf_.rte.mapping.holder.ItemStack p0, int p1);

    public default int getColor(net.minecraft.world.item.ItemStack p0, int p1) {
        return this.getColor2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0), p1);
    }
}
