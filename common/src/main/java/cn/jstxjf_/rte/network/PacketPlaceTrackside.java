package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.block.BlockEyeCandy;
import net.minecraft.nbt.TagParser;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.InitClient;

import java.util.ArrayList;
import java.util.List;

public class PacketPlaceTrackside extends PacketHandler {

    private final long anchorPos;
    private final List<Object[]> placements;

    public PacketPlaceTrackside(PacketBufferReceiver receiver) {
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
    public void write(PacketBufferSender sender) {
        sender.writeLong(anchorPos);
        sender.writeInt(placements.size());
        for (final Object[] entry : placements) {
            sender.writeLong((Long) entry[0]);
            sender.writeString((String) entry[1]);
        }
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final net.minecraft.server.level.ServerLevel level = ((net.minecraft.server.level.ServerPlayer) player.data).serverLevel();
            final BlockPos anchor = BlockPos.fromLong(anchorPos);
            final net.minecraft.core.BlockPos nativeAnchor = new net.minecraft.core.BlockPos(anchor.getX(), anchor.getY(), anchor.getZ());
            final net.minecraft.world.level.block.state.BlockState anchorState = level.getBlockState(nativeAnchor);
            if (!(anchorState.getBlock() instanceof BlockEyeCandy)) {
                return;
            }

            for (final Object[] entry : placements) {
                final BlockPos targetPos = BlockPos.fromLong((Long) entry[0]);
                final net.minecraft.core.BlockPos nativeTarget = new net.minecraft.core.BlockPos(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                final net.minecraft.world.level.block.state.BlockState existing = level.getBlockState(nativeTarget);
                if (!existing.isAir() && !existing.canBeReplaced() && !(existing.getBlock() instanceof BlockEyeCandy)) {
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
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketPlaceTrackside(anchorPos, placements));
    }
}
