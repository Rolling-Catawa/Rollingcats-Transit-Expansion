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
        final boolean freeNode = entity instanceof cn.jstxjf_.rte.block.BlockFreeNode.BlockEntityFreeNode;
        ImGui.begin(t(freeNode ? "gui.rte.free_node.title" : "gui.rte.direct_node.title") + "###rte_direct_node");

        final boolean bound = entity.isBound();
        if (bound) {
            ImGui.textColored(0.35f, 0.9f, 0.4f, 1f, String.format(Locale.ROOT, "%s: %.3f°", t("gui.rte.imgui.node_angle"), entity.getAngleDegrees()));
        } else {
            ImGui.textColored(0.95f, 0.85f, 0.3f, 1f, t(freeNode ? "gui.rte.free_node.unbound" : "gui.rte.direct_node.unbound"));
        }
        ImGui.textDisabled(t("gui.rte.imgui.node_angle_hint"));

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
            applyAngle(bound ? Math.round(entity.getAngleDegrees() / 45.0) * 45f : 0);
        }
        ImGui.sameLine();
        if (bound && ImGui.button(t("gui.rte.direct_node.unbind") + "###unbind")) {
            entity.unbind();
        }
        if (entity.isFittingNode()) {
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.imgui.clear_fitting") + "###clear_fit")) {
                entity.setFittingNode(false);
                entity.sendUpdateC2S();
            }
        }

        ImGui.separator();
        drawOffsetAxis('x', "gui.rte.direct_node.x_offset", entity.getXOffset());
        drawOffsetAxis('y', "gui.rte.direct_node.y_offset", entity.getYOffset());
        drawOffsetAxis('z', "gui.rte.direct_node.z_offset", entity.getZOffset());
        if (ImGui.button(t("gui.rte.direct_node.reset_position") + "###reset_position")) {
            entity.setOffsetsLocal(0, 0, 0);
            dirty = true;
        }

        ImGui.separator();
        ImGui.text(String.format(Locale.ROOT, "%s: %+.3f°",
                t("gui.rte.direct_node.pitch"), entity.getPitchDegrees()));
        ImGui.textDisabled(t("gui.rte.direct_node.pitch_hint"));
        final float[] pitchSlider = {(float) entity.getPitchDegrees()};
        if (ImGui.sliderFloat("##node_pitch_slider", pitchSlider, -180f, 180f, "%+.2f°")) {
            applyPitch(pitchSlider[0]);
        }
        final float[] pitchInput = {(float) entity.getPitchDegrees()};
        if (ImGui.dragFloat("##node_pitch", pitchInput, 0.05f, -180f, 180f, "%+.3f°")) {
            applyPitch(pitchInput[0]);
        }
        pitchStep(-90f); ImGui.sameLine(); pitchStep(-15f); ImGui.sameLine(); pitchStep(-1f); ImGui.sameLine();
        pitchStep(1f); ImGui.sameLine(); pitchStep(15f); ImGui.sameLine(); pitchStep(90f);
        if (ImGui.button(t("gui.rte.direct_node.reset_pitch") + "###reset_pitch")) {
            applyPitch(0);
        }

        ImGui.separator();
        if (ImGui.button(t("gui.rte.imgui.back") + "###back")) {
            onClose();
        }
        ImGui.end();
        flushIfReleased();
    }

    private void step(float delta) {
        if (ImGui.button(String.format(Locale.ROOT, "%+.1f°###step_%s", delta, String.valueOf(delta)))) {
            applyAngle((float) ((entity.isBound() ? entity.getAngleDegrees() : 0) + delta));
        }
    }

    private void applyAngle(float degrees) {
        double normalized = degrees % 360;
        if (normalized < 0) {
            normalized += 360;
        }
        entity.setAngleLocal(normalized);
        dirty = true;
    }

    private void drawOffsetAxis(char axis, String translationKey, double value) {
        ImGui.text(String.format(Locale.ROOT, "%s: %+.3f m", t(translationKey), value));
        final float[] slider = {(float) value};
        if (ImGui.sliderFloat("##node_" + axis + "_offset_slider", slider, -8f, 8f, "%+.2f m")) {
            applyOffset(axis, slider[0]);
        }
        final float[] input = {(float) value};
        if (ImGui.dragFloat("##node_" + axis + "_offset", input, 0.01f, -64f, 64f, "%+.3f m")) {
            applyOffset(axis, input[0]);
        }
        offsetStep(axis, -1f); ImGui.sameLine(); offsetStep(axis, -0.1f); ImGui.sameLine();
        offsetStep(axis, 0.1f); ImGui.sameLine(); offsetStep(axis, 1f);
    }

    private void offsetStep(char axis, float delta) {
        if (ImGui.button(String.format(Locale.ROOT, "%+.1f m###%sstep_%s", delta, axis, delta))) {
            final double value = axis == 'x' ? entity.getXOffset() : axis == 'y' ? entity.getYOffset() : entity.getZOffset();
            applyOffset(axis, (float) (value + delta));
        }
    }

    private void applyOffset(char axis, float value) {
        if (axis == 'x') entity.setXOffsetLocal(value);
        else if (axis == 'y') entity.setYOffsetLocal(value);
        else entity.setZOffsetLocal(value);
        dirty = true;
    }

    private void pitchStep(float delta) {
        if (ImGui.button(String.format(Locale.ROOT, "%+.0f°###pitch_step_%s", delta, delta))) {
            applyPitch((float) (entity.getPitchDegrees() + delta));
        }
    }

    private void applyPitch(float value) {
        entity.setPitchLocal(value);
        dirty = true;
    }

    private void flushIfReleased() {
        if (dirty && !ImGui.isAnyItemActive()) {
            if (onAngleChanged != null) {
                onAngleChanged.run();
            }
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
            if (onAngleChanged != null) {
                onAngleChanged.run();
            }
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
