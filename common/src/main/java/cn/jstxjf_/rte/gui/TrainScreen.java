package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.KeyMappings;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.gui.imgui.ImGuiTrainScreen;
import cn.jstxjf_.rte.gui.imgui.RteImGuiHost;
import cn.jstxjf_.rte.mixin.VehicleRidingMovementAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.VehicleExtension;

public class TrainScreen {

    public static void tick() {
        if (KeyMappings.TRAIN_SCREEN == null) {
            return;
        }

        while (KeyMappings.TRAIN_SCREEN.consumeClick()) {
            final Minecraft client = Minecraft.getInstance();
            final LocalPlayer player = client.player;
            if (player == null || client.screen != null) {
                return;
            }

            final long ridingVehicleId = VehicleRidingMovementAccessor.rte$getRidingVehicleId();
            if (ridingVehicleId == 0) {
                return;
            }

            for (final VehicleExtension vehicle : MinecraftClientData.getInstance().vehicles) {
                if (vehicle.getId() == ridingVehicleId) {
                    client.setScreen(createScreen(vehicle, client.screen));
                    return;
                }
            }
        }
    }

    public static Screen createScreen(Object vehicle, Screen parent) {
        if (!(vehicle instanceof TrainExtraSupplier)) return parent;
        if (!RteImGuiHost.isAvailable()) {
            RteImGuiHost.notifyUnavailable();
            return parent;
        }
        return new ImGuiTrainScreen(vehicle, parent);
    }
}
