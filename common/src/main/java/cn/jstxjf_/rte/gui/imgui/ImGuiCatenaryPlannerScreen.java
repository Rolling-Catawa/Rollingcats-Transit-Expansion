package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.data.CatenaryPlan;
import cn.jstxjf_.rte.data.MsdBridge;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.mtr.mapping.holder.BlockPos;

import java.util.Locale;

public class ImGuiCatenaryPlannerScreen extends net.minecraft.client.gui.screens.Screen implements ImGuiWorkspace.Panel {

    @Override
    protected void init() {
        super.init();
        ImGuiWorkspace.open(this);
    }

    private static final CatenaryPlan.Params params = new CatenaryPlan.Params();
    private static final ImInt nodeBlockIndex = new ImInt(0);
    private static final ImInt catenaryTypeIndex = new ImInt(0);
    private static final float[] span = {50f};
    private static final float[] stagger = {0.3f};
    private static final float[] height = {5f};
    private static final float[] lateral = {0f};
    private static final float[] startOffset = {0f};
    private static final float[] blockLateral = {2.8f};
    private static final float[] blockVertical = {0f};
    private static final float[] blockLongitudinal = {0f};
    private static final float[] yawOffset = {0f};

    private final BlockPos posStart;
    private final BlockPos posEnd;
    private final net.minecraft.client.gui.screens.Screen parent;

    private CatenaryPlan.Plan plan;
    private String statusLine = "";
    private float viewZoom = 1f;
    private float viewPanX = 0;
    private float viewPanY = 0;

    public ImGuiCatenaryPlannerScreen(BlockPos posStart, BlockPos posEnd, net.minecraft.client.gui.screens.Screen parent) {
        super(Component.translatable("gui.rte.catenary_planner"));
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.parent = parent;
        rebuild();
    }

    private void rebuild() {
        params.nodeBlockIndex = nodeBlockIndex.get();
        params.catenaryTypeIndex = catenaryTypeIndex.get();
        params.span = span[0];
        params.stagger = stagger[0];
        params.height = height[0];
        params.lateral = lateral[0];
        params.startOffset = startOffset[0];
        params.blockLateral = blockLateral[0];
        params.blockVertical = blockVertical[0];
        params.blockLongitudinal = blockLongitudinal[0];
        params.yawOffset = yawOffset[0];
        plan = CatenaryPlan.build(posStart, posEnd, params);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RteImGuiHost.beginFrame();
        ImGuiWorkspace.drawAll();
        RteImGuiHost.endFrame();
    }

    @Override
    public void drawPanel() {
        ImGui.setNextWindowSize(760, 560, imgui.flag.ImGuiCond.FirstUseEver);
        ImGui.begin(t("gui.rte.catenary_planner") + "###rte_catenary_planner");

        if (!MsdBridge.isLoaded()) {
            ImGui.textColored(0.95f, 0.4f, 0.35f, 1f, t("gui.rte.catenary_planner.no_msd"));
            if (ImGui.button(t("gui.rte.imgui.back") + "###back")) {
                onClose();
            }
            ImGui.end();
            return;
        }

        drawParams();
        ImGui.separator();
        drawSummary();
        drawViewport();
        ImGui.separator();
        drawActions();

        ImGui.end();
    }

