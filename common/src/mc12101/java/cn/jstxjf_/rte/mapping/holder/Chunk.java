package cn.jstxjf_.rte.mapping.holder;

public final class Chunk extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.chunk.ChunkAccess> {

    public Chunk(net.minecraft.world.level.chunk.ChunkAccess p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Chunk cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Chunk((net.minecraft.world.level.chunk.ChunkAccess) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.chunk.ChunkAccess;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, boolean p2) {
        final var v0 = ((net.minecraft.world.level.chunk.ChunkAccess) this.data).setBlockState(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockState(v0));
    }

    public int getMaxLightLevel() {
        return ((net.minecraft.world.level.chunk.ChunkAccess) this.data).getMaxLightLevel();
    }

    public void addPendingBlockEntityNbt(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        ((net.minecraft.world.level.chunk.ChunkAccess) this.data).setBlockEntityNbt(((net.minecraft.nbt.CompoundTag) p0.data));
    }

    public void addEntity(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.world.level.chunk.ChunkAccess) this.data).addEntity(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void markBlockForPostProcessing(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        ((net.minecraft.world.level.chunk.ChunkAccess) this.data).markPosForPostprocessing(((net.minecraft.core.BlockPos) p0.data));
    }

    public void markBlockForPostProcessing(short p0, int p1) {
        ((net.minecraft.world.level.chunk.ChunkAccess) this.data).addPackedPostProcess(p0, p1);
    }

    public int getLuminance(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.chunk.ChunkAccess) this.data).getLightEmission(((net.minecraft.core.BlockPos) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.chunk.ChunkAccess) this.data).getFluidState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockEntity getBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.world.level.chunk.ChunkAccess) this.data).getBlockEntity(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycastBlock(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.VoxelShape p3, cn.jstxjf_.rte.mapping.holder.BlockState p4) {
        final var v0 = ((net.minecraft.world.level.chunk.ChunkAccess) this.data).clipWithInteractionOverride(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.VoxelShape) p3.data), ((net.minecraft.world.level.block.state.BlockState) p4.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }

    public void removeBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        ((net.minecraft.world.level.chunk.ChunkAccess) this.data).removeBlockEntity(((net.minecraft.core.BlockPos) p0.data));
    }

    public static it.unimi.dsi.fastutil.shorts.ShortList getList(it.unimi.dsi.fastutil.shorts.ShortList[] p0, int p1) {
        return net.minecraft.world.level.chunk.ChunkAccess.getOrCreateOffsetList(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.chunk.ChunkAccess) this.data).getBlockState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public double getDismountHeight(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.chunk.ChunkAccess) this.data).getBlockFloorHeight(((net.minecraft.core.BlockPos) p0.data));
    }

    public boolean areSectionsEmptyBetween(int p0, int p1) {
        return ((net.minecraft.world.level.chunk.ChunkAccess) this.data).isYSpaceEmpty(p0, p1);
    }
}
