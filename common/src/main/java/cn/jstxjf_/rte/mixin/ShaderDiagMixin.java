package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RenderDiagnostics;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.shader.ShaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShaderManager.class, remap = false)
public abstract class ShaderDiagMixin {

    @Inject(method = "setupShaderBatchState", at = @At("HEAD"), require = 0)
    private void rte$diagShader(MaterialProperties materialProperties, CallbackInfo ci) {
        if (!RenderDiagnostics.allow()) return;
        try {
            final java.lang.reflect.Field stf = MaterialProperties.class.getDeclaredField("shaderType");
            stf.setAccessible(true);
            final Object shaderType = stf.get(materialProperties);
            final java.lang.reflect.Method gsn = ShaderManager.class.getDeclaredMethod("getShaderName", shaderType.getClass());
            gsn.setAccessible(true);
            final String name = (String) gsn.invoke(null, shaderType);
            final java.lang.reflect.Field sf = ShaderManager.class.getDeclaredField("shaders");
            sf.setAccessible(true);
            final java.util.Map<?, ?> map = (java.util.Map<?, ?>) sf.get(this);
            final Object resolved = map.get(name);
            int wantProgram = -1;
            if (resolved != null) {
                final java.lang.reflect.Field pf = net.minecraft.client.renderer.ShaderInstance.class.getDeclaredField("programId");
                pf.setAccessible(true);
                wantProgram = pf.getInt(resolved);
            }
            final java.lang.reflect.Field lpf = net.minecraft.client.renderer.ShaderInstance.class.getDeclaredField("lastProgramId");
            lpf.setAccessible(true);
            final int lastProgramId = lpf.getInt(null);
            final int[] bound = new int[1];
            org.lwjgl.opengl.GL33.glGetIntegerv(0x8B8D, bound);
            RenderDiagnostics.log("SHADER name={} mtrWantsProgram={} vanillaLastProgramId={} currentlyBound={} hasModelMat={}",
                name, wantProgram, lastProgramId, bound[0],
                wantProgram < 0 ? "n/a" : org.lwjgl.opengl.GL33.glGetAttribLocation(wantProgram, "ModelMat"));
        } catch (Throwable t) {
            RenderDiagnostics.log("SHADER probe failed: {}", t.toString());
        }
    }
}
