package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode;
import cn.jstxjf_.rte.gui.imgui.ImGuiDirectNodeScreen;
import cn.jstxjf_.rte.gui.imgui.RteImGuiHost;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

public final class DirectNodeScreen {

    private DirectNodeScreen() {
    }

    public static Screen createScreen(BlockEntityDirectNode entity, Supplier<Screen> parent) {
        return createScreen(entity, parent, null);
    }

    public static Screen createScreen(BlockEntityDirectNode entity, Supplier<Screen> parent, Runnable onAngleChanged) {
        if (entity == null) {
            return parent == null ? null : parent.get();
        }
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent == null ? null : parent.get();
        }
        return new ImGuiDirectNodeScreen(entity, parent == null ? null : parent.get(), onAngleChanged);
    }

    public static Screen createScreen(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, Supplier<Screen> parent) {
        if (world == null) {
            return parent == null ? null : parent.get();
        }
        final net.minecraft.world.level.block.entity.BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof BlockEntityDirectNode directNode ? createScreen(directNode, parent) : (parent == null ? null : parent.get());
    }
}
