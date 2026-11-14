package cn.jstxjf_.rte.compat;

//#if MC<12000
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;
import java.util.Optional;

public class GuiGraphics extends GuiComponent {

    private final PoseStack pose;

    public GuiGraphics(PoseStack pose) {
        this.pose = pose;
    }

    public PoseStack pose() {
        return pose;
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(pose, x1, y1, x2, y2, color);
    }

    public void fillGradient(int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        fillGradient(pose, x1, y1, x2, y2, colorFrom, colorTo);
    }

    public int drawString(Font font, String text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, String text, int x, int y, int color, boolean shadow) {
        return text == null ? 0 : shadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public int drawString(Font font, Component text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, Component text, int x, int y, int color, boolean shadow) {
        return shadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public int drawString(Font font, FormattedCharSequence text, int x, int y, int color) {
        return drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
        return shadow ? font.drawShadow(pose, text, x, y, color) : font.draw(pose, text, x, y, color);
    }

    public void drawCenteredString(Font font, String text, int x, int y, int color) {
        GuiComponent.drawCenteredString(pose, font, text, x, y, color);
    }

    public void drawCenteredString(Font font, Component text, int x, int y, int color) {
        GuiComponent.drawCenteredString(pose, font, text, x, y, color);
    }

    public void drawCenteredString(Font font, FormattedCharSequence text, int x, int y, int color) {
        GuiComponent.drawCenteredString(pose, font, text, x, y, color);
    }

    public void renderTooltip(Font font, List<Component> lines, Optional<TooltipComponent> image, int x, int y) {
        final net.minecraft.client.gui.screens.Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            screen.renderTooltip(pose, lines, image, x, y);
        }
    }

    public void renderTooltip(Font font, Component line, int x, int y) {
        final net.minecraft.client.gui.screens.Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            screen.renderTooltip(pose, line, x, y);
        }
    }

    public int guiWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public int guiHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public void flush() {
        RenderSystem.disableDepthTest();
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        RenderSystem.enableDepthTest();
    }
}
//#endif
