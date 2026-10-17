package cn.jstxjf_.rte.render.rail;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.item.RTEItems;
import cn.jstxjf_.rte.gfx.batch.BatchManager;
import cn.jstxjf_.rte.gfx.batch.ShaderProp;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.mixin.LevelRendererAccessor;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.Vertex;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.mod.data.RailType;
import org.mtr.mod.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.LightLayer;
import net.minecraft.client.renderer.culling.Frustum;
import org.mtr.mapping.holder.*;
import org.mtr.mod.client.MinecraftClientData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.mtr.core.data.Position;
import cn.jstxjf_.rte.data.RailModelProperties;

import java.util.*;

public class RailRenderDispatcher {

    private final HashMap<Rail, java.util.List<BakedRail>> railRefMap = new HashMap<>();
    private final HashMap<String, Rail> railByHexId = new HashMap<>();
    private final HashMap<String, HashMap<Long, RailChunkBase>> railChunkMap = new HashMap<>();
    private final List<RailChunkBase> railChunkList = new ArrayList<>();
    private final HashMap<Long, List<RailChunkBase>> railChunksById = new HashMap<>();
    private static final boolean REPLAY_MOD_LOADED = isClassPresent("com.replaymod.replay.ReplayModReplay");
    private boolean isInstanced;
    private static String rte$probeCacheGate() {
        try {
            final java.lang.reflect.Field field = Class.forName("org.mtr.mod.resource.CachedResource")
                .getDeclaredField("canFetchCache");
            field.setAccessible(true);
            return String.valueOf(field.getBoolean(null));
        } catch (Throwable e) {
            return "n/a";
        }
    }

    private static String rte$probeCachedCount() {
        try {
            final java.lang.reflect.Field field = Class.forName("org.mtr.mod.resource.CachedResource")
                .getDeclaredField("CACHED_RESOURCES");
            field.setAccessible(true);
            final Object list = field.get(null);
            return String.valueOf(((java.util.Collection<?>) list).size());
        } catch (Throwable e) {
            return "n/a";
        }
    }

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, RailRenderDispatcher.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private int rte$lastReportedLevel = -1;
    private boolean rte$lastReportedPreload;

    private final Set<Rail> currentFrameRails = new LinkedHashSet<>();
    private final java.util.concurrent.ConcurrentLinkedQueue<String> pendingEvictionHexIds = new java.util.concurrent.ConcurrentLinkedQueue<>();
    private boolean toolItemsResolved;
    private net.minecraft.world.item.Item brushItem;
    private net.minecraft.world.item.Item compoundCreatorItem;
    private net.minecraft.world.item.Item displacementToolItem;
    private net.minecraft.world.item.Item railPathEditorItem;

    public static boolean isHoldingRailItem = false;
    public static boolean isHoldingBrush = false;
    public static boolean isHoldingRailItemOrBrush = false;
    public static boolean isPreviewingModel = false;

    public void evictRailByHexId(String hexId) {
        pendingEvictionHexIds.add(hexId);
    }

    public static Rail resolveRenderCarrier(Rail rail) {
        if (rail == null) {
            return null;
        }
        try {
            final Rail clientRail = MinecraftClientData.getInstance().railIdMap.get(rail.getHexId());
            if (clientRail != null && ((Object) clientRail) instanceof RailExtraSupplier supplier
                    && RailExtraSupplier.usesBezierCurve(supplier.getPathMode())
                    && supplier.getBezierCurve() != null) {
                return clientRail;
            }
        } catch (Exception ignored) {
        }
        return rail;
    }

    private void processPendingEvictions() {
        String hexId;
        while ((hexId = pendingEvictionHexIds.poll()) != null) {
            rte$forceRailScan = true;
            final Rail rail = railByHexId.get(hexId);
            if (rail != null) {
                removeRail(rail);
            }
        }
    }

