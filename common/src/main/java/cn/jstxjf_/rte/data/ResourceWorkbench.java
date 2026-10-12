package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTE;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.mtr.core.data.TransportMode;
import org.mtr.core.serializer.JsonReader;
import org.mtr.core.serializer.JsonWriter;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.mtr.mod.client.CustomResourceLoader;
import org.mtr.mod.resource.ResourceProvider;
import org.mtr.mod.resource.VehicleResource;
import org.mtr.mod.resource.VehicleResourceWrapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ResourceWorkbench {

    private static final Path EXPORT_DIR = Path.of("./rte/workbench/").toAbsolutePath().normalize();
    private static final Path TEXTURE_DIR = EXPORT_DIR.resolve("textures");
    private static final Path MODEL_DIR = EXPORT_DIR.resolve("models");
    private static final Path PACKS_DIR = EXPORT_DIR.resolve("packs");
    private static final String LOCAL_MODEL_PREFIX = "rte:workbench_model/";
    private static final String LOCAL_TEXTURE_PREFIX = "rte:workbench_tex/";
    private static final java.util.Set<String> REGISTERED_TEXTURES = new java.util.HashSet<>();
    private static final java.util.Map<String, String> TEXTURE_ID_TO_FILE = new java.util.HashMap<>();
    private static final java.util.Set<String> PACK_RESOURCE_SUFFIXES =
        java.util.Set.of(".png", ".obj", ".bbmodel", ".json", ".mtl");

    private ResourceWorkbench() {
    }

    private static final Path AUTOLOAD_DIR = EXPORT_DIR.resolve("autoload");

    public static void ensureDirs() {
        try {
            Files.createDirectories(TEXTURE_DIR);
            Files.createDirectories(MODEL_DIR);
            Files.createDirectories(AUTOLOAD_DIR);
        } catch (Exception ignored) {
        }
    }

    public static boolean saveAutoload(JsonObject json, String id) {
        try {
            Files.createDirectories(AUTOLOAD_DIR);
            Files.writeString(AUTOLOAD_DIR.resolve(sanitize(id) + ".json"), json.toString());
            return true;
        } catch (Exception e) {
            RTE.LOGGER.warn("[Workbench] saveAutoload failed", e);
            return false;
        }
    }

    public static List<String> listAutoload() {
        return listFiles(AUTOLOAD_DIR, ".json");
    }

    public static boolean deleteAutoload(String fileName) {
        try {
            return Files.deleteIfExists(AUTOLOAD_DIR.resolve(fileName));
        } catch (Exception e) {
            return false;
        }
    }

    public static void autoloadAll() {
        try {
            if (!Files.isDirectory(AUTOLOAD_DIR)) {
                return;
            }
            int loaded = 0;
            for (final String fileName : listFiles(AUTOLOAD_DIR, ".json")) {
                try {
                    final JsonObject json = org.mtr.core.tool.Utilities.parseJson(Files.readString(AUTOLOAD_DIR.resolve(fileName)));
                    final String id = json.has("id") ? json.get("id").getAsString() : null;
                    if (id != null && !id.isBlank() && registerCopy(json, id)) {
                        loaded++;
                    }
                } catch (Exception e) {
                    RTE.LOGGER.warn("[Workbench] autoload failed for {}", fileName, e);
                }
            }
            if (loaded > 0) {
                RTE.LOGGER.info("[Workbench] autoloaded {} vehicle(s)", loaded);
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("[Workbench] autoloadAll failed", e);
        }
    }

    public static Path exportDir() {
        return EXPORT_DIR;
    }

    public static List<String> listTextureFiles() {
        return listFiles(TEXTURE_DIR, ".png");
    }

    public static List<String> listModelFiles() {
        final List<String> files = listFiles(MODEL_DIR, ".bbmodel");
        files.addAll(listFiles(MODEL_DIR, ".obj"));
        return files;
    }

    private static List<String> listFiles(Path dir, String suffix) {
        final List<String> names = new ArrayList<>();
        try {
            if (Files.isDirectory(dir)) {
                try (java.util.stream.Stream<Path> stream = Files.list(dir)) {
                    stream.filter(path -> path.getFileName().toString().toLowerCase().endsWith(suffix))
                        .sorted()
                        .forEach(path -> names.add(path.getFileName().toString()));
                }
            }
        } catch (Exception ignored) {
        }
        return names;
    }

    public static String importLocalTexture(String fileName) {
        try {
            final Path file = TEXTURE_DIR.resolve(fileName);
            if (!Files.exists(file)) {
                return null;
            }
            final String id = LOCAL_TEXTURE_PREFIX + fileName.toLowerCase().replaceAll("[^a-z0-9._-]", "_");
            try (InputStream inputStream = Files.newInputStream(file)) {
                final com.mojang.blaze3d.platform.NativeImage image = com.mojang.blaze3d.platform.NativeImage.read(inputStream);
                final net.minecraft.client.renderer.texture.DynamicTexture texture = new net.minecraft.client.renderer.texture.DynamicTexture(image);
                Minecraft.getInstance().getTextureManager().register(new ResourceLocation(id), texture);
                REGISTERED_TEXTURES.add(id);
                TEXTURE_ID_TO_FILE.put(id, fileName);
            }
            return id;
        } catch (Exception e) {
            RTE.LOGGER.warn("[Workbench] importLocalTexture failed for {}", fileName, e);
            return null;
        }
    }

    private static final java.util.Map<String, String> MODEL_ID_TO_FILE = new java.util.HashMap<>();

    public static String localModelId(String fileName) {
        final String sanitized = fileName.toLowerCase().replaceAll("[^a-z0-9._/-]", "_");
        MODEL_ID_TO_FILE.put(sanitized, fileName);
        return LOCAL_MODEL_PREFIX + sanitized;
    }

    public static List<VehicleResource> listVehicles() {
        final List<VehicleResource> vehicles = new ArrayList<>();
        try {
            CustomResourceLoader.iterateVehicles(TransportMode.TRAIN, vehicles::add);
        } catch (Throwable e) {
            RTE.LOGGER.warn("[Workbench] listVehicles failed", e);
        }
        return vehicles;
    }

    public static JsonObject snapshot(VehicleResource vehicle) {
        try {
            final VehicleResourceWrapper wrapper = vehicle.toVehicleResourceWrapper();
            final JsonObject jsonObject = new JsonObject();
            wrapper.serializeData(new JsonWriter(jsonObject));
            return jsonObject;
        } catch (Throwable e) {
            RTE.LOGGER.warn("[Workbench] snapshot failed", e);
            return null;
        }
    }

    public static boolean registerCopy(JsonObject json, String newId) {
        try {
            json.addProperty("id", newId);
            final VehicleResourceWrapper wrapper = new VehicleResourceWrapper(new JsonReader(json));
            final VehicleResource vehicle = wrapper.toVehicleResource(
                resourceProvider(), new Object2ObjectArrayMap<>(), new Object2ObjectArrayMap<>());
            CustomResourceLoader.registerVehicle(vehicle);
            return true;
        } catch (Throwable e) {
            RTE.LOGGER.warn("[Workbench] registerCopy failed", e);
            return false;
        }
    }

    public static boolean export(JsonObject json, String name) {
        try {
            Files.createDirectories(EXPORT_DIR);
            final com.google.gson.JsonElement pretty = com.google.gson.JsonParser.parseString(json.toString());
            Files.writeString(EXPORT_DIR.resolve(sanitize(name) + ".json"),
                new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(pretty));
            return true;
        } catch (Throwable e) {
            RTE.LOGGER.warn("[Workbench] export failed", e);
            return false;
        }
    }

    public static Path exportPack(JsonObject vehicleSnapshot, String packName) {
        try {
            final String packNs = sanitizeNamespace(packName);
            final JsonObject vehicle = org.mtr.core.tool.Utilities.parseJson(vehicleSnapshot.toString());

            final java.util.LinkedHashMap<String, byte[]> files = new java.util.LinkedHashMap<>();
            final java.util.LinkedHashSet<String> textureIds = new java.util.LinkedHashSet<>();
            final java.util.List<String> missing = new ArrayList<>();
            rewriteAndCollect(vehicle, packNs, files, textureIds, missing);
            collectObjDependencies(files, packNs, missing);

            final JsonObject root = new JsonObject();
            final org.mtr.libraries.com.google.gson.JsonArray vehiclesArray = new org.mtr.libraries.com.google.gson.JsonArray();
            vehiclesArray.add(vehicle);
            root.add("vehicles", vehiclesArray);
            final org.mtr.libraries.com.google.gson.JsonArray textureArray = new org.mtr.libraries.com.google.gson.JsonArray();
            for (final String textureId : textureIds) {
                textureArray.add(textureId);
            }
            root.add("textureResources", textureArray);

            Files.createDirectories(PACKS_DIR);
            final Path zipFile = PACKS_DIR.resolve(sanitize(packName) + ".zip");
            try (java.util.zip.ZipOutputStream zip = new java.util.zip.ZipOutputStream(Files.newOutputStream(zipFile))) {
                final String mcmeta = "{\"pack\":{\"pack_format\":" + RtePackFormat.VALUE + ",\"description\":\"RTE exported: " + packName + "\"}}";
                putZipEntry(zip, "pack.mcmeta", mcmeta.getBytes(StandardCharsets.UTF_8));
                putZipEntry(zip, "assets/" + packNs + "/mtr_custom_resources.json", root.toString().getBytes(StandardCharsets.UTF_8));
                for (final java.util.Map.Entry<String, byte[]> entry : files.entrySet()) {
                    putZipEntry(zip, entry.getKey(), entry.getValue());
                }
            }
            if (!missing.isEmpty()) {
                RTE.LOGGER.warn("[Workbench] exportPack {} missing {} resource(s): {}", zipFile, missing.size(), missing);
            }
            RTE.LOGGER.info("[Workbench] exported pack {} ({} files, {} missing)", zipFile, files.size(), missing.size());
            return zipFile;
        } catch (Throwable e) {
            RTE.LOGGER.warn("[Workbench] exportPack failed", e);
            return null;
        }
    }

    private static void rewriteAndCollect(org.mtr.libraries.com.google.gson.JsonElement element, String packNs,
                                          java.util.Map<String, byte[]> files, java.util.Set<String> textureIds,
                                          java.util.List<String> missing) {
        if (element.isJsonArray()) {
            for (final org.mtr.libraries.com.google.gson.JsonElement child : element.getAsJsonArray()) {
                rewriteAndCollect(child, packNs, files, textureIds, missing);
            }
            return;
        }
        if (!element.isJsonObject()) {
            return;
        }
        final JsonObject object = element.getAsJsonObject();
        for (final String key : new ArrayList<>(object.keySet())) {
            final org.mtr.libraries.com.google.gson.JsonElement value = object.get(key);
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                final String raw = value.getAsString();
                final String suffix = packResourceSuffix(raw);
                if (suffix == null) {
                    continue;
                }
                final String newId = remapResourceId(raw, packNs);
                object.addProperty(key, newId);
                final String zipPath = "assets/" + packNs + "/" + newId.substring(newId.indexOf(':') + 1);
                if (!files.containsKey(zipPath)) {
                    final byte[] bytes = readResourceBytes(raw);
                    if (bytes != null) {
                        files.put(zipPath, bytes);
                    } else {
                        missing.add(raw);
                    }
                }
                if (suffix.equals(".png")) {
                    textureIds.add(newId);
                }
            } else {
                rewriteAndCollect(value, packNs, files, textureIds, missing);
            }
        }
    }

    private static void collectObjDependencies(java.util.Map<String, byte[]> files, String packNs, java.util.List<String> missing) {
        final java.util.Set<String> visitedMaterialLibraries = new java.util.HashSet<>();
        for (final String zipPath : new ArrayList<>(files.keySet())) {
            if (!zipPath.endsWith(".obj")) {
                continue;
            }
            final ResourceLocation objId = sourceIdOf(zipPath, packNs);
            if (objId == null) {
                continue;
            }
            final String[] lines = new String(files.get(zipPath), StandardCharsets.UTF_8).split("\\r?\\n", -1);
            final java.util.Set<String> materialNames = new java.util.LinkedHashSet<>();
            boolean materialLibraryFound = false;
            boolean rewritten = false;
            for (int i = 0; i < lines.length; i++) {
                final String trimmed = lines[i].trim();
                if (trimmed.startsWith("mtllib ")) {
                    final StringBuilder builder = new StringBuilder("mtllib");
                    for (final String reference : trimmed.substring(7).trim().split("\\s+")) {
                        final ResourceLocation mtlId = cn.jstxjf_.rte.gfxext.util.ResourceUtil.resolveRelativePath(objId, reference, ".mtl");
                        if (copyDependency(files, packNs, mtlId, missing)) {
                            materialLibraryFound = true;
                            if (visitedMaterialLibraries.add(zipPathOf(mtlId, packNs))) {
                                collectMtlDependencies(files, packNs, mtlId, missing);
                            }
                        }
                        builder.append(' ').append(exportReference(reference, mtlId, packNs));
                    }
                    if (!builder.toString().equals(trimmed)) {
                        lines[i] = builder.toString();
                        rewritten = true;
                    }
                } else if (trimmed.startsWith("usemtl ")) {
                    materialNames.add(trimmed.substring(7).trim().split("#", 2)[0].trim());
                }
            }
            if (!materialLibraryFound) {
                for (final String materialName : materialNames) {
                    if (!materialName.isEmpty() && !materialName.equals("_")) {
                        copyDependency(files, packNs, cn.jstxjf_.rte.gfxext.util.ResourceUtil.resolveRelativePath(objId, materialName, ".png"), null);
                    }
                }
            }
            if (rewritten) {
                files.put(zipPath, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static void collectMtlDependencies(java.util.Map<String, byte[]> files, String packNs, ResourceLocation mtlId, java.util.List<String> missing) {
        final String zipPath = zipPathOf(mtlId, packNs);
        final String[] lines = new String(files.get(zipPath), StandardCharsets.UTF_8).split("\\r?\\n", -1);
        boolean rewritten = false;
        for (int i = 0; i < lines.length; i++) {
            final String trimmed = lines[i].trim();
            if (!trimmed.startsWith("map_")) {
                continue;
            }
            final String[] tokens = trimmed.split("\\s+");
            if (tokens.length < 2) {
                continue;
            }
            final String reference = tokens[tokens.length - 1];
            final ResourceLocation textureId = cn.jstxjf_.rte.gfxext.util.ResourceUtil.resolveRelativePath(mtlId, reference, ".png");
            copyDependency(files, packNs, textureId, missing);
            final String exported = exportReference(reference, textureId, packNs);
            if (!exported.equals(reference)) {
                lines[i] = trimmed.substring(0, trimmed.length() - reference.length()) + exported;
                rewritten = true;
            }
        }
        if (rewritten) {
            files.put(zipPath, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
        }
    }

    private static boolean copyDependency(java.util.Map<String, byte[]> files, String packNs, ResourceLocation id, java.util.List<String> missing) {
        final String zipPath = zipPathOf(id, packNs);
        if (files.containsKey(zipPath)) {
            return true;
        }
        final byte[] bytes = readResourceBytes(id.toString());
        if (bytes == null) {
            if (missing != null) {
                missing.add(id.toString());
            }
            return false;
        }
        files.put(zipPath, bytes);
        return true;
    }

    private static String exportReference(String reference, ResourceLocation resolved, String packNs) {
        return reference.indexOf(':') < 0 ? reference : remapResourceId(resolved.toString(), packNs);
    }

    private static String zipPathOf(ResourceLocation id, String packNs) {
        final String remapped = remapResourceId(id.toString(), packNs);
        return "assets/" + packNs + "/" + remapped.substring(remapped.indexOf(':') + 1);
    }

    private static ResourceLocation sourceIdOf(String zipPath, String packNs) {
        final String prefix = "assets/" + packNs + "/";
        if (!zipPath.startsWith(prefix)) {
            return null;
        }
        final String rest = zipPath.substring(prefix.length());
        final int slash = rest.indexOf('/');
        return slash <= 0 ? null : ResourceLocation.tryParse(rest.substring(0, slash) + ":" + rest.substring(slash + 1));
    }

    private static String packResourceSuffix(String value) {
        if (value.length() < 5 || value.indexOf(' ') >= 0) {
            return null;
        }
        final String lower = value.toLowerCase();
        for (final String suffix : PACK_RESOURCE_SUFFIXES) {
            if (lower.endsWith(suffix)) {
                return suffix;
            }
        }
        return null;
    }

    private static String remapResourceId(String resourceId, String packNs) {
        final int colon = resourceId.indexOf(':');
        final String namespace = colon >= 0 ? resourceId.substring(0, colon) : "minecraft";
        final String path = colon >= 0 ? resourceId.substring(colon + 1) : resourceId;
        return packNs + ":" + namespace.replaceAll("[^a-z0-9._-]", "_") + "/" + path;
    }

    private static byte[] readResourceBytes(String resourceId) {
        try {
            if (resourceId.startsWith(LOCAL_MODEL_PREFIX)) {
                final String sanitized = resourceId.substring(LOCAL_MODEL_PREFIX.length());
                final Path file = MODEL_DIR.resolve(MODEL_ID_TO_FILE.getOrDefault(sanitized, sanitized));
                return Files.exists(file) ? Files.readAllBytes(file) : null;
            }
            if (resourceId.startsWith(LOCAL_TEXTURE_PREFIX)) {
                final String fileName = TEXTURE_ID_TO_FILE.get(resourceId);
                if (fileName != null) {
                    final Path file = TEXTURE_DIR.resolve(fileName);
                    if (Files.exists(file)) {
                        return Files.readAllBytes(file);
                    }
                }
                return null;
            }
            final String normalized = resourceId.contains(":") ? resourceId : "minecraft:" + resourceId;
            final Optional<Resource> resource = Minecraft.getInstance().getResourceManager()
                .getResource(new ResourceLocation(normalized));
            if (resource.isEmpty()) {
                return null;
            }
            try (InputStream inputStream = resource.get().open()) {
                return inputStream.readAllBytes();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static void putZipEntry(java.util.zip.ZipOutputStream zip, String path, byte[] bytes) throws java.io.IOException {
        zip.putNextEntry(new java.util.zip.ZipEntry(path));
        zip.write(bytes);
        zip.closeEntry();
    }

    private static String sanitizeNamespace(String name) {
        final String namespace = name.toLowerCase().replaceAll("[^a-z0-9._-]", "_");
        return namespace.isEmpty() ? "rte_pack" : namespace;
    }

    private static ResourceProvider resourceProvider() {
        return identifier -> {
            try {
                final ResourceLocation location = identifier.data;
                if (location.getNamespace().equals("rte") && location.getPath().startsWith("workbench_model/")) {
                    final String sanitized = location.getPath().substring("workbench_model/".length());
                    final String fileName = MODEL_ID_TO_FILE.getOrDefault(sanitized, sanitized);
                    final Path file = MODEL_DIR.resolve(fileName);
                    return Files.exists(file) ? Files.readString(file, StandardCharsets.UTF_8) : "";
                }
                final Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location);
                if (resource.isEmpty()) {
                    return "";
                }
                try (InputStream inputStream = resource.get().open()) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                return "";
            }
        };
    }

    private static String sanitize(String name) {
        return name.trim().replaceAll("[^\\w\\u4e00-\\u9fff-]", "_");
    }

    private static final java.util.Map<String, List<float[]>> MODEL_MESH_CACHE = new java.util.HashMap<>();
    private static final List<float[]> ORIGIN_ONLY = List.of(new float[]{0, 0, 0});
    private static final String[] FACE_NAMES = {"north", "south", "west", "east", "up", "down"};
    private static final float[][][] FACE_CORNERS = {
        {{1, 1, 0}, {0, 1, 0}, {0, 0, 0}, {1, 0, 0}},
        {{0, 1, 1}, {1, 1, 1}, {1, 0, 1}, {0, 0, 1}},
        {{0, 1, 0}, {0, 1, 1}, {0, 0, 1}, {0, 0, 0}},
        {{1, 1, 1}, {1, 1, 0}, {1, 0, 0}, {1, 0, 1}},
        {{0, 1, 0}, {1, 1, 0}, {1, 1, 1}, {0, 1, 1}},
        {{0, 0, 1}, {1, 0, 1}, {1, 0, 0}, {0, 0, 0}}
    };

    private static void collectOutlinerGroups(com.google.gson.JsonElement node, String inheritedGroup,
                                              java.util.Map<String, String> uuidToGroup) {
        if (node.isJsonPrimitive()) {
            if (inheritedGroup != null) {
                uuidToGroup.put(node.getAsString(), inheritedGroup);
            }
            return;
        }
        if (!node.isJsonObject()) {
            return;
        }
        final com.google.gson.JsonObject group = node.getAsJsonObject();
        final String groupName = group.has("name") ? group.get("name").getAsString() : inheritedGroup;
        if (group.has("children")) {
            for (final com.google.gson.JsonElement child : group.getAsJsonArray("children")) {
                collectOutlinerGroups(child, groupName, uuidToGroup);
            }
        }
    }


    private static java.util.Map<String, List<float[]>> readInlineParts(JsonObject model) {
        final java.util.Map<String, List<float[]>> groupToOffsets = new java.util.HashMap<>();
        if (model == null || !model.has("parts") || !model.get("parts").isJsonArray()) {
            return groupToOffsets;
        }
        try {
            for (final org.mtr.libraries.com.google.gson.JsonElement partEntry : model.getAsJsonArray("parts")) {
                if (!partEntry.isJsonObject()) {
                    continue;
                }
                final JsonObject part = partEntry.getAsJsonObject();
                if (!part.has("positionDefinition") || !part.get("positionDefinition").isJsonObject()) {
                    continue;
                }
                final JsonObject definition = part.getAsJsonObject("positionDefinition");
                if (!definition.has("name")) {
                    continue;
                }
                final List<float[]> offsets =
                    groupToOffsets.computeIfAbsent(definition.get("name").getAsString(), ignored -> new ArrayList<>());
                for (final String field : new String[]{"positions", "positionsFlipped"}) {
                    if (!definition.has(field) || !definition.get(field).isJsonArray()) {
                        continue;
                    }
                    for (final org.mtr.libraries.com.google.gson.JsonElement positionEntry : definition.getAsJsonArray(field)) {
                        if (!positionEntry.isJsonObject()) {
                            continue;
                        }
                        final JsonObject position = positionEntry.getAsJsonObject();
                        offsets.add(new float[]{
                            position.has("x") ? position.get("x").getAsFloat() : 0,
                            position.has("y") ? position.get("y").getAsFloat() : 0,
                            position.has("z") ? position.get("z").getAsFloat() : 0
                        });
                    }
                }
                if (offsets.isEmpty()) {
                    offsets.add(new float[]{0, 0, 0});
                }
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("[Workbench] readInlineParts failed", e);
        }
        return groupToOffsets;
    }

    public static List<float[]> loadModelMesh(String modelResource) {
        return loadModelMesh(modelResource, null);
    }

    public static List<float[]> loadModelMesh(String modelResource, JsonObject model) {
        final java.util.Map<String, List<float[]>> inlineGroups = readInlineParts(model);
        final String cacheKey = modelResource + "|" + inlineGroups.hashCode() + "|" + inlineGroups.size();
        return MODEL_MESH_CACHE.computeIfAbsent(cacheKey, ignoredKey -> {
            final String key = modelResource;
            final java.util.Map<String, List<float[]>> groupToOffsets = inlineGroups;
            try {
                final String content = readModelContent(key);
                if (content == null || content.isBlank()) {
                    return java.util.Collections.emptyList();
                }
                final com.google.gson.JsonObject root = com.google.gson.JsonParser.parseString(content).getAsJsonObject();
                if (!root.has("elements")) {
                    return java.util.Collections.emptyList();
                }
                float textureWidth = 16;
                float textureHeight = 16;
                if (root.has("resolution")) {
                    final com.google.gson.JsonObject resolution = root.getAsJsonObject("resolution");
                    textureWidth = Math.max(1, resolution.get("width").getAsFloat());
                    textureHeight = Math.max(1, resolution.get("height").getAsFloat());
                }
                final java.util.Map<String, String> uuidToGroup = new java.util.HashMap<>();
                if (root.has("outliner")) {
                    for (final com.google.gson.JsonElement node : root.getAsJsonArray("outliner")) {
                        collectOutlinerGroups(node, null, uuidToGroup);
                    }
                }
                final List<float[]> mesh = new ArrayList<>();
                for (final com.google.gson.JsonElement element : root.getAsJsonArray("elements")) {
                    final com.google.gson.JsonObject cube = element.getAsJsonObject();
                    if (!cube.has("from") || !cube.has("to")) {
                        continue;
                    }
                    List<float[]> elementOffsets = ORIGIN_ONLY;
                    if (!groupToOffsets.isEmpty()) {
                        final String group = cube.has("uuid") ? uuidToGroup.get(cube.get("uuid").getAsString()) : null;
                        if (group == null) {
                            continue;
                        }
                        final List<float[]> resolved = groupToOffsets.get(group);
                        if (resolved == null || resolved.isEmpty()) {
                            continue;
                        }
                        elementOffsets = resolved;
                    }
                    final com.google.gson.JsonArray from = cube.getAsJsonArray("from");
                    final com.google.gson.JsonArray to = cube.getAsJsonArray("to");
                    final float x1 = from.get(0).getAsFloat();
                    final float y1 = from.get(1).getAsFloat();
                    final float z1 = from.get(2).getAsFloat();
                    final float x2 = to.get(0).getAsFloat();
                    final float y2 = to.get(1).getAsFloat();
                    final float z2 = to.get(2).getAsFloat();
                    float rotX = 0, rotY = 0, rotZ = 0, originX = 0, originY = 0, originZ = 0;
                    boolean hasRotation = false;
                    if (cube.has("rotation") && cube.get("rotation").isJsonArray()) {
                        final com.google.gson.JsonArray rotation = cube.getAsJsonArray("rotation");
                        if (rotation.size() >= 3) {
                            rotX = (float) Math.toRadians(rotation.get(0).getAsFloat());
                            rotY = (float) Math.toRadians(rotation.get(1).getAsFloat());
                            rotZ = (float) Math.toRadians(rotation.get(2).getAsFloat());
                            hasRotation = rotX != 0 || rotY != 0 || rotZ != 0;
                        }
                    }
                    if (hasRotation && cube.has("origin") && cube.get("origin").isJsonArray()) {
                        final com.google.gson.JsonArray origin = cube.getAsJsonArray("origin");
                        if (origin.size() >= 3) {
                            originX = origin.get(0).getAsFloat();
                            originY = origin.get(1).getAsFloat();
                            originZ = origin.get(2).getAsFloat();
                        }
                    }
                    final com.google.gson.JsonObject faces = cube.has("faces") ? cube.getAsJsonObject("faces") : null;
                    for (int dir = 0; dir < 6; dir++) {
                        float u1 = 0, v1 = 0, u2 = 1, v2 = 1;
                        boolean hasUv = false;
                        if (faces != null && faces.has(FACE_NAMES[dir])) {
                            final com.google.gson.JsonObject face = faces.getAsJsonObject(FACE_NAMES[dir]);
                            if (face.has("uv")) {
                                final com.google.gson.JsonArray uv = face.getAsJsonArray("uv");
                                u1 = uv.get(0).getAsFloat() / textureWidth;
                                v1 = uv.get(1).getAsFloat() / textureHeight;
                                u2 = uv.get(2).getAsFloat() / textureWidth;
                                v2 = uv.get(3).getAsFloat() / textureHeight;
                                hasUv = true;
                            }
                        }
                        if (faces != null && !faces.has(FACE_NAMES[dir])) {
                            continue;
                        }
                        final float[] quad = new float[17];
                        for (int c = 0; c < 4; c++) {
                            final float[] corner = FACE_CORNERS[dir][c];
                            float px = corner[0] == 0 ? x1 : x2;
                            float py = corner[1] == 0 ? y1 : y2;
                            float pz = corner[2] == 0 ? z1 : z2;
                            if (hasRotation) {
                                px -= originX;
                                py -= originY;
                                pz -= originZ;
                                if (rotZ != 0) {
                                    final float cos = (float) Math.cos(rotZ);
                                    final float sin = (float) Math.sin(rotZ);
                                    final float nx = px * cos - py * sin;
                                    final float ny = px * sin + py * cos;
                                    px = nx;
                                    py = ny;
                                }
                                if (rotY != 0) {
                                    final float cos = (float) Math.cos(rotY);
                                    final float sin = (float) Math.sin(rotY);
                                    final float nx = px * cos + pz * sin;
                                    final float nz = -px * sin + pz * cos;
                                    px = nx;
                                    pz = nz;
                                }
                                if (rotX != 0) {
                                    final float cos = (float) Math.cos(rotX);
                                    final float sin = (float) Math.sin(rotX);
                                    final float ny = py * cos - pz * sin;
                                    final float nz = py * sin + pz * cos;
                                    py = ny;
                                    pz = nz;
                                }
                                px += originX;
                                py += originY;
                                pz += originZ;
                            }
                            quad[c * 3] = px;
                            quad[c * 3 + 1] = py;
                            quad[c * 3 + 2] = pz;
                        }
                        quad[12] = u1;
                        quad[13] = v1;
                        quad[14] = u2;
                        quad[15] = v2;
                        quad[16] = dir + (hasUv ? 0 : 6);
                        for (final float[] offset : elementOffsets) {
                            final float[] placed;
                            if (offset[0] == 0 && offset[1] == 0 && offset[2] == 0) {
                                placed = quad;
                            } else {
                                placed = quad.clone();
                                for (int c = 0; c < 4; c++) {
                                    placed[c * 3] += offset[0];
                                    placed[c * 3 + 1] += offset[1];
                                    placed[c * 3 + 2] += offset[2];
                                }
                            }
                            mesh.add(placed);
                        }
                        if (mesh.size() >= 12000) {
                            return mesh;
                        }
                    }
                }
                return mesh;
            } catch (Exception e) {
                RTE.LOGGER.warn("[Workbench] loadModelMesh failed for {}", modelResource, e);
                return java.util.Collections.emptyList();
            }
        });
    }

    public static void clearModelBoxCache() {
        MODEL_MESH_CACHE.clear();
    }

    private static String readModelContent(String modelResource) {
        try {
            if (modelResource.startsWith(LOCAL_MODEL_PREFIX)) {
                final String sanitized = modelResource.substring(LOCAL_MODEL_PREFIX.length());
                final String fileName = MODEL_ID_TO_FILE.getOrDefault(sanitized, sanitized);
                final Path file = MODEL_DIR.resolve(fileName);
                return Files.exists(file) ? Files.readString(file, StandardCharsets.UTF_8) : null;
            }
            final String normalized = modelResource.contains(":") ? modelResource : "minecraft:" + modelResource;
            final Optional<Resource> resource = Minecraft.getInstance().getResourceManager()
                .getResource(new ResourceLocation(normalized));
            if (resource.isEmpty()) {
                return null;
            }
            try (InputStream inputStream = resource.get().open()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static int textureGlId(String textureResource) {
        try {
            final String namespaced = textureResource.contains(":") ? textureResource : "minecraft:" + textureResource;
            final String fileName = namespaced.substring(namespaced.lastIndexOf('/') + 1);
            final String normalized = fileName.contains(".") ? namespaced : namespaced + ".png";
            final net.minecraft.client.renderer.texture.AbstractTexture texture =
                Minecraft.getInstance().getTextureManager().getTexture(new ResourceLocation(normalized));
            return texture == null ? -1 : texture.getId();
        } catch (Exception e) {
            return -1;
        }
    }
}
