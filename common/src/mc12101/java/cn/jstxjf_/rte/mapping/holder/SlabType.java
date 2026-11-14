package cn.jstxjf_.rte.mapping.holder;

public enum SlabType {
    TOP(net.minecraft.world.level.block.state.properties.SlabType.TOP),
    BOTTOM(net.minecraft.world.level.block.state.properties.SlabType.BOTTOM),
    DOUBLE(net.minecraft.world.level.block.state.properties.SlabType.DOUBLE);

    public final net.minecraft.world.level.block.state.properties.SlabType data;

    SlabType(net.minecraft.world.level.block.state.properties.SlabType data) {
        this.data = data;
    }

    public static SlabType convert(net.minecraft.world.level.block.state.properties.SlabType data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.SlabType p0) {
        return this == p0;
    }

    public java.lang.String asString() {
        return this.data.getSerializedName();
    }

    public static cn.jstxjf_.rte.mapping.holder.SlabType getTopMapped() {
        return cn.jstxjf_.rte.mapping.holder.SlabType.convert(net.minecraft.world.level.block.state.properties.SlabType.TOP);
    }

    public static cn.jstxjf_.rte.mapping.holder.SlabType getBottomMapped() {
        return cn.jstxjf_.rte.mapping.holder.SlabType.convert(net.minecraft.world.level.block.state.properties.SlabType.BOTTOM);
    }

    public static cn.jstxjf_.rte.mapping.holder.SlabType getDoubleMapped() {
        return cn.jstxjf_.rte.mapping.holder.SlabType.convert(net.minecraft.world.level.block.state.properties.SlabType.DOUBLE);
    }
}
