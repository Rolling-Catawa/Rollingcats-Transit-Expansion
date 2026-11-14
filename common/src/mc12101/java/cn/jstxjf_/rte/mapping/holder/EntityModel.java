package cn.jstxjf_.rte.mapping.holder;

public final class EntityModel<T extends cn.jstxjf_.rte.mapping.holder.EntityAbstractMapping> extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.model.EntityModel<T>> {

    public EntityModel(net.minecraft.client.model.EntityModel<T> p0) {
        super(p0);
    }

    public static <T extends org.mtr.mapping.holder.EntityAbstractMapping> cn.jstxjf_.rte.mapping.holder.EntityModel<T> cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new EntityModel<>((net.minecraft.client.model.EntityModel<T>) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.model.EntityModel;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void setAngles(T p0, float p1, float p2, float p3, float p4, float p5) {
        ((net.minecraft.client.model.EntityModel) this.data).setupAnim(p0, p1, p2, p3, p4, p5);
    }

    public void copyStateTo(cn.jstxjf_.rte.mapping.holder.EntityModel<T> p0) {
        ((net.minecraft.client.model.EntityModel) this.data).copyPropertiesTo(((net.minecraft.client.model.EntityModel) p0.data));
    }

    public final cn.jstxjf_.rte.mapping.holder.RenderLayer getLayer(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(((net.minecraft.client.model.EntityModel) this.data).renderType(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public void animateModel(T p0, float p1, float p2, float p3) {
        ((net.minecraft.client.model.EntityModel) this.data).prepareMobModel(p0, p1, p2, p3);
    }

    public boolean getChildMapped() {
        return ((net.minecraft.client.model.EntityModel) this.data).young;
    }

    public void setChildMapped(boolean p0) {
        ((net.minecraft.client.model.EntityModel) this.data).young = p0;
    }

    public boolean getRidingMapped() {
        return ((net.minecraft.client.model.EntityModel) this.data).riding;
    }

    public void setRidingMapped(boolean p0) {
        ((net.minecraft.client.model.EntityModel) this.data).riding = p0;
    }

    public float getHandSwingProgressMapped() {
        return ((net.minecraft.client.model.EntityModel) this.data).attackTime;
    }

    public void setHandSwingProgressMapped(float p0) {
        ((net.minecraft.client.model.EntityModel) this.data).attackTime = p0;
    }
}
