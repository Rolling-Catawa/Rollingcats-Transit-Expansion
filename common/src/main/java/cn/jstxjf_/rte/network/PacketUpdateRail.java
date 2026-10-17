package cn.jstxjf_.rte.network;

import net.minecraft.server.level.ServerPlayer;
import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Rail;
import org.mtr.core.serializer.JsonReader;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.com.google.gson.JsonParser;

import java.util.LinkedHashSet;
import java.util.Set;

public class PacketUpdateRail extends RtePacket {

    private final String railJson;
    private final String railReverseJson;
    private final String oldRailId;
    private final String oldRailReverseId;

    public PacketUpdateRail(RtePacketReader receiver) {
        this.railJson = receiver.readString();
        this.railReverseJson = receiver.readString();
        this.oldRailId = receiver.readString();
        this.oldRailReverseId = receiver.readString();
    }

    public PacketUpdateRail(Rail original, Rail rebuilt) {
        Rail originalReverse = ((RailExtraSupplier)(Object)original).getTransposition();
        Rail rebuiltReverse = ((RailExtraSupplier)(Object)rebuilt).getTransposition();
        this.railJson = Utilities.getJsonObjectFromData(rebuilt).toString();
        this.railReverseJson = Utilities.getJsonObjectFromData(rebuiltReverse).toString();
        this.oldRailId = original.getHexId();
        this.oldRailReverseId = originalReverse.getHexId();
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeString(railJson);
        sender.writeString(railReverseJson);
        sender.writeString(oldRailId);
        sender.writeString(oldRailReverseId);
    }

    @Override
    protected void handleServer(net.minecraft.server.MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            try {
                Rail rail = new Rail(new JsonReader(JsonParser.parseString(railJson)));
                Rail railReverse = new Rail(new JsonReader(JsonParser.parseString(railReverseJson)));
                final net.minecraft.server.level.ServerLevel level = player == null ? server.overworld() : player.serverLevel();
                Set<String> deletedRailIds = new LinkedHashSet<>();
                if (!oldRailId.isEmpty() && !oldRailId.equals(rail.getHexId()) && deletedRailIds.add(oldRailId)) {
                    cn.jstxjf_.rte.bridge.MtrBridge.deleteRailOnServer(level, oldRailId);
                }
                if (!oldRailReverseId.isEmpty() && !oldRailReverseId.equals(railReverse.getHexId()) && deletedRailIds.add(oldRailReverseId)) {
                    cn.jstxjf_.rte.bridge.MtrBridge.deleteRailOnServer(level, oldRailReverseId);
                }
                cn.jstxjf_.rte.bridge.MtrBridge.sendRailToServer(level, rail);
                if (!rail.getHexId().equals(railReverse.getHexId())) {
                    cn.jstxjf_.rte.bridge.MtrBridge.sendRailToServer(level, railReverse);
                }
                rte$markNodesConnected(level, rail);
            } catch (Exception e) {
                RTE.LOGGER.error("Failed applying updated rail", e);
            }
        });
    }

    private static void rte$markNodesConnected(net.minecraft.server.level.ServerLevel level, Rail rail) {
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
            return;
        }
        for (final org.mtr.core.data.Position position :
            new org.mtr.core.data.Position[]{extra.getExtraPosition1(), extra.getExtraPosition2()}) {
            if (position == null) {
                continue;
            }
            try {
                final net.minecraft.core.BlockPos blockPos = new net.minecraft.core.BlockPos(
                    (int) position.getX(), (int) position.getY(), (int) position.getZ());
                final net.minecraft.world.level.block.state.BlockState state = level.getBlockState(blockPos);
                if (!(state.getBlock() instanceof org.mtr.mod.block.BlockNode)) {
                    continue;
                }
                if (Boolean.TRUE.equals(state.getValue(org.mtr.mod.block.BlockNode.IS_CONNECTED.data))) {
                    continue;
                }
                level.setBlockAndUpdate(blockPos,
                    state.setValue(org.mtr.mod.block.BlockNode.IS_CONNECTED.data, Boolean.TRUE));
            } catch (Exception ignored) {
            }
        }
    }

    public static void sendUpdateC2S(Rail rail) {
        Rail source = resolveCurrentRail(rail);
        Rail rebuilt = RailRebuildHelper.rebuildFromCurrentState(source);
        if (((Object)source) instanceof RailExtraSupplier origExtra && ((Object)rebuilt) instanceof RailExtraSupplier rebuiltExtra) {
            final double sourceRad1 = origExtra.getExtraAngle1Rad();
            final double sourceRad2 = origExtra.getExtraAngle2Rad();
            final org.mtr.core.data.Position sourceP1 = origExtra.getExtraPosition1();
            final org.mtr.core.data.Position rebuiltP1 = rebuiltExtra.getExtraPosition1();
            final boolean sameOrder = sourceP1 == null || rebuiltP1 == null
                || (sourceP1.getX() == rebuiltP1.getX() && sourceP1.getY() == rebuiltP1.getY()
                    && sourceP1.getZ() == rebuiltP1.getZ());
            final double preciseRad1 = sameOrder ? sourceRad1 : sourceRad2;
            final double preciseRad2 = sameOrder ? sourceRad2 : sourceRad1;
            rebuiltExtra.setExtraAngle1Rad(preciseRad1);
            rebuiltExtra.setExtraAngle2Rad(preciseRad2);
            final int pathMode = origExtra.getPathMode();
            rebuiltExtra.setPathModeRaw(pathMode);
            final cn.jstxjf_.rte.data.BezierCurve sourceCurve = origExtra.getBezierCurve();
            if (sourceCurve != null) {
                rebuiltExtra.setBezier(sameOrder ? sourceCurve : sourceCurve.getTransposition());
            }
            if (Double.isFinite(preciseRad1) && Double.isFinite(preciseRad2)) {
                cn.jstxjf_.rte.data.RailAngleExtra.rebuildRailMath(rebuilt, preciseRad1, preciseRad2);
            }
        }
        sendUpdateC2S(source, rebuilt);
    }

    public static void sendUpdateC2S(Rail original, Rail rebuilt) {
        if (original == null || rebuilt == null) {
            return;
        }
        try {
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(original.getHexId());
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(((RailExtraSupplier)(Object)original).getTransposition().getHexId());
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(rebuilt.getHexId());
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(((RailExtraSupplier)(Object)rebuilt).getTransposition().getHexId());
        } catch (Exception ignored) {}
        RailRebuildHelper.replaceClientRail(rebuilt);
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketUpdateRail(original, rebuilt));
    }

    private static Rail resolveCurrentRail(Rail rail) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
            return rail;
        }
        final org.mtr.core.data.Position position1 = extra.getExtraPosition1();
        final org.mtr.core.data.Position position2 = extra.getExtraPosition2();
        if (position1 == null || position2 == null) {
            return rail;
        }
        final Rail resolved = RailRebuildHelper.findClientRail(position1, position2);
        return resolved == null ? rail : resolved;
    }
}
