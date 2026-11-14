package cn.jstxjf_.rte.mapping.holder;

public final class ModelPart extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.model.geom.ModelPart> {

    public ModelPart(net.minecraft.client.model.geom.ModelPart p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ModelPart cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ModelPart((net.minecraft.client.model.geom.ModelPart) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.model.geom.ModelPart;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public ModelPart(java.util.List<net.minecraft.client.model.geom.ModelPart.Cube> p0, java.util.Map<java.lang.String, net.minecraft.client.model.geom.ModelPart> p1) {
        super(new net.minecraft.client.model.geom.ModelPart(p0, p1));
    }

    public boolean getVisibleMapped() {
        return ((net.minecraft.client.model.geom.ModelPart) this.data).visible;
    }

    public void setVisibleMapped(boolean p0) {
        ((net.minecraft.client.model.geom.ModelPart) this.data).visible = p0;
    }
}
