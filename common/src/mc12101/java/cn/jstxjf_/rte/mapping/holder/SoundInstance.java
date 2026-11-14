package cn.jstxjf_.rte.mapping.holder;

public final class SoundInstance extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.resources.sounds.SoundInstance> {

    public SoundInstance(net.minecraft.client.resources.sounds.SoundInstance p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundInstance cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new SoundInstance((net.minecraft.client.resources.sounds.SoundInstance) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.resources.sounds.SoundInstance;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int getRepeatDelay() {
        return ((net.minecraft.client.resources.sounds.SoundInstance) this.data).getDelay();
    }

    public double getX() {
        return ((net.minecraft.client.resources.sounds.SoundInstance) this.data).getX();
    }

    public float getVolume() {
        return ((net.minecraft.client.resources.sounds.SoundInstance) this.data).getVolume();
    }

    public double getY() {
        return ((net.minecraft.client.resources.sounds.SoundInstance) this.data).getY();
    }

    public float getPitch() {
        return ((net.minecraft.client.resources.sounds.SoundInstance) this.data).getPitch();
    }

    public double getZ() {
        return ((net.minecraft.client.resources.sounds.SoundInstance) this.data).getZ();
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier getId() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(((net.minecraft.client.resources.sounds.SoundInstance) this.data).getLocation());
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getCategory() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(((net.minecraft.client.resources.sounds.SoundInstance) this.data).getSource());
    }
}
