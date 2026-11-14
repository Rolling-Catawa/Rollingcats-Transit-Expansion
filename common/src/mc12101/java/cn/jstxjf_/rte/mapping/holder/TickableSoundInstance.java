package cn.jstxjf_.rte.mapping.holder;

public interface TickableSoundInstance extends net.minecraft.client.resources.sounds.TickableSoundInstance {

    public int getRepeatDelay2();

    public default int getDelay() {
        return this.getRepeatDelay2();
    }

    public double getX2();

    public default double getX() {
        return this.getX2();
    }

    public float getVolume2();

    public default float getVolume() {
        return this.getVolume2();
    }

    public double getY2();

    public default double getY() {
        return this.getY2();
    }

    public float getPitch2();

    public default float getPitch() {
        return this.getPitch2();
    }

    public double getZ2();

    public default double getZ() {
        return this.getZ2();
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier getId2();

    public default net.minecraft.resources.ResourceLocation getLocation() {
        final var v0 = this.getId2();
        return (v0 == null ? null : ((net.minecraft.resources.ResourceLocation) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getCategory2();

    public default net.minecraft.sounds.SoundSource getSource() {
        final var v0 = this.getCategory2();
        return (v0 == null ? null : v0.data);
    }

    public void tick2();

    public default void tick() {
        this.tick2();
    }
}
