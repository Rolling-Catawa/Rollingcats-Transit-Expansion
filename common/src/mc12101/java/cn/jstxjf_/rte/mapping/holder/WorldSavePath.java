package cn.jstxjf_.rte.mapping.holder;

public final class WorldSavePath extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.storage.LevelResource> {

    public WorldSavePath(net.minecraft.world.level.storage.LevelResource p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.WorldSavePath cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new WorldSavePath((net.minecraft.world.level.storage.LevelResource) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.storage.LevelResource;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public java.lang.String getRelativePath() {
        return ((net.minecraft.world.level.storage.LevelResource) this.data).getId();
    }

    public static cn.jstxjf_.rte.mapping.holder.WorldSavePath getRootMapped() {
        return new cn.jstxjf_.rte.mapping.holder.WorldSavePath(net.minecraft.world.level.storage.LevelResource.ROOT);
    }
}
