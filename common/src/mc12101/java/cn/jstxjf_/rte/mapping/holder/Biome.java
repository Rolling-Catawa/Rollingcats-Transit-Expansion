package cn.jstxjf_.rte.mapping.holder;

public final class Biome extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.biome.Biome> {

    public Biome(net.minecraft.world.level.biome.Biome p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Biome cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Biome((net.minecraft.world.level.biome.Biome) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.biome.Biome;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int getFogColor() {
        return ((net.minecraft.world.level.biome.Biome) this.data).getFogColor();
    }

    public boolean hasPrecipitation() {
        return ((net.minecraft.world.level.biome.Biome) this.data).hasPrecipitation();
    }

    public int getSkyColor() {
        return ((net.minecraft.world.level.biome.Biome) this.data).getSkyColor();
    }

    public int getGrassColorAt(double p0, double p1) {
        return ((net.minecraft.world.level.biome.Biome) this.data).getGrassColor(p0, p1);
    }

    public int getFoliageColor() {
        return ((net.minecraft.world.level.biome.Biome) this.data).getFoliageColor();
    }
}
