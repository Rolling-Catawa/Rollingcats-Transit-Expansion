package cn.jstxjf_.rte.mapping.holder;

public final class StairsBlock extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.StairBlock> {

    public StairsBlock(net.minecraft.world.level.block.StairBlock p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.StairsBlock cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new StairsBlock((net.minecraft.world.level.block.StairBlock) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.StairBlock;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void onEntityLand(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.Entity p1) {
        ((net.minecraft.world.level.block.StairBlock) this.data).updateEntityAfterFallOn(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.world.entity.Entity) p1.data));
    }

    public static void dropStacks(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        net.minecraft.world.level.block.StairBlock.dropResources(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public static void dropStacks(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.WorldAccess p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockEntity p3) {
        net.minecraft.world.level.block.StairBlock.dropResources(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.LevelAccessor) p1.data), ((net.minecraft.core.BlockPos) p2.data), (p3 == null ? null : ((net.minecraft.world.level.block.entity.BlockEntity) p3.data)));
    }

    public static void dropStacks(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockEntity p3, cn.jstxjf_.rte.mapping.holder.Entity p4, cn.jstxjf_.rte.mapping.holder.ItemStack p5) {
        net.minecraft.world.level.block.StairBlock.dropResources(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), (p3 == null ? null : ((net.minecraft.world.level.block.entity.BlockEntity) p3.data)), (p4 == null ? null : ((net.minecraft.world.entity.Entity) p4.data)), ((net.minecraft.world.item.ItemStack) p5.data));
    }

    public void onBroken(cn.jstxjf_.rte.mapping.holder.WorldAccess p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.BlockState p2) {
        ((net.minecraft.world.level.block.StairBlock) this.data).destroy(((net.minecraft.world.level.LevelAccessor) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockState getStateFromRawId(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(net.minecraft.world.level.block.StairBlock.stateById(p0));
    }

    public static boolean isShapeFullCube(cn.jstxjf_.rte.mapping.holder.VoxelShape p0) {
        return net.minecraft.world.level.block.StairBlock.isShapeFullBlock(((net.minecraft.world.phys.shapes.VoxelShape) p0.data));
    }

    public void onDestroyedByExplosion(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Explosion p2) {
        ((net.minecraft.world.level.block.StairBlock) this.data).wasExploded(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.Explosion) p2.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape createCuboidShape(double p0, double p1, double p2, double p3, double p4, double p5) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.level.block.StairBlock.box(p0, p1, p2, p3, p4, p5));
    }

    public StairsBlock(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockSettings p1) {
        super(new net.minecraft.world.level.block.StairBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.block.state.BlockBehaviour.Properties) p1.data)));
    }

    public static void dropStack(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.ItemStack p2) {
        net.minecraft.world.level.block.StairBlock.popResource(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.item.ItemStack) p2.data));
    }

    public void randomDisplayTick(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Random p3) {
        ((net.minecraft.world.level.block.StairBlock) this.data).animateTick(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.util.RandomSource) p3.data));
    }

    public static boolean isStairs(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return net.minecraft.world.level.block.StairBlock.isStairs(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlockFromItem(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.StairBlock.byItem((p0 == null ? null : ((net.minecraft.world.item.Item) p0.data))));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getPlacementState(cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.block.StairBlock) this.data).getStateForPlacement(((net.minecraft.world.item.context.BlockPlaceContext) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Item asItem() {
        return new cn.jstxjf_.rte.mapping.holder.Item(((net.minecraft.world.level.block.StairBlock) this.data).asItem());
    }

    public cn.jstxjf_.rte.mapping.holder.MutableText getName() {
        return new cn.jstxjf_.rte.mapping.holder.MutableText(((net.minecraft.world.level.block.StairBlock) this.data).getName());
    }

    public static void replace(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.WorldAccess p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3, int p4) {
        net.minecraft.world.level.block.StairBlock.updateOrDestroy(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.LevelAccessor) p2.data), ((net.minecraft.core.BlockPos) p3.data), p4);
    }

    public static void replace(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.WorldAccess p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3, int p4, int p5) {
        net.minecraft.world.level.block.StairBlock.updateOrDestroy(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.LevelAccessor) p2.data), ((net.minecraft.core.BlockPos) p3.data), p4, p5);
    }

    public static boolean hasTopRim(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return net.minecraft.world.level.block.StairBlock.canSupportRigidBlock(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean shouldDropItemsOnExplosion(cn.jstxjf_.rte.mapping.holder.Explosion p0) {
        return ((net.minecraft.world.level.block.StairBlock) this.data).dropFromExplosion(((net.minecraft.world.level.Explosion) p0.data));
    }

    public static boolean isFaceFullSquare(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return net.minecraft.world.level.block.StairBlock.isFaceFull(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), p1.data);
    }

    public void afterBreak(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, cn.jstxjf_.rte.mapping.holder.BlockEntity p4, cn.jstxjf_.rte.mapping.holder.ItemStack p5) {
        ((net.minecraft.world.level.block.StairBlock) this.data).playerDestroy(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.player.Player) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), (p4 == null ? null : ((net.minecraft.world.level.block.entity.BlockEntity) p4.data)), ((net.minecraft.world.item.ItemStack) p5.data));
    }

    public cn.jstxjf_.rte.mapping.holder.MapColor getDefaultMapColor() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(((net.minecraft.world.level.block.StairBlock) this.data).defaultMapColor());
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockState postProcessState(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.WorldAccess p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(net.minecraft.world.level.block.StairBlock.updateFromNeighbourShapes(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.LevelAccessor) p1.data), ((net.minecraft.core.BlockPos) p2.data)));
    }

    public final cn.jstxjf_.rte.mapping.holder.BlockState getDefaultState() {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.block.StairBlock) this.data).defaultBlockState());
    }

    public static int getRawIdFromState(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return net.minecraft.world.level.block.StairBlock.getId((p0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) p0.data)));
    }

    public void onPlaced(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.LivingEntity p3, cn.jstxjf_.rte.mapping.holder.ItemStack p4) {
        ((net.minecraft.world.level.block.StairBlock) this.data).setPlacedBy(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), (p3 == null ? null : ((net.minecraft.world.entity.LivingEntity) p3.data)), ((net.minecraft.world.item.ItemStack) p4.data));
    }

    public java.lang.String getTranslationKey() {
        return ((net.minecraft.world.level.block.StairBlock) this.data).getDescriptionId();
    }

    public boolean tryFillWithFluid(cn.jstxjf_.rte.mapping.holder.WorldAccess p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.FluidState p3) {
        return ((net.minecraft.world.level.block.StairBlock) this.data).placeLiquid(((net.minecraft.world.level.LevelAccessor) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), ((net.minecraft.world.level.material.FluidState) p3.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.DirectionProperty getFacingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DirectionProperty(net.minecraft.world.level.block.StairBlock.FACING);
    }

    public static cn.jstxjf_.rte.mapping.holder.BooleanProperty getWaterloggedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.BooleanProperty(net.minecraft.world.level.block.StairBlock.WATERLOGGED);
    }
}
