package cn.jstxjf_.rte.compat;

import net.minecraft.client.gui.components.AbstractWidget;

public final class WidgetCompat {

    private WidgetCompat() {
    }

    public static net.minecraft.client.gui.components.Button button(net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress, int x, int y, int width, int height) {
        //#if MC>=11903
        return net.minecraft.client.gui.components.Button.builder(message, onPress).bounds(x, y, width, height).build();
        //#else
        return new net.minecraft.client.gui.components.Button(x, y, width, height, message, onPress);
        //#endif
    }

    public static int widgetX(AbstractWidget widget) {
        //#if MC>=11903
        return widget.getX();
        //#else
        return widget.x;
        //#endif
    }

    public static int widgetY(AbstractWidget widget) {
        //#if MC>=11903
        return widget.getY();
        //#else
        return widget.y;
        //#endif
    }

    public static void setWidgetX(AbstractWidget widget, int x) {
        //#if MC>=11903
        widget.setX(x);
        //#else
        widget.x = x;
        //#endif
    }

    public static void setWidgetY(AbstractWidget widget, int y) {
        //#if MC>=11903
        widget.setY(y);
        //#else
        widget.y = y;
        //#endif
    }
}
