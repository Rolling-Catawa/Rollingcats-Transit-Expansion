package cn.jstxjf_.rte.mapping.holder;

public final class ParticleEffect extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.core.particles.ParticleOptions> {

    public ParticleEffect(net.minecraft.core.particles.ParticleOptions p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ParticleEffect cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ParticleEffect((net.minecraft.core.particles.ParticleOptions) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.core.particles.ParticleOptions;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }
}
