package cn.jstxjf_.rte.mapping.holder;

public final class ItemPlacementContext extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.item.context.BlockPlaceContext> {

    public ItemPlacementContext(net.minecraft.world.item.context.BlockPlaceContext p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ItemPlacementContext cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ItemPlacementContext((net.minecraft.world.item.context.BlockPlaceContext) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.item.context.BlockPlaceContext;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static cn.jstxjf_.rte.mapping.holder.ItemPlacementContext offset(cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return new cn.jstxjf_.rte.mapping.holder.ItemPlacementContext(net.minecraft.world.item.context.BlockPlaceContext.at(((net.minecraft.world.item.context.BlockPlaceContext) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getStack() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.context.BlockPlaceContext) this.data).getItemInHand());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getHitPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.item.context.BlockPlaceContext) this.data).getClickLocation());
    }

    public cn.jstxjf_.rte.mapping.holder.Hand getHand() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(((net.minecraft.world.item.context.BlockPlaceContext) this.data).getHand());
    }

    public boolean hitsInsideBlock() {
        return ((net.minecraft.world.item.context.BlockPlaceContext) this.data).isInside();
    }

    public ItemPlacementContext(cn.jstxjf_.rte.mapping.holder.ItemUsageContext p0) {
        super(new net.minecraft.world.item.context.BlockPlaceContext(((net.minecraft.world.item.context.UseOnContext) p0.data)));
    }

    public ItemPlacementContext(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Hand p1, cn.jstxjf_.rte.mapping.holder.ItemStack p2, cn.jstxjf_.rte.mapping.holder.BlockHitResult p3) {
        super(new net.minecraft.world.item.context.BlockPlaceContext(((net.minecraft.world.entity.player.Player) p0.data), p1.data, ((net.minecraft.world.item.ItemStack) p2.data), ((net.minecraft.world.phys.BlockHitResult) p3.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.item.context.BlockPlaceContext) this.data).getClickedPos());
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getPlayerFacing() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.item.context.BlockPlaceContext) this.data).getHorizontalDirection());
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getPlayerLookDirection() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.item.context.BlockPlaceContext) this.data).getNearestLookingDirection());
    }

    public boolean canReplaceExisting() {
        return ((net.minecraft.world.item.context.BlockPlaceContext) this.data).replacingClickedOnBlock();
    }

    public boolean canPlace() {
        return ((net.minecraft.world.item.context.BlockPlaceContext) this.data).canPlace();
    }

    public cn.jstxjf_.rte.mapping.holder.World getWorld() {
        return new cn.jstxjf_.rte.mapping.holder.World(((net.minecraft.world.item.context.BlockPlaceContext) this.data).getLevel());
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getPlayer() {
        final var v0 = ((net.minecraft.world.item.context.BlockPlaceContext) this.data).getPlayer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public float getPlayerYaw() {
        return ((net.minecraft.world.item.context.BlockPlaceContext) this.data).getRotation();
    }

    public boolean shouldCancelInteraction() {
        return ((net.minecraft.world.item.context.BlockPlaceContext) this.data).isSecondaryUseActive();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getSide() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.item.context.BlockPlaceContext) this.data).getClickedFace());
    }
}
