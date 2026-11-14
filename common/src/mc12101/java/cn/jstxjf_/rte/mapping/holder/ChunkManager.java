package cn.jstxjf_.rte.mapping.holder;

public final class ChunkManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.chunk.ChunkSource> {

    public ChunkManager(net.minecraft.world.level.chunk.ChunkSource p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ChunkManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ChunkManager((net.minecraft.world.level.chunk.ChunkSource) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.chunk.ChunkSource;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void setChunkForced(cn.jstxjf_.rte.mapping.holder.ChunkPos p0, boolean p1) {
        ((net.minecraft.world.level.chunk.ChunkSource) this.data).updateChunkForced(((net.minecraft.world.level.ChunkPos) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(int p0, int p1) {
        final var v0 = ((net.minecraft.world.level.chunk.ChunkSource) this.data).getChunkNow(p0, p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.WorldChunk(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(int p0, int p1, boolean p2) {
        final var v0 = ((net.minecraft.world.level.chunk.ChunkSource) this.data).getChunk(p0, p1, p2);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.WorldChunk(v0));
    }

    public void setMobSpawnOptions(boolean p0, boolean p1) {
        ((net.minecraft.world.level.chunk.ChunkSource) this.data).setSpawnSettings(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockView getWorld() {
        return new cn.jstxjf_.rte.mapping.holder.BlockView(((net.minecraft.world.level.chunk.ChunkSource) this.data).getLevel());
    }

    public void close() throws java.io.IOException {
        ((net.minecraft.world.level.chunk.ChunkSource) this.data).close();
    }

    public java.lang.String getDebugString() {
        return ((net.minecraft.world.level.chunk.ChunkSource) this.data).gatherStats();
    }

    public boolean isChunkLoaded(int p0, int p1) {
        return ((net.minecraft.world.level.chunk.ChunkSource) this.data).hasChunk(p0, p1);
    }
}
