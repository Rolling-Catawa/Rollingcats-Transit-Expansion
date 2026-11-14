package cn.jstxjf_.rte.mapping.holder;

public final class BlockPos extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.core.BlockPos> {

    public BlockPos(net.minecraft.core.BlockPos p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockPos cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BlockPos((net.minecraft.core.BlockPos) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.core.BlockPos;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isWithinDistance(cn.jstxjf_.rte.mapping.holder.Vector3i p0, double p1) {
        return ((net.minecraft.core.BlockPos) this.data).closerThan(((net.minecraft.core.Vec3i) p0.data), p1);
    }

    public boolean isWithinDistance(cn.jstxjf_.rte.mapping.holder.Position p0, double p1) {
        return ((net.minecraft.core.BlockPos) this.data).closerToCenterThan(((net.minecraft.core.Position) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos north(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).north(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos north() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).north());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos south() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).south());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos south(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).south(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos toImmutable() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).immutable());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos subtract(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).subtract(((net.minecraft.core.Vec3i) p0.data)));
    }

    public int getComponentAlongAxis(cn.jstxjf_.rte.mapping.holder.Axis p0) {
        return ((net.minecraft.core.BlockPos) this.data).get(p0.data);
    }

    public static long asLong(int p0, int p1, int p2) {
        return net.minecraft.core.BlockPos.asLong(p0, p1, p2);
    }

    public long asLong() {
        return ((net.minecraft.core.BlockPos) this.data).asLong();
    }

    public BlockPos(int p0, int p1, int p2) {
        super(new net.minecraft.core.BlockPos(p0, p1, p2));
    }

    public BlockPos(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        super(new net.minecraft.core.BlockPos(((net.minecraft.core.Vec3i) p0.data)));
    }

    public double getSquaredDistance(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return ((net.minecraft.core.BlockPos) this.data).distSqr(((net.minecraft.core.Vec3i) p0.data));
    }

    public int getManhattanDistance(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return ((net.minecraft.core.BlockPos) this.data).distManhattan(((net.minecraft.core.Vec3i) p0.data));
    }

    public int compareTo(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return ((net.minecraft.core.BlockPos) this.data).compareTo(((net.minecraft.core.Vec3i) p0.data));
    }

    public static long removeChunkSectionLocalY(long p0) {
        return net.minecraft.core.BlockPos.getFlatIndex(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos east(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).east(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos east() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).east());
    }

    public java.lang.String toShortString() {
        return ((net.minecraft.core.BlockPos) this.data).toShortString();
    }

    public int hashCode() {
        return ((net.minecraft.core.BlockPos) this.data).hashCode();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos add(int p0, int p1, int p2) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).offset(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos add(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).offset(((net.minecraft.core.Vec3i) p0.data)));
    }

    public static long add(long p0, int p1, int p2, int p3) {
        return net.minecraft.core.BlockPos.offset(p0, p1, p2, p3);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos offset(cn.jstxjf_.rte.mapping.holder.Direction p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).relative(p0.data, p1));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos offset(cn.jstxjf_.rte.mapping.holder.Axis p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).relative(p0.data, p1));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos offset(cn.jstxjf_.rte.mapping.holder.Direction p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).relative(p0.data));
    }

    public static long offset(long p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return net.minecraft.core.BlockPos.offset(p0, p1.data);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockPos fromLong(long p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(net.minecraft.core.BlockPos.of(p0));
    }

    public int getY() {
        return ((net.minecraft.core.BlockPos) this.data).getY();
    }

    public int getX() {
        return ((net.minecraft.core.BlockPos) this.data).getX();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos down(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).below(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos down() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).below());
    }

    public int getZ() {
        return ((net.minecraft.core.BlockPos) this.data).getZ();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos rotate(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).rotate(p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos crossProduct(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).cross(((net.minecraft.core.Vec3i) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos up(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).above(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos up() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).above());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos west() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).west());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos west(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.core.BlockPos) this.data).west(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3i getZeroMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3i(net.minecraft.core.BlockPos.ZERO);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockPos getOriginMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(net.minecraft.core.BlockPos.ZERO);
    }
}
