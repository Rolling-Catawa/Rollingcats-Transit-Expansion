package cn.jstxjf_.rte.mapping.holder;

public enum BlockRotation {
    NONE(net.minecraft.world.level.block.Rotation.NONE),
    CLOCKWISE_90(net.minecraft.world.level.block.Rotation.CLOCKWISE_90),
    CLOCKWISE_180(net.minecraft.world.level.block.Rotation.CLOCKWISE_180),
    COUNTERCLOCKWISE_90(net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90);

    public final net.minecraft.world.level.block.Rotation data;

    BlockRotation(net.minecraft.world.level.block.Rotation data) {
        this.data = data;
    }

    public static BlockRotation convert(net.minecraft.world.level.block.Rotation data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return this == p0;
    }

    public int rotate(int p0, int p1) {
        return this.data.rotate(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockRotation rotate(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(this.data.getRotated(p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Direction rotate(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(this.data.rotate(p0.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRotation random(cn.jstxjf_.rte.mapping.holder.Random p0) {
        return cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(net.minecraft.world.level.block.Rotation.getRandom(((net.minecraft.util.RandomSource) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRotation getClockwise90Mapped() {
        return cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(net.minecraft.world.level.block.Rotation.CLOCKWISE_90);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRotation getCounterclockwise90Mapped() {
        return cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRotation getNoneMapped() {
        return cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(net.minecraft.world.level.block.Rotation.NONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRotation getClockwise180Mapped() {
        return cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(net.minecraft.world.level.block.Rotation.CLOCKWISE_180);
    }
}
