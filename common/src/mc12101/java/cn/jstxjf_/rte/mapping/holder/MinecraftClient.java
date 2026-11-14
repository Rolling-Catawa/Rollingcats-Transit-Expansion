package cn.jstxjf_.rte.mapping.holder;

public final class MinecraftClient extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.Minecraft> {

    public MinecraftClient(net.minecraft.client.Minecraft p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.MinecraftClient cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new MinecraftClient((net.minecraft.client.Minecraft) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.Minecraft;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.ClientPlayNetworkHandler getNetworkHandler() {
        final var v0 = ((net.minecraft.client.Minecraft) this.data).getConnection();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.ClientPlayNetworkHandler(v0));
    }

    public void close() {
        ((net.minecraft.client.Minecraft) this.data).close();
    }

    public com.mojang.authlib.minecraft.MinecraftSessionService getSessionService() {
        return ((net.minecraft.client.Minecraft) this.data).getMinecraftSessionService();
    }

    public void runTasks(java.util.function.BooleanSupplier p0) {
        ((net.minecraft.client.Minecraft) this.data).managedBlock(p0);
    }

    public void stop() {
        ((net.minecraft.client.Minecraft) this.data).destroy();
    }

    public boolean isPaused() {
        return ((net.minecraft.client.Minecraft) this.data).isPaused();
    }

    public java.lang.String getVersionType() {
        return ((net.minecraft.client.Minecraft) this.data).getVersionType();
    }

    public MinecraftClient(net.minecraft.client.main.GameConfig p0) {
        super(new net.minecraft.client.Minecraft(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.MinecraftClient getInstance() {
        return new cn.jstxjf_.rte.mapping.holder.MinecraftClient(net.minecraft.client.Minecraft.getInstance());
    }

    public cn.jstxjf_.rte.mapping.holder.EntityRenderDispatcher getEntityRenderDispatcher() {
        return new cn.jstxjf_.rte.mapping.holder.EntityRenderDispatcher(((net.minecraft.client.Minecraft) this.data).getEntityRenderDispatcher());
    }

    public boolean isRunning() {
        return ((net.minecraft.client.Minecraft) this.data).isRunning();
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getCameraEntity() {
        final var v0 = ((net.minecraft.client.Minecraft) this.data).getCameraEntity();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.SoundManager getSoundManager() {
        return new cn.jstxjf_.rte.mapping.holder.SoundManager(((net.minecraft.client.Minecraft) this.data).getSoundManager());
    }

    public cn.jstxjf_.rte.mapping.holder.BufferBuilderStorage getBufferBuilders() {
        return new cn.jstxjf_.rte.mapping.holder.BufferBuilderStorage(((net.minecraft.client.Minecraft) this.data).renderBuffers());
    }

    public cn.jstxjf_.rte.mapping.holder.Window getWindow() {
        return new cn.jstxjf_.rte.mapping.holder.Window(((net.minecraft.client.Minecraft) this.data).getWindow());
    }

    public void run() {
        ((net.minecraft.client.Minecraft) this.data).run();
    }

    public boolean hasOutline(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.client.Minecraft) this.data).shouldEntityAppearGlowing(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void openScreen(cn.jstxjf_.rte.mapping.holder.Screen p0) {
        ((net.minecraft.client.Minecraft) this.data).setScreen((p0 == null ? null : ((net.minecraft.client.gui.screens.Screen) p0.data)));
    }

    public java.util.concurrent.CompletableFuture<java.lang.Void> submit(java.lang.Runnable p0) {
        return ((net.minecraft.client.Minecraft) this.data).submit(p0);
    }

    public <V> java.util.concurrent.CompletableFuture<V> submit(java.util.function.Supplier<V> p0) {
        return ((net.minecraft.client.Minecraft) this.data).submit(p0);
    }

    public com.mojang.datafixers.DataFixer getDataFixer() {
        return ((net.minecraft.client.Minecraft) this.data).getFixerUpper();
    }

    public void tick() {
        ((net.minecraft.client.Minecraft) this.data).tick();
    }

    public void execute(java.lang.Runnable p0) {
        ((net.minecraft.client.Minecraft) this.data).execute(p0);
    }

    public java.net.Proxy getNetworkProxy() {
        return ((net.minecraft.client.Minecraft) this.data).getProxy();
    }

    public cn.jstxjf_.rte.mapping.holder.TextureManager getTextureManager() {
        return new cn.jstxjf_.rte.mapping.holder.TextureManager(((net.minecraft.client.Minecraft) this.data).getTextureManager());
    }

    public void setCameraEntity(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.client.Minecraft) this.data).setCameraEntity(((net.minecraft.world.entity.Entity) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ResourceManager getResourceManager() {
        return new cn.jstxjf_.rte.mapping.holder.ResourceManager(((net.minecraft.client.Minecraft) this.data).getResourceManager());
    }

    public final boolean isDemo() {
        return ((net.minecraft.client.Minecraft) this.data).isDemo();
    }

    public static boolean getIsSystemMacMapped() {
        return net.minecraft.client.Minecraft.ON_OSX;
    }

    public java.io.File getRunDirectoryMapped() {
        return ((net.minecraft.client.Minecraft) this.data).gameDirectory;
    }

    public cn.jstxjf_.rte.mapping.holder.ClientWorld getWorldMapped() {
        return (((net.minecraft.client.Minecraft) this.data).level == null ? null : new cn.jstxjf_.rte.mapping.holder.ClientWorld(((net.minecraft.client.Minecraft) this.data).level));
    }

    public void setWorldMapped(cn.jstxjf_.rte.mapping.holder.ClientWorld p0) {
        ((net.minecraft.client.Minecraft) this.data).level = (p0 == null ? null : ((net.minecraft.client.multiplayer.ClientLevel) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getCameraEntityMapped() {
        return (((net.minecraft.client.Minecraft) this.data).cameraEntity == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(((net.minecraft.client.Minecraft) this.data).cameraEntity));
    }

    public void setCameraEntityMapped(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.client.Minecraft) this.data).cameraEntity = (p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.GameOptions getOptionsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.GameOptions(((net.minecraft.client.Minecraft) this.data).options);
    }

    public cn.jstxjf_.rte.mapping.holder.GameRenderer getGameRendererMapped() {
        return new cn.jstxjf_.rte.mapping.holder.GameRenderer(((net.minecraft.client.Minecraft) this.data).gameRenderer);
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getTargetedEntityMapped() {
        return (((net.minecraft.client.Minecraft) this.data).crosshairPickEntity == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(((net.minecraft.client.Minecraft) this.data).crosshairPickEntity));
    }

    public void setTargetedEntityMapped(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.client.Minecraft) this.data).crosshairPickEntity = (p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.ClientPlayerEntity getPlayerMapped() {
        return (((net.minecraft.client.Minecraft) this.data).player == null ? null : new cn.jstxjf_.rte.mapping.holder.ClientPlayerEntity(((net.minecraft.client.Minecraft) this.data).player));
    }

    public void setPlayerMapped(cn.jstxjf_.rte.mapping.holder.ClientPlayerEntity p0) {
        ((net.minecraft.client.Minecraft) this.data).player = (p0 == null ? null : ((net.minecraft.client.player.LocalPlayer) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.HitResult getCrosshairTargetMapped() {
        return (((net.minecraft.client.Minecraft) this.data).hitResult == null ? null : new cn.jstxjf_.rte.mapping.holder.HitResult(((net.minecraft.client.Minecraft) this.data).hitResult));
    }

    public void setCrosshairTargetMapped(cn.jstxjf_.rte.mapping.holder.HitResult p0) {
        ((net.minecraft.client.Minecraft) this.data).hitResult = (p0 == null ? null : ((net.minecraft.world.phys.HitResult) p0.data));
    }

    public java.lang.String getFpsDebugStringMapped() {
        return ((net.minecraft.client.Minecraft) this.data).fpsString;
    }

    public void setFpsDebugStringMapped(java.lang.String p0) {
        ((net.minecraft.client.Minecraft) this.data).fpsString = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.WorldRenderer getWorldRendererMapped() {
        return new cn.jstxjf_.rte.mapping.holder.WorldRenderer(((net.minecraft.client.Minecraft) this.data).levelRenderer);
    }

    public cn.jstxjf_.rte.mapping.holder.Screen getCurrentScreenMapped() {
        return (((net.minecraft.client.Minecraft) this.data).screen == null ? null : new cn.jstxjf_.rte.mapping.holder.Screen(((net.minecraft.client.Minecraft) this.data).screen));
    }

    public void setCurrentScreenMapped(cn.jstxjf_.rte.mapping.holder.Screen p0) {
        ((net.minecraft.client.Minecraft) this.data).screen = (p0 == null ? null : ((net.minecraft.client.gui.screens.Screen) p0.data));
    }
}
