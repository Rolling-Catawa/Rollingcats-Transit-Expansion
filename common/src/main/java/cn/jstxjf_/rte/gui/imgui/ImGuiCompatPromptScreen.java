package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.util.CompatArbiter;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import net.minecraft.network.chat.Component;

public class ImGuiCompatPromptScreen extends ImGuiScreenBase {

    private static final String[] MOD_IDS = {
        CompatArbiter.MOD_JCM,
        CompatArbiter.MOD_FANGSU,
        CompatArbiter.MOD_S1,
    };

    public ImGuiCompatPromptScreen() {
        super(Component.translatable("gui.rte.compat.title"), null);
    }

    public ImGuiCompatPromptScreen(net.minecraft.client.gui.screens.Screen parent) {
        super(Component.translatable("gui.rte.compat.title"), parent);
    }

    @Override
    protected void buildWindow() {
        ImGui.setNextWindowPos(
            ImGui.getIO().getDisplaySizeX() / 2f - 320,
            ImGui.getIO().getDisplaySizeY() / 2f - 200, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(640, 0, ImGuiCond.FirstUseEver);
        ImGui.begin(t("gui.rte.compat.title") + "###rte_compat_prompt");

        ImGui.textWrapped(t("gui.rte.compat.body"));
        ImGui.spacing();

        ImGui.text(t("gui.rte.compat.detected"));
        boolean any = false;
        for (final String modId : MOD_IDS) {
            if (CompatArbiter.isModLoaded(modId)) {
                any = true;
                ImGui.bulletText(t("gui.rte.compat.mod." + modId));
            }
        }
        if (!any) {
            ImGui.textDisabled(t("gui.rte.compat.none"));
        }
        ImGui.separator();

        for (final CompatArbiter.Domain domain : CompatArbiter.Domain.values()) {
            if (!CompatArbiter.hasRival(domain)) {
                continue;
            }
            renderDomain(domain);
            ImGui.separator();
        }

        if (ImGui.button(t("gui.rte.compat.use_defaults") + "###compat_defaults")) {
            CompatArbiter.applyDefaults();
            commit();
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.compat.confirm") + "###compat_confirm")) {
            commit();
        }
        ImGui.textDisabled(t("gui.rte.compat.hint"));
        ImGui.end();
    }

    private void renderDomain(CompatArbiter.Domain domain) {
        ImGui.text(t("gui.rte.compat.domain." + domain.key));
        ImGui.textDisabled("  " + t("gui.rte.compat.domain." + domain.key + ".desc"));

        final String current = CompatArbiter.ownerOf(domain);
        renderOwnerRow(domain, null, current);

        final java.util.List<String> loadedRivals = new java.util.ArrayList<>();
        for (final String modId : domain.rivals()) {
            if (CompatArbiter.isModLoaded(modId)) {
                loadedRivals.add(modId);
            }
        }
        if (loadedRivals.size() < 2 && !CompatArbiter.hasPerModChoice(domain)) {
            return;
        }
        if (ImGui.treeNode(t("gui.rte.compat.per_mod") + "###" + domain.key + "_permod")) {
            for (final String modId : loadedRivals) {
                ImGui.text("  " + t("gui.rte.compat.mod." + modId));
                renderOwnerRow(domain, modId, CompatArbiter.ownerOf(domain, modId));
            }
            ImGui.treePop();
        }
    }

    private void renderOwnerRow(CompatArbiter.Domain domain, String modId, String current) {
        final boolean scripted = domain == CompatArbiter.Domain.SCRIPT_VEHICLE
            || domain == CompatArbiter.Domain.SCRIPT_EYECANDY;
        if (scripted) {
            option(domain, modId, CompatArbiter.OWNER_DEDUPE, current, "gui.rte.compat.owner.dedupe");
            ImGui.sameLine();
            option(domain, modId, CompatArbiter.OWNER_BOTH, current, "gui.rte.compat.owner.both");
            ImGui.sameLine();
            option(domain, modId, CompatArbiter.OWNER_OTHER, current, "gui.rte.compat.owner.other");
        } else {
            option(domain, modId, CompatArbiter.OWNER_RTE, current, "gui.rte.compat.owner.rte");
            ImGui.sameLine();
            option(domain, modId, CompatArbiter.OWNER_OTHER, current, "gui.rte.compat.owner.other");
        }
    }

    private void option(CompatArbiter.Domain domain, String modId, String owner, String current, String labelKey) {
        final boolean selected = owner.equals(current);
        final String label = (selected ? "* " : "") + t(labelKey)
            + "###" + domain.key + "_" + (modId == null ? "all" : modId) + "_" + owner;
        if (ImGui.button(label)) {
            if (modId == null) {
                CompatArbiter.setOwner(domain, owner);
            } else {
                CompatArbiter.setOwner(domain, modId, owner);
            }
        }
    }

    private void commit() {
        CompatArbiter.syncVehicleChoiceToLegacy();
        ClientConfig.compatAsked = true;
        ClientConfig.save();
        onClose();
        if (minecraft != null) {
            minecraft.execute(minecraft::reloadResourcePacks);
        }
    }
}
