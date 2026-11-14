package cn.jstxjf_.rte.gui;

//#if MC>=12000
import net.minecraft.client.gui.GuiGraphics;
//#else
import cn.jstxjf_.rte.compat.GuiGraphics;
//#endif
import cn.jstxjf_.rte.data.BezierCurve;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.network.PacketReplaceRailNode;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import net.minecraft.core.BlockPos;
import org.mtr.mapping.holder.Screen;
import org.mtr.mapping.holder.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ManualBezierPathEditorScreen extends net.minecraft.client.gui.screens.Screen implements cn.jstxjf_.rte.gui.imgui.ImGuiMouseRouter.Handler {

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

    private static final int CURVE_SAMPLES = 200;
    private static final double HANDLE_PICK_DIST_SQ = 100;

    private final BlockPos posStart;
    private final BlockPos posEnd;
    private Rail rail;
    private final Supplier<Screen> parent;
    private PlaneMode planeMode = PlaneMode.XZ;
    private final List<BezierCurve.Vec3> controlPoints = new ArrayList<>();
    private final List<List<BezierCurve.Vec3>> undoStack = new ArrayList<>();
    private int dragIndex = -1;
    private int hoverIndex = -1;
    private double panX = 0;
    private double panZ = 0;
    private double panY = 0;
    private double zoom = 1.0;
    private String statusMessage = "";
    private boolean statusIsError = false;
    private long statusTime = 0;

    private Vec3 savedPlayerPos;
    private float savedYaw;
    private float savedPitch;
    private boolean savedFlying;
    private boolean savedHideGui;

    public ManualBezierPathEditorScreen(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
        super(Component.translatable("tooltip.rte.rail.path_mode.manual_bezier"));
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.rail = RailRebuildHelper.resolveClientRail(posStart, posEnd, rail);
        this.parent = parent == null ? () -> null : parent;
        initControlPoints();
    }

    public static Screen createScreen(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
        return new Screen(new ManualBezierPathEditorScreen(posStart, posEnd, rail, parent));
    }

    private void initControlPoints() {
        controlPoints.clear();
        if (rail != null && ((Object) rail) instanceof RailExtraSupplier extra && extra.getBezierCurve() != null) {
            for (BezierCurve.Vec3 point : extra.getBezierCurve().getPoints()) {
                controlPoints.add(new BezierCurve.Vec3(point));
            }
        }
        if (controlPoints.size() < 4) {
            controlPoints.clear();
            generateDefaultControls();
        }
    }

    private void generateDefaultControls() {
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;
        double dx = p2.getX() - p1.getX();
        double dz = p2.getZ() - p1.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double handleLen = Math.max(1, dist / 3.0);

        double a1 = extra.getExtraAngle1() != null ? extra.getExtraAngle1().angleRadians : Math.atan2(dz, dx);
        double a2 = extra.getExtraAngle2() != null ? extra.getExtraAngle2().angleRadians : Math.atan2(-dz, -dx);

        controlPoints.add(new BezierCurve.Vec3(p1.getX(), p1.getY(), p1.getZ()));
        controlPoints.add(new BezierCurve.Vec3(
                p1.getX() + Math.cos(a1) * handleLen,
                p1.getY() + (p2.getY() - p1.getY()) / 3.0,
                p1.getZ() + Math.sin(a1) * handleLen));
        controlPoints.add(new BezierCurve.Vec3(
                p2.getX() + Math.cos(a2) * handleLen,
                p2.getY() - (p2.getY() - p1.getY()) / 3.0,
                p2.getZ() + Math.sin(a2) * handleLen));
        controlPoints.add(new BezierCurve.Vec3(p2.getX(), p2.getY(), p2.getZ()));
    }

    @Override
    protected void init() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && savedPlayerPos == null) {
            savedPlayerPos = mc.player.position();
            savedYaw = mc.player.getYRot();
            savedPitch = mc.player.getXRot();
            savedFlying = mc.player.getAbilities().flying;
            savedHideGui = mc.options.hideGui;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().mayfly = true;
            mc.options.hideGui = true;
        }

        orientCamera();
        centerView();
    }

    @Override
    public void tick() {
        orientCamera();
    }

    @Override
    //#if MC>=12002
    public void renderBackground(GuiGraphics graphics, int rteMouseX, int rteMouseY, float rteDelta) {
    //#elseif MC>=12000
    public void renderBackground(GuiGraphics graphics) {
    //#else
    public void renderBackground(com.mojang.blaze3d.vertex.PoseStack rtePoseStack) {
        final GuiGraphics graphics = new GuiGraphics(rtePoseStack);
    //#endif
    }

    @Override
    //#if MC>=12000
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    //#else
    public void render(com.mojang.blaze3d.vertex.PoseStack rtePoseStack, int mouseX, int mouseY, float partialTick) {
        final GuiGraphics graphics = new GuiGraphics(rtePoseStack);
    //#endif
        super.render(cn.jstxjf_.rte.compat.GuiArgs.of(graphics), mouseX, mouseY, partialTick);
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.beginFrame();
        renderCanvasBackground(graphics);
        renderGrid(graphics);
        renderEndpoints(graphics);
        renderCurvePreview(graphics);
        renderHandleLines(graphics);
        updateHover(mouseX, mouseY);
        renderHandles(graphics);
        rte$mouseRouter.poll(this);
        buildImGuiPanel();
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.endFrame();
    }

    private void buildImGuiPanel() {
        final float rte$scale = cn.jstxjf_.rte.gui.imgui.CanvasPainter.guiScale();
        imgui.ImGui.setNextWindowPos(8 * rte$scale, 4 * rte$scale, imgui.flag.ImGuiCond.Always);
        imgui.ImGui.begin("###rte_manual_bezier_panel", cn.jstxjf_.rte.gui.imgui.CanvasPainter.toolbarFlags());

        if (imgui.ImGui.button((planeMode == PlaneMode.XZ ? "XZ" : "Y") + "###mb_plane")) {
            togglePlane();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(Component.translatable("gui.rte.curve_fitting.build").getString() + "###mb_build")) {
            build();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("Undo###mb_undo")) {
            undo();
        }

        if (imgui.ImGui.button(Component.translatable("gui.rte.manual_bezier.add_segment").getString() + "###mb_add")) {
            addSegment();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(Component.translatable("gui.rte.manual_bezier.remove_segment").getString() + "###mb_remove")) {
            removeSegment();
        }

        if (imgui.ImGui.button(Component.translatable("gui.rte.manual_bezier.reset").getString() + "###mb_reset")) {
            resetControls();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(Component.translatable("tooltip.rte.rail.path_mode.fitting").getString() + "###mb_to_fitting")) {
            switchToNextMode();
        }

        imgui.ImGui.end();

        imgui.ImGui.setNextWindowPos(8 * rte$scale, (height - 26) * rte$scale, imgui.flag.ImGuiCond.Always);
        imgui.ImGui.begin("###rte_manual_bezier_status", cn.jstxjf_.rte.gui.imgui.CanvasPainter.toolbarFlags());
        final int segCount = controlPoints.size() >= 4 ? (controlPoints.size() - 1) / 3 : 0;
        imgui.ImGui.text(planeMode + " | Pts=" + controlPoints.size() + " Seg=" + segCount);
        imgui.ImGui.sameLine();
        try {
            if (controlPoints.size() >= 4) {
                final BezierCurve preview = new BezierCurve(0.1, new ArrayList<>(controlPoints));
                if (Double.isFinite(preview.getLength())) {
                    imgui.ImGui.text(String.format(java.util.Locale.ROOT, "| Len=%.1fm  Zoom=%.0f%%", preview.getLength(), zoom * 100));
                }
            }
        } catch (Exception ignored) {
        }
        imgui.ImGui.separator();
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("-###mb_zoom_out")) {
            zoom = Math.max(0.1, zoom / 1.25);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("+###mb_zoom_in")) {
            zoom = Math.min(10.0, zoom * 1.25);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(Component.translatable("gui.rte.rail_path_editor.fit").getString() + "###mb_fit")) {
            centerView();
        }

        if (imgui.ImGui.collapsingHeader(Component.translatable("gui.rte.rail_path_editor.hints").getString() + "###mb_hints")) {
            imgui.ImGui.textWrapped(Component.translatable("gui.rte.imgui.canvas_hint").getString());
        }

        if (!statusMessage.isEmpty()) {
            if (System.currentTimeMillis() - statusTime > 5000) {
                statusMessage = "";
            } else if (statusIsError) {
                imgui.ImGui.textColored(1f, 0.4f, 0.4f, 1f, statusMessage);
            } else {
                imgui.ImGui.textColored(0.65f, 1f, 0.65f, 1f, statusMessage);
            }
        }

        imgui.ImGui.separator();
        if (imgui.ImGui.button(Component.translatable("gui.rte.imgui.back").getString() + "###mb_back")) {
            onClose();
        }
        imgui.ImGui.end();
    }

    private void resetControls() {
        saveUndo();
        controlPoints.clear();
        generateDefaultControls();
        setStatus(Component.translatable("gui.rte.manual_bezier.reset_done").getString(), false);
    }

    private boolean imGuiCapturesMouse() {
        return imgui.ImGui.getIO().getWantCaptureMouse();
    }

    @Override
    public boolean onCanvasDown(double mouseX, double mouseY, int button) {
        if (imGuiCapturesMouse()) return true;
        if (false) return true;
        if (!isInCanvas(mouseX, mouseY)) return false;
        if (button == 0) {
            int idx = findClosestHandle(mouseX, mouseY);
            if (idx >= 0 && !isEndpoint(idx)) {
                saveUndo();
                dragIndex = idx;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCanvasDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (imGuiCapturesMouse() && dragIndex < 0) return true;
        if (button == 0 && dragIndex >= 0) {
            moveHandle(dragIndex, mouseX, mouseY);
            return true;
        }
        if (button == 1) {
            double worldDx = dragX / pixelsPerBlock();
            double worldDy = dragY / pixelsPerBlock();
            if (planeMode == PlaneMode.XZ) {
                Basis basis = railBasis();
                panX -= worldDx * basis.nx + worldDy * basis.ax;
                panZ -= worldDx * basis.nz + worldDy * basis.az;
            } else {
                Basis basis = railBasis();
                panX -= worldDx * basis.ax;
                panZ -= worldDx * basis.az;
                panY += worldDy;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCanvasUp(double mouseX, double mouseY, int button) {
        if (button == 0 && dragIndex >= 0) {
            dragIndex = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean onCanvasScroll(double mouseX, double mouseY, double amount) {
        if (imGuiCapturesMouse()) return true;
        if (hasControlDown()) {
            double factor = amount > 0 ? 1.15 : 1.0 / 1.15;
            zoom = clamp(zoom * factor, 0.1, 10.0);
            return true;
        }
        if (planeMode == PlaneMode.Y) {
            panY += amount * 0.5;
        }
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
        restorePlayerState();
        final Screen previous = parent.get();
        minecraft.setScreen(previous == null ? null : previous.data);
    }

    @Override
    public void removed() {
        restorePlayerState();
        super.removed();
    }

    private void restorePlayerState() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && savedPlayerPos != null) {
            mc.player.setPos(savedPlayerPos.x, savedPlayerPos.y, savedPlayerPos.z);
            mc.player.xo = savedPlayerPos.x;
            mc.player.yo = savedPlayerPos.y;
            mc.player.zo = savedPlayerPos.z;
            mc.player.xOld = savedPlayerPos.x;
            mc.player.yOld = savedPlayerPos.y;
            mc.player.zOld = savedPlayerPos.z;
            mc.player.setYRot(savedYaw);
            mc.player.setXRot(savedPitch);
            mc.player.yRotO = savedYaw;
            mc.player.xRotO = savedPitch;
            mc.player.yHeadRot = savedYaw;
            mc.player.yHeadRotO = savedYaw;
            mc.player.getAbilities().flying = savedFlying;
            mc.player.setDeltaMovement(0, 0, 0);
            mc.player.fallDistance = 0;
            mc.options.hideGui = savedHideGui;
            savedPlayerPos = null;
        }
    }

    private void togglePlane() {
        planeMode = planeMode == PlaneMode.XZ ? PlaneMode.Y : PlaneMode.XZ;
        orientCamera();
    }

    private void saveUndo() {
        List<BezierCurve.Vec3> snapshot = new ArrayList<>();
        for (BezierCurve.Vec3 p : controlPoints) snapshot.add(new BezierCurve.Vec3(p));
        undoStack.add(snapshot);
        if (undoStack.size() > 30) undoStack.remove(0);
    }

    private void undo() {
        if (undoStack.isEmpty()) return;
        List<BezierCurve.Vec3> snapshot = undoStack.remove(undoStack.size() - 1);
        controlPoints.clear();
        controlPoints.addAll(snapshot);
    }

    private void switchToNextMode() {
        rail = RailRebuildHelper.resolveClientRail(posStart, posEnd, rail);
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        extra.setPathModeRaw(RailExtraSupplier.PATH_MODE_CURVE_FITTING);
        extra.setBezier(null);
        extra.setExtraAngle1Rad(Double.NaN);
        extra.setExtraAngle2Rad(Double.NaN);
        PacketUpdateRail.sendUpdateC2S(rail);
        Screen next = RailPathEditorScreen.createScreen(posStart, posEnd, rail, parent);
        minecraft.setScreen(next == null ? null : next.data);
    }

    private void setStatus(String message, boolean isError) {
        this.statusMessage = message;
        this.statusIsError = isError;
        this.statusTime = System.currentTimeMillis();
    }

    private int findClosestHandle(double mouseX, double mouseY) {
        double bestDistSq = HANDLE_PICK_DIST_SQ;
        int bestIndex = -1;
        for (int i = 0; i < controlPoints.size(); i++) {
            if (isEndpoint(i)) continue;
            int[] s = worldToScreen(controlPoints.get(i));
            double dx = mouseX - s[0];
            double dy = mouseY - s[1];
            double distSq = dx * dx + dy * dy;
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private void updateHover(double mouseX, double mouseY) {
        hoverIndex = isInCanvas(mouseX, mouseY) ? findClosestHandle(mouseX, mouseY) : -1;
    }

    private boolean isEndpoint(int index) {
        return index == 0 || index == controlPoints.size() - 1;
    }

    private boolean isAnchor(int index) {
        if (controlPoints.size() >= 4 && (controlPoints.size() - 1) % 3 == 0) {
            return index % 3 == 0;
        }
        return isEndpoint(index);
    }

    private void moveHandle(int index, double mouseX, double mouseY) {
        if (index < 0 || index >= controlPoints.size() || isEndpoint(index)) return;

        double[] world = screenToWorld(mouseX, mouseY);
        BezierCurve.Vec3 old = controlPoints.get(index);

        if (planeMode == PlaneMode.XZ) {
            controlPoints.set(index, new BezierCurve.Vec3(world[0], old.y, world[2]));
        } else {
            controlPoints.set(index, new BezierCurve.Vec3(old.x, world[1], old.z));
        }

        if (!isAnchor(index)) {
            mirrorOppositeHandle(index);
        }
    }

    private void mirrorOppositeHandle(int handleIndex) {
        int segmentCount = (controlPoints.size() - 1) / 3;
        if (segmentCount < 2) return;

        int localIdx = handleIndex % 3;
        int anchorIndex;
        int oppositeIndex;

        if (localIdx == 1) {
            anchorIndex = handleIndex - 1;
            oppositeIndex = handleIndex - 2;
        } else if (localIdx == 2) {
            anchorIndex = handleIndex + 1;
            oppositeIndex = handleIndex + 2;
        } else {
            return;
        }

        if (oppositeIndex < 0 || oppositeIndex >= controlPoints.size()) return;
        if (isEndpoint(anchorIndex)) return;

        BezierCurve.Vec3 anchor = controlPoints.get(anchorIndex);
        BezierCurve.Vec3 handle = controlPoints.get(handleIndex);
        double dx = handle.x - anchor.x;
        double dy = handle.y - anchor.y;
        double dz = handle.z - anchor.z;

        BezierCurve.Vec3 opposite = controlPoints.get(oppositeIndex);
        double oppLen = Math.sqrt(
                (opposite.x - anchor.x) * (opposite.x - anchor.x) +
                (opposite.y - anchor.y) * (opposite.y - anchor.y) +
                (opposite.z - anchor.z) * (opposite.z - anchor.z));
        double handleLen = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (handleLen < 1E-6) return;
        double scale = oppLen / handleLen;
        controlPoints.set(oppositeIndex, new BezierCurve.Vec3(
                anchor.x - dx * scale,
                anchor.y - dy * scale,
                anchor.z - dz * scale));
    }

    private void addSegment() {
        if (controlPoints.size() < 4) return;
        saveUndo();
        int last = controlPoints.size() - 1;
        BezierCurve.Vec3 lastCtrl = controlPoints.get(last - 1);
        BezierCurve.Vec3 endPoint = controlPoints.get(last);

        BezierCurve.Vec3 midPoint = new BezierCurve.Vec3(
                (lastCtrl.x + endPoint.x) / 2,
                (lastCtrl.y + endPoint.y) / 2,
                (lastCtrl.z + endPoint.z) / 2);

        BezierCurve.Vec3 mirrorCtrl = new BezierCurve.Vec3(
                2 * midPoint.x - lastCtrl.x,
                2 * midPoint.y - lastCtrl.y,
                2 * midPoint.z - lastCtrl.z);

        BezierCurve.Vec3 newCtrl = new BezierCurve.Vec3(
                (midPoint.x + endPoint.x) / 2,
                (midPoint.y + endPoint.y) / 2,
                (midPoint.z + endPoint.z) / 2);

        controlPoints.set(last, midPoint);
        controlPoints.add(mirrorCtrl);
        controlPoints.add(newCtrl);
        controlPoints.add(endPoint);

        setStatus(Component.translatable("gui.rte.manual_bezier.segment_added",
                (controlPoints.size() - 1) / 3).getString(), false);
    }

    private void removeSegment() {
        int segmentCount = (controlPoints.size() - 1) / 3;
        if (segmentCount <= 1) {
            setStatus(Component.translatable("gui.rte.manual_bezier.min_segments").getString(), true);
            return;
        }
        saveUndo();
        BezierCurve.Vec3 endPoint = controlPoints.get(controlPoints.size() - 1);
        controlPoints.remove(controlPoints.size() - 1);
        controlPoints.remove(controlPoints.size() - 1);
        controlPoints.remove(controlPoints.size() - 1);
        controlPoints.set(controlPoints.size() - 1, endPoint);
        setStatus(Component.translatable("gui.rte.manual_bezier.segment_removed",
                (controlPoints.size() - 1) / 3).getString(), false);
    }

    private void build() {
        rail = RailRebuildHelper.resolveClientRail(posStart, posEnd, rail);
        if (minecraft == null || minecraft.level == null || rail == null
                || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        if (controlPoints.size() < 4 || (controlPoints.size() - 1) % 3 != 0) {
            setStatus(Component.translatable("gui.rte.manual_bezier.invalid_points").getString(), true);
            return;
        }

        snapEndpointTangentsToNeighbours(extra);

        BezierCurve curve = new BezierCurve(0.1, new ArrayList<>(controlPoints));
        if (!Double.isFinite(curve.getLength()) || curve.getLength() < 1) {
            setStatus(Component.translatable("gui.rte.manual_bezier.invalid_curve").getString(), true);
            return;
        }

        BezierCurve.Vec3 start = controlPoints.get(0);
        BezierCurve.Vec3 c1 = controlPoints.get(1);
        BezierCurve.Vec3 cLast = controlPoints.get(controlPoints.size() - 2);
        BezierCurve.Vec3 end = controlPoints.get(controlPoints.size() - 1);
        double angleStart = Math.atan2(c1.z - start.z, c1.x - start.x);
        double angleEnd = Math.atan2(end.z - cLast.z, end.x - cLast.x);

        extra.setPathModeRaw(RailExtraSupplier.PATH_MODE_MANUAL_BEZIER);
        extra.setBezier(curve);
        extra.setExtraAngle1Rad(angleStart);
        extra.setExtraAngle2Rad(Math.atan2(Math.sin(angleEnd + Math.PI), Math.cos(angleEnd + Math.PI)));
        RailAngleExtra.rebuildRailMathFromBezier(rail, curve);

        PacketUpdateRail.sendUpdateC2S(rail);
        setStatus(Component.translatable("gui.rte.curve_fitting.built").getString(), false);
    }

    private void snapEndpointTangentsToNeighbours(RailExtraSupplier extra) {
        final int last = controlPoints.size() - 1;
        final Double tangentStart = neighbourTangentRadians(extra.getExtraPosition1(), rail);
        final Double tangentEnd = neighbourTangentRadians(extra.getExtraPosition2(), rail);
        if (tangentStart != null) {
            alignHandle(0, 1, tangentStart + Math.PI);
        }
        if (tangentEnd != null) {
            alignHandle(last, last - 1, tangentEnd + Math.PI);
        }
    }

    private void alignHandle(int endpointIndex, int handleIndex, double angleRadians) {
        final BezierCurve.Vec3 endpoint = controlPoints.get(endpointIndex);
        final BezierCurve.Vec3 handle = controlPoints.get(handleIndex);
        final double dx = handle.x - endpoint.x;
        final double dz = handle.z - endpoint.z;
        final double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < 1e-6) {
            return;
        }
        controlPoints.set(handleIndex, new BezierCurve.Vec3(
            endpoint.x + distance * Math.cos(angleRadians),
            handle.y,
            endpoint.z + distance * Math.sin(angleRadians)));
    }

    private Double neighbourTangentRadians(org.mtr.core.data.Position node, Rail self) {
        if (node == null || self == null) {
            return null;
        }
        try {
            final var byEnd = org.mtr.mod.client.MinecraftClientData.getInstance().positionsToRail.get(node);
            if (byEnd == null) {
                return null;
            }
            final String selfHex = self.getHexId();
            for (final var entry : byEnd.entrySet()) {
                final Rail neighbour = entry.getValue();
                if (neighbour == null || neighbour.getHexId().equals(selfHex)
                        || !(((Object) neighbour) instanceof RailExtraSupplier neighbourExtra)) {
                    continue;
                }
                final double length = neighbourExtra.getLength();
                if (!Double.isFinite(length) || length < 0.2) {
                    continue;
                }
                final double eps = Math.min(0.5, length * 0.05);
                final boolean atP1 = node.equals(neighbourExtra.getExtraPosition1());
                final boolean atP2 = node.equals(neighbourExtra.getExtraPosition2());
                if (!atP1 && !atP2) {
                    continue;
                }
                final net.minecraft.world.phys.Vec3 atNode = neighbourExtra.getPosition(atP1 ? 0 : length);
                final net.minecraft.world.phys.Vec3 inward = neighbourExtra.getPosition(atP1 ? eps : length - eps);
                if (atNode == null || inward == null) {
                    continue;
                }
                final double dx = inward.x - atNode.x;
                final double dz = inward.z - atNode.z;
                if (Math.abs(dx) < 1e-9 && Math.abs(dz) < 1e-9) {
                    continue;
                }
                return Math.atan2(dz, dx);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void orientCamera() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || posStart == null || posEnd == null) return;
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;
        Basis basis = railBasis();
        double midX = (p1.getX() + p2.getX()) / 2.0;
        double midZ = (p1.getZ() + p2.getZ()) / 2.0;

        float yaw;
        float pitch;
        double camX, camY, camZ;
        double camDist = 60;

        if (planeMode == PlaneMode.XZ) {
            yaw = (float) -Math.toDegrees(Math.atan2(basis.ax, basis.az));
            pitch = 90;
            camX = midX + panX;
            camZ = midZ + panZ;
            camY = p1.getY() + camDist;
        } else {
            yaw = (float) -Math.toDegrees(Math.atan2(basis.ax, basis.az)) + 90;
            pitch = 0;
            camX = midX + basis.az * camDist + panX;
            camZ = midZ - basis.ax * camDist + panZ;
            camY = (p1.getY() + p2.getY()) / 2.0 + panY;
        }

        mc.player.setPos(camX, camY, camZ);
        mc.player.xo = camX;
        mc.player.yo = camY;
        mc.player.zo = camZ;
        mc.player.xOld = camX;
        mc.player.yOld = camY;
        mc.player.zOld = camZ;
        mc.player.setDeltaMovement(0, 0, 0);
        mc.player.fallDistance = 0;
        mc.player.setYRot(yaw);
        mc.player.setXRot(pitch);
        mc.player.yRotO = yaw;
        mc.player.xRotO = pitch;
        mc.player.yHeadRot = yaw;
        mc.player.yHeadRotO = yaw;
    }

    private void centerView() {
        panX = 0;
        panZ = 0;
        panY = 0;
        double rLen = railBasis().len;
        double canvasBlocks = canvasWidth() / basePixelsPerBlock();
        if (rLen > canvasBlocks * 0.8) {
            zoom = canvasBlocks * 0.8 / rLen;
        }
    }

    private void renderCanvasBackground(GuiGraphics graphics) {
        cn.jstxjf_.rte.gui.imgui.CanvasPainter.drawBackground(canvasLeft(), canvasTop(), canvasRight(), canvasBottom());
    }

    private void renderGrid(GuiGraphics graphics) {
        cn.jstxjf_.rte.gui.imgui.CanvasPainter.drawGrid(canvasLeft(), canvasTop(), canvasRight(), canvasBottom(),
            canvasCenterX(), canvasCenterY(), pixelsPerBlock());
    }

    private void renderEndpoints(GuiGraphics graphics) {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;

        int[] s1 = worldToScreen(new BezierCurve.Vec3(p1.getX(), p1.getY(), p1.getZ()));
        int[] s2 = worldToScreen(new BezierCurve.Vec3(p2.getX(), p2.getY(), p2.getZ()));

        Basis basis = railBasis();
        int steps = 40;
        int prevX = s1[0], prevY = s1[1];
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            double wx = p1.getX() + basis.ax * basis.len * t;
            double wz = p1.getZ() + basis.az * basis.len * t;
            double wy = lerp(p1.getY(), p2.getY(), t);
            int[] s = worldToScreen(new BezierCurve.Vec3(wx, wy, wz));
            drawLine(graphics, prevX, prevY, s[0], s[1], 0x22FFFFFF);
            prevX = s[0];
            prevY = s[1];
        }
    }

    private void renderCurvePreview(GuiGraphics graphics) {
        if (controlPoints.size() < 4) return;
        try {
            BezierCurve preview = new BezierCurve(0.1, new ArrayList<>(controlPoints));
            double len = preview.getLength();
            if (!Double.isFinite(len) || len < 0.01) return;

            int prevSx = -1, prevSy = -1;
            for (int i = 0; i <= CURVE_SAMPLES; i++) {
                double arcT = len * i / CURVE_SAMPLES;
                BezierCurve.Vec3 pos = preview.getPosition(arcT);
                int[] s = worldToScreen(pos);
                if (prevSx >= 0) {
                    drawLine(graphics, prevSx, prevSy, s[0], s[1], 0xFF00FF00);
                }
                prevSx = s[0];
                prevSy = s[1];
            }
        } catch (Exception ignored) {
        }
    }

    private void renderHandleLines(GuiGraphics graphics) {
        if (controlPoints.size() < 4 || (controlPoints.size() - 1) % 3 != 0) return;
        int segmentCount = (controlPoints.size() - 1) / 3;
        for (int seg = 0; seg < segmentCount; seg++) {
            int base = seg * 3;
            int[] sAnchor = worldToScreen(controlPoints.get(base));
            int[] sHandle = worldToScreen(controlPoints.get(base + 1));
            drawLine(graphics, sAnchor[0], sAnchor[1], sHandle[0], sHandle[1], 0x66AAAAAA);

            int[] sAnchorEnd = worldToScreen(controlPoints.get(base + 3));
            int[] sHandleEnd = worldToScreen(controlPoints.get(base + 2));
            drawLine(graphics, sAnchorEnd[0], sAnchorEnd[1], sHandleEnd[0], sHandleEnd[1], 0x66AAAAAA);
        }
    }

    private void renderHandles(GuiGraphics graphics) {
        for (int i = 0; i < controlPoints.size(); i++) {
            int[] s = worldToScreen(controlPoints.get(i));
            int color;
            int radius;
            if (isEndpoint(i)) {
                color = i == 0 ? 0xFF00FF00 : 0xFFFF4444;
                radius = 5;
            } else if (isAnchor(i)) {
                color = 0xFFFFAA00;
                radius = 5;
            } else {
                color = 0xFF44AAFF;
                radius = 4;
            }
            if (i == dragIndex) color = 0xFFFFFFFF;
            else if (i == hoverIndex) color = 0xFFFFFF88;

            if (isEndpoint(i)) {
                drawCircle(graphics, s[0], s[1], radius, color);
            } else if (isAnchor(i)) {
                drawCircle(graphics, s[0], s[1], radius, color);
            } else {
                drawDiamond(graphics, s[0], s[1], radius, color);
            }
        }
    }

    private double[] screenToWorld(double mouseX, double mouseY) {
        double ppb = pixelsPerBlock();
        int cx = canvasCenterX();
        int cy = canvasCenterY();

        if (planeMode == PlaneMode.XZ) {
            RailExtraSupplier extra = (RailExtraSupplier) (Object) rail;
            Position p1 = extra.getExtraPosition1();
            if (p1 == null) return new double[]{0, 0, 0};
            Basis basis = railBasis();
            double midX = p1.getX() + basis.ax * basis.len * 0.5 + panX;
            double midZ = p1.getZ() + basis.az * basis.len * 0.5 + panZ;
            double screenDx = (mouseX - cx) / ppb;
            double screenDy = (mouseY - cy) / ppb;
            return new double[]{
                    midX + screenDx * basis.nx - screenDy * basis.ax,
                    0,
                    midZ + screenDx * basis.nz - screenDy * basis.az};
        } else {
            RailExtraSupplier extra = (RailExtraSupplier) (Object) rail;
            Position p1 = extra.getExtraPosition1();
            if (p1 == null) return new double[]{0, 0, 0};
            Basis basis = railBasis();
            double along = (mouseX - cx) / ppb + basis.len * 0.5 + panX * basis.ax + panZ * basis.az;
            double midYVal = midY();
            double wy = midYVal + panY + (cy - mouseY) / ppb;
            return new double[]{
                    p1.getX() + basis.ax * along,
                    wy,
                    p1.getZ() + basis.az * along};
        }
    }

    private int[] worldToScreen(BezierCurve.Vec3 point) {
        double ppb = pixelsPerBlock();
        int cx = canvasCenterX();
        int cy = canvasCenterY();

        if (planeMode == PlaneMode.XZ) {
            RailExtraSupplier extra = (RailExtraSupplier) (Object) rail;
            Position p1 = extra.getExtraPosition1();
            if (p1 == null) return new int[]{cx, cy};
            Basis basis = railBasis();
            double midX = p1.getX() + basis.ax * basis.len * 0.5 + panX;
            double midZ = p1.getZ() + basis.az * basis.len * 0.5 + panZ;
            double dx = point.x - midX;
            double dz = point.z - midZ;
            double screenX = dx * basis.nx + dz * basis.nz;
            double screenY = -(dx * basis.ax + dz * basis.az);
            return new int[]{cx + (int) (screenX * ppb), cy + (int) (screenY * ppb)};
        } else {
            RailExtraSupplier extra = (RailExtraSupplier) (Object) rail;
            Position p1 = extra.getExtraPosition1();
            if (p1 == null) return new int[]{cx, cy};
            Basis basis = railBasis();
            double along = (point.x - p1.getX()) * basis.ax + (point.z - p1.getZ()) * basis.az;
            double screenX = (along - basis.len * 0.5 - panX * basis.ax - panZ * basis.az) * ppb;
            double screenY = -(point.y - midY() - panY) * ppb;
            return new int[]{cx + (int) screenX, cy + (int) screenY};
        }
    }

    private Basis railBasis() {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return new Basis(1, 0, 0, 1, 1);
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return new Basis(1, 0, 0, 1, 1);
        double dx = p2.getX() - p1.getX();
        double dz = p2.getZ() - p1.getZ();
        double len = Math.max(1, Math.sqrt(dx * dx + dz * dz));
        double ax = dx / len;
        double az = dz / len;
        return new Basis(ax, az, -az, ax, len);
    }

    private double midY() {
        if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) return 0;
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return 0;
        return (p1.getY() + p2.getY()) / 2.0;
    }

    private double basePixelsPerBlock() {
        return Math.max(2, canvasWidth() / Math.max(24, railBasis().len * 1.4));
    }

    private double pixelsPerBlock() {
        return basePixelsPerBlock() * zoom;
    }

    private boolean isInCanvas(double x, double y) {
        return x >= canvasLeft() && x < canvasRight() && y >= canvasTop() && y < canvasBottom();
    }

    private int canvasLeft() { return 8; }
    private int canvasTop() { return 34; }
    private int canvasRight() { return width - 8; }
    private int canvasBottom() { return height - 30; }
    private int canvasWidth() { return Math.max(1, canvasRight() - canvasLeft()); }
    private int canvasCenterX() { return (canvasLeft() + canvasRight()) / 2; }
    private int canvasCenterY() { return (canvasTop() + canvasBottom()) / 2; }

    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        int steps = Math.max(1, Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            int x = (int) Math.round(x1 + (x2 - x1) * t);
            int y = (int) Math.round(y1 + (y2 - y1) * t);
            graphics.fill(x, y, x + 1, y + 1, color);
        }
    }

    private void drawCircle(GuiGraphics graphics, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                if (dx * dx + dy * dy <= r * r) {
                    graphics.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
                }
            }
        }
    }

    private void drawDiamond(GuiGraphics graphics, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                if (Math.abs(dx) + Math.abs(dy) <= r) {
                    graphics.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
                }
            }
        }
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * clamp(t, 0, 1);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private record Basis(double ax, double az, double nx, double nz, double len) {}
}
