package cn.jstxjf_.rte.gfxext.model;

import cn.jstxjf_.rte.gfx.batch.MaterialProp;
import cn.jstxjf_.rte.gfx.model.Model;
import cn.jstxjf_.rte.gfx.util.AttrUtil;
import cn.jstxjf_.rte.gfx.util.DrawContext;
import cn.jstxjf_.rte.gfx.vertex.VertAttrMapping;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfx.vertex.VertAttrState;
import cn.jstxjf_.rte.gfx.vertex.VertAttrType;
import cn.jstxjf_.rte.gfxext.model.integration.BufferSourceProxy;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import cn.jstxjf_.rte.gfx.model.Mesh;
import java.util.function.Function;
import java.util.HashSet;
import java.util.Set;

public class RawModel {

    public ResourceLocation sourceLocation;

    public HashMap<MaterialProp, RawMesh> meshList = new HashMap<>();

    public RawModel() {

    }

    public RawModel(DataInputStream dis) throws IOException {
        int count = dis.readInt();
        for (int i = 0; i < count; i++) {
            RawMesh mesh = new RawMesh(dis);
            this.append(mesh);
        }
    }

    public Model upload(VertAttrMapping mapping) {
        Model model = new Model();
        final int limit = cn.jstxjf_.rte.ClientConfig.modelDedupeFaceLimit;
        final boolean suppress = limit > 0 && getFaceCount() > limit;
        for (RawMesh mesh : meshList.values()) {
            mesh.dedupeSuppressed = suppress;
        }
        return upload1(mapping, model);
    }

    private Model upload1(VertAttrMapping mapping, Model model) {
        for (RawMesh mesh : meshList.values()) {
            if (!mesh.faces.isEmpty()) {
                model.meshList.add(mesh.upload(mapping));
            }
        }
        return model;
    }

    public Supplier<Model> uploadAsync(VertAttrMapping mapping) { 
        Model model = new Model();
        Set<Runnable> uploadTasks = new HashSet<>();
        for (RawMesh mesh : meshList.values()) {
            if (mesh.faces.isEmpty()) continue;
            Supplier<Mesh> meshSupplier = mesh.uploadAsync(mapping);
            uploadTasks.add(() -> {
                model.meshList.add(meshSupplier.get());
            });
        }
        return () -> {
            for (Runnable task : uploadTasks) task.run();
            return model;
        };
    }

    public void append(RawMesh nextMesh) {
        if (meshList.containsKey(nextMesh.materialProp)) {
            RawMesh mesh = meshList.get(nextMesh.materialProp);
            mesh.append(nextMesh);
        } else {
            RawMesh newMesh = new RawMesh(nextMesh.materialProp);
            meshList.put(nextMesh.materialProp, newMesh);
            newMesh.append(nextMesh);
        }
    }

    public void appendTransformed(RawModel nextModel, Matrix4f mat, int color, int light) {
        for (RawMesh nextMesh : nextModel.meshList.values()) {
            if (meshList.containsKey(nextMesh.materialProp)) {
                RawMesh mesh = meshList.get(nextMesh.materialProp);
                mesh.appendTransformed(nextMesh, mat, color, light);
            } else {
                RawMesh newMesh = new RawMesh(nextMesh.materialProp);
                meshList.put(nextMesh.materialProp, newMesh);
                newMesh.appendTransformed(nextMesh, mat, color, light);
            }
        }
    }

    public int getVertexCount() {
        int result = 0;
        for (RawMesh mesh : meshList.values()) {
            result += mesh.vertices.size();
        }
        return result;
    }

    public int getFaceCount() {
        int result = 0;
        for (RawMesh mesh : meshList.values()) {
            result += mesh.faces.size();
        }
        return result;
    }

    public void append(Collection<RawMesh> nextMesh) {
        for (RawMesh mesh : nextMesh) append(mesh);
    }

    public void append(RawModel nextModel) {
        append(nextModel.meshList.values());
    }

    public void applyMatrix(Matrix4f matrix) {
        for (RawMesh mesh : meshList.values()) mesh.applyMatrix(matrix);
    }

    public void applyTranslation(float x, float y, float z) {
        for (RawMesh mesh : meshList.values()) mesh.applyTranslation(x, y, z);
    }

    public void applyRotation(Vector3f axis, float angle) {
        for (RawMesh mesh : meshList.values()) mesh.applyRotation(axis, angle);
    }

    public void applyScale(float x, float y, float z) {
        for (RawMesh mesh : meshList.values()) mesh.applyScale(x, y, z);
    }

    public void applyMirror(boolean vx, boolean vy, boolean vz, boolean nx, boolean ny, boolean nz) {
        for (RawMesh mesh : meshList.values()) mesh.applyMirror(vx, vy, vz, nx, ny, nz);
    }

    public void applyUVMirror(boolean u, boolean v) {
        for (RawMesh mesh : meshList.values()) mesh.applyUVMirror(u, v);
    }

