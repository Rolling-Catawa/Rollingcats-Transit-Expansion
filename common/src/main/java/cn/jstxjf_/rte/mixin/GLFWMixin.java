package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.gfx.ContextCapability;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GLFW.class, remap = false)
public class GLFWMixin {

    @Inject(method = "glfwCreateWindow(IILjava/lang/CharSequence;JJ)J", at = @At("HEAD"), cancellable = true, remap = false)
    private static void rte$glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share, CallbackInfoReturnable<Long> cir) {
        cir.setReturnValue(ContextCapability.createWindow(width, height, title, monitor, share));
    }
}
