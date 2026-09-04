package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class ImGuiDirectNodeScreen extends net.minecraft.client.gui.screens.Screen implements ImGuiWorkspace.Panel {

    @Override
    protected void init() {
        super.init();
        ImGuiWorkspace.open(this);
    }

    private final BlockEntityDirectNode entity;
    private final net.minecraft.client.gui.screens.Screen parent;
    private final Runnable onAngleChanged;
    private boolean dirty;

    public ImGuiDirectNodeScreen(BlockEntityDirectNode entity, net.minecraft.client.gui.screens.Screen parent, Runnable onAngleChanged) {
        super(Component.translatable("gui.rte.direct_node.title"));
        this.entity = entity;
        this.parent = parent;
        this.onAngleChanged = onAngleChanged;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RteImGuiHost.beginFrame();
        ImGuiWorkspace.drawAll();
        RteImGuiHost.endFrame();
    }

    @Override
    public void drawPanel() {
        ImGui.setNextWindowSize(460, 0, ImGuiCond.FirstUseEver);
        ImGui.begin(t("gui.rte.direct_node.title") + "###rte_direct_node");

        final boolean bound = entity.isBound();
        if (bound) {
            ImGui.textColored(0.35f, 0.9f, 0.4f, 1f, String.format(Locale.ROOT, "%s: %.3f°", t("gui.rte.imgui.node_angle"), entity.getAngleDegrees()));
        } else {
            ImGui.textColored(0.95f, 0.85f, 0.3f, 1f, t("gui.rte.direct_node.unbound"));
        }

        final float[] sliderHolder = {(float) (bound ? entity.getAngleDegrees() : 0)};
        if (ImGui.sliderFloat("##node_angle_slider", sliderHolder, 0f, 360f, "%.2f°")) {
            applyAngle(sliderHolder[0]);
        }

        final float[] holder = {(float) (bound ? entity.getAngleDegrees() : 0)};
        if (ImGui.dragFloat("##node_angle", holder, 0.05f, -1, 361, "%.2f°")) {
            applyAngle(holder[0]);
        }

        step(-22.5f); ImGui.sameLine(); step(-1f); ImGui.sameLine(); step(-0.1f); ImGui.sameLine();
        step(0.1f); ImGui.sameLine(); step(1f); ImGui.sameLine(); step(22.5f);

        if (ImGui.button(t("gui.rte.imgui.snap_45") + "###snap45")) {
            applyAngle(Math.round(entity.getAngleDegrees() / 45.0) * 45f);
        }
        ImGui.sameLine();
        if (bound && ImGui.button(t("gui.rte.direct_node.unbind_button") + "###unbind")) {
            entity.unbind();
            entity.sendUpdateC2S();
        }
        if (entity.isFittingNode()) {
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.imgui.clear_fitting") + "###clear_fit")) {
                entity.setFittingNode(false);
                entity.sendUpdateC2S();
            }
        }

        ImGui.separator();
        if (ImGui.button(t("gui.rte.imgui.back") + "###back")) {
            onClose();
        }
        ImGui.end();
        flushIfReleased();
    }

    private void step(float delta) {
        if (ImGui.button(String.format(Locale.ROOT, "%+.1f###step_%s", delta, String.valueOf(delta)).replace("+22.5", "+22.5°").replace("-22.5", "-22.5°"))) {
            applyAngle((float) (entity.getAngleDegrees() + delta));
        }
    }

    private void applyAngle(float degrees) {
        double normalized = degrees % 360;
        if (normalized < 0) {
            normalized += 360;
        }
        entity.bind(normalized);
        dirty = true;
        if (onAngleChanged != null) {
            onAngleChanged.run();
        }
    }

    private void flushIfReleased() {
        if (dirty && !ImGui.isAnyItemActive()) {
            entity.sendUpdateC2S();
            dirty = false;
        }
    }

    private static String t(String key) {
        return net.minecraft.client.resources.language.I18n.get(key);
    }

    @Override
    public void onClose() {
        if (ImGuiWorkspace.activateRemaining(net.minecraft.client.Minecraft.getInstance(), this)) {
            return;
        }
        if (dirty) {
            entity.sendUpdateC2S();
            dirty = false;
        }
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
