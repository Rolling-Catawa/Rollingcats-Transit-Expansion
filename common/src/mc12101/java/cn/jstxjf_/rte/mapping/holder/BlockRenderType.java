package cn.jstxjf_.rte.mapping.holder;

public enum BlockRenderType {
    INVISIBLE(net.minecraft.world.level.block.RenderShape.INVISIBLE),
    ENTITYBLOCK_ANIMATED(net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED),
    MODEL(net.minecraft.world.level.block.RenderShape.MODEL);

    public final net.minecraft.world.level.block.RenderShape data;

    BlockRenderType(net.minecraft.world.level.block.RenderShape data) {
        this.data = data;
    }

    public static BlockRenderType convert(net.minecraft.world.level.block.RenderShape data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.BlockRenderType p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRenderType getModelMapped() {
        return cn.jstxjf_.rte.mapping.holder.BlockRenderType.convert(net.minecraft.world.level.block.RenderShape.MODEL);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRenderType getEntityblockAnimatedMapped() {
        return cn.jstxjf_.rte.mapping.holder.BlockRenderType.convert(net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockRenderType getInvisibleMapped() {
        return cn.jstxjf_.rte.mapping.holder.BlockRenderType.convert(net.minecraft.world.level.block.RenderShape.INVISIBLE);
    }
}
