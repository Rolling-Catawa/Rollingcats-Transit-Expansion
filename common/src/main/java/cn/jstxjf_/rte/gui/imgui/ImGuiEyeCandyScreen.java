package cn.jstxjf_.rte.gui.imgui;

//#if MC>=12000
import net.minecraft.client.gui.GuiGraphics;
//#else
import cn.jstxjf_.rte.compat.GuiGraphics;
//#endif
import cn.jstxjf_.rte.block.BlockEyeCandy.BlockEntityEyeCandy;
import cn.jstxjf_.rte.data.EyeCandyProperties;
import cn.jstxjf_.rte.data.EyeCandyRegistry;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class ImGuiEyeCandyScreen extends net.minecraft.client.gui.screens.Screen implements ImGuiWorkspace.Panel {

    @Override
    protected void init() {
        super.init();
        ImGuiWorkspace.open(this);
    }

    private final BlockEntityEyeCandy blockEntity;
    private final net.minecraft.client.gui.screens.Screen parent;
    private boolean dirty;

    public ImGuiEyeCandyScreen(BlockEntityEyeCandy blockEntity, net.minecraft.client.gui.screens.Screen parent) {
        super(Component.translatable("gui.rte.eye_candy.title"));
        this.blockEntity = blockEntity;
        this.parent = parent;
    }

    @Override
    //#if MC>=12000
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    //#else
    public void render(com.mojang.blaze3d.vertex.PoseStack rtePoseStack, int mouseX, int mouseY, float partialTick) {
        final GuiGraphics graphics = new GuiGraphics(rtePoseStack);
    //#endif
        RteImGuiHost.beginFrame();
        ImGuiWorkspace.drawAll();
        RteImGuiHost.endFrame();
    }

    @Override
    public void drawPanel() {
        ImGui.setNextWindowSize(520, 0, ImGuiCond.FirstUseEver);
        ImGui.begin(t("gui.rte.eye_candy.title") + "###rte_eyecandy");

        final EyeCandyProperties properties = blockEntity.getProperties();
        ImGui.text(properties != null ? properties.name.getString() + " (" + blockEntity.prefabId + ")" : (blockEntity.prefabId == null ? "-" : blockEntity.prefabId));
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.select") + "###select_model")) {
            final net.minecraft.client.gui.screens.Screen self = this;
            minecraft.setScreen(ImGuiSelectScreen.create(() -> self, EyeCandyRegistry.TREE, () -> blockEntity.prefabId, (mc, screen, key) -> {
                blockEntity.setPrefabId(key);
                blockEntity.sendUpdateC2S();
            }, "https://rolling-catawa.github.io/Rollingcats-Transit-Expansion/wiki/eye-candy.html"));
            ImGui.end();
            return;
        }

        if (ImGui.checkbox(t("gui.rte.eye_candy.full_light"), blockEntity.fullLight)) {
            blockEntity.fullLight = !blockEntity.fullLight;
            blockEntity.sendUpdateC2S();
        }
        ImGui.sameLine();
        if (ImGui.checkbox(t("gui.rte.eye_candy.as_platform"), blockEntity.asPlatform)) {
            blockEntity.asPlatform = !blockEntity.asPlatform;
            blockEntity.sendUpdateC2S();
        }

        ImGui.textDisabled(t("gui.rte.eye_candy.shape", String.valueOf(blockEntity.getShape())));
        ImGui.textDisabled(t("gui.rte.eye_candy.collision", String.valueOf(blockEntity.getCollisionShape())));

        ImGui.separator();
        if (blockEntity.fixedMatrix) {
            ImGui.text(String.format(Locale.ROOT, "T(%.2f, %.2f, %.2f) R(%.1f°, %.1f°, %.1f°) S(%.2f, %.2f, %.2f)",
                blockEntity.translateX, blockEntity.translateY, blockEntity.translateZ,
                Math.toDegrees(blockEntity.rotateX), Math.toDegrees(blockEntity.rotateY), Math.toDegrees(blockEntity.rotateZ),
                blockEntity.scaleX, blockEntity.scaleY, blockEntity.scaleZ));
        } else {
            ImGui.text(t("gui.rte.imgui.translate"));
            blockEntity.translateX = drag("TX", blockEntity.translateX, 0.01f, -16, 16, "%.3f m");
            blockEntity.translateY = drag("TY", blockEntity.translateY, 0.01f, -16, 16, "%.3f m");
            blockEntity.translateZ = drag("TZ", blockEntity.translateZ, 0.01f, -16, 16, "%.3f m");

            ImGui.text(t("gui.rte.imgui.rotate"));
            blockEntity.rotateX = dragDegrees("RX", blockEntity.rotateX);
            blockEntity.rotateY = dragDegrees("RY", blockEntity.rotateY);
            blockEntity.rotateZ = dragDegrees("RZ", blockEntity.rotateZ);

            ImGui.text(t("gui.rte.imgui.scale"));
            blockEntity.scaleX = drag("SX", blockEntity.scaleX, 0.01f, 0.01f, 32, "%.3f");
            blockEntity.scaleY = drag("SY", blockEntity.scaleY, 0.01f, 0.01f, 32, "%.3f");
            blockEntity.scaleZ = drag("SZ", blockEntity.scaleZ, 0.01f, 0.01f, 32, "%.3f");

            if (ImGui.button(t("gui.rte.imgui.reset_pose") + "###reset_pose")) {
                blockEntity.translateX = 0; blockEntity.translateY = 0; blockEntity.translateZ = 0;
                blockEntity.rotateX = 0; blockEntity.rotateY = 0; blockEntity.rotateZ = 0;
                blockEntity.scaleX = 1; blockEntity.scaleY = 1; blockEntity.scaleZ = 1;
                blockEntity.sendUpdateC2S();
            }
        }

        if (blockEntity.getCustomResponders() != null && !blockEntity.getCustomResponders().isEmpty()) {
            ImGui.separator();
            if (ImGuiWidgets.drawResponders("eyecandy_custom", blockEntity.getCustomConfigs(), blockEntity.getCustomResponders())) {
                blockEntity.sendUpdateC2S();
            }
        }

        ImGui.separator();
        if (ImGui.button(t("gui.rte.imgui.back") + "###back")) {
            onClose();
        }
        ImGui.end();
        flushIfReleased();
    }

    private float drag(String label, float value, float speed, float min, float max, String format) {
        final float[] holder = {value};
        ImGui.dragFloat(label, holder, speed, min, max, format);
        if (ImGui.isItemDeactivatedAfterEdit()) {
            dirty = true;
        } else if (ImGui.isItemActive() && holder[0] != value) {
            dirty = true;
        }
        return holder[0];
    }

    private float dragDegrees(String label, float radians) {
        final float[] holder = {(float) Math.toDegrees(radians)};
        ImGui.dragFloat(label, holder, 0.5f, -360, 360, "%.1f°");
        if (ImGui.isItemActive() || ImGui.isItemDeactivatedAfterEdit()) {
            final float newRadians = (float) Math.toRadians(holder[0]);
            if (newRadians != radians) {
                dirty = true;
            }
            return newRadians;
        }
        return radians;
    }

    private void flushIfReleased() {
        if (dirty && !ImGui.isAnyItemActive()) {
            blockEntity.sendUpdateC2S();
            dirty = false;
        }
    }

    private static String t(String key) {
        return net.minecraft.client.resources.language.I18n.get(key);
    }

    private static String t(String key, Object... args) {
        return net.minecraft.client.resources.language.I18n.get(key, args);
    }

    @Override
    public void onClose() {
        if (ImGuiWorkspace.activateRemaining(net.minecraft.client.Minecraft.getInstance(), this)) {
            return;
        }
        if (dirty) {
            blockEntity.sendUpdateC2S();
            dirty = false;
        }
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
