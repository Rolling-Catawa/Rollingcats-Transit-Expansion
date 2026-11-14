package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.simulation.Simulator;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.World;
import org.mtr.mod.packet.PacketUpdateData;

import java.util.ArrayList;

public final class RailRepairUtil {

    private static final int SWEEP_INTERVAL_TICKS = 600;
    private static final java.util.Map<String, Integer> SWEEP_TICKS = new java.util.HashMap<>();

    private RailRepairUtil() {
    }

    public static void autoSweep(ServerWorld serverWorld) {
        final net.minecraft.server.level.ServerLevel nativeLevel = (net.minecraft.server.level.ServerLevel) serverWorld.data;
        final String dimensionKey = nativeLevel.dimension().location().toString();
        final int tick = SWEEP_TICKS.merge(dimensionKey, 1, Integer::sum);
        if (tick == 1) {
            RTE.LOGGER.info("[RailRepair] auto sweep active dimension={}", dimensionKey);
        }
        if (tick % SWEEP_INTERVAL_TICKS != 0) return;

        final World world = new World(nativeLevel);
        final Simulator simulator;
        try {
            simulator = BlockDirectNode.BlockEntityDirectNode.findSimulator(world);
        } catch (Exception e) {
            return;
        }
        if (simulator == null) return;

        for (final Rail rail : new ArrayList<>(simulator.rails)) {
            if (!(((Object) rail) instanceof RailExtraSupplier extra)) continue;
            final Position p1 = extra.getExtraPosition1();
            final Position p2 = extra.getExtraPosition2();
            if (p1 == null || p2 == null) continue;

            if (p1.getX() == p2.getX() && p1.getY() == p2.getY() && p1.getZ() == p2.getZ()) {
                RTE.LOGGER.warn("[RailRepair] zero-length rail detected id={} platform={} siding={} (run /rte repairrails to clean)",
                    rail.getHexId(), rail.isPlatform(), rail.isSiding());
            }
        }
    }

    public static java.util.List<Rail> findDanglingRails(net.minecraft.server.level.ServerLevel nativeLevel) {
        final Simulator simulator;
        try {
            simulator = BlockDirectNode.BlockEntityDirectNode.findSimulator(new World(nativeLevel));
        } catch (Exception e) {
            RTE.LOGGER.warn("[DanglingRails] simulator lookup failed: {}", e.toString());
            return null;
        }
        if (simulator == null) return null;

        final java.util.List<Rail> dangling = new ArrayList<>();
        for (final Rail rail : new ArrayList<>(simulator.rails)) {
            if (!(((Object) rail) instanceof RailExtraSupplier extra)) continue;
            if (!hasNode(nativeLevel, extra.getExtraPosition1()) || !hasNode(nativeLevel, extra.getExtraPosition2())) {
                dangling.add(rail);
            }
        }
        return dangling;
    }

    public static int cleanDanglingRails(net.minecraft.server.level.ServerLevel nativeLevel) {
        final java.util.List<Rail> dangling = findDanglingRails(nativeLevel);
        if (dangling == null) return -1;
        final ServerWorld serverWorld = new ServerWorld(nativeLevel);
        for (final Rail rail : dangling) {
            final RailExtraSupplier extra = (RailExtraSupplier) (Object) rail;
            RTE.LOGGER.info("[DanglingRails] deleting rail id={} from {} to {}",
                rail.getHexId(), describe(extra.getExtraPosition1()), describe(extra.getExtraPosition2()));
            org.mtr.mod.packet.PacketDeleteData.sendDirectlyToServerRailId(serverWorld, rail.getHexId());
        }
        return dangling.size();
    }

    public static String describe(Position position) {
        return position == null ? "?" : position.getX() + " " + position.getY() + " " + position.getZ();
    }

    private static boolean hasNode(net.minecraft.server.level.ServerLevel level, Position position) {
        return position != null && cn.jstxjf_.rte.bridge.MtrBridge.isNode(level.getBlockState(cn.jstxjf_.rte.bridge.MtrBridge.blockPos(position)));
    }

