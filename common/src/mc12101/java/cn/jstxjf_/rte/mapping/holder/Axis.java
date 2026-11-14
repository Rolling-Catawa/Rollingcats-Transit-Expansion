package cn.jstxjf_.rte.mapping.holder;

public enum Axis {
    X(net.minecraft.core.Direction.Axis.X),
    Y(net.minecraft.core.Direction.Axis.Y),
    Z(net.minecraft.core.Direction.Axis.Z);

    public final net.minecraft.core.Direction.Axis data;

    Axis(net.minecraft.core.Direction.Axis data) {
        this.data = data;
    }

    public static Axis convert(net.minecraft.core.Direction.Axis data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.Axis p0) {
        return this == p0;
    }

    public java.lang.String asString() {
        return this.data.getSerializedName();
    }

    public static cn.jstxjf_.rte.mapping.holder.Axis fromName(java.lang.String p0) {
        final var v0 = net.minecraft.core.Direction.Axis.byName(p0);
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.Axis.convert(v0));
    }

    public double choose(double p0, double p1, double p2) {
        return this.data.choose(p0, p1, p2);
    }

    public int choose(int p0, int p1, int p2) {
        return this.data.choose(p0, p1, p2);
    }

    public boolean isHorizontal() {
        return this.data.isHorizontal();
    }

    public java.lang.String getName() {
        return this.data.getName();
    }

    public static cn.jstxjf_.rte.mapping.holder.Axis pickRandomAxis(cn.jstxjf_.rte.mapping.holder.Random p0) {
        return cn.jstxjf_.rte.mapping.holder.Axis.convert(net.minecraft.core.Direction.Axis.getRandom(((net.minecraft.util.RandomSource) p0.data)));
    }

    public boolean isVertical() {
        return this.data.isVertical();
    }

    public boolean test(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return this.data.test((p0 == null ? null : p0.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Axis getZMapped() {
        return cn.jstxjf_.rte.mapping.holder.Axis.convert(net.minecraft.core.Direction.Axis.Z);
    }

    public static cn.jstxjf_.rte.mapping.holder.Axis getYMapped() {
        return cn.jstxjf_.rte.mapping.holder.Axis.convert(net.minecraft.core.Direction.Axis.Y);
    }

    public static cn.jstxjf_.rte.mapping.holder.Axis getXMapped() {
        return cn.jstxjf_.rte.mapping.holder.Axis.convert(net.minecraft.core.Direction.Axis.X);
    }
}
