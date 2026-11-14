package cn.jstxjf_.rte.mapping.holder;

public abstract class ModelAbstractMapping extends net.minecraft.client.model.Model {

    public final cn.jstxjf_.rte.mapping.holder.RenderLayer getLayer2(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(super.renderType(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public ModelAbstractMapping(java.util.function.Function<net.minecraft.resources.ResourceLocation, net.minecraft.client.renderer.RenderType> p0) {
        super(p0);
    }
}
