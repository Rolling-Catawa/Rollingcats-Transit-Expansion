package cn.jstxjf_.rte.mapping.holder;

public enum Mirror {
    NONE(net.minecraft.world.level.block.Mirror.NONE),
    LEFT_RIGHT(net.minecraft.world.level.block.Mirror.LEFT_RIGHT),
    FRONT_BACK(net.minecraft.world.level.block.Mirror.FRONT_BACK);

    public final net.minecraft.world.level.block.Mirror data;

    Mirror(net.minecraft.world.level.block.Mirror data) {
        this.data = data;
    }

    public static Mirror convert(net.minecraft.world.level.block.Mirror data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.Mirror p0) {
        return this == p0;
    }

    public cn.jstxjf_.rte.mapping.holder.Direction apply(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(this.data.mirror(p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockRotation getRotation(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(this.data.getRotation(p0.data));
    }

    public int mirror(int p0, int p1) {
        return this.data.mirror(p0, p1);
    }

    public static cn.jstxjf_.rte.mapping.holder.Mirror getNoneMapped() {
        return cn.jstxjf_.rte.mapping.holder.Mirror.convert(net.minecraft.world.level.block.Mirror.NONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Mirror getFrontBackMapped() {
        return cn.jstxjf_.rte.mapping.holder.Mirror.convert(net.minecraft.world.level.block.Mirror.FRONT_BACK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Mirror getLeftRightMapped() {
        return cn.jstxjf_.rte.mapping.holder.Mirror.convert(net.minecraft.world.level.block.Mirror.LEFT_RIGHT);
    }
}
