package cn.jstxjf_.rte.mapping.holder;

public final class BooleanBiFunction extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.phys.shapes.BooleanOp> {

    public BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BooleanBiFunction((net.minecraft.world.phys.shapes.BooleanOp) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.phys.shapes.BooleanOp;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean apply(boolean p0, boolean p1) {
        return ((net.minecraft.world.phys.shapes.BooleanOp) this.data).apply(p0, p1);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getNotFirstMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.NOT_FIRST);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getNotSecondMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.NOT_SECOND);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getOnlyFirstMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.ONLY_FIRST);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getFalseMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.FALSE);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getSecondMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.SECOND);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getCausesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.CAUSES);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getAndMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.AND);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getNotOrMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.NOT_OR);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getNotSameMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.NOT_SAME);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getOnlySecondMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.ONLY_SECOND);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getFirstMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.FIRST);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getCausedByMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.CAUSED_BY);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getNotAndMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.NOT_AND);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getTrueMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.TRUE);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getOrMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.OR);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanBiFunction getSameMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanBiFunction(net.minecraft.world.phys.shapes.BooleanOp.SAME);
    }
}
