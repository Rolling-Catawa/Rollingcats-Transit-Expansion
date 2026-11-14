package cn.jstxjf_.rte.mapping.holder;

public final class ClientPlayNetworkHandler extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.multiplayer.ClientPacketListener> {

    public ClientPlayNetworkHandler(net.minecraft.client.multiplayer.ClientPacketListener p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ClientPlayNetworkHandler cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ClientPlayNetworkHandler((net.minecraft.client.multiplayer.ClientPacketListener) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.multiplayer.ClientPacketListener;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public ClientPlayNetworkHandler(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0, net.minecraft.network.Connection p1, net.minecraft.client.multiplayer.CommonListenerCookie p2) {
        super(new net.minecraft.client.multiplayer.ClientPacketListener(((net.minecraft.client.Minecraft) p0.data), p1, p2));
    }

    public java.util.UUID getSessionId() {
        return ((net.minecraft.client.multiplayer.ClientPacketListener) this.data).getId();
    }

    public com.mojang.authlib.GameProfile getProfile() {
        return ((net.minecraft.client.multiplayer.ClientPacketListener) this.data).getLocalGameProfile();
    }

    public cn.jstxjf_.rte.mapping.holder.ClientWorld getWorld() {
        return new cn.jstxjf_.rte.mapping.holder.ClientWorld(((net.minecraft.client.multiplayer.ClientPacketListener) this.data).getLevel());
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerListEntry getPlayerListEntry(java.util.UUID p0) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientPacketListener) this.data).getPlayerInfo(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerListEntry(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.PlayerListEntry getPlayerListEntry(java.lang.String p0) {
        final var v0 = ((net.minecraft.client.multiplayer.ClientPacketListener) this.data).getPlayerInfo(p0);
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.PlayerListEntry(v0));
    }

    public java.util.Collection<java.util.UUID> getPlayerUuids() {
        return ((net.minecraft.client.multiplayer.ClientPacketListener) this.data).getOnlinePlayerIds();
    }
}
