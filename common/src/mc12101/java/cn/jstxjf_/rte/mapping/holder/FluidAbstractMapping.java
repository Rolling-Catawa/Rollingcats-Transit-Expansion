package cn.jstxjf_.rte.mapping.holder;

public abstract class FluidAbstractMapping extends net.minecraft.world.level.material.Fluid {

    public abstract int getLevel2(cn.jstxjf_.rte.mapping.holder.FluidState p0);

    public final int getAmount(net.minecraft.world.level.material.FluidState p0) {
        return this.getLevel2(new cn.jstxjf_.rte.mapping.holder.FluidState(p0));
    }

    protected abstract float getBlastResistance2();

    protected final float getExplosionResistance() {
        return this.getBlastResistance2();
    }

    protected abstract boolean canBeReplacedWith2(cn.jstxjf_.rte.mapping.holder.FluidState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.Fluid p3, cn.jstxjf_.rte.mapping.holder.Direction p4);

    protected final boolean canBeReplacedWith(net.minecraft.world.level.material.FluidState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2, net.minecraft.world.level.material.Fluid p3, net.minecraft.core.Direction p4) {
        return this.canBeReplacedWith2(new cn.jstxjf_.rte.mapping.holder.FluidState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.Fluid(p3), cn.jstxjf_.rte.mapping.holder.Direction.convert(p4));
    }

    protected boolean isEmpty2() {
        return super.isEmpty();
    }

    protected final boolean isEmpty() {
        return this.isEmpty2();
    }

    public abstract boolean isStill2(cn.jstxjf_.rte.mapping.holder.FluidState p0);

    public final boolean isSource(net.minecraft.world.level.material.FluidState p0) {
        return this.isStill2(new cn.jstxjf_.rte.mapping.holder.FluidState(p0));
    }

    public boolean matchesType2(cn.jstxjf_.rte.mapping.holder.Fluid p0) {
        return super.isSame(((net.minecraft.world.level.material.Fluid) p0.data));
    }

    public final boolean isSame(net.minecraft.world.level.material.Fluid p0) {
        return this.matchesType2(new cn.jstxjf_.rte.mapping.holder.Fluid(p0));
    }

    protected boolean hasRandomTicks2() {
        return super.isRandomlyTicking();
    }

    protected final boolean isRandomlyTicking() {
        return this.hasRandomTicks2();
    }

    protected abstract cn.jstxjf_.rte.mapping.holder.BlockState toBlockState2(cn.jstxjf_.rte.mapping.holder.FluidState p0);

    protected final net.minecraft.world.level.block.state.BlockState createLegacyBlock(net.minecraft.world.level.material.FluidState p0) {
        final var v0 = this.toBlockState2(new cn.jstxjf_.rte.mapping.holder.FluidState(p0));
        return (v0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) v0.data));
    }

    public abstract float getHeight2(cn.jstxjf_.rte.mapping.holder.FluidState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2);

    public final float getHeight(net.minecraft.world.level.material.FluidState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2) {
        return this.getHeight2(new cn.jstxjf_.rte.mapping.holder.FluidState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2));
    }

    public abstract float getHeight2(cn.jstxjf_.rte.mapping.holder.FluidState p0);

    public final float getOwnHeight(net.minecraft.world.level.material.FluidState p0) {
        return this.getHeight2(new cn.jstxjf_.rte.mapping.holder.FluidState(p0));
    }

    protected cn.jstxjf_.rte.mapping.holder.ParticleEffect getParticle2() {
        final var v0 = super.getDripParticle();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.ParticleEffect(v0));
    }

    protected final net.minecraft.core.particles.ParticleOptions getDripParticle() {
        final var v0 = this.getParticle2();
        return (v0 == null ? null : ((net.minecraft.core.particles.ParticleOptions) v0.data));
    }

    public abstract cn.jstxjf_.rte.mapping.holder.VoxelShape getShape2(cn.jstxjf_.rte.mapping.holder.FluidState p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2);

    public final net.minecraft.world.phys.shapes.VoxelShape getShape(net.minecraft.world.level.material.FluidState p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2) {
        final var v0 = this.getShape2(new cn.jstxjf_.rte.mapping.holder.FluidState(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2));
        return (v0 == null ? null : ((net.minecraft.world.phys.shapes.VoxelShape) v0.data));
    }

    protected abstract cn.jstxjf_.rte.mapping.holder.Vector3d getVelocity2(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.FluidState p2);

    protected final net.minecraft.world.phys.Vec3 getFlow(net.minecraft.world.level.BlockGetter p0, net.minecraft.core.BlockPos p1, net.minecraft.world.level.material.FluidState p2) {
        final var v0 = this.getVelocity2(new cn.jstxjf_.rte.mapping.holder.BlockView(p0), new cn.jstxjf_.rte.mapping.holder.BlockPos(p1), new cn.jstxjf_.rte.mapping.holder.FluidState(p2));
        return (v0 == null ? null : ((net.minecraft.world.phys.Vec3) v0.data));
    }

    protected void onScheduledTick2(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.FluidState p2) {
        super.tick(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.material.FluidState) p2.data));
    }

    protected final void tick(net.minecraft.world.level.Level p0, net.minecraft.core.BlockPos p1, net.minecraft.world.level.material.FluidState p2) {
        this.onScheduledTick2(new cn.jstxjf_.rte.mapping.holder.World(p0), new cn.jstxjf_.rte.mapping.holder.BlockPos(p1), new cn.jstxjf_.rte.mapping.holder.FluidState(p2));
    }

    protected final void setDefaultState2(cn.jstxjf_.rte.mapping.holder.FluidState p0) {
        super.registerDefaultState(((net.minecraft.world.level.material.FluidState) p0.data));
    }

    public final cn.jstxjf_.rte.mapping.holder.FluidState getDefaultState2() {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(super.defaultFluidState());
    }

    public abstract cn.jstxjf_.rte.mapping.holder.Item getBucketItem2();

    public final net.minecraft.world.item.Item getBucket() {
        final var v0 = this.getBucketItem2();
        return (v0 == null ? null : ((net.minecraft.world.item.Item) v0.data));
    }

    public FluidAbstractMapping() {
        super();
    }
}
