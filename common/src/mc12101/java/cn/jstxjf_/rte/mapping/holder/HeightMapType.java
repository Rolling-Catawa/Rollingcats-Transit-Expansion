package cn.jstxjf_.rte.mapping.holder;

public enum HeightMapType {
    WORLD_SURFACE_WG(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG),
    WORLD_SURFACE(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE),
    OCEAN_FLOOR_WG(net.minecraft.world.level.levelgen.Heightmap.Types.OCEAN_FLOOR_WG),
    OCEAN_FLOOR(net.minecraft.world.level.levelgen.Heightmap.Types.OCEAN_FLOOR),
    MOTION_BLOCKING(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING),
    MOTION_BLOCKING_NO_LEAVES(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);

    public final net.minecraft.world.level.levelgen.Heightmap.Types data;

    HeightMapType(net.minecraft.world.level.levelgen.Heightmap.Types data) {
        this.data = data;
    }

    public static HeightMapType convert(net.minecraft.world.level.levelgen.Heightmap.Types data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.HeightMapType p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.HeightMapType getMotionBlockingNoLeavesMapped() {
        return cn.jstxjf_.rte.mapping.holder.HeightMapType.convert(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }

    public static cn.jstxjf_.rte.mapping.holder.HeightMapType getOceanFloorMapped() {
        return cn.jstxjf_.rte.mapping.holder.HeightMapType.convert(net.minecraft.world.level.levelgen.Heightmap.Types.OCEAN_FLOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.HeightMapType getMotionBlockingMapped() {
        return cn.jstxjf_.rte.mapping.holder.HeightMapType.convert(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING);
    }

    public static cn.jstxjf_.rte.mapping.holder.HeightMapType getOceanFloorWgMapped() {
        return cn.jstxjf_.rte.mapping.holder.HeightMapType.convert(net.minecraft.world.level.levelgen.Heightmap.Types.OCEAN_FLOOR_WG);
    }

    public static cn.jstxjf_.rte.mapping.holder.HeightMapType getWorldSurfaceMapped() {
        return cn.jstxjf_.rte.mapping.holder.HeightMapType.convert(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE);
    }

    public static cn.jstxjf_.rte.mapping.holder.HeightMapType getWorldSurfaceWgMapped() {
        return cn.jstxjf_.rte.mapping.holder.HeightMapType.convert(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG);
    }
}
