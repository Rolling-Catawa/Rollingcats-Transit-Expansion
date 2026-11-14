package cn.jstxjf_.rte.mapping.holder;

public final class DimensionType extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.dimension.DimensionType> {

    public DimensionType(net.minecraft.world.level.dimension.DimensionType p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.DimensionType cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new DimensionType((net.minecraft.world.level.dimension.DimensionType) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.dimension.DimensionType;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean hasFixedTime() {
        return ((net.minecraft.world.level.dimension.DimensionType) this.data).hasFixedTime();
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier effects() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(((net.minecraft.world.level.dimension.DimensionType) this.data).effectsLocation());
    }

    public boolean hasSkyLight() {
        return ((net.minecraft.world.level.dimension.DimensionType) this.data).hasSkyLight();
    }

    public double coordinateScale() {
        return ((net.minecraft.world.level.dimension.DimensionType) this.data).coordinateScale();
    }

    public static double getCoordinateScaleFactor(cn.jstxjf_.rte.mapping.holder.DimensionType p0, cn.jstxjf_.rte.mapping.holder.DimensionType p1) {
        return net.minecraft.world.level.dimension.DimensionType.getTeleportationScale(((net.minecraft.world.level.dimension.DimensionType) p0.data), ((net.minecraft.world.level.dimension.DimensionType) p1.data));
    }

    public float getSkyAngle(long p0) {
        return ((net.minecraft.world.level.dimension.DimensionType) this.data).timeOfDay(p0);
    }

    public int getMoonPhase(long p0) {
        return ((net.minecraft.world.level.dimension.DimensionType) this.data).moonPhase(p0);
    }

    public DimensionType(java.util.OptionalLong p0, boolean p1, boolean p2, boolean p3, boolean p4, double p5, boolean p6, boolean p7, int p8, int p9, int p10, net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> p11, cn.jstxjf_.rte.mapping.holder.Identifier p12, float p13, net.minecraft.world.level.dimension.DimensionType.MonsterSettings p14) {
        super(new net.minecraft.world.level.dimension.DimensionType(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, ((net.minecraft.resources.ResourceLocation) p12.data), p13, p14));
    }

    public boolean hasCeiling() {
        return ((net.minecraft.world.level.dimension.DimensionType) this.data).hasCeiling();
    }

    public boolean hasRaids() {
        return ((net.minecraft.world.level.dimension.DimensionType) this.data).hasRaids();
    }

    public static float[] getMoonSizesMapped() {
        return net.minecraft.world.level.dimension.DimensionType.MOON_BRIGHTNESS_PER_PHASE;
    }
}
