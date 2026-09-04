package cn.jstxjf_.rte.scripting.eyecandy;

import cn.jstxjf_.rte.block.BlockEyeCandy;
import cn.jstxjf_.rte.scripting.AbstractDrawCalls.DrawCallMap;
import cn.jstxjf_.rte.scripting.AbstractScriptContext;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import cn.jstxjf_.rte.gfx.math.Matrices;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.scripting.util.client.DynamicModelHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class EyeCandyScriptContext extends AbstractScriptContext {

    public BlockEyeCandy.BlockEntityEyeCandy entity;
    public EyeCandyDrawCalls scriptResult;
    private EyeCandyDrawCalls scriptResultWriting;
    public DrawCallMap drawCalls = new DrawCallMap();
    public boolean disposeForReload = false;

    public EyeCandyScriptContext(BlockEyeCandy.BlockEntityEyeCandy entity) {
        scriptResult = new EyeCandyDrawCalls();
        scriptResultWriting = new EyeCandyDrawCalls();
        this.entity = entity;
    }

    public void commit(DrawScheduler drawScheduler, Matrix4f basePose, Matrix4f worldPose, int light) {
        drawCalls.commit(drawScheduler, basePose, worldPose, light);
        scriptResult.commit(drawScheduler, basePose, worldPose, light);
    }

    @Override
    public void renderFunctionFinished() {
        synchronized (this) {
            EyeCandyDrawCalls temp = scriptResultWriting;
            scriptResultWriting = scriptResult;
            scriptResult = temp;
            scriptResultWriting.reset();
        }
    }

    @Override
    public Object getWrapperObject() {
        return entity;
    }

    @Override
    public boolean isBearerAlive() {
        return !disposeForReload && !entity.isRemoved2() && !disposed && entity.scriptContext == this;
    }

    public void drawModel(ModelCluster model, Matrices poseStack) {
        scriptResultWriting.addModel(model, poseStack == null ? Matrix4f.IDENTITY : poseStack.getAsMatrix4f().copy());
    }

    public void drawModel(DynamicModelHolder model, Matrices poseStack) {
        scriptResultWriting.addModel(model, poseStack == null ? Matrix4f.IDENTITY : poseStack.getAsMatrix4f().copy());
    }

    public void playSound(ResourceLocation sound, float volume, float pitch) {
        scriptResultWriting.addSound(
            SoundEvent.createVariableRangeEvent(sound),
            volume, pitch
        );
    }
}
