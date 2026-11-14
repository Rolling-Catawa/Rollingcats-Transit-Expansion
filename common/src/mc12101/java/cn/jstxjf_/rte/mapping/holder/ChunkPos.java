package cn.jstxjf_.rte.mapping.holder;

public final class ChunkPos extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.ChunkPos> {

    public ChunkPos(net.minecraft.world.level.ChunkPos p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ChunkPos cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ChunkPos((net.minecraft.world.level.ChunkPos) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.ChunkPos;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int getEndZ() {
        return ((net.minecraft.world.level.ChunkPos) this.data).getMaxBlockZ();
    }

    public int getEndX() {
        return ((net.minecraft.world.level.ChunkPos) this.data).getMaxBlockX();
    }

    public int getRegionRelativeX() {
        return ((net.minecraft.world.level.ChunkPos) this.data).getRegionLocalX();
    }

    public int getRegionRelativeZ() {
        return ((net.minecraft.world.level.ChunkPos) this.data).getRegionLocalZ();
    }

    public long toLong() {
        return ((net.minecraft.world.level.ChunkPos) this.data).toLong();
    }

    public static long toLong(int p0, int p1) {
        return net.minecraft.world.level.ChunkPos.asLong(p0, p1);
    }

    public int getRegionX() {
        return ((net.minecraft.world.level.ChunkPos) this.data).getRegionX();
    }

    public int getRegionZ() {
        return ((net.minecraft.world.level.ChunkPos) this.data).getRegionZ();
    }

    public int getStartX() {
        return ((net.minecraft.world.level.ChunkPos) this.data).getMinBlockX();
    }

    public int getStartZ() {
        return ((net.minecraft.world.level.ChunkPos) this.data).getMinBlockZ();
    }

    public ChunkPos(long p0) {
        super(new net.minecraft.world.level.ChunkPos(p0));
    }

    public ChunkPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        super(new net.minecraft.world.level.ChunkPos(((net.minecraft.core.BlockPos) p0.data)));
    }

    public ChunkPos(int p0, int p1) {
        super(new net.minecraft.world.level.ChunkPos(p0, p1));
    }

    public static int getPackedX(long p0) {
        return net.minecraft.world.level.ChunkPos.getX(p0);
    }

    public static int getPackedZ(long p0) {
        return net.minecraft.world.level.ChunkPos.getZ(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getStartPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.level.ChunkPos) this.data).getWorldPosition());
    }

    public int hashCode() {
        return ((net.minecraft.world.level.ChunkPos) this.data).hashCode();
    }

    public static long getMarkerMapped() {
        return net.minecraft.world.level.ChunkPos.INVALID_CHUNK_POS;
    }

    public int getXMapped() {
        return ((net.minecraft.world.level.ChunkPos) this.data).x;
    }

    public int getZMapped() {
        return ((net.minecraft.world.level.ChunkPos) this.data).z;
    }
}
