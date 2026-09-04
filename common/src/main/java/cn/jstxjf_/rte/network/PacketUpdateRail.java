package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Rail;
import org.mtr.core.serializer.JsonReader;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.com.google.gson.JsonParser;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.InitClient;
import org.mtr.mod.packet.PacketDeleteData;
import org.mtr.mod.packet.PacketUpdateData;

import java.util.LinkedHashSet;
import java.util.Set;

public class PacketUpdateRail extends PacketHandler {

    private final String railJson;
    private final String railReverseJson;
    private final String oldRailId;
    private final String oldRailReverseId;

    public PacketUpdateRail(PacketBufferReceiver receiver) {
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
    public void write(PacketBufferSender sender) {
        sender.writeString(railJson);
        sender.writeString(railReverseJson);
        sender.writeString(oldRailId);
        sender.writeString(oldRailReverseId);
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            try {
                Rail rail = new Rail(new JsonReader(JsonParser.parseString(railJson)));
                Rail railReverse = new Rail(new JsonReader(JsonParser.parseString(railReverseJson)));
                final ServerWorld serverWorld = player == null
                    ? server.getOverworld()
                    : new ServerWorld(((net.minecraft.server.level.ServerPlayer) player.data).serverLevel());
                Set<String> deletedRailIds = new LinkedHashSet<>();
                if (!oldRailId.isEmpty() && !oldRailId.equals(rail.getHexId()) && deletedRailIds.add(oldRailId)) {
                    PacketDeleteData.sendDirectlyToServerRailId(serverWorld, oldRailId);
                }
                if (!oldRailReverseId.isEmpty() && !oldRailReverseId.equals(railReverse.getHexId()) && deletedRailIds.add(oldRailReverseId)) {
                    PacketDeleteData.sendDirectlyToServerRailId(serverWorld, oldRailReverseId);
                }
                PacketUpdateData.sendDirectlyToServerRail(serverWorld, rail);
                if (!rail.getHexId().equals(railReverse.getHexId())) {
                    PacketUpdateData.sendDirectlyToServerRail(serverWorld, railReverse);
                }
                rte$markNodesConnected(serverWorld, rail);
            } catch (Exception e) {
                RTE.LOGGER.error("Failed applying updated rail", e);
            }
        });
    }

    private static void rte$markNodesConnected(ServerWorld serverWorld, Rail rail) {
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
            return;
        }
        final net.minecraft.server.level.ServerLevel level =
            (net.minecraft.server.level.ServerLevel) serverWorld.data;
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
            final double preciseRad1 = sameOrder ? sourceRad1 : wrapRadians(sourceRad2 + Math.PI);
            final double preciseRad2 = sameOrder ? sourceRad2 : wrapRadians(sourceRad1 + Math.PI);
            rebuiltExtra.setExtraAngle1Rad(preciseRad1);
            rebuiltExtra.setExtraAngle2Rad(preciseRad2);
            final int pathMode = origExtra.getPathMode();
            rebuiltExtra.setPathModeRaw(pathMode);
            final cn.jstxjf_.rte.data.BezierCurve sourceCurve = origExtra.getBezierCurve();
            if (sourceCurve != null) {
                rebuiltExtra.setBezier(sourceCurve);
            }
            if (Double.isFinite(preciseRad1) && Double.isFinite(preciseRad2)) {
                cn.jstxjf_.rte.data.RailAngleExtra.rebuildRailMath(rebuilt, preciseRad1, preciseRad2);
            }
        }
        sendUpdateC2S(source, rebuilt);
    }

    private static double wrapRadians(double rad) {
        rad = rad % (2 * Math.PI);
        if (rad <= -Math.PI) {
            rad += 2 * Math.PI;
        } else if (rad > Math.PI) {
            rad -= 2 * Math.PI;
        }
        return rad;
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
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketUpdateRail(original, rebuilt));
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
