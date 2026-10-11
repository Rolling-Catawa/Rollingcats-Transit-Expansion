package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.render.ShadersModHandler;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class ImGuiConfigScreen extends ImGuiScreenBase {

    private final ImInt railInterval = new ImInt(ClientConfig.railDistanceRendererInterval);
    private final ImInt mtrMeshDedupeLimit = new ImInt(ClientConfig.mtrMeshDedupeFaceLimit);
    private final ImInt maxTextureSize = new ImInt(ClientConfig.maxTextureSize);
    private final ImInt scriptTextureLimit = new ImInt(ClientConfig.scriptTextureLimit);
    private final ImInt bbModelPreloadLimit = new ImInt(ClientConfig.bbModelPreloadLimit);
    private final ImInt preloadHeapCeiling = new ImInt(ClientConfig.preloadHeapCeilingPercent);
    private final ImInt railMaxDistance = new ImInt((int) Math.round(Math.sqrt(Math.max(0, ClientConfig.railDistanceRendererMaxDistanceSqr))));
    private final Map<String, ImFloat> floatHolders = new HashMap<>();
    private final Map<String, ImInt> intHolders = new HashMap<>();
    private boolean dirty;

    public ImGuiConfigScreen(Screen parent) {
        super(Component.translatable("gui.rte.config.client.title"), parent);
    }

    @Override
    protected void buildWindow() {
        ImGui.setNextWindowSize(620, 640, ImGuiCond.FirstUseEver);
        ImGui.begin(title.getString() + "###rte_config_screen");

        if (ImGui.checkbox(t("gui.rte.config.client.hideridingtrain") + "###cfg_hideriding", ClientConfig.hideRidingTrain)) {
            ClientConfig.hideRidingTrain = !ClientConfig.hideRidingTrain;
            dirty = true;
        }

        if (!ShadersModHandler.canDrawWithBuffer()) {
            ImGui.textColored(0.95f, 0.85f, 0.3f, 1f, t("gui.rte.config.client.shaderactive"));
        } else if (ShadersModHandler.isShaderPackGpuPathActive()) {
            ImGui.textColored(0.5f, 0.85f, 0.5f, 1f, t("gui.rte.config.client.shaderactive_gpu"));
        }

        if (ImGui.checkbox(t("gui.rte.config.client.rail3d") + "###cfg_rail3d", ClientConfig.enableRail3D)) {
            ClientConfig.enableRail3D = !ClientConfig.enableRail3D;
            reloadLevelRenderer();
            dirty = true;
        }
        ImGui.textDisabled(t("gui.rte.config.client.rail3d.description"));

        if (ImGui.checkbox(t("gui.rte.config.client.brush_editing") + "###cfg_brushedit", ClientConfig.rteBrushEditing)) {
            ClientConfig.rteBrushEditing = !ClientConfig.rteBrushEditing;
            dirty = true;
        }
        ImGui.textDisabled(t("gui.rte.config.client.brush_editing.hint"));

        if (ImGui.checkbox(t("gui.rte.config.client.override_mtr_screens") + "###cfg_ovrscreen", ClientConfig.overrideMtrScreens)) {
            ClientConfig.overrideMtrScreens = !ClientConfig.overrideMtrScreens;
            dirty = true;
        }
        ImGui.textDisabled(t("gui.rte.config.client.override_mtr_screens.hint"));

        if (ImGui.checkbox(t("gui.rte.config.client.rail_deform") + "###cfg_deform", ClientConfig.enableRailDeform)) {
            ClientConfig.enableRailDeform = !ClientConfig.enableRailDeform;
            if (ClientConfig.enableRail3D) {
                reloadLevelRenderer();
            }
            dirty = true;
        }
        if (ImGui.isItemHovered()) {
            ImGui.setTooltip(t("gui.rte.config.client.rail_deform.description"));
        }

        if (ImGui.checkbox(t("gui.rte.config.client.preloadbbmodel") + "###cfg_preload", ClientConfig.enableBbModelPreload)) {
            ClientConfig.enableBbModelPreload = !ClientConfig.enableBbModelPreload;
            dirty = true;
            final Minecraft client = Minecraft.getInstance();
            client.execute(client::reloadResourcePacks);
        }
        ImGui.textDisabled(t("gui.rte.config.client.preloadbbmodel.description"));
        dirty |= toggle("gui.rte.config.client.mesh_disk_cache", "cfg_mesh_disk_cache", ClientConfig.vehicleMeshDiskCache, v -> ClientConfig.vehicleMeshDiskCache = v);
        ImGui.textDisabled(t("gui.rte.config.client.mesh_disk_cache.description"));
        dirty |= toggle("gui.rte.config.client.slider", "cfg_slider", ClientConfig.useEditBoxSetRailRolling, v -> ClientConfig.useEditBoxSetRailRolling = v);
        dirty |= toggle("gui.rte.config.client.renderdiag", "cfg_renderdiag", ClientConfig.enableRenderDiagnostics, v -> ClientConfig.enableRenderDiagnostics = v);
        dirty |= toggle("gui.rte.config.client.gpu_translucent", "cfg_gpu_translucent", ClientConfig.gpuTranslucentRendering, v -> ClientConfig.gpuTranslucentRendering = v);
        dirty |= toggle("gui.rte.config.client.shaderpack_gpu", "cfg_shaderpack_gpu", ClientConfig.shaderPackGpuRendering, v -> ClientConfig.shaderPackGpuRendering = v);
        ImGui.textDisabled(t("gui.rte.config.client.shaderpack_gpu.description"));
        dirty |= toggle("gui.rte.config.client.instanced_vehicle", "cfg_instanced_vehicle", ClientConfig.instancedVehicleRendering, v -> ClientConfig.instancedVehicleRendering = v);
        ImGui.textDisabled(t("gui.rte.config.client.instanced_vehicle.description"));

        ImGui.separator();
        ImGui.text(t("gui.rte.config.client.category.misc"));

        dirty |= toggle("gui.rte.config.client.translucentsort", "cfg_sort", ClientConfig.translucentSort, v -> ClientConfig.translucentSort = v);
        dirty |= toggle("gui.rte.config.client.shadercompat", "cfg_shader", ClientConfig.enableOptimization, v -> ClientConfig.enableOptimization = v);
        ImGui.textDisabled(t("gui.rte.config.client.shadercompat.description"));
        if (ClientConfig.enableOptimization && !ShadersModHandler.canDrawWithBuffer()) {
            ImGui.textColored(0.95f, 0.85f, 0.3f, 1f, t("gui.rte.config.client.shaderactive"));
        }
        dirty |= toggle("gui.rte.config.client.trainrender", "cfg_trainrender", ClientConfig.enableTrainRender, v -> ClientConfig.enableTrainRender = v);
        dirty |= toggle("gui.rte.config.client.trainsound", "cfg_trainsound", ClientConfig.enableTrainSound, v -> ClientConfig.enableTrainSound = v);
        dirty |= toggle("gui.rte.config.client.railrender", "cfg_railrender", ClientConfig.enableRailRender, v -> ClientConfig.enableRailRender = v);
        dirty |= toggle("gui.rte.config.client.slsmoke", "cfg_smoke", ClientConfig.enableSmoke, v -> ClientConfig.enableSmoke = v);
        dirty |= toggle("gui.rte.config.client.rolling", "cfg_rolling", ClientConfig.enableRolling, v -> ClientConfig.enableRolling = v);

        if (ImGui.inputInt(t("gui.rte.config.client.rail_distance_renderer_interval") + "###cfg_interval", railInterval)) {
            ClientConfig.railDistanceRendererInterval = Math.max(1, Math.min(120, railInterval.get()));
            railInterval.set(ClientConfig.railDistanceRendererInterval);
            dirty = true;
        }
        if (ImGui.inputInt(t("gui.rte.config.client.rail_distance_renderer_max_distance_sqr") + "###cfg_maxdist", railMaxDistance)) {
            final int distance = Math.max(0, Math.min(4096, railMaxDistance.get()));
            railMaxDistance.set(distance);
            ClientConfig.railDistanceRendererMaxDistanceSqr = distance * distance;
            dirty = true;
        }

        if (ImGui.inputInt(t("gui.rte.config.client.max_texture_size") + "###cfg_maxtex", maxTextureSize)) {
            ClientConfig.maxTextureSize = Math.max(0, Math.min(16384, maxTextureSize.get()));
            maxTextureSize.set(ClientConfig.maxTextureSize);
            ClientConfig.save();
        }
        ImGui.textDisabled(t("gui.rte.config.client.max_texture_size.hint"));
        if (ImGui.inputInt(t("gui.rte.config.client.script_texture_limit") + "###cfg_scripttex", scriptTextureLimit)) {
            ClientConfig.scriptTextureLimit = Math.max(0, Math.min(4096, scriptTextureLimit.get()));
            scriptTextureLimit.set(ClientConfig.scriptTextureLimit);
            ClientConfig.save();
        }
        if (ImGui.inputInt(t("gui.rte.config.client.bbmodel_preload_limit") + "###cfg_bbpreload", bbModelPreloadLimit)) {
            ClientConfig.bbModelPreloadLimit = Math.max(0, Math.min(4096, bbModelPreloadLimit.get()));
            bbModelPreloadLimit.set(ClientConfig.bbModelPreloadLimit);
            ClientConfig.save();
        }
        if (ImGui.inputInt(t("gui.rte.config.client.preload_heap_ceiling") + "###cfg_preheap", preloadHeapCeiling)) {
            ClientConfig.preloadHeapCeilingPercent = Math.max(5, Math.min(80, preloadHeapCeiling.get()));
            preloadHeapCeiling.set(ClientConfig.preloadHeapCeilingPercent);
            ClientConfig.save();
        }
        ImGui.textDisabled(t("gui.rte.config.client.preload_heap_ceiling.hint"));
        if (ImGui.inputInt(t("gui.rte.config.client.mtr_mesh_dedupe_limit") + "###cfg_meshdedupe", mtrMeshDedupeLimit)) {
            ClientConfig.mtrMeshDedupeFaceLimit = Math.max(0, Math.min(1000000, mtrMeshDedupeLimit.get()));
            mtrMeshDedupeLimit.set(ClientConfig.mtrMeshDedupeFaceLimit);
            dirty = true;
        }
        ImGui.textDisabled(t("gui.rte.config.client.mtr_mesh_dedupe_limit.description"));

        drawEntryGroup(ClientConfig.eyecandyScreenGroup);
        if (ImGui.collapsingHeader(t("gui.rte.imgui.group.direct_node_screen") + "###grp_direct_node_screen")) {
            drawEntry("direct_node_screen", ClientConfig.directNodeScreenGroup);
        }
        drawEntryGroup(ClientConfig.rollAnglesListEntryGroup);

        ImGui.separator();
        dirty |= ImGuiWidgets.drawResponders("client_cfg", ClientConfig.getCustomConfigs(), ClientConfig.getCustomResponders());

        ImGui.separator();
        if (ImGui.button(t("gui.rte.workbench.title") + "###cfg_workbench")) {
            minecraft.setScreen(new ImGuiResourceWorkbenchScreen(this));
            ImGui.end();
            return;
        }
        ImGui.sameLine();
        if (ImGui.button(t("gui.rte.imgui.back") + "###cfg_back")) {
            onClose();
        }
        ImGui.end();
    }

    private void drawEntryGroup(ClientConfig.EntryGroup group) {
        if (!ImGui.collapsingHeader(t("gui.rte.imgui.group." + group.key) + "###grp_" + group.key)) {
            return;
        }
        for (final ClientConfig.Entry entry : group.entries) {
            drawEntry(group.key, entry);
        }
    }

    private void drawEntry(String groupKey, ClientConfig.Entry entry) {
        final String base = groupKey + "_" + entry.key;
        final ImFloat min = floatHolders.computeIfAbsent(base + "_min", k -> new ImFloat(entry.min));
        final ImFloat max = floatHolders.computeIfAbsent(base + "_max", k -> new ImFloat(entry.max));
        final ImInt step = intHolders.computeIfAbsent(base + "_step", k -> new ImInt(entry.step));
        if (ImGui.inputFloat(t("gui.rte." + entry.tooltipKey + ".min") + "###" + base + "_min", min)) {
            entry.min = min.get();
            dirty = true;
        }
        if (ImGui.inputFloat(t("gui.rte." + entry.tooltipKey + ".max") + "###" + base + "_max", max)) {
            entry.max = max.get();
            dirty = true;
        }
        if (ImGui.inputInt(t("gui.rte." + entry.tooltipKey + ".step") + "###" + base + "_step", step)) {
            entry.step = step.get();
            dirty = true;
        }
        ImGui.spacing();
    }

    private interface BoolSetter {
        void set(boolean value);
    }

    private static boolean toggle(String key, String id, boolean current, BoolSetter setter) {
        if (ImGui.checkbox(t(key) + "###" + id, current)) {
            setter.set(!current);
            return true;
        }
        return false;
    }

    private static void reloadLevelRenderer() {
        final Minecraft client = Minecraft.getInstance();
        if (client.level != null) {
            client.levelRenderer.allChanged();
        }
    }

    @Override
    public void onClose() {
        ImGuiWorkspace.close(this);
        if (dirty) {
            ClientConfig.save();
        }
        super.onClose();
    }
}
