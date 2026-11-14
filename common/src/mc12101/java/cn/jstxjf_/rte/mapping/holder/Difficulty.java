package cn.jstxjf_.rte.mapping.holder;

public enum Difficulty {
    PEACEFUL(net.minecraft.world.Difficulty.PEACEFUL),
    EASY(net.minecraft.world.Difficulty.EASY),
    NORMAL(net.minecraft.world.Difficulty.NORMAL),
    HARD(net.minecraft.world.Difficulty.HARD);

    public final net.minecraft.world.Difficulty data;

    Difficulty(net.minecraft.world.Difficulty data) {
        this.data = data;
    }

    public static Difficulty convert(net.minecraft.world.Difficulty data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.Difficulty p0) {
        return this == p0;
    }

    public int getId() {
        return this.data.getId();
    }

    public static cn.jstxjf_.rte.mapping.holder.Difficulty byName(java.lang.String p0) {
        final var v0 = net.minecraft.world.Difficulty.byName(p0);
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.Difficulty.convert(v0));
    }

    public static cn.jstxjf_.rte.mapping.holder.Difficulty byId(int p0) {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(net.minecraft.world.Difficulty.byId(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.Difficulty getNormalMapped() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(net.minecraft.world.Difficulty.NORMAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Difficulty getPeacefulMapped() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(net.minecraft.world.Difficulty.PEACEFUL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Difficulty getHardMapped() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(net.minecraft.world.Difficulty.HARD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Difficulty getEasyMapped() {
        return cn.jstxjf_.rte.mapping.holder.Difficulty.convert(net.minecraft.world.Difficulty.EASY);
    }
}
