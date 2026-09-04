package cn.jstxjf_.rte.scripting.util.client;

import cn.jstxjf_.rte.gfx.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundHelper {

    public static void play(SoundInstance sound) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getSoundManager().play(sound));
    }

    public static void play(SoundInstance sound, int delay) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getSoundManager().playDelayed(sound, delay));
    }

    public static void play(SoundEvent sound, Vector3f pos, SoundSource source, float volume, float pitch) {
        Minecraft.getInstance().execute(() ->
            Minecraft.getInstance().level.playLocalSound(pos.x(), pos.y(), pos.z(), sound, source, volume, pitch, false));
    }

    public static void play(SoundEvent sound, Vector3f pos, float volume, float pitch) {
        Minecraft.getInstance().execute(() ->
            Minecraft.getInstance().level.playLocalSound(pos.x(), pos.y(), pos.z(), sound, SoundSource.BLOCKS, volume, pitch, false));
    }

    public static void stop() {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getSoundManager().stop());
    }

    public static void stop(SoundInstance sound) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getSoundManager().stop(sound));
    }

    public static void stop(ResourceLocation sound, SoundSource source) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getSoundManager().stop(sound, source));
    }

    public static void stop(ResourceLocation sound) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().getSoundManager().stop(sound, SoundSource.BLOCKS));
    }

    public static SoundSource getSoundSource(String str) {
        return switch (str.toUpperCase()) {
            case "MASTER" -> SoundSource.MASTER;
            case "MUSIC" -> SoundSource.MUSIC;
            case "RECORDS" -> SoundSource.RECORDS;
            case "WEATHER" -> SoundSource.WEATHER;
            case "HOSTILE" -> SoundSource.HOSTILE;
            case "NEUTRAL" -> SoundSource.NEUTRAL;
            case "PLAYERS" -> SoundSource.PLAYERS;
            case "AMBIENT" -> SoundSource.AMBIENT;
            case "VOICE" -> SoundSource.VOICE;
            default -> SoundSource.BLOCKS;
        };
    }
}
