package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.block.BlockEyeCandy;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.InitClient;

public class PacketUpdateBlockEntity extends PacketHandler {

    private final BlockPos pos;
    private final boolean hasTag;
    private final String tagString;

    public PacketUpdateBlockEntity(PacketBufferReceiver receiver) {
        pos = BlockPos.fromLong(receiver.readLong());
        hasTag = receiver.readBoolean();
        tagString = hasTag ? receiver.readString() : "";
    }

    public PacketUpdateBlockEntity(BlockEntityExtension blockEntity) {
        final net.minecraft.world.level.block.entity.BlockEntity nativeBlockEntity = (net.minecraft.world.level.block.entity.BlockEntity) (Object) blockEntity;
        final CompoundTag tag = new CompoundTag();
        blockEntity.writeCompoundTag(tag);
        pos = new BlockPos(nativeBlockEntity.getBlockPos());
        hasTag = !tag.data.isEmpty();
        tagString = hasTag ? tag.data.toString() : "";
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeLong(pos.asLong());
        sender.writeBoolean(hasTag);
        if (hasTag) {
            sender.writeString(tagString);
        }
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            final net.minecraft.server.level.ServerPlayer nativePlayer = (net.minecraft.server.level.ServerPlayer) player.data;
            final BlockEntity blockEntity = nativePlayer.serverLevel().getBlockEntity(new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ()));
            if (blockEntity == null) {
                return;
            }

            final net.minecraft.nbt.CompoundTag nativeTag;
            try {
                nativeTag = hasTag ? TagParser.parseTag(tagString) : new net.minecraft.nbt.CompoundTag();
            } catch (Exception e) {
                return;
            }

            if (blockEntity instanceof BlockEyeCandy.BlockEntityEyeCandy eyeCandy) {
                eyeCandy.readCompoundTag(new CompoundTag(nativeTag));
                final BlockState state = eyeCandy.getCachedState2();
                if (state != null && state.data.getBlock() instanceof cn.jstxjf_.rte.block.BlockEyeCandy) {
                    final int newLightLevel = eyeCandy.getLightLevel();
                    final int oldLightLevel = state.data.getValue(BlockEyeCandy.LEVEL.data);
                    if (newLightLevel != oldLightLevel) {
                        nativePlayer.serverLevel().setBlock(new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ()), state.data.setValue(BlockEyeCandy.LEVEL.data, newLightLevel), 2);
                    }
                }
            } else if (blockEntity instanceof BlockDirectNode.BlockEntityDirectNode directNode) {
                directNode.readCompoundTag(new CompoundTag(nativeTag));
            } else {
                blockEntity.load(nativeTag);
            }

            blockEntity.setChanged();
            nativePlayer.serverLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
        });
    }

    public static void sendUpdateC2S(BlockEntityExtension blockEntity) {
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketUpdateBlockEntity(blockEntity));
    }
}
