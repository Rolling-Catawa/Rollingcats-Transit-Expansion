package cn.jstxjf_.rte.client.gui;

import cn.jstxjf_.rte.gui.imgui.ImGuiConfigScreen;
import cn.jstxjf_.rte.gui.imgui.RteImGuiHost;
import net.minecraft.client.gui.screens.Screen;

public final class RTEConfigScreen {

    private RTEConfigScreen() {
    }

    public static Screen createScreen(Screen parent) {
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent;
        }
        return new ImGuiConfigScreen(parent);
    }
}
