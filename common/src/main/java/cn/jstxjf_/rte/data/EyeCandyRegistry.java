package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.render.integration.MtrModelRegistryUtil;
import cn.jstxjf_.rte.scripting.ScriptHolderBase;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.Vertex;
import cn.jstxjf_.rte.gfxext.util.ResourceUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.jstxjf_.rte.compat.McCompat;

public class EyeCandyRegistry {

    public static final Map<String, EyeCandyProperties> ELEMENTS = new LinkedHashMap<>();
    public static final Map<String, EyeCandyProperties> PATH_MAP = new HashMap<>();
    public static Tree.Root<EyeCandyProperties> TREE = new Tree.Root<>("block.rte.eye_candy");

    public static void register(String key, EyeCandyProperties properties) {
        if (properties == null) return;
        ELEMENTS.put(key, properties);
        PATH_MAP.put(properties.path, properties);
    }

    public static List<ResourceLocation> getModelLocations() {
        final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager == null) {
            return Collections.emptyList();
        }

        final List<ResourceLocation> modelLocations = new ArrayList<>();
        for (String namespace : getResourceNamespaces(resourceManager)) {
            final List<com.mojang.datafixers.util.Pair<ResourceLocation, Resource>> resources =
                MtrModelRegistryUtil.listResources(resourceManager, namespace, "eyecandies", ".json");

            for (com.mojang.datafixers.util.Pair<ResourceLocation, Resource> pair : resources) {
                try (InputStream is = pair.getSecond().open()) {
                    final JsonObject rootObj = JsonParser.parseString(IOUtils.toString(is, StandardCharsets.UTF_8)).getAsJsonObject();
                    if (rootObj.has("model") || rootObj.has("scriptFiles")) {
                        rte$collectModelLocation(resourceManager, modelLocations, rootObj);
                    } else {
                        for (Map.Entry<String, JsonElement> jsonEntry : rootObj.entrySet()) {
                            if (jsonEntry.getValue().isJsonObject()) {
                                rte$collectModelLocation(resourceManager, modelLocations, jsonEntry.getValue().getAsJsonObject());
                            }
                        }
                    }
                } catch (Exception e) {
                    RTE.LOGGER.error("Failed collecting eye-candy item model locations from {}", pair.getFirst(), e);
                }
            }
        }

