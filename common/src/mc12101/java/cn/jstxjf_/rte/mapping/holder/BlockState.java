package cn.jstxjf_.rte.mapping.holder;

public final class BlockState extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.state.BlockState> {

    public BlockState(net.minecraft.world.level.block.state.BlockState p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockState cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BlockState((net.minecraft.world.level.block.state.BlockState) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.state.BlockState;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.FluidState getFluidState() {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.block.state.BlockState) this.data).getFluidState());
    }

    public void onEntityCollision(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Entity p2) {
        ((net.minecraft.world.level.block.state.BlockState) this.data).entityInside(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.entity.Entity) p2.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getOutlineShape(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.block.state.BlockState) this.data).getShape(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public boolean hasEmissiveLighting(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).emissiveRendering(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public float calcBlockBreakingDelta(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).getDestroyProgress(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public boolean exceedsCube() {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).hasLargeCollisionShape();
    }

    public float getHardness(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).getDestroySpeed(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public float getAmbientOcclusionLightLevel(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).getShadeBrightness(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getSidesShape(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.block.state.BlockState) this.data).getBlockSupportShape(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.Block getBlock() {
        return new cn.jstxjf_.rte.mapping.holder.Block(((net.minecraft.world.level.block.state.BlockState) this.data).getBlock());
    }

    public void randomTick(cn.jstxjf_.rte.mapping.holder.ServerWorld p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Random p2) {
        ((net.minecraft.world.level.block.state.BlockState) this.data).randomTick(((net.minecraft.server.level.ServerLevel) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.util.RandomSource) p2.data));
    }

    public <T extends java.lang.Comparable<T>> boolean contains(cn.jstxjf_.rte.mapping.holder.Property<T> p0) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).hasProperty(((net.minecraft.world.level.block.state.properties.Property) p0.data));
    }

    public boolean isTranslucent(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).propagatesSkylightDown(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean hasRandomTicks() {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isRandomlyTicking();
    }

    public void scheduledTick(cn.jstxjf_.rte.mapping.holder.ServerWorld p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Random p2) {
        ((net.minecraft.world.level.block.state.BlockState) this.data).tick(((net.minecraft.server.level.ServerLevel) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.util.RandomSource) p2.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getCullingFace(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.block.state.BlockState) this.data).getFaceOcclusionShape(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2.data));
    }

    public int getWeakRedstonePower(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).getSignal(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2.data);
    }

    public boolean isToolRequired() {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).requiresCorrectToolForDrops();
    }

    public final void updateNeighbors(cn.jstxjf_.rte.mapping.holder.WorldAccess p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, int p2, int p3) {
        ((net.minecraft.world.level.block.state.BlockState) this.data).updateNeighbourShapes(((net.minecraft.world.level.LevelAccessor) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2, p3);
    }

    public final void updateNeighbors(cn.jstxjf_.rte.mapping.holder.WorldAccess p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, int p2) {
        ((net.minecraft.world.level.block.state.BlockState) this.data).updateNeighbourShapes(((net.minecraft.world.level.LevelAccessor) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getModelOffset(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.level.block.state.BlockState) this.data).getOffset(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public boolean allowsSpawning(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.EntityType<?> p2) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isValidSpawn(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.entity.EntityType) p2.data));
    }

    public boolean isFullCube(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isCollisionShapeFullBlock(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getCollisionShape(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.ShapeContext p2) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.block.state.BlockState) this.data).getCollisionShape(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.phys.shapes.CollisionContext) p2.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getCollisionShape(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.block.state.BlockState) this.data).getCollisionShape(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public long getRenderingSeed(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).getSeed(((net.minecraft.core.BlockPos) p0.data));
    }

    public <T extends java.lang.Comparable<T>> cn.jstxjf_.rte.mapping.holder.BlockState cycle(cn.jstxjf_.rte.mapping.holder.Property<T> p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.block.state.BlockState) ((net.minecraft.world.level.block.state.BlockState) this.data).cycle(((net.minecraft.world.level.block.state.properties.Property) p0.data))));
    }

    public boolean isOf(cn.jstxjf_.rte.mapping.holder.Block p0) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).is(((net.minecraft.world.level.block.Block) p0.data));
    }

    public boolean onSyncedBlockEvent(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, int p2, int p3) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).triggerEvent(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2, p3);
    }

    public boolean isSolidBlock(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isRedstoneConductor(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public final boolean isSolidSurface(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Entity p2, cn.jstxjf_.rte.mapping.holder.Direction p3) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).entityCanStandOnFace(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.entity.Entity) p2.data), p3.data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockRenderType getRenderType() {
        return cn.jstxjf_.rte.mapping.holder.BlockRenderType.convert(((net.minecraft.world.level.block.state.BlockState) this.data).getRenderShape());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState rotate(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.block.state.BlockState) this.data).rotate(p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getStateForNeighborUpdate(cn.jstxjf_.rte.mapping.holder.Direction p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.WorldAccess p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3, cn.jstxjf_.rte.mapping.holder.BlockPos p4) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.block.state.BlockState) this.data).updateShape(p0.data, ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.LevelAccessor) p2.data), ((net.minecraft.core.BlockPos) p3.data), ((net.minecraft.core.BlockPos) p4.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getRaycastShape(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.block.state.BlockState) this.data).getInteractionShape(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public boolean emitsRedstonePower() {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isSignalSource();
    }

    public boolean canBucketPlace(cn.jstxjf_.rte.mapping.holder.Fluid p0) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).canBeReplaced(((net.minecraft.world.level.material.Fluid) p0.data));
    }

    public boolean shouldSuffocate(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isSuffocating(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean shouldPostProcess(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).hasPostProcess(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public void onBlockBreakStart(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.PlayerEntity p2) {
        ((net.minecraft.world.level.block.state.BlockState) this.data).attack(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.entity.player.Player) p2.data));
    }

    public final boolean hasSolidTopSurface(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Entity p2) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).entityCanStandOn(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.entity.Entity) p2.data));
    }

    public boolean isSideInvisible(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Direction p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).skipRendering(((net.minecraft.world.level.block.state.BlockState) p0.data), p1.data);
    }

    public boolean isOpaqueFullCube(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isSolidRender(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean hasComparatorOutput() {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).hasAnalogOutputSignal();
    }

    public boolean canReplace(cn.jstxjf_.rte.mapping.holder.ItemPlacementContext p0) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).canBeReplaced(((net.minecraft.world.item.context.BlockPlaceContext) p0.data));
    }

    public int getStrongRedstonePower(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).getDirectSignal(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2.data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState mirror(cn.jstxjf_.rte.mapping.holder.Mirror p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.block.state.BlockState) this.data).mirror(p0.data));
    }

    public boolean shouldBlockVision(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isViewBlocking(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public boolean isAir() {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isAir();
    }

    public void initShapeCache() {
        ((net.minecraft.world.level.block.state.BlockState) this.data).initCache();
    }

    public boolean hasSidedTransparency() {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).useShapeForLightOcclusion();
    }

    public int getComparatorOutput(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).getAnalogOutputSignal(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.MapColor getMapColor(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(((net.minecraft.world.level.block.state.BlockState) this.data).getMapColor(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public <T extends java.lang.Comparable<T>, V extends T> cn.jstxjf_.rte.mapping.holder.BlockState with(cn.jstxjf_.rte.mapping.holder.Property<T> p0, V p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.block.state.BlockState) ((net.minecraft.world.level.block.state.BlockState) this.data).setValue(((net.minecraft.world.level.block.state.properties.Property) p0.data), p1)));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getCullingShape(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.block.state.BlockState) this.data).getOcclusionShape(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public boolean isSideSolidFullSquare(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Direction p2) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).isFaceSturdy(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2.data);
    }

    public <T extends java.lang.Comparable<T>> java.util.Optional<T> getOrEmpty(cn.jstxjf_.rte.mapping.holder.Property<T> p0) {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).getOptionalValue(((net.minecraft.world.level.block.state.properties.Property) p0.data));
    }

    public void prepare(cn.jstxjf_.rte.mapping.holder.WorldAccess p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, int p2, int p3) {
        ((net.minecraft.world.level.block.state.BlockState) this.data).updateIndirectNeighbourShapes(((net.minecraft.world.level.LevelAccessor) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2, p3);
    }

    public final void prepare(cn.jstxjf_.rte.mapping.holder.WorldAccess p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, int p2) {
        ((net.minecraft.world.level.block.state.BlockState) this.data).updateIndirectNeighbourShapes(((net.minecraft.world.level.LevelAccessor) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2);
    }

    public boolean isOpaque() {
        return ((net.minecraft.world.level.block.state.BlockState) this.data).canOcclude();
    }

    public <T extends java.lang.Comparable<T>> T get(cn.jstxjf_.rte.mapping.holder.Property<T> p0) {
        return this.data.getValue(p0.data);
    }
}
