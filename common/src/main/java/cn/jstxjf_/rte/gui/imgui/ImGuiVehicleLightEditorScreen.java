package cn.jstxjf_.rte.gui.imgui;

//#if MC>=12000
import net.minecraft.client.gui.GuiGraphics;
//#else
import cn.jstxjf_.rte.compat.GuiGraphics;
//#endif
import cn.jstxjf_.rte.data.VehicleLightRegistry.Type;
import cn.jstxjf_.rte.render.train.VehicleLightEditor;
import cn.jstxjf_.rte.render.train.VehicleLightEditor.EditableLight;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Locale;

public class ImGuiVehicleLightEditorScreen extends ImGuiScreenBase {

    private static final Type[] TYPES = Type.values();

    private final long preferredVehicle;
    private boolean opened;
    private String status = "";

    public ImGuiVehicleLightEditorScreen(long preferredVehicle, Screen parent) {
        super(Component.translatable("gui.rte.light_editor.title"), parent);
        this.preferredVehicle = preferredVehicle;
    }

    @Override
    protected void init() {
        super.init();
        if (!opened) {
            opened = true;
            if (!VehicleLightEditor.open(camera(), preferredVehicle)) {
                status = t("gui.rte.light_editor.no_vehicle");
            }
        }
    }

    @Override
    //#if MC>=12000
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    //#else
    public void render(com.mojang.blaze3d.vertex.PoseStack rtePoseStack, int mouseX, int mouseY, float partialTick) {
        final GuiGraphics graphics = new GuiGraphics(rtePoseStack);
    //#endif
        if (!RteImGuiHost.isAvailable()) {
            super.render(cn.jstxjf_.rte.compat.GuiArgs.of(graphics), mouseX, mouseY, partialTick);
            return;
        }
        RteImGuiHost.beginFrame();
        ImGuiWorkspace.drawAll();
        RteImGuiHost.endFrame();
    }

    @Override
    protected void buildWindow() {
        if (VehicleLightEditor.isActive()) {
            drawViewport();
        }
        ImGui.setNextWindowSize(460, 0, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(20, 20, ImGuiCond.FirstUseEver);
        ImGui.begin(title.getString() + "###rte_light_editor");
        if (!VehicleLightEditor.isActive()) {
            ImGui.textWrapped(status);
            if (ImGui.button(t("gui.rte.light_editor.retry") + "###light_retry")) {
                status = VehicleLightEditor.open(camera(), preferredVehicle) ? "" : t("gui.rte.light_editor.no_vehicle");
            }
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.imgui.back") + "###light_back_inactive")) {
                onClose();
            }
            ImGui.end();
            return;
        }

        ImGui.text(t("gui.rte.light_editor.vehicle", VehicleLightEditor.resourceId()));
        if (ImGui.button(t("gui.rte.light_editor.previous_car") + "###light_prev")) {
            VehicleLightEditor.cycle(camera(), -1);
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.light_editor.next_car") + "###light_next")) {
            VehicleLightEditor.cycle(camera(), 1);
        }
        ImGui.textDisabled(t("gui.rte.light_editor.hint"));
        ImGui.separator();

        final List<EditableLight> lights = VehicleLightEditor.lights();
        for (int i = 0; i < lights.size(); i++) {
            final EditableLight light = lights.get(i);
            final String label = String.format(Locale.ROOT, "%d. %s  (%.2f, %.2f, %.2f)%s###light_row_%d", i + 1,
                t("gui.rte.light_editor.type." + light.type.name().toLowerCase(Locale.ROOT)),
                light.position[0], light.position[1], light.position[2], light.mirrorX ? "  ×2" : "", i);
            if (ImGui.selectable(label, i == VehicleLightEditor.selected())) {
                VehicleLightEditor.select(i);
            }
        }
        if (ImGui.button(t("gui.rte.light_editor.add") + "###light_add")) {
            VehicleLightEditor.add();
        }
        final int selected = VehicleLightEditor.selected();
        if (selected >= 0) {
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.light_editor.remove") + "###light_remove")) {
                VehicleLightEditor.remove(selected);
            }
        }

        if (ImGui.button(t("gui.rte.light_editor.focus_light") + "###light_focus_light")) {
            VehicleLightEditor.focusSelected();
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.light_editor.focus_car") + "###light_focus_car")) {
            VehicleLightEditor.focusCar();
        }
        if (VehicleLightEditor.selected() >= 0) {
            ImGui.separator();
            drawProperties(lights.get(VehicleLightEditor.selected()));
        }

