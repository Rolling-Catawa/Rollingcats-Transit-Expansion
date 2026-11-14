package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.mixin.ObjModelAccessor;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.mapper.ResourceManagerHelper;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mod.resource.ModelResourceLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncVehicleModelLoader {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2, runnable -> {
        final Thread thread = new Thread(runnable, "RTE Vehicle Model Loader");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });
    private static final Map<Object, Pending> PENDING = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final long UNCLAIMED_LIFETIME_MS = 120_000;
    private static final long SWEEP_INTERVAL_MS = 5_000;
    private static final long SHARED_IDLE_MS = 300_000;
    private static final Map<String, SharedParse> SHARED = new java.util.concurrent.ConcurrentHashMap<>();
    private static final int MAX_IN_FLIGHT = 3;
    private static final java.util.concurrent.atomic.AtomicInteger IN_FLIGHT = new java.util.concurrent.atomic.AtomicInteger();
    private static long nextSweepMillis;

    private AsyncVehicleModelLoader() {
    }

    public static boolean isReady(Object owner, String modelResource, Identifier textureId, boolean flipTextureV) {
        if (!ClientConfig.asyncVehicleModelLoading || !isOffloadable(modelResource)) {
            return true;
        }
        sweepUnclaimed();
        final long now = System.currentTimeMillis();
        Pending pending = PENDING.get(owner);
        if (pending == null) {
            final String key = modelResource + "|" + (textureId == null ? "" : String.valueOf(textureId.data)) + "|" + flipTextureV;
            SharedParse shared = SHARED.get(key);
            if (shared == null) {
                if (IN_FLIGHT.get() >= MAX_IN_FLIGHT) {
                    return false;
                }
                IN_FLIGHT.incrementAndGet();
                shared = new SharedParse(key, CompletableFuture.supplyAsync(
                    () -> ModelResourceLoader.loadModel(modelResource, textureId, flipTextureV, AsyncVehicleModelLoader::readResource),
                    EXECUTOR).whenComplete((result, error) -> IN_FLIGHT.decrementAndGet()), System.nanoTime());
                SHARED.put(key, shared);
            }
            shared.lastUsedMillis = now;
            pending = new Pending(shared, now);
            PENDING.put(owner, pending);
        }
        final SharedParse shared = pending.shared;
        shared.lastUsedMillis = now;
        if (!shared.models.isDone()) {
            AsyncLoadGate.markPending();
            return false;
        }
        if (shared.models.isCompletedExceptionally()) {
            PENDING.remove(owner);
            SHARED.remove(shared.key, shared);
            return true;
        }
        if (shared.textures == null) {
            shared.textures = preloadTextures(shared.models.join());
            RTE.LOGGER.info("[AsyncVehicleModel] parsed {} in {} ms, preloading textures",
                modelResource, (System.nanoTime() - shared.started) / 1000000L);
        }
        if (!shared.textures.isDone()) {
            AsyncLoadGate.markPending();
            return false;
        }
        return true;
    }

    public static Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel> take(Object owner) {
        final Pending pending = PENDING.remove(owner);
        if (pending == null || !pending.shared.models.isDone() || pending.shared.models.isCompletedExceptionally()) {
            return null;
        }
        return cloneModels(pending.shared.models.join());
    }

    private static Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel> cloneModels(Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel> parsed) {
        final Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel> copies = new Object2ObjectAVLTreeMap<>();
        for (final Map.Entry<String, OptimizedModel.ObjModel> entry : parsed.entrySet()) {
            final OptimizedModel.ObjModel model = entry.getValue();
            copies.put(entry.getKey(), ObjModelAccessor.rte$create(((ObjModelAccessor) (Object) model).rte$getRawMeshes(), false,
                model.getMinX(), model.getMinY(), model.getMinZ(), model.getMaxX(), model.getMaxY(), model.getMaxZ()));
        }
        return copies;
    }

    public static void clear() {
        PENDING.clear();
        SHARED.clear();
    }

    private static void sweepUnclaimed() {
        final long now = System.currentTimeMillis();
        if (now < nextSweepMillis) {
            return;
        }
        nextSweepMillis = now + SWEEP_INTERVAL_MS;
        synchronized (PENDING) {
            PENDING.values().removeIf(pending -> pending.shared.models.isDone()
                && now - pending.startedMillis > UNCLAIMED_LIFETIME_MS);
        }
        SHARED.values().removeIf(shared -> shared.models.isDone() && now - shared.lastUsedMillis > SHARED_IDLE_MS);
    }

    private static boolean isOffloadable(String modelResource) {
        if (modelResource == null) {
            return false;
        }
        final String lower = modelResource.toLowerCase(Locale.ROOT);
        return lower.endsWith(".obj") || lower.endsWith(".mqo") || lower.endsWith(".mqoz");
    }

    private static String readResource(Identifier identifier) {
        return ResourceManagerHelper.readResource(identifier);
    }

    private static CompletableFuture<Void> preloadTextures(Map<String, OptimizedModel.ObjModel> models) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Set<ResourceLocation> textures = new HashSet<>();
        for (final OptimizedModel.ObjModel model : models.values()) {
            for (final RawMesh mesh : ((ObjModelAccessor) (Object) model).rte$getRawMeshes()) {
                final Identifier texture = mesh.materialProperties.getTexture();
                if (texture != null && texture.data != null) {
                    textures.add((ResourceLocation) (Object) texture.data);
                }
            }
        }
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (final ResourceLocation texture : textures) {
            futures.add(minecraft.getTextureManager().preload(texture, Util.backgroundExecutor()));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private static final class SharedParse {
        private final String key;
        private final CompletableFuture<Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel>> models;
        private final long started;
        private volatile long lastUsedMillis;
        private CompletableFuture<Void> textures;

        private SharedParse(String key, CompletableFuture<Object2ObjectAVLTreeMap<String, OptimizedModel.ObjModel>> models, long started) {
            this.key = key;
            this.models = models;
            this.started = started;
        }
    }

    private static final class Pending {
        private final SharedParse shared;
        private final long startedMillis;

        private Pending(SharedParse shared, long startedMillis) {
            this.shared = shared;
            this.startedMillis = startedMillis;
        }
    }
}
