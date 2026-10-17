package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.RTE;
import net.minecraft.nbt.TagParser;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PacketUpdateHoldingItem extends RtePacket {

    private final boolean isMainHand;
    private final boolean hasTag;
    private final String tagString;

    public PacketUpdateHoldingItem(RtePacketReader receiver) {
        isMainHand = receiver.readBoolean();
        hasTag = receiver.readBoolean();
        tagString = hasTag ? receiver.readString() : "";
    }

    public PacketUpdateHoldingItem(Hand hand) {
        isMainHand = hand == Hand.MAIN_HAND;
        final org.mtr.mapping.holder.ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
        final ItemStack stack = player == null ? ItemStack.getEmptyMapped() : player.getStackInHand(hand);
        final CompoundTag tag = stack.isEmpty() ? null : stack.getTag();
        hasTag = tag != null && !tag.data.isEmpty();
        tagString = hasTag ? tag.data.toString() : "";
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeBoolean(isMainHand);
        sender.writeBoolean(hasTag);
        if (hasTag) {
            sender.writeString(tagString);
        }
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            final ItemStack stack = new ItemStack(player.getItemInHand(isMainHand ? net.minecraft.world.InteractionHand.MAIN_HAND : net.minecraft.world.InteractionHand.OFF_HAND));
            if (stack.isEmpty()) {
                return;
            }

            try {
                if (hasTag) {
                    final net.minecraft.nbt.CompoundTag incoming = TagParser.parseTag(tagString);
                    final net.minecraft.nbt.CompoundTag existing = stack.data.getOrCreateTag();
                    if (incoming.contains(cn.jstxjf_.rte.util.RailBrushHelper.BRUSH_TAG) || incoming.contains(cn.jstxjf_.rte.util.RailBrushHelper.LEGACY_BRUSH_TAG)) {
                        existing.put(cn.jstxjf_.rte.util.RailBrushHelper.BRUSH_TAG, incoming.contains(cn.jstxjf_.rte.util.RailBrushHelper.BRUSH_TAG) ? incoming.getCompound(cn.jstxjf_.rte.util.RailBrushHelper.BRUSH_TAG) : incoming.getCompound(cn.jstxjf_.rte.util.RailBrushHelper.LEGACY_BRUSH_TAG));
                    }
                    if (incoming.contains("tasks")) {
                        existing.put("tasks", incoming.getCompound("tasks"));
                    }
                } else {
                    final net.minecraft.nbt.CompoundTag existing = stack.data.getTag();
                    if (existing != null) {
                        existing.remove(cn.jstxjf_.rte.util.RailBrushHelper.BRUSH_TAG);
                        existing.remove(cn.jstxjf_.rte.util.RailBrushHelper.LEGACY_BRUSH_TAG);
                        existing.remove("tasks");
                        if (existing.isEmpty()) {
                            stack.data.setTag(null);
                        }
                    }
                }
            } catch (Exception e) {
                RTE.LOGGER.error("Failed updating holding item NBT", e);
            }
        });
    }
}
