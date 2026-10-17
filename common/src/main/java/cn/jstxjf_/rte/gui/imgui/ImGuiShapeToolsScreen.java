package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.data.MsdBridge;
import cn.jstxjf_.rte.data.ParallelLineTool;
import cn.jstxjf_.rte.data.RailChainSmoother;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailGeometryAnalysis;
import cn.jstxjf_.rte.data.TracksidePlacer;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.mtr.core.data.Rail;
import net.minecraft.core.BlockPos;
import org.mtr.mapping.holder.World;

import java.util.Locale;

public class ImGuiShapeToolsScreen extends net.minecraft.client.gui.screens.Screen implements ImGuiWorkspace.Panel {

    @Override
    protected void init() {
        super.init();
        ImGuiWorkspace.open(this);
    }

    private static final float[] parallelSpacing = {4.5f};
    private static final imgui.type.ImBoolean parallelMirrorOneWay = new imgui.type.ImBoolean(true);
    private static final float[] tracksideInterval = {30f};
    private static final ImInt catenaryTypeIndex = new ImInt(0);
    private static final String[] CATENARY_TYPES = {"ELECTRIC", "CATENARY", "RIGID_CATENARY", "RIGID_SOFT_CATENARY", "NONE"};

    private final Rail pickedRail;
    private final BlockPos posStart;
    private final BlockPos posEnd;
    private final net.minecraft.client.gui.screens.Screen parent;

    private RailGeometryAnalysis.Analysis analysis;
    private String statusLine = "";
    private net.minecraft.client.gui.screens.Screen pendingScreen;

    public ImGuiShapeToolsScreen(Rail pickedRail, BlockPos posStart, BlockPos posEnd, net.minecraft.client.gui.screens.Screen parent) {
        super(Component.translatable("gui.rte.rail_shape_tools"));
        this.pickedRail = pickedRail;
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.parent = parent;
        refreshData();
    }

    private void refreshData() {
        final Rail rail = RailRebuildHelper.resolveClientRail(posStart, posEnd, pickedRail);
        analysis = rail == null ? null : RailGeometryAnalysis.analyze(rail);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RteImGuiHost.beginFrame();
        ImGuiWorkspace.drawAll();
        RteImGuiHost.endFrame();
        if (pendingScreen != null) {
            final net.minecraft.client.gui.screens.Screen next = pendingScreen;
            pendingScreen = null;
            net.minecraft.client.Minecraft.getInstance().setScreen(next);
        }
    }

    @Override
    public void drawPanel() {
        ImGui.setNextWindowSize(560, 0, imgui.flag.ImGuiCond.FirstUseEver);
        ImGui.begin(title.getString() + "###rte_shape_tools");

        if (analysis != null) {
            final float[] color = switch (analysis.rating()) {
                case RailGeometryAnalysis.RATING_GREEN -> new float[]{0.35f, 0.9f, 0.4f};
                case RailGeometryAnalysis.RATING_YELLOW -> new float[]{0.95f, 0.85f, 0.3f};
                default -> new float[]{0.95f, 0.35f, 0.3f};
            };
            ImGui.textColored(color[0], color[1], color[2], 1f, switch (analysis.rating()) {
                case RailGeometryAnalysis.RATING_GREEN -> "● " + t("gui.rte.rail_rating.green");
                case RailGeometryAnalysis.RATING_YELLOW -> "● " + t("gui.rte.rail_rating.yellow");
                default -> "● " + t("gui.rte.rail_rating.red");
            });
            ImGui.sameLine();
            ImGui.text(String.format(Locale.ROOT, " %.1f m | R min %s m | %.1f‰ | %.0f km/h",
                analysis.lengthMeters(),
                analysis.minRadiusMeters() == null ? "∞" : String.format(Locale.ROOT, "%.0f", analysis.minRadiusMeters()),
                analysis.maxGradePermille(), analysis.suggestedSpeedKph()));
        }

        drawChainSection();

        ImGui.separator();
        ImGui.text(t("gui.rte.parallel.title"));
        ImGui.sliderFloat("##parallel_spacing", parallelSpacing, 2f, 12f, "%.1f m");
        ImGui.checkbox(t("gui.rte.parallel.mirror_one_way") + "###parallel_mirror", parallelMirrorOneWay);
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.copy_left") + "###copy_left")) {
            final int created = ParallelLineTool.copyParallel(posStart, posEnd, parallelSpacing[0], false, parallelMirrorOneWay.get());
            status(t("gui.rte.parallel.result").replace("%s", String.valueOf(created)));
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.copy_right") + "###copy_right")) {
            final int created = ParallelLineTool.copyParallel(posStart, posEnd, parallelSpacing[0], true, parallelMirrorOneWay.get());
            status(t("gui.rte.parallel.result").replace("%s", String.valueOf(created)));
        }

        ImGui.separator();
        ImGui.text(t("gui.rte.imgui.trackside"));
        ImGui.sliderFloat("##trackside_interval", tracksideInterval, 5f, 60f, "%.0f m");
        if (MsdBridge.isLoaded()) {
            ImGui.combo(t("gui.rte.imgui.catenary_type"), catenaryTypeIndex, CATENARY_TYPES);
            TracksidePlacer.catenaryTypeName = CATENARY_TYPES[catenaryTypeIndex.get()];
        }
        if (MsdBridge.isLoaded()) {
            if (ImGui.button(t("gui.rte.catenary_planner.open") + "###open_catenary_planner")) {
                pendingScreen = new ImGuiCatenaryPlannerScreen(posStart, posEnd, this);
            }
        }
        if (ImGui.button(t("gui.rte.trackside.array") + "###trackside_array")) {
            final int result = TracksidePlacer.arrayAlongChain(posStart, posEnd, tracksideInterval[0]);
            status(result == -2 ? t("gui.rte.trackside.no_anchor")
                : result < 0 ? t("gui.rte.trackside.failed")
                : t("gui.rte.trackside.result").replace("%s", String.valueOf(result)));
        }

