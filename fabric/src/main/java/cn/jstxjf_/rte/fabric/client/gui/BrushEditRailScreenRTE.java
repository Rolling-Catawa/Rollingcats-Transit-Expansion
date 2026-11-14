package cn.jstxjf_.rte.fabric.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.mtr.core.data.Rail;
import org.mtr.core.operation.UpdateDataRequest;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mod.InitClient;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.client.CustomResourceLoader;
import org.mtr.mod.packet.PacketUpdateData;
import org.mtr.mod.resource.RailResource;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;

public class BrushEditRailScreenRTE extends Screen {

    private ObjectImmutableList<RailResource> allRails;
    private int selectedIndex;
    private Button applyButton;

    public static Screen create() {
        return new BrushEditRailScreenRTE();
    }

    public BrushEditRailScreenRTE() {
        super(Component.literal("RTE Brush"));
    }

    private static Rail getFirstRail() {
        var rails = MinecraftClientData.getInstance().rails;
        if (rails.isEmpty()) return null;
        return rails.iterator().next();
    }

    @Override
    protected void init() {
        allRails = CustomResourceLoader.getRails();
        selectedIndex = 0;
        int cx = this.width / 2;
        int cy = this.height / 2;
        this.addRenderableWidget(cn.jstxjf_.rte.compat.WidgetCompat.button(Component.literal("Prev Style"), b -> prev(), cx - 160, cy - 35, 120, 20));
        applyButton = cn.jstxjf_.rte.compat.WidgetCompat.button(Component.literal("Apply: default"), b -> applySelected(), cx - 35, cy - 35, 230, 20);
        this.addRenderableWidget(applyButton);
        this.addRenderableWidget(cn.jstxjf_.rte.compat.WidgetCompat.button(Component.literal("Next Style"), b -> next(), cx - 160, cy - 10, 120, 20));
        this.addRenderableWidget(cn.jstxjf_.rte.compat.WidgetCompat.button(Component.literal("Flip Style"), b -> flipStyle(), cx - 35, cy - 10, 230, 20));
        this.addRenderableWidget(cn.jstxjf_.rte.compat.WidgetCompat.button(Component.literal("Close"), b -> onClose(), cx - 100, cy + 15, 200, 20));
        refreshApplyButton();
    }

    private void applySelected() {
        Rail rail = getFirstRail();
        if (rail == null) return;
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        final String selectedId = getSelectedRailId();
        final ObjectArrayList<String> styles = new ObjectArrayList<>();
        if (selectedId.equals(CustomResourceLoader.DEFAULT_RAIL_ID)) {
            styles.add(CustomResourceLoader.DEFAULT_RAIL_ID);
        } else {
            final Object2BooleanArrayMap<String> existingDirections = new Object2BooleanArrayMap<>();
            rail.getStyles().forEach(style -> {
                if (!style.equals(CustomResourceLoader.DEFAULT_RAIL_ID)) {
                    existingDirections.put(org.mtr.mod.resource.RailResource.getIdWithoutDirection(style), style.endsWith("_2"));
                }
            });
            styles.add(selectedId + (existingDirections.getBoolean(selectedId) ? "_2" : "_1"));
        }
        var req = new UpdateDataRequest(MinecraftClientData.getInstance()).addRail(Rail.copy(rail, styles));
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketUpdateData(req));
    }

    private void flipStyle() {
        Rail rail = getFirstRail();
        if (rail == null) return;
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var current = rail.getStyles();
        ObjectArrayList<String> styles = new ObjectArrayList<>();
        for (int i = 0; i < current.size(); i++) {
            String s = current.get(i);
            String ns = s.endsWith("_2") ? s.substring(0, s.length() - 2) : s + "_2";
            styles.add(ns);
        }
        if (styles.isEmpty()) styles.add(CustomResourceLoader.DEFAULT_RAIL_ID + "_2");
        var req = new UpdateDataRequest(MinecraftClientData.getInstance()).addRail(Rail.copy(rail, styles));
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketUpdateData(req));
    }

    private void prev() {
        if (allRails == null || allRails.size() == 0) return;
        selectedIndex = (selectedIndex - 1 + allRails.size()) % allRails.size();
        refreshApplyButton();
    }

    private void next() {
        if (allRails == null || allRails.size() == 0) return;
        selectedIndex = (selectedIndex + 1) % allRails.size();
        refreshApplyButton();
    }

    private void refreshApplyButton() {
        if (applyButton == null) return;
        String id = getSelectedRailId();
        applyButton.setMessage(Component.literal("Apply: " + id));
    }

    private String getSelectedRailId() {
        if (allRails == null || allRails.size() == 0) return CustomResourceLoader.DEFAULT_RAIL_ID;
        final RailResource railResource = allRails.get(selectedIndex);
        return railResource == null ? CustomResourceLoader.DEFAULT_RAIL_ID : railResource.getId();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
