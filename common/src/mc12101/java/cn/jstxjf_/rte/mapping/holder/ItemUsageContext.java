package cn.jstxjf_.rte.mapping.holder;

public final class ItemUsageContext extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.item.context.UseOnContext> {

    public ItemUsageContext(net.minecraft.world.item.context.UseOnContext p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ItemUsageContext cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ItemUsageContext((net.minecraft.world.item.context.UseOnContext) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.item.context.UseOnContext;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean hitsInsideBlock() {
        return ((net.minecraft.world.item.context.UseOnContext) this.data).isInside();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getPlayerFacing() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.item.context.UseOnContext) this.data).getHorizontalDirection());
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getStack() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.item.context.UseOnContext) this.data).getItemInHand());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getHitPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.item.context.UseOnContext) this.data).getClickLocation());
    }

    public cn.jstxjf_.rte.mapping.holder.World getWorld() {
        return new cn.jstxjf_.rte.mapping.holder.World(((net.minecraft.world.item.context.UseOnContext) this.data).getLevel());
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getPlayer() {
        final var v0 = ((net.minecraft.world.item.context.UseOnContext) this.data).getPlayer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public ItemUsageContext(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Hand p1, cn.jstxjf_.rte.mapping.holder.BlockHitResult p2) {
        super(new net.minecraft.world.item.context.UseOnContext(((net.minecraft.world.entity.player.Player) p0.data), p1.data, ((net.minecraft.world.phys.BlockHitResult) p2.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.item.context.UseOnContext) this.data).getClickedPos());
    }

    public float getPlayerYaw() {
        return ((net.minecraft.world.item.context.UseOnContext) this.data).getRotation();
    }

    public boolean shouldCancelInteraction() {
        return ((net.minecraft.world.item.context.UseOnContext) this.data).isSecondaryUseActive();
    }

    public cn.jstxjf_.rte.mapping.holder.Hand getHand() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(((net.minecraft.world.item.context.UseOnContext) this.data).getHand());
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getSide() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.item.context.UseOnContext) this.data).getClickedFace());
    }
}
