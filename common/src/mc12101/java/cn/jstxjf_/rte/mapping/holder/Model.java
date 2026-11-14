package cn.jstxjf_.rte.mapping.holder;

public final class Model extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.model.Model> {

    public Model(net.minecraft.client.model.Model p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Model cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Model((net.minecraft.client.model.Model) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.model.Model;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public final cn.jstxjf_.rte.mapping.holder.RenderLayer getLayer(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(((net.minecraft.client.model.Model) this.data).renderType(((net.minecraft.resources.ResourceLocation) p0.data)));
    }
}
