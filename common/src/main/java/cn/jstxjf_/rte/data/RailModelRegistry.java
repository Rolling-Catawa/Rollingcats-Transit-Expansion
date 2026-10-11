package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.render.integration.MtrModelRegistryUtil;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import cn.jstxjf_.rte.gfxext.util.ResourceUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import cn.jstxjf_.rte.scripting.ScriptHolderBase;
import cn.jstxjf_.rte.scripting.ScriptHolderClient;
import com.google.gson.JsonArray;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RailModelRegistry {

    public static Map<String, RailModelProperties> ELEMENTS = new HashMap<>();
    public static final Map<String, RailModelProperties> PATH_MAP = new HashMap<>();
    public static Tree.Root<RailModelProperties> TREE = new Tree.Root<>("rail.rte.type");

    public static void register(String key, RailModelProperties properties) {
        if (properties == null) return;
        ELEMENTS.put(key, properties);
        PATH_MAP.put(properties.path, properties);
    }

    public static void reload(ResourceManager resourceManager) {
        ELEMENTS.clear();
        PATH_MAP.clear();
        TREE = new Tree.Root<>("rail.rte.type");

        register("", new RailModelProperties("", (MutableComponent) Component.translatable("rail.rte.default"), null, 1f, 0f, null, "group.rte.builtin"));
        register("null", new RailModelProperties("null", (MutableComponent) Component.translatable("rail.rte.hidden"), null, Float.MAX_VALUE, 0f, null, "group.rte.builtin"));
        register("rte_builtin_concrete_sleeper", new RailModelProperties("rte_builtin_concrete_sleeper", (MutableComponent) Component.literal("Concrete Sleeper"), null, 1f, 0f, null, "group.rte.builtin"));
        register("rte_builtin_depot", new RailModelProperties("rte_builtin_depot", (MutableComponent) Component.literal("Depot"), null, 1f, 0f, null, "group.rte.builtin"));

        if (resourceManager != null) {
            for (String namespace : resourceManager.getNamespaces().stream().sorted().toList()) {
                List<com.mojang.datafixers.util.Pair<ResourceLocation, Resource>> resourceList = MtrModelRegistryUtil.listResources(resourceManager, namespace, "rails", ".json");
                for (com.mojang.datafixers.util.Pair<ResourceLocation, Resource> pair : resourceList) {
                    ResourceLocation entryKey = pair.getFirst();
                    Resource entryValue = pair.getSecond();
                    try {
                        try (InputStream is = entryValue.open()) {
                            JsonObject rootObj = JsonParser.parseString(IOUtils.toString(is, StandardCharsets.UTF_8)).getAsJsonObject();
                            String baseGroup = rootObj.has("group") ? rootObj.get("group").getAsString() :
                                    entryKey.getPath().replaceAll("rails/", "").replaceAll(".json", "");
                            if (rootObj.has("model")) {
                                String key = FilenameUtils.getBaseName(entryKey.getPath());
                                register(key, loadFromJson(resourceManager, key, rootObj, baseGroup, namespace));
                            } else {
                                for (Map.Entry<String, JsonElement> jsonEntry : rootObj.entrySet()) {
                                    if (!jsonEntry.getValue().isJsonObject()) continue;
                                    JsonObject obj = jsonEntry.getValue().getAsJsonObject();
                                    String key = jsonEntry.getKey().toLowerCase(Locale.ROOT);
                                    register(key, loadFromJson(resourceManager, key, obj, baseGroup, namespace));
                                }
                            }
                        }
                    } catch (Exception ex) {
                        RTE.LOGGER.error("Failed loading rail: " + entryKey.toString(), ex);
                        MtrModelRegistryUtil.recordLoadingError("Failed loading rail " + entryKey, ex);
                    }
                }
            }
        }

        TREE = Tree.loadTree("rail.rte.type", PATH_MAP, t -> t.name.getString());
        RTEClient.railRenderDispatcher.clearRail();
    }

    private static final RailModelProperties EMPTY_PROPERTY = new RailModelProperties(
            "null", (MutableComponent) Component.literal(""), null, 1f, 0, null, "group.rte.builtin"
    );

    private static final String LEGACY_BUILTIN_PREFIX = "nte_builtin_";
    private static final String BUILTIN_PREFIX = "rte_builtin_";

    public static String canonicalKey(String key) {
        return key != null && key.startsWith(LEGACY_BUILTIN_PREFIX)
            ? BUILTIN_PREFIX + key.substring(LEGACY_BUILTIN_PREFIX.length())
            : key;
    }

    public static RailModelProperties getProperty(String key) {
        final RailModelProperties direct = ELEMENTS.get(key);
        if (direct != null) {
            return direct;
        }
        final String canonical = canonicalKey(key);
        return canonical.equals(key) ? EMPTY_PROPERTY : ELEMENTS.getOrDefault(canonical, EMPTY_PROPERTY);
    }

    private static RailModelProperties loadFromJson(ResourceManager resourceManager, String key, JsonObject obj, String baseGroup, String scriptNamespace) throws Exception {
        if (obj.has("atlasIndex")) {
            RTEClient.atlasManager.load(resourceManager, new ResourceLocation(obj.get("atlasIndex").getAsString()));
        }

        RawModel rawModel = null;
        if (obj.has("model")) {
            rawModel = RTEClient.modelManager.loadRawModel(resourceManager,
                    new ResourceLocation(obj.get("model").getAsString()), RTEClient.atlasManager).copy();
            if (obj.has("textureId")) {
                rawModel.replaceTexture("default.png", new ResourceLocation(obj.get("textureId").getAsString()));
            }
            if (obj.has("flipV") && obj.get("flipV").getAsBoolean()) {
                rawModel.applyUVMirror(false, true);
            }
            rawModel.sourceLocation = new ResourceLocation(rawModel.sourceLocation.toString() + "/" + key);
        }

        float repeatInterval = obj.has("repeatInterval") ? obj.get("repeatInterval").getAsFloat() : 0.5f;
        float yOffset = obj.has("yOffset") ? obj.get("yOffset").getAsFloat() : 0f;
        String group = obj.has("group") ? obj.get("group").getAsString() : baseGroup;

        ScriptHolderBase script = null;
        if (obj.has("scriptFiles")) {
            try {
                script = new ScriptHolderClient();
                Map<ResourceLocation, String> scripts = new LinkedHashMap<>();
                if (obj.has("scriptTexts")) {
                    JsonArray scriptTexts = obj.get("scriptTexts").getAsJsonArray();
                    for (int i = 0; i < scriptTexts.size(); i++) {
                        scripts.put(new ResourceLocation(scriptNamespace, "script_texts/" + key + "/" + i),
                                scriptTexts.get(i).getAsString());
                    }
                }
                JsonArray scriptFiles = obj.get("scriptFiles").getAsJsonArray();
                for (int i = 0; i < scriptFiles.size(); i++) {
                    ResourceLocation scriptLocation = new ResourceLocation(scriptFiles.get(i).getAsString());
                    scripts.put(scriptLocation, ResourceUtil.readResource(resourceManager, scriptLocation));
                }
                script.load("Rail " + key, "Rail", resourceManager, scripts, obj, key, "create", "render", "dispose");
            } catch (Exception ex) {
                RTE.LOGGER.error("Failed loading rail script for " + key, ex);
                MtrModelRegistryUtil.recordLoadingError("Failed loading rail script " + key, ex);
                script = null;
            }
        }

        return new RailModelProperties(key, (MutableComponent) Component.translatable(obj.get("name").getAsString()), rawModel, repeatInterval, yOffset, script, group);
    }
}

