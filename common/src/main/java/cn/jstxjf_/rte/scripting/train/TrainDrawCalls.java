package cn.jstxjf_.rte.scripting.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.scripting.AbstractDrawCalls;
import cn.jstxjf_.rte.scripting.util.client.DynamicModelHolder;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import org.mtr.core.tool.Vector;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MoreRenderLayers;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mapping.holder.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainDrawCalls extends AbstractDrawCalls {

    private final List<ClusterDrawCall>[] carDrawLists;
    private final List<ClusterDrawCall>[] connDrawLists;
    private final Identifier[] connStretchTextures;
    private final List<PlaySoundCall>[] carSoundLists;

    @SuppressWarnings("unchecked")
    public TrainDrawCalls(int carCount) {
        carDrawLists = new List[carCount];
        Arrays.setAll(carDrawLists, ignored -> new ArrayList<>());
        
        if (carCount > 1) {
            connDrawLists = new List[carCount - 1];
            Arrays.setAll(connDrawLists, ignored -> new ArrayList<>());
            connStretchTextures = new Identifier[carCount - 1];
        } else {
            connDrawLists = new List[0];
            connStretchTextures = new Identifier[0];
        }
        
        carSoundLists = new List[carCount];
        Arrays.setAll(carSoundLists, ignored -> new ArrayList<>());
    }

    public void addCarModel(int car, ModelCluster model, Matrix4f pose) {
        if (car >= 0 && car < carDrawLists.length)
             carDrawLists[car].add(new ClusterDrawCall(model, pose));
    }

    public void addCarModel(int car, DynamicModelHolder model, Matrix4f pose) {
        if (car >= 0 && car < carDrawLists.length)
            carDrawLists[car].add(new ClusterDrawCall(model, pose));
    }

    public void addCarSound(int car, SoundEvent sound, Vector3f position, float volume, float pitch) {
        if (!ClientConfig.enableTrainSound) {
            return;
        }
        if (car >= 0 && car < carSoundLists.length) {
            carSoundLists[car].add(new PlaySoundCall(sound, position, volume, pitch));
        }
    }


    public void commitCar(int car, DrawScheduler drawScheduler, Matrix4f basePose, Matrix4f worldPose, int light) {
        if (car >= 0 && car < carDrawLists.length) {
            for (ClusterDrawCall clusterDrawCall : carDrawLists[car]) {
                clusterDrawCall.commit(drawScheduler, basePose, worldPose, light);
            }
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null || !ClientConfig.enableTrainSound) return;
            for (PlaySoundCall playSoundCall : carSoundLists[car]) {
                playSoundCall.commit(level, basePose);
            }
        }
    }

    public void addConnModel(int car, ModelCluster model, Matrix4f pose) {
        if (car >= 0 && car < connDrawLists.length) {
            connDrawLists[car].add(new ClusterDrawCall(model, pose));
        }
    }

    public void addConnModel(int car, DynamicModelHolder model, Matrix4f pose) {
        if (car >= 0 && car < connDrawLists.length) {
            connDrawLists[car].add(new ClusterDrawCall(model, pose));
        }
    }

    public void drawConnStretchTexture(int car, Identifier texture) {
        if (car >= 0 && car < connStretchTextures.length)
            connStretchTextures[car] = texture;
    }

    public void commitConn(int car, DrawScheduler drawScheduler, Matrix4f basePose, Matrix4f worldPose, int light) {
        if (car >= 0 && car < connDrawLists.length) {
            for (ClusterDrawCall clusterDrawCall : connDrawLists[car]) {
                clusterDrawCall.commit(drawScheduler, basePose, worldPose, light);
            }
        }
    }

    public void commitConnStretchTexture(int car, boolean useOffset, Vector3d zeroVector, int exteriorLight, int interiorLight,
                                         Vector prevPos1, Vector prevPos2, Vector prevPos3, Vector prevPos4,
                                         Vector thisPos1, Vector thisPos2, Vector thisPos3, Vector thisPos4) {
        if (car < 0 || car >= connStretchTextures.length || connStretchTextures[car] == null) {
            return;
        }

        final Identifier texture = connStretchTextures[car];
        MainRenderer.scheduleRender(texture, false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
            final Vector3d renderOffset = useOffset ? offset : zeroVector;
            drawScheduledTexture(graphicsHolder, thisPos2, prevPos3, prevPos4, thisPos1, renderOffset, 0, 0, 0.5F, 0.5F, exteriorLight);
            drawScheduledTexture(graphicsHolder, prevPos2, thisPos3, thisPos4, prevPos1, renderOffset, 0, 0, 0.5F, 0.5F, exteriorLight);
            drawScheduledTexture(graphicsHolder, prevPos3, thisPos2, thisPos3, prevPos2, renderOffset, 0, 0, 0.5F, 0.5F, exteriorLight);
            drawScheduledTexture(graphicsHolder, prevPos1, thisPos4, thisPos1, prevPos4, renderOffset, 0, 0, 0.5F, 0.5F, exteriorLight);
            drawScheduledTexture(graphicsHolder, thisPos3, prevPos2, prevPos1, thisPos4, renderOffset, 0.5F, 0, 1F, 0.5F, interiorLight);
            drawScheduledTexture(graphicsHolder, prevPos3, thisPos2, thisPos1, prevPos4, renderOffset, 0.5F, 0, 1F, 0.5F, interiorLight);
            drawScheduledTexture(graphicsHolder, prevPos2, thisPos3, thisPos2, prevPos3, renderOffset, 0, 0.5F, 0.5F, 1F, interiorLight);
            drawScheduledTexture(graphicsHolder, prevPos4, thisPos1, thisPos4, prevPos1, renderOffset, 0.5F, 0.5F, 1F, 1F, interiorLight);
        });
    }

    public void commitConnImmediate(int car, PoseStack matrices, MultiBufferSource vertexConsumers,
                                    Vec3 prevPos1, Vec3 prevPos2, Vec3 prevPos3, Vec3 prevPos4,
                                    Vec3 thisPos1, Vec3 thisPos2, Vec3 thisPos3, Vec3 thisPos4, int light) {
        if (car < 0 || car >= connStretchTextures.length || connStretchTextures[car] == null) {
            return;
        }

        final VertexConsumer exterior = vertexConsumers.getBuffer(MoreRenderLayers.getExterior(connStretchTextures[car]).data);
        drawTexture(matrices, exterior, thisPos2, prevPos3, prevPos4, thisPos1, 0, 0, 0.5F, 0.5F, light);
        drawTexture(matrices, exterior, prevPos2, thisPos3, thisPos4, prevPos1, 0, 0, 0.5F, 0.5F, light);
        drawTexture(matrices, exterior, prevPos3, thisPos2, thisPos3, prevPos2, 0, 0, 0.5F, 0.5F, light);
        drawTexture(matrices, exterior, prevPos1, thisPos4, thisPos1, prevPos4, 0, 0, 0.5F, 0.5F, light);

        final int emissiveLight = LightTexture.FULL_BRIGHT;
        final VertexConsumer interior = vertexConsumers.getBuffer(MoreRenderLayers.getInterior(connStretchTextures[car]).data);
        drawTexture(matrices, interior, thisPos3, prevPos2, prevPos1, thisPos4, 0.5F, 0, 1F, 0.5F, emissiveLight);
        drawTexture(matrices, interior, prevPos3, thisPos2, thisPos1, prevPos4, 0.5F, 0, 1F, 0.5F, emissiveLight);
        drawTexture(matrices, interior, prevPos2, thisPos3, thisPos2, prevPos3, 0, 0.5F, 0.5F, 1F, emissiveLight);
        drawTexture(matrices, interior, prevPos4, thisPos1, thisPos4, prevPos1, 0.5F, 0.5F, 1F, 1F, emissiveLight);
    }

    private static void drawTexture(PoseStack matrices, VertexConsumer vertexConsumer,
                                    Vec3 pos1, Vec3 pos2, Vec3 pos3, Vec3 pos4,
                                    float u1, float v1, float u2, float v2, int light) {
        final PoseStack.Pose pose = matrices.last();
        final net.minecraft.world.phys.Vec3 edge1 = pos2.subtract(pos1);
        final net.minecraft.world.phys.Vec3 edge2 = pos4.subtract(pos1);
        final net.minecraft.world.phys.Vec3 normal = edge1.cross(edge2).normalize();
        final float nx = (float) normal.x;
        final float ny = (float) normal.y;
        final float nz = (float) normal.z;

        putVertex(vertexConsumer, pose, pos1, u1, v1, light, nx, ny, nz);
        putVertex(vertexConsumer, pose, pos2, u1, v2, light, nx, ny, nz);
        putVertex(vertexConsumer, pose, pos3, u2, v2, light, nx, ny, nz);
        putVertex(vertexConsumer, pose, pos4, u2, v1, light, nx, ny, nz);
    }

    private static void drawScheduledTexture(org.mtr.mapping.mapper.GraphicsHolder graphicsHolder,
                                             Vector pos1, Vector pos2, Vector pos3, Vector pos4,
                                             Vector3d offset, float u1, float v1, float u2, float v2, int light) {
        IDrawing.drawTexture(
            graphicsHolder,
            pos1.x, pos1.y, pos1.z,
            pos2.x, pos2.y, pos2.z,
            pos3.x, pos3.y, pos3.z,
            pos4.x, pos4.y, pos4.z,
            offset, u1, v1, u2, v2, Direction.UP, IGui.ARGB_WHITE, light
        );
    }

    private static void putVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, Vec3 position,
                                  float u, float v, int light, float nx, float ny, float nz) {
        vertexConsumer.vertex(pose.pose(), (float) position.x, (float) position.y, (float) position.z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }

    public void reset() {
        for (List<ClusterDrawCall> list : carDrawLists) list.clear();
        for (List<ClusterDrawCall> list : connDrawLists) list.clear();
        for (List<PlaySoundCall> list : carSoundLists) list.clear();
        Arrays.fill(connStretchTextures, null);
    }

    public String describeDrawCalls() {
        final StringBuilder sb = new StringBuilder("cars=[");
        for (int i = 0; i < carDrawLists.length; i++) {
            if (i > 0) sb.append(',');
            int uploaded = 0;
            for (ClusterDrawCall call : carDrawLists[i]) {
                if (call.model != null || (call.modelHolder != null && call.modelHolder.getUploadedModel() != null)) {
                    uploaded++;
                }
            }
            sb.append(carDrawLists[i].size()).append('/').append(uploaded);
        }
        sb.append("] conns=[");
        for (int i = 0; i < connDrawLists.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(connDrawLists[i].size());
        }
        sb.append(']');
        return sb.toString();
    }
}
