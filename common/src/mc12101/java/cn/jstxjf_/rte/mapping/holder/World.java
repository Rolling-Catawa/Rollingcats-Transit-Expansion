package cn.jstxjf_.rte.mapping.holder;

public final class World extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.Level> {

    public World(net.minecraft.world.level.Level p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.World cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new World((net.minecraft.world.level.Level) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.Level;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Difficulty getDifficulty() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(((net.minecraft.world.level.Level) this.data).getDifficulty());
    }

    public void addImportantParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, boolean p1, double p2, double p3, double p4, double p5, double p6, double p7) {
        ((net.minecraft.world.level.Level) this.data).addAlwaysVisibleParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6, p7);
    }

    public void addImportantParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, double p1, double p2, double p3, double p4, double p5, double p6) {
        ((net.minecraft.world.level.Level) this.data).addAlwaysVisibleParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6);
    }

    public void sendEntityStatus(cn.jstxjf_.rte.mapping.holder.Entity p0, byte p1) {
        ((net.minecraft.world.level.Level) this.data).broadcastEntityEvent(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public boolean isDirectionSolid(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Entity p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return ((net.minecraft.world.level.Level) this.data).loadedAndEntityCanStandOnFace(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.entity.Entity) p1.data), p2.data);
    }

    public void playSound(double p0, double p1, double p2, cn.jstxjf_.rte.mapping.holder.SoundEvent p3, cn.jstxjf_.rte.mapping.holder.SoundCategory p4, float p5, float p6, boolean p7) {
        ((net.minecraft.world.level.Level) this.data).playLocalSound(p0, p1, p2, ((net.minecraft.sounds.SoundEvent) p3.data), p4.data, p5, p6, p7);
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.SoundEvent p2, cn.jstxjf_.rte.mapping.holder.SoundCategory p3, float p4, float p5) {
        ((net.minecraft.world.level.Level) this.data).playSound((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.sounds.SoundEvent) p2.data), p3.data, p4, p5);
    }

    public cn.jstxjf_.rte.mapping.holder.Chunk getChunk(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.Chunk(((net.minecraft.world.level.Level) this.data).getChunk(((net.minecraft.core.BlockPos) p0.data)));
    }

    public int getSeaLevel() {
        return ((net.minecraft.world.level.Level) this.data).getSeaLevel();
    }

    public cn.jstxjf_.rte.mapping.holder.Scoreboard getScoreboard() {
        return new cn.jstxjf_.rte.mapping.holder.Scoreboard(((net.minecraft.world.level.Level) this.data).getScoreboard());
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer() {
        final var v0 = ((net.minecraft.world.level.Level) this.data).getServer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.MinecraftServer(v0));
    }

    public int getMaxLightLevel() {
        return ((net.minecraft.world.level.Level) this.data).getMaxLightLevel();
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getPlayerByUuid(java.util.UUID p0) {
        final var v0 = ((net.minecraft.world.level.Level) this.data).getPlayerByUUID(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getRandomPosInChunk(int p0, int p1, int p2, int p3) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.level.Level) this.data).getBlockRandomPos(p0, p1, p2, p3));
    }

    public void disconnect() {
        ((net.minecraft.world.level.Level) this.data).disconnect();
    }

    public void setLightningTicksLeft(int p0) {
        ((net.minecraft.world.level.Level) this.data).setSkyFlashTime(p0);
    }

    public int getMoonPhase() {
        return ((net.minecraft.world.level.Level) this.data).getMoonPhase();
    }

    public boolean isDay() {
        return ((net.minecraft.world.level.Level) this.data).isDay();
    }

    public boolean containsFluid(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return ((net.minecraft.world.level.Level) this.data).containsAnyLiquid(((net.minecraft.world.phys.AABB) p0.data));
    }

    public int getHeight() {
        return ((net.minecraft.world.level.Level) this.data).getHeight();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockEntity getBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.world.level.Level) this.data).getBlockEntity(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(v0));
    }

    public void setMobSpawnOptions(boolean p0, boolean p1) {
        ((net.minecraft.world.level.Level) this.data).setSpawnSettings(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycastBlock(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.VoxelShape p3, cn.jstxjf_.rte.mapping.holder.BlockState p4) {
        final var v0 = ((net.minecraft.world.level.Level) this.data).clipWithInteractionOverride(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.VoxelShape) p3.data), ((net.minecraft.world.level.block.state.BlockState) p4.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.ChunkManager getChunkManager() {
        return new cn.jstxjf_.rte.mapping.holder.ChunkManager(((net.minecraft.world.level.Level) this.data).getChunkSource());
    }

    public boolean isTopSolid(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Entity p1) {
        return ((net.minecraft.world.level.Level) this.data).loadedAndEntityCanStandOn(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.entity.Entity) p1.data));
    }

    public boolean canPlayerModifyAt(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.Level) this.data).mayInteract(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.Level) this.data).getBlockState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public double getDismountHeight(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.Level) this.data).getBlockFloorHeight(((net.minecraft.core.BlockPos) p0.data));
    }

    public boolean doesNotIntersectEntities(cn.jstxjf_.rte.mapping.holder.Entity p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1) {
        return ((net.minecraft.world.level.Level) this.data).isUnobstructed((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getTopPosition(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.level.Level) this.data).getHeightmapPos(p0.data, ((net.minecraft.core.BlockPos) p1.data)));
    }

    public int getTopY(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, int p1, int p2) {
        return ((net.minecraft.world.level.Level) this.data).getHeight(p0.data, p1, p2);
    }

    public boolean isAir(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.Level) this.data).isEmptyBlock(((net.minecraft.core.BlockPos) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getEntityById(int p0) {
        final var v0 = ((net.minecraft.world.level.Level) this.data).getEntity(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public float getBrightness(cn.jstxjf_.rte.mapping.holder.Direction p0, boolean p1) {
        return ((net.minecraft.world.level.Level) this.data).getShade(p0.data, p1);
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, int p2, int p3) {
        return ((net.minecraft.world.level.Level) this.data).setBlock(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2, p3);
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.world.level.Level) this.data).setBlockAndUpdate(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, int p2) {
        return ((net.minecraft.world.level.Level) this.data).setBlock(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2);
    }

    public boolean canPlace(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.ShapeContext p2) {
        return ((net.minecraft.world.level.Level) this.data).isUnobstructed(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.phys.shapes.CollisionContext) p2.data));
    }

    public int getLightLevel(cn.jstxjf_.rte.mapping.holder.LightType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.Level) this.data).getBrightness(p0.data, ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockView getChunkAsView(int p0, int p1) {
        final var v0 = ((net.minecraft.world.level.Level) this.data).getChunkForCollisions(p0, p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockView(v0));
    }

    public void updateNeighbors(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1) {
        ((net.minecraft.world.level.Level) this.data).blockUpdated(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data));
    }

    public void updateListeners(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, int p3) {
        ((net.minecraft.world.level.Level) this.data).sendBlockUpdated(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), p3);
    }

    public void close() throws java.io.IOException {
        ((net.minecraft.world.level.Level) this.data).close();
    }

    public boolean isEmittingRedstonePower(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return ((net.minecraft.world.level.Level) this.data).hasSignal(((net.minecraft.core.BlockPos) p0.data), p1.data);
    }

    public boolean isSpaceEmpty(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return ((net.minecraft.world.level.Level) this.data).noCollision(((net.minecraft.world.phys.AABB) p0.data));
    }

    public boolean isNight() {
        return ((net.minecraft.world.level.Level) this.data).isNight();
    }

    public boolean spawnEntity(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.level.Level) this.data).addFreshEntity(((net.minecraft.world.entity.Entity) p0.data));
    }

    public boolean isRaining() {
        return ((net.minecraft.world.level.Level) this.data).isRaining();
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.WorldChunk(((net.minecraft.world.level.Level) this.data).getChunkAt(((net.minecraft.core.BlockPos) p0.data)));
    }

    public boolean isWater(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.Level) this.data).isWaterAt(((net.minecraft.core.BlockPos) p0.data));
    }

    public void setBlockBreakingInfo(int p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, int p2) {
        ((net.minecraft.world.level.Level) this.data).destroyBlockProgress(p0, ((net.minecraft.core.BlockPos) p1.data), p2);
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getChunk(int p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.WorldChunk(((net.minecraft.world.level.Level) this.data).getChunk(p0, p1));
    }

    public boolean isThundering() {
        return ((net.minecraft.world.level.Level) this.data).isThundering();
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.Level) this.data).getFluidState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public long getTime() {
        return ((net.minecraft.world.level.Level) this.data).getGameTime();
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getClosestPlayer(double p0, double p1, double p2, double p3, boolean p4) {
        final var v0 = ((net.minecraft.world.level.Level) this.data).getNearestPlayer(p0, p1, p2, p3, p4);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getClosestPlayer(cn.jstxjf_.rte.mapping.holder.Entity p0, double p1) {
        final var v0 = ((net.minecraft.world.level.Level) this.data).getNearestPlayer(((net.minecraft.world.entity.Entity) p0.data), p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public boolean isRegionLoaded(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.Level) this.data).hasChunksAt(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean isRegionLoaded(int p0, int p1, int p2, int p3, int p4, int p5) {
        return ((net.minecraft.world.level.Level) this.data).hasChunksAt(p0, p1, p2, p3, p4, p5);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Entity p2, int p3) {
        return ((net.minecraft.world.level.Level) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1, (p2 == null ? null : ((net.minecraft.world.entity.Entity) p2.data)), p3);
    }

    public boolean isSavingDisabled() {
        return ((net.minecraft.world.level.Level) this.data).noSave();
    }

    public void updateNeighbor(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        ((net.minecraft.world.level.Level) this.data).neighborChanged(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public boolean isPlayerInRange(double p0, double p1, double p2, double p3) {
        return ((net.minecraft.world.level.Level) this.data).hasNearbyAlivePlayer(p0, p1, p2, p3);
    }

    public boolean isClient() {
        return ((net.minecraft.world.level.Level) this.data).isClientSide();
    }

    public void syncWorldEvent(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, int p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, int p3) {
        ((net.minecraft.world.level.Level) this.data).levelEvent((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), p1, ((net.minecraft.core.BlockPos) p2.data), p3);
    }

    public long getLunarTime() {
        return ((net.minecraft.world.level.Level) this.data).dayTime();
    }

    public void addSyncedBlockEvent(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, int p2, int p3) {
        ((net.minecraft.world.level.Level) this.data).blockEvent(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), p2, p3);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        return ((net.minecraft.world.level.Level) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Entity p2) {
        return ((net.minecraft.world.level.Level) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1, (p2 == null ? null : ((net.minecraft.world.entity.Entity) p2.data)));
    }

    public boolean isChunkLoaded(int p0, int p1) {
        return ((net.minecraft.world.level.Level) this.data).hasChunk(p0, p1);
    }

    public void updateNeighborsExcept(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        ((net.minecraft.world.level.Level) this.data).updateNeighborsAtExceptFromFacing(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), p2.data);
    }

    public final boolean isDebugWorld() {
        return ((net.minecraft.world.level.Level) this.data).isDebug();
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, boolean p1, double p2, double p3, double p4, double p5, double p6, double p7) {
        ((net.minecraft.world.level.Level) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6, p7);
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, double p1, double p2, double p3, double p4, double p5, double p6) {
        ((net.minecraft.world.level.Level) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6);
    }

    public boolean removeBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        return ((net.minecraft.world.level.Level) this.data).removeBlock(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandom() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.world.level.Level) this.data).getRandom());
    }

    public void removeBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        ((net.minecraft.world.level.Level) this.data).removeBlockEntity(((net.minecraft.core.BlockPos) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.DimensionType getDimension() {
        return new cn.jstxjf_.rte.mapping.holder.DimensionType(((net.minecraft.world.level.Level) this.data).dimensionType());
    }

    public boolean getIsClientMapped() {
        return ((net.minecraft.world.level.Level) this.data).isClientSide;
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.world.level.Level) this.data).random);
    }
}
