package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.RTE;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.TagParser;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public class PacketUpdateHoldingItem extends PacketHandler {

    private final boolean isMainHand;
    private final boolean hasTag;
    private final String tagString;

    public PacketUpdateHoldingItem(PacketBufferReceiver receiver) {
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
    public void write(PacketBufferSender sender) {
        sender.writeBoolean(isMainHand);
        sender.writeBoolean(hasTag);
        if (hasTag) {
            sender.writeString(tagString);
        }
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            final Hand hand = isMainHand ? Hand.MAIN_HAND : Hand.OFF_HAND;
            final ItemStack stack = player.getStackInHand(hand);
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