    public void generateNormals() {
        for (RawMesh mesh : meshList.values()) mesh.generateNormals();
    }

    public void distinct() {
        for (RawMesh mesh : meshList.values()) mesh.distinct();
    }

    public void triangulate() {
        for (RawMesh mesh : meshList.values()) mesh.triangulate();
    }

    public void applyShear(Vector3f dir, Vector3f shear, float ratio) {
        for (RawMesh mesh : meshList.values()) mesh.applyShear(dir, shear, ratio);
    }

    private Map<RawMesh, MaterialProp> originalMaterialProps;

    public void setAllRenderType(String renderType) {
        if (originalMaterialProps == null) {
            originalMaterialProps = new HashMap<>();
            for (Map.Entry<MaterialProp, RawMesh> entry : meshList.entrySet()) {
                originalMaterialProps.put(entry.getValue(), entry.getKey().copy());
            }
        }
        for (Map.Entry<MaterialProp, RawMesh> entry : meshList.entrySet()) {
            if (renderType.equals("reset")) {
                MaterialProp originalProp = originalMaterialProps.get(entry.getValue());
                if (originalProp != null) {
                    entry.getValue().materialProp.copyFrom(originalProp);
                    entry.getKey().copyFrom(originalProp);
                } else {
                    entry.getValue().setRenderType(renderType);
                }
            } else {
                entry.getValue().setRenderType(renderType);
            }
            final MaterialProp applied = entry.getValue().materialProp;
            entry.getKey().shaderName = applied.shaderName;
            entry.getKey().translucent = applied.translucent;
            entry.getKey().writeDepthBuf = applied.writeDepthBuf;
            entry.getKey().cutoutHack = applied.cutoutHack;
            entry.getKey().cull = applied.cull;
            entry.getKey().attrState = applied.attrState.copy();
        }
    }

    public void replaceTexture(String oldTexture, ResourceLocation newTexture) {
        for (Map.Entry<MaterialProp, RawMesh> entry : meshList.entrySet()) {
            if (entry.getKey().texture == null) continue;
            String oldPath = entry.getKey().texture.getPath();
            if (oldPath.substring(oldPath.lastIndexOf("/") + 1).equals(oldTexture)) {
                entry.getValue().materialProp.texture = newTexture;
                entry.getKey().texture = newTexture;
            }
        }
    }

    public void replaceAllTexture(ResourceLocation newTexture) {
        for (Map.Entry<MaterialProp, RawMesh> entry : meshList.entrySet()) {
            entry.getValue().materialProp.texture = newTexture;
            entry.getKey().texture = newTexture;
        }
    }

    public void clearAttrState(VertAttrType attrType) {
        for (Map.Entry<MaterialProp, RawMesh> entry : meshList.entrySet()) {
            entry.getKey().attrState.clearAttr(attrType);
        }
    }

    public void writeBlazeBuffer(BufferSourceProxy vertexConsumers, Matrix4f matrix, int light, int overlay, DrawContext drawContext) {
        if (meshList.isEmpty()) return;
        for (Map.Entry<MaterialProp, RawMesh> entry : meshList.entrySet()) {
            RenderType renderType = entry.getKey().getBlazeRenderType();
            int resultColor = entry.getKey().attrState.color != null ? entry.getKey().attrState.color : 0xFFFFFFFF;
            int resultLight = entry.getKey().attrState.lightmapUV != null ? entry.getKey().attrState.lightmapUV : light;
            int resultOverlay = entry.getKey().attrState.overlayUV != null ? AttrUtil.exchangeLightmapUVBits(entry.getKey().attrState.overlayUV) : overlay;

            Matrix4f resultMatrix = matrix;
            
            entry.getValue().writeBlazeBuffer(vertexConsumers.getBuffer(renderType, entry.getKey().translucent),
                    resultMatrix, resultColor, resultLight, resultOverlay, drawContext);
        }
    }

    public RawModel copy() {
        RawModel result = new RawModel();
        result.sourceLocation = this.sourceLocation;
        for (RawMesh mesh : this.meshList.values()) {
            RawMesh meshCopy = mesh.copy();
            result.meshList.put(meshCopy.materialProp, meshCopy);
        }
        return result;
    }

    public RawModel copyForMaterialChanges() {
        RawModel result = new RawModel();
        result.sourceLocation = this.sourceLocation;
        for (RawMesh mesh : this.meshList.values()) {
            RawMesh meshCopy = mesh.copyForMaterialChanges();
            result.meshList.put(meshCopy.materialProp, meshCopy);
        }
        return result;
    }

    public void serializeTo(DataOutputStream dos) throws IOException {
        dos.writeInt(meshList.size());
        for (RawMesh mesh : meshList.values()) {
            mesh.serializeTo(dos);
        }
    }

    public void setMatixProcess(Function<Matrix4f, Matrix4f> matrixProcess) {
        for (RawMesh mesh : meshList.values()) {
            mesh.setMatixProcess(matrixProcess);
        }
    }
}
