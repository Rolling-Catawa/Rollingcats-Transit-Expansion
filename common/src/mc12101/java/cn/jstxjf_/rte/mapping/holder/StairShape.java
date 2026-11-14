package cn.jstxjf_.rte.mapping.holder;

public enum StairShape {
    STRAIGHT(net.minecraft.world.level.block.state.properties.StairsShape.STRAIGHT),
    INNER_LEFT(net.minecraft.world.level.block.state.properties.StairsShape.INNER_LEFT),
    INNER_RIGHT(net.minecraft.world.level.block.state.properties.StairsShape.INNER_RIGHT),
    OUTER_LEFT(net.minecraft.world.level.block.state.properties.StairsShape.OUTER_LEFT),
    OUTER_RIGHT(net.minecraft.world.level.block.state.properties.StairsShape.OUTER_RIGHT);

    public final net.minecraft.world.level.block.state.properties.StairsShape data;

    StairShape(net.minecraft.world.level.block.state.properties.StairsShape data) {
        this.data = data;
    }

    public static StairShape convert(net.minecraft.world.level.block.state.properties.StairsShape data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.StairShape p0) {
        return this == p0;
    }

    public java.lang.String asString() {
        return this.data.getSerializedName();
    }

    public static cn.jstxjf_.rte.mapping.holder.StairShape getStraightMapped() {
        return cn.jstxjf_.rte.mapping.holder.StairShape.convert(net.minecraft.world.level.block.state.properties.StairsShape.STRAIGHT);
    }

    public static cn.jstxjf_.rte.mapping.holder.StairShape getInnerLeftMapped() {
        return cn.jstxjf_.rte.mapping.holder.StairShape.convert(net.minecraft.world.level.block.state.properties.StairsShape.INNER_LEFT);
    }

    public static cn.jstxjf_.rte.mapping.holder.StairShape getInnerRightMapped() {
        return cn.jstxjf_.rte.mapping.holder.StairShape.convert(net.minecraft.world.level.block.state.properties.StairsShape.INNER_RIGHT);
    }

    public static cn.jstxjf_.rte.mapping.holder.StairShape getOuterLeftMapped() {
        return cn.jstxjf_.rte.mapping.holder.StairShape.convert(net.minecraft.world.level.block.state.properties.StairsShape.OUTER_LEFT);
    }

    public static cn.jstxjf_.rte.mapping.holder.StairShape getOuterRightMapped() {
        return cn.jstxjf_.rte.mapping.holder.StairShape.convert(net.minecraft.world.level.block.state.properties.StairsShape.OUTER_RIGHT);
    }
}
