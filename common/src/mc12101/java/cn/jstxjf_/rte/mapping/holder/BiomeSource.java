package cn.jstxjf_.rte.mapping.holder;

public final class BiomeSource extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.biome.BiomeSource> {

    public BiomeSource(net.minecraft.world.level.biome.BiomeSource p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BiomeSource cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BiomeSource((net.minecraft.world.level.biome.BiomeSource) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.biome.BiomeSource;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }
}
