package cn.jstxjf_.rte.mapping.holder;

public final class WorldChunk extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.chunk.LevelChunk> {

    public WorldChunk(net.minecraft.world.level.chunk.LevelChunk p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.WorldChunk cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new WorldChunk((net.minecraft.world.level.chunk.LevelChunk) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.chunk.LevelChunk;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, boolean p2) {
        final var v0 = ((net.minecraft.world.level.chunk.LevelChunk) this.data).setBlockState(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockState(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getBlockEntityNbt(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.world.level.chunk.LevelChunk) this.data).getBlockEntityNbt(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.CompoundTag(v0));
    }

    public int getMaxLightLevel() {
        return ((net.minecraft.world.level.chunk.LevelChunk) this.data).getMaxLightLevel();
    }

    public void setInhabitedTime(long p0) {
        ((net.minecraft.world.level.chunk.LevelChunk) this.data).setInhabitedTime(p0);
    }

    public void setHeightmap(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, long[] p1) {
        ((net.minecraft.world.level.chunk.LevelChunk) this.data).setHeightmap(p0.data, p1);
    }

    public void addEntity(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.world.level.chunk.LevelChunk) this.data).addEntity(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void addPendingBlockEntityNbt(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        ((net.minecraft.world.level.chunk.LevelChunk) this.data).setBlockEntityNbt(((net.minecraft.nbt.CompoundTag) p0.data));
    }

    public WorldChunk(cn.jstxjf_.rte.mapping.holder.ServerWorld p0, net.minecraft.world.level.chunk.ProtoChunk p1, net.minecraft.world.level.chunk.LevelChunk.PostLoadProcessor p2) {
        super(new net.minecraft.world.level.chunk.LevelChunk(((net.minecraft.server.level.ServerLevel) p0.data), p1, p2));
    }

    public WorldChunk(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.ChunkPos p1, net.minecraft.world.level.chunk.UpgradeData p2, net.minecraft.world.ticks.LevelChunkTicks<net.minecraft.world.level.block.Block> p3, net.minecraft.world.ticks.LevelChunkTicks<net.minecraft.world.level.material.Fluid> p4, long p5, net.minecraft.world.level.chunk.LevelChunkSection[] p6, net.minecraft.world.level.chunk.LevelChunk.PostLoadProcessor p7, net.minecraft.world.level.levelgen.blending.BlendingData p8) {
        super(new net.minecraft.world.level.chunk.LevelChunk(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.level.ChunkPos) p1.data), p2, p3, p4, p5, p6, p7, p8));
    }

    public WorldChunk(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.ChunkPos p1) {
        super(new net.minecraft.world.level.chunk.LevelChunk(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.level.ChunkPos) p1.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.HeightMap getHeightmap(cn.jstxjf_.rte.mapping.holder.HeightMapType p0) {
        return new cn.jstxjf_.rte.mapping.holder.HeightMap(((net.minecraft.world.level.chunk.LevelChunk) this.data).getOrCreateHeightmapUnprimed(p0.data));
    }

    public void markBlockForPostProcessing(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        ((net.minecraft.world.level.chunk.LevelChunk) this.data).markPosForPostprocessing(((net.minecraft.core.BlockPos) p0.data));
    }

    public void markBlockForPostProcessing(short p0, int p1) {
        ((net.minecraft.world.level.chunk.LevelChunk) this.data).addPackedPostProcess(p0, p1);
    }

    public int getLuminance(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.chunk.LevelChunk) this.data).getLightEmission(((net.minecraft.core.BlockPos) p0.data));
    }

    public boolean isEmpty() {
        return ((net.minecraft.world.level.chunk.LevelChunk) this.data).isEmpty();
    }

    public it.unimi.dsi.fastutil.shorts.ShortList[] getPostProcessingLists() {
        return ((net.minecraft.world.level.chunk.LevelChunk) this.data).getPostProcessing();
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.chunk.LevelChunk) this.data).getFluidState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(int p0, int p1, int p2) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.chunk.LevelChunk) this.data).getFluidState(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockEntity getBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.world.level.chunk.LevelChunk) this.data).getBlockEntity(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycastBlock(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.VoxelShape p3, cn.jstxjf_.rte.mapping.holder.BlockState p4) {
        final var v0 = ((net.minecraft.world.level.chunk.LevelChunk) this.data).clipWithInteractionOverride(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.VoxelShape) p3.data), ((net.minecraft.world.level.block.state.BlockState) p4.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }

    public long getInhabitedTime() {
        return ((net.minecraft.world.level.chunk.LevelChunk) this.data).getInhabitedTime();
    }

    public cn.jstxjf_.rte.mapping.holder.ChunkPos getPos() {
        return new cn.jstxjf_.rte.mapping.holder.ChunkPos(((net.minecraft.world.level.chunk.LevelChunk) this.data).getPos());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.chunk.LevelChunk) this.data).getBlockState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public int sampleHeightmap(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, int p1, int p2) {
        return ((net.minecraft.world.level.chunk.LevelChunk) this.data).getHeight(p0.data, p1, p2);
    }

    public double getDismountHeight(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.chunk.LevelChunk) this.data).getBlockFloorHeight(((net.minecraft.core.BlockPos) p0.data));
    }

    public boolean areSectionsEmptyBetween(int p0, int p1) {
        return ((net.minecraft.world.level.chunk.LevelChunk) this.data).isYSpaceEmpty(p0, p1);
    }

    public void removeBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        ((net.minecraft.world.level.chunk.LevelChunk) this.data).removeBlockEntity(((net.minecraft.core.BlockPos) p0.data));
    }
}
