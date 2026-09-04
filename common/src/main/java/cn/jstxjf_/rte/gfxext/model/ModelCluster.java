package cn.jstxjf_.rte.gfxext.model;

import cn.jstxjf_.rte.gfx.batch.BatchManager;
import cn.jstxjf_.rte.gfx.batch.EnqueueProp;
import cn.jstxjf_.rte.gfx.batch.ShaderProp;
import cn.jstxjf_.rte.gfx.model.VertArrays;
import cn.jstxjf_.rte.gfx.util.AttrUtil;
import cn.jstxjf_.rte.gfx.util.DrawContext;
import cn.jstxjf_.rte.gfx.vertex.VertAttrMapping;
import cn.jstxjf_.rte.gfx.vertex.VertAttrState;
import cn.jstxjf_.rte.gfx.vertex.VertAttrSrc;
import cn.jstxjf_.rte.gfx.vertex.VertAttrType;
import cn.jstxjf_.rte.gfx.object.InstanceBuf;
import cn.jstxjf_.rte.gfx.object.VertBuf;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.model.integration.BufferSourceProxy;
import cn.jstxjf_.rte.gfxext.reuse.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.io.Closeable;
import java.util.function.Function;

public class ModelCluster implements Closeable {

    public final VertArrays uploadedOpaqueParts;
    public final RawModel opaqueParts;
    public final VertArrays uploadedTranslucentParts;
    public final RawModel translucentParts;

    private boolean matrixProcessed;
    private cn.jstxjf_.rte.gfx.model.Model uploadedOpaqueModel;
    private InstanceBuf instanceBuf;
    private VertArrays instancedOpaqueParts;
    private boolean instancingFailed;

    public static final VertAttrMapping INSTANCED_MAPPING = new VertAttrMapping.Builder()
            .set(VertAttrType.POSITION, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.COLOR, VertAttrSrc.INSTANCE_BUF_OR_GLOBAL)
            .set(VertAttrType.UV_TEXTURE, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.UV_OVERLAY, VertAttrSrc.GLOBAL)
            .set(VertAttrType.UV_LIGHTMAP, VertAttrSrc.INSTANCE_BUF_OR_GLOBAL)
            .set(VertAttrType.NORMAL, VertAttrSrc.VERTEX_BUF)
            .set(VertAttrType.MATRIX_MODEL, VertAttrSrc.INSTANCE_BUF)
            .build();

    public ModelCluster(RawModel source, VertAttrMapping mapping, ModelManager modelManager) {
        this.translucentParts = new RawModel();
        this.opaqueParts = new RawModel();
        for (RawMesh mesh : source.meshList.values()) {
            if (mesh.materialProp.translucent) {
                translucentParts.append(mesh);
            } else {
                opaqueParts.append(mesh);
            }
        }
        this.uploadedOpaqueModel = modelManager.uploadModel(opaqueParts);
        this.uploadedOpaqueParts = VertArrays.createAll(uploadedOpaqueModel, mapping, null);
        this.uploadedTranslucentParts = VertArrays.createAll(
                modelManager.uploadModel(translucentParts), mapping, null);
    }

    public ModelCluster(RawModel source, VertAttrMapping mapping) {
        // Untracked variant
        this.translucentParts = new RawModel();
        this.opaqueParts = new RawModel();
        for (RawMesh mesh : source.meshList.values()) {
            if (mesh.materialProp.translucent) {
                translucentParts.append(mesh);
            } else {
                opaqueParts.append(mesh);
            }
        }
        this.uploadedOpaqueModel = opaqueParts.upload(mapping);
        this.uploadedOpaqueParts = VertArrays.createAll(uploadedOpaqueModel, mapping, null);
        this.uploadedTranslucentParts = VertArrays.createAll(
                translucentParts.upload(mapping), mapping, null);
    }

    private ModelCluster(VertArrays uploadedOpaqueParts, RawModel opaqueParts, VertArrays uploadedTranslucentParts, RawModel translucentParts) {
        this.uploadedOpaqueParts = uploadedOpaqueParts;
        this.opaqueParts = opaqueParts;
        this.uploadedTranslucentParts = uploadedTranslucentParts;
        this.translucentParts = translucentParts;
    }

    public void enqueueOpaqueGl(BatchManager batchManager, Matrix4f pose, int light, int overlay, DrawContext drawContext) {
        int shaderLightmapUV = AttrUtil.exchangeLightmapUVBits(light);
        batchManager.enqueue(uploadedOpaqueParts, new EnqueueProp(
                new VertAttrState()
                        .setColor(255, 255, 255, 255).setOverlayUV(overlay)
                        .setLightmapUV(shaderLightmapUV).setModelMatrix(pose)
        ), ShaderProp.DEFAULT);
    }

