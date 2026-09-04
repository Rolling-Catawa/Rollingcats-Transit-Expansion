package cn.jstxjf_.rte.client.gui;

import cn.jstxjf_.rte.item.CompoundCreatorLogic;
import cn.jstxjf_.rte.util.PinyinUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.mtr.mapping.holder.BlockState;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public class SliceTaskCanvasScreen extends Screen {

    private static final int CELL_SIZE = 18;
    private static final int INVENTORY_WIDTH = 104;
    private static final int INVENTORY_COLUMNS = 5;
    private static final int AXIS_STEP = 3;

    private final CompoundCreatorLogic.SliceTask originalTask;
    private final CompoundCreatorLogic.SliceTask workingTask;
    private final Consumer<CompoundCreatorLogic.SliceTask> onCommit;
    private final Runnable onBack;

    private final List<BlockState> allStates = new ArrayList<>();
    private final List<BlockState> filteredStates = new ArrayList<>();

    private final imgui.type.ImString nameBuffer = new imgui.type.ImString(256);
    private final imgui.type.ImString searchBuffer = new imgui.type.ImString(128);
    private final imgui.type.ImDouble startHolder = new imgui.type.ImDouble(0);
    private final imgui.type.ImDouble lengthHolder = new imgui.type.ImDouble(-1);
    private final imgui.type.ImDouble intervalHolder = new imgui.type.ImDouble(-1);
    private final imgui.type.ImDouble incrementHolder = new imgui.type.ImDouble(0.1);
    private StateTarget propertyTarget;

    private int panX = 0;
    private int panY = 0;
    private int inventoryScroll = 0;
    private boolean draggingInventorySlider = false;
    private boolean closed = false;

    private BlockState brushState = null;
    private boolean brushReplacement = true;

    private CellHit hoveredCanvasCell = null;
    private BlockState hoveredInventoryState = null;
    private int activePaintButton = -1;
    private int lastPaintIndex = -1;

    public SliceTaskCanvasScreen(CompoundCreatorLogic.SliceTask task, Consumer<CompoundCreatorLogic.SliceTask> onCommit, Runnable onBack) {
        super(Component.translatable("gui.rte.compound_creator.slice_task"));
        this.originalTask = task;
        this.workingTask = new CompoundCreatorLogic.SliceTask(task);
        this.onCommit = onCommit;
        this.onBack = onBack;
    }

    @Override
    protected void init() {
        if (allStates.isEmpty()) {
            for (final Block block : BuiltInRegistries.BLOCK) {
                allStates.add(new BlockState(block.defaultBlockState()));
            }
            filteredStates.addAll(allStates);
            nameBuffer.set(workingTask.name);
            startHolder.set(workingTask.start);
            lengthHolder.set(workingTask.length == null ? -1D : workingTask.length);
            intervalHolder.set(workingTask.interval == null ? -1D : workingTask.interval);
            incrementHolder.set(workingTask.increment);
        }

        clearWidgets();
        clampPan();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        hoveredCanvasCell = null;
        hoveredInventoryState = null;

        final Rect canvasFrame = getCanvasFrame();
        final Rect canvasArea = getCanvasArea();
        final Rect inventoryArea = getInventoryArea();
        final Rect brushPreview = getBrushPreviewRect();

        graphics.drawCenteredString(font, title, width / 2, 16, 0xFFFFFF);

        graphics.fill(canvasFrame.x - 1, canvasFrame.y - 1, canvasFrame.x + canvasFrame.width + 1, canvasFrame.y + canvasFrame.height + 1, 0xFF3A3A42);
        graphics.fill(canvasFrame.x, canvasFrame.y, canvasFrame.x + canvasFrame.width, canvasFrame.y + canvasFrame.height, 0xF017171B);
        graphics.fill(canvasArea.x - 1, canvasArea.y - 1, canvasArea.x + canvasArea.width + 1, canvasArea.y + canvasArea.height + 1, 0xFF3A3A42);
        graphics.fill(canvasArea.x, canvasArea.y, canvasArea.x + canvasArea.width, canvasArea.y + canvasArea.height, 0xFF202026);

        graphics.fill(brushPreview.x - 1, brushPreview.y - 1, brushPreview.x + brushPreview.width + 1, brushPreview.y + brushPreview.height + 1, 0xFF3A3A42);
        graphics.fill(brushPreview.x, brushPreview.y, brushPreview.x + brushPreview.width, brushPreview.y + brushPreview.height, 0xF017171B);
        renderCell(graphics, brushPreview.x, brushPreview.y, brushState, brushReplacement, true, mouseX, mouseY, partialTick);
        graphics.drawString(font, Component.literal("Brush"), brushPreview.x + 22, brushPreview.y + 5, 0xE0E0E0);
        graphics.drawString(font, Component.literal(getBrushStatus()), brushPreview.x + 22, brushPreview.y + 15, 0xB0B0B0);

        renderAxisRulers(graphics, canvasFrame, canvasArea);
        renderCanvas(graphics, canvasArea, mouseX, mouseY, partialTick);
        renderInventory(graphics, inventoryArea, mouseX, mouseY, partialTick);

        if (hoveredCanvasCell != null) {
            renderCanvasTooltip(graphics, mouseX, mouseY, hoveredCanvasCell);
        } else if (hoveredInventoryState != null) {
            renderStateTooltip(graphics, mouseX, mouseY, hoveredInventoryState, true);
        } else if (brushPreview.contains(mouseX, mouseY) && brushState != null) {
            renderStateTooltip(graphics, mouseX, mouseY, brushState, brushReplacement);
        }

        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.beginFrame();
        buildImGuiPanel();
        if (propertyTarget != null) {
            buildPropertyWindow();
        }
        cn.jstxjf_.rte.gui.imgui.RteImGuiHost.endFrame();
    }

    private void buildImGuiPanel() {
        imgui.ImGui.setNextWindowPos(10, imgui.ImGui.getIO().getDisplaySizeY() - 460, imgui.flag.ImGuiCond.FirstUseEver);
        imgui.ImGui.setNextWindowSize(320, 0, imgui.flag.ImGuiCond.FirstUseEver);
        imgui.ImGui.begin(Component.translatable("gui.rte.compound_creator.slice_task").getString() + "###rte_slice_panel", imgui.flag.ImGuiWindowFlags.NoCollapse);

        if (imgui.ImGui.inputText(Component.translatable("gui.rte.compound_creator.name").getString() + "###slice_name", nameBuffer)) {
            workingTask.name = nameBuffer.get();
        }
        if (imgui.ImGui.inputTextWithHint("###slice_search", Component.translatable("gui.rte.compound_creator.search_blocks", filteredStates.size(), allStates.size()).getString(), searchBuffer)) {
            filterInventory(searchBuffer.get());
        }

        imgui.ImGui.text(Component.translatable("gui.rte.compound_creator.size_filled", workingTask.width, workingTask.height, countFilledLumps(workingTask)).getString());
        if (imgui.ImGui.button("+W###slice_wp")) {
            resizeTask(workingTask.width + 2, workingTask.height);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("-W###slice_wm")) {
            resizeTask(workingTask.width - 2, workingTask.height);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("+H###slice_hp")) {
            resizeTask(workingTask.width, workingTask.height + 2);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("-H###slice_hm")) {
            resizeTask(workingTask.width, workingTask.height - 2);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button("0###slice_center")) {
            centerCanvas();
        }

        imgui.ImGui.textDisabled(getBrushStatus());
        imgui.ImGui.textDisabled(Component.translatable("gui.rte.compound_creator.hint_canvas").getString());

        if (imgui.ImGui.collapsingHeader(Component.translatable("gui.rte.compound_creator.config").getString() + "###slice_config")) {
            if (imgui.ImGui.inputDouble("Start###slice_start", startHolder)) {
                workingTask.start = startHolder.get();
            }
            if (imgui.ImGui.inputDouble("Length###slice_length", lengthHolder)) {
                workingTask.length = lengthHolder.get() <= 0 ? null : lengthHolder.get();
            }
            if (imgui.ImGui.inputDouble("Interval###slice_interval", intervalHolder)) {
                workingTask.interval = intervalHolder.get() <= 0 ? null : intervalHolder.get();
            }
            if (imgui.ImGui.inputDouble("Increment###slice_increment", incrementHolder)) {
                workingTask.increment = Math.max(0.01D, incrementHolder.get());
            }
            if (imgui.ImGui.checkbox("Yaw###slice_yaw", workingTask.useYaw)) {
                workingTask.useYaw = !workingTask.useYaw;
            }
            imgui.ImGui.sameLine();
            if (imgui.ImGui.checkbox("Pitch###slice_pitch", workingTask.usePitch)) {
                workingTask.usePitch = !workingTask.usePitch;
            }
            imgui.ImGui.sameLine();
            if (imgui.ImGui.checkbox("Roll###slice_roll", workingTask.useRoll)) {
                workingTask.useRoll = !workingTask.useRoll;
            }
        }

        rte$buildSegmentPanel();
        rte$buildRangePanel();
        rte$buildPreviewPanel();
        rte$buildCapturePanel();

        imgui.ImGui.separator();
        if (imgui.ImGui.button(Component.translatable("gui.rte.compound_creator.save").getString() + "###slice_save")) {
            closeAndCommit();
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(Component.translatable("gui.rte.compound_creator.back").getString() + "###slice_back")) {
            closeAndCommit();
        }
        imgui.ImGui.end();
    }

    private int rte$captureOffset = 0;
    private boolean rte$captureFlip = false;

    private void rte$captureFromWorld() {
        final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        final net.minecraft.core.Direction facing = mc.player.getDirection();
        final net.minecraft.core.Direction lateral = rte$captureFlip
            ? facing.getClockWise() : facing.getCounterClockWise();
        final net.minecraft.core.BlockPos centre =
            mc.player.blockPosition().relative(facing, rte$captureOffset);

        final java.util.List<CompoundCreatorLogic.Lump> target = rte$activeLumps();
        int captured = 0;
        for (int y = 0; y < workingTask.height; y++) {
            for (int x = 0; x < workingTask.width; x++) {
                final int index = y * workingTask.width + x;
                if (index >= target.size()) {
                    continue;
                }
                final net.minecraft.core.BlockPos sample = centre
                    .relative(lateral, workingTask.width / 2 - x)
                    .above(workingTask.height / 2 - y);
                final net.minecraft.world.level.block.state.BlockState sampled = mc.level.getBlockState(sample);
                if (sampled.isAir()) {
                    target.get(index).blockState = null;
                } else {
                    target.get(index).blockState = new BlockState(sampled);
                    captured++;
                }
            }
        }
        cn.jstxjf_.rte.RTE.LOGGER.info("[CompoundCreator] captured {} block(s) from world at {} facing {}",
            captured, centre, facing);
    }

    private void rte$buildCapturePanel() {
        imgui.ImGui.separator();
        if (!imgui.ImGui.collapsingHeader(t("gui.rte.compound_creator.capture") + "###slice_capture")) {
            return;
        }
        imgui.ImGui.textDisabled("  " + t("gui.rte.compound_creator.capture.hint"));

        final imgui.type.ImInt offsetHolder = new imgui.type.ImInt(rte$captureOffset);
        if (imgui.ImGui.inputInt(t("gui.rte.compound_creator.capture.offset") + "###cap_offset", offsetHolder)) {
            rte$captureOffset = Math.max(-64, Math.min(64, offsetHolder.get()));
        }
        if (imgui.ImGui.checkbox(t("gui.rte.compound_creator.capture.flip") + "###cap_flip", rte$captureFlip)) {
            rte$captureFlip = !rte$captureFlip;
        }
        if (imgui.ImGui.button(t("gui.rte.compound_creator.capture.run") + "###cap_run")) {
            rte$captureFromWorld();
        }
    }

    private final SlicePreview3D rte$preview3d = new SlicePreview3D();
    private final imgui.type.ImInt rte$selX1 = new imgui.type.ImInt(0);
    private final imgui.type.ImInt rte$selY1 = new imgui.type.ImInt(0);
    private final imgui.type.ImInt rte$selX2 = new imgui.type.ImInt(0);
    private final imgui.type.ImInt rte$selY2 = new imgui.type.ImInt(0);

    private void rte$buildPreviewPanel() {
        imgui.ImGui.separator();
        if (!imgui.ImGui.collapsingHeader(t("gui.rte.compound_creator.preview3d") + "###slice_preview3d_hdr")) {
            return;
        }
        rte$preview3d.draw(workingTask, rte$activeLumps(), workingTask.name);

        imgui.ImGui.separator();
        final boolean ghost = cn.jstxjf_.rte.render.SliceGhostRenderer.isActive();
        if (imgui.ImGui.checkbox(t("gui.rte.compound_creator.ghost") + "###slice_ghost", ghost)) {
            if (ghost) {
                cn.jstxjf_.rte.render.SliceGhostRenderer.hide();
            } else {
                cn.jstxjf_.rte.render.SliceGhostRenderer.show(
                    workingTask, rte$preview3d.depthBlocks(), rte$captureFlip);
            }
        }
        imgui.ImGui.textDisabled("  " + t("gui.rte.compound_creator.ghost.hint"));
        if (ghost && imgui.ImGui.button(t("gui.rte.compound_creator.ghost.close") + "###slice_ghost_close")) {
            cn.jstxjf_.rte.render.SliceGhostRenderer.hide();
            onClose();
        }
    }

    private void rte$forEachSelected(java.util.function.IntConsumer action) {
        final int w = Math.max(1, workingTask.width);
        final int h = Math.max(1, workingTask.height);
        final int x1 = Math.max(0, Math.min(w - 1, Math.min(rte$selX1.get(), rte$selX2.get())));
        final int x2 = Math.max(0, Math.min(w - 1, Math.max(rte$selX1.get(), rte$selX2.get())));
        final int y1 = Math.max(0, Math.min(h - 1, Math.min(rte$selY1.get(), rte$selY2.get())));
        final int y2 = Math.max(0, Math.min(h - 1, Math.max(rte$selY1.get(), rte$selY2.get())));
        final java.util.List<CompoundCreatorLogic.Lump> lumps = rte$activeLumps();
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                final int index = y * w + x;
                if (index < lumps.size()) {
                    action.accept(index);
                }
            }
        }
    }

    private void rte$buildRangePanel() {
        imgui.ImGui.separator();
        if (!imgui.ImGui.collapsingHeader(t("gui.rte.compound_creator.range") + "###slice_range")) {
            return;
        }
        imgui.ImGui.textDisabled("  " + t("gui.rte.compound_creator.range.hint"));

        imgui.ImGui.setNextItemWidth(70);
        imgui.ImGui.inputInt("x1###rng_x1", rte$selX1, 0);
        imgui.ImGui.sameLine();
        imgui.ImGui.setNextItemWidth(70);
        imgui.ImGui.inputInt("y1###rng_y1", rte$selY1, 0);
        imgui.ImGui.sameLine();
        imgui.ImGui.setNextItemWidth(70);
        imgui.ImGui.inputInt("x2###rng_x2", rte$selX2, 0);
        imgui.ImGui.sameLine();
        imgui.ImGui.setNextItemWidth(70);
        imgui.ImGui.inputInt("y2###rng_y2", rte$selY2, 0);

        if (imgui.ImGui.button(t("gui.rte.compound_creator.range.all") + "###rng_all")) {
            rte$selX1.set(0);
            rte$selY1.set(0);
            rte$selX2.set(Math.max(0, workingTask.width - 1));
            rte$selY2.set(Math.max(0, workingTask.height - 1));
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(t("gui.rte.compound_creator.range.fill") + "###rng_fill")) {
            final BlockState state = brushState;
            final java.util.List<CompoundCreatorLogic.Lump> lumps = rte$activeLumps();
            rte$forEachSelected(index -> lumps.get(index).blockState = state);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(t("gui.rte.compound_creator.range.clear") + "###rng_clear")) {
            final java.util.List<CompoundCreatorLogic.Lump> lumps = rte$activeLumps();
            rte$forEachSelected(index -> lumps.get(index).blockState = null);
        }

        if (imgui.ImGui.button(t("gui.rte.compound_creator.range.replacement_on") + "###rng_rep_on")) {
            final java.util.List<CompoundCreatorLogic.Lump> lumps = rte$activeLumps();
            rte$forEachSelected(index -> lumps.get(index).replacement = true);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(t("gui.rte.compound_creator.range.replacement_off") + "###rng_rep_off")) {
            final java.util.List<CompoundCreatorLogic.Lump> lumps = rte$activeLumps();
            rte$forEachSelected(index -> lumps.get(index).replacement = false);
        }
        imgui.ImGui.sameLine();
        if (imgui.ImGui.button(t("gui.rte.compound_creator.range.mirror") + "###rng_mirror")) {
            rte$mirrorSelection();
        }
    }

    private void rte$mirrorSelection() {
        final int w = Math.max(1, workingTask.width);
        final java.util.List<CompoundCreatorLogic.Lump> lumps = rte$activeLumps();
        final java.util.List<Integer> indices = new java.util.ArrayList<>();
        rte$forEachSelected(indices::add);
        for (final int index : indices) {
            final int y = index / w;
            final int x = index % w;
            final int mirrored = y * w + (w - 1 - x);
            if (mirrored < lumps.size() && mirrored > index) {
                final CompoundCreatorLogic.Lump a = lumps.get(index);
                final CompoundCreatorLogic.Lump b = lumps.get(mirrored);
                final BlockState state = a.blockState;
                final boolean replacement = a.replacement;
                a.blockState = b.blockState;
                a.replacement = b.replacement;
                b.blockState = state;
                b.replacement = replacement;
            }
        }
    }

    private int rte$editingSegment = -1;

    private java.util.List<CompoundCreatorLogic.Lump> rte$activeLumps() {
        if (rte$editingSegment >= 0 && workingTask.segments != null
                && rte$editingSegment < workingTask.segments.size()) {
            return workingTask.segments.get(rte$editingSegment).lumps;
        }
        return workingTask.lumps;
    }

    private void rte$buildSegmentPanel() {
        imgui.ImGui.separator();
        if (!imgui.ImGui.collapsingHeader(t("gui.rte.compound_creator.segments") + "###slice_segments")) {
            return;
        }
        imgui.ImGui.textDisabled("  " + t("gui.rte.compound_creator.segments.hint"));

        if (workingTask.segments == null) {
            workingTask.segments = new java.util.ArrayList<>();
        }

        final boolean baseActive = rte$editingSegment < 0;
        if (imgui.ImGui.button((baseActive ? "* " : "") + t("gui.rte.compound_creator.segments.base") + "###seg_base")) {
            rte$editingSegment = -1;
        }

        for (int i = 0; i < workingTask.segments.size(); i++) {
            final CompoundCreatorLogic.Segment segment = workingTask.segments.get(i);
            final boolean active = rte$editingSegment == i;
            if (imgui.ImGui.button((active ? "* " : "") + t("gui.rte.compound_creator.segments.item", i + 1) + "###seg_" + i)) {
                rte$editingSegment = i;
            }
            imgui.ImGui.sameLine();
            final imgui.type.ImDouble lengthHolder = new imgui.type.ImDouble(segment.length);
            imgui.ImGui.pushItemWidth(90);
            if (imgui.ImGui.inputDouble(t("gui.rte.compound_creator.segments.length") + "###seg_len_" + i, lengthHolder)) {
                segment.length = Math.max(0.1, lengthHolder.get());
            }
            imgui.ImGui.popItemWidth();
            imgui.ImGui.sameLine();
            if (imgui.ImGui.button(t("gui.rte.compound_creator.segments.remove") + "###seg_del_" + i)) {
                workingTask.segments.remove(i);
                if (rte$editingSegment >= workingTask.segments.size()) {
                    rte$editingSegment = workingTask.segments.size() - 1;
                }
                break;
            }
        }

        if (imgui.ImGui.button(t("gui.rte.compound_creator.segments.add") + "###seg_add")) {
            final java.util.List<CompoundCreatorLogic.Lump> copied =
                CompoundCreatorLogic.Lump.copyFrom(rte$activeLumps());
            workingTask.segments.add(new CompoundCreatorLogic.Segment(copied, 1.0));
            rte$editingSegment = workingTask.segments.size() - 1;
        }
        if (!workingTask.segments.isEmpty()) {
            imgui.ImGui.sameLine();
            imgui.ImGui.textDisabled(t("gui.rte.compound_creator.segments.cycle",
                String.format(java.util.Locale.ROOT, "%.1f", workingTask.segmentCycleLength())));
        }

        imgui.ImGui.separator();
        imgui.ImGui.text(t("gui.rte.compound_creator.pier"));
        imgui.ImGui.textDisabled("  " + t("gui.rte.compound_creator.pier.hint"));
        final imgui.type.ImDouble pierIntervalHolder = new imgui.type.ImDouble(workingTask.pierInterval);
        if (imgui.ImGui.inputDouble(t("gui.rte.compound_creator.pier.interval") + "###pier_interval", pierIntervalHolder)) {
            workingTask.pierInterval = Math.max(0, pierIntervalHolder.get());
        }
        final imgui.type.ImInt pierDepthHolder = new imgui.type.ImInt(workingTask.pierDepth);
        if (imgui.ImGui.inputInt(t("gui.rte.compound_creator.pier.depth") + "###pier_depth", pierDepthHolder)) {
            workingTask.pierDepth = Math.max(0, Math.min(256, pierDepthHolder.get()));
        }
    }

    private static String t(String key, Object... args) {
        return Component.translatable(key, args).getString();
    }

    private void buildPropertyWindow() {
        final BlockState state = propertyTarget.getState();
        if (state == null) {
            propertyTarget = null;
            return;
        }
        imgui.ImGui.setNextWindowPos(imgui.ImGui.getIO().getDisplaySizeX() / 2f - 180, imgui.ImGui.getIO().getDisplaySizeY() / 2f - 160, imgui.flag.ImGuiCond.FirstUseEver);
        imgui.ImGui.setNextWindowSize(360, 0, imgui.flag.ImGuiCond.FirstUseEver);
        imgui.ImGui.begin(state.getBlock().getName().getString() + "###rte_slice_property", imgui.flag.ImGuiWindowFlags.NoCollapse);

        if (imgui.ImGui.checkbox("Replacement###prop_replacement", propertyTarget.getReplacement())) {
            propertyTarget.setReplacement(!propertyTarget.getReplacement());
        }

        final Collection<Property<?>> properties = state.getBlock().data.getStateDefinition().getProperties();
        int propertyIndex = 0;
        for (final Property<?> property : properties) {
            drawPropertyCombo(property, propertyIndex++);
        }

        imgui.ImGui.separator();
        if (imgui.ImGui.button(Component.translatable("gui.rte.imgui.back").getString() + "###prop_close")) {
            propertyTarget = null;
        }
        imgui.ImGui.end();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void drawPropertyCombo(Property property, int propertyIndex) {
        final org.mtr.mapping.holder.Property wrappedProperty = new org.mtr.mapping.holder.Property(property);
        final BlockState currentState = propertyTarget.getState();
        final List<String> values = new ArrayList<>();
        for (final Object value : wrappedProperty.getValues()) {
            values.add(wrappedProperty.name((Comparable) value));
        }
        final String current = wrappedProperty.name((Comparable) currentState.get(wrappedProperty));
        final imgui.type.ImInt indexHolder = new imgui.type.ImInt(Math.max(0, values.indexOf(current)));
        if (imgui.ImGui.combo(wrappedProperty.getName() + "###prop_" + propertyIndex, indexHolder, values.toArray(new String[0]))) {
            final Optional parsed = wrappedProperty.parse(values.get(indexHolder.get()));
            parsed.ifPresent(value -> propertyTarget.setState(currentState.with(wrappedProperty, (Comparable) value)));
        }
    }

    private void openPropertyScreen(StateTarget target) {
        if (target.getState() == null) {
            return;
        }
        propertyTarget = target;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (imGuiCapturesMouse()) {
            return true;
        }
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        final Rect brushPreview = getBrushPreviewRect();
        if (brushPreview.contains(mouseX, mouseY) && brushState != null && button == 1) {
            openPropertyScreen(new StateTarget() {
                @Override
                public BlockState getState() {
                    return brushState;
                }

                @Override
                public void setState(BlockState state) {
                    brushState = state;
                }

                @Override
                public boolean getReplacement() {
                    return brushReplacement;
                }

                @Override
                public void setReplacement(boolean replacement) {
                    brushReplacement = replacement;
                }
            });
            return true;
        }

        final InventoryHit inventoryHit = getInventoryHit(mouseX, mouseY);
        if (inventoryHit != null) {
            if (button == 0 || button == 2) {
                brushState = inventoryHit.state;
                brushReplacement = true;
                return true;
            }
            if (button == 1) {
                final BlockState sampledState = inventoryHit.state;
                openPropertyScreen(new StateTarget() {
                    private BlockState state = sampledState;
                    private boolean replacement = brushReplacement;

                    @Override
                    public BlockState getState() {
                        return state;
                    }

                    @Override
                    public void setState(BlockState state) {
                        this.state = state;
                        brushState = state;
                    }

                    @Override
                    public boolean getReplacement() {
                        return replacement;
                    }

                    @Override
                    public void setReplacement(boolean replacement) {
                        this.replacement = replacement;
                        brushReplacement = replacement;
                    }
                });
                return true;
            }
        }

        final int sliderTop = getInventorySliderTop();
        final int sliderHeight = getInventorySliderHeight();
        if (canScrollInventory() && mouseX >= width - 5 && mouseX <= width && mouseY >= sliderTop && mouseY <= sliderTop + sliderHeight) {
            draggingInventorySlider = true;
            setInventoryScrollFromMouse((int) mouseY);
            return true;
        }

        final CellHit cellHit = getCanvasCell(mouseX, mouseY);
        if (cellHit != null) {
            activePaintButton = button;
            if (button == 0) {
                paintCell(cellHit.index);
            } else if (button == 1 && cellHit.lump.blockState != null) {
                openPropertyScreen(new CellTarget(cellHit.index));
            } else if (button == 2) {
                brushState = cellHit.lump.blockState;
                brushReplacement = cellHit.lump.replacement;
            }
            return true;
        }

        return false;
    }

    private boolean imGuiCapturesMouse() {
        return imgui.ImGui.getIO().getWantCaptureMouse();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (imGuiCapturesMouse() && activePaintButton < 0 && !draggingInventorySlider) {
            return true;
        }
        if (draggingInventorySlider) {
            setInventoryScrollFromMouse((int) mouseY);
            return true;
        }

        if (button == 0 && activePaintButton == 0) {
            final CellHit cellHit = getCanvasCell(mouseX, mouseY);
            if (cellHit != null) {
                paintCell(cellHit.index);
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingInventorySlider = false;
        activePaintButton = -1;
        lastPaintIndex = -1;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    //#if MC>=12002
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double amount) {
    //#else
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    //#endif
        if (imGuiCapturesMouse()) {
            return true;
        }
        if (getCanvasFrame().contains(mouseX, mouseY)) {
            if (hasShiftDown()) {
                nudgePan((int) Math.signum(amount) * CELL_SIZE * 2, 0);
            } else {
                nudgePan(0, (int) Math.signum(amount) * CELL_SIZE * 2);
            }
            return true;
        }
        if (getInventoryArea().contains(mouseX, mouseY) && canScrollInventory()) {
            final int next = inventoryScroll + (int) Math.signum(amount) * CELL_SIZE * 2;
            inventoryScroll = clampInventoryScroll(next);
            return true;
        }
        //#if MC>=12002
        return super.mouseScrolled(mouseX, mouseY, scrollX, amount);
        //#else
        return super.mouseScrolled(mouseX, mouseY, amount);
        //#endif
    }

    @Override
    public void onClose() {
        closeAndCommit();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (imgui.ImGui.getIO().getWantCaptureKeyboard() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT -> {
                nudgePan(CELL_SIZE * 2, 0);
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                nudgePan(-CELL_SIZE * 2, 0);
                return true;
            }
            case GLFW.GLFW_KEY_UP -> {
                nudgePan(0, CELL_SIZE * 2);
                return true;
            }
            case GLFW.GLFW_KEY_DOWN -> {
                nudgePan(0, -CELL_SIZE * 2);
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                centerCanvas();
                return true;
            }
            case GLFW.GLFW_KEY_DELETE, GLFW.GLFW_KEY_BACKSPACE -> {
                clearHoveredCell();
                return true;
            }
            case GLFW.GLFW_KEY_R -> {
                brushReplacement = !brushReplacement;
                return true;
            }
            default -> {
                if (keyCode == GLFW.GLFW_KEY_S && hasControlDown()) {
                    closeAndCommit();
                    return true;
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    private void renderCanvas(GuiGraphics graphics, Rect canvasArea, int mouseX, int mouseY, float partialTick) {
        final int centerX = canvasArea.x + canvasArea.width / 2 + panX;
        final int centerY = canvasArea.y + canvasArea.height / 2 + panY;
        final int startX = centerX - workingTask.width * CELL_SIZE / 2;
        final int startY = centerY - workingTask.height * CELL_SIZE / 2;

        graphics.enableScissor(canvasArea.x, canvasArea.y, canvasArea.x + canvasArea.width, canvasArea.y + canvasArea.height);

        for (int index = 0; index < rte$activeLumps().size(); index++) {
            final int x = startX + (index % workingTask.width) * CELL_SIZE;
            final int y = startY + (index / workingTask.width) * CELL_SIZE;
            final CompoundCreatorLogic.Lump lump = rte$activeLumps().get(index);
            renderCell(graphics, x, y, lump.blockState, lump.replacement, false, mouseX, mouseY, partialTick);
            if (x <= mouseX && mouseX <= x + CELL_SIZE && y <= mouseY && mouseY <= y + CELL_SIZE) {
                hoveredCanvasCell = new CellHit(index, lump);
            }
        }

        final int guideY1 = centerY - CELL_SIZE / 2;
        final int guideY2 = guideY1 + CELL_SIZE;
        final int guideX1 = centerX - CELL_SIZE / 2;
        final int guideX2 = guideX1 + CELL_SIZE;
        graphics.fill(startX, guideY1, startX + workingTask.width * CELL_SIZE, guideY1 + 2, 0x50E06C75);
        graphics.fill(startX, guideY2, startX + workingTask.width * CELL_SIZE, guideY2 + 2, 0x50E06C75);
        graphics.fill(guideX1, startY, guideX1 + 2, startY + workingTask.height * CELL_SIZE, 0x5098C379);
        graphics.fill(guideX2, startY, guideX2 + 2, startY + workingTask.height * CELL_SIZE, 0x5098C379);

        graphics.disableScissor();
    }

    private void renderInventory(GuiGraphics graphics, Rect inventoryArea, int mouseX, int mouseY, float partialTick) {
        graphics.fill(inventoryArea.x - 1, 0, width, height, 0xF0141418);
        graphics.fill(inventoryArea.x - 1, 0, inventoryArea.x, height, mouseX >= inventoryArea.x - 1 ? 0xFF4296FA : 0xFF3A3A42);

        graphics.enableScissor(inventoryArea.x, inventoryArea.y, inventoryArea.x + inventoryArea.width, inventoryArea.y + inventoryArea.height);
        final int startX = inventoryArea.x + 3;
        for (int i = 0; i < filteredStates.size(); i++) {
            final int x = startX + (i % INVENTORY_COLUMNS) * CELL_SIZE;
            final int y = inventoryArea.y + inventoryScroll + (i / INVENTORY_COLUMNS) * CELL_SIZE;
            if (y + CELL_SIZE < inventoryArea.y || y > inventoryArea.y + inventoryArea.height) {
                continue;
            }
            renderCell(graphics, x, y, filteredStates.get(i), true, isSameState(filteredStates.get(i), brushState) && brushReplacement, mouseX, mouseY, partialTick);
            if (x <= mouseX && mouseX <= x + CELL_SIZE && y <= mouseY && mouseY <= y + CELL_SIZE) {
                hoveredInventoryState = filteredStates.get(i);
            }
        }
        graphics.disableScissor();

        if (canScrollInventory()) {
            final int sliderTop = getInventorySliderTop();
            final int sliderHeight = getInventorySliderHeight();
            graphics.fill(width - 5, sliderTop, width, sliderTop + sliderHeight, 0xFF5A5A64);
        }
    }

    private void renderCell(GuiGraphics graphics, int x, int y, BlockState state, boolean replacement, boolean fixed, int mouseX, int mouseY, float partialTick) {
        final boolean hovered = x <= mouseX && mouseX <= x + CELL_SIZE && y <= mouseY && mouseY <= y + CELL_SIZE;
        graphics.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, hovered ? 0xFF4296FA : 0xFF33333A);
        graphics.fill(x + 1, y + 1, x + CELL_SIZE - 1, y + CELL_SIZE - 1, 0xFF26262B);
        if (state != null) {
            renderBlockState(graphics, x + 1, y + 1, partialTick, state);
            graphics.fill(x + 1, y + 1, x + 5, y + 5, replacement ? 0xE0B180F0 : 0xE050A8F0);
            if (fixed) {
                graphics.fill(x + 7, y + 7, x + 11, y + 11, 0xE0F0D060);
            }
        }
    }

    private void renderBlockState(GuiGraphics graphics, int x, int y, float partialTick, BlockState state) {
        final BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        graphics.pose().pushPose();
        graphics.pose().translate(x, y + 16, 200);
        graphics.pose().scale(15.5F, -15.5F, 15.5F);
        graphics.pose().mulPose(Axis.XP.rotationDegrees(3F));

        final MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        blockRenderer.renderSingleBlock(state.data, graphics.pose(), buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        buffer.endBatch();
        graphics.pose().popPose();
    }

    private void renderStateTooltip(GuiGraphics graphics, int mouseX, int mouseY, BlockState state, boolean replacement) {
        final List<Component> lines = new ArrayList<>();
        if (state == null) {
            lines.add(Component.literal("Empty"));
        } else {
            lines.add(Component.literal("Replacement: " + replacement));
            lines.add(state.getBlock().getName().data);
            lines.add(Component.literal(state.getBlock().getTranslationKey()));
            final Collection<Property<?>> properties = state.getBlock().data.getStateDefinition().getProperties();
            for (final Property<?> property : properties) {
                lines.add(Component.literal(property.getName() + ": " + state.data.getValue(property)));
            }
        }
        graphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
    }

    private void renderCanvasTooltip(GuiGraphics graphics, int mouseX, int mouseY, CellHit cellHit) {
        final List<Component> lines = new ArrayList<>();
        final int x = cellHit.index % workingTask.width - workingTask.width / 2;
        final int y = cellHit.index / workingTask.width - workingTask.height / 2;
        lines.add(Component.literal(String.format(Locale.ROOT, "Canvas X %d | Y %d", x, y)));
        if (cellHit.lump.blockState == null) {
            lines.add(Component.literal("Empty"));
        } else {
            lines.add(Component.literal("Replacement: " + cellHit.lump.replacement));
            lines.add(cellHit.lump.blockState.getBlock().getName().data);
            lines.add(Component.literal(cellHit.lump.blockState.getBlock().getTranslationKey()));
            final Collection<Property<?>> properties = cellHit.lump.blockState.getBlock().data.getStateDefinition().getProperties();
            for (final Property<?> property : properties) {
                lines.add(Component.literal(property.getName() + ": " + cellHit.lump.blockState.data.getValue(property)));
            }
        }
        graphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
    }

    private void resizeTask(int width, int height) {
        if (workingTask.setWidthAndHeight(width, height)) {
            clampPan();
        }
    }

    private void centerCanvas() {
        panX = 0;
        panY = 0;
    }

    private void nudgePan(int deltaX, int deltaY) {
        panX += deltaX;
        panY += deltaY;
        clampPan();
    }

    private void paintCell(int index) {
        if (brushState == null || index == lastPaintIndex || index < 0 || index >= rte$activeLumps().size()) {
            return;
        }
        final CompoundCreatorLogic.Lump lump = rte$activeLumps().get(index);
        lump.blockState = brushState;
        lump.replacement = brushReplacement;
        lastPaintIndex = index;
    }

    private void clearHoveredCell() {
        if (hoveredCanvasCell == null) {
            return;
        }
        final CompoundCreatorLogic.Lump lump = rte$activeLumps().get(hoveredCanvasCell.index);
        lump.blockState = null;
        lump.replacement = true;
    }




    private void filterInventory(String search) {
        filteredStates.clear();
        if (search == null || search.isBlank()) {
            filteredStates.addAll(allStates);
            inventoryScroll = 0;
            return;
        }

        final String needle = search.toLowerCase(Locale.ROOT);
        for (final BlockState state : allStates) {
            final String blockName = state.getBlock().getName().getString().toLowerCase(Locale.ROOT);
            final String descriptionId = state.getBlock().getTranslationKey().toLowerCase(Locale.ROOT);
            if (blockName.contains(needle) || descriptionId.contains(needle)) {
                filteredStates.add(state);
                continue;
            }
            final String pinyin = PinyinUtils.getPinyin(blockName).toLowerCase(Locale.ROOT);
            final String initials = PinyinUtils.getPinyinInitials(blockName).toLowerCase(Locale.ROOT);
            if (pinyin.contains(needle) || initials.contains(needle)) {
                filteredStates.add(state);
            }
        }
        inventoryScroll = 0;
    }


    private String getBrushStatus() {
        if (brushState == null) {
            return "Empty";
        }
        return String.format(Locale.ROOT, "%s | %s", brushReplacement ? "Replacement" : "Keep", brushState.getBlock().getName().getString());
    }

    private static boolean isSameState(BlockState first, BlockState second) {
        return first != null && second != null && first.data.equals(second.data);
    }

    private InventoryHit getInventoryHit(double mouseX, double mouseY) {
        final Rect inventoryArea = getInventoryArea();
        if (!inventoryArea.contains(mouseX, mouseY)) {
            return null;
        }

        final int startX = inventoryArea.x + 3;
        for (int i = 0; i < filteredStates.size(); i++) {
            final int x = startX + (i % INVENTORY_COLUMNS) * CELL_SIZE;
            final int y = inventoryArea.y + inventoryScroll + (i / INVENTORY_COLUMNS) * CELL_SIZE;
            if (x <= mouseX && mouseX <= x + CELL_SIZE && y <= mouseY && mouseY <= y + CELL_SIZE) {
                return new InventoryHit(i, filteredStates.get(i));
            }
        }
        return null;
    }

    private CellHit getCanvasCell(double mouseX, double mouseY) {
        final Rect canvasArea = getCanvasArea();
        if (!canvasArea.contains(mouseX, mouseY)) {
            return null;
        }

        final int centerX = canvasArea.x + canvasArea.width / 2 + panX;
        final int centerY = canvasArea.y + canvasArea.height / 2 + panY;
        final int startX = centerX - workingTask.width * CELL_SIZE / 2;
        final int startY = centerY - workingTask.height * CELL_SIZE / 2;

        final int localX = (int) Math.floor((mouseX - startX) / CELL_SIZE);
        final int localY = (int) Math.floor((mouseY - startY) / CELL_SIZE);
        if (localX < 0 || localY < 0 || localX >= workingTask.width || localY >= workingTask.height) {
            return null;
        }
        final int index = localY * workingTask.width + localX;
        return new CellHit(index, rte$activeLumps().get(index));
    }

    private Rect getCanvasFrame() {
        return new Rect(40, 40, width - 40 - 12 - INVENTORY_WIDTH, height - 90);
    }

    private Rect getCanvasArea() {
        final Rect frame = getCanvasFrame();
        return new Rect(frame.x + 20, frame.y + 10, frame.width - 24, frame.height - 20);
    }

    private Rect getInventoryArea() {
        return new Rect(width - INVENTORY_WIDTH, 26, INVENTORY_WIDTH, height - 60);
    }

    private Rect getBrushPreviewRect() {
        return new Rect(228, 8, CELL_SIZE, CELL_SIZE);
    }

    private void setInventoryScrollFromMouse(int mouseY) {
        if (!canScrollInventory()) {
            inventoryScroll = 0;
            return;
        }
        final Rect area = getInventoryArea();
        final int sliderHeight = getInventorySliderHeight();
        final int maxDelta = area.height - sliderHeight;
        final int relativeY = mouseY - area.y - sliderHeight / 2;
        final int maxScroll = getMinInventoryScroll();
        final int next = maxDelta <= 0 ? 0 : (int) Math.round((double) relativeY / maxDelta * maxScroll);
        inventoryScroll = clampInventoryScroll(next);
    }

    private boolean canScrollInventory() {
        return getInventoryContentHeight() > getInventoryArea().height;
    }

    private int getInventoryContentHeight() {
        final int rows = (int) Math.ceil(filteredStates.size() / (double) INVENTORY_COLUMNS);
        return rows * CELL_SIZE;
    }

    private int getMinInventoryScroll() {
        return Math.min(0, getInventoryArea().height - getInventoryContentHeight());
    }

    private int clampInventoryScroll(int next) {
        if (!canScrollInventory()) {
            return 0;
        }
        return Math.max(getMinInventoryScroll(), Math.min(0, next));
    }

    private int getInventorySliderHeight() {
        final Rect area = getInventoryArea();
        final float total = getInventoryContentHeight();
        return Math.max(5, (int) (area.height * (area.height / total)));
    }

    private int getInventorySliderTop() {
        final Rect area = getInventoryArea();
        final int sliderHeight = getInventorySliderHeight();
        final int maxDelta = area.height - sliderHeight;
        if (maxDelta <= 0 || getMinInventoryScroll() == 0) {
            return area.y;
        }
        return area.y + (int) Math.round((-inventoryScroll / (double) -getMinInventoryScroll()) * maxDelta);
    }

    private void clampPan() {
        final Rect canvasArea = getCanvasArea();
        final int maxPanX = Math.max(0, (workingTask.width * CELL_SIZE - canvasArea.width) / 2);
        final int maxPanY = Math.max(0, (workingTask.height * CELL_SIZE - canvasArea.height) / 2);
        panX = Math.max(-maxPanX, Math.min(maxPanX, panX));
        panY = Math.max(-maxPanY, Math.min(maxPanY, panY));
    }

    private void closeAndCommit() {
        if (closed) {
            return;
        }
        closed = true;
        onCommit.accept(workingTask);
        onBack.run();
    }

    private void renderAxisRulers(GuiGraphics graphics, Rect canvasFrame, Rect canvasArea) {
        final int centerX = canvasArea.x + canvasArea.width / 2 + panX;
        final int centerY = canvasArea.y + canvasArea.height / 2 + panY;
        final int halfWidth = workingTask.width / 2;
        final int halfHeight = workingTask.height / 2;
        final int topLabelY = canvasFrame.y + 2;
        final int leftLabelX = canvasFrame.x + 10;

        graphics.enableScissor(canvasArea.x, canvasFrame.y, canvasArea.x + canvasArea.width, canvasFrame.y + 12);
        graphics.drawCenteredString(font, "0", centerX, topLabelY, 0xFFA8A8B0);
        if (halfWidth > 0) {
            if (halfWidth < AXIS_STEP) {
                graphics.drawCenteredString(font, "+" + halfWidth, centerX + halfWidth * CELL_SIZE, topLabelY, 0xFF7A7A84);
                graphics.drawCenteredString(font, "-" + halfWidth, centerX - halfWidth * CELL_SIZE, topLabelY, 0xFF7A7A84);
            } else {
                for (int i = AXIS_STEP; i <= halfWidth; i += AXIS_STEP) {
                    graphics.drawCenteredString(font, "+" + i, centerX + i * CELL_SIZE, topLabelY, 0xFF7A7A84);
                    graphics.drawCenteredString(font, "-" + i, centerX - i * CELL_SIZE, topLabelY, 0xFF7A7A84);
                }
            }
        }
        graphics.disableScissor();

        graphics.enableScissor(canvasFrame.x, canvasArea.y, canvasFrame.x + 22, canvasArea.y + canvasArea.height);
        graphics.drawCenteredString(font, "0", leftLabelX, centerY - 5, 0xFFA8A8B0);
        if (halfHeight > 0) {
            if (halfHeight < AXIS_STEP) {
                graphics.drawCenteredString(font, "+" + halfHeight, leftLabelX, centerY - 5 + halfHeight * CELL_SIZE, 0xFF7A7A84);
                graphics.drawCenteredString(font, "-" + halfHeight, leftLabelX, centerY - 5 - halfHeight * CELL_SIZE, 0xFF7A7A84);
            } else {
                for (int i = AXIS_STEP; i <= halfHeight; i += AXIS_STEP) {
                    graphics.drawCenteredString(font, "-" + i, leftLabelX, centerY - 5 - i * CELL_SIZE, 0xFF7A7A84);
                    graphics.drawCenteredString(font, "+" + i, leftLabelX, centerY - 5 + i * CELL_SIZE, 0xFF7A7A84);
                }
            }
        }
        graphics.disableScissor();
    }

    private static int countFilledLumps(CompoundCreatorLogic.SliceTask task) {
        int count = 0;
        for (final CompoundCreatorLogic.Lump lump : task.lumps) {
            if (lump != null && lump.blockState != null) {
                count++;
            }
        }
        return count;
    }

    private interface StateTarget {
        BlockState getState();
        void setState(BlockState state);
        boolean getReplacement();
        void setReplacement(boolean replacement);
    }

    private final class CellTarget implements StateTarget {
        private final int index;

        private CellTarget(int index) {
            this.index = index;
        }

        @Override
        public BlockState getState() {
            return rte$activeLumps().get(index).blockState;
        }

        @Override
        public void setState(BlockState state) {
            rte$activeLumps().get(index).blockState = state;
        }

        @Override
        public boolean getReplacement() {
            return rte$activeLumps().get(index).replacement;
        }

        @Override
        public void setReplacement(boolean replacement) {
            rte$activeLumps().get(index).replacement = replacement;
        }
    }

    private static final class Rect {
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        private Rect(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        private boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }

    private static final class CellHit {
        private final int index;
        private final CompoundCreatorLogic.Lump lump;

        private CellHit(int index, CompoundCreatorLogic.Lump lump) {
            this.index = index;
            this.lump = lump;
        }
    }

    private static final class InventoryHit {
        private final int index;
        private final BlockState state;

        private InventoryHit(int index, BlockState state) {
            this.index = index;
            this.state = state;
        }
    }
}
