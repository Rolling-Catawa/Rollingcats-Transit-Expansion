package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RenderDiagnostics;
import org.mtr.mapping.render.vertex.VertexAttributeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VertexAttributeState.class, remap = false)
public abstract class AttribDiagMixin {

    @Shadow(remap = false) public org.mtr.mapping.holder.Matrix4f matrix4f;

    @Inject(method = "apply", at = @At("TAIL"), require = 0)
    private void rte$diagApply(CallbackInfo ci) {
        if (!RenderDiagnostics.allow()) return;
        String applied = "NULL";
        if (matrix4f != null) {
            try {
                java.lang.reflect.Field f = null;
                Class<?> c = matrix4f.getClass();
                while (c != null && f == null) {
                    try {
                        f = c.getDeclaredField("data");
                    } catch (NoSuchFieldException e) {
                        c = c.getSuperclass();
                    }
                }
                f.setAccessible(true);
                final org.joml.Matrix4f m = (org.joml.Matrix4f) f.get(matrix4f);
                applied = String.format("(%.2f, %.2f, %.2f) dist=%.2f", m.m30(), m.m31(), m.m32(),
                    Math.sqrt(m.m30() * m.m30() + m.m31() * m.m31() + m.m32() * m.m32()));
            } catch (Throwable t) {
                applied = "err:" + t;
            }
        }
        String gl = "?";
        try {
            final float[] col3 = new float[4];
            org.lwjgl.opengl.GL33.glGetVertexAttribfv(9, 0x8626, col3);
            final int[] enabled = new int[1];
            org.lwjgl.opengl.GL33.glGetVertexAttribiv(9, 0x8622, enabled);
            gl = String.format("(%.2f, %.2f, %.2f) arrayEnabled=%d", col3[0], col3[1], col3[2], enabled[0]);
        } catch (Throwable t) {
            gl = "err:" + t;
        }
        String prog = "?";
        try {
            final int[] pid = new int[1];
            org.lwjgl.opengl.GL33.glGetIntegerv(0x8B8D, pid);
            final int modelMatLoc = org.lwjgl.opengl.GL33.glGetAttribLocation(pid[0], "ModelMat");
            final int posLoc = org.lwjgl.opengl.GL33.glGetAttribLocation(pid[0], "Position");
            final int active = org.lwjgl.opengl.GL33.glGetProgrami(pid[0], 0x8B89);
            prog = String.format("program=%d ModelMatLoc=%d PositionLoc=%d activeAttribs=%d",
                pid[0], modelMatLoc, posLoc, active);
        } catch (Throwable t) {
            prog = "err:" + t;
        }
        RenderDiagnostics.log("APPLY appliedMatrix={} glCurrent={} {}", applied, gl, prog);
    }
}
