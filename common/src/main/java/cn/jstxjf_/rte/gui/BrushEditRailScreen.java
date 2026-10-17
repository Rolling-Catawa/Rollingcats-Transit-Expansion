package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.gui.imgui.ImGuiBrushEditRailScreen;
import cn.jstxjf_.rte.gui.imgui.RteImGuiHost;
import cn.jstxjf_.rte.render.RailPicker;
import org.mtr.core.data.Rail;
import net.minecraft.core.BlockPos;
import org.mtr.mapping.holder.Screen;

public final class BrushEditRailScreen {

    private BrushEditRailScreen() {
    }

    public static Screen createScreen(Screen parent) {
        return createScreen(RailPicker.pickedRail, RailPicker.pickedPosStart, RailPicker.pickedPosEnd, parent);
    }

    public static Screen createScreen(Rail rail, BlockPos posStart, BlockPos posEnd, Screen parent) {
        if (rail == null) {
            return parent;
        }
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent;
        }
        return new Screen(new ImGuiBrushEditRailScreen(rail, posStart, posEnd, parent));
    }
}
