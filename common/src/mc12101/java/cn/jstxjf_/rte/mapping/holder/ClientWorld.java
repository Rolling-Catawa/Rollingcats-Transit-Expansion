package cn.jstxjf_.rte.mapping.holder;

public final class ClientWorld extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.multiplayer.ClientLevel> {

    public ClientWorld(net.minecraft.client.multiplayer.ClientLevel p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ClientWorld cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ClientWorld((net.minecraft.client.multiplayer.ClientLevel) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.multiplayer.ClientLevel;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, int p2, int p3) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).setBlock(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2, p3);
    }

    public cn.jstxjf_.rte.mapping.holder.Difficulty getDifficulty() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(((net.minecraft.client.multiplayer.ClientLevel) this.data).getDifficulty());
    }

    public void sendEntityStatus(cn.jstxjf_.rte.mapping.holder.Entity p0, byte p1) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).broadcastEntityEvent(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public boolean isDirectionSolid(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Entity p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).loadedAndEntityCanStandOnFace(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.entity.Entity) p1.data), p2.data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getSpawnPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.client.multiplayer.ClientLevel) this.data).getSharedSpawnPos());
    }

    public cn.jstxjf_.rte.mapping.holder.ClientChunkManager getChunkManager() {
        return new cn.jstxjf_.rte.mapping.holder.ClientChunkManager(((net.minecraft.client.multiplayer.ClientLevel) this.data).getChunkSource());
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, double p1, double p2, double p3, cn.jstxjf_.rte.mapping.holder.SoundEvent p4, cn.jstxjf_.rte.mapping.holder.SoundCategory p5, float p6, float p7) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).playSound((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), p1, p2, p3, ((net.minecraft.sounds.SoundEvent) p4.data), p5.data, p6, p7);
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.SoundEvent p2, cn.jstxjf_.rte.mapping.holder.SoundCategory p3, float p4, float p5) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).playSound((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.sounds.SoundEvent) p2.data), p3.data, p4, p5);
    }

    public cn.jstxjf_.rte.mapping.holder.Chunk getChunk(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.Chunk(((net.minecraft.client.multiplayer.ClientLevel) this.data).getChunk(((net.minecraft.core.BlockPos) p0.data)));
    }

    public int getSeaLevel() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getSeaLevel();
    }

    public void updateListeners(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, int p3) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).sendBlockUpdated(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), p3);
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer() {
        final var v0 = ((net.minecraft.client.multiplayer.ClientLevel) this.data).getServer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.MinecraftServer(v0));
    }

    public int getMaxLightLevel() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getMaxLightLevel();
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getPlayerByUuid(java.util.UUID p0) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientLevel) this.data).getPlayerByUUID(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getRandomPosInChunk(int p0, int p1, int p2, int p3) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.client.multiplayer.ClientLevel) this.data).getBlockRandomPos(p0, p1, p2, p3));
    }

    public int getMoonPhase() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getMoonPhase();
    }

    public ClientWorld(cn.jstxjf_.rte.mapping.holder.ClientPlayNetworkHandler p0, net.minecraft.client.multiplayer.ClientLevel.ClientLevelData p1, net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> p2, net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType> p3, int p4, int p5, java.util.function.Supplier<net.minecraft.util.profiling.ProfilerFiller> p6, cn.jstxjf_.rte.mapping.holder.WorldRenderer p7, boolean p8, long p9) {
        super(new net.minecraft.client.multiplayer.ClientLevel(((net.minecraft.client.multiplayer.ClientPacketListener) p0.data), p1, p2, p3, p4, p5, p6, ((net.minecraft.client.renderer.LevelRenderer) p7.data), p8, p9));
    }

    public boolean isDay() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isDay();
    }

    public boolean containsFluid(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).containsAnyLiquid(((net.minecraft.world.phys.AABB) p0.data));
    }

    public int getHeight() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getHeight();
    }

    public void setSpawnPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0, float p1) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).setDefaultSpawnPos(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockEntity getBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientLevel) this.data).getBlockEntity(((net.minecraft.core.BlockPos) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(v0));
    }

    public void setMobSpawnOptions(boolean p0, boolean p1) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).setSpawnSettings(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult raycastBlock(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.VoxelShape p3, cn.jstxjf_.rte.mapping.holder.BlockState p4) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientLevel) this.data).clipWithInteractionOverride(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.VoxelShape) p3.data), ((net.minecraft.world.level.block.state.BlockState) p4.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockHitResult(v0));
    }

    public boolean isTopSolid(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Entity p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).loadedAndEntityCanStandOn(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.entity.Entity) p1.data));
    }

    public boolean canPlayerModifyAt(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).mayInteract(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public float getBrightness(cn.jstxjf_.rte.mapping.holder.Direction p0, boolean p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getShade(p0.data, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.client.multiplayer.ClientLevel) this.data).getBlockState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public double getDismountHeight(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getBlockFloorHeight(((net.minecraft.core.BlockPos) p0.data));
    }

    public boolean doesNotIntersectEntities(cn.jstxjf_.rte.mapping.holder.Entity p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isUnobstructed((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getTopPosition(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.client.multiplayer.ClientLevel) this.data).getHeightmapPos(p0.data, ((net.minecraft.core.BlockPos) p1.data)));
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, boolean p1, double p2, double p3, double p4, double p5, double p6, double p7) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6, p7);
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, double p1, double p2, double p3, double p4, double p5, double p6) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6);
    }

    public int getTopY(cn.jstxjf_.rte.mapping.holder.HeightMapType p0, int p1, int p2) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getHeight(p0.data, p1, p2);
    }

    public boolean isAir(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isEmptyBlock(((net.minecraft.core.BlockPos) p0.data));
    }

    public void addImportantParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, boolean p1, double p2, double p3, double p4, double p5, double p6, double p7) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).addAlwaysVisibleParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6, p7);
    }

    public void addImportantParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, double p1, double p2, double p3, double p4, double p5, double p6) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).addAlwaysVisibleParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6);
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).setBlockAndUpdate(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public boolean setBlockState(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, int p2) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).setBlock(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2);
    }

    public boolean canPlace(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.ShapeContext p2) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isUnobstructed(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.phys.shapes.CollisionContext) p2.data));
    }

    public int getLightLevel(cn.jstxjf_.rte.mapping.holder.LightType p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getBrightness(p0.data, ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockView getChunkAsView(int p0, int p1) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientLevel) this.data).getChunkForCollisions(p0, p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockView(v0));
    }

    public void updateNeighbors(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).blockUpdated(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Scoreboard getScoreboard() {
        return new cn.jstxjf_.rte.mapping.holder.Scoreboard(((net.minecraft.client.multiplayer.ClientLevel) this.data).getScoreboard());
    }

    public void playSound(double p0, double p1, double p2, cn.jstxjf_.rte.mapping.holder.SoundEvent p3, cn.jstxjf_.rte.mapping.holder.SoundCategory p4, float p5, float p6, boolean p7) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).playLocalSound(p0, p1, p2, ((net.minecraft.sounds.SoundEvent) p3.data), p4.data, p5, p6, p7);
    }

    public void close() throws java.io.IOException {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).close();
    }

    public boolean isEmittingRedstonePower(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).hasSignal(((net.minecraft.core.BlockPos) p0.data), p1.data);
    }

    public boolean isSpaceEmpty(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).noCollision(((net.minecraft.world.phys.AABB) p0.data));
    }

    public boolean isNight() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isNight();
    }

    public boolean spawnEntity(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).addFreshEntity(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void syncWorldEvent(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, int p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, int p3) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).levelEvent((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), p1, ((net.minecraft.core.BlockPos) p2.data), p3);
    }

    public boolean isRaining() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isRaining();
    }

    public java.lang.String asString() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).gatherChunkSourceStats();
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.WorldChunk(((net.minecraft.client.multiplayer.ClientLevel) this.data).getChunkAt(((net.minecraft.core.BlockPos) p0.data)));
    }

    public boolean isWater(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isWaterAt(((net.minecraft.core.BlockPos) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getCloudsColor(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.multiplayer.ClientLevel) this.data).getCloudColor(p0));
    }

    public void disconnect() {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).disconnect();
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getChunk(int p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.WorldChunk(((net.minecraft.client.multiplayer.ClientLevel) this.data).getChunk(p0, p1));
    }

    public boolean isThundering() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isThundering();
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.client.multiplayer.ClientLevel) this.data).getFluidState(((net.minecraft.core.BlockPos) p0.data)));
    }

    public long getTime() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).getGameTime();
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getClosestPlayer(double p0, double p1, double p2, double p3, boolean p4) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientLevel) this.data).getNearestPlayer(p0, p1, p2, p3, p4);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerEntity getClosestPlayer(cn.jstxjf_.rte.mapping.holder.Entity p0, double p1) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientLevel) this.data).getNearestPlayer(((net.minecraft.world.entity.Entity) p0.data), p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerEntity(v0));
    }

    public boolean isRegionLoaded(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).hasChunksAt(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean isRegionLoaded(int p0, int p1, int p2, int p3, int p4, int p5) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).hasChunksAt(p0, p1, p2, p3, p4, p5);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Entity p2, int p3) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1, (p2 == null ? null : ((net.minecraft.world.entity.Entity) p2.data)), p3);
    }

    public void updateNeighbor(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).neighborChanged(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public void playSoundAtBlockCenter(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.SoundEvent p1, cn.jstxjf_.rte.mapping.holder.SoundCategory p2, float p3, float p4, boolean p5) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).playLocalSound(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.sounds.SoundEvent) p1.data), p2.data, p3, p4, p5);
    }

    public boolean isPlayerInRange(double p0, double p1, double p2, double p3) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).hasNearbyAlivePlayer(p0, p1, p2, p3);
    }

    public long getLunarTime() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).dayTime();
    }

    public void addSyncedBlockEvent(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, int p2, int p3) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).blockEvent(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), p2, p3);
    }

    public void tick(java.util.function.BooleanSupplier p0) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).tick(p0);
    }

    public void playSoundFromEntity(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Entity p1, cn.jstxjf_.rte.mapping.holder.SoundEvent p2, cn.jstxjf_.rte.mapping.holder.SoundCategory p3, float p4, float p5) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).playSound((p0 == null ? null : ((net.minecraft.world.entity.player.Player) p0.data)), ((net.minecraft.world.entity.Entity) p1.data), ((net.minecraft.sounds.SoundEvent) p2.data), p3.data, p4, p5);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public boolean breakBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Entity p2) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).destroyBlock(((net.minecraft.core.BlockPos) p0.data), p1, (p2 == null ? null : ((net.minecraft.world.entity.Entity) p2.data)));
    }

    public void updateNeighborsExcept(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Block p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).updateNeighborsAtExceptFromFacing(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.Block) p1.data), p2.data);
    }

    public void tickEntities() {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).tickEntities();
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getEntityById(int p0) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientLevel) this.data).getEntity(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public final boolean isDebugWorld() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isDebug();
    }

    public boolean removeBlock(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).removeBlock(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandom() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.client.multiplayer.ClientLevel) this.data).getRandom());
    }

    public void removeBlockEntity(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        ((net.minecraft.client.multiplayer.ClientLevel) this.data).removeBlockEntity(((net.minecraft.core.BlockPos) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.DimensionType getDimension() {
        return new cn.jstxjf_.rte.mapping.holder.DimensionType(((net.minecraft.client.multiplayer.ClientLevel) this.data).dimensionType());
    }

    public boolean getIsClientMapped() {
        return ((net.minecraft.client.multiplayer.ClientLevel) this.data).isClientSide;
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.client.multiplayer.ClientLevel) this.data).random);
    }
}
