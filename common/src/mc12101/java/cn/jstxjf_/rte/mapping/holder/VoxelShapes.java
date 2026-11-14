package cn.jstxjf_.rte.mapping.holder;

public final class VoxelShapes extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.phys.shapes.Shapes> {

    public VoxelShapes(net.minecraft.world.phys.shapes.Shapes p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShapes cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new VoxelShapes((net.minecraft.world.phys.shapes.Shapes) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.phys.shapes.Shapes;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape union(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.or(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape fullCube() {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.block());
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape combineAndSimplify(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1, cn.jstxjf_.rte.mapping.holder.BooleanBiFunction p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.join(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data), ((net.minecraft.world.phys.shapes.BooleanOp) p2.data)));
    }

    public static boolean adjacentSidesCoverSquare(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return net.minecraft.world.phys.shapes.Shapes.mergedFaceOccludes(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data), p2.data);
    }

    public static boolean matchesAnywhere(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1, cn.jstxjf_.rte.mapping.holder.BooleanBiFunction p2) {
        return net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data), ((net.minecraft.world.phys.shapes.BooleanOp) p2.data));
    }

    public static boolean isSideCovered(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return net.minecraft.world.phys.shapes.Shapes.blockOccudes(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data), p2.data);
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape empty() {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.empty());
    }

    public static boolean unionCoversFullCube(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1) {
        return net.minecraft.world.phys.shapes.Shapes.faceShapeOccludes(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape extrudeFace(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.getFaceShape(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), p1.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape combine(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.VoxelShape p1, cn.jstxjf_.rte.mapping.holder.BooleanBiFunction p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.joinUnoptimized(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), ((net.minecraft.world.phys.shapes.VoxelShape) p1.data), ((net.minecraft.world.phys.shapes.BooleanOp) p2.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape cuboid(cn.jstxjf_.rte.mapping.holder.Box p0) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.create(((net.minecraft.world.phys.AABB) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape cuboid(double p0, double p1, double p2, double p3, double p4, double p5) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.box(p0, p1, p2, p3, p4, p5));
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape getUnboundedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.phys.shapes.Shapes.INFINITY);
    }
}
