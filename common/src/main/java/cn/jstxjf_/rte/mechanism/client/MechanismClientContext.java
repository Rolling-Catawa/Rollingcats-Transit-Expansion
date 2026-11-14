package cn.jstxjf_.rte.mechanism.client;

import cn.jstxjf_.rte.gfx.math.Matrices;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import cn.jstxjf_.rte.mechanism.RailPose;
import cn.jstxjf_.rte.scripting.AbstractScriptContext;
import cn.jstxjf_.rte.scripting.eyecandy.EyeCandyDrawCalls;
import cn.jstxjf_.rte.scripting.util.client.DynamicModelHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class MechanismClientContext extends AbstractScriptContext {

    public final ClientMechanisms.Entry entry;
    private EyeCandyDrawCalls moving = new EyeCandyDrawCalls();
    private EyeCandyDrawCalls fixed = new EyeCandyDrawCalls();
    private EyeCandyDrawCalls movingWriting = new EyeCandyDrawCalls();
    private EyeCandyDrawCalls fixedWriting = new EyeCandyDrawCalls();
    public boolean alive = true;

    MechanismClientContext(ClientMechanisms.Entry entry) {
        this.entry = entry;
    }

    public synchronized void commit(DrawScheduler drawScheduler, Matrix4f movingPose, Matrix4f fixedPose, Matrix4f worldPose, int light) {
        moving.commit(drawScheduler, movingPose, worldPose, light);
        fixed.commit(drawScheduler, fixedPose, worldPose, light);
    }

    @Override
    public synchronized void renderFunctionFinished() {
        EyeCandyDrawCalls temp = movingWriting;
        movingWriting = moving;
        moving = temp;
        movingWriting.reset();
        temp = fixedWriting;
        fixedWriting = fixed;
        fixed = temp;
        fixedWriting.reset();
    }

    @Override
    public Object getWrapperObject() {
        return entry.view;
    }

    @Override
    public boolean isBearerAlive() {
        return alive;
    }

    public void drawModel(ModelCluster model, Matrices poseStack) {
        movingWriting.addModel(model, poseOf(poseStack));
    }

    public void drawModel(DynamicModelHolder model, Matrices poseStack) {
        movingWriting.addModel(model, poseOf(poseStack));
    }

    public void drawFixedModel(ModelCluster model, Matrices poseStack) {
        fixedWriting.addModel(model, poseOf(poseStack));
    }

    public void drawFixedModel(DynamicModelHolder model, Matrices poseStack) {
        fixedWriting.addModel(model, poseOf(poseStack));
    }

    public void drawRailModel(String railId, ModelCluster model, Matrices poseStack) {
        final Matrix4f pose = railPose(railId, poseStack);
        if (pose == null) {
            return;
        }
        (railId.equals(entry.deckRail()) ? movingWriting : fixedWriting).addModel(model, pose);
    }

    public void drawRailModel(String railId, DynamicModelHolder model, Matrices poseStack) {
        final Matrix4f pose = railPose(railId, poseStack);
        if (pose == null) {
            return;
        }
        (railId.equals(entry.deckRail()) ? movingWriting : fixedWriting).addModel(model, pose);
    }

    private static Matrix4f poseOf(Matrices poseStack) {
        return poseStack == null ? Matrix4f.IDENTITY : poseStack.getAsMatrix4f().copy();
    }

    private Matrix4f railPose(String railId, Matrices poseStack) {
        final RailPose origin = entry.origin();
        final RailPose rail = entry.pose(railId);
        if (origin == null || rail == null) {
            return null;
        }
        final Matrix4f pose = new Matrix4f();
        pose.translate((float) (rail.midX() - origin.midX()), (float) (rail.midY() - origin.midY()), (float) (rail.midZ() - origin.midZ()));
        pose.rotateY((float) Math.toRadians(-rail.yawDegrees()));
        if (poseStack != null) {
            pose.multiply(poseStack.getAsMatrix4f());
        }
        return pose;
    }

    public void playSound(ResourceLocation sound, float volume, float pitch) {
        movingWriting.addSound(cn.jstxjf_.rte.compat.McCompat.soundEvent(sound), volume, pitch);
    }

    public String syncedState() {
        return entry.synced();
    }

    public long now() {
        return System.currentTimeMillis();
    }
}
