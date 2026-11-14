package cn.jstxjf_.rte.mapping.holder;

public final class HitResult extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.phys.HitResult> {

    public HitResult(net.minecraft.world.phys.HitResult p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.HitResult cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new HitResult((net.minecraft.world.phys.HitResult) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.phys.HitResult;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.phys.HitResult) this.data).distanceTo(((net.minecraft.world.entity.Entity) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.HitResultType getType() {
        return cn.jstxjf_.rte.mapping.holder.HitResultType.convert(((net.minecraft.world.phys.HitResult) this.data).getType());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.phys.HitResult) this.data).getLocation());
    }
}
