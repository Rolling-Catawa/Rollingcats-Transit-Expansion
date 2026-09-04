package cn.jstxjf_.rte.scripting;

import cn.jstxjf_.rte.scripting.util.client.DynamicModelHolder;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.*;

public abstract class AbstractDrawCalls {

    @FunctionalInterface
    public static interface DrawCall {
        void commit(DrawScheduler drawScheduler, Matrix4f basePose, Matrix4f worldPose, int light);
    }

    private static final java.util.Set<String> RTE_GEOM_TRACE = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public static abstract class DrawCallBase implements DrawCall {

        private void rte$traceGeometry(Matrix4f finalPose) {
            if (!cn.jstxjf_.rte.ClientConfig.enableRenderDiagnostics || RTE_GEOM_TRACE.size() > 256) {
                return;
            }
            try {
                final cn.jstxjf_.rte.gfx.math.Vector3f t = finalPose.getTranslationPart();
                final cn.jstxjf_.rte.gfx.math.Vector3f local = pose.getTranslationPart();
                String tex = "?";
                int meshes = 0;
                if (model != null && model.opaqueParts != null) {
                    meshes = model.opaqueParts.meshList.size();
                    for (final cn.jstxjf_.rte.gfx.batch.MaterialProp mp : model.opaqueParts.meshList.keySet()) {
                        if (mp.texture != null) {
                            tex = mp.texture.toString();
                            break;
                        }
                    }
                }
                final String key = String.format(java.util.Locale.ROOT,
                    "%s|%.2f,%.2f,%.2f", tex, local.x(), local.y(), local.z());
                if (RTE_GEOM_TRACE.add(key)) {
                    cn.jstxjf_.rte.RTE.LOGGER.info(
                        "[Geom] local=({},{},{}) world=({},{},{}) model={}",
                        String.format(java.util.Locale.ROOT, "%.2f", local.x()),
                        String.format(java.util.Locale.ROOT, "%.2f", local.y()),
                        String.format(java.util.Locale.ROOT, "%.2f", local.z()),
                        String.format(java.util.Locale.ROOT, "%.1f", t.x()),
                        String.format(java.util.Locale.ROOT, "%.1f", t.y()),
                        String.format(java.util.Locale.ROOT, "%.1f", t.z()),
                        model != null ? (meshes + "mesh " + tex) : (modelHolder != null ? "holder" : "null"));
                }
            } catch (Exception ignored) {
            }
        }

        public ModelCluster model;
        public DynamicModelHolder modelHolder;
        public Matrix4f pose;

        public DrawCallBase(ModelCluster model, Matrix4f pose) {
            this.model = model;
            this.pose = pose;
        }

        public DrawCallBase(DynamicModelHolder model, Matrix4f pose) {
            this.modelHolder = model;
            this.pose = pose;
        }

        @Override
        public void commit(DrawScheduler drawScheduler, Matrix4f basePose, Matrix4f worldPose, int light) {
            Matrix4f finalPose = selectPose(basePose, worldPose).copy();
            finalPose.multiply(pose);
            rte$traceGeometry(finalPose);
            if (model != null) {
                drawScheduler.enqueue(model, finalPose, light);
            } else if (modelHolder != null) {
                ModelCluster m = modelHolder.getUploadedModel();
                if (m != null) {
                    drawScheduler.enqueue(m, finalPose, light);
                }
            }
        }

        protected abstract Matrix4f selectPose(Matrix4f basePose, Matrix4f worldPose);
    }

    public static class ClusterDrawCall extends DrawCallBase {
        public ClusterDrawCall(ModelCluster model, Matrix4f pose) {
            super(model, pose);
        }

        public ClusterDrawCall(DynamicModelHolder model, Matrix4f pose) {
            super(model, pose);
        }

        @Override
        protected Matrix4f selectPose(Matrix4f basePose, Matrix4f worldPose) {
            return basePose;
        }
    }

    public static class WorldDrawCall extends DrawCallBase {
        public WorldDrawCall(ModelCluster model, Matrix4f pose) {
            super(model, pose);
        }

        public WorldDrawCall(DynamicModelHolder model, Matrix4f pose) {
            super(model, pose);
        }

        @Override
        protected Matrix4f selectPose(Matrix4f basePose, Matrix4f worldPose) {
            return worldPose;
        }
    }

    public static class PlaySoundCall {
        public SoundEvent sound;
        public Vector3f position;
        public float volume;
        public float pitch;

        public PlaySoundCall(SoundEvent sound, Vector3f position, float volume, float pitch) {
            this.sound = sound;
            this.position = position;
            this.volume = volume;
            this.pitch = pitch;
        }

        public void commit(ClientLevel level, Matrix4f worldPose) {
            Vector3f worldPos = worldPose.transform(position);
            level.playLocalSound(worldPos.x(), worldPos.y(), worldPos.z(),
                sound, SoundSource.BLOCKS, volume, pitch, false);
        }
    }

    public static class DrawCallMap implements DrawCall, Map<Object, DrawCall> {
        private final Map<Object, DrawCall> map = new HashMap<>();

        public DrawCallMap() {}

        public DrawCall put(Object key, DrawCall value) { return map.put(key, value); }
        public DrawCall get(Object key) { return map.get(key); }
        public DrawCall remove(Object key) { return map.remove(key); }
        public void clear() { map.clear(); }
        public boolean containsKey(Object key) { return map.containsKey(key); }
        public boolean containsValue(Object value) { return map.containsValue(value); }
        public boolean isEmpty() { return map.isEmpty(); }
        public Set<Object> keySet() { return map.keySet(); }
        public Collection<DrawCall> values() { return map.values(); }
        public Set<Map.Entry<Object, DrawCall>> entrySet() { return map.entrySet(); }
        public int hashCode() { return map.hashCode(); }
        public int size() { return map.size(); }
        public void putAll(Map<? extends Object, ? extends DrawCall> map) { this.map.putAll(map); }

        @Override
        public void commit(DrawScheduler drawScheduler, Matrix4f basePose, Matrix4f worldPose, int light) {
            for (DrawCall call : map.values()) {
                call.commit(drawScheduler, basePose, worldPose, light);
            }
        }
    }
}
