package cn.jstxjf_.rte.mapping.holder;

public final class WorldAccess extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.LevelAccessor> {

    public WorldAccess(net.minecraft.world.level.LevelAccessor p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.WorldAccess cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new WorldAccess((net.minecraft.world.level.LevelAccessor) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.LevelAccessor;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public float getBrightness(cn.jstxjf_.rte.mapping.holder.Direction p0, boolean p1) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getShade(p0.data, p1);
    }

    public boolean canPlace(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.ShapeContext p2) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).isUnobstructed(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.phys.shapes.CollisionContext) p2.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Difficulty getDifficulty() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(((net.minecraft.world.level.LevelAccessor) this.data).getDifficulty());
    }

    public int getLightLevel(cn.jstxjf_.rte.mapping.holder.LightType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getBrightness(p0.data, ((net.minecraft.core.BlockPos) p1.data));
    }

    public void updateNeighbors(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1) {
        ((net.minecraft.world.level.LevelAccessor) this.data).blockUpdated(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Chunk getChunk(int p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.Chunk(((net.minecraft.world.level.LevelAccessor) this.data).getChunk(p0, p1));
    }

    public cn.jstxjf_.rte.mapping.holder.Chunk getChunk(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.Chunk(((net.minecraft.world.level.LevelAccessor) this.data).getChunk(((net.minecraft.core.BlockPos) p0.data)));
    }

    public int getMaxLightLevel() {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getMaxLightLevel();
    }

    public float getMoonSize() {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getMoonBrightness();
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getPlayerByUuid(java.util.UUID p0) {
        final var v0 = ((net.minecraft.world.level.LevelAccessor) this.data).getPlayerByUUID(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public int getTopY(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, int p1, int p2) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getHeight(p0.data, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.DimensionType getDimension() {
        return new cn.jstxjf_.rte.mapping.holder.DimensionType(((net.minecraft.world.level.LevelAccessor) this.data).dimensionType());
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, int p2, int p3) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).setBlock(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2, p3);
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, int p2) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).setBlock(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2);
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, double p1, double p2, double p3, double p4, double p5, double p6) {
        ((net.minecraft.world.level.LevelAccessor) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6);
    }

    public int getMoonPhase() {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getMoonPhase();
    }

    public boolean isClient() {
        return ((net.minecraft.world.level.LevelAccessor) this.data).isClientSide();
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandom() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.world.level.LevelAccessor) this.data).getRandom());
    }

    public boolean removeBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).removeBlock(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockView getChunkAsView(int p0, int p1) {
        final var v0 = ((net.minecraft.world.level.LevelAccessor) this.data).getChunkForCollisions(p0, p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockView(v0));
    }

    public int getSeaLevel() {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getSeaLevel();
    }

    public int getHeight() {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getHeight();
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.LevelAccessor) this.data).getFluidState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockEntity getBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.world.level.LevelAccessor) this.data).getBlockEntity(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(v0));
    }

    public float getSkyAngle(float p0) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getTimeOfDay(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycastBlock(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.VoxelShape p3, cn.jstxjf_.rte.mapping.holder.BlockState p4) {
        final var v0 = ((net.minecraft.world.level.LevelAccessor) this.data).clipWithInteractionOverride(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.VoxelShape) p3.data), ((net.minecraft.world.level.block.state.BlockState) p4.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getClosestPlayer(double p0, double p1, double p2, double p3, boolean p4) {
        final var v0 = ((net.minecraft.world.level.LevelAccessor) this.data).getNearestPlayer(p0, p1, p2, p3, p4);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getClosestPlayer(cn.jstxjf_.rte.mapping.holder.Entity p0, double p1) {
        final var v0 = ((net.minecraft.world.level.LevelAccessor) this.data).getNearestPlayer(((net.minecraft.world.entity.Entity) p0.data), p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.ChunkManager getChunkManager() {
        return new cn.jstxjf_.rte.mapping.holder.ChunkManager(((net.minecraft.world.level.LevelAccessor) this.data).getChunkSource());
    }

    public boolean isRegionLoaded(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).hasChunksAt(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean isRegionLoaded(int p0, int p1, int p2, int p3, int p4, int p5) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).hasChunksAt(p0, p1, p2, p3, p4, p5);
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.SoundEvent p2, cn.jstxjf_.rte.mapping.holder.SoundCategory p3, float p4, float p5) {
        ((net.minecraft.world.level.LevelAccessor) this.data).playSound((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.sounds.SoundEvent) p2.data), p3.data, p4, p5);
    }

    public boolean isPlayerInRange(double p0, double p1, double p2, double p3) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).hasNearbyAlivePlayer(p0, p1, p2, p3);
    }

    public void syncWorldEvent(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, int p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, int p3) {
        ((net.minecraft.world.level.LevelAccessor) this.data).levelEvent((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), p1, ((net.minecraft.core.BlockPos) p2.data), p3);
    }

    public void syncWorldEvent(int p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, int p2) {
        ((net.minecraft.world.level.LevelAccessor) this.data).levelEvent(p0, ((net.minecraft.core.BlockPos) p1.data), p2);
    }

    public long getLunarTime() {
        return ((net.minecraft.world.level.LevelAccessor) this.data).dayTime();
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Entity p2, int p3) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1, (p2 == null ? null : ((net.minecraft.world.entity.Entity) p2.data)), p3);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Entity p2) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1, (p2 == null ? null : ((net.minecraft.world.entity.Entity) p2.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.LevelAccessor) this.data).getBlockState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public double getDismountHeight(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getBlockFloorHeight(((net.minecraft.core.BlockPos) p0.data));
    }

    public boolean doesNotIntersectEntities(cn.jstxjf_.rte.mapping.holder.Entity p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1) {
        return ((net.minecraft.world.level.LevelAccessor) this.data).isUnobstructed((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getTopPosition(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.level.LevelAccessor) this.data).getHeightmapPos(p0.data, ((net.minecraft.core.BlockPos) p1.data)));
    }

    public int getAmbientDarkness() {
        return ((net.minecraft.world.level.LevelAccessor) this.data).getSkyDarken();
    }
}
