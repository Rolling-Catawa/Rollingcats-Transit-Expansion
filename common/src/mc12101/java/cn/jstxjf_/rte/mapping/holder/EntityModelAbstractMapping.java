package cn.jstxjf_.rte.mapping.holder;

public abstract class EntityModelAbstractMapping<T extends cn.jstxjf_.rte.mapping.holder.EntityAbstractMapping> extends net.minecraft.client.model.EntityModel<T> {

    public abstract void setAngles2(T p0, float p1, float p2, float p3, float p4, float p5);

    public final void setAngles(T p0, float p1, float p2, float p3, float p4, float p5) {
        this.setAngles2(p0, p1, p2, p3, p4, p5);
    }

    public void copyStateTo2(cn.jstxjf_.rte.mapping.holder.EntityModel<T> p0) {
        super.copyPropertiesTo(((net.minecraft.client.model.EntityModel) p0.data));
    }

    public final void copyPropertiesTo(net.minecraft.client.model.EntityModel<T> p0) {
        this.copyStateTo2(new cn.jstxjf_.rte.mapping.holder.EntityModel(p0));
    }

    public final cn.jstxjf_.rte.mapping.holder.RenderLayer getLayer2(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(super.renderType(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public EntityModelAbstractMapping() {
        super();
    }

    public EntityModelAbstractMapping(java.util.function.Function<net.minecraft.resources.ResourceLocation, net.minecraft.client.renderer.RenderType> p0) {
        super(p0);
    }

    public void animateModel2(T p0, float p1, float p2, float p3) {
        super.prepareMobModel(p0, p1, p2, p3);
    }

    public final void animateModel(T p0, float p1, float p2, float p3) {
        this.animateModel2(p0, p1, p2, p3);
    }

    public boolean getChildMapped() {
        return this.young;
    }

    public void setChildMapped(boolean p0) {
        this.young = p0;
    }

    public boolean getRidingMapped() {
        return this.riding;
    }

    public void setRidingMapped(boolean p0) {
        this.riding = p0;
    }

    public float getHandSwingProgressMapped() {
        return this.attackTime;
    }

    public void setHandSwingProgressMapped(float p0) {
        this.attackTime = p0;
    }
}
