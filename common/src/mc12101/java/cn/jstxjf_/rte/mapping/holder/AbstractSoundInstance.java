package cn.jstxjf_.rte.mapping.holder;

public final class AbstractSoundInstance extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.resources.sounds.AbstractSoundInstance> {

    public AbstractSoundInstance(net.minecraft.client.resources.sounds.AbstractSoundInstance p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.AbstractSoundInstance cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new AbstractSoundInstance((net.minecraft.client.resources.sounds.AbstractSoundInstance) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.resources.sounds.AbstractSoundInstance;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public float getVolume() {
        return ((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).getVolume();
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier getId() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).getLocation());
    }

    public double getZ() {
        return ((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).getZ();
    }

    public double getX() {
        return ((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).getX();
    }

    public double getY() {
        return ((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).getY();
    }

    public float getPitch() {
        return ((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).getPitch();
    }

    public int getRepeatDelay() {
        return ((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).getDelay();
    }

    public boolean shouldAlwaysPlay() {
        return ((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).canStartSilent();
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getCategory() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).getSource());
    }

    public boolean canPlay() {
        return ((net.minecraft.client.resources.sounds.AbstractSoundInstance) this.data).canPlaySound();
    }
}
