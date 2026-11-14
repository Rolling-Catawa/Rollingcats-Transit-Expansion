package cn.jstxjf_.rte.util;

public final class RailEditPermission {

    private static final int OP_LEVEL = 2;

    private RailEditPermission() {
    }

    public static boolean allowed(net.minecraft.server.level.ServerPlayer player, double x, double y, double z) {
        if (player == null) {
            return true;
        }
        return RailEditRules.allowed(player.hasPermissions(OP_LEVEL), player.mayBuild(), player.distanceToSqr(x, y, z));
    }

    public static boolean allowedRail(net.minecraft.server.level.ServerPlayer player, org.mtr.core.data.Rail rail) {
        if (player == null) {
            return true;
        }
        if (rail == null) {
            return player.hasPermissions(OP_LEVEL);
        }
        final org.mtr.core.data.Position p1 = ((cn.jstxjf_.rte.mixin.RailAccessor) (Object) rail).rte$getPosition1();
        final org.mtr.core.data.Position p2 = ((cn.jstxjf_.rte.mixin.RailAccessor) (Object) rail).rte$getPosition2();
        if (p1 == null || p2 == null) {
            return player.hasPermissions(OP_LEVEL);
        }
        return allowed(player, p1.getX() + 0.5, p1.getY(), p1.getZ() + 0.5)
            && allowed(player, p2.getX() + 0.5, p2.getY(), p2.getZ() + 0.5);
    }

    public static boolean allowedRailId(net.minecraft.server.level.ServerPlayer player, String railId) {
        if (player == null || railId == null || railId.isEmpty()) {
            return true;
        }
        org.mtr.core.data.Rail rail = null;
        try {
            final org.mtr.core.simulation.Simulator simulator = cn.jstxjf_.rte.bridge.MtrBridge.simulator(cn.jstxjf_.rte.compat.McCompat.serverLevel(player));
            if (simulator != null) {
                rail = simulator.railIdMap.get(railId);
            }
        } catch (Exception ignored) {
        }
        return rail == null || allowedRail(player, rail);
    }

    public static void logRejected(String packet, net.minecraft.server.level.ServerPlayer player) {
        cn.jstxjf_.rte.RTE.LOGGER.warn("[RailEdit] rejected {} from {}", packet,
            player == null ? "?" : player.getGameProfile().getName());
    }
}
