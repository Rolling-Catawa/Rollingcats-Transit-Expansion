package cn.jstxjf_.rte.mapping.holder;

public final class PersistentState extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.saveddata.SavedData> {

    public PersistentState(net.minecraft.world.level.saveddata.SavedData p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.PersistentState cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new PersistentState((net.minecraft.world.level.saveddata.SavedData) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.saveddata.SavedData;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void setDirty(boolean p0) {
        ((net.minecraft.world.level.saveddata.SavedData) this.data).setDirty(p0);
    }

    public void markDirty() {
        ((net.minecraft.world.level.saveddata.SavedData) this.data).setDirty();
    }

    public boolean isDirty() {
        return ((net.minecraft.world.level.saveddata.SavedData) this.data).isDirty();
    }
}
