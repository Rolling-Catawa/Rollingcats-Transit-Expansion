package cn.jstxjf_.rte.mapping.holder;

public final class LightmapTextureManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.LightTexture> {

    public LightmapTextureManager(net.minecraft.client.renderer.LightTexture p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.LightmapTextureManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new LightmapTextureManager((net.minecraft.client.renderer.LightTexture) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.LightTexture;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void close() {
        ((net.minecraft.client.renderer.LightTexture) this.data).close();
    }

    public void enable() {
        ((net.minecraft.client.renderer.LightTexture) this.data).turnOnLightLayer();
    }

    public void disable() {
        ((net.minecraft.client.renderer.LightTexture) this.data).turnOffLightLayer();
    }

    public static int getBlockLightCoordinates(int p0) {
        return net.minecraft.client.renderer.LightTexture.block(p0);
    }

    public void tick() {
        ((net.minecraft.client.renderer.LightTexture) this.data).tick();
    }

    public void update(float p0) {
        ((net.minecraft.client.renderer.LightTexture) this.data).updateLightTexture(p0);
    }

    public static int pack(int p0, int p1) {
        return net.minecraft.client.renderer.LightTexture.pack(p0, p1);
    }

    public static int getSkyLightCoordinates(int p0) {
        return net.minecraft.client.renderer.LightTexture.sky(p0);
    }

    public LightmapTextureManager(cn.jstxjf_.rte.mapping.holder.GameRenderer p0, cn.jstxjf_.rte.mapping.holder.MinecraftClient p1) {
        super(new net.minecraft.client.renderer.LightTexture(((net.minecraft.client.renderer.GameRenderer) p0.data), ((net.minecraft.client.Minecraft) p1.data)));
    }
}
