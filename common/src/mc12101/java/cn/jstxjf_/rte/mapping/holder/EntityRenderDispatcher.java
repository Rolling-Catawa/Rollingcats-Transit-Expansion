package cn.jstxjf_.rte.mapping.holder;

public final class EntityRenderDispatcher extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.entity.EntityRenderDispatcher> {

    public EntityRenderDispatcher(net.minecraft.client.renderer.entity.EntityRenderDispatcher p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.EntityRenderDispatcher cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new EntityRenderDispatcher((net.minecraft.client.renderer.entity.EntityRenderDispatcher) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.entity.EntityRenderDispatcher;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public <E extends org.mtr.mapping.holder.EntityAbstractMapping> boolean shouldRender(E p0, cn.jstxjf_.rte.mapping.holder.Frustum p1, double p2, double p3, double p4) {
        return ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).shouldRender(p0, ((net.minecraft.client.renderer.culling.Frustum) p1.data), p2, p3, p4);
    }

    public EntityRenderDispatcher(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0, cn.jstxjf_.rte.mapping.holder.TextureManager p1, net.minecraft.client.renderer.entity.ItemRenderer p2, net.minecraft.client.renderer.block.BlockRenderDispatcher p3, cn.jstxjf_.rte.mapping.holder.TextRenderer p4, cn.jstxjf_.rte.mapping.holder.GameOptions p5, net.minecraft.client.model.geom.EntityModelSet p6) {
        super(new net.minecraft.client.renderer.entity.EntityRenderDispatcher(((net.minecraft.client.Minecraft) p0.data), ((net.minecraft.client.renderer.texture.TextureManager) p1.data), p2, p3, ((net.minecraft.client.gui.Font) p4.data), ((net.minecraft.client.Options) p5.data), p6));
    }

    public double getSquaredDistanceToCamera(double p0, double p1, double p2) {
        return ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).distanceToSqr(p0, p1, p2);
    }

    public double getSquaredDistanceToCamera(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).distanceToSqr(((net.minecraft.world.entity.Entity) p0.data));
    }

    public boolean shouldRenderHitboxes() {
        return ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).shouldRenderHitBoxes();
    }

    public void setWorld(cn.jstxjf_.rte.mapping.holder.World p0) {
        ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).setLevel((p0 == null ? null : ((net.minecraft.world.level.Level) p0.data)));
    }

    public void configure(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.Camera p1, cn.jstxjf_.rte.mapping.holder.Entity p2) {
        ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).prepare(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.client.Camera) p1.data), ((net.minecraft.world.entity.Entity) p2.data));
    }

    public <E extends org.mtr.mapping.holder.EntityAbstractMapping> int getLight(E p0, float p1) {
        return ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).getPackedLightCoords(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getTargetedEntityMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Entity(((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).crosshairPickEntity);
    }

    public void setTargetedEntityMapped(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).crosshairPickEntity = ((net.minecraft.world.entity.Entity) p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.Camera getCameraMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Camera(((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).camera);
    }

    public void setCameraMapped(cn.jstxjf_.rte.mapping.holder.Camera p0) {
        ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).camera = ((net.minecraft.client.Camera) p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.GameOptions getGameOptionsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.GameOptions(((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).options);
    }

    public cn.jstxjf_.rte.mapping.holder.TextureManager getTextureManagerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.TextureManager(((net.minecraft.client.renderer.entity.EntityRenderDispatcher) this.data).textureManager);
    }
}
