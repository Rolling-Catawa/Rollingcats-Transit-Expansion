package cn.jstxjf_.rte.mapping.holder;

public final class CompoundTag extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.nbt.CompoundTag> {

    public CompoundTag(net.minecraft.nbt.CompoundTag p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.CompoundTag cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new CompoundTag((net.minecraft.nbt.CompoundTag) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.nbt.CompoundTag;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int getInt(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getInt(p0);
    }

    public void putFloat(java.lang.String p0, float p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putFloat(p0, p1);
    }

    public boolean contains(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).contains(p0);
    }

    public boolean contains(java.lang.String p0, int p1) {
        return ((net.minecraft.nbt.CompoundTag) this.data).contains(p0, p1);
    }

    public byte getType(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getTagType(p0);
    }

    public java.util.UUID getUuid(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getUUID(p0);
    }

    public void putInt(java.lang.String p0, int p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putInt(p0, p1);
    }

    public boolean containsUuid(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).hasUUID(p0);
    }

    public void putBoolean(java.lang.String p0, boolean p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putBoolean(p0, p1);
    }

    public void write(java.io.DataOutput p0) throws java.io.IOException {
        ((net.minecraft.nbt.CompoundTag) this.data).write(p0);
    }

    public void putLongArray(java.lang.String p0, java.util.List<java.lang.Long> p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putLongArray(p0, p1);
    }

    public void putLongArray(java.lang.String p0, long[] p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putLongArray(p0, p1);
    }

    public void putUuid(java.lang.String p0, java.util.UUID p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putUUID(p0, p1);
    }

    public CompoundTag() {
        super(new net.minecraft.nbt.CompoundTag());
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getCompound(java.lang.String p0) {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.nbt.CompoundTag) this.data).getCompound(p0));
    }

    public byte getByte(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getByte(p0);
    }

    public long getLong(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getLong(p0);
    }

    public int getSize() {
        return ((net.minecraft.nbt.CompoundTag) this.data).size();
    }

    public byte[] getByteArray(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getByteArray(p0);
    }

    public int[] getIntArray(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getIntArray(p0);
    }

    public short getShort(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getShort(p0);
    }

    public void putString(java.lang.String p0, java.lang.String p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putString(p0, p1);
    }

    public boolean isEmpty() {
        return ((net.minecraft.nbt.CompoundTag) this.data).isEmpty();
    }

    public java.lang.String getString(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getString(p0);
    }

    public java.util.Set<java.lang.String> getKeys() {
        return ((net.minecraft.nbt.CompoundTag) this.data).getAllKeys();
    }

    public void putByteArray(java.lang.String p0, byte[] p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putByteArray(p0, p1);
    }

    public long[] getLongArray(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getLongArray(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag copyFrom(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.nbt.CompoundTag) this.data).merge(((net.minecraft.nbt.CompoundTag) p0.data)));
    }

    public java.lang.String asString() {
        return ((net.minecraft.nbt.CompoundTag) this.data).getAsString();
    }

    public void putByte(java.lang.String p0, byte p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putByte(p0, p1);
    }

    public int hashCode() {
        return ((net.minecraft.nbt.CompoundTag) this.data).hashCode();
    }

    public void putIntArray(java.lang.String p0, int[] p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putIntArray(p0, p1);
    }

    public void putIntArray(java.lang.String p0, java.util.List<java.lang.Integer> p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putIntArray(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag copy() {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.nbt.CompoundTag) this.data).copy());
    }

    public boolean getBoolean(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getBoolean(p0);
    }

    public void putShort(java.lang.String p0, short p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putShort(p0, p1);
    }

    public double getDouble(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getDouble(p0);
    }

    public void putDouble(java.lang.String p0, double p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putDouble(p0, p1);
    }

    public float getFloat(java.lang.String p0) {
        return ((net.minecraft.nbt.CompoundTag) this.data).getFloat(p0);
    }

    public void putLong(java.lang.String p0, long p1) {
        ((net.minecraft.nbt.CompoundTag) this.data).putLong(p0, p1);
    }

    public void remove(java.lang.String p0) {
        ((net.minecraft.nbt.CompoundTag) this.data).remove(p0);
    }
}
