package cn.jstxjf_.rte.mapping.holder;

public final class AbstractTexture extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.texture.AbstractTexture> {

    public AbstractTexture(net.minecraft.client.renderer.texture.AbstractTexture p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.AbstractTexture cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new AbstractTexture((net.minecraft.client.renderer.texture.AbstractTexture) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.texture.AbstractTexture;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void bindTexture() {
        ((net.minecraft.client.renderer.texture.AbstractTexture) this.data).bind();
    }

    public int getGlId() {
        return ((net.minecraft.client.renderer.texture.AbstractTexture) this.data).getId();
    }

    public void setFilter(boolean p0, boolean p1) {
        ((net.minecraft.client.renderer.texture.AbstractTexture) this.data).setFilter(p0, p1);
    }

    public void registerTexture(cn.jstxjf_.rte.mapping.holder.TextureManager p0, cn.jstxjf_.rte.mapping.holder.ResourceManager p1, cn.jstxjf_.rte.mapping.holder.Identifier p2, java.util.concurrent.Executor p3) {
        ((net.minecraft.client.renderer.texture.AbstractTexture) this.data).reset(((net.minecraft.client.renderer.texture.TextureManager) p0.data), ((net.minecraft.server.packs.resources.ResourceManager) p1.data), ((net.minecraft.resources.ResourceLocation) p2.data), p3);
    }

    public void load(cn.jstxjf_.rte.mapping.holder.ResourceManager p0) throws java.io.IOException {
        ((net.minecraft.client.renderer.texture.AbstractTexture) this.data).load(((net.minecraft.server.packs.resources.ResourceManager) p0.data));
    }

    public void clearGlId() {
        ((net.minecraft.client.renderer.texture.AbstractTexture) this.data).releaseId();
    }

    public void close() {
        ((net.minecraft.client.renderer.texture.AbstractTexture) this.data).close();
    }
}
