package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.mtr.mod.block.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class PacketPlaceParallelNodes extends RtePacket {

    private final List<long[]> nodes;

    public PacketPlaceParallelNodes(RtePacketReader receiver) {
        final int count = receiver.readInt();
        nodes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            nodes.add(new long[]{receiver.readLong(), receiver.readLong()});
        }
    }

    public PacketPlaceParallelNodes(List<long[]> nodes) {
        this.nodes = nodes;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeInt(nodes.size());
        for (final long[] entry : nodes) {
            sender.writeLong(entry[0]);
            sender.writeLong(entry[1]);
        }
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final net.minecraft.server.level.ServerLevel level = player.serverLevel();
            for (final long[] entry : nodes) {
                final net.minecraft.core.BlockPos nativeTarget = net.minecraft.core.BlockPos.of(entry[0]);
                final net.minecraft.core.BlockPos nativeSource = net.minecraft.core.BlockPos.of(entry[1]);
                final net.minecraft.world.level.block.state.BlockState sourceState = level.getBlockState(nativeSource);
                if (!(sourceState.getBlock() instanceof BlockNode)) {
                    continue;
                }
                final net.minecraft.world.level.block.state.BlockState targetState = level.getBlockState(nativeTarget);
                if (targetState.getBlock() instanceof BlockNode) {
                    continue;
                }
                if (!targetState.isAir() && !targetState.canBeReplaced()) {
                    continue;
                }
                level.setBlockAndUpdate(nativeTarget, sourceState);

                final net.minecraft.world.level.block.entity.BlockEntity sourceEntity =
                    level.getBlockEntity(nativeSource);
                if (sourceEntity != null) {
                    try {
                        final net.minecraft.nbt.CompoundTag data = sourceEntity.saveWithoutMetadata();
                        final net.minecraft.world.level.block.entity.BlockEntity targetEntity =
                            level.getBlockEntity(nativeTarget);
                        if (targetEntity != null) {
                            targetEntity.load(data);
                            targetEntity.setChanged();
                            level.sendBlockUpdated(nativeTarget, sourceState, sourceState, 3);
                        } else {
                        }
                    } catch (Exception e) {
                    }
                } else {
                }
            }
        });
    }

    public static void sendPlaceC2S(List<long[]> nodes) {
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketPlaceParallelNodes(nodes));
    }
}
