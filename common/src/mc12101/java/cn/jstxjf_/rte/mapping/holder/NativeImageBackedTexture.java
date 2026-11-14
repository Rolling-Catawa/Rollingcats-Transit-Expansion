package cn.jstxjf_.rte.mapping.holder;

public final class NativeImageBackedTexture extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.texture.DynamicTexture> {

    public NativeImageBackedTexture(net.minecraft.client.renderer.texture.DynamicTexture p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.NativeImageBackedTexture cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new NativeImageBackedTexture((net.minecraft.client.renderer.texture.DynamicTexture) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.texture.DynamicTexture;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void bindTexture() {
        ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).bind();
    }

    public void setImage(cn.jstxjf_.rte.mapping.holder.NativeImage p0) {
        ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).setPixels(((com.mojang.blaze3d.platform.NativeImage) p0.data));
    }

    public void close() {
        ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).close();
    }

    public cn.jstxjf_.rte.mapping.holder.NativeImage getImage() {
        final var v0 = ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).getPixels();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.NativeImage(v0));
    }

    public int getGlId() {
        return ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).getId();
    }

    public void setFilter(boolean p0, boolean p1) {
        ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).setFilter(p0, p1);
    }

    public void load(cn.jstxjf_.rte.mapping.holder.ResourceManager p0) {
        ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).load(((net.minecraft.server.packs.resources.ResourceManager) p0.data));
    }

    public void registerTexture(cn.jstxjf_.rte.mapping.holder.TextureManager p0, cn.jstxjf_.rte.mapping.holder.ResourceManager p1, cn.jstxjf_.rte.mapping.holder.Identifier p2, java.util.concurrent.Executor p3) {
        ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).reset(((net.minecraft.client.renderer.texture.TextureManager) p0.data), ((net.minecraft.server.packs.resources.ResourceManager) p1.data), ((net.minecraft.resources.ResourceLocation) p2.data), p3);
    }

    public void clearGlId() {
        ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).releaseId();
    }

    public NativeImageBackedTexture(cn.jstxjf_.rte.mapping.holder.NativeImage p0) {
        super(new net.minecraft.client.renderer.texture.DynamicTexture(((com.mojang.blaze3d.platform.NativeImage) p0.data)));
    }

    public NativeImageBackedTexture(int p0, int p1, boolean p2) {
        super(new net.minecraft.client.renderer.texture.DynamicTexture(p0, p1, p2));
    }

    public void upload() {
        ((net.minecraft.client.renderer.texture.DynamicTexture) this.data).upload();
    }
}
