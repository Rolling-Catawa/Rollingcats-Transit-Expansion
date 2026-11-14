package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.client.gui.SliceTaskCanvasScreen;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailModelProperties;
import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.item.CompoundCreatorLogic;
import cn.jstxjf_.rte.item.CompoundCreatorLogic.RailModifierTask;
import cn.jstxjf_.rte.item.CompoundCreatorLogic.SliceTask;
import cn.jstxjf_.rte.item.CompoundCreatorLogic.Task;
import cn.jstxjf_.rte.network.PacketUpdateHoldingItem;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.minecraft.network.chat.Component;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.MinecraftClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class ImGuiCompoundCreatorScreen extends ImGuiScreenBase {

    private static final String TAG_TASKS = "tasks";
    private static final String[] OPENING_DIRECTIONS = {"None", "Left", "Right", "Both"};

    private final Hand hand;
    private final List<Task> tasks = new ArrayList<>();
    private final Map<Integer, ImString> nameBuffers = new HashMap<>();
    private int selectedIndex = -1;

    private RailModifierTask railOriginal;
    private RailModifierTask railEdited;
    private final ImString railType = new ImString(64);
    private final ImInt speedLimit = new ImInt(0);
    private final ImFloat verticalRadius = new ImFloat(0);
    private final ImInt openingDirection = new ImInt(0);
    private final List<float[]> rollNodes = new ArrayList<>();
    private final float[] rollOffset = new float[1];
    private int rollSelected = -1;

    public ImGuiCompoundCreatorScreen(net.minecraft.client.gui.screens.Screen parent, Hand hand, int selectedIndex) {
        super(Component.translatable("gui.rte.compound_creator.title"), parent);
        this.hand = hand;
        load();
        this.selectedIndex = tasks.isEmpty() ? -1 : Math.max(0, Math.min(selectedIndex, tasks.size() - 1));
    }

    private void load() {
        tasks.clear();
        nameBuffers.clear();
        tasks.addAll(CompoundCreatorLogic.getTasks(getStack()));
    }

    private ItemStack getStack() {
        final ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
        return player == null ? ItemStack.getEmptyMapped() : player.getStackInHand(hand);
    }

    private void update() {
        final ItemStack stack = getStack();
        if (stack.isEmpty()) {
            return;
        }
        final CompoundTag tag = stack.getOrCreateTag();
        final CompoundTag tasksTag = new CompoundTag();
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).order = i;
            tasksTag.data.put(String.valueOf(i), tasks.get(i).toCompoundTag().data);
        }
        tag.data.put(TAG_TASKS, tasksTag.data);
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketUpdateHoldingItem(hand));
    }

    @Override
    protected void buildWindow() {
        if (railEdited != null) {
            buildRailEditorWindow();
        } else {
            buildListWindow();
        }
    }

    private void buildListWindow() {
        ImGui.setNextWindowSize(760, 520, ImGuiCond.FirstUseEver);
        ImGui.begin(t("gui.rte.compound_creator.title") + "###rte_compound_creator");

        ImGui.textDisabled(t("gui.rte.compound_creator.hint"));

        if (ImGui.button("+###cc_add")) {
            ImGui.openPopup("###cc_add_popup");
        }
        if (ImGui.beginPopup("###cc_add_popup")) {
            if (ImGui.menuItem(t("gui.rte.compound_creator.slice_task") + "###cc_add_slice")) {
                addTask(new SliceTask());
            }
            if (ImGui.menuItem(t("gui.rte.compound_creator.rail_modifier") + "###cc_add_rail")) {
                addTask(new RailModifierTask());
            }
            ImGui.endPopup();
        }
        ImGui.sameLine();
        final boolean hasSelection = selectedIndex >= 0 && selectedIndex < tasks.size();
        if (!hasSelection) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("-###cc_remove") && hasSelection) {
            tasks.remove(selectedIndex);
            selectedIndex = tasks.isEmpty() ? -1 : Math.min(selectedIndex, tasks.size() - 1);
            nameBuffers.clear();
            update();
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.compound_creator.copy") + "###cc_copy") && hasSelection) {
            final Task copiedTask = CompoundCreatorLogic.copyTask(tasks.get(selectedIndex));
            copiedTask.order = tasks.size();
            copiedTask.name = copiedTask.name + " Copy";
            tasks.add(copiedTask);
            selectedIndex = tasks.size() - 1;
            update();
        }
        if (!hasSelection) {
            ImGui.endDisabled();
        }
        ImGui.sameLine();
        final boolean noTasks = tasks.isEmpty();
        if (noTasks) {
            ImGui.beginDisabled();
        }
        if (ImGui.button(t("gui.rte.compound_creator.clear") + "###cc_clear") && !noTasks) {
            tasks.clear();
            selectedIndex = -1;
            nameBuffers.clear();
            update();
        }
        if (noTasks) {
            ImGui.endDisabled();
        }

        ImGui.separator();
        ImGui.beginChild("###cc_list", 0, -36, true);
        for (int i = 0; i < tasks.size(); i++) {
            final Task task = tasks.get(i);
            ImGui.pushID(i);
            if (ImGui.selectable("#" + (i + 1) + "###cc_row", i == selectedIndex, 0, 30, 0)) {
                selectedIndex = i;
            }
            ImGui.sameLine();
            final ImString nameBuffer = nameBuffers.computeIfAbsent(System.identityHashCode(task), k -> new ImString(task.name, 256));
            ImGui.setNextItemWidth(160);
            if (ImGui.inputText("###cc_name", nameBuffer)) {
                task.name = nameBuffer.get();
                selectedIndex = i;
                update();
            }
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.compound_creator.edit") + "###cc_edit")) {
                selectedIndex = i;
                openTaskEditor(task);
            }
            ImGui.sameLine();
            if (i <= 0) {
                ImGui.beginDisabled();
            }
            if (ImGui.arrowButton("###cc_up", imgui.flag.ImGuiDir.Up) && i > 0) {
                tasks.set(i, tasks.get(i - 1));
                tasks.set(i - 1, task);
                selectedIndex = i - 1;
                update();
            }
            if (i <= 0) {
                ImGui.endDisabled();
            }
            ImGui.sameLine();
            if (i >= tasks.size() - 1) {
                ImGui.beginDisabled();
            }
            if (ImGui.arrowButton("###cc_down", imgui.flag.ImGuiDir.Down) && i < tasks.size() - 1) {
                tasks.set(i, tasks.get(i + 1));
                tasks.set(i + 1, task);
                selectedIndex = i + 1;
                update();
            }
            if (i >= tasks.size() - 1) {
                ImGui.endDisabled();
            }
            ImGui.sameLine();
            ImGui.textDisabled(buildSummary(task));
            ImGui.popID();
        }
        if (tasks.isEmpty()) {
            ImGui.textDisabled(t("gui.rte.compound_creator.no_task_selected"));
        }
        ImGui.endChild();

        if (ImGui.button(t("gui.rte.imgui.back") + "###cc_back")) {
            onClose();
        }
        ImGui.end();
    }

    private void addTask(Task task) {
        task.order = tasks.size();
        tasks.add(task);
        selectedIndex = tasks.size() - 1;
        update();
    }

    private void openTaskEditor(Task task) {
        if (task instanceof RailModifierTask railModifierTask) {
            railOriginal = railModifierTask;
            railEdited = new RailModifierTask(railModifierTask);
            final RailExtraSupplier extra = extraOf(railEdited.rail);
            railType.set(RailRebuildHelper.getStoredOrInferredRailTypeName(railEdited.rail));
            speedLimit.set(RailRebuildHelper.getStoredOrCurrentSpeedLimitKph(railEdited.rail));
            verticalRadius.set(extra.getVerticalCurveRadius());
            openingDirection.set(Math.max(0, Math.min(3, extra.getOpeningDirection())));
            rollNodes.clear();
            for (final Map.Entry<Double, Float> entry : extra.getRollAngleMap().entrySet()) {
                rollNodes.add(new float[]{entry.getKey().floatValue(), entry.getValue()});
            }
            rollNodes.sort((a, b) -> Float.compare(a[0], b[0]));
            rollOffset[0] = extra.getRollingOffset();
            rollSelected = rollNodes.isEmpty() ? -1 : 0;
        } else if (task instanceof SliceTask sliceTask) {
            final Consumer<SliceTask> commit = updatedTask -> {
                sliceTask.copyFrom(updatedTask);
                update();
            };
            final ImGuiCompoundCreatorScreen self = this;
            minecraft.setScreen(new SliceTaskCanvasScreen(sliceTask, commit, () -> minecraft.setScreen(self)));
        }
    }

    private void buildRailEditorWindow() {
        ImGui.setNextWindowSize(560, 0, ImGuiCond.FirstUseEver);
        ImGui.begin(t("gui.rte.compound_creator.task.rail_modifier.title") + "###rte_cc_rail_editor");

        final RailExtraSupplier extra = extraOf(railEdited.rail);

        ImGui.text(modelLabel(extra));
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.select") + "###ccr_model")) {
            final ImGuiCompoundCreatorScreen self = this;
            minecraft.setScreen(ImGuiSelectScreen.create(
                () -> self,
                RailModelRegistry.TREE,
                extra::getModelKey,
                (mc, screen, key) -> {
                    extra.setModelKey(key);
                    minecraft.setScreen(self);
                },
                "https://aphrodite281.github.io/mtr-ante/#/railmodel"));
            ImGui.end();
            return;
        }

        final boolean railTypeValid = railType.get().isBlank() || RailRebuildHelper.isRailTypeValid(railType.get().trim().toUpperCase(Locale.ROOT));
        if (!railTypeValid) {
            ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 1f, 0.35f, 0.35f, 1f);
        }
        ImGui.inputText(t("gui.rte.brush_edit_rail.rail_type") + "###ccr_rail_type", railType);
        if (!railTypeValid) {
            ImGui.popStyleColor();
        }
        ImGui.inputInt(t("gui.rte.brush_edit_rail.speed_limit_kph") + "###ccr_speed", speedLimit);
        ImGui.inputFloat(t("gui.rte.brush_edit_rail.vertical_curve_radius") + "###ccr_vr", verticalRadius);

        if (ImGui.checkbox(t("gui.rte.compound_creator.isOneWay") + "###ccr_oneway", railEdited.isOneWay)) {
            railEdited.isOneWay = !railEdited.isOneWay;
        }
        ImGui.sameLine();
        if (ImGui.checkbox(t("gui.rte.compound_creator.isReversed") + "###ccr_reversed", railEdited.isReversed)) {
            railEdited.isReversed = !railEdited.isReversed;
        }
        ImGui.combo(t("gui.rte.brush_edit_rail.opening_direction") + "###ccr_opening", openingDirection, OPENING_DIRECTIONS);

        ImGui.separator();
        ImGui.text(t("gui.rte.imgui.roll_angle_editor"));
        drawRollEditor(extra);

        final Map<String, cn.jstxjf_.rte.data.ConfigResponder> responders = extra.getCustomResponders();
        if (responders != null && !responders.isEmpty()) {
            ImGui.separator();
            ImGuiWidgets.drawResponders("cc_rail_custom", extra.getCustomConfigs(), responders);
        }

        ImGui.separator();
        final boolean canSave = railTypeValid && speedLimit.get() >= 0;
        if (!canSave) {
            ImGui.beginDisabled();
        }
        if (ImGui.button(t("gui.rte.compound_creator.save") + "###ccr_save") && canSave) {
            saveRailTask(extra);
        }
        if (!canSave) {
            ImGui.endDisabled();
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.compound_creator.back") + "###ccr_cancel")) {
            closeRailEditor();
        }
        ImGui.end();
    }

    private void drawRollEditor(RailExtraSupplier extra) {
        final cn.jstxjf_.rte.ClientConfig.Entry rotation = cn.jstxjf_.rte.ClientConfig.rollAnglesListEntryGroup.entries[0];
        final cn.jstxjf_.rte.ClientConfig.Entry offset = cn.jstxjf_.rte.ClientConfig.rollAnglesListEntryGroup.entries[1];
        final float length = (float) extra.getLength();

        ImGui.indent();
        if (ImGui.sliderFloat(t("gui.rte.brush_edit_rail.offset_input") + "###ccr_roll_offset", rollOffset, offset.min, offset.max, "%.4f m")) {
            extra.setRollingOffset(rollOffset[0]);
        }

        if (ImGui.button("+###ccr_roll_add")) {
            rollNodes.add(new float[]{length / 2f, 0f});
            rollSelected = rollNodes.size() - 1;
            pushRollChanges(extra);
        }
        ImGui.sameLine();
        final boolean hasSelection = rollSelected >= 0 && rollSelected < rollNodes.size();
        if (!hasSelection) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("-###ccr_roll_remove") && hasSelection) {
            rollNodes.remove(rollSelected);
            rollSelected = rollNodes.isEmpty() ? -1 : 0;
            pushRollChanges(extra);
        }
        if (!hasSelection) {
            ImGui.endDisabled();
        }
        ImGui.sameLine();
        final boolean rollEmpty = rollNodes.isEmpty();
        if (rollEmpty) {
            ImGui.beginDisabled();
        }
        if (ImGui.button(t("gui.rte.brush_edit_rail.clear") + "###ccr_roll_clear") && !rollEmpty) {
            rollNodes.clear();
            rollSelected = -1;
            pushRollChanges(extra);
        }
        if (rollEmpty) {
            ImGui.endDisabled();
        }

        for (int i = 0; i < rollNodes.size(); i++) {
            final float[] node = rollNodes.get(i);
            final String label = String.format(Locale.ROOT, "#%d  %.2f m  %.2f°###ccr_roll_node_%d", i + 1, node[0], Math.toDegrees(node[1]), i);
            if (ImGui.selectable(label, i == rollSelected)) {
                rollSelected = i;
            }
        }

        if (rollSelected >= 0 && rollSelected < rollNodes.size()) {
            final float[] node = rollNodes.get(rollSelected);
            final float[] positionHolder = {node[0]};
            if (ImGui.sliderFloat(t("gui.rte.brush_edit_rail.position_input") + "###ccr_roll_pos", positionHolder, 0f, length, "%.2f m")) {
                node[0] = positionHolder[0];
                pushRollChanges(extra);
            }
            final float[] angleHolder = {(float) Math.toDegrees(node[1])};
            if (ImGui.sliderFloat(t("gui.rte.brush_edit_rail.angle_input") + "###ccr_roll_angle", angleHolder, rotation.min, rotation.max, "%.2f°")) {
                node[1] = (float) Math.toRadians(angleHolder[0]);
                pushRollChanges(extra);
            }
        }
        ImGui.unindent();
    }

    private void pushRollChanges(RailExtraSupplier extra) {
        final Map<Double, Float> map = new HashMap<>();
        for (final float[] node : rollNodes) {
            map.put((double) node[0], node[1]);
        }
        extra.setRollAngleMap(map);
    }

    private void saveRailTask(RailExtraSupplier extra) {
        extra.setRailType(railType.get().trim().toUpperCase(Locale.ROOT));
        extra.setSpeedLimitKph(Math.max(0, speedLimit.get()));
        extra.setVerticalCurveRadius(verticalRadius.get());
        extra.setOpeningDirection(openingDirection.get());
        railEdited.rail = RailRebuildHelper.rebuildFromCurrentState(railEdited.rail);
        railOriginal.copyFrom(railEdited);
        update();
        closeRailEditor();
    }

    private void closeRailEditor() {
        railOriginal = null;
        railEdited = null;
    }

    private static RailExtraSupplier extraOf(Rail rail) {
        return (RailExtraSupplier) (Object) rail;
    }

    private static String modelLabel(RailExtraSupplier extra) {
        final RailModelProperties properties = RailModelRegistry.getProperty(extra.getModelKey());
        final String label = properties == null || properties.name == null || properties.name.getString().isBlank()
            ? extra.getModelKey()
            : properties.name.getString();
        return label == null || label.isBlank()
            ? net.minecraft.client.resources.language.I18n.get("gui.rte.compound_creator.task.rail_modifier.select_model")
            : label;
    }

    private static String openingDirectionLabel(int value) {
        return OPENING_DIRECTIONS[Math.max(0, Math.min(3, value))];
    }

    private static int countFilledLumps(SliceTask task) {
        int count = 0;
        for (final CompoundCreatorLogic.Lump lump : task.lumps) {
            if (lump != null && lump.blockState != null) {
                count++;
            }
        }
        return count;
    }

    private static String buildSummary(Task task) {
        if (task instanceof RailModifierTask railModifierTask) {
            final RailExtraSupplier extra = extraOf(railModifierTask.rail);
            final RailModelProperties properties = RailModelRegistry.getProperty(extra.getModelKey());
            final String modelName = properties == null || properties.name == null || properties.name.getString().isBlank() ? extra.getModelKey() : properties.name.getString();
            return String.format(
                Locale.ROOT,
                "Modifier | %s | type=%s speed=%skph open=%s ow=%s rev=%s vr=%.1f",
                modelName,
                RailRebuildHelper.getStoredOrInferredRailTypeName(railModifierTask.rail),
                RailRebuildHelper.getStoredOrCurrentSpeedLimitKph(railModifierTask.rail),
                openingDirectionLabel(extra.getOpeningDirection()),
                railModifierTask.isOneWay,
                railModifierTask.isReversed,
                extra.getVerticalCurveRadius()
            );
        }
        if (task instanceof SliceTask sliceTask) {
            return String.format(
                Locale.ROOT,
                "Slice | %dx%d start=%.1f len=%s intv=%s inc=%.2f fill=%d yaw=%s pitch=%s roll=%s",
                sliceTask.width,
                sliceTask.height,
                sliceTask.start,
                sliceTask.length == null ? "all" : String.format(Locale.ROOT, "%.1f", sliceTask.length),
                sliceTask.interval == null ? "once" : String.format(Locale.ROOT, "%.1f", sliceTask.interval),
                sliceTask.increment,
                countFilledLumps(sliceTask),
                sliceTask.useYaw,
                sliceTask.usePitch,
                sliceTask.useRoll
            );
        }
        return task.getClass().getSimpleName();
    }
}
