package cn.jstxjf_.rte.network;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockNode;

import java.util.ArrayList;
import java.util.List;

public class PacketPlaceParallelNodes extends PacketHandler {

    private final List<long[]> nodes;

    public PacketPlaceParallelNodes(PacketBufferReceiver receiver) {
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
    public void write(PacketBufferSender sender) {
        sender.writeInt(nodes.size());
        for (final long[] entry : nodes) {
            sender.writeLong(entry[0]);
            sender.writeLong(entry[1]);
        }
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final net.minecraft.server.level.ServerLevel level = ((net.minecraft.server.level.ServerPlayer) player.data).serverLevel();
            for (final long[] entry : nodes) {
                final BlockPos targetPos = BlockPos.fromLong(entry[0]);
                final BlockPos sourcePos = BlockPos.fromLong(entry[1]);
                final net.minecraft.core.BlockPos nativeTarget = new net.minecraft.core.BlockPos(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                final net.minecraft.core.BlockPos nativeSource = new net.minecraft.core.BlockPos(sourcePos.getX(), sourcePos.getY(), sourcePos.getZ());
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
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketPlaceParallelNodes(nodes));
    }
}
