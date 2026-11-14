package cn.jstxjf_.rte.mapping.holder;

public final class FluidState extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.material.FluidState> {

    public FluidState(net.minecraft.world.level.material.FluidState p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.FluidState cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new FluidState((net.minecraft.world.level.material.FluidState) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.material.FluidState;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getVelocity(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.level.material.FluidState) this.data).getFlow(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public void onScheduledTick(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        ((net.minecraft.world.level.material.FluidState) this.data).tick(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.VoxelShape getShape(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return new cn.jstxjf_.rte.mapping.holder.VoxelShape(((net.minecraft.world.level.material.FluidState) this.data).getShape(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.ParticleEffect getParticle() {
        final var v0 = ((net.minecraft.world.level.material.FluidState) this.data).getDripParticle();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.ParticleEffect(v0));
    }

    public float getHeight(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return ((net.minecraft.world.level.material.FluidState) this.data).getHeight(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public float getHeight() {
        return ((net.minecraft.world.level.material.FluidState) this.data).getOwnHeight();
    }

    public boolean hasRandomTicks() {
        return ((net.minecraft.world.level.material.FluidState) this.data).isRandomlyTicking();
    }

    public <T extends java.lang.Comparable<T>> boolean contains(cn.jstxjf_.rte.mapping.holder.Property<T> p0) {
        return ((net.minecraft.world.level.material.FluidState) this.data).hasProperty(((net.minecraft.world.level.block.state.properties.Property) p0.data));
    }

    public <T extends java.lang.Comparable<T>> cn.jstxjf_.rte.mapping.holder.FluidState cycle(cn.jstxjf_.rte.mapping.holder.Property<T> p0) {
        return new cn.jstxjf_.rte.mapping.holder.FluidState(((net.minecraft.world.level.material.FluidState) ((net.minecraft.world.level.material.FluidState) this.data).cycle(((net.minecraft.world.level.block.state.properties.Property) p0.data))));
    }

    public boolean isEmpty() {
        return ((net.minecraft.world.level.material.FluidState) this.data).isEmpty();
    }

    public boolean isStill() {
        return ((net.minecraft.world.level.material.FluidState) this.data).isSource();
    }

    public int getLevel() {
        return ((net.minecraft.world.level.material.FluidState) this.data).getAmount();
    }

    public <T extends java.lang.Comparable<T>> java.util.Optional<T> getOrEmpty(cn.jstxjf_.rte.mapping.holder.Property<T> p0) {
        return ((net.minecraft.world.level.material.FluidState) this.data).getOptionalValue(((net.minecraft.world.level.block.state.properties.Property) p0.data));
    }

    public float getBlastResistance() {
        return ((net.minecraft.world.level.material.FluidState) this.data).getExplosionResistance();
    }

    public boolean canBeReplacedWith(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.Fluid p2, cn.jstxjf_.rte.mapping.holder.Direction p3) {
        return ((net.minecraft.world.level.material.FluidState) this.data).canBeReplacedWith(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.material.Fluid) p2.data), p3.data);
    }

    public cn.jstxjf_.rte.mapping.holder.Fluid getFluid() {
        return new cn.jstxjf_.rte.mapping.holder.Fluid(((net.minecraft.world.level.material.FluidState) this.data).getType());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockState getBlockState() {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(((net.minecraft.world.level.material.FluidState) this.data).createLegacyBlock());
    }
}
