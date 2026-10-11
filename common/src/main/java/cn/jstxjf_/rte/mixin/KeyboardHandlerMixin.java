package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTEClient;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void rte$freeNodeKey(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (cn.jstxjf_.rte.render.FreeNodeEditor.onKey(key, action, modifiers)) {
            ci.cancel();
        }
    }

    @Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
    private void rte$reloadClientResources(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GLFW.GLFW_KEY_5) {
            RTEClient.reloadResourcesWithFeedback(minecraft);
            cir.setReturnValue(true);
        }
    }
}