        return modelLocations;
    }

    public static List<ResourceLocation> getModelLoacations() {
        return getModelLocations();
    }

    public static void reload(ResourceManager resourceManager) {
        ELEMENTS.clear();
        PATH_MAP.clear();

        if (resourceManager != null) {
            for (String namespace : getResourceNamespaces(resourceManager)) {
                List<com.mojang.datafixers.util.Pair<ResourceLocation, Resource>> reloadResources =
                    MtrModelRegistryUtil.listResources(resourceManager, namespace, "eyecandies", ".json");
                for (com.mojang.datafixers.util.Pair<ResourceLocation, Resource> pair : reloadResources) {
                    ResourceLocation entryKey = pair.getFirst();
                    Resource entryValue = pair.getSecond();
                    try {
                        try (InputStream is = entryValue.open()) {
                            JsonObject rootObj = JsonParser.parseString(IOUtils.toString(is, StandardCharsets.UTF_8)).getAsJsonObject();
                            String packId = entryValue.sourcePackId();
                            String baseGroup = rootObj.has("group") ? rootObj.get("group").getAsString() :
                                packId + "/" + entryKey.getPath().replaceAll("eyecandies/", "").replaceAll(".json", "");
                            if (rootObj.has("model") || rootObj.has("scriptFiles")) {
                                String key = FilenameUtils.getBaseName(entryKey.getPath());
                                register(key, loadFromJson(resourceManager, key, rootObj, baseGroup, namespace));
                            } else {
                                for (Map.Entry<String, JsonElement> jsonEntry : rootObj.entrySet()) {
                                    if (!jsonEntry.getValue().isJsonObject()) continue;
                                    JsonObject obj = jsonEntry.getValue().getAsJsonObject();
                                    String key = jsonEntry.getKey().toLowerCase(Locale.ROOT);
                                    try {
                                        register(key, loadFromJson(resourceManager, key, obj, baseGroup, namespace));
                                    } catch (MissingDependencyException ignored) {
                                    } catch (java.io.IOException missingFile) {
                                        RTE.LOGGER.warn("[EyeCandy] skipped {} in {}: {}", key, entryKey, missingFile.getMessage());
                                    } catch (Exception entryEx) {
                                        RTE.LOGGER.error("Failed loading eye-candy entry " + key + " in " + entryKey, entryEx);
                                        MtrModelRegistryUtil.recordLoadingError(
                                            "Failed loading eye-candy entry " + key + " in " + entryKey, entryEx);
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        RTE.LOGGER.error("Failed loading eye-candy: " + entryKey.toString(), ex);
                        MtrModelRegistryUtil.recordLoadingError("Failed loading eye-candy " + entryKey, ex);
                    }
                }
            }
        }

        rte$summariseMissingDependencies();
        TREE = Tree.loadTree("block.rte.eye_candy", PATH_MAP, t -> t.name.getString());
    }

    public static EyeCandyProperties getProperty(String key) {
        return ELEMENTS.getOrDefault(key, null);
    }

    private static List<String> getResourceNamespaces(ResourceManager resourceManager) {
        return resourceManager.getNamespaces().stream().sorted().toList();
    }

    private static void rte$collectModelLocation(ResourceManager resourceManager, List<ResourceLocation> modelLocations, JsonObject obj) {
        if (!obj.has("itemModel")) {
            return;
        }

        final String itemModelPath = obj.get("itemModel").getAsString();
        final String[] pathParts = itemModelPath.split("/");
        ResourceLocation location = McCompat.id(itemModelPath);
        if (itemModelPath.endsWith(".png")) {
            location = addImageModel(resourceManager, location);
            modelLocations.add(location);
        } else if (!pathParts[pathParts.length - 1].contains(".")) {
            modelLocations.add(location);
        }
    }

    private static ResourceLocation mapImageItem(ResourceLocation imageLocation) {
        return McCompat.id(
            "dynamic___item__" + imageLocation.getNamespace(),
            imageLocation.getPath().replace(".png", "_png")
        );
    }

    private static ResourceLocation addImageModel(ResourceManager resourceManager, ResourceLocation imageLocation) {
        final ResourceLocation itemLocation = mapImageItem(imageLocation);
        final ResourceLocation modelLocation = prefix(itemLocation, "models/item/", ".json");
        final ResourceLocation textureLocation = prefix(itemLocation, "textures/", ".png");
        DynamicResource.addResourcesClient(modelLocation, () -> new ByteArrayInputStream(String.format(
            "{\"parent\":\"item/generated\",\"textures\":{\"layer0\":\"%s\"}}",
            itemLocation
        ).getBytes(StandardCharsets.UTF_8)));
        DynamicResource.addResourcesClient(textureLocation, () -> ResourceUtil.getResource(resourceManager, imageLocation)
            .orElseThrow(() -> new FileNotFoundException(imageLocation.toString()))
            .open());
        return itemLocation;
    }

    private static ResourceLocation prefix(ResourceLocation location, String prefix, String suffix) {
        return McCompat.id(location.getNamespace(), prefix + location.getPath() + suffix);
    }

    public static final class MissingDependencyException extends Exception {

        public final String namespace;

        MissingDependencyException(String key, String namespace) {
            super("Eye-candy " + key + " needs resource pack namespace '" + namespace + "', which is not installed");
            this.namespace = namespace;
        }
    }

    private static final Set<String> RTE_REPORTED_MISSING = new LinkedHashSet<>();
    private static final Map<String, Integer> RTE_MISSING_COUNTS = new LinkedHashMap<>();

    private static void rte$reportMissingDependency(String namespace, String key) {
        RTE_MISSING_COUNTS.merge(namespace, 1, Integer::sum);
        if (RTE_REPORTED_MISSING.add(namespace)) {
            RTE.LOGGER.warn("[EyeCandy] resource pack namespace '{}' is not installed; entries needing it are skipped (first: {})",
                namespace, key);
        }
    }

    public static void rte$summariseMissingDependencies() {
        for (final Map.Entry<String, Integer> entry : RTE_MISSING_COUNTS.entrySet()) {
            if (cn.jstxjf_.rte.ClientConfig.reportMissingResourcePacks) {
                MtrModelRegistryUtil.recordLoadingError(
                    "Missing resource pack '" + entry.getKey() + "': " + entry.getValue() + " decoration(s) skipped",
                    new MissingDependencyException("(" + entry.getValue() + " entries)", entry.getKey()));
            } else {
                RTE.LOGGER.info("[EyeCandy] resource pack '{}' is not installed; {} decoration(s) skipped",
                    entry.getKey(), entry.getValue());
            }
        }
        RTE_REPORTED_MISSING.clear();
        RTE_MISSING_COUNTS.clear();
    }

    private static String rte$missingNamespace(ResourceManager resourceManager, JsonObject obj) {
        final Set<String> available = resourceManager.getNamespaces();
        for (final String reference : rte$referencedLocations(obj)) {
            final int colon = reference.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            final String namespace = reference.substring(0, colon).toLowerCase(Locale.ROOT);
            if (!available.contains(namespace)) {
                return namespace;
            }
        }
        return null;
    }

    private static List<String> rte$referencedLocations(JsonObject obj) {
        final List<String> found = new ArrayList<>();
        for (final String field : new String[]{"model", "itemModel", "textureId"}) {
            if (!obj.has(field)) {
                continue;
            }
            try {
                found.add(obj.get(field).getAsString());
            } catch (Exception ignored) {
            }
        }
        for (final String field : new String[]{"scriptFiles", "scriptTexts"}) {
            if (!obj.has(field)) {
                continue;
            }
            try {
                final JsonArray array = obj.get(field).getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    final String value = array.get(i).getAsString();
                    if ("scriptFiles".equals(field)) {
                        found.add(value);
                    } else {
                        final Matcher matcher = RTE_LOCATION_PATTERN.matcher(value);
                        while (matcher.find()) {
                            found.add(matcher.group(1));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return found;
    }

    private static final Pattern RTE_LOCATION_PATTERN =
        Pattern.compile("\"([a-z0-9_.-]+:[a-z0-9_./-]+)\"");

    private static final String[][] RTE_KEY_ALIASES = {
        {"textureResource", "textureId"},
        {"flipTextureV", "flipV"},
        {"Shape", "shape"},
        {"CollisionShape", "collisionShape"}
    };

    private static void rte$normalizeKeys(JsonObject obj) {
        for (final String[] alias : RTE_KEY_ALIASES) {
            if (obj.has(alias[0]) && !obj.has(alias[1])) {
                obj.add(alias[1], obj.get(alias[0]));
            }
        }
    }

    private static EyeCandyProperties loadFromJson(ResourceManager resourceManager, String key, JsonObject obj, String group, String scriptNamespace) throws Exception {
        rte$normalizeKeys(obj);
        final String rteMissingNamespace = rte$missingNamespace(resourceManager, obj);
        if (rteMissingNamespace != null) {
            rte$reportMissingDependency(rteMissingNamespace, key);
            throw new MissingDependencyException(key, rteMissingNamespace);
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Invalid eye-candy key: empty");
        }
        if (key.contains("/")) {
            throw new IllegalArgumentException("Invalid eye-candy key: " + key + " (contains /)");
        }

        if (obj.has("atlasIndex")) {
            RTEClient.atlasManager.load(resourceManager, McCompat.id(obj.get("atlasIndex").getAsString()));
        }

        int lightLevel = obj.has("lightLevel") ? obj.get("lightLevel").getAsInt() : 0;

        ModelCluster cluster = null;
        if (obj.has("model")) {
            RawModel rawModel = RTEClient.modelManager.loadRawModel(resourceManager,
                McCompat.id(obj.get("model").getAsString()), RTEClient.atlasManager).copy();

            if (obj.has("textureId")) {
                rawModel.replaceTexture("default.png", McCompat.id(obj.get("textureId").getAsString()));
            }
            if (obj.has("flipV") && obj.get("flipV").getAsBoolean()) {
                rawModel.applyUVMirror(false, true);
            }
            if (obj.has("translation")) {
                JsonArray vec = obj.get("translation").getAsJsonArray();
                rawModel.applyTranslation(vec.get(0).getAsFloat(), vec.get(1).getAsFloat(), vec.get(2).getAsFloat());
            }
            if (obj.has("rotation")) {
                JsonArray vec = obj.get("rotation").getAsJsonArray();
                rawModel.applyRotation(new Vector3f(1, 0, 0), vec.get(0).getAsFloat());
                rawModel.applyRotation(new Vector3f(0, 1, 0), vec.get(1).getAsFloat());
                rawModel.applyRotation(new Vector3f(0, 0, 1), vec.get(2).getAsFloat());
            }
            if (obj.has("scale")) {
                JsonArray vec = obj.get("scale").getAsJsonArray();
                rawModel.applyScale(vec.get(0).getAsFloat(), vec.get(1).getAsFloat(), vec.get(2).getAsFloat());
            }
            if (obj.has("mirror")) {
                JsonArray vec = obj.get("mirror").getAsJsonArray();
                rawModel.applyMirror(
                    vec.get(0).getAsBoolean(), vec.get(1).getAsBoolean(), vec.get(2).getAsBoolean(),
                    vec.get(0).getAsBoolean(), vec.get(1).getAsBoolean(), vec.get(2).getAsBoolean()
                );
            }

            rawModel.sourceLocation = McCompat.id(rawModel.sourceLocation.toString() + "/" + key);
            cluster = RTEClient.modelManager.uploadVertArrays(rawModel);
        }

        ModelCluster itemModelCluster = null;
        Matrix4f itemTransform = null;
        BakedModel itemBakedModel = null;
        if (obj.has("itemModel")) {
            final String path = obj.get("itemModel").getAsString();
            ResourceLocation location = McCompat.id(path);
            final String[] pathParts = path.split("/");
            if (path.endsWith(".png")) {
                location = mapImageItem(location);
                itemBakedModel = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(location, "inventory"));
            } else if (pathParts[pathParts.length - 1].contains(".")) {
                final RawModel rawModel = RTEClient.modelManager.loadRawModel(resourceManager, location, RTEClient.atlasManager).copy();
                itemModelCluster = RTEClient.modelManager.uploadVertArrays(rawModel);
            } else {
                itemBakedModel = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(location, "inventory"));
            }
        } else {
            itemModelCluster = cluster;
        }

        if (itemModelCluster != null) {
            itemTransform = new Matrix4f();
            itemTransform.rotateY((float) Math.toRadians(-30));
            float minX = 0;
            float minY = 0;
            float minZ = 0;
            float maxX = 0;
            float maxY = 0;
            float maxZ = 0;
            for (RawModel rawModel : new RawModel[]{itemModelCluster.opaqueParts, itemModelCluster.translucentParts}) {
                for (RawMesh mesh : rawModel.meshList.values()) {
                    for (Vertex vertex : mesh.vertices) {
                        final Vector3f position = itemTransform.transform(vertex.position);
                        minX = Math.min(minX, position.x());
                        maxX = Math.max(maxX, position.x());
                        minY = Math.min(minY, position.y());
                        maxY = Math.max(maxY, position.y());
                        minZ = Math.min(minZ, position.z());
                        maxZ = Math.max(maxZ, position.z());
                    }
                }
            }
            minY = Math.min(minY, 0);
            final float xExtent = Math.max(Math.abs(minX), Math.abs(maxX));
            final float yExtent = maxY - minY;
            final float zExtent = Math.max(Math.abs(minZ), Math.abs(maxZ));
            final float horizontalScale = 0.5f / Math.max(0.5f, Math.max(xExtent, zExtent));
            final float verticalScale = 1f / Math.max(1f, yExtent);
            itemTransform.scale(Math.min(horizontalScale, verticalScale));
            itemTransform.translate(0, -minY, 0);
        }

        ScriptHolderBase script = null;
        if (obj.has("scriptFiles")) {
            try {
                script = new cn.jstxjf_.rte.scripting.ScriptHolderClient();
                Map<ResourceLocation, String> scripts = new LinkedHashMap<>();
                if (obj.has("scriptTexts")) {
                    JsonArray scriptTexts = obj.get("scriptTexts").getAsJsonArray();
                    for (int i = 0; i < scriptTexts.size(); i++) {
                        scripts.put(McCompat.id(scriptNamespace, "script_texts/" + key + "/" + i),
                            scriptTexts.get(i).getAsString());
                    }
                }
                JsonArray scriptFiles = obj.get("scriptFiles").getAsJsonArray();
                for (int i = 0; i < scriptFiles.size(); i++) {
                    ResourceLocation scriptLocation = McCompat.id(scriptFiles.get(i).getAsString());
                    scripts.put(scriptLocation, ResourceUtil.readResource(resourceManager, scriptLocation));
                }
                script.load("EyeCandy " + key, "Block", resourceManager, scripts, obj, key, "create", "render", "dispose", "use");
            } catch (Exception e) {
                RTE.LOGGER.error("Failed loading eye-candy script: " + key, e);
                MtrModelRegistryUtil.recordLoadingError("Failed loading eye-candy script " + key, e);
                script = null;
            }
        }

        String shape = obj.has("shape") ? obj.get("shape").getAsString() : "0, 0, 0, 16, 16, 16";
        String collisionShape = obj.has("collisionShape") ? obj.get("collisionShape").getAsString() : "0, 0, 0, 0, 0, 0";
        boolean fixedMatrix = obj.has("fixedMatrix") && obj.get("fixedMatrix").getAsBoolean();
        boolean isTicketBarrier = obj.has("isTicketBarrier") && obj.get("isTicketBarrier").getAsBoolean();
        boolean isEntrance = obj.has("isEntrance") && obj.get("isEntrance").getAsBoolean();
        boolean asPlatform = obj.has("asPlatform") && obj.get("asPlatform").getAsBoolean();
        group = obj.has("group") ? obj.get("group").getAsString() : group;
        RelativePosition.Combination positions = RelativePosition.Combination.decode(
            obj.has("combination") ? obj.get("combination").getAsString() : ""
        );

        if (cluster == null && script == null) {
            throw new IllegalArgumentException("Invalid eye-candy json: " + key
                + " (no usable model and its script failed to load)");
        }

        return new EyeCandyProperties(key, (MutableComponent) Component.translatable(obj.get("name").getAsString()),
            cluster, itemModelCluster, itemTransform, itemBakedModel, script, shape, collisionShape, fixedMatrix, lightLevel,
            isTicketBarrier, isEntrance, asPlatform, group, positions);
    }
}
