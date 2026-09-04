package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.network.PacketUpdateTrainCustomConfigs;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
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

        final boolean hasEditable = responders != null && customConfigs.keySet().stream().anyMatch(responders::containsKey);
        if (!hasEditable && customConfigs.isEmpty()) {
            ImGui.text(t("gui.rte.custom_config.null"));
        } else {
            dirty |= ImGuiWidgets.drawResponders("train", customConfigs, responders);
        }

        ImGui.separator();
        if (ImGui.button(t("gui.rte.imgui.back") + "###train_back")) {
            onClose();
        }
        ImGui.end();
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
