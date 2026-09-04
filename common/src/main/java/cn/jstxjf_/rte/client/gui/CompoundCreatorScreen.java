package cn.jstxjf_.rte.client.gui;

import cn.jstxjf_.rte.gui.imgui.ImGuiCompoundCreatorScreen;
import cn.jstxjf_.rte.gui.imgui.RteImGuiHost;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.Screen;

public final class CompoundCreatorScreen {

    private CompoundCreatorScreen() {
    }

    public static Screen createScreen(Screen parent, Hand hand) {
        return createScreen(parent, hand, 0, 0);
    }

    public static Screen createScreen(Screen parent, Hand hand, int selectedIndex, int scroll) {
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent;
        }
        return new Screen(new ImGuiCompoundCreatorScreen(parent == null ? null : parent.data, hand, selectedIndex));
    }
}
