package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.data.BezierCurve;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import net.minecraft.core.BlockPos;
import org.mtr.mapping.holder.Screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RailPathEditorScreen extends net.minecraft.client.gui.screens.Screen implements cn.jstxjf_.rte.gui.imgui.ImGuiMouseRouter.Handler {

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

    public static Screen createScreen(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
        if (!cn.jstxjf_.rte.gui.imgui.RteImGuiHost.isAvailable()) {
            cn.jstxjf_.rte.gui.imgui.RteImGuiHost.notifyUnavailable();
            return parent != null ? parent.get() : null;
        }
        final Rail resolvedRail = RailRebuildHelper.resolveClientRail(posStart, posEnd, rail);
        if (!((Object) resolvedRail instanceof RailExtraSupplier extra)) {
            return parent != null ? parent.get() : null;
        }
        cn.jstxjf_.rte.RTE.LOGGER.info("[PathEditor] open id={} pathMode={} hasBezier={} sameRail={}",
            resolvedRail.getHexId(), extra.getPathMode(), extra.getBezierCurve() != null, resolvedRail == rail);
        return switch (extra.getPathMode()) {
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> new Screen(new Bezier(posStart, posEnd, resolvedRail, parent));
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> ManualBezierPathEditorScreen.createScreen(posStart, posEnd, resolvedRail, parent);
            case RailExtraSupplier.PATH_MODE_CURVE_FITTING -> CurveFittingPathEditorScreen.createScreen(posStart, posEnd, resolvedRail, parent);
            default -> new Screen(new Original(posStart, posEnd, resolvedRail, parent));
        };
    }

    protected final BlockPos posStart;
    protected final BlockPos posEnd;
    protected final Rail rail;
    protected final RailExtraSupplier extra;
    protected final Supplier<Screen> parent;
    private final List<String> infoLines = new ArrayList<>();
    private cn.jstxjf_.rte.data.RailGeometryAnalysis.Analysis analysis;

    private double panX = 0;
    private double panZ = 0;
    private double panY = 0;
    private double zoom = 1.0;
    private boolean showXZ = true;

    protected RailPathEditorScreen(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
        super(Component.translatable("gui.rte.rail_path_editor.title"));
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.rail = rail;
        this.extra = (RailExtraSupplier) (Object) rail;
        this.parent = parent != null ? parent : () -> null;
    }

    @Override
    protected void init() {
        refreshInfo();
        orientCamera();
        centerView();
    }

    protected void refreshInfo() {
        infoLines.clear();
        infoLines.add(getInfoText());
        infoLines.add(getHeightText());
        try {
            final var radii = rail.railMath.getHorizontalRadii();
            infoLines.add(String.format(java.util.Locale.ROOT, "R1=%.1f  R2=%.1f  VR=%.1f",
                radii.leftDouble(), radii.rightDouble(), rail.railMath.getVerticalRadius()));
        } catch (Exception ignored) {
        }
        try {
            analysis = cn.jstxjf_.rte.data.RailGeometryAnalysis.analyze(rail);
        } catch (Exception e) {
            analysis = null;
        }
    }

    @Override
    public void tick() {
        orientCamera();
        refreshInfo();
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
        super.render(graphics, mouseX, mouseY, partialTick);
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.beginFrame();
        rte$mouseRouter.poll(this);
        renderCanvasBackground(graphics);
        renderGrid(graphics);
        renderRailPath(graphics);
        renderBezierPreview(graphics);
        buildImGuiPanel();
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.endFrame();
    }

    private void buildImGuiPanel() {
        final float scale = guiScale();
        final int flags = imgui.flag.ImGuiWindowFlags.NoTitleBar
            | imgui.flag.ImGuiWindowFlags.NoResize
            | imgui.flag.ImGuiWindowFlags.NoMove
            | imgui.flag.ImGuiWindowFlags.AlwaysAutoResize
            | imgui.flag.ImGuiWindowFlags.NoScrollbar
            | imgui.flag.ImGuiWindowFlags.NoNavFocus;

        imgui.ImGui.setNextWindowPos(8 * scale, 4 * scale, imgui.flag.ImGuiCond.Always);
        imgui.ImGui.begin("###rpe_toolbar", flags);
        buildToolbar();
        imgui.ImGui.end();

        imgui.ImGui.setNextWindowPos(8 * scale, (height - 26) * scale, imgui.flag.ImGuiCond.Always);
        imgui.ImGui.begin("###rpe_status", flags);
        buildStatusBar();
        imgui.ImGui.end();
    }

    private void buildToolbar() {
        final boolean canSwitch = extra.couldSwitchModeTo(getNextMode(extra.getPathMode()));
        if (!canSwitch) {
            imgui.ImGui.beginDisabled();
        }
        if (imgui.ImGui.button(text("gui.rte.rail_path_editor.switch_to")
                .replace("%s", modeTextOf(getNextMode(extra.getPathMode())).getString()) + "###rpe_mode")) {
            changeMode();
        }
        if (!canSwitch) {
            imgui.ImGui.endDisabled();
        }
        if (imgui.ImGui.isItemHovered()) {
            imgui.ImGui.setTooltip(canSwitch
                ? text("gui.rte.rail_path_editor.current_mode").replace("%s", getModeTextComponent().getString())
                : text("gui.rte.rail_path_editor.mode_locked"));
        }

        imgui.ImGui.sameLine();
        if (imgui.ImGui.button((showXZ ? "XZ" : "Y") + "###rpe_plane")) {
            showXZ = !showXZ;
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("-###rpe_zoom_out")) {
            zoom = Math.max(0.1, zoom / 1.25);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("+###rpe_zoom_in")) {
            zoom = Math.min(10.0, zoom * 1.25);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(text("gui.rte.rail_path_editor.fit") + "###rpe_fit")) {
            centerView();
        }

        buildExtraPanelSection();

        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(text("gui.rte.imgui.back") + "###rpe_back")) {
            onClose();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("?###rpe_hints")) {
            imgui.ImGui.openPopup("###rpe_hints_popup");
        }
        if (imgui.ImGui.beginPopup("###rpe_hints_popup")) {
            imgui.ImGui.textWrapped(text("gui.rte.imgui.canvas_hint"));
            imgui.ImGui.endPopup();
        }
    }

    private void buildStatusBar() {
        for (int i = 0; i < infoLines.size(); i++) {
            if (i > 0) {
                imgui.ImGui.sameLine();
                imgui.ImGui.text("|");
                imgui.ImGui.sameLine();
            }
            imgui.ImGui.text(infoLines.get(i));
        }
        if (analysis == null) {
            return;
        }
        final float[] color = switch (analysis.rating()) {
            case cn.jstxjf_.rte.data.RailGeometryAnalysis.RATING_GREEN -> new float[]{0.35f, 0.9f, 0.4f};
            case cn.jstxjf_.rte.data.RailGeometryAnalysis.RATING_YELLOW -> new float[]{0.95f, 0.85f, 0.3f};
            default -> new float[]{0.95f, 0.35f, 0.3f};
        };
        final String ratingText = switch (analysis.rating()) {
            case cn.jstxjf_.rte.data.RailGeometryAnalysis.RATING_GREEN -> text("gui.rte.rail_rating.green");
            case cn.jstxjf_.rte.data.RailGeometryAnalysis.RATING_YELLOW -> text("gui.rte.rail_rating.yellow");
            default -> text("gui.rte.rail_rating.red");
        };
        imgui.ImGui.sameLine();
        imgui.ImGui.text("|");
        imgui.ImGui.sameLine();
        imgui.ImGui.textColored(color[0], color[1], color[2], 1f, "● " + ratingText);
        imgui.ImGui.sameLine();
        imgui.ImGui.text(String.format(java.util.Locale.ROOT, "R min %s m | %.1f‰ | %.0f km/h | %s %.0f%%",
            analysis.minRadiusMeters() == null ? "∞" : String.format(java.util.Locale.ROOT, "%.0f", analysis.minRadiusMeters()),
            analysis.maxGradePermille(), analysis.suggestedSpeedKph(),
            text("gui.rte.rail_path_editor.zoom"), zoom * 100));
    }

    protected void buildExtraPanelSection() {
    }

    protected static String text(String key) {
        return Component.translatable(key).getString();
    }

    @Override
    public boolean onCanvasDown(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean onCanvasUp(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean onCanvasDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (imgui.ImGui.getIO().getWantCaptureMouse()) {
            return true;
        }
        if (button == 1 && isInCanvas(mouseX, mouseY)) {
            double worldDx = dragX / pixelsPerBlock();
            double worldDy = dragY / pixelsPerBlock();
            if (showXZ) {
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
    public boolean onCanvasScroll(double mouseX, double mouseY, double amount) {
        if (imgui.ImGui.getIO().getWantCaptureMouse()) {
            return true;
        }
        if (isInCanvas(mouseX, mouseY)) {
            double factor = amount > 0 ? 1.15 : 1.0 / 1.15;
            zoom = Math.max(0.1, Math.min(10.0, zoom * factor));
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        final Screen previous = parent.get();
        minecraft.setScreen(previous == null ? null : previous.data);
    }

    protected int getMode() {
        return extra.getPathMode();
    }

    protected String getModeText() {
        return switch (getMode()) {
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> "Auto Bezier";
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> "Manual Bezier";
            case RailExtraSupplier.PATH_MODE_CURVE_FITTING -> "Curve Fitting";
            case RailExtraSupplier.PATH_MODE_ORIGINAL -> "Original";
            default -> "Unknown";
        };
    }

    protected String getInfoText() {
        return Component.translatable("gui.rte.rail_path_editor.info", String.format("%.1f", extra.getLength()), getModeText()).getString();
    }

    protected String getHeightText() {
        return Component.translatable(
                "gui.rte.rail_path_editor.height",
                extra.getHeight(),
                posStart.getX(), posStart.getY(), posStart.getZ(),
                posEnd.getX(), posEnd.getY(), posEnd.getZ()
        ).getString();
    }

    protected void changeMode() {
        final Rail resolvedRail = RailRebuildHelper.resolveClientRail(posStart, posEnd, rail);
        if (!((Object) resolvedRail instanceof RailExtraSupplier resolvedExtra)) return;
        final int nextMode = getNextMode(resolvedExtra.getPathMode());
        if (nextMode == RailExtraSupplier.PATH_MODE_CURVE_FITTING) {
            resolvedExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_CURVE_FITTING);
            resolvedExtra.setBezier(null);
            resolvedExtra.setExtraAngle1Rad(Double.NaN);
            resolvedExtra.setExtraAngle2Rad(Double.NaN);
        } else {
            resolvedExtra.changePathMode(nextMode);
        }
        updateRail(resolvedRail);
        final Screen next = createScreen(posStart, posEnd, resolvedRail, parent);
        minecraft.setScreen(next == null ? null : next.data);
    }

    protected void updateRail() {
        PacketUpdateRail.sendUpdateC2S(rail);
    }

    protected void updateRail(Rail updatedRail) {
        PacketUpdateRail.sendUpdateC2S(updatedRail);
    }

    protected Component modeTextOf(int mode) {
        return switch (mode) {
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> Component.translatable("tooltip.rte.rail.path_mode.bezier");
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> Component.translatable("tooltip.rte.rail.path_mode.manual_bezier");
            case RailExtraSupplier.PATH_MODE_CURVE_FITTING -> Component.translatable("tooltip.rte.rail.path_mode.fitting");
            case RailExtraSupplier.PATH_MODE_ORIGINAL -> Component.translatable("tooltip.rte.rail.path_mode.original");
            default -> Component.translatable("tooltip.rte.rail.path_mode.unknown");
        };
    }

    protected Component getModeTextComponent() {
        return switch (getMode()) {
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> Component.translatable("tooltip.rte.rail.path_mode.bezier");
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> Component.translatable("tooltip.rte.rail.path_mode.manual_bezier");
            case RailExtraSupplier.PATH_MODE_CURVE_FITTING -> Component.translatable("tooltip.rte.rail.path_mode.fitting");
            case RailExtraSupplier.PATH_MODE_ORIGINAL -> Component.translatable("tooltip.rte.rail.path_mode.original");
            default -> Component.translatable("tooltip.rte.rail.path_mode.unknown");
        };
    }

    protected int getNextMode(int mode) {
        return switch (mode) {
            case RailExtraSupplier.PATH_MODE_ORIGINAL -> RailExtraSupplier.PATH_MODE_AUTO_BEZIER;
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> RailExtraSupplier.PATH_MODE_MANUAL_BEZIER;
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> RailExtraSupplier.PATH_MODE_CURVE_FITTING;
            default -> RailExtraSupplier.PATH_MODE_ORIGINAL;
        };
    }

    private void orientCamera() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || posStart == null || posEnd == null) return;
        Basis basis = railBasis();
        float yaw = (float) -Math.toDegrees(Math.atan2(basis.ax, basis.az));
        float pitch = showXZ ? 90 : 0;
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
        double basePixels = Math.max(2, canvasWidth() / Math.max(24, rLen * 1.4));
        double canvasBlocks = canvasWidth() / basePixels;
        if (rLen > canvasBlocks * 0.8) {
            zoom = canvasBlocks * 0.8 / rLen;
        }
    }

    private void renderCanvasBackground(GuiGraphics graphics) {
        int l = canvasLeft(), t = canvasTop(), r = canvasRight(), b = canvasBottom();
        fillRect(l, t, r, b, 0x44000000);
        fillRect(l, t, r, t + 1, 0x66FFFFFF);
        fillRect(l, b - 1, r, b, 0x66FFFFFF);
        fillRect(l, t, l + 1, b, 0x66FFFFFF);
        fillRect(r - 1, t, r, b, 0x66FFFFFF);
    }

    private void renderGrid(GuiGraphics graphics) {
        final double ppb = pixelsPerBlock();
        if (ppb < 1.5) return;
        final int l = canvasLeft(), t = canvasTop(), r = canvasRight(), b = canvasBottom();
        final int cx = canvasCenterX(), cy = canvasCenterY();

        final double minorStep = ppb < 4 ? 10.0 : ppb < 10 ? 5.0 : ppb < 24 ? 2.0 : 1.0;
        final double majorStep = minorStep * 5;
        final double minorPixels = minorStep * ppb;
        final double majorPixels = majorStep * ppb;

        if (minorPixels >= 4) {
            drawGridLines(l, t, r, b, cx, cy, minorPixels, 0x14FFFFFF);
        }
        if (majorPixels >= 12) {
            drawGridLines(l, t, r, b, cx, cy, majorPixels, 0x30FFFFFF);
            drawGridRuler(l, t, r, b, cx, cy, majorPixels, majorStep);
        }

        fillRect(cx - 0.5, t, cx + 0.5, b, 0x55FF6666);
        fillRect(l, cy - 0.5, r, cy + 0.5, 0x5566AAFF);
        drawScaleBar(l, b, ppb);
    }

    private void drawGridLines(int l, int t, int r, int b, int cx, int cy, double stepPixels, int color) {
        for (double px = cx; px < r; px += stepPixels) fillRect(px, t, px + 0.6, b, color);
        for (double px = cx - stepPixels; px > l; px -= stepPixels) fillRect(px, t, px + 0.6, b, color);
        for (double py = cy; py < b; py += stepPixels) fillRect(l, py, r, py + 0.6, color);
        for (double py = cy - stepPixels; py > t; py -= stepPixels) fillRect(l, py, r, py + 0.6, color);
    }

    private void drawGridRuler(int l, int t, int r, int b, int cx, int cy, double stepPixels, double stepMeters) {
        final float scale = guiScale();
        final imgui.ImDrawList drawList = imgui.ImGui.getBackgroundDrawList();
        final int color = toImColor(0x99FFFFFF);
        int index = 0;
        for (double px = cx; px < r - 12; px += stepPixels, index++) {
            if (index > 0) {
                drawList.addText((float) (px + 2) * scale, (t + 2) * scale, color,
                    String.format(java.util.Locale.ROOT, "%.0f", index * stepMeters));
            }
        }
        index = 0;
        for (double px = cx - stepPixels; px > l + 12; px -= stepPixels) {
            index++;
            drawList.addText((float) (px + 2) * scale, (t + 2) * scale, color,
                String.format(java.util.Locale.ROOT, "-%.0f", index * stepMeters));
        }
    }

    private void drawScaleBar(int l, int b, double ppb) {
        final float scale = guiScale();
        final imgui.ImDrawList drawList = imgui.ImGui.getBackgroundDrawList();
        double meters = 10;
        while (meters * ppb < 40) meters *= 2;
        while (meters * ppb > 160) meters /= 2;
        final double barPixels = meters * ppb;
        final double y = b - 10;
        fillRect(l + 8, y, l + 8 + barPixels, y + 1.5, 0xCCFFFFFF);
        fillRect(l + 8, y - 3, l + 9.5, y + 4, 0xCCFFFFFF);
        fillRect(l + 8 + barPixels - 1.5, y - 3, l + 8 + barPixels, y + 4, 0xCCFFFFFF);
        drawList.addText((float) (l + 12) * scale, (float) (y - 13) * scale, toImColor(0xCCFFFFFF),
            String.format(java.util.Locale.ROOT, "%.0f m", meters));
    }

    private void renderRailPath(GuiGraphics graphics) {
        Position p1 = extra.getExtraPosition1();
        Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;

        double length = extra.getLength();
        if (length < 0.01) return;

        int steps = Math.max(20, (int) (length * 2));
        steps = Math.min(steps, 500);

        final java.util.List<double[]> polyline = new java.util.ArrayList<>(steps + 1);
        for (int i = 0; i <= steps; i++) {
            double d = length * i / steps;
            try {
                var pos = extra.getPosition(d);
                if (pos == null) continue;
                polyline.add(worldToScreenExact(pos.x, pos.y, pos.z));
            } catch (Exception ignored) {
                break;
            }
        }
        drawPolyline(polyline, 0xFFFF8800, 2f);

        int[] s1 = worldToScreen(p1.getX(), p1.getY(), p1.getZ());
        int[] s2 = worldToScreen(p2.getX(), p2.getY(), p2.getZ());
        drawCircle(graphics, s1[0], s1[1], 5, 0xFF00FF00);
        drawCircle(graphics, s2[0], s2[1], 5, 0xFFFF4444);
    }

    private void renderBezierPreview(GuiGraphics graphics) {
        BezierCurve bezier = extra.getBezierCurve();
        if (bezier == null) return;

        double len = bezier.getLength();
        if (!Double.isFinite(len) || len < 0.01) return;

        int samples = Math.min(300, Math.max(50, (int) (len * 3)));
        int prevX = -1, prevY = -1;
        for (int i = 0; i <= samples; i++) {
            double arcT = len * i / samples;
            BezierCurve.Vec3 pos = bezier.getPosition(arcT);
            int[] s = worldToScreen(pos.x, pos.y, pos.z);
            if (prevX >= 0) {
                drawLine(graphics, prevX, prevY, s[0], s[1], 0xFF00FF00);
            }
            prevX = s[0];
            prevY = s[1];
        }

        List<BezierCurve.Vec3> points = bezier.getPoints();
        if (points != null && points.size() >= 4) {
            for (int i = 0; i < points.size(); i++) {
                BezierCurve.Vec3 p = points.get(i);
                int[] s = worldToScreen(p.x, p.y, p.z);
                boolean isEndpt = i == 0 || i == points.size() - 1;
                boolean isAnchor = i % 3 == 0;
                int color = isEndpt ? 0xFF00FF00 : isAnchor ? 0xFFFFAA00 : 0x8844AAFF;
                int r = isEndpt || isAnchor ? 4 : 3;
                drawCircle(graphics, s[0], s[1], r, color);
            }

            int segCount = (points.size() - 1) / 3;
            for (int seg = 0; seg < segCount; seg++) {
                int base = seg * 3;
                if (base + 3 < points.size()) {
                    int[] sA = worldToScreen(points.get(base).x, points.get(base).y, points.get(base).z);
                    int[] sH = worldToScreen(points.get(base + 1).x, points.get(base + 1).y, points.get(base + 1).z);
                    drawLine(graphics, sA[0], sA[1], sH[0], sH[1], 0x44AAAAAA);

                    int[] sA2 = worldToScreen(points.get(base + 3).x, points.get(base + 3).y, points.get(base + 3).z);
                    int[] sH2 = worldToScreen(points.get(base + 2).x, points.get(base + 2).y, points.get(base + 2).z);
                    drawLine(graphics, sA2[0], sA2[1], sH2[0], sH2[1], 0x44AAAAAA);
                }
            }
        }
    }

    private int[] worldToScreen(double wx, double wy, double wz) {
        final double[] exact = worldToScreenExact(wx, wy, wz);
        return new int[]{(int) Math.round(exact[0]), (int) Math.round(exact[1])};
    }

    protected double[] worldToScreenExact(double wx, double wy, double wz) {
        double ppb = pixelsPerBlock();
        int cx = canvasCenterX();
        int cy = canvasCenterY();

        if (showXZ) {
            Position p1 = extra.getExtraPosition1();
            if (p1 == null) return new double[]{cx, cy};
            Basis basis = railBasis();
            double midX = p1.getX() + basis.ax * basis.len * 0.5 + panX;
            double midZ = p1.getZ() + basis.az * basis.len * 0.5 + panZ;
            double dx = wx - midX;
            double dz = wz - midZ;
            double screenX = dx * basis.nx + dz * basis.nz;
            double screenY = -(dx * basis.ax + dz * basis.az);
            return new double[]{cx + screenX * ppb, cy + screenY * ppb};
        } else {
            Position p1 = extra.getExtraPosition1();
            if (p1 == null) return new double[]{cx, cy};
            Basis basis = railBasis();
            double along = (wx - p1.getX()) * basis.ax + (wz - p1.getZ()) * basis.az;
            double screenX = (along - basis.len * 0.5 - panX * basis.ax - panZ * basis.az) * ppb;
            double midYVal = (extra.getExtraPosition1().getY() + extra.getExtraPosition2().getY()) / 2.0;
            double screenY = -(wy - midYVal - panY) * ppb;
            return new double[]{cx + screenX, cy + screenY};
        }
    }

    private Basis railBasis() {
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

    private double pixelsPerBlock() {
        double base = Math.max(2, canvasWidth() / Math.max(24, railBasis().len * 1.4));
        return base * zoom;
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

    protected static int toImColor(int argb) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    protected float guiScale() {
        return (float) net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScale();
    }

    protected void drawPolyline(java.util.List<double[]> points, int argb, float thickness) {
        if (points.size() < 2) {
            return;
        }
        final float s = guiScale();
        final imgui.ImDrawList drawList = imgui.ImGui.getBackgroundDrawList();
        final int color = toImColor(argb);
        for (int i = 0; i + 1 < points.size(); i++) {
            final double[] a = points.get(i);
            final double[] b = points.get(i + 1);
            drawList.addLine((float) (a[0] * s), (float) (a[1] * s),
                (float) (b[0] * s), (float) (b[1] * s), color, thickness * s * 0.5f);
        }
    }

    protected void fillRect(double x1, double y1, double x2, double y2, int argb) {
        final float s = guiScale();
        imgui.ImGui.getBackgroundDrawList().addRectFilled(
            (float) (x1 * s), (float) (y1 * s), (float) (x2 * s), (float) (y2 * s), toImColor(argb));
    }

    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        final float s = guiScale();
        imgui.ImGui.getBackgroundDrawList().addLine(
            x1 * s, y1 * s, x2 * s, y2 * s, toImColor(color), Math.max(1f, s * 0.8f));
    }

    private void drawCircle(GuiGraphics graphics, int cx, int cy, int r, int color) {
        final float s = guiScale();
        imgui.ImGui.getBackgroundDrawList().addCircleFilled(
            cx * s, cy * s, r * s, toImColor(color), 16);
    }

    private record Basis(double ax, double az, double nx, double nz, double len) {}

    public static class Original extends RailPathEditorScreen {
        public Original(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
            super(posStart, posEnd, rail, parent);
        }
    }

    public static class Bezier extends RailPathEditorScreen {
        public Bezier(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
            super(posStart, posEnd, rail, parent);
        }
    }
}
