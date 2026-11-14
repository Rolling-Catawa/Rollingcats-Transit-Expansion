package cn.jstxjf_.rte.gui;

//#if MC>=12000
import net.minecraft.client.gui.GuiGraphics;
//#else
import cn.jstxjf_.rte.compat.GuiGraphics;
//#endif
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class WidgetLabel extends AbstractWidget {

    public boolean alignR = false;
    public boolean centre = true;
    public boolean selected = false;
    public int color = 0xFFFFFFFF;
    public int backgroundColor = 0;
    public int hoverBackgroundColor = 0x88A8A9AD;
    public int selectedBackgroundColor = 0x88785BCB;
    public String disabledPrefix = "!";

    private final Runnable onClick;

    public WidgetLabel(int x, int y, int width, Component text) {
        this(x, y, width, text, null);
    }

    public WidgetLabel(int x, int y, int width, Component text, Runnable onClick) {
        super(x, y, width, text.getString().split("\n", -1).length * 10, text);
        this.onClick = onClick;
    }

    @Override
    //#if MC>=12000
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
    //#else
    public void renderButton(com.mojang.blaze3d.vertex.PoseStack rtePoseStack, int mouseX, int mouseY, float delta) {
        final GuiGraphics graphics = new GuiGraphics(rtePoseStack);
    //#endif
        if (!visible) {
            return;
        }

        final String[] lines = getMessage().getString().split("\n");
        height = lines.length * 10;
        final boolean hovered = mouseX >= getX() && mouseX < getX() + getWidth() && mouseY >= getY() && mouseY < getY() + getHeight();

        if (backgroundColor != 0) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), backgroundColor);
        }
        if (selected && selectedBackgroundColor != 0) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), selectedBackgroundColor);
        } else if (hovered && hoverBackgroundColor != 0) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), hoverBackgroundColor);
        }

        for (int i = 0; i < lines.length; ++i) {
            final int textWidth = Minecraft.getInstance().font.width(lines[i]);
            final int x = centre ? getX() + (getWidth() - textWidth) / 2 : alignR ? getX() + getWidth() - textWidth : getX();
            final int y = getY() + 10 * i;
            if (textWidth > width) {
                final int offset = (int) (System.currentTimeMillis() / 25 % (textWidth + 40));
                AbstractScrollWidget.vcEnableScissor(getX(), getY(), getX() + width, getY() + height);
                graphics.drawString(Minecraft.getInstance().font, lines[i], x - offset, y, color);
                graphics.drawString(Minecraft.getInstance().font, lines[i], x + textWidth + 40 - offset, y, color);
                RenderSystem.disableScissor();
            } else {
                graphics.drawString(Minecraft.getInstance().font, lines[i], x, y, color);
            }
            if (!isActive()) {
                graphics.drawString(Minecraft.getInstance().font, disabledPrefix, x - 8, y, 0xFFFF0000);
            }
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        if (onClick != null) {
            onClick.run();
        }
    }

    //#if MC>=11903
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
    //#else
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    //#endif

    public void setHeight(int height) {
        this.height = height;
    }
}
