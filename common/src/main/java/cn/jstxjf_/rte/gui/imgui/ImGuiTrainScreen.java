package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.network.PacketUpdateTrainCustomConfigs;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.mtr.core.data.Vehicle;

import java.util.Map;

public class ImGuiTrainScreen extends ImGuiScreenBase {

    private final Object vehicle;
    private final TrainExtraSupplier supplier;
    private final Map<String, String> customConfigs;
    private final Map<String, ConfigResponder> responders;
    private boolean dirty;

    public ImGuiTrainScreen(Object vehicle, Screen parent) {
        super(Component.translatable("gui.rte.train_screen.title"), parent);
        this.vehicle = vehicle;
        this.supplier = (TrainExtraSupplier) vehicle;
        this.customConfigs = supplier.getCustomConfigs();
        this.responders = supplier.getConfigResponders();
    }

    @Override
    protected void buildWindow() {
        ImGui.setNextWindowSize(520, 0, ImGuiCond.FirstUseEver);
        ImGui.begin(title.getString() + "###rte_train_screen");

        if (customConfigs.isEmpty()) {
            ImGui.text(t("gui.rte.custom_config.null"));
        } else {
            dirty |= ImGuiWidgets.drawResponders("train", customConfigs, responders);
            drawRawConfigs();
        }
        ImGui.setNextItemWidth(180);
        ImGui.inputTextWithHint("##train_new_key", t("gui.rte.rail_trigger.key"), newKey);
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.rail_trigger.add") + "###train_add_key") && !newKey.get().trim().isEmpty()) {
            customConfigs.putIfAbsent(newKey.get().trim(), "");
            newKey.set("");
            dirty = true;
        }

        ImGui.separator();
        if (ImGui.button(t("gui.rte.light_editor.open") + "###train_light_editor") && vehicle instanceof Vehicle train) {
            Minecraft.getInstance().setScreen(new ImGuiVehicleLightEditorScreen(train.getId(), this));
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.back") + "###train_back")) {
            onClose();
        }
        ImGui.end();
    }

    private final Map<String, imgui.type.ImString> rawValues = new java.util.HashMap<>();
    private final imgui.type.ImString newKey = new imgui.type.ImString(64);

    private void drawRawConfigs() {
        final java.util.List<String> keys = new java.util.ArrayList<>();
        for (final String key : customConfigs.keySet()) {
            if (responders == null || !responders.containsKey(key)) {
                keys.add(key);
            }
        }
        if (keys.isEmpty()) {
            return;
        }
        java.util.Collections.sort(keys);
        ImGui.text(t("gui.rte.custom_config.raw"));
        for (final String key : keys) {
            final imgui.type.ImString value = rawValues.computeIfAbsent(key, ignored -> {
                final imgui.type.ImString text = new imgui.type.ImString(256);
                text.set(customConfigs.getOrDefault(key, ""));
                return text;
            });
            ImGui.pushID("train_raw_" + key);
            ImGui.text(key);
            ImGui.sameLine();
            ImGui.setNextItemWidth(220);
            if (ImGui.inputText("##value", value)) {
                customConfigs.put(key, value.get());
                dirty = true;
            }
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.rail_trigger.remove") + "###remove")) {
                customConfigs.remove(key);
                rawValues.remove(key);
                dirty = true;
            }
            ImGui.popID();
        }
    }

    @Override
    public void onClose() {
        ImGuiWorkspace.close(this);
        if (dirty) {
            supplier.setCustomConfigs(customConfigs);
            if (vehicle instanceof Vehicle train) {
                PacketUpdateTrainCustomConfigs.sendUpdateC2S(train, customConfigs);
            }
        }
        super.onClose();
    }
}
