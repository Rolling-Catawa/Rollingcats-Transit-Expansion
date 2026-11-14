package cn.jstxjf_.rte.mapping.holder;

public final class BlockEntity extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.entity.BlockEntity> {

    public BlockEntity(net.minecraft.world.level.block.entity.BlockEntity p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockEntity cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BlockEntity((net.minecraft.world.level.block.entity.BlockEntity) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.entity.BlockEntity;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.World getWorld() {
        final var v0 = ((net.minecraft.world.level.block.entity.BlockEntity) this.data).getLevel();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.World(v0));
    }

    public void markDirty() {
        ((net.minecraft.world.level.block.entity.BlockEntity) this.data).setChanged();
    }

    public boolean onSyncedBlockEvent(int p0, int p1) {
        return ((net.minecraft.world.level.block.entity.BlockEntity) this.data).triggerEvent(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getCachedState() {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.block.entity.BlockEntity) this.data).getBlockState());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.level.block.entity.BlockEntity) this.data).getBlockPos());
    }

    public void markRemoved() {
        ((net.minecraft.world.level.block.entity.BlockEntity) this.data).setRemoved();
    }

    public boolean isRemoved() {
        return ((net.minecraft.world.level.block.entity.BlockEntity) this.data).isRemoved();
    }
}
