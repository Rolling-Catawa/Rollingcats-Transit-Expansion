package cn.jstxjf_.rte.mapping.holder;

public final class DimensionOptions extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.dimension.LevelStem> {

    public DimensionOptions(net.minecraft.world.level.dimension.LevelStem p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.DimensionOptions cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new DimensionOptions((net.minecraft.world.level.dimension.LevelStem) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.dimension.LevelStem;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.ChunkGenerator chunkGenerator() {
        return new cn.jstxjf_.rte.mapping.holder.ChunkGenerator(((net.minecraft.world.level.dimension.LevelStem) this.data).generator());
    }

    public DimensionOptions(net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType> p0, cn.jstxjf_.rte.mapping.holder.ChunkGenerator p1) {
        super(new net.minecraft.world.level.dimension.LevelStem(p0, ((net.minecraft.world.level.chunk.ChunkGenerator) p1.data)));
    }
}
