package cn.jstxjf_.rte.mapping.holder;

public final class WorldRenderer extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.renderer.LevelRenderer> {

    public WorldRenderer(net.minecraft.client.renderer.LevelRenderer p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.WorldRenderer cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new WorldRenderer((net.minecraft.client.renderer.LevelRenderer) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.renderer.LevelRenderer;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, boolean p1, double p2, double p3, double p4, double p5, double p6, double p7) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6, p7);
    }

    public void addParticle(cn.jstxjf_.rte.mapping.holder.ParticleEffect p0, boolean p1, boolean p2, double p3, double p4, double p5, double p6, double p7, double p8) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).addParticle(((net.minecraft.core.particles.ParticleOptions) p0.data), p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public void tickRainSplashing(cn.jstxjf_.rte.mapping.holder.Camera p0) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).tickRain(((net.minecraft.client.Camera) p0.data));
    }

    public java.lang.String getName() {
        return ((net.minecraft.client.renderer.LevelRenderer) this.data).getName();
    }

    public void tick() {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).tick();
    }

    public void setWorld(cn.jstxjf_.rte.mapping.holder.ClientWorld p0) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).setLevel((p0 == null ? null : ((net.minecraft.client.multiplayer.ClientLevel) p0.data)));
    }

    public void reload(cn.jstxjf_.rte.mapping.holder.ResourceManager p0) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).onResourceManagerReload(((net.minecraft.server.packs.resources.ResourceManager) p0.data));
    }

    public void scheduleBlockRenders(int p0, int p1, int p2, int p3, int p4, int p5) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).setBlocksDirty(p0, p1, p2, p3, p4, p5);
    }

    public static int getLightmapCoordinates(cn.jstxjf_.rte.mapping.holder.BlockRenderView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1) {
        return net.minecraft.client.renderer.LevelRenderer.getLightColor(((net.minecraft.world.level.BlockAndTintGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data));
    }

    public static int getLightmapCoordinates(cn.jstxjf_.rte.mapping.holder.BlockRenderView p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2) {
        return net.minecraft.client.renderer.LevelRenderer.getLightColor(((net.minecraft.world.level.BlockAndTintGetter) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.core.BlockPos) p2.data));
    }

    public WorldRenderer(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0, cn.jstxjf_.rte.mapping.holder.EntityRenderDispatcher p1, net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher p2, cn.jstxjf_.rte.mapping.holder.BufferBuilderStorage p3) {
        super(new net.minecraft.client.renderer.LevelRenderer(((net.minecraft.client.Minecraft) p0.data), ((net.minecraft.client.renderer.entity.EntityRenderDispatcher) p1.data), p2, ((net.minecraft.client.renderer.RenderBuffers) p3.data)));
    }

    public void close() {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).close();
    }

    public void onResized(int p0, int p1) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).resize(p0, p1);
    }

    public void scheduleBlockRerenderIfNeeded(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1, cn.jstxjf_.rte.mapping.holder.BlockState p2) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).setBlockDirty(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data));
    }

    public void updateBlock(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, int p4) {
        ((net.minecraft.client.renderer.LevelRenderer) this.data).blockChanged(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.level.block.state.BlockState) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), p4);
    }
}
