package cn.jstxjf_.rte.mapping.holder;

public final class PlayerListEntry extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.multiplayer.PlayerInfo> {

    public PlayerListEntry(net.minecraft.client.multiplayer.PlayerInfo p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.PlayerListEntry cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new PlayerListEntry((net.minecraft.client.multiplayer.PlayerInfo) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.multiplayer.PlayerInfo;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public int getLatency() {
        return ((net.minecraft.client.multiplayer.PlayerInfo) this.data).getLatency();
    }

    public void setDisplayName(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.client.multiplayer.PlayerInfo) this.data).setTabListDisplayName((p0 == null ? null : ((net.minecraft.network.chat.Component) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.GameMode getGameMode() {
        final var v0 = ((net.minecraft.client.multiplayer.PlayerInfo) this.data).getGameMode();
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.GameMode.convert(v0));
    }

    public PlayerListEntry(com.mojang.authlib.GameProfile p0, boolean p1) {
        super(new net.minecraft.client.multiplayer.PlayerInfo(p0, p1));
    }

    public com.mojang.authlib.GameProfile getProfile() {
        return ((net.minecraft.client.multiplayer.PlayerInfo) this.data).getProfile();
    }

    public cn.jstxjf_.rte.mapping.holder.Team getScoreboardTeam() {
        final var v0 = ((net.minecraft.client.multiplayer.PlayerInfo) this.data).getTeam();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Team(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName() {
        final var v0 = ((net.minecraft.client.multiplayer.PlayerInfo) this.data).getTabListDisplayName();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(v0));
    }
}
