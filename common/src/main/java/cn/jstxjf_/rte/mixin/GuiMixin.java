package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.FreeNodeEditor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void rte$renderFreeNodeHints(GuiGraphics graphics, float partialTick, CallbackInfo ci) {
        FreeNodeEditor.renderHud(graphics);
    }
}
