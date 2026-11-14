package cn.jstxjf_.rte.mapping.holder;

public final class BossBarManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.bossevents.CustomBossEvents> {

    public BossBarManager(net.minecraft.server.bossevents.CustomBossEvents p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BossBarManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BossBarManager((net.minecraft.server.bossevents.CustomBossEvents) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.bossevents.CustomBossEvents;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void onPlayerConnect(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0) {
        ((net.minecraft.server.bossevents.CustomBossEvents) this.data).onPlayerConnect(((net.minecraft.server.level.ServerPlayer) p0.data));
    }

    public BossBarManager() {
        super(new net.minecraft.server.bossevents.CustomBossEvents());
    }

    public void onPlayerDisconnect(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0) {
        ((net.minecraft.server.bossevents.CustomBossEvents) this.data).onPlayerDisconnect(((net.minecraft.server.level.ServerPlayer) p0.data));
    }
}
