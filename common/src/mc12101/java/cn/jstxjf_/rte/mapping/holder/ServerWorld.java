package cn.jstxjf_.rte.mapping.holder;

public final class ServerWorld extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.level.ServerLevel> {

    public ServerWorld(net.minecraft.server.level.ServerLevel p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ServerWorld cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ServerWorld((net.minecraft.server.level.ServerLevel) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.level.ServerLevel;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Difficulty getDifficulty() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(((net.minecraft.server.level.ServerLevel) this.data).getDifficulty());
    }

    public void addImportantParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, boolean p1, double p2, double p3, double p4, double p5, double p6, double p7) {
        ((net.minecraft.server.level.ServerLevel) this.data).addAlwaysVisibleParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6, p7);
    }

    public void addImportantParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, double p1, double p2, double p3, double p4, double p5, double p6) {
        ((net.minecraft.server.level.ServerLevel) this.data).addAlwaysVisibleParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6);
    }

    public boolean isSavingDisabled() {
        return ((net.minecraft.server.level.ServerLevel) this.data).noSave();
    }

    public void setSpawnPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0, float p1) {
        ((net.minecraft.server.level.ServerLevel) this.data).setDefaultSpawnPos(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public boolean isDirectionSolid(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Entity p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return ((net.minecraft.server.level.ServerLevel) this.data).loadedAndEntityCanStandOnFace(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.entity.Entity) p1.data), p2.data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getSpawnPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.server.level.ServerLevel) this.data).getSharedSpawnPos());
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, double p1, double p2, double p3, cn.jstxjf_.rte.mapping.holder.SoundEvent p4, cn.jstxjf_.rte.mapping.holder.SoundCategory p5, float p6, float p7) {
        ((net.minecraft.server.level.ServerLevel) this.data).playSound((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), p1, p2, p3, ((net.minecraft.sounds.SoundEvent) p4.data), p5.data, p6, p7);
    }

    public void playSound(double p0, double p1, double p2, cn.jstxjf_.rte.mapping.holder.SoundEvent p3, cn.jstxjf_.rte.mapping.holder.SoundCategory p4, float p5, float p6, boolean p7) {
        ((net.minecraft.server.level.ServerLevel) this.data).playLocalSound(p0, p1, p2, ((net.minecraft.sounds.SoundEvent) p3.data), p4.data, p5, p6, p7);
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.SoundEvent p2, cn.jstxjf_.rte.mapping.holder.SoundCategory p3, float p4, float p5) {
        ((net.minecraft.server.level.ServerLevel) this.data).playSound((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.sounds.SoundEvent) p2.data), p3.data, p4, p5);
    }

    public cn.jstxjf_.rte.mapping.holder.Chunk getChunk(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.Chunk(((net.minecraft.server.level.ServerLevel) this.data).getChunk(((net.minecraft.core.BlockPos) p0.data)));
    }

    public int getSeaLevel() {
        return ((net.minecraft.server.level.ServerLevel) this.data).getSeaLevel();
    }

    public int getMaxLightLevel() {
        return ((net.minecraft.server.level.ServerLevel) this.data).getMaxLightLevel();
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getPlayerByUuid(java.util.UUID p0) {
        final var v0 = ((net.minecraft.server.level.ServerLevel) this.data).getPlayerByUUID(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getRandomPosInChunk(int p0, int p1, int p2, int p3) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.server.level.ServerLevel) this.data).getBlockRandomPos(p0, p1, p2, p3));
    }

    public void updateNeighbor(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        ((net.minecraft.server.level.ServerLevel) this.data).neighborChanged(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public float getBrightness(cn.jstxjf_.rte.mapping.holder.Direction p0, boolean p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).getShade(p0.data, p1);
    }

    public void disconnect() {
        ((net.minecraft.server.level.ServerLevel) this.data).disconnect();
    }

    public void setLightningTicksLeft(int p0) {
        ((net.minecraft.server.level.ServerLevel) this.data).setSkyFlashTime(p0);
    }

    public int getMoonPhase() {
        return ((net.minecraft.server.level.ServerLevel) this.data).getMoonPhase();
    }

    public void addSyncedBlockEvent(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, int p2, int p3) {
        ((net.minecraft.server.level.ServerLevel) this.data).blockEvent(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), p2, p3);
    }

    public void updateNeighborsExcept(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        ((net.minecraft.server.level.ServerLevel) this.data).updateNeighborsAtExceptFromFacing(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), p2.data);
    }

    public void updateListeners(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, int p3) {
        ((net.minecraft.server.level.ServerLevel) this.data).sendBlockUpdated(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), p3);
    }

    public it.unimi.dsi.fastutil.longs.LongSet getForcedChunks() {
        return ((net.minecraft.server.level.ServerLevel) this.data).getForcedChunks();
    }

    public boolean isDay() {
        return ((net.minecraft.server.level.ServerLevel) this.data).isDay();
    }

    public boolean containsFluid(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return ((net.minecraft.server.level.ServerLevel) this.data).containsAnyLiquid(((net.minecraft.world.phys.AABB) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ServerChunkManager getChunkManager() {
        return new cn.jstxjf_.rte.mapping.holder.ServerChunkManager(((net.minecraft.server.level.ServerLevel) this.data).getChunkSource());
    }

    public int getHeight() {
        return ((net.minecraft.server.level.ServerLevel) this.data).getHeight();
    }

    public void close() throws java.io.IOException {
        ((net.minecraft.server.level.ServerLevel) this.data).close();
    }

    public boolean spawnEntity(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.server.level.ServerLevel) this.data).addFreshEntity(((net.minecraft.world.entity.Entity) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockEntity getBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.server.level.ServerLevel) this.data).getBlockEntity(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(v0));
    }

    public boolean isFlat() {
        return ((net.minecraft.server.level.ServerLevel) this.data).isFlat();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycastBlock(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.VoxelShape p3, cn.jstxjf_.rte.mapping.holder.BlockState p4) {
        final var v0 = ((net.minecraft.server.level.ServerLevel) this.data).clipWithInteractionOverride(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.VoxelShape) p3.data), ((net.minecraft.world.level.block.state.BlockState) p4.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }

    public boolean isTopSolid(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Entity p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).loadedAndEntityCanStandOn(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.entity.Entity) p1.data));
    }

    public ServerWorld(cn.jstxjf_.rte.mapping.holder.MinecraftServer p0, java.util.concurrent.Executor p1, net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess p2, net.minecraft.world.level.storage.ServerLevelData p3, net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> p4, cn.jstxjf_.rte.mapping.holder.DimensionOptions p5, net.minecraft.server.level.progress.ChunkProgressListener p6, boolean p7, long p8, java.util.List<net.minecraft.world.level.CustomSpawner> p9, boolean p10, net.minecraft.world.RandomSequences p11) {
        super(new net.minecraft.server.level.ServerLevel(((net.minecraft.server.MinecraftServer) p0.data), p1, p2, p3, p4, ((net.minecraft.world.level.dimension.LevelStem) p5.data), p6, p7, p8, p9, p10, p11));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.server.level.ServerLevel) this.data).getBlockState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public double getDismountHeight(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.server.level.ServerLevel) this.data).getBlockFloorHeight(((net.minecraft.core.BlockPos) p0.data));
    }

    public boolean doesNotIntersectEntities(cn.jstxjf_.rte.mapping.holder.Entity p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).isUnobstructed((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getTopPosition(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.server.level.ServerLevel) this.data).getHeightmapPos(p0.data, ((net.minecraft.core.BlockPos) p1.data)));
    }

    public void setTimeOfDay(long p0) {
        ((net.minecraft.server.level.ServerLevel) this.data).setDayTime(p0);
    }

    public int getTopY(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, int p1, int p2) {
        return ((net.minecraft.server.level.ServerLevel) this.data).getHeight(p0.data, p1, p2);
    }

    public boolean isAir(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.server.level.ServerLevel) this.data).isEmptyBlock(((net.minecraft.core.BlockPos) p0.data));
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, int p2, int p3) {
        return ((net.minecraft.server.level.ServerLevel) this.data).setBlock(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2, p3);
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).setBlockAndUpdate(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, int p2) {
        return ((net.minecraft.server.level.ServerLevel) this.data).setBlock(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2);
    }

    public boolean canPlace(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.ShapeContext p2) {
        return ((net.minecraft.server.level.ServerLevel) this.data).isUnobstructed(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.phys.shapes.CollisionContext) p2.data));
    }

    public void updateNeighbors(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1) {
        ((net.minecraft.server.level.ServerLevel) this.data).blockUpdated(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data));
    }

    public void dump(java.nio.file.Path p0) throws java.io.IOException {
        ((net.minecraft.server.level.ServerLevel) this.data).saveDebugReport(p0);
    }

    public int getLightLevel(cn.jstxjf_.rte.mapping.holder.LightType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).getBrightness(p0.data, ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockView getChunkAsView(int p0, int p1) {
        final var v0 = ((net.minecraft.server.level.ServerLevel) this.data).getChunkForCollisions(p0, p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockView(v0));
    }

    public boolean isEmittingRedstonePower(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).hasSignal(((net.minecraft.core.BlockPos) p0.data), p1.data);
    }

    public boolean isSpaceEmpty(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return ((net.minecraft.server.level.ServerLevel) this.data).noCollision(((net.minecraft.world.phys.AABB) p0.data));
    }

    public boolean isNight() {
        return ((net.minecraft.server.level.ServerLevel) this.data).isNight();
    }

    public boolean isRaining() {
        return ((net.minecraft.server.level.ServerLevel) this.data).isRaining();
    }

    public boolean canPlayerModifyAt(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).mayInteract(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.WorldChunk(((net.minecraft.server.level.ServerLevel) this.data).getChunkAt(((net.minecraft.core.BlockPos) p0.data)));
    }

    public boolean isWater(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.server.level.ServerLevel) this.data).isWaterAt(((net.minecraft.core.BlockPos) p0.data));
    }

    public void tick(java.util.function.BooleanSupplier p0) {
        ((net.minecraft.server.level.ServerLevel) this.data).tick(p0);
    }

    public void sendEntityStatus(cn.jstxjf_.rte.mapping.holder.Entity p0, byte p1) {
        ((net.minecraft.server.level.ServerLevel) this.data).broadcastEntityEvent(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity getRandomAlivePlayer() {
        final var v0 = ((net.minecraft.server.level.ServerLevel) this.data).getRandomPlayer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getChunk(int p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.WorldChunk(((net.minecraft.server.level.ServerLevel) this.data).getChunk(p0, p1));
    }

    public boolean isThundering() {
        return ((net.minecraft.server.level.ServerLevel) this.data).isThundering();
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.server.level.ServerLevel) this.data).getFluidState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public long getTime() {
        return ((net.minecraft.server.level.ServerLevel) this.data).getGameTime();
    }

    public long getSeed() {
        return ((net.minecraft.server.level.ServerLevel) this.data).getSeed();
    }

    public boolean setChunkForced(int p0, int p1, boolean p2) {
        return ((net.minecraft.server.level.ServerLevel) this.data).setChunkForced(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getEntity(java.util.UUID p0) {
        final var v0 = ((net.minecraft.server.level.ServerLevel) this.data).getEntity(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getClosestPlayer(double p0, double p1, double p2, double p3, boolean p4) {
        final var v0 = ((net.minecraft.server.level.ServerLevel) this.data).getNearestPlayer(p0, p1, p2, p3, p4);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getClosestPlayer(cn.jstxjf_.rte.mapping.holder.Entity p0, double p1) {
        final var v0 = ((net.minecraft.server.level.ServerLevel) this.data).getNearestPlayer(((net.minecraft.world.entity.Entity) p0.data), p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public boolean isRegionLoaded(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).hasChunksAt(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean isRegionLoaded(int p0, int p1, int p2, int p3, int p4, int p5) {
        return ((net.minecraft.server.level.ServerLevel) this.data).hasChunksAt(p0, p1, p2, p3, p4, p5);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Entity p2, int p3) {
        return ((net.minecraft.server.level.ServerLevel) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1, (p2 == null ? null : ((net.minecraft.world.entity.Entity) p2.data)), p3);
    }

    public void tickChunk(cn.jstxjf_.rte.mapping.holder.WorldChunk p0, int p1) {
        ((net.minecraft.server.level.ServerLevel) this.data).tickChunk(((net.minecraft.world.level.chunk.LevelChunk) p0.data), p1);
    }

    public boolean isPlayerInRange(double p0, double p1, double p2, double p3) {
        return ((net.minecraft.server.level.ServerLevel) this.data).hasNearbyAlivePlayer(p0, p1, p2, p3);
    }

    public boolean isClient() {
        return ((net.minecraft.server.level.ServerLevel) this.data).isClientSide();
    }

    public long getLunarTime() {
        return ((net.minecraft.server.level.ServerLevel) this.data).dayTime();
    }

    public void playSoundFromEntity(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Entity p1, cn.jstxjf_.rte.mapping.holder.SoundEvent p2, cn.jstxjf_.rte.mapping.holder.SoundCategory p3, float p4, float p5) {
        ((net.minecraft.server.level.ServerLevel) this.data).playSound((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), ((net.minecraft.world.entity.Entity) p1.data), ((net.minecraft.sounds.SoundEvent) p2.data), p3.data, p4, p5);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Entity p2) {
        return ((net.minecraft.server.level.ServerLevel) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1, (p2 == null ? null : ((net.minecraft.world.entity.Entity) p2.data)));
    }

    public boolean isChunkLoaded(int p0, int p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).hasChunk(p0, p1);
    }

    public void setWeather(int p0, int p1, boolean p2, boolean p3) {
        ((net.minecraft.server.level.ServerLevel) this.data).setWeatherParameters(p0, p1, p2, p3);
    }

    public void syncWorldEvent(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, int p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, int p3) {
        ((net.minecraft.server.level.ServerLevel) this.data).levelEvent((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), p1, ((net.minecraft.core.BlockPos) p2.data), p3);
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer() {
        return new cn.jstxjf_.rte.mapping.holder.MinecraftServer(((net.minecraft.server.level.ServerLevel) this.data).getServer());
    }

    public final boolean isDebugWorld() {
        return ((net.minecraft.server.level.ServerLevel) this.data).isDebug();
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, boolean p1, double p2, double p3, double p4, double p5, double p6, double p7) {
        ((net.minecraft.server.level.ServerLevel) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6, p7);
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, double p1, double p2, double p3, double p4, double p5, double p6) {
        ((net.minecraft.server.level.ServerLevel) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6);
    }

    public boolean removeBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        return ((net.minecraft.server.level.ServerLevel) this.data).removeBlock(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandom() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.server.level.ServerLevel) this.data).getRandom());
    }

    public void removeBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        ((net.minecraft.server.level.ServerLevel) this.data).removeBlockEntity(((net.minecraft.core.BlockPos) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.DimensionType getDimension() {
        return new cn.jstxjf_.rte.mapping.holder.DimensionType(((net.minecraft.server.level.ServerLevel) this.data).dimensionType());
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.server.level.ServerLevel) this.data).random);
    }

    public boolean getIsClientMapped() {
        return ((net.minecraft.server.level.ServerLevel) this.data).isClientSide;
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockPos getEndSpawnPosMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(net.minecraft.server.level.ServerLevel.END_SPAWN_POINT);
    }
}