    private void drawParams() {
        boolean dirty = false;
        ImGui.setNextItemWidth(200);
        if (ImGui.combo(t("gui.rte.catenary_planner.node_block"), nodeBlockIndex, CatenaryPlan.NODE_BLOCKS)) {
            dirty = true;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(180);
        if (ImGui.combo(t("gui.rte.imgui.catenary_type"), catenaryTypeIndex, CatenaryPlan.CATENARY_TYPES)) {
            dirty = true;
        }

        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.span"), span, 8f, 80f, "%.0f m")) {
            dirty = true;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.stagger"), stagger, 0f, 1.5f, "%.2f m")) {
            dirty = true;
        }

        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.height"), height, 3f, 12f, "%.1f m")) {
            dirty = true;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.lateral"), lateral, -8f, 8f, "%.1f m")) {
            dirty = true;
        }

        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.start_offset"), startOffset, 0f, 60f, "%.1f m")) {
            dirty = true;
        }

        ImGui.separator();
        ImGui.textDisabled(t("gui.rte.catenary_planner.pole_offset_hint"));
        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.block_lateral"), blockLateral, -8f, 8f, "%.1f m")) {
            dirty = true;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.block_vertical"), blockVertical, -8f, 8f, "%.1f m")) {
            dirty = true;
        }
        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.block_longitudinal"), blockLongitudinal, -8f, 8f, "%.1f m")) {
            dirty = true;
        }

        ImGui.setNextItemWidth(240);
        if (ImGui.sliderFloat(t("gui.rte.catenary_planner.yaw_offset"), yawOffset, -180f, 180f, "%.0f°")) {
            dirty = true;
        }
        ImGui.sameLine();
        for (final float preset : new float[]{0f, 90f, 180f, -90f}) {
            if (ImGui.button(String.format(Locale.ROOT, "%.0f°###yaw_%.0f", preset, preset))) {
                yawOffset[0] = preset;
                dirty = true;
            }
            ImGui.sameLine();
        }
        ImGui.newLine();
        ImGui.textDisabled(t("gui.rte.catenary_planner.yaw_hint"));

        if (ImGui.checkbox(t("gui.rte.catenary_planner.start_positive"), params.startPositive)) {
            params.startPositive = !params.startPositive;
            dirty = true;
        }
        ImGui.sameLine();
        if (ImGui.checkbox(t("gui.rte.catenary_planner.curve_tighten"), params.curveTighten)) {
            params.curveTighten = !params.curveTighten;
            dirty = true;
        }

        if (ImGui.checkbox(t("gui.rte.catenary_planner.pole_on_right"), params.poleOnRight)) {
            params.poleOnRight = !params.poleOnRight;
            dirty = true;
        }
        ImGui.sameLine();
        if (ImGui.checkbox(t("gui.rte.catenary_planner.auto_mirror"), params.autoMirror)) {
            params.autoMirror = !params.autoMirror;
            dirty = true;
        }

        if (dirty) {
            rebuild();
        }
    }

    private void drawSummary() {
        if (plan == null) {
            return;
        }
        if (plan.failure != null) {
            ImGui.textColored(0.95f, 0.4f, 0.35f, 1f, t("gui.rte.catenary_planner." + plan.failure));
            return;
        }
        ImGui.text(String.format(Locale.ROOT, "%s: %.1f m", t("gui.rte.catenary_planner.length"), plan.totalLength));
        ImGui.sameLine();
        ImGui.text(String.format(Locale.ROOT, " | %s: %d", t("gui.rte.catenary_planner.nodes"), plan.nodes.size()));
        ImGui.sameLine();
        if (plan.invalidSpans == 0) {
            ImGui.textColored(0.35f, 0.9f, 0.4f, 1f, " | " + t("gui.rte.catenary_planner.all_valid"));
        } else {
            ImGui.textColored(0.95f, 0.35f, 0.3f, 1f,
                String.format(Locale.ROOT, " | %s: %d", t("gui.rte.catenary_planner.invalid"), plan.invalidSpans));
        }
    }

    private void drawViewport() {
        if (plan == null || plan.centerLine.isEmpty()) {
            return;
        }
        final float viewWidth = Math.max(320, ImGui.getContentRegionAvailX() - 8);
        final float viewHeight = 300;
        final float originX = ImGui.getCursorScreenPosX();
        final float originY = ImGui.getCursorScreenPosY();

        final ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(originX, originY, originX + viewWidth, originY + viewHeight, 0xE0141418);
        drawList.addRect(originX, originY, originX + viewWidth, originY + viewHeight, 0xFF42423A);

        ImGui.invisibleButton("###catenary_viewport", viewWidth, viewHeight);
        if (ImGui.isItemActive()) {
            if (ImGui.getIO().getMouseDown(1)) {
                viewZoom = Math.max(0.2f, Math.min(20f, viewZoom * (1 - ImGui.getIO().getMouseDeltaY() * 0.01f)));
            } else {
                viewPanX += ImGui.getIO().getMouseDeltaX();
                viewPanY += ImGui.getIO().getMouseDeltaY();
            }
        }

        double minX = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        for (final double[] point : plan.centerLine) {
            minX = Math.min(minX, point[0]);
            maxX = Math.max(maxX, point[0]);
            minZ = Math.min(minZ, point[2]);
            maxZ = Math.max(maxZ, point[2]);
        }
        final double centerX = (minX + maxX) / 2;
        final double centerZ = (minZ + maxZ) / 2;
        final double extent = Math.max(4, Math.max(maxX - minX, maxZ - minZ));
        final double scale = Math.min(viewWidth, viewHeight) * 0.85 / extent * viewZoom;
        final float screenCenterX = originX + viewWidth / 2 + viewPanX;
        final float screenCenterY = originY + viewHeight / 2 + viewPanY;

        drawList.pushClipRect(originX, originY, originX + viewWidth, originY + viewHeight, true);

        float prevX = Float.NaN, prevY = Float.NaN;
        for (final double[] point : plan.centerLine) {
            final float px = (float) (screenCenterX + (point[0] - centerX) * scale);
            final float py = (float) (screenCenterY + (point[2] - centerZ) * scale);
            if (!Float.isNaN(prevX)) {
                drawList.addLine(prevX, prevY, px, py, 0xFF808080, 2f);
            }
            prevX = px;
            prevY = py;
        }

        prevX = Float.NaN;
        prevY = Float.NaN;
        for (int i = 0; i < plan.nodes.size(); i++) {
            final CatenaryPlan.Node node = plan.nodes.get(i);
            final float px = (float) (screenCenterX + (node.wireX - centerX) * scale);
            final float py = (float) (screenCenterY + (node.wireZ - centerZ) * scale);
            if (!Float.isNaN(prevX)) {
                final int wireColor = plan.nodes.get(i - 1).valid ? 0xFF40E0FF : 0xFF3030FF;
                drawList.addLine(prevX, prevY, px, py, wireColor, 2f);
                if (scale > 1.2) {
                    final CatenaryPlan.Node previous = plan.nodes.get(i - 1);
                    drawList.addText((prevX + px) / 2f, (prevY + py) / 2f - 14,
                        0xFFB0B0B0, String.format(Locale.ROOT, "%.0fm", previous.spanToNext));
                }
            }
            drawList.addCircleFilled(px, py, 4f, node.positive ? 0xFF40FF40 : 0xFF4090FF);
            final float bx = (float) (screenCenterX + (node.blockX + 0.5 - centerX) * scale);
            final float by = (float) (screenCenterY + (node.blockZ + 0.5 - centerZ) * scale);
            if (Math.abs(bx - px) > 1.5f || Math.abs(by - py) > 1.5f) {
                drawList.addRect(bx - 3f, by - 3f, bx + 3f, by + 3f, 0xFFB0B0B0, 0f, 0, 1.5f);
                drawList.addLine(bx, by, px, py, 0x80B0B0B0, 1f);
            }
            prevX = px;
            prevY = py;
        }

        drawList.popClipRect();

        ImGui.textDisabled(t("gui.rte.catenary_planner.viewport_hint"));
    }

    private void drawActions() {
        final boolean canApply = plan != null && plan.failure == null && plan.nodes.size() >= 2;
        if (canApply) {
            if (ImGui.button(t("gui.rte.catenary_planner.apply") + "###apply")) {
                CatenaryPlan.apply(plan, params);
                statusLine = t("gui.rte.catenary_planner.applied")
                    .replace("%s", String.valueOf(plan.nodes.size()));
            }
            ImGui.sameLine();
        }
        if (ImGui.button(t("gui.rte.catenary_planner.refresh") + "###refresh")) {
            rebuild();
            statusLine = "";
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.back") + "###back")) {
            onClose();
        }
        if (!statusLine.isEmpty()) {
            ImGui.textColored(0.6f, 0.85f, 1f, 1f, statusLine);
        }
    }

    private static String t(String key) {
        return Component.translatable(key).getString();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (ImGuiWorkspace.activateRemaining(net.minecraft.client.Minecraft.getInstance(), this)) {
            return;
        }
        net.minecraft.client.Minecraft.getInstance().setScreen(parent);
    }
}
