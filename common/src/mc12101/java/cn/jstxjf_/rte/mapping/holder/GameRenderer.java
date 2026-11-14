package cn.jstxjf_.rte.mapping.holder;

public final class GameRenderer extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.GameRenderer> {

    public GameRenderer(net.minecraft.client.renderer.GameRenderer p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.GameRenderer cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new GameRenderer((net.minecraft.client.renderer.GameRenderer) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.GameRenderer;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void tick() {
        ((net.minecraft.client.renderer.GameRenderer) this.data).tick();
    }

    public static float getNightVisionStrength(cn.jstxjf_.rte.mapping.holder.LivingEntity p0, float p1) {
        return net.minecraft.client.renderer.GameRenderer.getNightVisionScale(((net.minecraft.world.entity.LivingEntity) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Camera getCamera() {
        return new cn.jstxjf_.rte.mapping.holder.Camera(((net.minecraft.client.renderer.GameRenderer) this.data).getMainCamera());
    }

    public cn.jstxjf_.rte.mapping.holder.OverlayTexture getOverlayTexture() {
        return new cn.jstxjf_.rte.mapping.holder.OverlayTexture(((net.minecraft.client.renderer.GameRenderer) this.data).overlayTexture());
    }

    public float getSkyDarkness(float p0) {
        return ((net.minecraft.client.renderer.GameRenderer) this.data).getDarkenWorldAmount(p0);
    }

    public void updateTargetedEntity(float p0) {
        ((net.minecraft.client.renderer.GameRenderer) this.data).pick(p0);
    }

    public GameRenderer(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0, net.minecraft.client.renderer.ItemInHandRenderer p1, cn.jstxjf_.rte.mapping.holder.ResourceManager p2, cn.jstxjf_.rte.mapping.holder.BufferBuilderStorage p3) {
        super(new net.minecraft.client.renderer.GameRenderer(((net.minecraft.client.Minecraft) p0.data), p1, ((net.minecraft.server.packs.resources.ResourceManager) p2.data), ((net.minecraft.client.renderer.RenderBuffers) p3.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.LightmapTextureManager getLightmapTextureManager() {
        return new cn.jstxjf_.rte.mapping.holder.LightmapTextureManager(((net.minecraft.client.renderer.GameRenderer) this.data).lightTexture());
    }

    public void loadProjectionMatrix(cn.jstxjf_.rte.mapping.holder.Matrix4f p0) {
        ((net.minecraft.client.renderer.GameRenderer) this.data).resetProjectionMatrix(((org.joml.Matrix4f) p0.data));
    }

    public void onResized(int p0, int p1) {
        ((net.minecraft.client.renderer.GameRenderer) this.data).resize(p0, p1);
    }

    public void close() {
        ((net.minecraft.client.renderer.GameRenderer) this.data).close();
    }

    public void showFloatingItem(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        ((net.minecraft.client.renderer.GameRenderer) this.data).displayItemActivation(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public void onCameraEntitySet(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.client.renderer.GameRenderer) this.data).checkEntityPostEffect((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)));
    }
}
