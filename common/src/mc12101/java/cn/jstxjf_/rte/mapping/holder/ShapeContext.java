package cn.jstxjf_.rte.mapping.holder;

public final class ShapeContext extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.phys.shapes.CollisionContext> {

    public ShapeContext(net.minecraft.world.phys.shapes.CollisionContext p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ShapeContext cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ShapeContext((net.minecraft.world.phys.shapes.CollisionContext) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.phys.shapes.CollisionContext;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isDescending() {
        return ((net.minecraft.world.phys.shapes.CollisionContext) this.data).isDescending();
    }

    public static cn.jstxjf_.rte.mapping.holder.ShapeContext of(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return new cn.jstxjf_.rte.mapping.holder.ShapeContext(net.minecraft.world.phys.shapes.CollisionContext.of(((net.minecraft.world.entity.Entity) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.ShapeContext absent() {
        return new cn.jstxjf_.rte.mapping.holder.ShapeContext(net.minecraft.world.phys.shapes.CollisionContext.empty());
    }

    public boolean isAbove(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, boolean p2) {
        return ((net.minecraft.world.phys.shapes.CollisionContext) this.data).isAbove(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2);
    }
}
