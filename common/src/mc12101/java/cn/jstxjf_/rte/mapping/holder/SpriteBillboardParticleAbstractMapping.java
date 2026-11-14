package cn.jstxjf_.rte.mapping.holder;

public abstract class SpriteBillboardParticleAbstractMapping extends net.minecraft.client.particle.TextureSheetParticle {

    public void markDead2() {
        super.remove();
    }

    public final void remove() {
        this.markDead2();
    }

    public void setColor2(float p0, float p1, float p2) {
        super.setColor(p0, p1, p2);
    }

    public final void setColor(float p0, float p1, float p2) {
        this.setColor2(p0, p1, p2);
    }

    public boolean isAlive2() {
        return super.isAlive();
    }

    public final boolean isAlive() {
        return this.isAlive2();
    }

    public void tick2() {
        super.tick();
    }

    public final void tick() {
        this.tick2();
    }

    public void move2(double p0, double p1, double p2) {
        super.move(p0, p1, p2);
    }

    public final void move(double p0, double p1, double p2) {
        this.move2(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Particle move2(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Particle(super.setPower(p0));
    }

    public final net.minecraft.client.particle.Particle setPower(float p0) {
        final var v0 = this.move2(p0);
        return (v0 == null ? null : ((net.minecraft.client.particle.Particle) v0.data));
    }

    public void setMaxAge2(int p0) {
        super.setLifetime(p0);
    }

    public final void setLifetime(int p0) {
        this.setMaxAge2(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Particle scale2(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Particle(super.scale(p0));
    }

    public final net.minecraft.client.particle.Particle scale(float p0) {
        final var v0 = this.scale2(p0);
        return (v0 == null ? null : ((net.minecraft.client.particle.Particle) v0.data));
    }

    public float getSize2(float p0) {
        return super.getQuadSize(p0);
    }

    public final float getQuadSize(float p0) {
        return this.getSize2(p0);
    }

    public void setPos2(double p0, double p1, double p2) {
        super.setPos(p0, p1, p2);
    }

    public final void setPos(double p0, double p1, double p2) {
        this.setPos2(p0, p1, p2);
    }

    public int getMaxAge2() {
        return super.getLifetime();
    }

    public final int getLifetime() {
        return this.getMaxAge2();
    }

    public cn.jstxjf_.rte.mapping.holder.Box getBoundingBox2() {
        return new cn.jstxjf_.rte.mapping.holder.Box(super.getBoundingBox());
    }

    public final net.minecraft.world.phys.AABB getBoundingBox() {
        final var v0 = this.getBoundingBox2();
        return (v0 == null ? null : ((net.minecraft.world.phys.AABB) v0.data));
    }

    public SpriteBillboardParticleAbstractMapping(cn.jstxjf_.rte.mapping.holder.ClientWorld p0, double p1, double p2, double p3) {
        super(((net.minecraft.client.multiplayer.ClientLevel) p0.data), p1, p2, p3);
    }

    public SpriteBillboardParticleAbstractMapping(cn.jstxjf_.rte.mapping.holder.ClientWorld p0, double p1, double p2, double p3, double p4, double p5, double p6) {
        super(((net.minecraft.client.multiplayer.ClientLevel) p0.data), p1, p2, p3, p4, p5, p6);
    }

    public void setBoundingBox2(cn.jstxjf_.rte.mapping.holder.Box p0) {
        super.setBoundingBox(((net.minecraft.world.phys.AABB) p0.data));
    }

    public final void setBoundingBox(net.minecraft.world.phys.AABB p0) {
        this.setBoundingBox2(new cn.jstxjf_.rte.mapping.holder.Box(p0));
    }
}
