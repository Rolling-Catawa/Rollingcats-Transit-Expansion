package cn.jstxjf_.rte.scripting.util.client;

import cn.jstxjf_.rte.gfx.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class TickableSound extends AbstractTickableSoundInstance {

    public TickableSound(ResourceLocation sound) {
        super(SoundEvent.createVariableRangeEvent(sound), SoundSource.BLOCKS, net.minecraft.util.RandomSource.create());
    }

    public TickableSound(ResourceLocation sound, SoundSource source) {
        super(SoundEvent.createVariableRangeEvent(sound), source, net.minecraft.util.RandomSource.create());
    }

    public void setData(float volume, float pitch, Vector3f pos) {
        this.pitch = pitch;
        this.volume = volume;
        x = pos.x();
        y = pos.y();
        z = pos.z();
    }

    public void setLooping(boolean looping) { this.looping = looping; }
    public void setDelay(int delay) { this.delay = delay; }

    public void setAttenuation(boolean attenuation) {
        this.attenuation = attenuation ? SoundInstance.Attenuation.LINEAR : SoundInstance.Attenuation.NONE;
    }

    public void setRelative(boolean relative) { this.relative = relative; }

    public void play() { Minecraft.getInstance().getSoundManager().play(this); }

    public void quit() {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getSoundManager().stop(this));
    }

    public void pause() { stop(); }

    @Override
    public boolean canStartSilent() { return true; }

    @Override
    public boolean canPlaySound() { return true; }

    @Override
    public void tick() {}
}
