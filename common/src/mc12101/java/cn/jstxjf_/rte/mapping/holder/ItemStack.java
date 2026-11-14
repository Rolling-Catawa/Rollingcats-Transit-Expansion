package cn.jstxjf_.rte.mapping.holder;

public final class ItemStack extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.item.ItemStack> {

    public ItemStack(net.minecraft.world.item.ItemStack p0) {
        super(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getTag() {
        final net.minecraft.nbt.CompoundTag tag = cn.jstxjf_.rte.compat.ItemData.getTag(this.data);
        return tag == null ? null : new cn.jstxjf_.rte.mapping.holder.CompoundTag(tag);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getOrCreateTag() {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(cn.jstxjf_.rte.compat.ItemData.getOrCreateTag(this.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.ItemStack cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ItemStack((net.minecraft.world.item.ItemStack) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.item.ItemStack;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack copy() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.ItemStack) this.data).copy());
    }

    public int getMaxDamage() {
        return ((net.minecraft.world.item.ItemStack) this.data).getMaxDamage();
    }

    public boolean isDamaged() {
        return ((net.minecraft.world.item.ItemStack) this.data).isDamaged();
    }

    public void increment(int p0) {
        ((net.minecraft.world.item.ItemStack) this.data).grow(p0);
    }

    public void decrement(int p0) {
        ((net.minecraft.world.item.ItemStack) this.data).shrink(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack finishUsing(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.LivingEntity p1) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.ItemStack) this.data).finishUsingItem(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.LivingEntity) p1.data)));
    }

    public void postHit(cn.jstxjf_.rte.mapping.holder.LivingEntity p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1) {
        ((net.minecraft.world.item.ItemStack) this.data).hurtEnemy(((net.minecraft.world.entity.LivingEntity) p0.data), ((net.minecraft.world.entity.player.Player) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getHolder() {
        final var v0 = ((net.minecraft.world.item.ItemStack) this.data).getEntityRepresentation();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public int getCount() {
        return ((net.minecraft.world.item.ItemStack) this.data).getCount();
    }

    public void setCount(int p0) {
        ((net.minecraft.world.item.ItemStack) this.data).setCount(p0);
    }

    public ItemStack(net.minecraft.core.Holder<net.minecraft.world.item.Item> p0, int p1) {
        super(new net.minecraft.world.item.ItemStack(p0, p1));
    }

    public ItemStack(cn.jstxjf_.rte.mapping.holder.ItemConvertible p0, int p1) {
        super(new net.minecraft.world.item.ItemStack(((net.minecraft.world.level.ItemLike) p0.data), p1));
    }

    public ItemStack(cn.jstxjf_.rte.mapping.holder.ItemConvertible p0) {
        super(new net.minecraft.world.item.ItemStack(((net.minecraft.world.level.ItemLike) p0.data)));
    }

    public ItemStack(net.minecraft.core.Holder<net.minecraft.world.item.Item> p0) {
        super(new net.minecraft.world.item.ItemStack(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Item getItem() {
        return new cn.jstxjf_.rte.mapping.holder.Item(((net.minecraft.world.item.ItemStack) this.data).getItem());
    }

    public void postMine(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.PlayerEntity p3) {
        ((net.minecraft.world.item.ItemStack) this.data).mineBlock(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.entity.player.Player) p3.data));
    }

    public void inventoryTick(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.Entity p1, int p2, boolean p3) {
        ((net.minecraft.world.item.ItemStack) this.data).inventoryTick(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.Entity) p1.data), p2, p3);
    }

    public boolean isSuitableFor(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return ((net.minecraft.world.item.ItemStack) this.data).isCorrectToolForDrops(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public float getMiningSpeedMultiplier(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return ((net.minecraft.world.item.ItemStack) this.data).getDestroySpeed(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public void onCraft(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, int p2) {
        ((net.minecraft.world.item.ItemStack) this.data).onCraftedBy(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.player.Player) p1.data), p2);
    }

    public java.lang.String getTranslationKey() {
        return ((net.minecraft.world.item.ItemStack) this.data).getDescriptionId();
    }

    public boolean isEmpty() {
        return ((net.minecraft.world.item.ItemStack) this.data).isEmpty();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack split(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.ItemStack) this.data).split(p0));
    }

    public boolean isStackable() {
        return ((net.minecraft.world.item.ItemStack) this.data).isStackable();
    }

    public boolean isEnchantable() {
        return ((net.minecraft.world.item.ItemStack) this.data).isEnchantable();
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult useOnEntity(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.LivingEntity p1, cn.jstxjf_.rte.mapping.holder.Hand p2) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.item.ItemStack) this.data).interactLivingEntity(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.world.entity.LivingEntity) p1.data), p2.data));
    }

    public void setHolder(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.world.item.ItemStack) this.data).setEntityRepresentation((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.ItemStack getEmptyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(net.minecraft.world.item.ItemStack.EMPTY);
    }
}
