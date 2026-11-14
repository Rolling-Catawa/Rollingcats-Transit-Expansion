package cn.jstxjf_.rte.mapping.holder;

public final class PlayerInventory extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.entity.player.Inventory> {

    public PlayerInventory(net.minecraft.world.entity.player.Inventory p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.PlayerInventory cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new PlayerInventory((net.minecraft.world.entity.player.Inventory) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.entity.player.Inventory;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public float getBlockBreakingSpeed(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return ((net.minecraft.world.entity.player.Inventory) this.data).getDestroySpeed(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public boolean isEmpty() {
        return ((net.minecraft.world.entity.player.Inventory) this.data).isEmpty();
    }

    public void setStack(int p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        ((net.minecraft.world.entity.player.Inventory) this.data).setItem(p0, ((net.minecraft.world.item.ItemStack) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getCustomName() {
        final var v0 = ((net.minecraft.world.entity.player.Inventory) this.data).getCustomName();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(v0));
    }

    public void dropAll() {
        ((net.minecraft.world.entity.player.Inventory) this.data).dropAll();
    }

    public void clone(cn.jstxjf_.rte.mapping.holder.PlayerInventory p0) {
        ((net.minecraft.world.entity.player.Inventory) this.data).replaceWith(((net.minecraft.world.entity.player.Inventory) p0.data));
    }

    public boolean insertStack(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.world.entity.player.Inventory) this.data).add(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public void swapSlotWithHotbar(int p0) {
        ((net.minecraft.world.entity.player.Inventory) this.data).pickSlot(p0);
    }

    public int count(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return ((net.minecraft.world.entity.player.Inventory) this.data).countItem(((net.minecraft.world.item.Item) p0.data));
    }

    public static int getHotbarSize() {
        return net.minecraft.world.entity.player.Inventory.getSelectionSize();
    }

    public static boolean isValidHotbarIndex(int p0) {
        return net.minecraft.world.entity.player.Inventory.isHotbarSlot(p0);
    }

    public void scrollInHotbar(double p0) {
        ((net.minecraft.world.entity.player.Inventory) this.data).swapPaint(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.entity.player.Inventory) this.data).getDisplayName());
    }

    public boolean contains(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.world.entity.player.Inventory) this.data).contains(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack removeStack(int p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.entity.player.Inventory) this.data).removeItem(p0, p1));
    }

    public boolean canPlayerUse(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return ((net.minecraft.world.entity.player.Inventory) this.data).stillValid(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.entity.player.Inventory) this.data).getName());
    }

    public PlayerInventory(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        super(new net.minecraft.world.entity.player.Inventory(((net.minecraft.world.entity.player.Player) p0.data)));
    }

    public boolean hasCustomName() {
        return ((net.minecraft.world.entity.player.Inventory) this.data).hasCustomName();
    }

    public int getSelectedSlotMapped() {
        return ((net.minecraft.world.entity.player.Inventory) this.data).selected;
    }

    public void setSelectedSlotMapped(int p0) {
        ((net.minecraft.world.entity.player.Inventory) this.data).selected = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getPlayerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.PlayerEntity(((net.minecraft.world.entity.player.Inventory) this.data).player);
    }
}