        if (!statusLine.isEmpty()) {
            ImGui.separator();
            ImGui.textColored(0.6f, 0.85f, 1f, 1f, statusLine);
        }

        ImGui.separator();
        if (ImGui.button(t("gui.rte.imgui.back") + "###back")) {
            onClose();
        }

        ImGui.end();
    }

    private void drawDepotRuleSection() {
        ImGui.separator();
        ImGui.text(t("gui.rte.depot_rules.title"));
        ImGui.textDisabled(t("gui.rte.depot_rules.hint"));
        final org.mtr.mod.client.MinecraftClientData data = org.mtr.mod.client.MinecraftClientData.getInstance();
        if (data == null || data.depots.isEmpty()) {
            ImGui.textDisabled(t("gui.rte.depot_rules.none"));
            return;
        }
        for (final org.mtr.core.data.Depot depot : data.depots) {
            final String name = depot.getName().split("\\|")[0];
            final int marked = cn.jstxjf_.rte.data.DepotRailRules.rulesOf(depot.getId()).size();
            if (ImGui.button(String.format(java.util.Locale.ROOT, "%s (%d)###depot_rule_%d",
                name, marked, depot.getId()))) {
                cn.jstxjf_.rte.render.DepotRailMarking.enter(depot.getId(), name);
                onClose();
                return;
            }
        }
    }

    private void drawChainSection() {
        drawDepotRuleSection();
        ImGui.separator();
        final org.mtr.mod.client.MinecraftClientData data = org.mtr.mod.client.MinecraftClientData.getInstance();
        final org.mtr.core.data.Position anchorA = cn.jstxjf_.rte.bridge.MtrBridge.position(posStart);
        final org.mtr.core.data.Position anchorB = cn.jstxjf_.rte.bridge.MtrBridge.position(posEnd);
        final boolean manual = cn.jstxjf_.rte.data.RailChainSelection.resolve(anchorA, anchorB) != null;

        ImGui.text(t("gui.rte.chain.title"));
        ImGui.sameLine();
        if (manual) {
            ImGui.textColored(0.95f, 0.85f, 0.3f, 1f, " " + t("gui.rte.chain.manual"));
        } else {
            ImGui.textDisabled(" " + t("gui.rte.chain.auto"));
        }

        if (!manual) {
            if (ImGui.button(t("gui.rte.chain.take_over") + "###chain_take_over")) {
                cn.jstxjf_.rte.data.RailChainSelection.adopt(
                    cn.jstxjf_.rte.data.RailChainSmoother.walkChainAuto(data, anchorA, anchorB), anchorA, anchorB);
                status(t("gui.rte.chain.taken_over"));
            }
            return;
        }

        ImGui.text(String.format(Locale.ROOT, "%s: %d", t("gui.rte.chain.nodes"),
            cn.jstxjf_.rte.data.RailChainSelection.size()));

        if (ImGui.button(t("gui.rte.chain.drag_mode") + "###chain_drag_mode")) {
            cn.jstxjf_.rte.render.RailChainHandles.enter();
            onClose();
            return;
        }
        ImGui.textDisabled(t("gui.rte.chain.drag_mode.hint"));

        if (ImGui.button(t("gui.rte.chain.trim_start") + "###chain_trim_start")) {
            if (cn.jstxjf_.rte.data.RailChainSelection.trim(true)) {
                status(t("gui.rte.chain.changed"));
            } else {
                status(t("gui.rte.chain.trim_blocked"));
            }
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.chain.trim_end") + "###chain_trim_end")) {
            if (cn.jstxjf_.rte.data.RailChainSelection.trim(false)) {
                status(t("gui.rte.chain.changed"));
            } else {
                status(t("gui.rte.chain.trim_blocked"));
            }
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.chain.reset") + "###chain_reset")) {
            cn.jstxjf_.rte.data.RailChainSelection.reset();
            status(t("gui.rte.chain.reset_done"));
        }

        drawExtensionButtons(data, true);
        drawExtensionButtons(data, false);
    }

    private void drawExtensionButtons(org.mtr.mod.client.MinecraftClientData data, boolean atStart) {
        final java.util.List<org.mtr.core.data.Position> candidates =
            cn.jstxjf_.rte.data.RailChainSelection.extensionCandidates(data, atStart);
        if (candidates.isEmpty()) {
            return;
        }
        ImGui.text(t(atStart ? "gui.rte.chain.extend_start" : "gui.rte.chain.extend_end"));
        for (int i = 0; i < candidates.size(); i++) {
            final org.mtr.core.data.Position candidate = candidates.get(i);
            if (i > 0) {
                ImGui.sameLine();
            }
            final String label = String.format(Locale.ROOT, "%d,%d,%d###chain_ext_%s_%d",
                candidate.getX(), candidate.getY(), candidate.getZ(), atStart ? "s" : "e", i);
            if (ImGui.button(label)) {
                if (cn.jstxjf_.rte.data.RailChainSelection.extend(candidate, atStart)) {
                    status(t("gui.rte.chain.changed"));
                }
            }
        }
    }

    private void status(String message) {
        statusLine = message;
        refreshData();
    }

    private static World world() {
        return new World(net.minecraft.client.Minecraft.getInstance().level);
    }

    private static String t(String key) {
        return net.minecraft.client.resources.language.I18n.get(key);
    }

    @Override
    public void onClose() {
        if (ImGuiWorkspace.activateRemaining(net.minecraft.client.Minecraft.getInstance(), this)) {
            return;
        }
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