    private void addRail(Rail rail) {
        if (railRefMap.containsKey(rail)) return;
        String hexId = rail.getHexId();
        final Rail existing = railByHexId.get(hexId);
        if (existing != null && existing != rail) {
            removeRail(existing);
        }

        final java.util.List<BakedRail> baked = new java.util.ArrayList<>();
        for (final String modelKey : getModelKeysForRender(rail)) {
            baked.add(new BakedRail(rail, modelKey, true));
        }
        if (baked.isEmpty()) {
            return;
        }
        railRefMap.put(rail, baked);
        railByHexId.put(hexId, rail);

        for (final BakedRail bakedRail : baked) {
            HashMap<Long, RailChunkBase> chunkMap = railChunkMap.computeIfAbsent(bakedRail.modelKey, k -> new HashMap<>());
            for (long chunkId : bakedRail.coveredChunks.keySet()) {
                if (chunkMap.containsKey(chunkId)) {
                    chunkMap.get(chunkId).addRail(bakedRail);
                } else {
                    RailChunkBase newChunk;
                    if (isInstanced) {
                        newChunk = new InstancedRailChunk(chunkId, bakedRail.modelKey);
                    } else {
                        newChunk = new MeshBuildingRailChunk(chunkId, bakedRail.modelKey);
                    }
                    newChunk.addRail(bakedRail);
                    chunkMap.put(chunkId, newChunk);
                    railChunkList.add(newChunk);
                    railChunksById.computeIfAbsent(chunkId, k -> new ArrayList<>(2)).add(newChunk);
                }
            }
        }
    }

    private void removeRail(Rail rail) {
        final java.util.List<BakedRail> baked = railRefMap.remove(rail);
        if (baked == null) return;
        railByHexId.remove(rail.getHexId(), rail);
        for (final BakedRail bakedRail : baked) {
            HashMap<Long, RailChunkBase> chunkMap = railChunkMap.get(bakedRail.modelKey);
            if (chunkMap != null) {
                for (long chunkId : bakedRail.coveredChunks.keySet()) {
                    RailChunkBase chunk = chunkMap.get(chunkId);
                    if (chunk == null) continue;
                    chunk.removeRail(bakedRail);
                }
            }
            bakedRail.dispose();
        }
    }

    public boolean registerRail(Rail rail) {
        String modelKey = getModelKeyForRender(rail);
        RailModelProperties prop = modelKey.isEmpty() ? null : RailModelRegistry.getProperty(modelKey);
        if (modelKey.isEmpty() || prop == null || prop.rawModel == null) {
            return false;
        }
        currentFrameRails.add(rail);
        return true;
    }

    public void clearRail() {
        currentFrameRails.clear();
        rte$forceRailScan = true;
        for (java.util.List<BakedRail> bakedRails : railRefMap.values()) {
            for (BakedRail rail : bakedRails) {
                rail.dispose();
            }
        }
        railRefMap.clear();
        railByHexId.clear();
        for (HashMap<Long, RailChunkBase> chunkMap : railChunkMap.values()) {
            for (RailChunkBase chunk : chunkMap.values()) {
                chunk.close();
            }
            chunkMap.clear();
        }
        railChunkMap.clear();
        railChunkList.clear();
        railChunksById.clear();
        for (String key : RailModelRegistry.ELEMENTS.keySet()) {
            railChunkMap.put(key, new HashMap<>());
        }
    }

    public void registerLightUpdate(int x, int yMin, int yMax, int z) {
        long chunkId = BakedRail.chunkIdFromSectPos(x, z);
        final long now = System.currentTimeMillis();
        final List<RailChunkBase> chunks = railChunksById.get(chunkId);
        if (chunks == null) {
            return;
        }
        for (final RailChunkBase chunk : chunks) {
            if (chunk.containsYSection(yMin, yMax)) {
                if (now < chunk.nextLightDirtyAllowedMillis) continue;
                chunk.nextLightDirtyAllowedMillis = now + RailChunkBase.LIGHT_DIRTY_COOLDOWN_MILLIS;
                chunk.isDirty = true;
                chunk.lightRecheckFrames = 1;
            }
        }
    }