    public cn.jstxjf_.rte.gfx.batch.MaterialProp opaqueMaterial() {
        return uploadedOpaqueParts.meshList.isEmpty()
            ? null : uploadedOpaqueParts.meshList.get(0).materialProp;
    }

    public boolean supportsInstancing() {
        return !instancingFailed && !matrixProcessed && uploadedOpaqueModel != null
                && !uploadedOpaqueParts.meshList.isEmpty();
    }

    public int instanceStride() {
        return INSTANCED_MAPPING.strideInstance;
    }

    public boolean enqueueOpaqueInstanced(BatchManager batchManager, java.nio.ByteBuffer instanceData,
                                          int instanceCount, ShaderProp shaderProp) {
        if (instanceCount < 1 || !supportsInstancing()) {
            return false;
        }
        try {
            if (instancedOpaqueParts == null) {
                instanceBuf = new InstanceBuf(0);
                instancedOpaqueParts = VertArrays.createAll(uploadedOpaqueModel, INSTANCED_MAPPING, instanceBuf);
            }
            instanceBuf.size = instanceCount;
            instanceBuf.upload(instanceData, VertBuf.USAGE_DYNAMIC_DRAW);
        } catch (Exception e) {
            instancingFailed = true;
            cn.jstxjf_.rte.RTE.LOGGER.warn("[ModelCluster] instancing disabled for this model: {}", e.toString());
            return false;
        }
        batchManager.enqueue(instancedOpaqueParts,
                new EnqueueProp(new VertAttrState().setOverlayUVNoOverlay()), shaderProp);
        return true;
    }

    public void enqueueOpaqueBlaze(BufferSourceProxy vertexConsumers, Matrix4f pose, int light, int overlay, DrawContext drawContext) {
        opaqueParts.writeBlazeBuffer(vertexConsumers, pose, light, overlay, drawContext);
    }

    public void enqueueTranslucentGl(BatchManager batchManager, Matrix4f matrix4f, int light, int overlay, DrawContext drawContext) {
        int shaderLightmapUV = AttrUtil.exchangeLightmapUVBits(light);
        batchManager.enqueue(uploadedTranslucentParts, new EnqueueProp(
                new VertAttrState()
                        .setColor(255, 255, 255, 255).setOverlayUV(overlay)
                        .setLightmapUV(shaderLightmapUV).setModelMatrix(matrix4f),
                cameraDistanceSquared(matrix4f)
        ), ShaderProp.DEFAULT);
    }

    private float cameraDistanceSquared(Matrix4f pose) {
        if (matrixProcessed || pose == null) {
            return Float.NaN;
        }
        final Vector3f offset = pose.getTranslationPart();
        if (offset == null) {
            return Float.NaN;
        }
        return offset.x() * offset.x() + offset.y() * offset.y() + offset.z() * offset.z();
    }

    public void enqueueTranslucentBlaze(BufferSourceProxy vertexConsumers, Matrix4f pose, int light, int overlay, DrawContext drawContext) {
        translucentParts.writeBlazeBuffer(vertexConsumers, pose, light, overlay, drawContext);
    }

    public void setMatixProcess(Function<Matrix4f, Matrix4f> matrixProcess) {
        matrixProcessed = matrixProcess != null;
        opaqueParts.setMatixProcess(matrixProcess);
        translucentParts.setMatixProcess(matrixProcess);
        uploadedOpaqueParts.setMatixProcess(matrixProcess);
        uploadedTranslucentParts.setMatixProcess(matrixProcess);
    }

    @Override
    public void close() {
        if (instancedOpaqueParts != null) instancedOpaqueParts.close();
        if (instanceBuf != null) instanceBuf.close();
        uploadedOpaqueParts.close();
        uploadedTranslucentParts.close();
    }


    public void replaceTexture(String oldTexture, ResourceLocation newTexture) {
        uploadedOpaqueParts.replaceTexture(oldTexture, newTexture);
        opaqueParts.replaceTexture(oldTexture, newTexture);
        uploadedTranslucentParts.replaceTexture(oldTexture, newTexture);
        translucentParts.replaceTexture(oldTexture, newTexture);
    }

    public void replaceAllTexture(ResourceLocation newTexture) {
        uploadedOpaqueParts.replaceAllTexture(newTexture);
        opaqueParts.replaceAllTexture(newTexture);
        uploadedTranslucentParts.replaceAllTexture(newTexture);
        translucentParts.replaceAllTexture(newTexture);
    }

    public ModelCluster copyForMaterialChanges() {
        return new ModelCluster(
                uploadedOpaqueParts.copyForMaterialChanges(),
                opaqueParts.copyForMaterialChanges(),
                uploadedTranslucentParts.copyForMaterialChanges(),
                translucentParts.copyForMaterialChanges()
        );
    }
}
