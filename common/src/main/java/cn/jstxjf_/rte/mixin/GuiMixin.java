package cn.jstxjf_.rte.mixin;

//#if MC>=12000
import net.minecraft.client.gui.GuiGraphics;
//#else
import cn.jstxjf_.rte.compat.GuiGraphics;
//#endif
import cn.jstxjf_.rte.render.FreeNodeEditor;
import cn.jstxjf_.rte.scripting.ScriptDebugOverlay;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "render", at = @At("TAIL"))
    //#if MC>=12101
    private void rte$renderFreeNodeHints(GuiGraphics graphics, net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
    //#elseif MC>=12000
    private void rte$renderFreeNodeHints(GuiGraphics graphics, float partialTick, CallbackInfo ci) {
    //#else
    private void rte$renderFreeNodeHints(com.mojang.blaze3d.vertex.PoseStack rtePoseStack, float partialTick, CallbackInfo ci) {
        final GuiGraphics graphics = new GuiGraphics(rtePoseStack);
    //#endif
        FreeNodeEditor.renderHud(graphics);
        ScriptDebugOverlay.render(graphics);
    }
}
