package cn.jstxjf_.rte.mapping.holder;

public final class PlayerManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.players.PlayerList> {

    public PlayerManager(net.minecraft.server.players.PlayerList p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.PlayerManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new PlayerManager((net.minecraft.server.players.PlayerList) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.players.PlayerList;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void remove(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0) {
        ((net.minecraft.server.players.PlayerList) this.data).remove(((net.minecraft.server.level.ServerPlayer) p0.data));
    }

    public void setMainWorld(cn.jstxjf_.rte.mapping.holder.ServerWorld p0) {
        ((net.minecraft.server.players.PlayerList) this.data).addWorldborderListener(((net.minecraft.server.level.ServerLevel) p0.data));
    }

    public int getViewDistance() {
        return ((net.minecraft.server.players.PlayerList) this.data).getViewDistance();
    }

    public cn.jstxjf_.rte.mapping.holder.Text checkCanJoin(java.net.SocketAddress p0, com.mojang.authlib.GameProfile p1) {
        final var v0 = ((net.minecraft.server.players.PlayerList) this.data).canPlayerLogin(p0, p1);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(v0));
    }

    public void sendWorldInfo(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0, cn.jstxjf_.rte.mapping.holder.ServerWorld p1) {
        ((net.minecraft.server.players.PlayerList) this.data).sendLevelInfo(((net.minecraft.server.level.ServerPlayer) p0.data), ((net.minecraft.server.level.ServerLevel) p1.data));
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getUserData() {
        final var v0 = ((net.minecraft.server.players.PlayerList) this.data).getSingleplayerData();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.CompoundTag(v0));
    }

    public void setViewDistance(int p0) {
        ((net.minecraft.server.players.PlayerList) this.data).setViewDistance(p0);
    }

    public java.lang.String[] getOpNames() {
        return ((net.minecraft.server.players.PlayerList) this.data).getOpNames();
    }

    public cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity getPlayer(java.lang.String p0) {
        final var v0 = ((net.minecraft.server.players.PlayerList) this.data).getPlayerByName(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity getPlayer(java.util.UUID p0) {
        final var v0 = ((net.minecraft.server.players.PlayerList) this.data).getPlayer(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer() {
        return new cn.jstxjf_.rte.mapping.holder.MinecraftServer(((net.minecraft.server.players.PlayerList) this.data).getServer());
    }

    public boolean canBypassPlayerLimit(com.mojang.authlib.GameProfile p0) {
        return ((net.minecraft.server.players.PlayerList) this.data).canBypassPlayerLimit(p0);
    }

    public static java.io.File getWhitelistFileMapped() {
        return net.minecraft.server.players.PlayerList.WHITELIST_FILE;
    }
}
