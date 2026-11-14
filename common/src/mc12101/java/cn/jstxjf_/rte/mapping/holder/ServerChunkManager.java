package cn.jstxjf_.rte.mapping.holder;

public final class ServerChunkManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.level.ServerChunkCache> {

    public ServerChunkManager(net.minecraft.server.level.ServerChunkCache p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ServerChunkManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ServerChunkManager((net.minecraft.server.level.ServerChunkCache) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.level.ServerChunkCache;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isChunkLoaded(int p0, int p1) {
        return ((net.minecraft.server.level.ServerChunkCache) this.data).hasChunk(p0, p1);
    }

    public boolean executeQueuedTasks() {
        return ((net.minecraft.server.level.ServerChunkCache) this.data).pollTask();
    }

    public java.lang.String getDebugString() {
        return ((net.minecraft.server.level.ServerChunkCache) this.data).gatherStats();
    }

    public cn.jstxjf_.rte.mapping.holder.ChunkGenerator getChunkGenerator() {
        return new cn.jstxjf_.rte.mapping.holder.ChunkGenerator(((net.minecraft.server.level.ServerChunkCache) this.data).getGenerator());
    }

    public java.lang.String getChunkLoadingDebugInfo(cn.jstxjf_.rte.mapping.holder.ChunkPos p0) {
        return ((net.minecraft.server.level.ServerChunkCache) this.data).getChunkDebugData(((net.minecraft.world.level.ChunkPos) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(int p0, int p1) {
        final var v0 = ((net.minecraft.server.level.ServerChunkCache) this.data).getChunkNow(p0, p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.WorldChunk(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.World getWorld() {
        return new cn.jstxjf_.rte.mapping.holder.World(((net.minecraft.server.level.ServerChunkCache) this.data).getLevel());
    }

    public ServerChunkManager(cn.jstxjf_.rte.mapping.holder.ServerWorld p0, net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess p1, com.mojang.datafixers.DataFixer p2, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager p3, java.util.concurrent.Executor p4, cn.jstxjf_.rte.mapping.holder.ChunkGenerator p5, int p6, int p7, boolean p8, net.minecraft.server.level.progress.ChunkProgressListener p9, net.minecraft.world.level.entity.ChunkStatusUpdateListener p10, java.util.function.Supplier<net.minecraft.world.level.storage.DimensionDataStorage> p11) {
        super(new net.minecraft.server.level.ServerChunkCache(((net.minecraft.server.level.ServerLevel) p0.data), p1, p2, p3, p4, ((net.minecraft.world.level.chunk.ChunkGenerator) p5.data), p6, p7, p8, p9, p10, p11));
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(int p0, int p1, boolean p2) {
        final var v0 = ((net.minecraft.server.level.ServerChunkCache) this.data).getChunk(p0, p1, p2);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.WorldChunk(v0));
    }

    public void setChunkForced(cn.jstxjf_.rte.mapping.holder.ChunkPos p0, boolean p1) {
        ((net.minecraft.server.level.ServerChunkCache) this.data).updateChunkForced(((net.minecraft.world.level.ChunkPos) p0.data), p1);
    }

    public void updatePosition(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0) {
        ((net.minecraft.server.level.ServerChunkCache) this.data).move(((net.minecraft.server.level.ServerPlayer) p0.data));
    }

    public void close() throws java.io.IOException {
        ((net.minecraft.server.level.ServerChunkCache) this.data).close();
    }

    public void save(boolean p0) {
        ((net.minecraft.server.level.ServerChunkCache) this.data).save(p0);
    }

    public void setMobSpawnOptions(boolean p0, boolean p1) {
        ((net.minecraft.server.level.ServerChunkCache) this.data).setSpawnSettings(p0, p1);
    }

    public void markForUpdate(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        ((net.minecraft.server.level.ServerChunkCache) this.data).blockChanged(((net.minecraft.core.BlockPos) p0.data));
    }
}
