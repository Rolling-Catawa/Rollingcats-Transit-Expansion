package cn.jstxjf_.rte.mapping.holder;

public final class Particle extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.particle.Particle> {

    public Particle(net.minecraft.client.particle.Particle p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Particle cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Particle((net.minecraft.client.particle.Particle) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.particle.Particle;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void markDead() {
        ((net.minecraft.client.particle.Particle) this.data).remove();
    }

    public void setColor(float p0, float p1, float p2) {
        ((net.minecraft.client.particle.Particle) this.data).setColor(p0, p1, p2);
    }

    public boolean isAlive() {
        return ((net.minecraft.client.particle.Particle) this.data).isAlive();
    }

    public void tick() {
        ((net.minecraft.client.particle.Particle) this.data).tick();
    }

    public void move(double p0, double p1, double p2) {
        ((net.minecraft.client.particle.Particle) this.data).move(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Particle move(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Particle(((net.minecraft.client.particle.Particle) this.data).setPower(p0));
    }

    public void setMaxAge(int p0) {
        ((net.minecraft.client.particle.Particle) this.data).setLifetime(p0);
    }

    public void setPos(double p0, double p1, double p2) {
        ((net.minecraft.client.particle.Particle) this.data).setPos(p0, p1, p2);
    }

    public int getMaxAge() {
        return ((net.minecraft.client.particle.Particle) this.data).getLifetime();
    }

    public cn.jstxjf_.rte.mapping.holder.Particle scale(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Particle(((net.minecraft.client.particle.Particle) this.data).scale(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Box getBoundingBox() {
        return new cn.jstxjf_.rte.mapping.holder.Box(((net.minecraft.client.particle.Particle) this.data).getBoundingBox());
    }

    public void setBoundingBox(cn.jstxjf_.rte.mapping.holder.Box p0) {
        ((net.minecraft.client.particle.Particle) this.data).setBoundingBox(((net.minecraft.world.phys.AABB) p0.data));
    }
}
