package cn.jstxjf_.rte.mapping.holder;

public final class MinecraftServer extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.MinecraftServer> {

    public MinecraftServer(net.minecraft.server.MinecraftServer p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.MinecraftServer cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new MinecraftServer((net.minecraft.server.MinecraftServer) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.MinecraftServer;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.SaveProperties getSaveProperties() {
        return new cn.jstxjf_.rte.mapping.holder.SaveProperties(((net.minecraft.server.MinecraftServer) this.data).getWorldData());
    }

    public float getTickTime() {
        return ((net.minecraft.server.MinecraftServer) this.data).getCurrentSmoothedTickTime();
    }

    public com.mojang.datafixers.DataFixer getDataFixer() {
        return ((net.minecraft.server.MinecraftServer) this.data).getFixerUpper();
    }

    public void setDemo(boolean p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setDemo(p0);
    }

    public void setMotd(java.lang.String p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setMotd(p0);
    }

    public boolean hasGui() {
        return ((net.minecraft.server.MinecraftServer) this.data).hasGui();
    }

    public void setPvpEnabled(boolean p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setPvpAllowed(p0);
    }

    public final cn.jstxjf_.rte.mapping.holder.ServerWorld getOverworld() {
        return new cn.jstxjf_.rte.mapping.holder.ServerWorld(((net.minecraft.server.MinecraftServer) this.data).overworld());
    }

    public void setDefaultGameMode(cn.jstxjf_.rte.mapping.holder.GameMode p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setDefaultGameType(p0.data);
    }

    public boolean isDemo() {
        return ((net.minecraft.server.MinecraftServer) this.data).isDemo();
    }

    public static <S extends net.minecraft.server.MinecraftServer> S startServer(java.util.function.Function<java.lang.Thread, S> p0) {
        return net.minecraft.server.MinecraftServer.spin(p0);
    }

    public java.lang.String[] getPlayerNames() {
        return ((net.minecraft.server.MinecraftServer) this.data).getPlayerNames();
    }

    public cn.jstxjf_.rte.mapping.holder.BossBarManager getBossBarManager() {
        return new cn.jstxjf_.rte.mapping.holder.BossBarManager(((net.minecraft.server.MinecraftServer) this.data).getCustomBossEvents());
    }

    public boolean isSpawnProtected(cn.jstxjf_.rte.mapping.holder.ServerWorld p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.PlayerEntity p2) {
        return ((net.minecraft.server.MinecraftServer) this.data).isUnderSpawnProtection(((net.minecraft.server.level.ServerLevel) p0.data), ((net.minecraft.core.BlockPos) p1.data), ((net.minecraft.world.entity.player.Player) p2.data));
    }

    public int getPlayerIdleTimeout() {
        return ((net.minecraft.server.MinecraftServer) this.data).getPlayerIdleTimeout();
    }

    public boolean isHardcore() {
        return ((net.minecraft.server.MinecraftServer) this.data).isHardcore();
    }

    public int getSpawnProtectionRadius() {
        return ((net.minecraft.server.MinecraftServer) this.data).getSpawnProtectionRadius();
    }

    public java.lang.Thread getThread() {
        return ((net.minecraft.server.MinecraftServer) this.data).getRunningThread();
    }

    public void setPlayerIdleTimeout(int p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setPlayerIdleTimeout(p0);
    }

    public int getPermissionLevel(com.mojang.authlib.GameProfile p0) {
        return ((net.minecraft.server.MinecraftServer) this.data).getProfilePermissions(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.DataCommandStorage getDataCommandStorage() {
        return new cn.jstxjf_.rte.mapping.holder.DataCommandStorage(((net.minecraft.server.MinecraftServer) this.data).getCommandStorage());
    }

    public int getSpawnRadius(cn.jstxjf_.rte.mapping.holder.ServerWorld p0) {
        return ((net.minecraft.server.MinecraftServer) this.data).getSpawnRadius((p0 == null ? null : ((net.minecraft.server.level.ServerLevel) p0.data)));
    }

    public void stop(boolean p0) {
        ((net.minecraft.server.MinecraftServer) this.data).halt(p0);
    }

    public void setEnforceWhitelist(boolean p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setEnforceWhitelist(p0);
    }

    public int adjustTrackingDistance(int p0) {
        return ((net.minecraft.server.MinecraftServer) this.data).getScaledTrackingDistance(p0);
    }

    public java.util.concurrent.CompletableFuture<java.lang.Void> submit(java.lang.Runnable p0) {
        return ((net.minecraft.server.MinecraftServer) this.data).submit(p0);
    }

    public <V> java.util.concurrent.CompletableFuture<V> submit(java.util.function.Supplier<V> p0) {
        return ((net.minecraft.server.MinecraftServer) this.data).submit(p0);
    }

    public boolean isHost(com.mojang.authlib.GameProfile p0) {
        return ((net.minecraft.server.MinecraftServer) this.data).isSingleplayerOwner(p0);
    }

    public void close() {
        ((net.minecraft.server.MinecraftServer) this.data).close();
    }

    public com.mojang.authlib.minecraft.MinecraftSessionService getSessionService() {
        return ((net.minecraft.server.MinecraftServer) this.data).getSessionService();
    }

    public void setPreventProxyConnections(boolean p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setPreventProxyConnections(p0);
    }

    public boolean isStopped() {
        return ((net.minecraft.server.MinecraftServer) this.data).isStopped();
    }

    public void setPlayerManager(cn.jstxjf_.rte.mapping.holder.PlayerManager p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setPlayerList(((net.minecraft.server.players.PlayerList) p0.data));
    }

    public void dumpProperties(java.nio.file.Path p0) throws java.io.IOException {
        ((net.minecraft.server.MinecraftServer) this.data).dumpServerProperties(p0);
    }

    public java.security.KeyPair getKeyPair() {
        return ((net.minecraft.server.MinecraftServer) this.data).getKeyPair();
    }

    public java.util.concurrent.CompletableFuture<java.lang.Void> reloadResources(java.util.Collection<java.lang.String> p0) {
        return ((net.minecraft.server.MinecraftServer) this.data).reloadResources(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerManager getPlayerManager() {
        return new cn.jstxjf_.rte.mapping.holder.PlayerManager(((net.minecraft.server.MinecraftServer) this.data).getPlayerList());
    }

    public boolean isEnforceWhitelist() {
        return ((net.minecraft.server.MinecraftServer) this.data).isEnforceWhitelist();
    }

    public void setFlightEnabled(boolean p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setFlightAllowed(p0);
    }

    public com.mojang.authlib.GameProfileRepository getGameProfileRepo() {
        return ((net.minecraft.server.MinecraftServer) this.data).getProfileRepository();
    }

    public java.lang.String getServerModName() {
        return ((net.minecraft.server.MinecraftServer) this.data).getServerModName();
    }

    public void setDifficultyLocked(boolean p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setDifficultyLocked(p0);
    }

    public boolean isRunning() {
        return ((net.minecraft.server.MinecraftServer) this.data).isRunning();
    }

    public void execute(java.lang.Runnable p0) {
        ((net.minecraft.server.MinecraftServer) this.data).execute(p0);
    }

    public void setDifficulty(cn.jstxjf_.rte.mapping.holder.Difficulty p0, boolean p1) {
        ((net.minecraft.server.MinecraftServer) this.data).setDifficulty(p0.data, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.CommandFunctionManager getCommandFunctionManager() {
        return new cn.jstxjf_.rte.mapping.holder.CommandFunctionManager(((net.minecraft.server.MinecraftServer) this.data).getFunctions());
    }

    public java.nio.file.Path getSavePath(cn.jstxjf_.rte.mapping.holder.WorldSavePath p0) {
        return ((net.minecraft.server.MinecraftServer) this.data).getWorldPath(((net.minecraft.world.level.storage.LevelResource) p0.data));
    }

    public void setOnlineMode(boolean p0) {
        ((net.minecraft.server.MinecraftServer) this.data).setUsesAuthentication(p0);
    }
}
