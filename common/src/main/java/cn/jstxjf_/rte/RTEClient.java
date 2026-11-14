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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
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
    private static boolean wasConnected;
    private static boolean graphicsCapabilitiesLogged;
    private static boolean resourcesLoaded;
    private static boolean workbenchAutoloadPending;
    private static boolean jcmPromptChecked;
    private static boolean resourcesReloading;
    private static boolean welcomePending;
    private static boolean wasInWorld;
    private static int welcomeDelayTicks;
    private static String lastResourcePackSignature = "";
    private static long lastResourceReloadCompletedNanos;

    public static void init() {
        ShadersModHandler.init();
        registerRollNeighborResolver();
        if (!renderersRegistered) {
            InitClient.REGISTRY_CLIENT.registerParticleRenderer(RTEParticleTypes.STEAM_SMOKE, SteamSmokeParticle::createFactory);
            renderersRegistered = true;
        }
        if (!itemPredicatesRegistered) {
            final Identifier selectedPredicate = new Identifier("mtr", "selected");
            InitClient.REGISTRY_CLIENT.registerItemModelPredicate(RTEItems.COMPOUND_CREATOR, selectedPredicate,
                //#if MC>=12101
                (itemStack, clientWorld, livingEntity) -> itemStack.data.has(org.mtr.registry.DataComponentTypes.START_POS.get()) ? 1 : 0);
                //#else
                (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0);
                //#endif
            itemPredicatesRegistered = true;
        }
        ClientConfig.load();
    }

    public static void tick() {
        tickConnectionState();
        cn.jstxjf_.rte.scripting.util.client.GraphicsTexture.flushPendingUploads();
        tickWelcomeMessage();
        cn.jstxjf_.rte.render.FreeNodeEditor.tick();
        cn.jstxjf_.rte.render.train.VehicleWarmup.tick();
        if (cn.jstxjf_.rte.render.RailChainHandles.isActive()
            && !cn.jstxjf_.rte.data.RailChainSelection.isManual()) {
            cn.jstxjf_.rte.render.RailChainHandles.exit();
        }
        if (!cn.jstxjf_.rte.gui.imgui.ImGuiWorkspace.isEmpty()
            && !(getMinecraft() != null && getMinecraft().screen instanceof cn.jstxjf_.rte.gui.imgui.ImGuiWorkspace.Panel)) {
            cn.jstxjf_.rte.gui.imgui.ImGuiWorkspace.closeAll();
        }
        final Minecraft fallbackClient = getMinecraft();
        if (!resourcesLoaded && fallbackClient != null && fallbackClient.level != null) {
            resourcesLoaded = true;
            try {
                reloadResources(fallbackClient.getResourceManager());
            } catch (Exception e) {
                RTE.LOGGER.error("[RTE] Tick fallback resource load failed", e);
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
            reloadResources(client.getResourceManager(), true);
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
        reloadResources(resourceManager, false);
    }

    public static void reloadResourcesAfterMtr(ResourceManager resourceManager) {
        reloadResources(resourceManager, true);
    }

    private static void reloadResources(ResourceManager resourceManager, boolean force) {
        if (resourcesReloading) {
            return;
        }
        final String packSignature = resourceManager.listPacks()
            .map(cn.jstxjf_.rte.compat.PackCompat::packId)
            .collect(java.util.stream.Collectors.joining("\u001f"));
        final long now = System.nanoTime();
        if (!force && packSignature.equals(lastResourcePackSignature)
                && now - lastResourceReloadCompletedNanos < java.util.concurrent.TimeUnit.SECONDS.toNanos(5)) {
            resourcesLoaded = true;
            return;
        }
        resourcesReloading = true;
        RTE.LOGGER.info("[RTE] Reloading client resources ({} packs)", resourceManager.listPacks().count());
        cn.jstxjf_.rte.util.JcmCompat.reset();
        boolean completed = false;
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
            cn.jstxjf_.rte.scripting.util.client.GraphicsTexture.disposeAll();
            cn.jstxjf_.rte.scripting.util.ScriptResourceUtil.clearFontCache();
            try {
                drawScheduler.reloadShaders(resourceManager);
            } catch (Exception e) {
                RTE.LOGGER.error("[RTE] Failed reloading draw shaders", e);
                MtrModelRegistryUtil.recordLoadingError("Failed reloading draw shaders", e);
            }
            atlasManager.clear();
            modelManager.clear();
            cn.jstxjf_.rte.gfxext.reuse.ModelPrefetcher.prefetch(resourceManager, modelManager, atlasManager);
            GlStateTracker.capture();
            try {
                BlockEntityDirectNodeRenderer.initGLModel(resourceManager);
                long phase = System.nanoTime();
                cn.jstxjf_.rte.data.RailModelRegistry.reload(resourceManager);
                railRenderDispatcher.clearRail();
                final long rails = System.nanoTime() - phase;
                phase = System.nanoTime();
                cn.jstxjf_.rte.data.EyeCandyRegistry.reload(resourceManager);
                final long eyeCandies = System.nanoTime() - phase;
                phase = System.nanoTime();
                ScriptResourceUtilClient.init(resourceManager);
                final long scriptResources = System.nanoTime() - phase;
                phase = System.nanoTime();
                cn.jstxjf_.rte.data.ScriptedCustomTrains.init(resourceManager);
                ScriptedTrainRenderers.clear();
                final long trainScripts = System.nanoTime() - phase;
                phase = System.nanoTime();
                BuiltinTrainRenderers.reload(resourceManager);
                cn.jstxjf_.rte.data.VehicleLightRegistry.reload(resourceManager);
                cn.jstxjf_.rte.render.train.VehicleLightRenderer.reload();
                RTE.LOGGER.info("[RTE] Reload phases: rails={} ms eyeCandies={} ms scriptResources={} ms trainScripts={} ms builtinTrains={} ms",
                    rails / 1_000_000L, eyeCandies / 1_000_000L, scriptResources / 1_000_000L,
                    trainScripts / 1_000_000L, (System.nanoTime() - phase) / 1_000_000L);
                final int releasedRawModels = modelManager.releaseLoadedRawModels();
                RTE.LOGGER.info("[RTE] Released {} cached raw models after reload", releasedRawModels);
            } finally {
                GlStateTracker.restore();
            }
            workbenchAutoloadPending = true;

            final Minecraft client = getMinecraft();
            if (client != null && !MtrModelRegistryUtil.loadingErrorList.isEmpty()) {
                final List<String> errors = List.copyOf(MtrModelRegistryUtil.loadingErrorList);
                cn.jstxjf_.rte.gui.ErrorScreen.show(client, errors);
            }
            completed = true;
        } finally {
            if (completed) {
                lastResourcePackSignature = packSignature;
                lastResourceReloadCompletedNanos = System.nanoTime();
            }
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
        cn.jstxjf_.rte.util.JcmCompat.reset();
        ScriptContextManager.disposeAllContexts();
        railRenderDispatcher.clearRail();
        cn.jstxjf_.rte.render.DashboardMapCache.flushAndClear();
    }

    private static void tickConnectionState() {
        final Minecraft client = getMinecraft();
        final boolean connected = client != null && client.getConnection() != null && client.level != null;
        if (connected == wasConnected) {
            return;
        }
        wasConnected = connected;
        if (connected) {
            onClientJoin();
        } else {
            onClientDisconnect();
        }
    }

    private static void onClientJoin() {
        clearTransientState();
        welcomePending = true;
        welcomeDelayTicks = 20;
    }

    private static void onClientDisconnect() {
        clearTransientState();
        welcomePending = false;
        welcomeDelayTicks = 0;
        wasInWorld = false;
    }

    private static void tickWelcomeMessage() {
        final Minecraft client = getMinecraft();
        final boolean inWorld = client != null && client.player != null && client.level != null;
        if (!inWorld) {
            wasInWorld = false;
            return;
        }
        if (!wasInWorld) {
            wasInWorld = true;
            welcomePending = true;
            welcomeDelayTicks = 20;
        }
        if (!welcomePending || welcomeDelayTicks-- > 0) {
            return;
        }
        welcomePending = false;
        client.player.displayClientMessage(
            Component.literal("\u6b22\u8fce\u4f7f\u7528 Rollingcats Transit Expansion\uff01\u8be5\u6a21\u7ec4\u8fd8\u5904\u4e8e beta \u6d4b\u8bd5\u9636\u6bb5\uff0c\u5982\u6709\u4efb\u4f55 bug \u8bf7 ")
                .append(Component.literal("[\u70b9\u6b64]").withStyle(style -> style
                    .withColor(ChatFormatting.AQUA)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "https://github.com/Rolling-Catawa/Rollingcats-Transit-Expansion/issues"))))
                .append(Component.literal(" \u4e0a\u62a5\u3002\u6784\u5efa\u53f7\uff1a" + BuildStamp.buildId() + "\u3002")),
            false
        );
    }
}
