package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RailChainHandles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void rte$freeNodePress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (cn.jstxjf_.rte.render.FreeNodeEditor.onMouseButton(button, action, modifiers)) {
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void rte$freeNodeScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (cn.jstxjf_.rte.render.FreeNodeEditor.onScroll(vertical)) {
            ci.cancel();
        }
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void rte$depotRailMarkPress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (cn.jstxjf_.rte.render.FreeNodeEditor.isCapturingPointer()) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null || minecraft.player == null
            || !cn.jstxjf_.rte.render.DepotRailMarking.isActive() || action != 1) {
            return;
        }
        if (button == 0) {
            cn.jstxjf_.rte.render.DepotRailMarking.cycleHovered();
            ci.cancel();
        } else if (button == 1) {
            cn.jstxjf_.rte.render.DepotRailMarking.exit();
            ci.cancel();
        }
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void rte$chainHandlePress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (cn.jstxjf_.rte.render.FreeNodeEditor.isCapturingPointer()) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null || minecraft.player == null || !RailChainHandles.isActive()) {
            return;
        }
        if (button != 0) {
            if (button == 1 && action == 1 && RailChainHandles.isDragging()) {
                RailChainHandles.cancelDrag();
                ci.cancel();
            }
            return;
        }
        if (action == 1) {
            if (RailChainHandles.beginDrag()) {
                ci.cancel();
            }
            return;
        }
        if (action == 0 && RailChainHandles.isDragging()) {
            RailChainHandles.commitDrag();
            ci.cancel();
        }
    }
}
