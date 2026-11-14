package cn.jstxjf_.rte.mapping.holder;

public interface BlockColorProvider extends net.minecraft.client.color.block.BlockColor {

    public int getColor2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockRenderView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, int p3);

    public default int getColor(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockAndTintGetter p1, net.minecraft.core.BlockPos p2, int p3) {
        return this.getColor2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), (p1 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockRenderView(p1)), (p2 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockPos(p2)), p3);
    }
}
