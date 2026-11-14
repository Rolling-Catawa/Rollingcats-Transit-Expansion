package cn.jstxjf_.rte.mapping.holder;

public abstract class BlockAbstractMapping extends net.minecraft.world.level.block.Block {

    public void onStateReplaced2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, boolean p4) {
        super.onRemove(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), p4);
    }

    public final void onRemove(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, net.minecraft.world.level.block.state.BlockState p3, boolean p4) {
        this.onStateReplaced2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.BlockState(p3), p4);
    }

    public void onEntityLand2(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.Entity p1) {
        super.updateEntityAfterFallOn(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.world.entity.Entity) p1.data));
    }

    public final void updateEntityAfterFallOn(net.minecraft.world.level.BlockGetter p0, net.minecraft.world.entity.Entity p1) {
        this.onEntityLand2(new cn.jstxjf_.rte.mapping.holder.BlockView(p0), new cn.jstxjf_.rte.mapping.holder.Entity(p1));
    }

    public BlockAbstractMapping(cn.jstxjf_.rte.mapping.holder.BlockSettings p0) {
        super(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) p0.data));
    }

    public static void dropStacks2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        net.minecraft.world.level.block.Block.dropResources(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public static void dropStacks2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.WorldAccess p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockEntity p3) {
        net.minecraft.world.level.block.Block.dropResources(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.LevelAccessor) p1.data), ((net.minecraft.core.BlockPos) p2.data), (p3 == null ? null : ((net.minecraft.world.level.block.entity.BlockEntity) p3.data)));
    }

    public static void dropStacks2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockEntity p3, cn.jstxjf_.rte.mapping.holder.Entity p4, cn.jstxjf_.rte.mapping.holder.ItemStack p5) {
        net.minecraft.world.level.block.Block.dropResources(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), (p3 == null ? null : ((net.minecraft.world.level.block.entity.BlockEntity) p3.data)), (p4 == null ? null : ((net.minecraft.world.entity.Entity) p4.data)), ((net.minecraft.world.item.ItemStack) p5.data));
    }

    public void scheduledTick2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.ServerWorld p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Random p3) {
        super.tick(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.server.level.ServerLevel) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.util.RandomSource) p3.data));
    }

    public final void tick(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.server.level.ServerLevel p1, net.minecraft.core.BlockPos p2, net.minecraft.util.RandomSource p3) {
        this.scheduledTick2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.ServerWorld(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.Random(p3));
    }

    public int getWeakRedstonePower2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Direction p3) {
        return super.getSignal(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), p3.data);
    }

    public final int getSignal(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2, net.minecraft.core.Direction p3) {
        return this.getWeakRedstonePower2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), cn.jstxjf_.rte.mapping.holder.Direction.convert(p3));
    }

    protected cn.jstxjf_.rte.mapping.holder.Block asBlock2() {
        return new cn.jstxjf_.rte.mapping.holder.Block(super.asBlock());
    }

    protected final net.minecraft.world.level.block.Block asBlock() {
        final var v0 = this.asBlock2();
        return (v0 == null ? null : ((net.minecraft.world.level.block.Block) v0.data));
    }

    public float getAmbientOcclusionLightLevel2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return super.getShadeBrightness(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public final float getShadeBrightness(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2) {
        return this.getAmbientOcclusionLightLevel2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getSidesShape2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(super.getBlockSupportShape(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data)));
    }

    public final net.minecraft.world.phys.shapes.VoxelShape getBlockSupportShape(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2) {
        final var v0 = this.getSidesShape2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2));
        return (v0 == null ? null : ((net.minecraft.world.phys.shapes.VoxelShape) v0.data));
    }

    public void onBroken2(cn.jstxjf_.rte.mapping.holder.WorldAccess p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.BlockState p2) {
        super.destroy(((net.minecraft.world.level.LevelAccessor) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data));
    }

    public final void destroy(net.minecraft.world.level.LevelAccessor p0, net.minecraft.core.BlockPos p1, net.minecraft.world.level.block.state.BlockState p2) {
        this.onBroken2(new cn.jstxjf_.rte.mapping.holder.WorldAccess(p0), new cn.jstxjf_.rte.mapping.holder.BlockPos(p1), new cn.jstxjf_.rte.mapping.holder.BlockState(p2));
    }

    public void randomTick2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.ServerWorld p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Random p3) {
        super.randomTick(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.server.level.ServerLevel) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.util.RandomSource) p3.data));
    }

    public final void randomTick(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.server.level.ServerLevel p1, net.minecraft.core.BlockPos p2, net.minecraft.util.RandomSource p3) {
        this.randomTick2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.ServerWorld(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.Random(p3));
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockState getStateFromRawId2(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(net.minecraft.world.level.block.Block.stateById(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState2(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(super.getFluidState(((net.minecraft.world.level.block.state.BlockState) p0.data)));
    }

    public final net.minecraft.world.level.material.FluidState getFluidState(net.minecraft.world.level.block.state.BlockState p0) {
        final var v0 = this.getFluidState2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0));
        return (v0 == null ? null : ((net.minecraft.world.level.material.FluidState) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getOutlineShape2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.ShapeContext p3) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(super.getShape(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.CollisionContext) p3.data)));
    }

    public final net.minecraft.world.phys.shapes.VoxelShape getShape(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2, net.minecraft.world.phys.shapes.CollisionContext p3) {
        final var v0 = this.getOutlineShape2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.ShapeContext(p3));
        return (v0 == null ? null : ((net.minecraft.world.phys.shapes.VoxelShape) v0.data));
    }

    public void onEntityCollision2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Entity p3) {
        super.entityInside(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.entity.Entity) p3.data));
    }

    public final void entityInside(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, net.minecraft.world.entity.Entity p3) {
        this.onEntityCollision2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.Entity(p3));
    }

    public static boolean isShapeFullCube2(cn.jstxjf_.rte.mapping.holder.VoxelShape p0) {
        return net.minecraft.world.level.block.Block.isShapeFullBlock(((net.minecraft.world.phys.shapes.VoxelShape) p0.data));
    }

    public float calcBlockBreakingDelta2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, cn.jstxjf_.rte.mapping.holder.BlockView p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3) {
        return super.getDestroyProgress(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.entity.player.Player) p1.data), ((net.minecraft.world.level.BlockGetter) p2.data), ((net.minecraft.core.BlockPos) p3.data));
    }

    public final float getDestroyProgress(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.entity.player.Player p1, net.minecraft.world.level.BlockGetter p2, net.minecraft.core.BlockPos p3) {
        return this.calcBlockBreakingDelta2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p1), new cn.jstxjf_.rte.mapping.holder.BlockView(p2), new cn.jstxjf_.rte.mapping.holder.BlockPos(p3));
    }

    public void onDestroyedByExplosion2(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Explosion p2) {
        super.wasExploded(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.Explosion) p2.data));
    }

    public final void wasExploded(net.minecraft.world.level.Level p0, net.minecraft.core.BlockPos p1, net.minecraft.world.level.Explosion p2) {
        this.onDestroyedByExplosion2(new cn.jstxjf_.rte.mapping.holder.World(p0), new cn.jstxjf_.rte.mapping.holder.BlockPos(p1), new cn.jstxjf_.rte.mapping.holder.Explosion(p2));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockRenderType getRenderType2(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return cn.jstxjf_.rte.mapping.holder.BlockRenderType.convert(super.getRenderShape(((net.minecraft.world.level.block.state.BlockState) p0.data)));
    }

    public final net.minecraft.world.level.block.RenderShape getRenderShape(net.minecraft.world.level.block.state.BlockState p0) {
        final var v0 = this.getRenderType2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0));
        return (v0 == null ? null : v0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState rotate2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockRotation p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(super.rotate(((net.minecraft.world.level.block.state.BlockState) p0.data), p1.data));
    }

    public final net.minecraft.world.level.block.state.BlockState rotate(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.block.Rotation p1) {
        final var v0 = this.rotate2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(p1));
        return (v0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getStateForNeighborUpdate2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Direction p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.WorldAccess p3, cn.jstxjf_.rte.mapping.holder.BlockPos p4, cn.jstxjf_.rte.mapping.holder.BlockPos p5) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(super.updateShape(((net.minecraft.world.level.block.state.BlockState) p0.data), p1.data, ((net.minecraft.world.level.block.state.BlockState) p2.data), ((net.minecraft.world.level.LevelAccessor) p3.data), ((net.minecraft.core.BlockPos) p4.data), ((net.minecraft.core.BlockPos) p5.data)));
    }

    public final net.minecraft.world.level.block.state.BlockState updateShape(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.core.Direction p1, net.minecraft.world.level.block.state.BlockState p2, net.minecraft.world.level.LevelAccessor p3, net.minecraft.core.BlockPos p4, net.minecraft.core.BlockPos p5) {
        final var v0 = this.getStateForNeighborUpdate2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), cn.jstxjf_.rte.mapping.holder.Direction.convert(p1), new cn.jstxjf_.rte.mapping.holder.BlockState(p2), new cn.jstxjf_.rte.mapping.holder.WorldAccess(p3), new cn.jstxjf_.rte.mapping.holder.BlockPos(p4), new cn.jstxjf_.rte.mapping.holder.BlockPos(p5));
        return (v0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getCollisionShape2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.ShapeContext p3) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(super.getCollisionShape(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.CollisionContext) p3.data)));
    }

    public final net.minecraft.world.phys.shapes.VoxelShape getCollisionShape(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2, net.minecraft.world.phys.shapes.CollisionContext p3) {
        final var v0 = this.getCollisionShape2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.ShapeContext(p3));
        return (v0 == null ? null : ((net.minecraft.world.phys.shapes.VoxelShape) v0.data));
    }

    public long getRenderingSeed2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return super.getSeed(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public final long getSeed(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.core.BlockPos p1) {
        return this.getRenderingSeed2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockPos(p1));
    }

    public static cn.jstxjf_.rte.mapping.holder.VoxelShape createCuboidShape2(double p0, double p1, double p2, double p3, double p4, double p5) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(net.minecraft.world.level.block.Block.box(p0, p1, p2, p3, p4, p5));
    }

    public boolean onSyncedBlockEvent2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, int p3, int p4) {
        return super.triggerEvent(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), p3, p4);
    }

    public final boolean triggerEvent(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, int p3, int p4) {
        return this.onSyncedBlockEvent2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), p3, p4);
    }

    public void onBlockAdded2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, boolean p4) {
        super.onPlace(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), p4);
    }

    public final void onPlace(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, net.minecraft.world.level.block.state.BlockState p3, boolean p4) {
        this.onBlockAdded2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.BlockState(p3), p4);
    }

    public static void dropStack2(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.ItemStack p2) {
        net.minecraft.world.level.block.Block.popResource(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.item.ItemStack) p2.data));
    }

    public void randomDisplayTick2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Random p3) {
        super.animateTick(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.util.RandomSource) p3.data));
    }

    public final void animateTick(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, net.minecraft.util.RandomSource p3) {
        this.randomDisplayTick2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.Random(p3));
    }

    public boolean hasComparatorOutput2(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return super.hasAnalogOutputSignal(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public final boolean hasAnalogOutputSignal(net.minecraft.world.level.block.state.BlockState p0) {
        return this.hasComparatorOutput2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0));
    }

    public boolean canReplace2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p1) {
        return super.canBeReplaced(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.item.context.BlockPlaceContext) p1.data));
    }

    public final boolean canBeReplaced(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.item.context.BlockPlaceContext p1) {
        return this.canReplace2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.ItemPlacementContext(p1));
    }

    public int getStrongRedstonePower2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Direction p3) {
        return super.getDirectSignal(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), p3.data);
    }

    public final int getDirectSignal(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2, net.minecraft.core.Direction p3) {
        return this.getStrongRedstonePower2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), cn.jstxjf_.rte.mapping.holder.Direction.convert(p3));
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlockFromItem2(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Block.byItem((p0 == null ? null : ((net.minecraft.world.item.Item) p0.data))));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState mirror2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Mirror p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(super.mirror(((net.minecraft.world.level.block.state.BlockState) p0.data), p1.data));
    }

    public final net.minecraft.world.level.block.state.BlockState mirror(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.block.Mirror p1) {
        final var v0 = this.mirror2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), cn.jstxjf_.rte.mapping.holder.Mirror.convert(p1));
        return (v0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Item asItem2() {
        return new cn.jstxjf_.rte.mapping.holder.Item(super.asItem());
    }

    public final net.minecraft.world.item.Item asItem() {
        final var v0 = this.asItem2();
        return (v0 == null ? null : ((net.minecraft.world.item.Item) v0.data));
    }

    public void onBlockBreakStart2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.PlayerEntity p3) {
        super.attack(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.entity.player.Player) p3.data));
    }

    public final void attack(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, net.minecraft.world.entity.player.Player p3) {
        this.onBlockBreakStart2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p3));
    }

    protected final void setDefaultState2(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        super.registerDefaultState(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.MutableText getName2() {
        return new cn.jstxjf_.rte.mapping.holder.MutableText(super.getName());
    }

    public final net.minecraft.network.chat.MutableComponent getName() {
        final var v0 = this.getName2();
        return (v0 == null ? null : ((net.minecraft.network.chat.MutableComponent) v0.data));
    }

    public boolean isSideInvisible2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return super.skipRendering(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), p2.data);
    }

    public final boolean skipRendering(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.block.state.BlockState p1, net.minecraft.core.Direction p2) {
        return this.isSideInvisible2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockState(p1), cn.jstxjf_.rte.mapping.holder.Direction.convert(p2));
    }

    public static void replace2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.WorldAccess p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3, int p4) {
        net.minecraft.world.level.block.Block.updateOrDestroy(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.LevelAccessor) p2.data), ((net.minecraft.core.BlockPos) p3.data), p4);
    }

    public static void replace2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.WorldAccess p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3, int p4, int p5) {
        net.minecraft.world.level.block.Block.updateOrDestroy(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.LevelAccessor) p2.data), ((net.minecraft.core.BlockPos) p3.data), p4, p5);
    }

    public static boolean hasTopRim2(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return net.minecraft.world.level.block.Block.canSupportRigidBlock(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean shouldDropItemsOnExplosion2(cn.jstxjf_.rte.mapping.holder.Explosion p0) {
        return super.dropFromExplosion(((net.minecraft.world.level.Explosion) p0.data));
    }

    public final boolean dropFromExplosion(net.minecraft.world.level.Explosion p0) {
        return this.shouldDropItemsOnExplosion2(new cn.jstxjf_.rte.mapping.holder.Explosion(p0));
    }

    public static boolean isFaceFullSquare2(cn.jstxjf_.rte.mapping.holder.VoxelShape p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return net.minecraft.world.level.block.Block.isFaceFull(((net.minecraft.world.phys.shapes.VoxelShape) p0.data), p1.data);
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getRaycastShape2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(super.getInteractionShape(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data)));
    }

    public final net.minecraft.world.phys.shapes.VoxelShape getInteractionShape(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2) {
        final var v0 = this.getRaycastShape2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2));
        return (v0 == null ? null : ((net.minecraft.world.phys.shapes.VoxelShape) v0.data));
    }

    public boolean emitsRedstonePower2(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return super.isSignalSource(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public final boolean isSignalSource(net.minecraft.world.level.block.state.BlockState p0) {
        return this.emitsRedstonePower2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getCameraCollisionShape2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.ShapeContext p3) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(super.getVisualShape(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.phys.shapes.CollisionContext) p3.data)));
    }

    public final net.minecraft.world.phys.shapes.VoxelShape getVisualShape(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2, net.minecraft.world.phys.shapes.CollisionContext p3) {
        final var v0 = this.getCameraCollisionShape2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.ShapeContext(p3));
        return (v0 == null ? null : ((net.minecraft.world.phys.shapes.VoxelShape) v0.data));
    }

    public boolean canBucketPlace2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Fluid p1) {
        return super.canBeReplaced(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.material.Fluid) p1.data));
    }

    public final boolean canBeReplaced(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.material.Fluid p1) {
        return this.canBucketPlace2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.Fluid(p1));
    }

    public void afterBreak2(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.PlayerEntity p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, cn.jstxjf_.rte.mapping.holder.BlockEntity p4, cn.jstxjf_.rte.mapping.holder.ItemStack p5) {
        super.playerDestroy(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.player.Player) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), (p4 == null ? null : ((net.minecraft.world.level.block.entity.BlockEntity) p4.data)), ((net.minecraft.world.item.ItemStack) p5.data));
    }

    public final void playerDestroy(net.minecraft.world.level.Level p0, net.minecraft.world.entity.player.Player p1, net.minecraft.core.BlockPos p2, net.minecraft.world.level.block.state.BlockState p3, net.minecraft.world.level.block.entity.BlockEntity p4, net.minecraft.world.item.ItemStack p5) {
        this.afterBreak2(new cn.jstxjf_.rte.mapping.holder.World(p0), new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.BlockState(p3), (p4 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockEntity(p4)), new cn.jstxjf_.rte.mapping.holder.ItemStack(p5));
    }

    public cn.jstxjf_.rte.mapping.holder.MapColor getDefaultMapColor2() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(super.defaultMapColor());
    }

    public final net.minecraft.world.level.material.MapColor defaultMapColor() {
        final var v0 = this.getDefaultMapColor2();
        return (v0 == null ? null : ((net.minecraft.world.level.material.MapColor) v0.data));
    }

    public void prepare2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.WorldAccess p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, int p3, int p4) {
        super.updateIndirectNeighbourShapes(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.LevelAccessor) p1.data), ((net.minecraft.core.BlockPos) p2.data), p3, p4);
    }

    public final void updateIndirectNeighbourShapes(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.LevelAccessor p1, net.minecraft.core.BlockPos p2, int p3, int p4) {
        this.prepare2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.WorldAccess(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), p3, p4);
    }

    public boolean isTranslucent2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return super.propagatesSkylightDown(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public final boolean propagatesSkylightDown(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2) {
        return this.isTranslucent2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2));
    }

    public boolean hasRandomTicks2(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return super.isRandomlyTicking(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public final boolean isRandomlyTicking(net.minecraft.world.level.block.state.BlockState p0) {
        return this.hasRandomTicks2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockState postProcessState2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.WorldAccess p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(net.minecraft.world.level.block.Block.updateFromNeighbourShapes(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.LevelAccessor) p1.data), ((net.minecraft.core.BlockPos) p2.data)));
    }

    public final cn.jstxjf_.rte.mapping.holder.BlockState getDefaultState2() {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(super.defaultBlockState());
    }

    public static int getRawIdFromState2(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return net.minecraft.world.level.block.Block.getId((p0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) p0.data)));
    }

    public void onPlaced2(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.LivingEntity p3, cn.jstxjf_.rte.mapping.holder.ItemStack p4) {
        super.setPlacedBy(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), (p3 == null ? null : ((net.minecraft.world.entity.LivingEntity) p3.data)), ((net.minecraft.world.item.ItemStack) p4.data));
    }

    public final void setPlacedBy(net.minecraft.world.level.Level p0, net.minecraft.core.BlockPos p1, net.minecraft.world.level.block.state.BlockState p2, net.minecraft.world.entity.LivingEntity p3, net.minecraft.world.item.ItemStack p4) {
        this.onPlaced2(new cn.jstxjf_.rte.mapping.holder.World(p0), new cn.jstxjf_.rte.mapping.holder.BlockPos(p1), new cn.jstxjf_.rte.mapping.holder.BlockState(p2), (p3 == null ? null : new cn.jstxjf_.rte.mapping.holder.LivingEntity(p3)), new cn.jstxjf_.rte.mapping.holder.ItemStack(p4));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getCullingShape2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(super.getOcclusionShape(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data)));
    }

    public final net.minecraft.world.phys.shapes.VoxelShape getOcclusionShape(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2) {
        final var v0 = this.getCullingShape2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2));
        return (v0 == null ? null : ((net.minecraft.world.phys.shapes.VoxelShape) v0.data));
    }

    public void neighborUpdate2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Block p3, cn.jstxjf_.rte.mapping.holder.BlockPos p4, boolean p5) {
        super.neighborChanged(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.Block) p3.data), ((net.minecraft.core.BlockPos) p4.data), p5);
    }

    public final void neighborChanged(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, net.minecraft.world.level.block.Block p3, net.minecraft.core.BlockPos p4, boolean p5) {
        this.neighborUpdate2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.Block(p3), new cn.jstxjf_.rte.mapping.holder.BlockPos(p4), p5);
    }

    public int getComparatorOutput2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return super.getAnalogOutputSignal(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.level.Level) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public final int getAnalogOutputSignal(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2) {
        return this.getComparatorOutput2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2));
    }

    public java.lang.String getTranslationKey2() {
        return super.getDescriptionId();
    }

    public final java.lang.String getDescriptionId() {
        return this.getTranslationKey2();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getPlacementState2(cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p0) {
        final var v0 = super.getStateForPlacement(((net.minecraft.world.item.context.BlockPlaceContext) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.BlockState(v0));
    }

    public final net.minecraft.world.level.block.state.BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext p0) {
        final var v0 = this.getPlacementState2(new cn.jstxjf_.rte.mapping.holder.ItemPlacementContext(p0));
        return (v0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult onUse2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.World p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.PlayerEntity p3, cn.jstxjf_.rte.mapping.holder.Hand p4, cn.jstxjf_.rte.mapping.holder.BlockHitResult p5) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(super.useWithoutItem(p0.data, p1.data, p2.data, p3.data, p5.data));
    }

    protected final net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.level.Level p1, net.minecraft.core.BlockPos p2, net.minecraft.world.entity.player.Player p3, net.minecraft.world.phys.BlockHitResult p4) {
        return this.onUse2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.World(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p3), cn.jstxjf_.rte.mapping.holder.Hand.MAIN_HAND, new cn.jstxjf_.rte.mapping.holder.BlockHitResult(p4)).data;
    }
}
