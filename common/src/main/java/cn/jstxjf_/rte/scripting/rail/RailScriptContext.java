package cn.jstxjf_.rte.scripting.rail;

import cn.jstxjf_.rte.gfx.math.Matrices;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.scripting.util.client.DynamicModelHolder;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import cn.jstxjf_.rte.scripting.AbstractScriptContext;
import cn.jstxjf_.rte.scripting.ScriptHolderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import org.mtr.core.data.Rail;

public final class RailScriptContext extends AbstractScriptContext {

    public final ScriptHolderBase holder;
    public final RailScriptRail rail;
    private RailScriptDrawCalls result = new RailScriptDrawCalls();
    private RailScriptDrawCalls writing = new RailScriptDrawCalls();
    private boolean alive = true;

    public RailScriptContext(ScriptHolderBase holder, Rail rail, String modelKey) {
        this.holder = holder;
        this.rail = new RailScriptRail(rail, modelKey);
    }

    public boolean isUsable() {
        return rail.isValid();
    }

    public void kill() {
        alive = false;
    }

    public synchronized void commit(Level level, DrawScheduler drawScheduler, Matrix4f viewMatrix) {
        if (result.isEmpty()) {
            return;
        }
        result.commit(level, drawScheduler, viewMatrix, rail.originX(), rail.originY(), rail.originZ());
    }

    @Override
    public synchronized void renderFunctionFinished() {
        final RailScriptDrawCalls finished = writing;
        writing = result;
        result = finished;
        writing.reset();
    }

    @Override
    public Object getWrapperObject() {
        return rail;
    }

    @Override
    public boolean isBearerAlive() {
        return alive && !disposed;
    }

    public void drawModel(ModelCluster model, Matrices poseStack) {
        writing.addModel(model, poseOf(poseStack));
    }

    public void drawModel(DynamicModelHolder model, Matrices poseStack) {
        writing.addModel(model, poseOf(poseStack));
    }

    public void drawModelAt(ModelCluster model, double distance, Matrices poseStack) {
        writing.addModel(model, poseAt(distance, poseStack));
    }

    public void drawModelAt(DynamicModelHolder model, double distance, Matrices poseStack) {
        writing.addModel(model, poseAt(distance, poseStack));
    }

    public void playSound(ResourceLocation sound, float volume, float pitch) {
        writing.addSound(cn.jstxjf_.rte.compat.McCompat.soundEvent(sound), rail.getPosition(rail.getLength() / 2), volume, pitch);
    }

    public void playSoundAt(ResourceLocation sound, double distance, float volume, float pitch) {
        writing.addSound(cn.jstxjf_.rte.compat.McCompat.soundEvent(sound), rail.getPosition(distance), volume, pitch);
    }

    private static Matrix4f poseOf(Matrices poseStack) {
        return poseStack == null ? new Matrix4f() : poseStack.getAsMatrix4f().copy();
    }

    private Matrix4f poseAt(double distance, Matrices poseStack) {
        final Matrix4f pose = rail.getPose(distance).getAsMatrix4f().copy();
        if (poseStack != null) {
            pose.multiply(poseStack.getAsMatrix4f());
        }
        return pose;
    }
}
