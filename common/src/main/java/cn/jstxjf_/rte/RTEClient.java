package cn.jstxjf_.rte;

import cn.jstxjf_.rte.block.RTEBlockEntityTypes;
import cn.jstxjf_.rte.item.RTEItems;
import cn.jstxjf_.rte.render.ShadersModHandler;
import cn.jstxjf_.rte.render.integration.MtrModelRegistryUtil;
import cn.jstxjf_.rte.render.block.BlockEntityDirectNodeRenderer;
import cn.jstxjf_.rte.render.block.BlockEntityEyeCandyRenderer;
import cn.jstxjf_.rte.render.rail.RailRenderDispatcher;
import cn.jstxjf_.rte.render.train.BuiltinTrainRenderers;
import cn.jstxjf_.rte.render.train.SteamSmokeParticle;
import cn.jstxjf_.rte.scripting.ScriptContextManager;
import cn.jstxjf_.rte.scripting.ScriptHolderBase;
import cn.jstxjf_.rte.scripting.util.client.ScriptResourceUtilClient;
import cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers;
import cn.jstxjf_.rte.gfx.ContextCapability;
import cn.jstxjf_.rte.gfx.util.GlStateTracker;
import cn.jstxjf_.rte.gfx.util.DrawContext;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import cn.jstxjf_.rte.gfxext.reuse.ModelManager;
import cn.jstxjf_.rte.gfxext.reuse.AtlasManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mod.InitClient;
import org.mtr.mod.item.ItemBlockClickingBase;

import java.io.IOException;
import java.util.List;

public class RTEClient {

    public static final ModelManager modelManager = new ModelManager();
    public static final AtlasManager atlasManager = new AtlasManager();
    public static final DrawScheduler drawScheduler = new DrawScheduler();
    public static final DrawContext drawContext = new DrawContext();
    public static final RailRenderDispatcher railRenderDispatcher = new RailRenderDispatcher();
    private static boolean renderersRegistered;
    private static boolean itemPredicatesRegistered;
    private static boolean lifecycleEventsRegistered;
    private static boolean graphicsCapabilitiesLogged;
    private static boolean resourcesLoaded;
    private static boolean workbenchAutoloadPending;
    private static boolean jcmPromptChecked;
    private static boolean resourcesReloading;

