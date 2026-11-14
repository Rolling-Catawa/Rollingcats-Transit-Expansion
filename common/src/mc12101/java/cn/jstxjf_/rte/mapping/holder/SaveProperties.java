package cn.jstxjf_.rte.mapping.holder;

public final class SaveProperties extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.storage.WorldData> {

    public SaveProperties(net.minecraft.world.level.storage.WorldData p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.SaveProperties cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new SaveProperties((net.minecraft.world.level.storage.WorldData) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.storage.WorldData;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void setGameMode(cn.jstxjf_.rte.mapping.holder.GameMode p0) {
        ((net.minecraft.world.level.storage.WorldData) this.data).setGameType(p0.data);
    }

    public boolean isDifficultyLocked() {
        return ((net.minecraft.world.level.storage.WorldData) this.data).isDifficultyLocked();
    }

    public int getVersion() {
        return ((net.minecraft.world.level.storage.WorldData) this.data).getVersion();
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getCustomBossEvents() {
        final var v0 = ((net.minecraft.world.level.storage.WorldData) this.data).getCustomBossEvents();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.CompoundTag(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.Difficulty getDifficulty() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(((net.minecraft.world.level.storage.WorldData) this.data).getDifficulty());
    }

    public boolean isHardcore() {
        return ((net.minecraft.world.level.storage.WorldData) this.data).isHardcore();
    }

    public void setCustomBossEvents(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        ((net.minecraft.world.level.storage.WorldData) this.data).setCustomBossEvents((p0 == null ? null : ((net.minecraft.nbt.CompoundTag) p0.data)));
    }

    public java.lang.String getLevelName() {
        return ((net.minecraft.world.level.storage.WorldData) this.data).getLevelName();
    }

    public cn.jstxjf_.rte.mapping.holder.GameMode getGameMode() {
        return cn.jstxjf_.rte.mapping.holder.GameMode.convert(((net.minecraft.world.level.storage.WorldData) this.data).getGameType());
    }

    public void setDifficulty(cn.jstxjf_.rte.mapping.holder.Difficulty p0) {
        ((net.minecraft.world.level.storage.WorldData) this.data).setDifficulty(p0.data);
    }

    public com.mojang.serialization.Lifecycle getLifecycle() {
        return ((net.minecraft.world.level.storage.WorldData) this.data).worldGenSettingsLifecycle();
    }

    public java.lang.String getFormatName(int p0) {
        return ((net.minecraft.world.level.storage.WorldData) this.data).getStorageVersionName(p0);
    }

    public void setDifficultyLocked(boolean p0) {
        ((net.minecraft.world.level.storage.WorldData) this.data).setDifficultyLocked(p0);
    }

    public void addServerBrand(java.lang.String p0, boolean p1) {
        ((net.minecraft.world.level.storage.WorldData) this.data).setModdedInfo(p0, p1);
    }
}
