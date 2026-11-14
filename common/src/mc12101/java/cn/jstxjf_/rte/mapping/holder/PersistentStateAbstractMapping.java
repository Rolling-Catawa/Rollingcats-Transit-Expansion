package cn.jstxjf_.rte.mapping.holder;

public abstract class PersistentStateAbstractMapping extends net.minecraft.world.level.saveddata.SavedData {

    public void setDirty2(boolean p0) {
        super.setDirty(p0);
    }

    public final void setDirty(boolean p0) {
        this.setDirty2(p0);
    }

    public PersistentStateAbstractMapping() {
        super();
    }

    public void markDirty2() {
        super.setDirty();
    }

    public final void setDirty() {
        this.markDirty2();
    }

    public abstract cn.jstxjf_.rte.mapping.holder.CompoundTag writeNbt2(cn.jstxjf_.rte.mapping.holder.CompoundTag p0);

    public final net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag p0) {
        final var v0 = this.writeNbt2(new cn.jstxjf_.rte.mapping.holder.CompoundTag(p0));
        return (v0 == null ? null : ((net.minecraft.nbt.CompoundTag) v0.data));
    }

    public boolean isDirty2() {
        return super.isDirty();
    }

    public final boolean isDirty() {
        return this.isDirty2();
    }
}
