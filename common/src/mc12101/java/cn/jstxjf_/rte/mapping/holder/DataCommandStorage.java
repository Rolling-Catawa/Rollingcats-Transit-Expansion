package cn.jstxjf_.rte.mapping.holder;

public final class DataCommandStorage extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.storage.CommandStorage> {

    public DataCommandStorage(net.minecraft.world.level.storage.CommandStorage p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.DataCommandStorage cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new DataCommandStorage((net.minecraft.world.level.storage.CommandStorage) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.storage.CommandStorage;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag get(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.world.level.storage.CommandStorage) this.data).get(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public void set(cn.jstxjf_.rte.mapping.holder.Identifier p0, cn.jstxjf_.rte.mapping.holder.CompoundTag p1) {
        ((net.minecraft.world.level.storage.CommandStorage) this.data).set(((net.minecraft.resources.ResourceLocation) p0.data), ((net.minecraft.nbt.CompoundTag) p1.data));
    }

    public DataCommandStorage(net.minecraft.world.level.storage.DimensionDataStorage p0) {
        super(new net.minecraft.world.level.storage.CommandStorage(p0));
    }
}
