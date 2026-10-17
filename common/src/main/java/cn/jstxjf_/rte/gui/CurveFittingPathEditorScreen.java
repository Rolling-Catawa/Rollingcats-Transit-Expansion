package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.data.CurveFitting;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.network.PacketDeleteRail;
import cn.jstxjf_.rte.network.PacketReplaceRailNode;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Screen;
import org.mtr.mapping.holder.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CurveFittingPathEditorScreen extends net.minecraft.client.gui.screens.Screen implements cn.jstxjf_.rte.gui.imgui.ImGuiMouseRouter.Handler {

    private final cn.jstxjf_.rte.gui.imgui.ImGuiMouseRouter rte$mouseRouter = new cn.jstxjf_.rte.gui.imgui.ImGuiMouseRouter();

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return true;
    }

    @Override
    //#if MC>=12002
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double amount) {
    //#else
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    //#endif
        return true;
    }


    private enum PlaneMode { XZ, Y }
    private enum Tool { DRAW, DELETE }

    private static final double SNAP_RADIUS_BLOCKS = 1.5;
    private static final double SNAP_RADIUS_PIXELS = 20;

    private final BlockPos posStart;
    private final BlockPos posEnd;
    private Rail rail;
    private final Supplier<Screen> parent;

    private final List<CurveFitting.DrawPoint> xzPoints = new ArrayList<>();
    private final List<CurveFitting.DrawPoint> yPoints = new ArrayList<>();
    private final List<List<CurveFitting.DrawPoint>> xzUndoStack = new ArrayList<>();
    private final List<List<CurveFitting.DrawPoint>> yUndoStack = new ArrayList<>();

    private final EditorCamera camera = new EditorCamera();
    private final XzPlaneView xzView = new XzPlaneView(camera);
    private final YModeGraphView graph = new YModeGraphView();

    private PlaneMode planeMode = PlaneMode.XZ;
    private Tool currentTool = Tool.DRAW;
    private boolean isDrawing = false;

    private String statusMessage = "";
    private boolean statusIsError = false;
    private long statusTime = 0;


    @Nullable
    private Rail hoveredRail = null;

    public CurveFittingPathEditorScreen(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
        super(Component.translatable("tooltip.rte.rail.path_mode.fitting"));
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.rail = rail;
        this.parent = parent == null ? () -> null : parent;
        this.camera.currentY = posStart == null ? 0 : posStart.getY();
        this.camera.planeY = this.camera.currentY;
    }

    public static Screen createScreen(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
        return new Screen(new CurveFittingPathEditorScreen(posStart, posEnd, rail, parent));
    }

    @Override
    protected void init() {
        camera.setViewport(width, height);
        graph.setViewport(width, height);
        camera.capturePlayer();
        camera.orient(planeMode == PlaneMode.XZ, rail);
    }

    @Override
    public void tick() {
        Rail fresh = RailRebuildHelper.resolveClientRail(cn.jstxjf_.rte.bridge.MtrTypes.blockPos(posStart), cn.jstxjf_.rte.bridge.MtrTypes.blockPos(posEnd), rail);
        if (fresh != null) rail = fresh;
        camera.orient(planeMode == PlaneMode.XZ, rail);
    }

    @Override
    //#if MC>=12002
    public void renderBackground(GuiGraphics graphics, int rteMouseX, int rteMouseY, float rteDelta) {
    //#else
    public void renderBackground(GuiGraphics graphics) {
    //#endif
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        camera.orient(planeMode == PlaneMode.XZ, rail);
        super.render(graphics, mouseX, mouseY, partialTick);
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.beginFrame();
        if (planeMode == PlaneMode.Y) {
            renderYGraph(graphics);
            graph.renderPoints(graphics, yPoints, 0xFFFFFF33, railLength());
            renderYModeCursor(graphics, mouseX, mouseY);
        } else {
            xzView.renderEndpoints(graphics, rail);
            xzView.renderDirectNodes(graphics, minecraft == null ? null : minecraft.level, posStart, posEnd, rail);
            hoveredRail = xzView.renderExistingRails(graphics, mouseX, mouseY, rail, currentTool == Tool.DELETE);
            renderXzPoints(graphics);
            renderCursor(graphics, mouseX, mouseY);
        }
        renderCursorCoords(graphics, mouseX, mouseY);
        rte$mouseRouter.poll(this);
        buildImGuiPanel();
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.endFrame();
    }

    private void buildImGuiPanel() {
        final float rteScale = cn.jstxjf_.rte.gui.imgui.CanvasPainter.guiScale();
        imgui.ImGui.setNextWindowPos(8 * rteScale, 4 * rteScale, imgui.flag.ImGuiCond.Always);
        imgui.ImGui.begin("###rte_curve_fitting_panel", cn.jstxjf_.rte.gui.imgui.CanvasPainter.toolbarFlags());

        if (imgui.ImGui.button((planeMode == PlaneMode.XZ ? "XZ" : "Y") + "###cf_plane")) {
            togglePlane();
        }
        imgui.ImGui.sameLine();
        final boolean canBuild = xzPoints.size() >= 2 && yPoints.size() >= 2;
        if (!canBuild) {
            imgui.ImGui.beginDisabled();
        }
        if (imgui.ImGui.button(Component.translatable("gui.rte.curve_fitting.build").getString() + "###cf_build")) {
            build();
        }
        if (!canBuild) {
            imgui.ImGui.endDisabled();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(Component.translatable("gui.rte.curve_fitting.undo").getString() + "###cf_undo")) {
            undo();
        }

        if (imgui.ImGui.checkbox(Component.translatable("gui.rte.curve_fitting.delete").getString() + "###cf_delete", currentTool == Tool.DELETE)) {
            toggleDelete();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(Component.translatable("gui.rte.brush_edit_rail.clear").getString() + "###cf_clear")) {
            clearCurrent();
        }

        if (imgui.ImGui.button(Component.translatable("gui.rte.curve_fitting.switch_original").getString() + "###cf_original")) {
            switchToNextMode();
        }

        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(Component.translatable("gui.rte.imgui.back").getString() + "###cf_back")) {
            onClose();
        }
        imgui.ImGui.end();

        imgui.ImGui.setNextWindowPos(8 * rteScale, (height - 26) * rteScale, imgui.flag.ImGuiCond.Always);
        imgui.ImGui.begin("###rte_curve_fitting_status", cn.jstxjf_.rte.gui.imgui.CanvasPainter.toolbarFlags());
        final String toolStr = Component.translatable(currentTool == Tool.DELETE
                ? "gui.rte.curve_fitting.tool_delete" : "gui.rte.curve_fitting.tool_draw").getString();
        imgui.ImGui.text((planeMode == PlaneMode.XZ ? "XZ" : "Y") + " | " + toolStr);
        imgui.ImGui.sameLine();
        imgui.ImGui.text("| " + Component.translatable("gui.rte.curve_fitting.points", xzPoints.size(), yPoints.size()).getString());
        imgui.ImGui.sameLine();
        if (planeMode == PlaneMode.XZ) {
            imgui.ImGui.text(String.format(java.util.Locale.ROOT, "| Y=%.0f  H=%.0f", camera.currentY, camera.cameraHeight));
        } else {
            imgui.ImGui.text(String.format(java.util.Locale.ROOT, "| H=%.0f", camera.cameraHeight));
        }
        imgui.ImGui.separator();
        if (imgui.ImGui.collapsingHeader(Component.translatable("gui.rte.rail_path_editor.hints").getString() + "###cf_hints")) {
            imgui.ImGui.textWrapped(Component.translatable(planeMode == PlaneMode.XZ
                    ? "gui.rte.curve_fitting.hint_scroll_xz" : "gui.rte.curve_fitting.hint_scroll_y").getString());
            imgui.ImGui.textWrapped(Component.translatable("gui.rte.curve_fitting.hint_pan").getString());
        }

        if (!statusMessage.isEmpty()) {
            if (System.currentTimeMillis() - statusTime > 5000) {
                statusMessage = "";
            } else {
                imgui.ImGui.sameLine();
                if (statusIsError) {
                    imgui.ImGui.textColored(1f, 0.4f, 0.4f, 1f, "| " + statusMessage);
                } else {
                    imgui.ImGui.textColored(0.65f, 1f, 0.65f, 1f, "| " + statusMessage);
                }
            }
        }
        imgui.ImGui.end();
    }

    private boolean imGuiCapturesMouse() {
        return imgui.ImGui.getIO().getWantCaptureMouse();
    }

    @Override
    public boolean onCanvasDown(double mouseX, double mouseY, int button) {
        if (imGuiCapturesMouse()) return true;
        if (camera.isAnimating()) return false;
        if (false) return true;
        if (!isInCanvas(mouseX, mouseY)) return false;

        if (currentTool == Tool.DELETE && button == 0) {
            deleteHoveredRail();
            return true;
        }
        if (button == 0 && currentTool == Tool.DRAW) {
            saveUndoSnapshot();
            isDrawing = true;
            appendPoint(mouseX, mouseY, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCanvasDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (imGuiCapturesMouse() && !isDrawing) return true;
        if (camera.isAnimating()) return false;
        if (button == 0 && isDrawing && isInCanvas(mouseX, mouseY)) {
            appendPoint(mouseX, mouseY, false);
            return true;
        }
        if (button == 1) {
            if (planeMode == PlaneMode.Y) {
                graph.pan(dragX, dragY);
            } else {
                double ppb = camera.pixelsPerBlock();
                EditorCamera.Basis basis = EditorCamera.basisOf(rail);
                double worldDx = dragX / ppb;
                double worldDy = dragY / ppb;
                camera.panX -= worldDx * basis.nx() - worldDy * basis.ax();
                camera.panZ -= worldDx * basis.nz() - worldDy * basis.az();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCanvasUp(double mouseX, double mouseY, int button) {
        if (button == 0 && isDrawing) {
            if (isInCanvas(mouseX, mouseY)) {
                snapLastPoint(mouseX, mouseY);
            }
            isDrawing = false;
            if (planeMode == PlaneMode.XZ) {
                reparametrizeXzPoints();
            }
        }
        return false;
    }

    @Override
    public boolean onCanvasScroll(double mouseX, double mouseY, double amount) {
        if (imGuiCapturesMouse()) return true;
        if (planeMode == PlaneMode.Y) {
            graph.zoom(amount);
            return true;
        }
        if (hasControlDown()) {
            camera.cameraHeight *= (amount > 0 ? 0.85 : 1.18);
            camera.cameraHeight = GuiDrawUtil.clamp(camera.cameraHeight, EditorCamera.MIN_CAM_HEIGHT, EditorCamera.MAX_CAM_HEIGHT);
            return true;
        }
        camera.currentY += amount;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 90 && hasControlDown()) {
            undo();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        camera.restorePlayer();
        final Screen previous = parent.get();
        minecraft.setScreen(previous == null ? null : previous.data);
    }

    @Override
    public void removed() {
        camera.restorePlayer();
        super.removed();
    }

    private void togglePlane() {
        camera.beginTransition();
        planeMode = planeMode == PlaneMode.XZ ? PlaneMode.Y : PlaneMode.XZ;
        camera.resetPan();
        if (planeMode == PlaneMode.Y) {
            initYModeGraph();
        }
        currentTool = Tool.DRAW;
    }

    private void toggleDelete() {
        currentTool = currentTool == Tool.DELETE ? Tool.DRAW : Tool.DELETE;
    }

    private void initYModeGraph() {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;
        graph.initView(railLength(), p1.getY(), p2.getY());
    }

    private List<CurveFitting.DrawPoint> currentPoints() {
        return planeMode == PlaneMode.XZ ? xzPoints : yPoints;
    }

    private List<List<CurveFitting.DrawPoint>> currentUndoStack() {
        return planeMode == PlaneMode.XZ ? xzUndoStack : yUndoStack;
    }

    private void saveUndoSnapshot() {
        List<List<CurveFitting.DrawPoint>> stack = currentUndoStack();
        stack.add(new ArrayList<>(currentPoints()));
        if (stack.size() > 30) stack.remove(0);
    }

    private void undo() {
        List<List<CurveFitting.DrawPoint>> stack = currentUndoStack();
        if (stack.isEmpty()) return;
        List<CurveFitting.DrawPoint> snapshot = stack.remove(stack.size() - 1);
        currentPoints().clear();
        currentPoints().addAll(snapshot);
    }

    private void clearCurrent() {
        saveUndoSnapshot();
        currentPoints().clear();
    }

    private void appendPoint(double mouseX, double mouseY, boolean allowSnap) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        final Position p1 = extra.getExtraPosition1();
        final Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;
        EditorCamera.Basis basis = EditorCamera.basisOf(rail);

        if (planeMode == PlaneMode.XZ) {
            double[] world = camera.screenToWorld(mouseX, mouseY);
            double wx = world[0];
            double wz = world[2];

            if (allowSnap) {
                double cx1 = p1.getX() + 0.5, cz1 = p1.getZ() + 0.5;
                double cx2 = p2.getX() + 0.5, cz2 = p2.getZ() + 0.5;
                int[] s1 = camera.xzToScreen(cx1, cz1);
                int[] s2 = camera.xzToScreen(cx2, cz2);
                double pd1sq = (mouseX - s1[0]) * (mouseX - s1[0]) + (mouseY - s1[1]) * (mouseY - s1[1]);
                double pd2sq = (mouseX - s2[0]) * (mouseX - s2[0]) + (mouseY - s2[1]) * (mouseY - s2[1]);
                double snapPx = SNAP_RADIUS_PIXELS;
                if (pd1sq < snapPx * snapPx && pd1sq <= pd2sq) {
                    wx = cx1;
                    wz = cz1;
                } else if (pd2sq < snapPx * snapPx) {
                    wx = cx2;
                    wz = cz2;
                }
            }

            double cx1 = p1.getX() + 0.5, cz1 = p1.getZ() + 0.5;
            double along = (wx - cx1) * basis.ax() + (wz - cz1) * basis.az();
            double t = GuiDrawUtil.clamp(along / basis.len(), 0, 1);
            addIfFar(xzPoints, new CurveFitting.DrawPoint(t, wx, camera.currentY, wz));
        } else {
            double[] graphPos = graph.toGraph(mouseX, mouseY);
            double along = graphPos[0];
            double wy = graphPos[1];
            double railLen = railLength();
            along = GuiDrawUtil.clamp(along, 0, railLen);

            if (allowSnap) {
                double snapDist = SNAP_RADIUS_BLOCKS;
                if (along < snapDist) {
                    along = 0;
                    wy = p1.getY();
                } else if (along > railLen - snapDist) {
                    along = railLen;
                    wy = p2.getY();
                }
            }

            double t = railLen > 0 ? along / railLen : 0;
            CurveFitting.DrawPoint xzSample = sampleXzAtT(t, p1, p2);
            addIfFar(yPoints, new CurveFitting.DrawPoint(t, xzSample.x(), wy, xzSample.z()));
        }
    }

    private void snapLastPoint(double mouseX, double mouseY) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        final Position p1 = extra.getExtraPosition1();
        final Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;

        List<CurveFitting.DrawPoint> pts = currentPoints();
        if (pts.isEmpty()) return;

        if (planeMode == PlaneMode.XZ) {
            double cx1 = p1.getX() + 0.5, cz1 = p1.getZ() + 0.5;
            double cx2 = p2.getX() + 0.5, cz2 = p2.getZ() + 0.5;
            int[] s1 = camera.xzToScreen(cx1, cz1);
            int[] s2 = camera.xzToScreen(cx2, cz2);
            double pd1sq = (mouseX - s1[0]) * (mouseX - s1[0]) + (mouseY - s1[1]) * (mouseY - s1[1]);
            double pd2sq = (mouseX - s2[0]) * (mouseX - s2[0]) + (mouseY - s2[1]) * (mouseY - s2[1]);
            double snapPx = SNAP_RADIUS_PIXELS;
            if (pd1sq < snapPx * snapPx && pd1sq <= pd2sq) {
                pts.add(new CurveFitting.DrawPoint(0, cx1, camera.currentY, cz1));
            } else if (pd2sq < snapPx * snapPx) {
                pts.add(new CurveFitting.DrawPoint(1, cx2, camera.currentY, cz2));
            }
        } else {
            double[] graphPos = graph.toGraph(mouseX, mouseY);
            double along = graphPos[0];
            double railLen = railLength();
            double snapDist = SNAP_RADIUS_BLOCKS;
            if (along < snapDist) {
                CurveFitting.DrawPoint xzSample = sampleXzAtT(0, p1, p2);
                pts.add(new CurveFitting.DrawPoint(0, xzSample.x(), p1.getY(), xzSample.z()));
            } else if (along > railLen - snapDist) {
                CurveFitting.DrawPoint xzSample = sampleXzAtT(1, p1, p2);
                pts.add(new CurveFitting.DrawPoint(1, xzSample.x(), p2.getY(), xzSample.z()));
            }
        }
    }

    private void reparametrizeXzPoints() {
        if (xzPoints.size() < 2) return;
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        if (p1 == null) return;
        double cx1 = p1.getX() + 0.5, cz1 = p1.getZ() + 0.5;
        CurveFitting.DrawPoint first = xzPoints.get(0);
        CurveFitting.DrawPoint last = xzPoints.get(xzPoints.size() - 1);
        double dFirst = (first.x() - cx1) * (first.x() - cx1) + (first.z() - cz1) * (first.z() - cz1);
        double dLast = (last.x() - cx1) * (last.x() - cx1) + (last.z() - cz1) * (last.z() - cz1);
        if (dLast < dFirst) {
            Collections.reverse(xzPoints);
        }
        double[] cumulative = new double[xzPoints.size()];
        for (int i = 1; i < xzPoints.size(); i++) {
            CurveFitting.DrawPoint a = xzPoints.get(i - 1);
            CurveFitting.DrawPoint b = xzPoints.get(i);
            double dx = b.x() - a.x();
            double dz = b.z() - a.z();
            cumulative[i] = cumulative[i - 1] + Math.sqrt(dx * dx + dz * dz);
        }
        double total = cumulative[xzPoints.size() - 1];
        if (total < 1E-6) return;
        for (int i = 0; i < xzPoints.size(); i++) {
            CurveFitting.DrawPoint p = xzPoints.get(i);
            xzPoints.set(i, new CurveFitting.DrawPoint(cumulative[i] / total, p.x(), p.y(), p.z()));
        }
    }

    private void addIfFar(List<CurveFitting.DrawPoint> points, CurveFitting.DrawPoint point) {
        if (!points.isEmpty()) {
            CurveFitting.DrawPoint last = points.get(points.size() - 1);
            double dx = point.x() - last.x();
            double dy = point.y() - last.y();
            double dz = point.z() - last.z();
            double minDist = planeMode == PlaneMode.Y ? 0.05 : 0.3;
            if (dx * dx + dy * dy + dz * dz < minDist * minDist) return;
        }
        points.add(point);
    }

    private CurveFitting.DrawPoint sampleXzAtT(double t, Position p1, Position p2) {
        if (xzPoints.size() >= 2) {
            CurveFitting.DrawPoint prev = xzPoints.get(0);
            for (int i = 1; i < xzPoints.size(); i++) {
                CurveFitting.DrawPoint next = xzPoints.get(i);
                if (t <= next.t()) {
                    double local = (t - prev.t()) / Math.max(1E-6, next.t() - prev.t());
                    return new CurveFitting.DrawPoint(t, GuiDrawUtil.lerp(prev.x(), next.x(), local), 0, GuiDrawUtil.lerp(prev.z(), next.z(), local));
                }
                prev = next;
            }
            CurveFitting.DrawPoint last = xzPoints.get(xzPoints.size() - 1);
            return new CurveFitting.DrawPoint(t, last.x(), 0, last.z());
        }
        EditorCamera.Basis basis = EditorCamera.basisOf(rail);
        return new CurveFitting.DrawPoint(t, p1.getX() + 0.5 + basis.ax() * basis.len() * t, 0, p1.getZ() + 0.5 + basis.az() * basis.len() * t);
    }

    private double railLength() {
        return EditorCamera.basisOf(rail).len();
    }

    private void build() {
        rail = RailRebuildHelper.resolveClientRail(cn.jstxjf_.rte.bridge.MtrTypes.blockPos(posStart), cn.jstxjf_.rte.bridge.MtrTypes.blockPos(posEnd), rail);
        if (minecraft == null || minecraft.level == null || rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        final World world = new World(minecraft.level);
        final CurveFitting.Result result = CurveFitting.fit(world == null ? null : world.data, rail, xzPoints, yPoints);
        if (!result.accepted() || result.curve() == null) {
            setStatus(result.message(), true);
            return;
        }
        extra.setPathModeRaw(RailExtraSupplier.PATH_MODE_CURVE_FITTING);
        extra.setBezier(result.curve());
        extra.setExtraAngle1Rad(result.angleStart());
        extra.setExtraAngle2Rad(Math.atan2(Math.sin(result.angleEnd() + Math.PI), Math.cos(result.angleEnd() + Math.PI)));
        RailAngleExtra.rebuildRailMathFromBezier(rail, result.curve());
        PacketUpdateRail.sendUpdateC2S(rail);
        float startDirDeg = (float) Math.toDegrees(result.angleStart());
        float endDirDeg = (float) Math.toDegrees(result.angleEnd() + Math.PI);
        if (canWriteNodeAngle(posStart)) {
            PacketReplaceRailNode.sendUpdateC2S(world.data, posStart.data, startDirDeg, true, true);
        }
        if (canWriteNodeAngle(posEnd)) {
            PacketReplaceRailNode.sendUpdateC2S(world.data, posEnd.data, endDirDeg, true, true);
        }
        setStatus(Component.translatable("gui.rte.curve_fitting.built").getString(), false);
    }

    private boolean canWriteNodeAngle(BlockPos pos) {
        if (minecraft == null || minecraft.level == null || pos == null) {
            return true;
        }
        final net.minecraft.world.level.block.entity.BlockEntity blockEntity = minecraft.level.getBlockEntity(new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        if (blockEntity instanceof cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode directNode) {
            return !directNode.isBound() || directNode.isFittingNode();
        }
        return true;
    }

    private void deleteHoveredRail() {
        if (hoveredRail != null) {
            PacketDeleteRail.sendUpdateC2S(hoveredRail);
            setStatus(Component.translatable("gui.rte.curve_fitting.deleted", hoveredRail.getHexId()).getString(), false);
            hoveredRail = null;
        }
    }

    private void switchToNextMode() {
        rail = RailRebuildHelper.resolveClientRail(cn.jstxjf_.rte.bridge.MtrTypes.blockPos(posStart), cn.jstxjf_.rte.bridge.MtrTypes.blockPos(posEnd), rail);
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        extra.setPathModeRaw(RailExtraSupplier.PATH_MODE_ORIGINAL);
        extra.setBezier(null);
        PacketUpdateRail.sendUpdateC2S(rail);
        Screen next = RailPathEditorScreen.createScreen(posStart, posEnd, rail, parent);
        minecraft.setScreen(next == null ? null : next.data);
    }

    private void setStatus(String message, boolean isError) {
        this.statusMessage = message;
        this.statusIsError = isError;
        this.statusTime = System.currentTimeMillis();
    }

    private void renderXzPoints(GuiGraphics graphics) {
        if (xzPoints.isEmpty()) return;
        CurveFitting.DrawPoint prev = null;
        for (CurveFitting.DrawPoint point : xzPoints) {
            int[] s = camera.xzToScreen(point.x(), point.z());
            GuiDrawUtil.fillRect(s[0] - 2, s[1] - 2, s[0] + 3, s[1] + 3, 0xFFFFFF33);
            if (prev != null) {
                int[] sp = camera.xzToScreen(prev.x(), prev.z());
                GuiDrawUtil.drawLine(graphics, sp[0], sp[1], s[0], s[1], 0xFFFFFF33);
            }
            prev = point;
        }
    }

    private void renderCursor(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isInCanvas(mouseX, mouseY)) return;
        int color;
        int size;
        if (currentTool == Tool.DELETE) {
            color = hoveredRail != null ? 0xFFFF3333 : 0xAAFF6666;
            size = 3;
        } else if (isDrawing) {
            color = 0xFFFFFF66;
            size = 3;
        } else {
            color = 0xCCFFFFFF;
            size = 2;
        }
        drawCrosshair(graphics, mouseX, mouseY, size, color);

        if (currentTool == Tool.DRAW) {
            xzView.renderSnapIndicator(graphics, mouseX, mouseY, rail, SNAP_RADIUS_PIXELS);
        }
    }

    private void renderYModeCursor(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isInCanvas(mouseX, mouseY)) return;
        int color = isDrawing ? 0xFFFFFF66 : 0xCCFFFFFF;
        int size = isDrawing ? 3 : 2;
        drawCrosshair(graphics, mouseX, mouseY, size, color);
        if (currentTool == Tool.DRAW) {
            renderYSnapIndicator(graphics, mouseX, mouseY);
        }
    }

    private void drawCrosshair(GuiGraphics graphics, int mouseX, int mouseY, int size, int color) {
        GuiDrawUtil.fillRect(mouseX - size, mouseY - 1, mouseX + size + 1, mouseY, color);
        GuiDrawUtil.fillRect(mouseX - size, mouseY + 1, mouseX + size + 1, mouseY + 2, color);
        GuiDrawUtil.fillRect(mouseX - 1, mouseY - size, mouseX, mouseY + size + 1, color);
        GuiDrawUtil.fillRect(mouseX + 1, mouseY - size, mouseX + 2, mouseY + size + 1, color);
    }

    private void renderYGraph(GuiGraphics graphics) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;
        graph.renderGraph(graphics, font, railLength(), p1.getY(), p2.getY());
    }

    private void renderYSnapIndicator(GuiGraphics graphics, int mouseX, int mouseY) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;
        graph.renderSnapIndicator(graphics, mouseX, mouseY, railLength(), p1.getY(), p2.getY(), SNAP_RADIUS_BLOCKS);
    }

    private void renderCursorCoords(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isInCanvas(mouseX, mouseY)) return;
        String coordText;
        if (planeMode == PlaneMode.XZ) {
            double[] world = camera.screenToWorld(mouseX, mouseY);
            coordText = String.format(java.util.Locale.ROOT, "X=%.1f  Z=%.1f", world[0], world[2]);
        } else {
            double[] graphPos = graph.toGraph(mouseX, mouseY);
            coordText = String.format(java.util.Locale.ROOT, "Along=%.1f  Y=%.1f", graphPos[0], graphPos[1]);
        }
        int tw = font.width(coordText);
        GuiDrawUtil.drawText(coordText, mouseX - tw / 2.0, mouseY - 14, 0xFFDDDDDD);
    }



    private boolean isInCanvas(double x, double y) {
        return x >= 0 && x < width - 128 && y >= 0 && y < height;
    }
}
