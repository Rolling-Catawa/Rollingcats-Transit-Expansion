package cn.jstxjf_.rte.compat;

public final class GuiArgs {

    private GuiArgs() {
    }

    //#if MC>=12000
    public static net.minecraft.client.gui.GuiGraphics of(net.minecraft.client.gui.GuiGraphics graphics) {
        return graphics;
    }
    //#else
    public static com.mojang.blaze3d.vertex.PoseStack of(GuiGraphics graphics) {
        return graphics.pose();
    }
    //#endif
}
