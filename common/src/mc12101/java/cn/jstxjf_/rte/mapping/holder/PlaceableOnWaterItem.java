package cn.jstxjf_.rte.mapping.holder;

public final class PlaceableOnWaterItem extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.item.PlaceOnWaterBlockItem> {

    public PlaceableOnWaterItem(net.minecraft.world.item.PlaceOnWaterBlockItem p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.PlaceableOnWaterItem cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new PlaceableOnWaterItem((net.minecraft.world.item.PlaceOnWaterBlockItem) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.item.PlaceOnWaterBlockItem;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean canMine(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.PlayerEntity p3) {
        return ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).canAttackBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.entity.player.Player) p3.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Item byRawId(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.Item(net.minecraft.world.item.PlaceOnWaterBlockItem.byId(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).getName(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).getDescription());
    }

    public cn.jstxjf_.rte.mapping.holder.ItemPlacementContext getPlacementContext(cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p0) {
        final var v0 = ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).updatePlacementContext(((net.minecraft.world.item.context.BlockPlaceContext) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.ItemPlacementContext(v0));
    }

    public void inventoryTick(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.Entity p2, int p3, boolean p4) {
        ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).inventoryTick(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.Entity) p2.data), p3, p4);
    }

    public boolean postMine(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3, cn.jstxjf_.rte.mapping.holder.LivingEntity p4) {
        return ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).mineBlock(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), ((net.minecraft.core.BlockPos) p3.data), ((net.minecraft.world.entity.LivingEntity) p4.data));
    }

    public int getEnchantability() {
        return ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).getEnchantmentValue();
    }

    public cn.jstxjf_.rte.mapping.holder.Item asItem() {
        return new cn.jstxjf_.rte.mapping.holder.Item(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).asItem());
    }

    public boolean isEnchantable(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).isEnchantable(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public float getMiningSpeedMultiplier(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).getDestroySpeed(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public PlaceableOnWaterItem(cn.jstxjf_.rte.mapping.holder.Block p0, cn.jstxjf_.rte.mapping.holder.ItemSettings p1) {
        super(new net.minecraft.world.item.PlaceOnWaterBlockItem(((net.minecraft.world.level.block.Block) p0.data), ((net.minecraft.world.item.Item.Properties) p1.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.Item fromBlock(cn.jstxjf_.rte.mapping.holder.Block p0) {
        return new cn.jstxjf_.rte.mapping.holder.Item(net.minecraft.world.item.PlaceOnWaterBlockItem.byBlock(((net.minecraft.world.level.block.Block) p0.data)));
    }

    public static int getRawId(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return net.minecraft.world.item.PlaceOnWaterBlockItem.getId(((net.minecraft.world.item.Item) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getDefaultStack() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).getDefaultInstance());
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult useOnEntity(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2, cn.jstxjf_.rte.mapping.holder.Hand p3) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).interactLivingEntity(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.entity.player.Player) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data), p3.data));
    }

    public static boolean writeNbtToBlockEntity(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.ItemStack p3) {
        return net.minecraft.world.item.PlaceOnWaterBlockItem.updateCustomBlockEntityTag(((net.minecraft.world.level.Level) p0.data), (p1 == null ? null : ((net.minecraft.world.entity.player.Player) p1.data)), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.item.ItemStack) p3.data));
    }

    public java.lang.String getTranslationKey() {
        return ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).getDescriptionId();
    }

    public cn.jstxjf_.rte.mapping.holder.Block getBlock() {
        return new cn.jstxjf_.rte.mapping.holder.Block(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).getBlock());
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult useOnBlock(cn.jstxjf_.rte.mapping.holder.ItemUsageContext p0) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).useOn(((net.minecraft.world.item.context.UseOnContext) p0.data)));
    }

    public void onStoppedUsing(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2, int p3) {
        ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).releaseUsing(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data), p3);
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult place(cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p0) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).place(((net.minecraft.world.item.context.BlockPlaceContext) p0.data)));
    }

    public final cn.jstxjf_.rte.mapping.holder.Item getRecipeRemainder() {
        final var v0 = ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).getCraftingRemainingItem();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Item(v0));
    }

    public boolean postHit(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.LivingEntity p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2) {
        return ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).hurtEnemy(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.entity.LivingEntity) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data));
    }

    public void usageTick(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.LivingEntity p1, cn.jstxjf_.rte.mapping.holder.ItemStack p2, int p3) {
        ((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).onUseTick(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.LivingEntity) p1.data), ((net.minecraft.world.item.ItemStack) p2.data), p3);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack finishUsing(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.PlaceOnWaterBlockItem) this.data).finishUsingItem(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data)));
    }
}
