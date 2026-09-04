package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.util.JcmCompat;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.network.chat.Component;

public class ImGuiJcmPromptScreen extends ImGuiScreenBase {

    public ImGuiJcmPromptScreen() {
        super(Component.translatable("gui.rte.jcm_prompt.title"), null);
    }

    @Override
    protected void buildWindow() {
        ImGui.setNextWindowPos(imgui.ImGui.getIO().getDisplaySizeX() / 2f - 260, imgui.ImGui.getIO().getDisplaySizeY() / 2f - 120, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(520, 0, ImGuiCond.FirstUseEver);
        ImGui.begin(t("gui.rte.jcm_prompt.title") + "###rte_jcm_prompt");

        ImGui.textWrapped(t("gui.rte.jcm_prompt.body"));
        ImGui.spacing();

        if (ImGui.button(t("gui.rte.jcm_prompt.dedupe") + "###jcm_dedupe")) {
            ClientConfig.jcmJsChoice = JcmCompat.CHOICE_AUTO_DEDUPE;
            ClientConfig.save();
            onClose();
        }
        ImGui.textDisabled(t("gui.rte.jcm_prompt.dedupe_hint"));
        ImGui.separator();

        if (ImGui.button(t("gui.rte.jcm_prompt.disable") + "###jcm_disable")) {
            ClientConfig.jcmJsChoice = JcmCompat.CHOICE_DISABLE_RTE_JS;
            ClientConfig.save();
            onClose();
            minecraft.execute(minecraft::reloadResourcePacks);
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.jcm_prompt.ignore") + "###jcm_ignore")) {
            ClientConfig.jcmJsChoice = JcmCompat.CHOICE_IGNORE;
            ClientConfig.save();
            onClose();
        }
        ImGui.textDisabled(t("gui.rte.jcm_prompt.hint"));
        ImGui.end();
    }
}
