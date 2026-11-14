package cn.jstxjf_.rte.mapping.holder;

public abstract class MovingSoundInstanceAbstractMapping extends net.minecraft.client.resources.sounds.AbstractTickableSoundInstance {

    public float getVolume2() {
        return super.getVolume();
    }

    public final float getVolume() {
        return this.getVolume2();
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier getId2() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(super.getLocation());
    }

    public final net.minecraft.resources.ResourceLocation getLocation() {
        final var v0 = this.getId2();
        return (v0 == null ? null : ((net.minecraft.resources.ResourceLocation) v0.data));
    }

    public double getZ2() {
        return super.getZ();
    }

    public final double getZ() {
        return this.getZ2();
    }

    protected final void setDone2() {
        super.stop();
    }

    public double getX2() {
        return super.getX();
    }

    public final double getX() {
        return this.getX2();
    }

    public double getY2() {
        return super.getY();
    }

    public final double getY() {
        return this.getY2();
    }

    public float getPitch2() {
        return super.getPitch();
    }

    public final float getPitch() {
        return this.getPitch2();
    }

    public int getRepeatDelay2() {
        return super.getDelay();
    }

    public final int getDelay() {
        return this.getRepeatDelay2();
    }

    public MovingSoundInstanceAbstractMapping(cn.jstxjf_.rte.mapping.holder.SoundEvent p0, cn.jstxjf_.rte.mapping.holder.SoundCategory p1, cn.jstxjf_.rte.mapping.holder.Random p2) {
        super(((net.minecraft.sounds.SoundEvent) p0.data), p1.data, ((net.minecraft.util.RandomSource) p2.data));
    }

    public boolean shouldAlwaysPlay2() {
        return super.canStartSilent();
    }

    public final boolean canStartSilent() {
        return this.shouldAlwaysPlay2();
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getCategory2() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(super.getSource());
    }

    public final net.minecraft.sounds.SoundSource getSource() {
        final var v0 = this.getCategory2();
        return (v0 == null ? null : v0.data);
    }

    public boolean canPlay2() {
        return super.canPlaySound();
    }

    public final boolean canPlaySound() {
        return this.canPlay2();
    }

    public abstract void tick2();

    public final void tick() {
        this.tick2();
    }
}
