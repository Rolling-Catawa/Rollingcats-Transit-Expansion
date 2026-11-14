package cn.jstxjf_.rte.mapping.holder;

public abstract class PlaceableOnWaterItemAbstractMapping extends net.minecraft.world.item.PlaceOnWaterBlockItem {

    public boolean canMine2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.PlayerEntity p3) {
        return super.canAttackBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.entity.player.Player) p3.data));
    }

    public final boolean canAttackBlock(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, net.minecraft.world.entity.player.Player p3) {
        return this.canMine2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p3));
    }

    public static cn.jstxjf_.rte.mapping.holder.Item byRawId2(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.Item(net.minecraft.world.item.PlaceOnWaterBlockItem.byId(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName2(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.Text(super.getName(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public final net.minecraft.network.chat.Component getName(net.minecraft.world.item.ItemStack p0) {
        final var v0 = this.getName2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0));
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName2() {
        return new cn.jstxjf_.rte.mapping.holder.Text(super.getDescription());
    }

    public final net.minecraft.network.chat.Component getDescription() {
        final var v0 = this.getName2();
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemPlacementContext getPlacementContext2(cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p0) {
        final var v0 = super.updatePlacementContext(((net.minecraft.world.item.context.BlockPlaceContext) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.ItemPlacementContext(v0));
    }

    public final net.minecraft.world.item.context.BlockPlaceContext updatePlacementContext(net.minecraft.world.item.context.BlockPlaceContext p0) {
        final var v0 = this.getPlacementContext2(new cn.jstxjf_.rte.mapping.holder.ItemPlacementContext(p0));
        return (v0 == null ? null : ((net.minecraft.world.item.context.BlockPlaceContext) v0.data));
    }

    public void inventoryTick2(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.Entity p2, int p3, boolean p4) {
        super.inventoryTick(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.Entity) p2.data), p3, p4);
    }

    public final void inventoryTick(net.minecraft.world.item.ItemStack p0, net.minecraft.world.level.Level p1, net.minecraft.world.entity.Entity p2, int p3, boolean p4) {
        this.inventoryTick2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.Entity(p2), p3, p4);
    }

    public boolean postMine2(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3, cn.jstxjf_.rte.mapping.holder.LivingEntity p4) {
        return super.mineBlock(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), ((net.minecraft.core.BlockPos) p3.data), ((net.minecraft.world.entity.LivingEntity) p4.data));
    }

    public final boolean mineBlock(net.minecraft.world.item.ItemStack p0, net.minecraft.world.level.Level p1, net.minecraft.world.level.block.state.BlockState p2, net.minecraft.core.BlockPos p3, net.minecraft.world.entity.LivingEntity p4) {
        return this.postMine2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockState(p2), new cn.jstxjf_.rte.mapping.holder.BlockPos(p3), new cn.jstxjf_.rte.mapping.holder.LivingEntity(p4));
    }

    public int getEnchantability2() {
        return super.getEnchantmentValue();
    }

    public final int getEnchantmentValue() {
        return this.getEnchantability2();
    }

    public cn.jstxjf_.rte.mapping.holder.Item asItem2() {
        return new cn.jstxjf_.rte.mapping.holder.Item(super.asItem());
    }

    public final net.minecraft.world.item.Item asItem() {
        final var v0 = this.asItem2();
        return (v0 == null ? null : ((net.minecraft.world.item.Item) v0.data));
    }

    public boolean isEnchantable2(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return super.isEnchantable(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public final boolean isEnchantable(net.minecraft.world.item.ItemStack p0) {
        return this.isEnchantable2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0));
    }

    public float getMiningSpeedMultiplier2(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return super.getDestroySpeed(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public final float getDestroySpeed(net.minecraft.world.item.ItemStack p0, net.minecraft.world.level.block.state.BlockState p1) {
        return this.getMiningSpeedMultiplier2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0), new cn.jstxjf_.rte.mapping.holder.BlockState(p1));
    }

    public PlaceableOnWaterItemAbstractMapping(cn.jstxjf_.rte.mapping.holder.Block p0, cn.jstxjf_.rte.mapping.holder.ItemSettings p1) {
        super(((net.minecraft.world.level.block.Block) p0.data), ((net.minecraft.world.item.Item.Properties) p1.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Item fromBlock2(cn.jstxjf_.rte.mapping.holder.Block p0) {
        return new cn.jstxjf_.rte.mapping.holder.Item(net.minecraft.world.item.PlaceOnWaterBlockItem.byBlock(((net.minecraft.world.level.block.Block) p0.data)));
    }

    public static int getRawId2(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return net.minecraft.world.item.PlaceOnWaterBlockItem.getId(((net.minecraft.world.item.Item) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getDefaultStack2() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(super.getDefaultInstance());
    }

    public final net.minecraft.world.item.ItemStack getDefaultInstance() {
        final var v0 = this.getDefaultStack2();
        return (v0 == null ? null : ((net.minecraft.world.item.ItemStack) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult useOnEntity2(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2, cn.jstxjf_.rte.mapping.holder.Hand p3) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(super.interactLivingEntity(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.entity.player.Player) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data), p3.data));
    }

    public final net.minecraft.world.InteractionResult interactLivingEntity(net.minecraft.world.item.ItemStack p0, net.minecraft.world.entity.player.Player p1, net.minecraft.world.entity.LivingEntity p2, net.minecraft.world.InteractionHand p3) {
        final var v0 = this.useOnEntity2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0), new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p1), new cn.jstxjf_.rte.mapping.holder.LivingEntity(p2), cn.jstxjf_.rte.mapping.holder.Hand.convert(p3));
        return (v0 == null ? null : v0.data);
    }

    public static boolean writeNbtToBlockEntity2(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.ItemStack p3) {
        return net.minecraft.world.item.PlaceOnWaterBlockItem.updateCustomBlockEntityTag(((net.minecraft.world.level.Level) p0.data), (p1 == null ? null : ((net.minecraft.world.entity.player.Player) p1.data)), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.item.ItemStack) p3.data));
    }

    public java.lang.String getTranslationKey2() {
        return super.getDescriptionId();
    }

    public final java.lang.String getDescriptionId() {
        return this.getTranslationKey2();
    }

    public cn.jstxjf_.rte.mapping.holder.Block getBlock2() {
        return new cn.jstxjf_.rte.mapping.holder.Block(super.getBlock());
    }

    public final net.minecraft.world.level.block.Block getBlock() {
        final var v0 = this.getBlock2();
        return (v0 == null ? null : ((net.minecraft.world.level.block.Block) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult useOnBlock2(cn.jstxjf_.rte.mapping.holder.ItemUsageContext p0) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(super.useOn(((net.minecraft.world.item.context.UseOnContext) p0.data)));
    }

    public final net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext p0) {
        final var v0 = this.useOnBlock2(new cn.jstxjf_.rte.mapping.holder.ItemUsageContext(p0));
        return (v0 == null ? null : v0.data);
    }

    public void onStoppedUsing2(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2, int p3) {
        super.releaseUsing(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data), p3);
    }

    public final void releaseUsing(net.minecraft.world.item.ItemStack p0, net.minecraft.world.level.Level p1, net.minecraft.world.entity.LivingEntity p2, int p3) {
        this.onStoppedUsing2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.LivingEntity(p2), p3);
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult place2(cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p0) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(super.place(((net.minecraft.world.item.context.BlockPlaceContext) p0.data)));
    }

    public final net.minecraft.world.InteractionResult place(net.minecraft.world.item.context.BlockPlaceContext p0) {
        final var v0 = this.place2(new cn.jstxjf_.rte.mapping.holder.ItemPlacementContext(p0));
        return (v0 == null ? null : v0.data);
    }

    public final cn.jstxjf_.rte.mapping.holder.Item getRecipeRemainder2() {
        final var v0 = super.getCraftingRemainingItem();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Item(v0));
    }

    public boolean postHit2(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.LivingEntity p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2) {
        return super.hurtEnemy(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.entity.LivingEntity) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data));
    }

    public final boolean hurtEnemy(net.minecraft.world.item.ItemStack p0, net.minecraft.world.entity.LivingEntity p1, net.minecraft.world.entity.LivingEntity p2) {
        return this.postHit2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0), new cn.jstxjf_.rte.mapping.holder.LivingEntity(p1), new cn.jstxjf_.rte.mapping.holder.LivingEntity(p2));
    }

    public void usageTick2(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.LivingEntity p1, cn.jstxjf_.rte.mapping.holder.ItemStack p2, int p3) {
        super.onUseTick(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.LivingEntity) p1.data), ((net.minecraft.world.item.ItemStack) p2.data), p3);
    }

    public final void onUseTick(net.minecraft.world.level.Level p0, net.minecraft.world.entity.LivingEntity p1, net.minecraft.world.item.ItemStack p2, int p3) {
        this.usageTick2(new cn.jstxjf_.rte.mapping.holder.World(p0), new cn.jstxjf_.rte.mapping.holder.LivingEntity(p1), new cn.jstxjf_.rte.mapping.holder.ItemStack(p2), p3);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack finishUsing2(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.LivingEntity p2) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(super.finishUsingItem(((net.minecraft.world.item.ItemStack) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.world.entity.LivingEntity) p2.data)));
    }

    public final net.minecraft.world.item.ItemStack finishUsingItem(net.minecraft.world.item.ItemStack p0, net.minecraft.world.level.Level p1, net.minecraft.world.entity.LivingEntity p2) {
        final var v0 = this.finishUsing2(new cn.jstxjf_.rte.mapping.holder.ItemStack(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.LivingEntity(p2));
        return (v0 == null ? null : ((net.minecraft.world.item.ItemStack) v0.data));
    }
}