    public static void init() {
        ShadersModHandler.init();
        registerRollNeighborResolver();
        if (!renderersRegistered) {
            InitClient.REGISTRY_CLIENT.registerParticleRenderer(RTEParticleTypes.STEAM_SMOKE, SteamSmokeParticle::createFactory);
            renderersRegistered = true;
        }
        if (!itemPredicatesRegistered) {
            final Identifier selectedPredicate = new Identifier("mtr", "selected");
            InitClient.REGISTRY_CLIENT.registerItemModelPredicate(RTEItems.BRIDGE_CREATOR_1, selectedPredicate,
                (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0);
            InitClient.REGISTRY_CLIENT.registerItemModelPredicate(RTEItems.BRIDGE_CREATOR_2, selectedPredicate,
                (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0);
            InitClient.REGISTRY_CLIENT.registerItemModelPredicate(RTEItems.BRIDGE_CREATOR_3, selectedPredicate,
                (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0);
            InitClient.REGISTRY_CLIENT.registerItemModelPredicate(RTEItems.COMPOUND_CREATOR, selectedPredicate,
                (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0);
            itemPredicatesRegistered = true;
        }
        if (!lifecycleEventsRegistered) {
            InitClient.REGISTRY_CLIENT.eventRegistryClient.registerClientJoin(RTEClient::clearTransientState);
            InitClient.REGISTRY_CLIENT.eventRegistryClient.registerClientDisconnect(RTEClient::clearTransientState);
            InitClient.REGISTRY_CLIENT.eventRegistryClient.registerResourceReloadEvent(() -> {
                final Minecraft client = getMinecraft();
                if (client != null) {
                    reloadResources(client.getResourceManager());
                }
            });
            lifecycleEventsRegistered = true;
        }
        ClientConfig.load();
    }

    public static void tick() {
        if (cn.jstxjf_.rte.render.RailChainHandles.isActive()
            && !cn.jstxjf_.rte.data.RailChainSelection.isManual()) {
            cn.jstxjf_.rte.render.RailChainHandles.exit();
        }
        if (!cn.jstxjf_.rte.gui.imgui.ImGuiWorkspace.isEmpty()
            && !(getMinecraft() != null && getMinecraft().screen instanceof cn.jstxjf_.rte.gui.imgui.ImGuiWorkspace.Panel)) {
            cn.jstxjf_.rte.gui.imgui.ImGuiWorkspace.closeAll();
        }
        if (!resourcesLoaded) {
            resourcesLoaded = true;
            final Minecraft client = getMinecraft();
            if (client != null) {
                try {
                    reloadResources(client.getResourceManager());
                } catch (Exception e) {
                    RTE.LOGGER.error("[RTE] Tick fallback resource load failed", e);
                }
            }
        }

        if (workbenchAutoloadPending) {
            workbenchAutoloadPending = false;
            cn.jstxjf_.rte.data.ResourceWorkbench.autoloadAll();
        }

        if (!jcmPromptChecked) {
            final Minecraft client = getMinecraft();
            if (client != null && client.player != null && client.screen == null) {
                jcmPromptChecked = true;
                cn.jstxjf_.rte.util.CompatArbiter.adoptLegacyVehicleChoice();
                if (cn.jstxjf_.rte.util.CompatArbiter.shouldPrompt()
                        && cn.jstxjf_.rte.gui.imgui.RteImGuiHost.isAvailable()) {
                    client.setScreen(new cn.jstxjf_.rte.gui.imgui.ImGuiCompatPromptScreen());
                }
            }
        }


        if (KeyMappings.RAIL_EDIT_KEY != null) {
            while (KeyMappings.RAIL_EDIT_KEY.consumeClick()) {
                final Minecraft client = getMinecraft();
                if (client == null || client.player == null || client.screen != null) {
                    continue;
                }
                if (cn.jstxjf_.rte.render.RailPicker.pickAlongLookRay()) {
                    final org.mtr.mapping.holder.Screen screen = cn.jstxjf_.rte.gui.BrushEditRailScreen.createScreen(
                        cn.jstxjf_.rte.render.RailPicker.pickedRail,
                        cn.jstxjf_.rte.render.RailPicker.pickedPosStart,
                        cn.jstxjf_.rte.render.RailPicker.pickedPosEnd,
                        null);
                    if (screen != null) {
                        client.setScreen(screen.data);
                    }
                } else {
                    client.player.displayClientMessage(
                        Component.translatable("gui.rte.rail_edit_key.no_rail"), true);
                }
            }
        }

        if (KeyMappings.DEBUG_KEY == null) {
            return;
        }

        if (KeyMappings.CONFIG_SCREEN != null) {
            while (KeyMappings.CONFIG_SCREEN.consumeClick()) {
                final Minecraft client = getMinecraft();
                if (client != null && client.screen == null) {
                    client.setScreen(cn.jstxjf_.rte.client.gui.RTEConfigScreen.createScreen(null));
                }
            }
        }

        rte$tickSelfTest();

        while (KeyMappings.DEBUG_KEY.consumeClick()) {
            final Minecraft client = getMinecraft();
            if (client == null || client.screen != null) {
                return;
            }
            reloadResourcesWithFeedback(client);
        }
    }


    private static long rte$nextSelfTestMillis = 0;
    private static int rte$selfTestRuns = 0;

    private static void rte$tickSelfTest() {
        if (!ClientConfig.enableRenderDiagnostics) {
            rte$nextSelfTestMillis = 0;
            rte$selfTestRuns = 0;
            return;
        }
        final Minecraft client = getMinecraft();
        if (client == null || client.player == null || client.level == null) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (rte$nextSelfTestMillis == 0) {
            rte$nextSelfTestMillis = now + 30000;
            return;
        }
        if (now < rte$nextSelfTestMillis || rte$selfTestRuns >= 20) {
            return;
        }
        rte$nextSelfTestMillis = now + 60000;
        rte$selfTestRuns++;
        cn.jstxjf_.test.RteSelfTest.logAll(cn.jstxjf_.test.RteSelfTest.runAll());
    }

    public static void reloadResourcesWithFeedback(Minecraft client) {
        final LocalPlayer player = client == null ? null : client.player;
        if (client == null) {
            return;
        }

        try {
            reloadResources(client.getResourceManager());
            if (player != null) {
                player.displayClientMessage(Component.literal("RTE client resources reloaded"), true);
            }
        } catch (Exception e) {
            RTE.LOGGER.error("Failed reloading RTE client resources", e);
            if (player != null) {
                player.displayClientMessage(Component.literal("RTE client resource reload failed: " + e.getClass().getSimpleName()), true);
            }
        }
    }

    public static void reloadResources(ResourceManager resourceManager) {
        if (resourcesReloading) {
            return;
        }
        resourcesReloading = true;
        try {
            resourcesLoaded = true;
            ContextCapability.checkContextVersion();
            if (!graphicsCapabilitiesLogged) {
                RTE.LOGGER.debug(
                    "RTE detected OpenGL {}.{}{}",
                    ContextCapability.contextVersion / 10,
                    ContextCapability.contextVersion % 10,
                    ContextCapability.isGL4ES ? " (GL4ES)" : ""
                );
                graphicsCapabilitiesLogged = true;
            }
            MtrModelRegistryUtil.resourceManager = resourceManager;
            MtrModelRegistryUtil.loadingErrorList.clear();
            ClientConfig.clearCustomResponders();
            ScriptContextManager.disposeAllContexts();
            ScriptHolderBase.resetRunner();
            cn.jstxjf_.rte.scripting.util.ScriptResourceUtil.clearFontCache();
            try {
                drawScheduler.reloadShaders(resourceManager);
            } catch (Exception e) {
                RTE.LOGGER.error("[RTE] Failed reloading draw shaders", e);
                MtrModelRegistryUtil.recordLoadingError("Failed reloading draw shaders", e);
            }
            atlasManager.clear();
            modelManager.clear();
            GlStateTracker.capture();
            try {
                BlockEntityDirectNodeRenderer.initGLModel(resourceManager);
                cn.jstxjf_.rte.data.RailModelRegistry.reload(resourceManager);
                railRenderDispatcher.clearRail();
                cn.jstxjf_.rte.data.EyeCandyRegistry.reload(resourceManager);
                ScriptResourceUtilClient.init(resourceManager);
                cn.jstxjf_.rte.data.ScriptedCustomTrains.init(resourceManager);
                ScriptedTrainRenderers.clear();
                BuiltinTrainRenderers.reload(resourceManager);
            } finally {
                GlStateTracker.restore();
            }
            workbenchAutoloadPending = true;

            final Minecraft client = getMinecraft();
            if (client != null && !MtrModelRegistryUtil.loadingErrorList.isEmpty()) {
                final List<String> errors = List.copyOf(MtrModelRegistryUtil.loadingErrorList);
                cn.jstxjf_.rte.gui.ErrorScreen.show(client, errors);
            }
        } finally {
            resourcesReloading = false;
        }
    }

    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    private static void registerRollNeighborResolver() {
        cn.jstxjf_.rte.data.RailExtraSupplier.ROLL_NEIGHBOR_RESOLVER.set((self, node) -> {
            try {
                final var railsAtPos = org.mtr.mod.client.MinecraftClientData.getInstance().positionsToRail.get(node);
                if (railsAtPos == null) return null;
                final cn.jstxjf_.rte.data.RailExtraSupplier selfExtra = (cn.jstxjf_.rte.data.RailExtraSupplier) self;
                final org.mtr.core.data.Position sp1 = selfExtra.getExtraPosition1();
                final org.mtr.core.data.Position sp2 = selfExtra.getExtraPosition2();
                Object found = null;
                org.mtr.core.data.Position fp1 = null;
                org.mtr.core.data.Position fp2 = null;
                for (final var entry : railsAtPos.entrySet()) {
                    final org.mtr.core.data.Rail r = entry.getValue();
                    if (!(((Object) r) instanceof cn.jstxjf_.rte.data.RailExtraSupplier rx)) continue;
                    final org.mtr.core.data.Position rp1 = rx.getExtraPosition1();
                    final org.mtr.core.data.Position rp2 = rx.getExtraPosition2();
                    if (rp1 == null || rp2 == null) continue;
                    final boolean sameAsSelf = (rp1.equals(sp1) && rp2.equals(sp2)) || (rp1.equals(sp2) && rp2.equals(sp1));
                    if (sameAsSelf) continue;
                    if (found == null) {
                        found = r;
                        fp1 = rp1;
                        fp2 = rp2;
                        continue;
                    }
                    final boolean samePhysical = (rp1.equals(fp1) && rp2.equals(fp2)) || (rp1.equals(fp2) && rp2.equals(fp1));
                    if (!samePhysical) {
                        return null;
                    }
                    final var foundMap = ((cn.jstxjf_.rte.data.RailExtraSupplier) found).getRollAngleMap();
                    final var candidateMap = rx.getRollAngleMap();
                    if ((foundMap == null || foundMap.isEmpty()) && candidateMap != null && !candidateMap.isEmpty()) {
                        found = r;
                        fp1 = rp1;
                        fp2 = rp2;
                    }
                }
                return found;
            } catch (Exception | NoClassDefFoundError e) {
                return null;
            }
        });
    }

    private static void clearTransientState() {
        ScriptContextManager.disposeAllContexts();
        railRenderDispatcher.clearRail();
        cn.jstxjf_.rte.render.DashboardMapCache.flushAndClear();
    }
}
