package cn.jstxjf_.rte.gui.imgui;

import java.util.ArrayList;
import java.util.List;

public final class ImGuiWorkspace {

    public interface Panel {
        void drawPanel();
    }

    private static final List<Panel> PANELS = new ArrayList<>();

    private ImGuiWorkspace() {
    }

    public static void open(Panel panel) {
        if (panel == null || PANELS.contains(panel)) {
            return;
        }
        PANELS.add(panel);
    }

    public static void close(Panel panel) {
        PANELS.remove(panel);
    }

    public static void closeAll() {
        PANELS.clear();
    }

    public static boolean isEmpty() {
        return PANELS.isEmpty();
    }

    public static int count() {
        return PANELS.size();
    }

    public static Panel topmost() {
        return PANELS.isEmpty() ? null : PANELS.get(PANELS.size() - 1);
    }

    public static boolean activateRemaining(net.minecraft.client.Minecraft minecraft, Panel self) {
        close(self);
        final Panel remaining = topmost();
        if (minecraft != null && remaining instanceof net.minecraft.client.gui.screens.Screen screen && remaining != self) {
            minecraft.setScreen(screen);
            return true;
        }
        closeAll();
        return false;
    }

    public static void drawAll() {
        if (!PANELS.isEmpty()) {
            imgui.ImGui.dockSpaceOverViewport(
                imgui.ImGui.getMainViewport(),
                imgui.flag.ImGuiDockNodeFlags.PassthruCentralNode);
        }
        for (final Panel panel : new ArrayList<>(PANELS)) {
            panel.drawPanel();
        }
    }
}
