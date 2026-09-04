package cn.jstxjf_.rte.gui.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ImGuiErrorScreen extends ImGuiScreenBase {

    private final List<String> errorList;

    public ImGuiErrorScreen(List<String> errorList, Screen parent) {
        super(Component.translatable("gui.rte.error.title"), parent);
        this.errorList = errorList;
    }

    @Override
    protected void buildWindow() {
        ImGui.setNextWindowSize(640, 400, ImGuiCond.FirstUseEver);
        ImGui.begin(title.getString() + "###rte_error_screen");
        ImGui.textWrapped(t("gui.rte.error.explain"));
        ImGui.separator();
        ImGui.beginChild("###error_list", 0, -32, true);
        for (final String error : errorList) {
            ImGui.textColored(0.95f, 0.45f, 0.4f, 1f, "• ");
            ImGui.sameLine();
            ImGui.textWrapped(error);
            ImGui.spacing();
        }
        ImGui.endChild();
        if (ImGui.button(t("gui.rte.imgui.back") + "###error_back")) {
            onClose();
        }
        ImGui.end();
    }
}
