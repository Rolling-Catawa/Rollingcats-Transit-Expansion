package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.block.BlockEyeCandy.BlockEntityEyeCandy;
import cn.jstxjf_.rte.gui.imgui.ImGuiEyeCandyScreen;
import cn.jstxjf_.rte.gui.imgui.RteImGuiHost;
import cn.jstxjf_.rte.item.BlockItemEyeCandy;
import cn.jstxjf_.rte.network.PacketUpdateHoldingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mtr.mapping.holder.Hand;

import java.util.function.Supplier;

public class EyeCandyScreen {

    public static boolean open(InteractionHand hand, Screen parent) {
        final Screen screen = createScreen(hand, parent);
        if (screen == null) {
            return false;
        }
        net.minecraft.client.Minecraft.getInstance().setScreen(screen);
        return true;
    }

    public static boolean open(BlockPos blockPos, Screen parent) {
        final Screen screen = createScreen(blockPos, parent);
        if (screen == null) {
            return false;
        }
        net.minecraft.client.Minecraft.getInstance().setScreen(screen);
        return true;
    }

    public static Screen createScreen(InteractionHand hand, Screen parent) {
        ItemStack itemStack = Minecraft.getInstance().player.getItemInHand(hand);
        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof BlockItemEyeCandy)) {
            return parent;
        }
        VirtualEyeCandy virtualEyeCandy = new VirtualEyeCandy(
            () -> Minecraft.getInstance().player.getItemInHand(hand).getOrCreateTagElement("BlockEntityTag"), hand);
        return createScreen(virtualEyeCandy, parent);
    }

    public static Screen createScreen(BlockPos blockPos, Screen parent) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return parent;
        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof BlockEntityEyeCandy blockEntity) {
            return createScreen(blockEntity, parent);
        }
        return parent;
    }

    public static Screen createScreen(BlockEntityEyeCandy blockEntity, Screen parent) {
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent;
        }
        return new ImGuiEyeCandyScreen(blockEntity, parent);
    }

    private static class VirtualEyeCandy extends BlockEntityEyeCandy {
        private final InteractionHand hand;
        private final Supplier<CompoundTag> tagSupplier;

        public VirtualEyeCandy(Supplier<CompoundTag> tagSupplier, InteractionHand hand) {
            super(new org.mtr.mapping.holder.BlockPos(0, -1145141919, 0),
                new org.mtr.mapping.holder.BlockState(
                    cn.jstxjf_.rte.block.RTEBlocks.EYE_CANDY.get().data.defaultBlockState()));
            this.hand = hand;
            readCompoundTag(new org.mtr.mapping.holder.CompoundTag(tagSupplier.get()));
            this.tagSupplier = tagSupplier;
        }

        @Override
        public void sendUpdateC2S() {
            writeCompoundTag(new org.mtr.mapping.holder.CompoundTag(tagSupplier.get()));
            org.mtr.mod.InitClient.REGISTRY_CLIENT.sendPacketToServer(
                new PacketUpdateHoldingItem(hand == InteractionHand.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND));
        }
    }
}