    public void prepareDraw() {
        if (Minecraft.getInstance().player == null) return;
        net.minecraft.world.entity.player.Player player = Minecraft.getInstance().player;

        net.minecraft.client.gui.screens.Screen currentScreen = Minecraft.getInstance().screen;
        isPreviewingModel = currentScreen != null && currentScreen.getClass().getSimpleName().equals("SelectListScreen");

        if (!isPreviewingModel) {
            resolveToolItems();

            net.minecraft.world.item.ItemStack mainHand = player.getMainHandItem();
            net.minecraft.world.item.ItemStack offHand = player.getOffhandItem();

            isHoldingBrush = isHoldingItem(mainHand, offHand, brushItem)
                || isHoldingItem(mainHand, offHand, compoundCreatorItem)
                || isHoldingItem(mainHand, offHand, displacementToolItem)
                || isHoldingItem(mainHand, offHand, railPathEditorItem);

            isHoldingRailItem = checkItemName(mainHand, "ItemRailModifier")
                             || checkItemName(offHand, "ItemRailModifier")
                             || checkItemName(mainHand, "ItemNodeModifier")
                             || checkItemName(offHand, "ItemNodeModifier");

        isHoldingRailItemOrBrush = isHoldingRailItem || isHoldingBrush;
        } else {
            isHoldingRailItem = false;
            isHoldingBrush = false;
            isHoldingRailItemOrBrush = false;
        }
    }

    private void resolveToolItems() {
        if (toolItemsResolved) {
            return;
        }
        toolItemsResolved = true;
        try { brushItem = Items.BRUSH.get().asItem().data; } catch (Exception ignored) {}
        try { compoundCreatorItem = RTEItems.compoundCreator(); } catch (Exception ignored) {}
        try { displacementToolItem = RTEItems.displacementTool(); } catch (Exception ignored) {}
        try { railPathEditorItem = RTEItems.railPathEditor(); } catch (Exception ignored) {}
    }
    
