package cn.jstxjf_.rte.mapping.holder;

public final class Explosion extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.Explosion> {

    public Explosion(net.minecraft.world.level.Explosion p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Explosion cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Explosion((net.minecraft.world.level.Explosion) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.Explosion;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void affectWorld(boolean p0) {
        ((net.minecraft.world.level.Explosion) this.data).finalizeExplosion(p0);
    }

    public Explosion(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.Entity p1, double p2, double p3, double p4, float p5, boolean p6, net.minecraft.world.level.Explosion.BlockInteraction p7, java.util.List<net.minecraft.core.BlockPos> p8) {
        super(new net.minecraft.world.level.Explosion(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.Entity) p1.data), p2, p3, p4, p5, p6, p7, p8));
    }

    public Explosion(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.Entity p1, double p2, double p3, double p4, float p5, boolean p6, net.minecraft.world.level.Explosion.BlockInteraction p7) {
        super(new net.minecraft.world.level.Explosion(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.entity.Entity) p1.data), p2, p3, p4, p5, p6, p7));
    }

    public static float getExposure(cn.jstxjf_.rte.mapping.holder.Vector3d p0, cn.jstxjf_.rte.mapping.holder.Entity p1) {
        return net.minecraft.world.level.Explosion.getSeenPercent(((net.minecraft.world.phys.Vec3) p0.data), ((net.minecraft.world.entity.Entity) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.LivingEntity getCausingEntity() {
        final var v0 = ((net.minecraft.world.level.Explosion) this.data).getIndirectSourceEntity();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.LivingEntity(v0));
    }
}
