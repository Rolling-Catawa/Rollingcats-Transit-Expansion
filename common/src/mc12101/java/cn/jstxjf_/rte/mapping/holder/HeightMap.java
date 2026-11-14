package cn.jstxjf_.rte.mapping.holder;

public final class HeightMap extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.levelgen.Heightmap> {

    public HeightMap(net.minecraft.world.level.levelgen.Heightmap p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.HeightMap cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new HeightMap((net.minecraft.world.level.levelgen.Heightmap) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.levelgen.Heightmap;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean trackUpdate(int p0, int p1, int p2, cn.jstxjf_.rte.mapping.holder.BlockState p3) {
        return ((net.minecraft.world.level.levelgen.Heightmap) this.data).update(p0, p1, p2, ((net.minecraft.world.level.block.state.BlockState) p3.data));
    }

    public HeightMap(cn.jstxjf_.rte.mapping.holder.Chunk p0, cn.jstxjf_.rte.mapping.holder.HeightMapType p1) {
        super(new net.minecraft.world.level.levelgen.Heightmap(((net.minecraft.world.level.chunk.ChunkAccess) p0.data), p1.data));
    }

    public long[] asLongArray() {
        return ((net.minecraft.world.level.levelgen.Heightmap) this.data).getRawData();
    }
}
