package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.block.BlockEyeCandy;
import net.minecraft.nbt.TagParser;
import org.mtr.mapping.holder.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PacketPlaceTrackside extends RtePacket {

    private final long anchorPos;
    private final List<Object[]> placements;

    public PacketPlaceTrackside(RtePacketReader receiver) {
        anchorPos = receiver.readLong();
        final int count = receiver.readInt();
        placements = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            placements.add(new Object[]{receiver.readLong(), receiver.readString()});
        }
    }

    public PacketPlaceTrackside(long anchorPos, List<Object[]> placements) {
        this.anchorPos = anchorPos;
        this.placements = placements;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeLong(anchorPos);
        sender.writeInt(placements.size());
        for (final Object[] entry : placements) {
            sender.writeLong((Long) entry[0]);
            sender.writeString((String) entry[1]);
        }
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final net.minecraft.server.level.ServerLevel level = cn.jstxjf_.rte.compat.McCompat.serverLevel(player);
            final net.minecraft.core.BlockPos nativeAnchor = net.minecraft.core.BlockPos.of(anchorPos);
            final net.minecraft.world.level.block.state.BlockState anchorState = level.getBlockState(nativeAnchor);
            if (!(anchorState.getBlock() instanceof BlockEyeCandy)) {
                return;
            }

            for (final Object[] entry : placements) {
                final net.minecraft.core.BlockPos nativeTarget = net.minecraft.core.BlockPos.of((Long) entry[0]);
                final net.minecraft.world.level.block.state.BlockState existing = level.getBlockState(nativeTarget);
                if (!existing.isAir() && !cn.jstxjf_.rte.compat.McCompat.canBeReplaced(existing) && !(existing.getBlock() instanceof BlockEyeCandy)) {
                    continue;
                }
                final net.minecraft.nbt.CompoundTag nativeTag;
                try {
                    nativeTag = TagParser.parseTag((String) entry[1]);
                } catch (Exception e) {
                    continue;
                }
                level.setBlockAndUpdate(nativeTarget, anchorState);
                final net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(nativeTarget);
                if (blockEntity instanceof BlockEyeCandy.BlockEntityEyeCandy eyeCandy) {
                    eyeCandy.readCompoundTag(new CompoundTag(nativeTag));
                    blockEntity.setChanged();
                    level.sendBlockUpdated(nativeTarget, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                }
            }
        });
    }

    public static void sendPlaceC2S(long anchorPos, List<Object[]> placements) {
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketPlaceTrackside(anchorPos, placements));
    }
}
