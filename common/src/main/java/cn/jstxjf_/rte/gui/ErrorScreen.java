package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.gui.imgui.ImGuiErrorScreen;
import cn.jstxjf_.rte.gui.imgui.RteImGuiHost;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

public final class ErrorScreen {

    private ErrorScreen() {
    }

    public static void show(net.minecraft.client.Minecraft client, List<String> errorList) {
        if (errorList.isEmpty()) {
            return;
        }
        final Screen screen = createScreen(errorList, client.screen);
        if (screen != null) {
            client.execute(() -> client.setScreen(screen));
        }
    }

    public static Screen createScreen(List<String> errorList, Screen parent) {
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent;
        }
        return new ImGuiErrorScreen(errorList, parent);
    }
}
