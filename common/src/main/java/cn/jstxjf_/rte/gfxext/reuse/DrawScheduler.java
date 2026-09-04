package cn.jstxjf_.rte.gfxext.reuse;

import cn.jstxjf_.rte.gfx.batch.BatchManager;
import cn.jstxjf_.rte.gfx.shader.ShaderManager;
import cn.jstxjf_.rte.gfx.util.GlStateTracker;
import cn.jstxjf_.rte.gfx.util.DrawContext;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.integration.BufferSourceProxy;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.Map;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.minecraft.world.phys.Vec3;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.gfx.batch.ShaderProp;
import cn.jstxjf_.rte.gfx.util.AttrUtil;
import cn.jstxjf_.rte.gfx.util.OffHeapAllocator;
import cn.jstxjf_.rte.ClientConfig;

public class DrawScheduler {

    private static final int INSTANCE_THRESHOLD = 2;

    public final BatchManager batchManager = new BatchManager();
    public final ShaderManager shaderManager = new ShaderManager();
    private final List<ClusterDrawCall> clusterDrawCalls = new ArrayList<>();
    private final ShaderProp cachedWorldSpaceProp = new ShaderProp();
    private double cachedCamX = Double.NaN, cachedCamY = Double.NaN, cachedCamZ = Double.NaN;
    private final List<BlazeDrawCall> blazeDrawCalls = new ArrayList<>();

    public void reloadShaders(ResourceManager resourceManager) throws IOException {
        shaderManager.reloadShaders(resourceManager);
    }

    public void enqueue(ModelCluster model, Matrix4f pose, int light) {
        clusterDrawCalls.add(new ClusterDrawCall(model, pose, light));
    }

    public void enqueue(ModelCluster model, Matrix4f pose, int light, int overlay) {
        clusterDrawCalls.add(new ClusterDrawCall(model, pose, light, overlay));
    }

    public void enqueue(RawModel model, Matrix4f pose, int light) {
        blazeDrawCalls.add(new BlazeDrawCall(model, pose, light));
    }

    public void enqueue(RawModel model, Matrix4f pose, int light, int overlay) {
        blazeDrawCalls.add(new BlazeDrawCall(model, pose, light, overlay));
    }

    public void commit(BufferSourceProxy vertexConsumers, DrawContext drawContext) {
        if (clusterDrawCalls.isEmpty() && blazeDrawCalls.isEmpty()) return;
        boolean restoreGlState = false;
        try {
            boolean useBlaze = drawContext.drawWithBlaze || !shaderManager.isReady();
            if (!useBlaze && !GlStateTracker.isStateProtected) {
                GlStateTracker.capture();
                restoreGlState = true;
            }
            boolean needBlazeCamera = useBlaze || drawContext.sortTranslucentFaces || !blazeDrawCalls.isEmpty();
            Matrix4f blazeCam = needBlazeCamera ? computeBlazeCameraMatrix() : null;
            if (useBlaze) {
                for (ClusterDrawCall drawCall : clusterDrawCalls)
                    drawCall.model.enqueueOpaqueBlaze(vertexConsumers, withCamera(blazeCam, drawCall.pose), drawCall.light, drawCall.overlay, drawContext);
            } else {
                enqueueOpaqueGrouped(drawContext);
            }
            final boolean cpuFaceSort = drawContext.sortTranslucentFaces
                    && !ClientConfig.gpuTranslucentRendering;
            if (useBlaze || cpuFaceSort) {
                for (ClusterDrawCall drawCall : clusterDrawCalls)
                    drawCall.model.enqueueTranslucentBlaze(vertexConsumers, withCamera(blazeCam, drawCall.pose), drawCall.light, drawCall.overlay, drawContext);
            } else {
                for (ClusterDrawCall drawCall : clusterDrawCalls)
                    drawCall.model.enqueueTranslucentGl(batchManager, drawCall.pose, drawCall.light, drawCall.overlay, drawContext);
            }
            for (BlazeDrawCall drawCall : blazeDrawCalls)
                drawCall.model.writeBlazeBuffer(vertexConsumers, withCamera(blazeCam, drawCall.pose), drawCall.light, drawCall.overlay, drawContext);
            if (!useBlaze) {
                commitRaw(drawContext);
            }
        } finally {
            if (restoreGlState) {
                GlStateTracker.restore();
            }
            clusterDrawCalls.clear();
            blazeDrawCalls.clear();
        }
    }

    public void commitRaw(DrawContext drawContext) {
        batchManager.drawAll(shaderManager, drawContext);
    }

    private void enqueueOpaqueGrouped(DrawContext drawContext) {
        final IdentityHashMap<ModelCluster, List<ClusterDrawCall>> grouped = new IdentityHashMap<>();
        for (ClusterDrawCall drawCall : clusterDrawCalls) {
            grouped.computeIfAbsent(drawCall.model, key -> new ArrayList<>()).add(drawCall);
        }
        Vec3 camPos = null;
        ShaderProp worldSpaceProp = null;
        for (Map.Entry<ModelCluster, List<ClusterDrawCall>> entry : grouped.entrySet()) {
            final ModelCluster model = entry.getKey();
            final List<ClusterDrawCall> calls = entry.getValue();
            if (calls.size() < INSTANCE_THRESHOLD || !model.supportsInstancing()
                    || !shaderManager.hasInstancedShader(model.opaqueMaterial())) {
                for (ClusterDrawCall drawCall : calls) {
                    model.enqueueOpaqueGl(batchManager, drawCall.pose, drawCall.light, drawCall.overlay, drawContext);
                }
                continue;
            }
            if (camPos == null) {
                camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                if (camPos.x != cachedCamX || camPos.y != cachedCamY || camPos.z != cachedCamZ) {
                    cachedCamX = camPos.x;
                    cachedCamY = camPos.y;
                    cachedCamZ = camPos.z;
                    cachedWorldSpaceProp.setViewMatrix(
                        Matrix4f.translation(-(float) camPos.x, -(float) camPos.y, -(float) camPos.z));
                }
                worldSpaceProp = cachedWorldSpaceProp;
            }
            final ByteBuffer instanceData = packInstances(model, calls, camPos);
            if (instanceData == null) {
                for (ClusterDrawCall drawCall : calls) {
                    model.enqueueOpaqueGl(batchManager, drawCall.pose, drawCall.light, drawCall.overlay, drawContext);
                }
                continue;
            }
            final boolean instanced = model.enqueueOpaqueInstanced(batchManager, instanceData, calls.size(), worldSpaceProp);
            OffHeapAllocator.free(instanceData);
            if (!instanced) {
                for (ClusterDrawCall drawCall : calls) {
                    model.enqueueOpaqueGl(batchManager, drawCall.pose, drawCall.light, drawCall.overlay, drawContext);
                }
            }
        }
    }

