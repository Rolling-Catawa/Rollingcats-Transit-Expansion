package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailGeometryAnalysis;
import cn.jstxjf_.rte.gui.DirectNodeScreen;
import cn.jstxjf_.rte.gui.RailPathEditorScreen;
import cn.jstxjf_.rte.network.PacketReplaceRailNode;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailBrushHelper;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.minecraft.network.chat.Component;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.World;
import cn.jstxjf_.rte.data.RailModelProperties;
import cn.jstxjf_.rte.data.RailModelRegistry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ImGuiBrushEditRailScreen extends ImGuiScreenBase {

    private final Rail pickedRail;
    private final BlockPos posStart;
    private final BlockPos posEnd;
    private final RailExtraSupplier railExtra;
    private final org.mtr.mapping.holder.Screen mtrParent;

    private final ImInt openingDirection = new ImInt(0);
    private final ImString railType = new ImString(64);
    private final ImFloat verticalCurveRadius = new ImFloat(0);
    private final ImInt speedLimitKph = new ImInt(0);
    private final java.util.List<float[]> rollNodes = new java.util.ArrayList<>();
    private final float[] rollOffset = new float[1];
    private int rollSelected = -1;
    private boolean rollLoaded;

    public ImGuiBrushEditRailScreen(Rail pickedRail, BlockPos posStart, BlockPos posEnd, org.mtr.mapping.holder.Screen mtrParent) {
        super(Component.translatable("gui.rte.brush_edit_rail.title"), mtrParent == null ? null : mtrParent.data);
        this.pickedRail = pickedRail;
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.railExtra = ((Object) pickedRail) instanceof RailExtraSupplier extra ? extra : null;
        this.mtrParent = mtrParent;
        final CompoundTag brushTag = brushTag();
        openingDirection.set(brushTag.getInt("OpeningDirection"));
        railType.set(brushTag.contains("RailType") ? brushTag.getString("RailType") : RailRebuildHelper.getStoredOrInferredRailTypeName(pickedRail));
        verticalCurveRadius.set(brushTag.contains("VerticalCurveRadius") ? brushTag.getFloat("VerticalCurveRadius") : (railExtra != null ? railExtra.getVerticalCurveRadius() : 0f));
        speedLimitKph.set(brushTag.contains("SpeedLimitKph") ? brushTag.getInt("SpeedLimitKph") : RailRebuildHelper.getStoredOrCurrentSpeedLimitKph(pickedRail));
    }

    private static CompoundTag brushTag() {
        final CompoundTag tag = RailBrushHelper.getBrushTag();
        return tag == null ? new CompoundTag() : tag;
    }

    @Override
    protected void init() {
        super.init();
        if (rte$openSnapshot == null) {
            rte$openSnapshot = rte$snapshotOfBrush();
        }
    }

    @Override
    protected void buildWindow() {
        ImGui.setNextWindowSize(600, 640, ImGuiCond.FirstUseEver);
        ImGui.begin(title.getString() + "###rte_brush_edit");

        drawNodeSection();
        drawRatingSection();
        drawToolsSection();
        ImGui.separator();
        ImGui.textDisabled(t("gui.rte.brush_edit_rail.brush_hint"));
        drawBrushSection();
        drawCustomConfigSection();

        ImGui.separator();
        {
            final CompoundTag bt = brushTag();
            final boolean has = bt.contains("RenderReversed");
            final imgui.type.ImBoolean mirror = new imgui.type.ImBoolean(has && bt.getBoolean("RenderReversed"));
            if (ImGui.checkbox(t("gui.rte.brush_edit_rail.mirror_direction") + "###brush_mirror", mirror)) {
                RailBrushHelper.updateBrushTag(tag -> tag.putBoolean("RenderReversed", mirror.get()));
            }
            ImGui.sameLine();
            if (has && ImGui.button(t("gui.rte.imgui.reset") + "###brush_mirror_reset")) {
                RailBrushHelper.updateBrushTag(tag -> tag.remove("RenderReversed"));
            }
        }
        ImGui.separator();
        if (ImGui.button(t("gui.rte.brush_edit_rail.pick_from_rail") + "###brush_pick")) {
            rte$pickFromRail();
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.brush_edit_rail.clear_brush") + "###brush_clear")) {
            RailBrushHelper.updateBrushTag(tag -> {
                for (final String key : new String[]{"ModelKey", "VerticalCurveRadius", "RollAngleMap",
                    "RollingOffset", "RailType", "SpeedLimitKph", "OpeningDirection", "RenderReversed"}) {
                    tag.remove(key);
                }
            });
        }
        ImGui.separator();
        if (ImGui.button(t("gui.rte.imgui.apply_and_back") + "###brush_apply")) {
            onClose();
        }
        ImGui.end();
    }

    private void drawNodeSection() {
        final net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
        if (client.level == null) {
            return;
        }
        final World clientWorld = new World(client.level);
        final BlockEntity blockEntity = clientWorld.getBlockEntity(posStart);
        if (blockEntity != null && blockEntity.data instanceof BlockDirectNode.BlockEntityDirectNode directNode) {
            if (ImGui.button(t("gui.rte.brush_edit_rail.adjust_angle") + "###brush_adjust_angle")) {
                client.setScreen(DirectNodeScreen.createScreen(directNode, () -> reopen(), this::rebuildPickedRailFromWorldNodes));
            }
        } else if (pickedRail.getTransportMode() == TransportMode.TRAIN) {
            final BlockState startState = clientWorld.getBlockState(posStart);
            if (startState != null && startState.getBlock().data instanceof org.mtr.mod.block.BlockNode) {
                if (ImGui.button(t("gui.rte.brush_edit_rail.switch_to_direct_node") + "###brush_to_direct")) {
                    PacketReplaceRailNode.sendUpdateC2S(clientWorld, posStart, startState, this::reopen);
                }
            }
        }

        if (railExtra != null && posStart != null && posEnd != null) {
            ImGui.sameLine();
            final String pathModeKey = switch (railExtra.getPathMode()) {
                case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> "tooltip.rte.rail.path_mode.bezier";
                case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> "tooltip.rte.rail.path_mode.manual_bezier";
                case RailExtraSupplier.PATH_MODE_CURVE_FITTING -> "tooltip.rte.rail.path_mode.fitting";
                default -> "tooltip.rte.rail.path_mode.original";
            };
            if (ImGui.button(t("gui.rte.brush_edit_rail.path_mode", t(pathModeKey)) + "###brush_path_mode")) {
                final org.mtr.mapping.holder.Screen pathEditorScreen = RailPathEditorScreen.createScreen(
                    posStart, posEnd, pickedRail,
                    () -> new org.mtr.mapping.holder.Screen(reopen()));
                if (pathEditorScreen != null) {
                    net.minecraft.client.Minecraft.getInstance().setScreen(pathEditorScreen.data);
                }
            }
        }
    }

    private void drawRatingSection() {
        final RailGeometryAnalysis.Analysis analysis = RailGeometryAnalysis.analyze(pickedRail);
        if (analysis == null) {
            return;
        }
        final float[] color = switch (analysis.rating()) {
            case RailGeometryAnalysis.RATING_GREEN -> new float[]{0.35f, 0.9f, 0.4f};
            case RailGeometryAnalysis.RATING_YELLOW -> new float[]{0.95f, 0.85f, 0.3f};
            default -> new float[]{0.95f, 0.35f, 0.3f};
        };
        final String ratingKey = switch (analysis.rating()) {
            case RailGeometryAnalysis.RATING_GREEN -> "gui.rte.rail_rating.green";
            case RailGeometryAnalysis.RATING_YELLOW -> "gui.rte.rail_rating.yellow";
            default -> "gui.rte.rail_rating.red";
        };
        ImGui.textColored(color[0], color[1], color[2], 1f, "● " + t(ratingKey));
        ImGui.sameLine();
        ImGui.text(String.format(Locale.ROOT, " %.1f m | R min %s m | %.1f‰ | %.0f km/h",
            analysis.lengthMeters(),
            analysis.minRadiusMeters() == null ? "∞" : String.format(Locale.ROOT, "%.0f", analysis.minRadiusMeters()),
            analysis.maxGradePermille(), analysis.suggestedSpeedKph()));
    }

    private void drawToolsSection() {
        if (railExtra != null && posStart != null && posEnd != null && pickedRail.getTransportMode() == TransportMode.TRAIN) {
            if (ImGui.button(t("gui.rte.rail_shape_tools") + "###brush_shape_tools")) {
                net.minecraft.client.Minecraft.getInstance().setScreen(new ImGuiShapeToolsScreen(pickedRail, posStart, posEnd, this));
            }
        }
    }

    private void drawBrushSection() {
        final CompoundTag brushTag = brushTag();

        if (railExtra != null) {
            final boolean enabled = brushTag.contains("OpeningDirection");
            if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_opening_direction") + "###brush_od_toggle", enabled)) {
                RailBrushHelper.updateBrushTag(tag -> {
                    if (!enabled) {
                        tag.putInt("OpeningDirection", railExtra.getOpeningDirection());
                    } else {
                        tag.remove("OpeningDirection");
                    }
                });
                openingDirection.set(railExtra.getOpeningDirection());
            }
            if (enabled) {
                final int[] holder = {openingDirection.get()};
                if (ImGui.sliderInt(t("gui.rte.brush_edit_rail.opening_direction") + "###brush_od", holder, 0, 3)) {
                    openingDirection.set(holder[0]);
                    RailBrushHelper.updateBrushTag(tag -> tag.putInt("OpeningDirection", holder[0]));
                }
                if (ImGui.isItemHovered()) {
                    ImGui.setTooltip(t("gui.rte.brush_edit_rail.opening_direction_tooltip"));
                }
            }
        }

        final boolean railTypeEnabled = brushTag.contains("RailType");
        if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_rail_type") + "###brush_rt_toggle", railTypeEnabled)) {
            RailBrushHelper.updateBrushTag(tag -> {
                if (!railTypeEnabled) {
                    tag.putString("RailType", railType.get());
                } else {
                    tag.remove("RailType");
                }
            });
        }
        if (railTypeEnabled) {
            final boolean valid = RailRebuildHelper.isRailTypeValid(railType.get());
            if (!valid) {
                ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 0.95f, 0.35f, 0.3f, 1f);
            }
            if (ImGui.inputText(t("gui.rte.brush_edit_rail.rail_type") + "###brush_rt", railType) && RailRebuildHelper.isRailTypeValid(railType.get())) {
                RailBrushHelper.updateBrushTag(tag -> tag.putString("RailType", railType.get()));
            }
            if (!valid) {
                ImGui.popStyleColor();
                ImGui.textColored(0.95f, 0.35f, 0.3f, 1f, t("gui.rte.brush_edit_rail.rail_type_error"));
            }
        }

        final boolean modelKeyEnabled = brushTag.contains("ModelKey");
        final String modelKey = normalizeModelKey(railExtra != null ? railExtra.getModelKey() : "");
        if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_model_key") + "###brush_mk_toggle", modelKeyEnabled)) {
            RailBrushHelper.updateBrushTag(tag -> {
                if (!modelKeyEnabled) {
                    tag.putString("ModelKey", modelKey);
                } else {
                    tag.remove("ModelKey");
                }
            });
        }
        if (modelKeyEnabled) {
            final java.util.List<String> stored = rte$brushModelKeys(brushTag);

            if (stored.isEmpty()) {
                ImGui.text(t("gui.rte.brush_edit_rail.present", "Default"));
            } else {
                for (int i = 0; i < stored.size(); i++) {
                    final String key = stored.get(i);
                    final RailModelProperties properties = RailModelRegistry.getProperty(key);
                    final String displayName = properties != null && properties.name != null
                        && !properties.name.getString().isEmpty() ? properties.name.getString() : key;
                    ImGui.text((i == 0 ? "  " : "+ ") + displayName);
                    ImGui.sameLine();
                    if (ImGui.button(t("gui.rte.brush_edit_rail.model_remove") + "###brush_mk_del_" + i)) {
                        final java.util.List<String> next = new java.util.ArrayList<>(stored);
                        next.remove(i);
                        rte$applyModelKeys(next);
                        break;
                    }
                }
            }

            if (ImGui.button(t("gui.rte.imgui.select") + "###brush_mk_select")) {
                rte$openModelPicker(stored, true);
            }
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.brush_edit_rail.model_add") + "###brush_mk_add")) {
                rte$openModelPicker(stored, false);
            }
            ImGui.textDisabled(t("gui.rte.brush_edit_rail.model_add.hint"));
        }

        final boolean vcrEnabled = brushTag.contains("VerticalCurveRadius");
        if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_vertical_curve_radius") + "###brush_vcr_toggle", vcrEnabled)) {
            RailBrushHelper.updateBrushTag(tag -> {
                if (!vcrEnabled) {
                    tag.putFloat("VerticalCurveRadius", verticalCurveRadius.get());
                } else {
                    tag.remove("VerticalCurveRadius");
                }
            });
        }
        if (vcrEnabled) {
            if (ImGui.inputFloat(t("gui.rte.brush_edit_rail.vertical_curve_radius") + "###brush_vcr", verticalCurveRadius)) {
                RailBrushHelper.updateBrushTag(tag -> tag.putFloat("VerticalCurveRadius", verticalCurveRadius.get()));
            }
            for (final String line : verticalValueLines(verticalCurveRadius.get())) {
                ImGui.textDisabled(line);
            }
        }

        if (railExtra != null) {
            final boolean speedEnabled = brushTag.contains("SpeedLimitKph");
            if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_speed_limit") + "###brush_sl_toggle", speedEnabled)) {
                RailBrushHelper.updateBrushTag(tag -> {
                    if (!speedEnabled) {
                        tag.putInt("SpeedLimitKph", speedLimitKph.get());
                    } else {
                        tag.remove("SpeedLimitKph");
                    }
                });
            }
            if (speedEnabled) {
                if (ImGui.inputInt(t("gui.rte.brush_edit_rail.speed_limit_kph") + "###brush_sl", speedLimitKph)) {
                    final int value = Math.max(0, speedLimitKph.get());
                    speedLimitKph.set(value);
                    RailBrushHelper.updateBrushTag(tag -> tag.putInt("SpeedLimitKph", value));
                }
            }

            final boolean rollEnabled = brushTag.contains("RollAngleMap");
            if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_roll_angle") + "###brush_ra_toggle", rollEnabled)) {
                RailBrushHelper.updateBrushTag(tag -> {
                    if (!rollEnabled) {
                        tag.putString("RollAngleMap", cn.jstxjf_.rte.util.DoubleFloatMapSerializer.serializeToString(getNormalizedRollAngleMap()));
                        tag.putFloat("RollingOffset", railExtra.getRollingOffset());
                    } else {
                        tag.remove("RollAngleMap");
                        tag.remove("RollingOffset");
                    }
                });
                rollLoaded = false;
            }
            ImGui.sameLine();
            if (ImGui.button(t("gui.rte.brush_edit_rail.roll_set_clear") + "###brush_ra_setclear")) {
                RailBrushHelper.updateBrushTag(tag -> {
                    tag.putString("RollAngleMap", "");
                    tag.putFloat("RollingOffset", 0f);
                });
                rollNodes.clear();
                rollSelected = -1;
                rollOffset[0] = 0f;
                rollLoaded = true;
            }
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip(t("gui.rte.brush_edit_rail.roll_set_clear_tooltip"));
            }
            if (rollEnabled) {
                drawRollEditor();
            }
        }
    }

    private void drawCustomConfigSection() {
        if (railExtra == null) {
            return;
        }
        final Map<String, String> customConfigs = railExtra.getCustomConfigs();
        final Map<String, cn.jstxjf_.rte.data.ConfigResponder> responders = railExtra.getCustomResponders();
        if (responders == null || responders.isEmpty()) {
            return;
        }
        ImGui.separator();
        ImGuiWidgets.drawResponders("brush_custom", customConfigs, responders);
    }

    private String[] verticalValueLines(float verticalRadius) {
        if (railExtra == null) {
            return new String[]{"(???)"};
        }
        final int height = Math.abs(railExtra.getHeight());
        final double length = railExtra.getLength();
        final double maxRadius = height == 0 ? 0 : (height * height + length * length) / (height * 4D);
        final double gradient;
        if (verticalRadius < 0) {
            gradient = length == 0 ? 0 : height / length * 1000D;
        } else if (verticalRadius == 0 || verticalRadius > maxRadius) {
            gradient = Math.tan(RailExtraSupplier.getVTheta(pickedRail, maxRadius)) * 1000D;
        } else {
            gradient = Math.tan(RailExtraSupplier.getVTheta(pickedRail, verticalRadius)) * 1000D;
        }
        return new String[]{
            t("gui.rte.brush_edit_rail.vertical_curve_radius_values.1"),
            t("gui.rte.brush_edit_rail.vertical_curve_radius_values.2", String.format(Locale.ROOT, "%.1f", maxRadius)),
            t("gui.rte.brush_edit_rail.vertical_curve_radius_values.3", String.format(Locale.ROOT, "%.1f", gradient))
        };
    }

    private void drawRollEditor() {
        if (!rollLoaded) {
            rollNodes.clear();
            for (final Map.Entry<Double, Float> entry : railExtra.getRollAngleMap().entrySet()) {
                rollNodes.add(new float[]{entry.getKey().floatValue(), entry.getValue()});
            }
            rollNodes.sort((a, b) -> Float.compare(a[0], b[0]));
            rollOffset[0] = railExtra.getRollingOffset();
            rollSelected = rollNodes.isEmpty() ? -1 : 0;
            rollLoaded = true;
        }

        final cn.jstxjf_.rte.ClientConfig.Entry rotation = cn.jstxjf_.rte.ClientConfig.rollAnglesListEntryGroup.entries[0];
        final cn.jstxjf_.rte.ClientConfig.Entry offset = cn.jstxjf_.rte.ClientConfig.rollAnglesListEntryGroup.entries[1];
        final float length = (float) railExtra.getLength();

        ImGui.indent();
        if (ImGui.sliderFloat(t("gui.rte.brush_edit_rail.offset_input") + "###roll_offset", rollOffset, offset.min, offset.max, "%.4f m")) {
            railExtra.setRollingOffset(rollOffset[0]);
            RailBrushHelper.updateBrushTag(tag -> tag.putFloat("RollingOffset", rollOffset[0]), false);
        }

        if (ImGui.button("+###roll_add")) {
            rollNodes.add(new float[]{length / 2f, 0f});
            rollSelected = rollNodes.size() - 1;
            pushRollChanges();
        }
        ImGui.sameLine();
        final boolean hasSelection = rollSelected >= 0 && rollSelected < rollNodes.size();
        if (!hasSelection) {
            ImGui.beginDisabled();
        }
        if (ImGui.button(t("gui.rte.brush_edit_rail.copy") + "###roll_copy") && hasSelection) {
            final float[] source = rollNodes.get(rollSelected);
            rollNodes.add(new float[]{source[0], source[1]});
            rollSelected = rollNodes.size() - 1;
            pushRollChanges();
        }
        ImGui.sameLine();
        if (ImGui.button("-###roll_remove") && hasSelection) {
            rollNodes.remove(rollSelected);
            rollSelected = rollNodes.isEmpty() ? -1 : 0;
            pushRollChanges();
        }
        if (!hasSelection) {
            ImGui.endDisabled();
        }
        ImGui.sameLine();
        final boolean rollEmpty = rollNodes.isEmpty();
        if (rollEmpty) {
            ImGui.beginDisabled();
        }
        if (ImGui.button(t("gui.rte.brush_edit_rail.clear") + "###roll_clear") && !rollEmpty) {
            rollNodes.clear();
            rollSelected = -1;
            pushRollChanges();
        }
        if (rollEmpty) {
            ImGui.endDisabled();
        }
        ImGui.textDisabled(t("gui.rte.imgui.roll_origin_hint",
            railExtra.getExtraPosition1() == null ? "?" : railExtra.getExtraPosition1().getX() + "," + railExtra.getExtraPosition1().getY() + "," + railExtra.getExtraPosition1().getZ()));

        final Rail resolvedRail = cn.jstxjf_.rte.util.RailRebuildHelper.resolveClientRail(posStart, posEnd, pickedRail);
        final Float alignP1 = resolvedRail == null ? null : RailExtraSupplier.neighborEdgeRollForAlignment(resolvedRail, true);
        final Float alignP2 = resolvedRail == null ? null : RailExtraSupplier.neighborEdgeRollForAlignment(resolvedRail, false);
        if (alignP1 != null || alignP2 != null) {
            if (alignP1 != null) {
                if (ImGui.button(t("gui.rte.imgui.roll_align_p1", String.format(Locale.ROOT, "%.2f", Math.toDegrees(alignP1))) + "###roll_align_p1")) {
                    alignJunctionNode(0f, alignP1);
                }
                if (alignP2 != null) {
                    ImGui.sameLine();
                }
            }
            if (alignP2 != null) {
                if (ImGui.button(t("gui.rte.imgui.roll_align_p2", String.format(Locale.ROOT, "%.2f", Math.toDegrees(alignP2))) + "###roll_align_p2")) {
                    alignJunctionNode((float) railExtra.getLength(), alignP2);
                }
            }
        }

        for (int i = 0; i < rollNodes.size(); i++) {
            final float[] node = rollNodes.get(i);
            final String label = String.format(Locale.ROOT, "#%d  %.2f m  %.2f°###roll_node_%d", i + 1, node[0], Math.toDegrees(node[1]), i);
            if (ImGui.selectable(label, i == rollSelected)) {
                rollSelected = i;
            }
        }

        if (rollSelected >= 0 && rollSelected < rollNodes.size()) {
            final float[] node = rollNodes.get(rollSelected);
            final float[] positionHolder = {node[0]};
            if (ImGui.sliderFloat(t("gui.rte.brush_edit_rail.position_input") + "###roll_pos", positionHolder, 0f, length, "%.2f m")) {
                node[0] = positionHolder[0];
                pushRollChanges();
            }
            final float[] angleHolder = {(float) Math.toDegrees(node[1])};
            if (ImGui.sliderFloat(t("gui.rte.brush_edit_rail.angle_input") + "###roll_angle", angleHolder, rotation.min, rotation.max, "%.2f°")) {
                node[1] = (float) Math.toRadians(angleHolder[0]);
                pushRollChanges();
            }
        }
        ImGui.unindent();
    }

    private void alignJunctionNode(float distance, float angle) {
        rollNodes.removeIf(node -> Math.abs(node[0] - distance) < 0.5f);
        rollNodes.add(new float[]{distance, angle});
        rollNodes.sort((a, b) -> Float.compare(a[0], b[0]));
        rollSelected = rollNodes.isEmpty() ? -1 : 0;
        pushRollChanges();
    }

    private void pushRollChanges() {
        final Map<Double, Float> map = new HashMap<>();
        for (final float[] node : rollNodes) {
            map.put((double) node[0], node[1]);
        }
        railExtra.setRollAngleMap(map);
        final double length = railExtra.getLength();
        final Map<Double, Float> normalized = new HashMap<>();
        for (final Map.Entry<Double, Float> entry : map.entrySet()) {
            normalized.put(length == 0 ? 0 : entry.getKey() / length, entry.getValue());
        }
        RailBrushHelper.updateBrushTag(tag -> tag.putString("RollAngleMap", cn.jstxjf_.rte.util.DoubleFloatMapSerializer.serializeToString(normalized)), false);
    }

    private Map<Double, Float> getNormalizedRollAngleMap() {
        final Map<Double, Float> normalized = new HashMap<>();
        if (railExtra == null) {
            return normalized;
        }
        final double length = railExtra.getLength();
        for (final Map.Entry<Double, Float> entry : railExtra.getRollAngleMap().entrySet()) {
            normalized.put(length == 0 ? 0 : entry.getKey() / length, entry.getValue());
        }
        return normalized;
    }

    private void rebuildPickedRailFromWorldNodes() {
        final net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
        if (client.level == null) {
            return;
        }
        final Rail resolvedRail = RailRebuildHelper.resolveClientRail(posStart, posEnd, pickedRail);
        final Rail rebuilt = RailRebuildHelper.rebuildFromWorldNodeAngles(new World(client.level), posStart, posEnd, resolvedRail);
        if (rebuilt != null && rebuilt != resolvedRail) {
            PacketUpdateRail.sendUpdateC2S(resolvedRail, rebuilt);
        }
    }

    private net.minecraft.client.gui.screens.Screen reopen() {
        return new ImGuiBrushEditRailScreen(pickedRail, posStart, posEnd, mtrParent);
    }

    private java.util.List<String> rte$brushModelKeys(org.mtr.mapping.holder.CompoundTag brushTag) {
        if (brushTag.contains("ModelKeys")) {
            return RailBrushHelper.splitModelKeys(brushTag.getString("ModelKeys"));
        }
        final java.util.List<String> single = new java.util.ArrayList<>();
        final String key = normalizeModelKey(brushTag.getString("ModelKey"));
        if (!key.isEmpty()) {
            single.add(key);
        }
        return single;
    }

    private void rte$applyModelKeys(java.util.List<String> keys) {
        final String packed = RailBrushHelper.joinModelKeys(keys);
        RailBrushHelper.updateBrushTag(tag -> {
            tag.putString("ModelKeys", packed);
            tag.putString("ModelKey", keys.isEmpty() ? "" : keys.get(0));
        });
        if (railExtra != null) {
            railExtra.setModelKeys(keys);
        }
    }

    private void rte$openModelPicker(java.util.List<String> current, boolean replace) {
        net.minecraft.client.Minecraft.getInstance().setScreen(ImGuiSelectScreen.create(
            () -> this,
            RailModelRegistry.TREE,
            () -> current.isEmpty() ? "" : current.get(0),
            (mc, screen0, key) -> {
                final String normalizedKey = normalizeModelKey(key);
                final java.util.List<String> next = replace
                    ? new java.util.ArrayList<>()
                    : new java.util.ArrayList<>(current);
                if (!normalizedKey.isEmpty() && !next.contains(normalizedKey)) {
                    next.add(normalizedKey);
                }
                rte$applyModelKeys(next);
            },
            "https://aphrodite281.github.io/mtr-ante/#/railmodel"));
    }

    private static String normalizeModelKey(String key) {
        if (key == null) {
            return "";
        }
        final String value = key.trim();
        return RailModelRegistry.ELEMENTS.containsKey(value) ? value : "";
    }

    private String rte$openSnapshot = null;
    private boolean rte$flushed = false;

    private String rte$snapshotOfBrush() {
        try {
            final CompoundTag tag = RailBrushHelper.getBrushTag();
            return tag == null ? "" : tag.data.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void rte$pickFromRail() {
        final Rail resolved = RailRebuildHelper.resolveClientRail(posStart, posEnd, pickedRail);
        if (!(((Object) resolved) instanceof cn.jstxjf_.rte.data.RailExtraSupplier source)) {
            return;
        }
        RailBrushHelper.updateBrushTag(tag -> {
            try {
                final String modelKey = source.getModelKey();
                if (modelKey == null || modelKey.isEmpty()) {
                    tag.remove("ModelKey");
                } else {
                    tag.putString("ModelKey", modelKey);
                }
                tag.putFloat("VerticalCurveRadius", source.getVerticalCurveRadius());
                tag.putFloat("RollingOffset", source.getRollingOffset());
                tag.putInt("OpeningDirection", source.getOpeningDirection());
                tag.putInt("SpeedLimitKph", source.getSpeedLimitKph());
                final String railType = source.getRailType();
                if (railType == null || railType.isEmpty()) {
                    tag.remove("RailType");
                } else {
                    tag.putString("RailType", railType);
                }
                final java.util.Map<Double, Float> rollAngleMap = source.getRollAngleMap();
                final double length = source.getLength();
                if (rollAngleMap == null || rollAngleMap.isEmpty() || !(length > 0)) {
                    tag.remove("RollAngleMap");
                } else {
                    final java.util.Map<Double, Float> normalized = new java.util.HashMap<>();
                    for (final java.util.Map.Entry<Double, Float> entry : rollAngleMap.entrySet()) {
                        normalized.put(entry.getKey() / length, entry.getValue());
                    }
                    tag.putString("RollAngleMap",
                        cn.jstxjf_.rte.util.DoubleFloatMapSerializer.serializeToString(normalized));
                }
            } catch (Exception ignored) {
            }
        });
        RailBrushHelper.flushBrushTag();
        rte$openSnapshot = rte$snapshotOfBrush();
    }

    private void rte$syncAndFlush() {
        if (rte$flushed) {
            return;
        }
        rte$flushed = true;
        final String now = rte$snapshotOfBrush();
        if (rte$openSnapshot != null && now != null && rte$openSnapshot.equals(now)) {
            return;
        }
        final CompoundTag tag = RailBrushHelper.getBrushTag();
        final Rail resolved = RailRebuildHelper.resolveClientRail(posStart, posEnd, pickedRail);
        if (tag != null && resolved != null) {
            RailBrushHelper.applyBrushToPickedRail(posStart, posEnd, resolved, tag, false);
        } else {
            RailBrushHelper.applyBrushToPickedRail(RailBrushHelper.getBrushTag(), false);
        }
        RailBrushHelper.flushBrushTag();
    }

    @Override
    public void onClose() {
        ImGuiWorkspace.close(this);
        rte$syncAndFlush();
        super.onClose();
    }

    @Override
    public void removed() {
        rte$syncAndFlush();
        super.removed();
    }
}
