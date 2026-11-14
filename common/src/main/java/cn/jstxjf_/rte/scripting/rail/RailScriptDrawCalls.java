package cn.jstxjf_.rte.scripting.rail;

import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.scripting.util.client.DynamicModelHolder;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import cn.jstxjf_.rte.scripting.AbstractDrawCalls;
import cn.jstxjf_.rte.util.LightSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class RailScriptDrawCalls {

    private record Sound(SoundEvent sound, Vector3f position, float volume, float pitch) {
    }

    private final List<AbstractDrawCalls.ClusterDrawCall> draws = new ArrayList<>();
    private final List<Sound> sounds = new ArrayList<>();

    void addModel(ModelCluster model, Matrix4f pose) {
        draws.add(new AbstractDrawCalls.ClusterDrawCall(model, pose));
    }

    void addModel(DynamicModelHolder model, Matrix4f pose) {
        draws.add(new AbstractDrawCalls.ClusterDrawCall(model, pose));
    }

    void addSound(SoundEvent sound, Vector3f position, float volume, float pitch) {
        sounds.add(new Sound(sound, position, volume, pitch));
    }

    boolean isEmpty() {
        return draws.isEmpty() && sounds.isEmpty();
    }

    void commit(Level level, DrawScheduler drawScheduler, Matrix4f viewMatrix, double originX, double originY, double originZ) {
        final Matrix4f basePose = viewMatrix.copy();
        basePose.translate((float) originX, (float) originY, (float) originZ);
        final Map<Long, Integer> lightCache = new HashMap<>();
        for (final AbstractDrawCalls.ClusterDrawCall draw : draws) {
            final Vector3f local = draw.pose.getTranslationPart();
            final BlockPos pos = cn.jstxjf_.rte.compat.McCompat.blockPos(originX + local.x(), originY + local.y() + 0.5, originZ + local.z());
            final int light = lightCache.computeIfAbsent(pos.asLong(), ignored -> LightSampler.packed(level, pos));
            draw.commit(drawScheduler, basePose, viewMatrix, light);
        }
        for (final Sound sound : sounds) {
            level.playLocalSound(originX + sound.position.x(), originY + sound.position.y(), originZ + sound.position.z(),
                sound.sound, SoundSource.BLOCKS, sound.volume, sound.pitch, false);
        }
        sounds.clear();
    }

    void reset() {
        draws.clear();
        sounds.clear();
    }
}
