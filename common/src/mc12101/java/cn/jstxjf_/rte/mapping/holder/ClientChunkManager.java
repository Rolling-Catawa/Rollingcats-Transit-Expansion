package cn.jstxjf_.rte.mapping.holder;

public final class ClientChunkManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.multiplayer.ClientChunkCache> {

    public ClientChunkManager(net.minecraft.client.multiplayer.ClientChunkCache p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ClientChunkManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ClientChunkManager((net.minecraft.client.multiplayer.ClientChunkCache) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.multiplayer.ClientChunkCache;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(int p0, int p1) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).getChunkNow(p0, p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.WorldChunk(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.WorldChunk getWorldChunk(int p0, int p1, boolean p2) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).getChunk(p0, p1, p2);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.WorldChunk(v0));
    }

    public ClientChunkManager(cn.jstxjf_.rte.mapping.holder.ClientWorld p0, int p1) {
        super(new net.minecraft.client.multiplayer.ClientChunkCache(((net.minecraft.client.multiplayer.ClientLevel) p0.data), p1));
    }

    public void setMobSpawnOptions(boolean p0, boolean p1) {
        ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).setSpawnSettings(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockView getWorld() {
        return new cn.jstxjf_.rte.mapping.holder.BlockView(((net.minecraft.client.multiplayer.ClientChunkCache) this.data).getLevel());
    }

    public void close() throws java.io.IOException {
        ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).close();
    }

    public java.lang.String getDebugString() {
        return ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).gatherStats();
    }

    public void updateLoadDistance(int p0) {
        ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).updateViewRadius(p0);
    }

    public void setChunkForced(cn.jstxjf_.rte.mapping.holder.ChunkPos p0, boolean p1) {
        ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).updateChunkForced(((net.minecraft.world.level.ChunkPos) p0.data), p1);
    }

    public int getLoadedChunkCount() {
        return ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).getLoadedChunksCount();
    }

    public boolean isChunkLoaded(int p0, int p1) {
        return ((net.minecraft.client.multiplayer.ClientChunkCache) this.data).hasChunk(p0, p1);
    }
}
