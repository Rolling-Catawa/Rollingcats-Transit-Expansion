package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailGeometryAnalysis;
import cn.jstxjf_.rte.gui.DirectNodeScreen;
import cn.jstxjf_.rte.gui.RailPathEditorScreen;
import cn.jstxjf_.rte.network.PacketReplaceRailNode;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailBrushHelper;
import cn.jstxjf_.rte.util.RailCollisionManager;
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
import net.minecraft.core.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;
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
    private final ImFloat collisionWidth = new ImFloat(3F);
    private final ImFloat collisionHeight = new ImFloat(0.25F);
    private final ImFloat collisionLength = new ImFloat(0.75F);
    private final ImFloat collisionSpacing = new ImFloat(0.5F);
    private final ImFloat collisionYOffset = new ImFloat(-0.25F);
    private final ImFloat collisionSideOffset = new ImFloat(0F);
    private final ImString collisionColumns = new ImString(256);
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
        collisionWidth.set(brushTag.contains("CollisionWidth") ? brushTag.getFloat("CollisionWidth") : railExtra != null ? railExtra.getCollisionWidth() : 3F);
        collisionHeight.set(brushTag.contains("CollisionHeight") ? brushTag.getFloat("CollisionHeight") : railExtra != null ? railExtra.getCollisionHeight() : 0.25F);
        collisionLength.set(brushTag.contains("CollisionLength") ? brushTag.getFloat("CollisionLength") : railExtra != null ? railExtra.getCollisionLength() : 0.75F);
        collisionSpacing.set(brushTag.contains("CollisionSpacing") ? brushTag.getFloat("CollisionSpacing") : railExtra != null ? railExtra.getCollisionSpacing() : 0.5F);
        collisionYOffset.set(brushTag.contains("CollisionYOffset") ? brushTag.getFloat("CollisionYOffset") : railExtra != null ? railExtra.getCollisionYOffset() : -0.25F);
        collisionSideOffset.set(brushTag.contains("CollisionSideOffset") ? brushTag.getFloat("CollisionSideOffset") : railExtra != null ? railExtra.getCollisionSideOffset() : 0F);
        collisionColumns.set(brushTag.contains("CollisionColumns") ? brushTag.getString("CollisionColumns") : RailCollisionManager.getColumns(railExtra));
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
                rte$updateBrushTag(tag -> tag.putBoolean("RenderReversed", mirror.get()));
            }
            ImGui.sameLine();
            if (has && ImGui.button(t("gui.rte.brush_edit_rail.reset") + "###brush_mirror_reset")) {
                rte$updateBrushTag(tag -> tag.remove("RenderReversed"));
            }
        }
        ImGui.separator();
        if (ImGui.button(t("gui.rte.brush_edit_rail.pick_from_rail") + "###brush_pick")) {
            rte$pickFromRail();
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.brush_edit_rail.clear_brush") + "###brush_clear")) {
            rte$updateBrushTag(tag -> {
                for (final String key : new String[]{"ModelKey", "ModelKeys", "VerticalCurveRadius", "RollAngleMap",
                    "RollingOffset", "RailType", "SpeedLimitKph", "OpeningDirection", "RenderReversed",
                    "CollisionEnabled", "CollisionWidth", "CollisionHeight", "CollisionLength", "CollisionSpacing",
                    "CollisionYOffset", "CollisionSideOffset", "CollisionColumns"}) {
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
        final Object blockEntity = client.level.getBlockEntity(posStart);
        if (blockEntity instanceof BlockDirectNode.BlockEntityDirectNode directNode) {
            if (ImGui.button(t("gui.rte.brush_edit_rail.adjust_angle") + "###brush_adjust_angle")) {
                client.setScreen(DirectNodeScreen.createScreen(directNode, () -> reopen(), this::rebuildPickedRailFromWorldNodes));
            }
        } else if (pickedRail.getTransportMode() == TransportMode.TRAIN) {
            final net.minecraft.world.level.block.state.BlockState startState = client.level.getBlockState(posStart);
            if (cn.jstxjf_.rte.bridge.MtrBridge.isNode(startState)) {
                if (ImGui.button(t("gui.rte.brush_edit_rail.switch_to_direct_node") + "###brush_to_direct")) {
                    PacketReplaceRailNode.sendUpdateC2S(client.level, posStart, startState, this::reopen);
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
                rte$updateBrushTag(tag -> {
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
                    rte$updateBrushTag(tag -> tag.putInt("OpeningDirection", holder[0]));
                }
                if (ImGui.isItemHovered()) {
                    ImGui.setTooltip(t("gui.rte.brush_edit_rail.opening_direction_tooltip"));
                }
            }
        }

        final boolean railTypeEnabled = brushTag.contains("RailType");
        if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_rail_type") + "###brush_rt_toggle", railTypeEnabled)) {
            rte$updateBrushTag(tag -> {
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
                rte$updateBrushTag(tag -> tag.putString("RailType", railType.get()));
            }
            if (!valid) {
                ImGui.popStyleColor();
                ImGui.textColored(0.95f, 0.35f, 0.3f, 1f, t("gui.rte.brush_edit_rail.rail_type_error"));
            }
        }

        final boolean modelKeyEnabled = brushTag.contains("ModelKey") || brushTag.contains("ModelKeys");
        final String modelKey = normalizeModelKey(railExtra != null ? railExtra.getModelKey() : "");
        if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_model_key") + "###brush_mk_toggle", modelKeyEnabled)) {
            if (!modelKeyEnabled) {
                final java.util.List<String> railKeys = railExtra != null ? new java.util.ArrayList<>(railExtra.getModelKeys()) : new java.util.ArrayList<>();
                if (railKeys.isEmpty() && !modelKey.isEmpty()) {
                    railKeys.add(modelKey);
                }
                rte$updateBrushTag(tag -> {
                    tag.putString("ModelKeys", RailBrushHelper.joinModelKeys(railKeys));
                    tag.putString("ModelKey", railKeys.isEmpty() ? "" : railKeys.get(0));
                });
            } else {
                rte$updateBrushTag(tag -> {
                    tag.remove("ModelKey");
                    tag.remove("ModelKeys");
                });
            }
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
            rte$updateBrushTag(tag -> {
                if (!vcrEnabled) {
                    tag.putFloat("VerticalCurveRadius", verticalCurveRadius.get());
                } else {
                    tag.remove("VerticalCurveRadius");
                }
            });
        }
        if (vcrEnabled) {
            if (ImGui.inputFloat(t("gui.rte.brush_edit_rail.vertical_curve_radius") + "###brush_vcr", verticalCurveRadius)) {
                rte$updateBrushTag(tag -> tag.putFloat("VerticalCurveRadius", verticalCurveRadius.get()));
            }
            for (final String line : verticalValueLines(verticalCurveRadius.get())) {
                ImGui.textDisabled(line);
            }
        }

        if (railExtra != null) {
            final boolean speedEnabled = brushTag.contains("SpeedLimitKph");
            if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_speed_limit") + "###brush_sl_toggle", speedEnabled)) {
                rte$updateBrushTag(tag -> {
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
                    rte$updateBrushTag(tag -> tag.putInt("SpeedLimitKph", value));
                }
            }

            drawCollisionEditor(brushTag);

            final boolean rollEnabled = brushTag.contains("RollAngleMap");
            if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_roll_angle") + "###brush_ra_toggle", rollEnabled)) {
                rte$updateBrushTag(tag -> {
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
                rte$updateBrushTag(tag -> {
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

    private void drawCollisionEditor(CompoundTag brushTag) {
        final boolean apply = brushTag.contains("CollisionEnabled");
        if (ImGui.checkbox(t("gui.rte.brush_edit_rail.enable_collision") + "###brush_collision_apply", apply)) {
            rte$updateBrushTag(tag -> {
                if (apply) {
                    for (final String key : new String[]{"CollisionEnabled", "CollisionWidth", "CollisionHeight",
                        "CollisionLength", "CollisionSpacing", "CollisionYOffset", "CollisionSideOffset", "CollisionColumns"}) {
                        tag.remove(key);
                    }
                } else {
                    tag.putBoolean("CollisionEnabled", railExtra.isCollisionEnabled());
                    tag.putFloat("CollisionWidth", collisionWidth.get());
                    tag.putFloat("CollisionHeight", collisionHeight.get());
                    tag.putFloat("CollisionLength", collisionLength.get());
                    tag.putFloat("CollisionSpacing", collisionSpacing.get());
                    tag.putFloat("CollisionYOffset", collisionYOffset.get());
                    tag.putFloat("CollisionSideOffset", collisionSideOffset.get());
                    tag.putString("CollisionColumns", collisionColumns.get());
                }
            });
        }
        if (!apply) {
            return;
        }

        final boolean enabled = brushTag.getBoolean("CollisionEnabled");
        if (ImGui.checkbox(t("gui.rte.brush_edit_rail.collision_enabled") + "###brush_collision_enabled", enabled)) {
            rte$updateBrushTag(tag -> tag.putBoolean("CollisionEnabled", !enabled));
        }
        if (!enabled) {
            return;
        }

        if (ImGui.inputFloat(t("gui.rte.brush_edit_rail.collision_width") + "###brush_collision_width", collisionWidth)) {
            collisionWidth.set(clamp(collisionWidth.get(), 0.05F, 32F));
            rte$updateBrushTag(tag -> tag.putFloat("CollisionWidth", collisionWidth.get()));
        }
        if (ImGui.inputFloat(t("gui.rte.brush_edit_rail.collision_height") + "###brush_collision_height", collisionHeight)) {
            collisionHeight.set(clamp(collisionHeight.get(), 0.05F, 16F));
            rte$updateBrushTag(tag -> tag.putFloat("CollisionHeight", collisionHeight.get()));
        }
        if (ImGui.inputFloat(t("gui.rte.brush_edit_rail.collision_length") + "###brush_collision_length", collisionLength)) {
            collisionLength.set(clamp(collisionLength.get(), 0.05F, 16F));
            rte$updateBrushTag(tag -> tag.putFloat("CollisionLength", collisionLength.get()));
        }
        if (ImGui.inputFloat(t("gui.rte.brush_edit_rail.collision_spacing") + "###brush_collision_spacing", collisionSpacing)) {
            collisionSpacing.set(clamp(collisionSpacing.get(), RailCollisionManager.MIN_SPACING, 16F));
            rte$updateBrushTag(tag -> tag.putFloat("CollisionSpacing", collisionSpacing.get()));
        }
        if (ImGui.inputFloat(t("gui.rte.brush_edit_rail.collision_y_offset") + "###brush_collision_y", collisionYOffset)) {
            collisionYOffset.set(clamp(collisionYOffset.get(), -32F, 32F));
            rte$updateBrushTag(tag -> tag.putFloat("CollisionYOffset", collisionYOffset.get()));
        }
        if (ImGui.inputFloat(t("gui.rte.brush_edit_rail.collision_side_offset") + "###brush_collision_side", collisionSideOffset)) {
            collisionSideOffset.set(clamp(collisionSideOffset.get(), -32F, 32F));
            rte$updateBrushTag(tag -> tag.putFloat("CollisionSideOffset", collisionSideOffset.get()));
        }
        if (ImGui.inputText(t("gui.rte.brush_edit_rail.collision_columns") + "###brush_collision_columns", collisionColumns)) {
            rte$updateBrushTag(tag -> tag.putString("CollisionColumns", collisionColumns.get()));
        }
        ImGui.textDisabled(t("gui.rte.brush_edit_rail.collision_hint"));
    }

    private static float clamp(float value, float min, float max) {
        return Float.isFinite(value) ? Math.max(min, Math.min(max, value)) : min;
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
            rte$refreshPreview();
            rte$updateBrushTag(tag -> tag.putFloat("RollingOffset", rollOffset[0]), false);
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
        rte$refreshPreview();
        final double length = railExtra.getLength();
        final Map<Double, Float> normalized = new HashMap<>();
        for (final Map.Entry<Double, Float> entry : map.entrySet()) {
            normalized.put(length == 0 ? 0 : entry.getKey() / length, entry.getValue());
        }
        rte$updateBrushTag(tag -> tag.putString("RollAngleMap", cn.jstxjf_.rte.util.DoubleFloatMapSerializer.serializeToString(normalized)), false);
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
        final Rail rebuilt = RailRebuildHelper.rebuildFromWorldNodeAngles(client.level, posStart, posEnd, resolvedRail);
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
        rte$updateBrushTag(tag -> {
            tag.putString("ModelKeys", packed);
            tag.putString("ModelKey", keys.isEmpty() ? "" : keys.get(0));
        });
        if (railExtra != null) {
            railExtra.setModelKeys(keys);
            rte$refreshPreview();
        }
    }

    private void rte$updateBrushTag(java.util.function.Consumer<org.mtr.mapping.holder.CompoundTag> modifier) {
        rte$updateBrushTag(modifier, true);
    }

    private void rte$updateBrushTag(java.util.function.Consumer<org.mtr.mapping.holder.CompoundTag> modifier, boolean flush) {
        RailBrushHelper.updateBrushTag(modifier, flush, posStart, posEnd, pickedRail);
    }

    private void rte$refreshPreview() {
        try {
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(pickedRail.getHexId());
            cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(railExtra.getTransposition().getHexId());
        } catch (Exception ignored) {
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
        rte$updateBrushTag(tag -> {
            try {
                final java.util.List<String> modelKeys = source.getModelKeys();
                if (modelKeys.isEmpty()) {
                    tag.remove("ModelKey");
                    tag.remove("ModelKeys");
                } else {
                    tag.putString("ModelKey", modelKeys.get(0));
                    tag.putString("ModelKeys", RailBrushHelper.joinModelKeys(modelKeys));
                }
                tag.putFloat("VerticalCurveRadius", source.getVerticalCurveRadius());
                tag.putFloat("RollingOffset", source.getRollingOffset());
                tag.putInt("OpeningDirection", source.getOpeningDirection());
                tag.putInt("SpeedLimitKph", source.getSpeedLimitKph());
                tag.putBoolean("CollisionEnabled", source.isCollisionEnabled());
                tag.putFloat("CollisionWidth", source.getCollisionWidth());
                tag.putFloat("CollisionHeight", source.getCollisionHeight());
                tag.putFloat("CollisionLength", source.getCollisionLength());
                tag.putFloat("CollisionSpacing", source.getCollisionSpacing());
                tag.putFloat("CollisionYOffset", source.getCollisionYOffset());
                tag.putFloat("CollisionSideOffset", source.getCollisionSideOffset());
                tag.putString("CollisionColumns", RailCollisionManager.getColumns(source));
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
            if (!RailBrushHelper.applyBrushToPickedRail(posStart, posEnd, resolved, tag, false)) {
                PacketUpdateRail.sendUpdateC2S(resolved);
            }
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
