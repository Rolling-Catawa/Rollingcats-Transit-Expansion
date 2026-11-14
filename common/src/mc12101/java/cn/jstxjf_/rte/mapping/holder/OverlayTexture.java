package cn.jstxjf_.rte.mapping.holder;

public final class OverlayTexture extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.texture.OverlayTexture> {

    public OverlayTexture(net.minecraft.client.renderer.texture.OverlayTexture p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.OverlayTexture cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new OverlayTexture((net.minecraft.client.renderer.texture.OverlayTexture) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.texture.OverlayTexture;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void setupOverlayColor() {
        ((net.minecraft.client.renderer.texture.OverlayTexture) this.data).setupOverlayColor();
    }

    public static int getV(boolean p0) {
        return net.minecraft.client.renderer.texture.OverlayTexture.v(p0);
    }

    public void teardownOverlayColor() {
        ((net.minecraft.client.renderer.texture.OverlayTexture) this.data).teardownOverlayColor();
    }

    public static int packUv(int p0, int p1) {
        return net.minecraft.client.renderer.texture.OverlayTexture.pack(p0, p1);
    }

    public void close() {
        ((net.minecraft.client.renderer.texture.OverlayTexture) this.data).close();
    }

    public OverlayTexture() {
        super(new net.minecraft.client.renderer.texture.OverlayTexture());
    }

    public static int getU(float p0) {
        return net.minecraft.client.renderer.texture.OverlayTexture.u(p0);
    }

    public static int getUv(float p0, boolean p1) {
        return net.minecraft.client.renderer.texture.OverlayTexture.pack(p0, p1);
    }

    public static int getDefaultUvMapped() {
        return net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
    }
}
