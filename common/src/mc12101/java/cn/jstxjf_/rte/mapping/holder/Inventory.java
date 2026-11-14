package cn.jstxjf_.rte.mapping.holder;

public final class Inventory extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.Container> {

    public Inventory(net.minecraft.world.Container p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Inventory cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Inventory((net.minecraft.world.Container) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.Container;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isEmpty() {
        return ((net.minecraft.world.Container) this.data).isEmpty();
    }

    public int count(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return ((net.minecraft.world.Container) this.data).countItem(((net.minecraft.world.item.Item) p0.data));
    }

    public boolean isValid(int p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        return ((net.minecraft.world.Container) this.data).canPlaceItem(p0, ((net.minecraft.world.item.ItemStack) p1.data));
    }

    public boolean canPlayerUse(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return ((net.minecraft.world.Container) this.data).stillValid(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public void setStack(int p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        ((net.minecraft.world.Container) this.data).setItem(p0, ((net.minecraft.world.item.ItemStack) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack removeStack(int p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.Container) this.data).removeItem(p0, p1));
    }

    public int getMaxCountPerStack() {
        return ((net.minecraft.world.Container) this.data).getMaxStackSize();
    }

    public int size() {
        return ((net.minecraft.world.Container) this.data).getContainerSize();
    }
}