    public static int fixOneWayDirections(net.minecraft.server.level.ServerLevel nativeLevel) {
        final World world = new World(nativeLevel);
        final ServerWorld serverWorld = new ServerWorld(nativeLevel);
        final Simulator simulator;
        try {
            simulator = BlockDirectNode.BlockEntityDirectNode.findSimulator(world);
        } catch (Exception e) {
            RTE.LOGGER.warn("[OneWayFix] simulator lookup failed: {}", e.toString());
            return -1;
        }
        if (simulator == null) return -1;

        int fixed = 0;
        for (final Rail rail : new ArrayList<>(simulator.rails)) {
            if (!cn.jstxjf_.rte.data.RailAngleExtra.swapOneWaySpeeds(rail)) continue;
            PacketUpdateData.sendDirectlyToServerRail(serverWorld, rail);
            fixed++;
        }
        RTE.LOGGER.info("[OneWayFix] swapped {} one-way rails (kph+mpm synced)", fixed);
        return fixed;
    }

    public static int repairAll(net.minecraft.server.level.ServerLevel nativeLevel) {
        final World world = new World(nativeLevel);
        final ServerWorld serverWorld = new ServerWorld(nativeLevel);
        final Simulator simulator;
        try {
            simulator = BlockDirectNode.BlockEntityDirectNode.findSimulator(world);
        } catch (Exception e) {
            RTE.LOGGER.warn("[RailRepair] simulator lookup failed: {}", e.toString());
            return -1;
        }
        if (simulator == null) {
            return -1;
        }

        int repaired = 0;
        for (final Rail rail : new ArrayList<>(simulator.rails)) {
            if (!(((Object) rail) instanceof RailExtraSupplier extra)) continue;
            final int mode = extra.getPathMode();
            if (mode != RailExtraSupplier.PATH_MODE_ORIGINAL || extra.getBezierCurve() != null) continue;
            final Position p1 = extra.getExtraPosition1();
            final Position p2 = extra.getExtraPosition2();
            if (p1 == null || p2 == null) continue;

            final double straight = Math.sqrt(
                Math.pow(p2.getX() - p1.getX(), 2)
                + Math.pow(p2.getY() - p1.getY(), 2)
                + Math.pow(p2.getZ() - p1.getZ(), 2));

            if (straight < 0.01 && !rail.isPlatform() && !rail.isSiding()
                && (!Double.isFinite(rail.railMath.getLength()) || rail.railMath.getLength() < 0.01)) {
                RTE.LOGGER.info("[RailRepair] deleting zero-length rail id={}", rail.getHexId());
                org.mtr.mod.packet.PacketDeleteData.sendDirectlyToServerRailId(serverWorld, rail.getHexId());
                continue;
            }

            final double curLen = rail.railMath.getLength();
            final boolean giantCircle = !Double.isFinite(curLen) || curLen > straight * 3.0 + 8.0;
            if (!giantCircle) continue;

            final Rail rebuilt;
            try {
                rebuilt = RailRebuildHelper.rebuildFromCurrentState(rail);
            } catch (Exception e) {
                RTE.LOGGER.warn("[RailRepair] rebuild failed for {}: {}", rail.getHexId(), e.toString());
                continue;
            }
            if (rebuilt == null || rebuilt == rail || !rebuilt.isValid()) continue;
            RTE.LOGGER.info("[RailRepair] fixed giant-circle rail id={} oldLen={} newLen={} straight={}",
                rail.getHexId(), String.format("%.1f", curLen),
                String.format("%.1f", rebuilt.railMath.getLength()), String.format("%.1f", straight));
            PacketUpdateData.sendDirectlyToServerRail(serverWorld, rebuilt);
            repaired++;
        }
        RTE.LOGGER.info("[RailRepair] repaired {} rails", repaired);
        return repaired;
    }
}
