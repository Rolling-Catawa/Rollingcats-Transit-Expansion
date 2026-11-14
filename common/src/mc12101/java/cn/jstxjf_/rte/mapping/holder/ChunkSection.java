package cn.jstxjf_.rte.mapping.holder;

public final class ChunkSection extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.chunk.LevelChunkSection> {

    public ChunkSection(net.minecraft.world.level.chunk.LevelChunkSection p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ChunkSection cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ChunkSection((net.minecraft.world.level.chunk.LevelChunkSection) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.chunk.LevelChunkSection;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState setBlockState(int p0, int p1, int p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, boolean p4) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.chunk.LevelChunkSection) this.data).setBlockState(p0, p1, p2, ((net.minecraft.world.level.block.state.BlockState) p3.data), p4));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState setBlockState(int p0, int p1, int p2, cn.jstxjf_.rte.mapping.holder.BlockState p3) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.chunk.LevelChunkSection) this.data).setBlockState(p0, p1, p2, ((net.minecraft.world.level.block.state.BlockState) p3.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState(int p0, int p1, int p2) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.chunk.LevelChunkSection) this.data).getFluidState(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState(int p0, int p1, int p2) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.chunk.LevelChunkSection) this.data).getBlockState(p0, p1, p2));
    }

    public ChunkSection(net.minecraft.world.level.chunk.PalettedContainer<net.minecraft.world.level.block.state.BlockState> p0, net.minecraft.world.level.chunk.PalettedContainerRO<net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome>> p1) {
        super(new net.minecraft.world.level.chunk.LevelChunkSection(p0, p1));
    }

    public ChunkSection(net.minecraft.core.Registry<net.minecraft.world.level.biome.Biome> p0) {
        super(new net.minecraft.world.level.chunk.LevelChunkSection(p0));
    }
}
