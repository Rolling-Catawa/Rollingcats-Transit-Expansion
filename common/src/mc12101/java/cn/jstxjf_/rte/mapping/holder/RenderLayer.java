package cn.jstxjf_.rte.mapping.holder;

public final class RenderLayer extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.RenderType> {

    public RenderLayer(net.minecraft.client.renderer.RenderType p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new RenderLayer((net.minecraft.client.renderer.RenderType) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.RenderType;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getBeaconBeam(cn.jstxjf_.rte.mapping.holder.Identifier p0, boolean p1) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.beaconBeam(((net.minecraft.resources.ResourceLocation) p0.data), p1));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getTripwire() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.tripwire());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getDirectEntityGlint() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityGlintDirect());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityShadow(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityShadow(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getGlint() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.glint());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getWaterMask() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.waterMask());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getLightning() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.lightning());
    }

    public cn.jstxjf_.rte.mapping.holder.VertexFormat getVertexFormat() {
        return new cn.jstxjf_.rte.mapping.holder.VertexFormat(((net.minecraft.client.renderer.RenderType) this.data).format());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityCutout(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityCutout(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityGlint() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityGlint());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getCutout() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.cutout());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getBlockBreaking(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.crumbling(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getLeash() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.leash());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getTranslucent() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.translucent());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityCutoutNoCullZOffset(cn.jstxjf_.rte.mapping.holder.Identifier p0, boolean p1) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityCutoutNoCullZOffset(((net.minecraft.resources.ResourceLocation) p0.data), p1));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityCutoutNoCullZOffset(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityCutoutNoCullZOffset(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public boolean isOutline() {
        return ((net.minecraft.client.renderer.RenderType) this.data).isOutline();
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityTranslucentCull(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityTranslucentCull(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getText(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.text(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getTextSeeThrough(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.textSeeThrough(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer method_30676() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.glintTranslucent());
    }

    public boolean hasCrumbling() {
        return ((net.minecraft.client.renderer.RenderType) this.data).affectsCrumbling();
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getCutoutMipped() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.cutoutMipped());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getItemEntityTranslucentCull(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.itemEntityTranslucentCull(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getArmorEntityGlint() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.armorEntityGlint());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getSolid() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.solid());
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getOutline(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.outline(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityCutoutNoCull(cn.jstxjf_.rte.mapping.holder.Identifier p0, boolean p1) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(((net.minecraft.resources.ResourceLocation) p0.data), p1));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityCutoutNoCull(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public int getExpectedBufferSize() {
        return ((net.minecraft.client.renderer.RenderType) this.data).bufferSize();
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntitySolid(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entitySolid(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityNoOutline(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityNoOutline(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityDecal(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityDecal(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getArmorCutoutNoCull(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.armorCutoutNoCull(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntitySmoothCutout(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entitySmoothCutout(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getLines() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.lines());
    }

    public void startDrawing() {
        ((net.minecraft.client.renderer.RenderType) this.data).setupRenderState();
    }

    public void endDrawing() {
        ((net.minecraft.client.renderer.RenderType) this.data).clearRenderState();
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityTranslucent(cn.jstxjf_.rte.mapping.holder.Identifier p0, boolean p1) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityTranslucent(((net.minecraft.resources.ResourceLocation) p0.data), p1));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEntityTranslucent(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.entityTranslucent(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getEyes(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.eyes(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public static cn.jstxjf_.rte.mapping.holder.RenderLayer getTranslucentMovingBlock() {
        return new cn.jstxjf_.rte.mapping.holder.RenderLayer(net.minecraft.client.renderer.RenderType.translucentMovingBlock());
    }
}