        ImGui.separator();
        if (ImGui.button(t("gui.rte.light_editor.save") + "###light_save")) {
            try {
                VehicleLightEditor.save();
                status = t("gui.rte.light_editor.saved");
            } catch (Exception e) {
                status = e.toString();
            }
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.light_editor.reset") + "###light_reset")) {
            try {
                VehicleLightEditor.resetToAutomatic();
                status = t("gui.rte.light_editor.reset_done");
            } catch (Exception e) {
                status = e.toString();
            }
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.light_editor.export") + "###light_export")) {
            try {
                status = t("gui.rte.light_editor.exported", VehicleLightEditor.export().toString());
            } catch (Exception e) {
                status = e.toString();
            }
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.back") + "###light_back")) {
            onClose();
        }
        if (!status.isEmpty()) {
            ImGui.textWrapped(status);
        }
        ImGui.end();
    }

    private void drawProperties(EditableLight light) {
        boolean changed = false;
        final String[] names = new String[TYPES.length];
        for (int i = 0; i < TYPES.length; i++) {
            names[i] = t("gui.rte.light_editor.type." + TYPES[i].name().toLowerCase(Locale.ROOT));
        }
        final ImInt type = new ImInt(light.type.ordinal());
        if (ImGui.combo(t("gui.rte.light_editor.type") + "###light_type", type, names)) {
            light.type = TYPES[type.get()];
            changed = true;
        }
        changed |= ImGui.dragFloat3(t("gui.rte.light_editor.position") + "###light_pos", light.position, 0.01F);
        changed |= ImGui.dragFloat3(t("gui.rte.light_editor.axis") + "###light_axis", light.axis, 0.01F, -1, 1);
        changed |= ImGui.colorEdit3(t("gui.rte.light_editor.color") + "###light_color", light.color);
        changed |= ImGui.sliderFloat(t("gui.rte.light_editor.brightness") + "###light_brightness", light.brightness, 0, 4);
        changed |= ImGui.sliderFloat(t("gui.rte.light_editor.flare") + "###light_flare", light.flareSize, 0, 4);
        changed |= ImGui.inputInt2(t("gui.rte.light_editor.blink") + "###light_blink", light.blink);
        if (ImGui.checkbox(t("gui.rte.light_editor.mirror") + "###light_mirror", light.mirrorX)) {
            light.mirrorX = !light.mirrorX;
            changed = true;
        }
        if (changed) {
            light.blink[0] = Math.max(0, light.blink[0]);
            light.blink[1] = Math.max(0, light.blink[1]);
            VehicleLightEditor.changed();
        }
    }

    private boolean orbiting;
    private boolean panning;

    private void drawViewport() {
        final imgui.ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(io.getDisplaySizeX(), io.getDisplaySizeY());
        final int flags = imgui.flag.ImGuiWindowFlags.NoDecoration | imgui.flag.ImGuiWindowFlags.NoBackground
            | imgui.flag.ImGuiWindowFlags.NoBringToFrontOnFocus | imgui.flag.ImGuiWindowFlags.NoSavedSettings
            | imgui.flag.ImGuiWindowFlags.NoFocusOnAppearing | imgui.flag.ImGuiWindowFlags.NoNav
            | imgui.flag.ImGuiWindowFlags.NoMove | imgui.flag.ImGuiWindowFlags.NoScrollWithMouse;
        ImGui.begin("##rte_light_viewport", flags);
        ImGui.setCursorPos(0, 0);
        ImGui.invisibleButton("##rte_light_viewport_area", io.getDisplaySizeX(), io.getDisplaySizeY(),
            imgui.flag.ImGuiButtonFlags.MouseButtonLeft | imgui.flag.ImGuiButtonFlags.MouseButtonRight | imgui.flag.ImGuiButtonFlags.MouseButtonMiddle);
        final boolean hovered = ImGui.isItemHovered();
        final double ndcX = ImGui.getMousePosX() / io.getDisplaySizeX() * 2 - 1;
        final double ndcY = 1 - ImGui.getMousePosY() / io.getDisplaySizeY() * 2;
        if (ImGui.isItemClicked(0)) {
            orbiting = !VehicleLightEditor.press(ndcX, ndcY);
        }
        if (ImGui.isItemClicked(1) || ImGui.isItemClicked(2)) {
            panning = true;
        }
        if (ImGui.isMouseDown(0)) {
            if (VehicleLightEditor.isDraggingAxis()) {
                VehicleLightEditor.drag(ndcX, ndcY);
            } else if (orbiting) {
                VehicleLightEditor.orbit(io.getMouseDeltaX(), io.getMouseDeltaY());
            }
        } else {
            orbiting = false;
            VehicleLightEditor.release();
        }
        if (panning && (ImGui.isMouseDown(1) || ImGui.isMouseDown(2))) {
            VehicleLightEditor.pan(io.getMouseDeltaX(), io.getMouseDeltaY());
        } else {
            panning = false;
        }
        if (hovered && io.getMouseWheel() != 0) {
            VehicleLightEditor.zoom(io.getMouseWheel());
        }
        if (!io.getWantTextInput()) {
            final long window = Minecraft.getInstance().getWindow().getWindow();
            final float forward = key(window, org.lwjgl.glfw.GLFW.GLFW_KEY_W) - key(window, org.lwjgl.glfw.GLFW.GLFW_KEY_S);
            final float right = key(window, org.lwjgl.glfw.GLFW.GLFW_KEY_D) - key(window, org.lwjgl.glfw.GLFW.GLFW_KEY_A);
            final float up = key(window, org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE) - key(window, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT);
            if (forward != 0 || right != 0 || up != 0) {
                VehicleLightEditor.moveFocus(forward, right, up, io.getDeltaTime());
            }
        }
        ImGui.end();
    }

    @Override
    public void onClose() {
        VehicleLightEditor.close();
        ImGuiWorkspace.close(this);
        super.onClose();
    }

    private static float key(long window, int key) {
        return com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, key) ? 1 : 0;
    }

    private static Vec3 camera() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }
}
