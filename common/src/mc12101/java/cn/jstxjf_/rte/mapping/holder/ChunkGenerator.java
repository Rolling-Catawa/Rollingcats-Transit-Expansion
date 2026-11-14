package cn.jstxjf_.rte.mapping.holder;

public final class ChunkGenerator extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.chunk.ChunkGenerator> {

    public ChunkGenerator(net.minecraft.world.level.chunk.ChunkGenerator p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ChunkGenerator cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ChunkGenerator((net.minecraft.world.level.chunk.ChunkGenerator) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.chunk.ChunkGenerator;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.BiomeSource getBiomeSource() {
        return new cn.jstxjf_.rte.mapping.holder.BiomeSource(((net.minecraft.world.level.chunk.ChunkGenerator) this.data).getBiomeSource());
    }
}