    private static ByteBuffer packInstances(ModelCluster model, List<ClusterDrawCall> calls, Vec3 camPos) {
        final int stride = model.instanceStride();
        ByteBuffer buffer = null;
        try {
            buffer = OffHeapAllocator.allocate(calls.size() * stride);
            buffer.order(ByteOrder.nativeOrder());
            final float[] matrixValues = new float[16];
            for (ClusterDrawCall drawCall : calls) {
                final int base = buffer.position();
                buffer.putInt(0xFFFFFFFF);
                final int lightmapUV = AttrUtil.exchangeLightmapUVBits(drawCall.light);
                buffer.putShort((short) (lightmapUV >>> 16));
                buffer.putShort((short) lightmapUV);
                final Matrix4f worldPose = Matrix4f.translation((float) camPos.x, (float) camPos.y, (float) camPos.z);
                worldPose.multiply(drawCall.pose);
                worldPose.asMoj().get(matrixValues);
                for (float value : matrixValues) {
                    buffer.putFloat(value);
                }
                buffer.position(base + stride);
            }
            buffer.rewind();
            return buffer;
        } catch (Exception e) {
            if (buffer != null) OffHeapAllocator.free(buffer);
            RTE.LOGGER.warn("[DrawScheduler] instance packing failed: {}", e.toString());
            return null;
        }
    }

    public void drawAlone(ModelCluster model, Matrix4f pose, int light, BufferSourceProxy vertexConsumers, DrawContext drawContext) {
        drawAlone(model, pose, light, OverlayTexture.NO_OVERLAY, vertexConsumers, drawContext);
    }

    public void drawAlone(ModelCluster model, Matrix4f pose, int light, int overlay, BufferSourceProxy vertexConsumers, DrawContext drawContext) {
        BatchManager batchManager = new BatchManager();
        boolean useBlaze = drawContext.drawWithBlaze || !shaderManager.isReady();
        Matrix4f blazePose = drawContext.screenSpace
            ? pose
            : ((useBlaze || drawContext.sortTranslucentFaces) ? withCamera(computeBlazeCameraMatrix(), pose) : pose);
        if (useBlaze) {
            model.enqueueOpaqueBlaze(vertexConsumers, blazePose, light, overlay, drawContext);
        } else {
            model.enqueueOpaqueGl(batchManager, pose, light, overlay, drawContext);
        }
        final boolean cpuFaceSort = drawContext.sortTranslucentFaces
                && !ClientConfig.gpuTranslucentRendering;
        if (useBlaze || cpuFaceSort) {
            model.enqueueTranslucentBlaze(vertexConsumers, blazePose, light, overlay, drawContext);
        } else {
            model.enqueueTranslucentGl(batchManager, pose, light, overlay, drawContext);
        }
        if (!useBlaze) {
            final boolean needProtection = !GlStateTracker.isStateProtected;
            if (needProtection) GlStateTracker.capture();
            try {
                batchManager.drawAll(shaderManager, drawContext);
            } finally {
                if (needProtection) GlStateTracker.restore();
            }
        }
        vertexConsumers.commit();
    }

    private static Matrix4f computeBlazeCameraMatrix() {
        return cn.jstxjf_.rte.render.CameraPose.copyOrFallback();
    }

    private static Matrix4f withCamera(Matrix4f cameraMatrix, Matrix4f pose) {
        Matrix4f result = cameraMatrix.copy();
        result.multiply(pose);
        return result;
    }

    private static class ClusterDrawCall {
        public ModelCluster model;
        public Matrix4f pose;
        public int light;
        public int overlay;

        public ClusterDrawCall(ModelCluster model, Matrix4f pose, int light) {
            this.model = model;
            this.pose = pose;
            this.light = light;
            this.overlay = OverlayTexture.NO_OVERLAY;
        }

        public ClusterDrawCall(ModelCluster model, Matrix4f pose, int light, int overlay) {
            this.model = model;
            this.pose = pose;
            this.light = light;
            this.overlay = overlay;
        }
    }

    private static class BlazeDrawCall {
        public RawModel model;
        public Matrix4f pose;
        public int light;
        public int overlay;

        public BlazeDrawCall(RawModel model, Matrix4f pose, int light) {
            this.model = model;
            this.pose = pose;
            this.light = light;
            this.overlay = OverlayTexture.NO_OVERLAY;
        }

        public BlazeDrawCall(RawModel model, Matrix4f pose, int light, int overlay) {
            this.model = model;
            this.pose = pose;
            this.light = light;
            this.overlay = overlay;
        }
    }
}
