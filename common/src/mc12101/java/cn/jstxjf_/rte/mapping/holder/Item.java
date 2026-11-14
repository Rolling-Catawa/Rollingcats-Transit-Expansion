package cn.jstxjf_.rte.mapping.holder;

public final class Item extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.item.Item> {

    public Item(net.minecraft.world.item.Item p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Item cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Item((net.minecraft.world.item.Item) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.item.Item;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean canMine(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.PlayerEntity p3) {
        return ((net.minecraft.world.item.Item) this.data).canAttackBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.entity.player.Player) p3.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Item byRawId(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.Item(net.minecraft.world.item.Item.byId(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.item.Item) this.data).getName(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.item.Item) this.data).getDescription());
    }

    public Item(cn.jstxjf_.rte.mapping.holder.ItemSettings p0) {
        super(new net.minecraft.world.item.Item(((net.minecraft.world.item.Item.Properties) p0.data)));
    }

    public void inventoryTick(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.Entity p2, int p3, boolean p4) {
        ((net.minecraft.world.item.Item) this.data).inventoryTick(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.Entity) p2.data), p3, p4);
    }

    public boolean postMine(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3, cn.jstxjf_.rte.mapping.holder.LivingEntity p4) {
        return ((net.minecraft.world.item.Item) this.data).mineBlock(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), ((net.minecraft.core.BlockPos) p3.data), ((net.minecraft.world.entity.LivingEntity) p4.data));
    }

    public int getEnchantability() {
        return ((net.minecraft.world.item.Item) this.data).getEnchantmentValue();
    }

    public cn.jstxjf_.rte.mapping.holder.Item asItem() {
        return new cn.jstxjf_.rte.mapping.holder.Item(((net.minecraft.world.item.Item) this.data).asItem());
    }

    public boolean isEnchantable(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.world.item.Item) this.data).isEnchantable(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public boolean isNetworkSynced() {
        return ((net.minecraft.world.item.Item) this.data).isComplex();
    }

    public float getMiningSpeedMultiplier(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.world.item.Item) this.data).getDestroySpeed(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Item fromBlock(cn.jstxjf_.rte.mapping.holder.Block p0) {
        return new cn.jstxjf_.rte.mapping.holder.Item(net.minecraft.world.item.Item.byBlock(((net.minecraft.world.level.block.Block) p0.data)));
    }

    public static int getRawId(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return net.minecraft.world.item.Item.getId(((net.minecraft.world.item.Item) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getDefaultStack() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.Item) this.data).getDefaultInstance());
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult useOnEntity(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2, cn.jstxjf_.rte.mapping.holder.Hand p3) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.item.Item) this.data).interactLivingEntity(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.entity.player.Player) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data), p3.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult useOnBlock(cn.jstxjf_.rte.mapping.holder.ItemUsageContext p0) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.item.Item) this.data).useOn(((net.minecraft.world.item.context.UseOnContext) p0.data)));
    }

    public java.lang.String getTranslationKey() {
        return ((net.minecraft.world.item.Item) this.data).getDescriptionId();
    }

    public void onStoppedUsing(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2, int p3) {
        ((net.minecraft.world.item.Item) this.data).releaseUsing(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data), p3);
    }

    public boolean hasRecipeRemainder() {
        return ((net.minecraft.world.item.Item) this.data).hasCraftingRemainingItem();
    }

    public final cn.jstxjf_.rte.mapping.holder.Item getRecipeRemainder() {
        final var v0 = ((net.minecraft.world.item.Item) this.data).getCraftingRemainingItem();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Item(v0));
    }

    public boolean hasGlint(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.world.item.Item) this.data).isFoil(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public boolean postHit(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.LivingEntity p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2) {
        return ((net.minecraft.world.item.Item) this.data).hurtEnemy(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.entity.LivingEntity) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data));
    }

    public void usageTick(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.LivingEntity p1, cn.jstxjf_.rte.mapping.holder.ItemStack p2, int p3) {
        ((net.minecraft.world.item.Item) this.data).onUseTick(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.LivingEntity) p1.data), ((net.minecraft.world.item.ItemStack) p2.data), p3);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack finishUsing(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.Item) this.data).finishUsingItem(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data)));
    }
}
