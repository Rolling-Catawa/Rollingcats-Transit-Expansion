package cn.jstxjf_.rte.gfxext.reuse;

import cn.jstxjf_.rte.gfx.model.Model;
import cn.jstxjf_.rte.gfx.vertex.VertAttrMapping;
import cn.jstxjf_.rte.gfx.vertex.VertAttrSrc;
import cn.jstxjf_.rte.gfx.vertex.VertAttrType;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.loader.CsvModelLoader;
import cn.jstxjf_.rte.gfxext.model.loader.NmbModelLoader;
import cn.jstxjf_.rte.gfxext.model.loader.ObjModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModelManager {

    public HashMap<ResourceLocation, Model> uploadedModels = new HashMap<>();
    public HashMap<ResourceLocation, ModelCluster> uploadedVertArrays = new HashMap<>();
    public HashMap<ResourceLocation, RawModel> loadedRawModels = new HashMap<>();

    public int vaoCount, vboCount;

    public static final VertAttrMapping DEFAULT_MAPPING = new VertAttrMapping.Builder()
            .set(VertAttrType.POSITION, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.COLOR, VertAttrSrc.GLOBAL)
            .set(VertAttrType.UV_TEXTURE, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.UV_OVERLAY, VertAttrSrc.GLOBAL)
            .set(VertAttrType.UV_LIGHTMAP, VertAttrSrc.GLOBAL)
            .set(VertAttrType.NORMAL, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.MATRIX_MODEL, VertAttrSrc.GLOBAL)
            .build();

    public void clear() {
        vaoCount = 0;
        for (ModelCluster vertArrays : uploadedVertArrays.values()) {
            vertArrays.close();
        }
        uploadedVertArrays.clear();
        vboCount = 0;
        for (Model model : uploadedModels.values()) {
            model.close();
        }
        uploadedModels.clear();
        loadedRawModels.clear();
    }

    public int releaseLoadedRawModels() {
        final int released = loadedRawModels.size();
        loadedRawModels.clear();
        return released;
    }

    public void clearNamespace(String namespace) {
        uploadedVertArrays.entrySet().stream()
                .filter(k -> k.getKey().getNamespace().equals(namespace))
                .forEach(k -> {
                    vaoCount -= k.getValue().uploadedOpaqueParts == null ? 0 : k.getValue().uploadedOpaqueParts.meshList.size();
                    k.getValue().close();
                });
        uploadedVertArrays.keySet().removeIf(k -> k.getNamespace().equals(namespace));
        uploadedModels.entrySet().stream()
                .filter(k -> k.getKey().getNamespace().equals(namespace))
                .forEach(k -> {
                    vboCount -= k.getValue().meshList.size();
                    k.getValue().close();
                });
        uploadedModels.keySet().removeIf(k -> k.getNamespace().equals(namespace));
        loadedRawModels.keySet().removeIf(k -> k.getNamespace().equals(namespace));
    }

    public RawModel loadRawModel(ResourceManager resourceManager, ResourceLocation objLocation, AtlasManager atlasManager) throws IOException {
        if (loadedRawModels.containsKey(objLocation)) return loadedRawModels.get(objLocation);
        String crntStatExt = FilenameUtils.getExtension(objLocation.getPath());
        RawModel result;
        switch (crntStatExt) {
            case "obj":
                result = ObjModelLoader.loadModel(resourceManager, objLocation, atlasManager);
                break;
            case "csv":
                result = CsvModelLoader.loadModel(resourceManager, objLocation, atlasManager);
                break;
            case "nmb":
                result = NmbModelLoader.loadModel(resourceManager, objLocation, atlasManager);
                break;
            case "animated":
                throw new IllegalArgumentException("ANIMATED model cannot be loaded as RawModel.");
            default:
                throw new IllegalArgumentException("Unknown model format: " + resourceManager);
        };
        loadedRawModels.put(objLocation, result);
        rte$diagModel("LOAD", objLocation, result);
        return result;
    }

    private static final java.util.Set<String> RTE_MODEL_DIAG_SEEN =
        java.util.concurrent.ConcurrentHashMap.newKeySet();

    private static void rte$diagModel(String stage, ResourceLocation location, RawModel model) {
        if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics) {
            return;
        }
        final String key = stage + "|" + location;
        if (!RTE_MODEL_DIAG_SEEN.add(key)) {
            return;
        }
        final int vertices = model == null ? -1 : model.getVertexCount();
        final int faces = model == null ? -1 : model.getFaceCount();
        final int meshes = model == null || model.meshList == null ? -1 : model.meshList.size();
        if (vertices <= 0 || faces <= 0) {
            cn.jstxjf_.rte.RTE.LOGGER.warn(
                "[ModelDiag] {} {} is EMPTY: vertices={} faces={} meshes={}",
                stage, location, vertices, faces, meshes);
        } else {
            cn.jstxjf_.rte.RTE.LOGGER.info(
                "[ModelDiag] {} {} vertices={} faces={} meshes={}",
                stage, location, vertices, faces, meshes);
        }
    }

    public Map<String, RawModel> loadPartedRawModel(ResourceManager resourceManager, ResourceLocation objLocation, AtlasManager atlasManager) throws IOException {
        String crntStatExt = FilenameUtils.getExtension(objLocation.getPath());
        Map<String, RawModel> result;
        switch (crntStatExt) {
            case "obj":
                result = ObjModelLoader.loadModels(resourceManager, objLocation, atlasManager);
                break;
            case "csv":
            case "nmb":
                throw new IllegalArgumentException("CSV/NMB model cannot be loaded as parted RawModel.");
            case "animated":
                throw new IllegalArgumentException("ANIMATED model cannot be loaded as RawModel.");
            default:
                throw new IllegalArgumentException("Unknown model format: " + resourceManager);
        };
        return result;
    }

    public Model uploadModel(RawModel rawModel) {
        if (rawModel.sourceLocation == null) {
            Model result = rawModel.upload(DEFAULT_MAPPING);
            vboCount += result.meshList.size();
            return result;
        } else {
            if (uploadedModels.containsKey(rawModel.sourceLocation)) return uploadedModels.get(rawModel.sourceLocation);
            Model result = rawModel.upload(DEFAULT_MAPPING);
            vboCount += result.meshList.size();
            uploadedModels.put(rawModel.sourceLocation, result);
            return result;
        }
    }

    public ModelCluster upload(RawModel rawModel) {
        return uploadVertArrays(rawModel);
    }

    public ModelCluster uploadVertArrays(RawModel rawModel) {
        if (rawModel != null && rawModel.sourceLocation != null) {
            rte$diagModel("UPLOAD", rawModel.sourceLocation, rawModel);
        }
        if (rawModel.sourceLocation == null) {
            ModelCluster result = new ModelCluster(rawModel, DEFAULT_MAPPING, this);
            vaoCount += result.uploadedOpaqueParts == null ? 0 : result.uploadedOpaqueParts.meshList.size();
            return result;
        } else {
            if (uploadedVertArrays.containsKey(rawModel.sourceLocation)) return uploadedVertArrays.get(rawModel.sourceLocation);
            ModelCluster result = new ModelCluster(rawModel, DEFAULT_MAPPING, this);
            vaoCount += result.uploadedOpaqueParts == null ? 0 : result.uploadedOpaqueParts.meshList.size();
            uploadedVertArrays.put(rawModel.sourceLocation, result);
            return result;
        }
    }

}
