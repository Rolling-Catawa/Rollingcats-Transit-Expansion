package cn.jstxjf_.rte.mapping.holder;

public enum SoundCategory {
    MASTER(net.minecraft.sounds.SoundSource.MASTER),
    MUSIC(net.minecraft.sounds.SoundSource.MUSIC),
    RECORDS(net.minecraft.sounds.SoundSource.RECORDS),
    WEATHER(net.minecraft.sounds.SoundSource.WEATHER),
    BLOCKS(net.minecraft.sounds.SoundSource.BLOCKS),
    HOSTILE(net.minecraft.sounds.SoundSource.HOSTILE),
    NEUTRAL(net.minecraft.sounds.SoundSource.NEUTRAL),
    PLAYERS(net.minecraft.sounds.SoundSource.PLAYERS),
    AMBIENT(net.minecraft.sounds.SoundSource.AMBIENT),
    VOICE(net.minecraft.sounds.SoundSource.VOICE);

    public final net.minecraft.sounds.SoundSource data;

    SoundCategory(net.minecraft.sounds.SoundSource data) {
        this.data = data;
    }

    public static SoundCategory convert(net.minecraft.sounds.SoundSource data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.SoundCategory p0) {
        return this == p0;
    }

    public java.lang.String getName() {
        return this.data.getName();
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getNeutralMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.NEUTRAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getBlocksMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.BLOCKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getAmbientMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.AMBIENT);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getRecordsMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.RECORDS);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getMusicMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.MUSIC);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getMasterMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.MASTER);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getHostileMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.HOSTILE);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getPlayersMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.PLAYERS);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getVoiceMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.VOICE);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundCategory getWeatherMapped() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(net.minecraft.sounds.SoundSource.WEATHER);
    }
}
