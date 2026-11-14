package cn.jstxjf_.rte.mapping.holder;

public abstract class BlockEntityAbstractMapping extends net.minecraft.world.level.block.entity.BlockEntity {

    public cn.jstxjf_.rte.mapping.holder.World getWorld2() {
        final var v0 = super.getLevel();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.World(v0));
    }

    public final net.minecraft.world.level.Level getLevel() {
        final var v0 = this.getWorld2();
        return (v0 == null ? null : ((net.minecraft.world.level.Level) v0.data));
    }

    public BlockEntityAbstractMapping(cn.jstxjf_.rte.mapping.holder.BlockEntityType<?> p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.BlockState p2) {
        super(((net.minecraft.world.level.block.entity.BlockEntityType) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data));
    }

    public void markDirty2() {
        super.setChanged();
    }

    public final void setChanged() {
        this.markDirty2();
    }

    public boolean onSyncedBlockEvent2(int p0, int p1) {
        return super.triggerEvent(p0, p1);
    }

    public final boolean triggerEvent(int p0, int p1) {
        return this.onSyncedBlockEvent2(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getCachedState2() {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(super.getBlockState());
    }

    public final net.minecraft.world.level.block.state.BlockState getBlockState() {
        final var v0 = this.getCachedState2();
        return (v0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getPos2() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(super.getBlockPos());
    }

    public final net.minecraft.core.BlockPos getBlockPos() {
        final var v0 = this.getPos2();
        return (v0 == null ? null : ((net.minecraft.core.BlockPos) v0.data));
    }

    public void markRemoved2() {
        super.setRemoved();
    }

    public final void setRemoved() {
        this.markRemoved2();
    }

    public boolean isRemoved2() {
        return super.isRemoved();
    }

    public final boolean isRemoved() {
        return this.isRemoved2();
    }

    public cn.jstxjf_.rte.mapping.holder.World getWorldMapped() {
        return (this.level == null ? null : new cn.jstxjf_.rte.mapping.holder.World(this.level));
    }

    public void setWorldMapped(cn.jstxjf_.rte.mapping.holder.World p0) {
        this.level = (p0 == null ? null : ((net.minecraft.world.level.Level) p0.data));
    }

    public boolean getRemovedMapped() {
        return this.remove;
    }

    public void setRemovedMapped(boolean p0) {
        this.remove = p0;
    }
}
