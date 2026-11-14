package cn.jstxjf_.rte.mapping.holder;

public enum GameMode {
    SURVIVAL(net.minecraft.world.level.GameType.SURVIVAL),
    CREATIVE(net.minecraft.world.level.GameType.CREATIVE),
    ADVENTURE(net.minecraft.world.level.GameType.ADVENTURE),
    SPECTATOR(net.minecraft.world.level.GameType.SPECTATOR);

    public final net.minecraft.world.level.GameType data;

    GameMode(net.minecraft.world.level.GameType data) {
        this.data = data;
    }

    public static GameMode convert(net.minecraft.world.level.GameType data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.GameMode p0) {
        return this == p0;
    }

    public java.lang.String getName() {
        return this.data.getName();
    }

    public boolean isCreative() {
        return this.data.isCreative();
    }

    public int getId() {
        return this.data.getId();
    }

    public static cn.jstxjf_.rte.mapping.holder.GameMode byName(java.lang.String p0) {
        return cn.jstxjf_.rte.mapping.holder.GameMode.convert(net.minecraft.world.level.GameType.byName(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.GameMode byName(java.lang.String p0, cn.jstxjf_.rte.mapping.holder.GameMode p1) {
        final var v0 = net.minecraft.world.level.GameType.byName(p0, (p1 == null ? null : p1.data));
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.GameMode.convert(v0));
    }

    public static cn.jstxjf_.rte.mapping.holder.GameMode byId(int p0) {
        return cn.jstxjf_.rte.mapping.holder.GameMode.convert(net.minecraft.world.level.GameType.byId(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.GameMode getDefaultMapped() {
        return cn.jstxjf_.rte.mapping.holder.GameMode.convert(net.minecraft.world.level.GameType.DEFAULT_MODE);
    }

    public static cn.jstxjf_.rte.mapping.holder.GameMode getSurvivalMapped() {
        return cn.jstxjf_.rte.mapping.holder.GameMode.convert(net.minecraft.world.level.GameType.SURVIVAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.GameMode getSpectatorMapped() {
        return cn.jstxjf_.rte.mapping.holder.GameMode.convert(net.minecraft.world.level.GameType.SPECTATOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.GameMode getCreativeMapped() {
        return cn.jstxjf_.rte.mapping.holder.GameMode.convert(net.minecraft.world.level.GameType.CREATIVE);
    }

    public static cn.jstxjf_.rte.mapping.holder.GameMode getAdventureMapped() {
        return cn.jstxjf_.rte.mapping.holder.GameMode.convert(net.minecraft.world.level.GameType.ADVENTURE);
    }
}
