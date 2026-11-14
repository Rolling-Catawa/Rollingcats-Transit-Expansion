package cn.jstxjf_.rte.mapping.holder;

public final class DefaultParticleType extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.core.particles.SimpleParticleType> {

    public DefaultParticleType(net.minecraft.core.particles.SimpleParticleType p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new DefaultParticleType((net.minecraft.core.particles.SimpleParticleType) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.core.particles.SimpleParticleType;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.DefaultParticleType getType() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(((net.minecraft.core.particles.SimpleParticleType) this.data).getType());
    }

    public boolean shouldAlwaysSpawn() {
        return ((net.minecraft.core.particles.SimpleParticleType) this.data).getOverrideLimiter();
    }
}