    private boolean checkItemName(net.minecraft.world.item.ItemStack stack, String simpleName) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.getItem().getClass().getSimpleName().contains(simpleName);
    }

    private boolean isHoldingItem(net.minecraft.world.item.ItemStack mainHand, net.minecraft.world.item.ItemStack offHand, net.minecraft.world.item.Item item) {
        return item != null && (mainHand.getItem() == item || offHand.getItem() == item);
    }

    public void drawRailNodes(net.minecraft.world.level.Level level, DrawScheduler drawScheduler, Matrix4f viewMatrix) {
        if (!isHoldingRailItemOrBrush || RailModelRegistry.railNodeModel == null) {
            return;
        }
        final var rails = MinecraftClientData.getInstance().positionsToRail;
        if (rails == null) {
            return;
        }
        final HashSet<Position> drawnNodes = new HashSet<>();
        for (final var entryStart : rails.entrySet()) {
            for (final var entryEnd : entryStart.getValue().entrySet()) {
                final Rail rail = entryEnd.getValue();
                if (drawnNodes.add(entryStart.getKey())) {
                    enqueueRailNode(level, drawScheduler, viewMatrix, rail, entryStart.getKey());
                }
                if (drawnNodes.add(entryEnd.getKey())) {
                    enqueueRailNode(level, drawScheduler, viewMatrix, rail, entryEnd.getKey());
                }
            }
        }
    }

    private void enqueueRailNode(net.minecraft.world.level.Level level, DrawScheduler drawScheduler, Matrix4f viewMatrix, Rail rail, Position node) {
        final boolean isEnd = isNodeAtRailEnd(rail, node);
        double x = node.getX() + 0.5;
        double y = node.getY();
        double z = node.getZ() + 0.5;
        double dx = 0;
        double dy = 0;
        double dz = 1;
        try {
            if ((Object) rail instanceof RailExtraSupplier extra) {
                final double length = extra.getLength();
                if (Double.isFinite(length) && length > 0.001) {
                    final double epsilon = Math.max(0.001, Math.min(0.5, length * 0.01));
                    final Vec3 endpoint = extra.getPosition(isEnd ? length : 0);
                    final Vec3 inside = extra.getPosition(isEnd ? Math.max(0, length - epsilon) : Math.min(length, epsilon));
                    x = endpoint.x;
                    y = endpoint.y;
                    z = endpoint.z;
                    final Vec3 direction = isEnd ? endpoint.subtract(inside) : inside.subtract(endpoint);
                    dx = direction.x;
                    dy = direction.y;
                    dz = direction.z;
                }
            }
        } catch (Exception ignored) {
        }
        final double horizontal = Math.hypot(dx, dz);
        final float yaw = horizontal < 1E-9 ? 0 : (float) net.minecraft.util.Mth.atan2(dx, dz);
        final float pitch = (float) net.minecraft.util.Mth.atan2(dy, horizontal);
        final Matrix4f nodePose = viewMatrix.copy();
        nodePose.translate((float) x, (float) y, (float) z);
        nodePose.rotateY(yaw);
        nodePose.rotateX(-pitch);
        final net.minecraft.core.BlockPos lightPos = net.minecraft.core.BlockPos.containing(x, y, z);
        final int light = net.minecraft.client.renderer.LightTexture.pack(level.getBrightness(LightLayer.BLOCK, lightPos), level.getBrightness(LightLayer.SKY, lightPos));
        drawScheduler.enqueue(RailModelRegistry.railNodeModel, nodePose, light);
    }

    private static boolean isNodeAtRailEnd(Rail rail, Position pos) {
        if (((Object) rail) instanceof RailExtraSupplier extra) {
            final Position p1 = extra.getExtraPosition1();
            if (p1 != null) {
                return p1.getX() != pos.getX() || p1.getY() != pos.getY() || p1.getZ() != pos.getZ();
            }
        }
        return false;
    }

    public void drawRails(World level, DrawScheduler drawScheduler, Matrix4f viewMatrix) {
        processPendingEvictions();
        BatchManager batchManager = drawScheduler.batchManager;
        final int railRenderLevel = ClientConfig.getRailRenderLevel();
        boolean shouldBeInstanced = railRenderLevel == 3;
        if (isInstanced != shouldBeInstanced) clearRail();
        isInstanced = shouldBeInstanced;
        if (rte$lastReportedLevel != railRenderLevel
                || rte$lastReportedPreload != ClientConfig.enableBbModelPreload) {
            rte$lastReportedLevel = railRenderLevel;
            rte$lastReportedPreload = ClientConfig.enableBbModelPreload;
            RTE.LOGGER.info("[RailRender] level={} instanced={} deform={} rail3D={} preload={} cacheGate={} cachedCount={} camMatrix=({}, {}, {})",
                rte$lastReportedLevel, shouldBeInstanced, ClientConfig.getEnableRailDeform(),
                ClientConfig.enableRail3D, rte$lastReportedPreload,
                rte$probeCacheGate(), rte$probeCachedCount(),
                String.format("%.1f", viewMatrix.getTranslationPart().x()),
                String.format("%.1f", viewMatrix.getTranslationPart().y()),
                String.format("%.1f", viewMatrix.getTranslationPart().z()));
        }

        final Vec3 cameraPosEarly = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        if (rte$shouldScanRails(cameraPosEarly)) {
            rte$scanRails(drawScheduler, cameraPosEarly, (Minecraft.getInstance().options.renderDistance().get() + 3) * 16);
        } else {
            currentFrameRails.clear();
        }

        Vec3 cameraBlockPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final net.minecraft.world.phys.Vec3 cameraVector = new net.minecraft.world.phys.Vec3(cameraBlockPos.x, cameraBlockPos.y, cameraBlockPos.z);
        for (final RailChunkBase chunk : railChunkList) {
            chunk.getCameraDistManhattanXZ(cameraVector);
        }
        railChunkList.sort(Comparator.comparingDouble(chunk -> chunk.cameraDistManhattanXZ));

        Frustum cullingFrustum = null;
        try {
            cullingFrustum = ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).getCullingFrustum();
        } catch (Exception e) { RTE.LOGGER.debug("Frustum not available", e); }
        ShaderProp shaderProp = new ShaderProp().setViewMatrix(viewMatrix);

        int renderDist = Minecraft.getInstance().options.renderDistance().get();
        int maxRailDistance = REPLAY_MOD_LOADED ? 64 * 16 : (renderDist + 3) * 16;
        boolean isOutsideRenderDistance = false;

        RailChunkBase.upload();

        for (Iterator<RailChunkBase> it = railChunkList.iterator(); it.hasNext(); ) {
            RailChunkBase chunk = it.next();
            if (chunk.containingRails.isEmpty() && chunk.containingRailsWriting.isEmpty()) {
                chunk.close();
                it.remove();
                railChunkMap.get(chunk.modelKey).remove(chunk.chunkId);
                final List<RailChunkBase> sameId = railChunksById.get(chunk.chunkId);
                if (sameId != null) {
                    sameId.remove(chunk);
                    if (sameId.isEmpty()) {
                        railChunksById.remove(chunk.chunkId);
                    }
                }
                continue;
            }
            if (isOutsideRenderDistance) continue;
            if (chunk.cameraDistManhattanXZ > maxRailDistance) {
                isOutsideRenderDistance = true;
                continue;
            }
            if (!chunk.bufferBuilding && (chunk.isDirty || !chunk.bufferBuilt)) {
                final long now = System.currentTimeMillis();
                if (!chunk.bufferBuilt || now >= chunk.nextRebuildAllowedMillis) {
                    chunk.nextRebuildAllowedMillis = now + RailChunkBase.REBUILD_COOLDOWN_MILLIS;
                    chunk.rebuildBuffer(level);
                }
            }
            if (chunk.bufferBuilt && (cullingFrustum == null || cullingFrustum.isVisible(chunk.boundingBox))) {
                chunk.enqueue(batchManager, shaderProp);
            }
        }
    }

    private static final long RAIL_SCAN_INTERVAL_MILLIS = 250;
    private static final double RAIL_SCAN_MOVE_THRESHOLD = 8;
    private long rte$nextRailScanMillis;
    private double rte$lastScanX = Double.NaN;
    private double rte$lastScanZ = Double.NaN;
    private boolean rte$pendingRailAdds;
    private boolean rte$forceRailScan = true;

    private boolean rte$shouldScanRails(Vec3 cameraPos) {
        final long now = System.currentTimeMillis();
        if (!rte$forceRailScan && !rte$pendingRailAdds && now < rte$nextRailScanMillis
                && Math.abs(cameraPos.x - rte$lastScanX) + Math.abs(cameraPos.z - rte$lastScanZ) <= RAIL_SCAN_MOVE_THRESHOLD) {
            return false;
        }
        rte$forceRailScan = false;
        rte$nextRailScanMillis = now + RAIL_SCAN_INTERVAL_MILLIS;
        rte$lastScanX = cameraPos.x;
        rte$lastScanZ = cameraPos.z;
        return true;
    }

    private void rte$scanRails(DrawScheduler drawScheduler, Vec3 cameraPosEarly, int maxRailDistanceEarly) {
        if (drawScheduler.shaderManager != null && drawScheduler.shaderManager.isReady()) {
            try {
                HashSet<String> seenThisFrame = new HashSet<>();
                var positionsToRail = MinecraftClientData.getInstance().positionsToRail;
                for (var entryStart : positionsToRail.entrySet()) {
                    Position nodePos = entryStart.getKey();
                    double dist = Math.abs(cameraPosEarly.x - nodePos.getX()) + Math.abs(cameraPosEarly.z - nodePos.getZ());
                    if (dist > maxRailDistanceEarly) continue;
                    for (var entryEnd : entryStart.getValue().entrySet()) {
                        Rail candidate = resolveRenderCarrier(entryEnd.getValue());
                        String hexId = candidate.getHexId();
                        if (!seenThisFrame.add(hexId)) continue;
                        Rail existing = railByHexId.get(hexId);
                        if (existing != null) {
                            java.util.List<BakedRail> bakedList = railRefMap.get(existing);
                            BakedRail baked = bakedList == null || bakedList.isEmpty() ? null : bakedList.get(0);
                            if (baked != null && baked.geometryFingerprint != BakedRail.computeFingerprint(candidate)) {
                                removeRail(existing);
                                registerRail(candidate);
                            } else {
                                registerRail(existing);
                            }
                        } else {
                            registerRail(candidate);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        Set<Rail> railsToAdd = new HashSet<>(currentFrameRails);
        railsToAdd.removeAll(railRefMap.keySet());
        int addedThisFrame = 0;
        for (Rail rail : railsToAdd) {
            if (addedThisFrame >= 64) break;
            addRail(rail);
            addedThisFrame++;
        }
        rte$pendingRailAdds = railsToAdd.size() > addedThisFrame;
        Set<Rail> railsToRemove = new HashSet<>(railRefMap.keySet());
        railsToRemove.removeAll(currentFrameRails);
        for (Rail rail : railsToRemove) removeRail(rail);
        currentFrameRails.clear();
    }

    public static java.util.List<String> getModelKeysForRender(Rail rail) {
        final java.util.List<String> declared = ((RailExtraSupplier) (Object) rail).getModelKeys();
        if (declared.size() <= 1) {
            final String single = getModelKeyForRender(rail);
            return single.isEmpty() ? java.util.Collections.emptyList() : java.util.List.of(single);
        }
        final java.util.List<String> resolved = new java.util.ArrayList<>();
        for (final String key : declared) {
            if (key == null || key.isEmpty() || key.equals("null")) {
                continue;
            }
            if (RailModelRegistry.ELEMENTS.containsKey(key) && !resolved.contains(key)) {
                resolved.add(key);
            }
        }
        if (resolved.isEmpty()) {
            final String single = getModelKeyForRender(rail);
            return single.isEmpty() ? java.util.Collections.emptyList() : java.util.List.of(single);
        }
        return resolved;
    }

    public static String getModelKeyForRender(Rail rail) {
        String customModelKey = ((RailExtraSupplier)(Object)rail).getModelKey();
        if (customModelKey == null) {
            customModelKey = "";
        }

        if (customModelKey == null || customModelKey.isEmpty() || !RailModelRegistry.ELEMENTS.containsKey(customModelKey)) {
            if (rail.getTransportMode() == TransportMode.TRAIN) {
                if (rail.isSiding()) {
                    return RailModelRegistry.ELEMENTS.containsKey("rte_builtin_depot") ? "rte_builtin_depot" : "rte_builtin_concrete_sleeper";
                } else {
                return "rte_builtin_concrete_sleeper";
                }
            } else {
                return "";
            }
        } else {
            if (customModelKey.equals("null")) {
                return isHoldingRailItemOrBrush ? "" : "null";
            } else {
                return customModelKey;
            }
        }
    }

    public void drawBoundingBoxes(PoseStack matrixStack, VertexConsumer buffer) {
        for (RailChunkBase chunk : railChunkList) {
            boolean isChunkEven = chunk.isEven();
            LevelRenderer.renderLineBox(matrixStack, buffer, chunk.boundingBox,
                    1.0f, isChunkEven ? 1.0f : 0.0f, isChunkEven ? 0.0f : 1.0f, 1.0f);
        }
    }

}
