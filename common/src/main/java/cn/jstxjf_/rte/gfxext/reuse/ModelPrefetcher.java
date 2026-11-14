package cn.jstxjf_.rte.gfxext.reuse;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.loader.CsvModelLoader;
import cn.jstxjf_.rte.gfxext.model.loader.ObjModelLoader;
import cn.jstxjf_.rte.render.integration.MtrModelRegistryUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import cn.jstxjf_.rte.compat.McCompat;

public final class ModelPrefetcher {

    private static final String[] MODEL_DIRECTORIES = {"eyecandies", "rails"};

    private ModelPrefetcher() {
    }

    public static void prefetch(ResourceManager resourceManager, ModelManager modelManager, AtlasManager atlasManager) {
        if (resourceManager == null) {
            return;
        }
        final long started = System.nanoTime();
        final Set<ResourceLocation> models = new LinkedHashSet<>();
        final Set<ResourceLocation> atlases = new LinkedHashSet<>();
        for (final String namespace : resourceManager.getNamespaces().stream().sorted().toList()) {
            for (final String directory : MODEL_DIRECTORIES) {
                for (final Pair<ResourceLocation, Resource> pair : MtrModelRegistryUtil.listResources(resourceManager, namespace, directory, ".json")) {
                    collect(pair.getSecond(), models, atlases);
                }
            }
        }
        for (final ResourceLocation atlas : atlases) {
            try {
                atlasManager.load(resourceManager, atlas);
            } catch (Exception ignored) {
            }
        }
        final List<ResourceLocation> pending = new ArrayList<>();
        for (final ResourceLocation model : models) {
            final String extension = FilenameUtils.getExtension(model.getPath()).toLowerCase(Locale.ROOT);
            if ((extension.equals("obj") || extension.equals("csv")) && !modelManager.loadedRawModels.containsKey(model)) {
                pending.add(model);
            }
        }
        if (pending.isEmpty()) {
            return;
        }
        final int threads = Math.max(2, Math.min(pending.size(), Runtime.getRuntime().availableProcessors() - 1));
        final ExecutorService executor = Executors.newFixedThreadPool(threads, runnable -> {
            final Thread thread = new Thread(runnable, "RTE Model Prefetch");
            thread.setDaemon(true);
            return thread;
        });
        final Map<ResourceLocation, RawModel> parsed = new ConcurrentHashMap<>();
        try {
            final List<Future<?>> futures = new ArrayList<>(pending.size());
            for (final ResourceLocation model : pending) {
                futures.add(executor.submit(() -> {
                    try {
                        final RawModel result = model.getPath().toLowerCase(Locale.ROOT).endsWith(".csv")
                            ? CsvModelLoader.loadModel(resourceManager, model, atlasManager)
                            : ObjModelLoader.loadModel(resourceManager, model, atlasManager);
                        if (result != null) {
                            parsed.put(model, result);
                        }
                    } catch (Throwable ignored) {
                    }
                }));
            }
            for (final Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception ignored) {
                }
            }
        } finally {
            executor.shutdownNow();
        }
        modelManager.loadedRawModels.putAll(parsed);
        RTE.LOGGER.info("[ModelPrefetch] parsed {}/{} models on {} threads in {} ms",
            parsed.size(), pending.size(), threads, (System.nanoTime() - started) / 1_000_000L);
    }

    private static void collect(Resource resource, Set<ResourceLocation> models, Set<ResourceLocation> atlases) {
        try (InputStream inputStream = resource.open()) {
            final JsonElement root = JsonParser.parseString(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                return;
            }
            final JsonObject rootObject = root.getAsJsonObject();
            if (rootObject.has("model") || rootObject.has("scriptFiles")) {
                collectEntry(rootObject, models, atlases);
                return;
            }
            for (final Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    collectEntry(entry.getValue().getAsJsonObject(), models, atlases);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void collectEntry(JsonObject entry, Set<ResourceLocation> models, Set<ResourceLocation> atlases) {
        addLocation(entry, "model", models);
        addLocation(entry, "atlasIndex", atlases);
    }

    private static void addLocation(JsonObject entry, String key, Set<ResourceLocation> target) {
        if (!entry.has(key) || !entry.get(key).isJsonPrimitive()) {
            return;
        }
        try {
            target.add(McCompat.id(entry.get(key).getAsString()));
        } catch (Exception ignored) {
        }
    }
}
