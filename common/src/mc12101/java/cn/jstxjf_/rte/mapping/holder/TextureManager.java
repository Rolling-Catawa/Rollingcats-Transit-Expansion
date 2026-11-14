package cn.jstxjf_.rte.mapping.holder;

public final class TextureManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.texture.TextureManager> {

    public TextureManager(net.minecraft.client.renderer.texture.TextureManager p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextureManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new TextureManager((net.minecraft.client.renderer.texture.TextureManager) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.texture.TextureManager;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void close() {
        ((net.minecraft.client.renderer.texture.TextureManager) this.data).close();
    }

    public java.util.concurrent.CompletableFuture<java.lang.Void> loadTextureAsync(cn.jstxjf_.rte.mapping.holder.Identifier p0, java.util.concurrent.Executor p1) {
        return ((net.minecraft.client.renderer.texture.TextureManager) this.data).preload(((net.minecraft.resources.ResourceLocation) p0.data), p1);
    }

    public void destroyTexture(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        ((net.minecraft.client.renderer.texture.TextureManager) this.data).release(((net.minecraft.resources.ResourceLocation) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier registerDynamicTexture(java.lang.String p0, cn.jstxjf_.rte.mapping.holder.NativeImageBackedTexture p1) {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(((net.minecraft.client.renderer.texture.TextureManager) this.data).register(p0, ((net.minecraft.client.renderer.texture.DynamicTexture) p1.data)));
    }

    public TextureManager(cn.jstxjf_.rte.mapping.holder.ResourceManager p0) {
        super(new net.minecraft.client.renderer.texture.TextureManager(((net.minecraft.server.packs.resources.ResourceManager) p0.data)));
    }

    public java.lang.String getName() {
        return ((net.minecraft.client.renderer.texture.TextureManager) this.data).getName();
    }

    public void registerTexture(cn.jstxjf_.rte.mapping.holder.Identifier p0, cn.jstxjf_.rte.mapping.holder.AbstractTexture p1) {
        ((net.minecraft.client.renderer.texture.TextureManager) this.data).register(((net.minecraft.resources.ResourceLocation) p0.data), ((net.minecraft.client.renderer.texture.AbstractTexture) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.AbstractTexture getTexture(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        final var v0 = ((net.minecraft.client.renderer.texture.TextureManager) this.data).getTexture(((net.minecraft.resources.ResourceLocation) p0.data));
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.AbstractTexture(v0));
    }

    public void tick() {
        ((net.minecraft.client.renderer.texture.TextureManager) this.data).tick();
    }

    public void bindTexture(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        ((net.minecraft.client.renderer.texture.TextureManager) this.data).bindForSetup(((net.minecraft.resources.ResourceLocation) p0.data));
    }

    public static cn.jstxjf_.rte.mapping.holder.Identifier getMissingIdentifierMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(net.minecraft.client.renderer.texture.TextureManager.INTENTIONAL_MISSING_TEXTURE);
    }
}
