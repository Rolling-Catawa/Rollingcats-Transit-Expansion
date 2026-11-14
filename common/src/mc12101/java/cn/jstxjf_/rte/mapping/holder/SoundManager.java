package cn.jstxjf_.rte.mapping.holder;

public final class SoundManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.sounds.SoundManager> {

    public SoundManager(net.minecraft.client.sounds.SoundManager p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new SoundManager((net.minecraft.client.sounds.SoundManager) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.sounds.SoundManager;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void stop(cn.jstxjf_.rte.mapping.holder.SoundInstance p0) {
        ((net.minecraft.client.sounds.SoundManager) this.data).stop(((net.minecraft.client.resources.sounds.SoundInstance) p0.data));
    }

    public void stopSounds(cn.jstxjf_.rte.mapping.holder.Identifier p0, cn.jstxjf_.rte.mapping.holder.SoundCategory p1) {
        ((net.minecraft.client.sounds.SoundManager) this.data).stop((p0 == null ? null : ((net.minecraft.resources.ResourceLocation) p0.data)), (p1 == null ? null : p1.data));
    }

    public void tick(boolean p0) {
        ((net.minecraft.client.sounds.SoundManager) this.data).tick(p0);
    }

    public void updateSoundVolume(cn.jstxjf_.rte.mapping.holder.SoundCategory p0, float p1) {
        ((net.minecraft.client.sounds.SoundManager) this.data).updateSourceVolume(p0.data, p1);
    }

    public boolean isPlaying(cn.jstxjf_.rte.mapping.holder.SoundInstance p0) {
        return ((net.minecraft.client.sounds.SoundManager) this.data).isActive(((net.minecraft.client.resources.sounds.SoundInstance) p0.data));
    }

    public java.lang.String getName() {
        return ((net.minecraft.client.sounds.SoundManager) this.data).getName();
    }

    public void updateListenerPosition(cn.jstxjf_.rte.mapping.holder.Camera p0) {
        ((net.minecraft.client.sounds.SoundManager) this.data).updateSource(((net.minecraft.client.Camera) p0.data));
    }

    public java.lang.String getDebugString() {
        return ((net.minecraft.client.sounds.SoundManager) this.data).getDebugString();
    }

    public SoundManager(cn.jstxjf_.rte.mapping.holder.GameOptions p0) {
        super(new net.minecraft.client.sounds.SoundManager(((net.minecraft.client.Options) p0.data)));
    }

    public void play(cn.jstxjf_.rte.mapping.holder.SoundInstance p0, int p1) {
        ((net.minecraft.client.sounds.SoundManager) this.data).playDelayed(((net.minecraft.client.resources.sounds.SoundInstance) p0.data), p1);
    }

    public void play(cn.jstxjf_.rte.mapping.holder.SoundInstance p0) {
        ((net.minecraft.client.sounds.SoundManager) this.data).play(((net.minecraft.client.resources.sounds.SoundInstance) p0.data));
    }
}
