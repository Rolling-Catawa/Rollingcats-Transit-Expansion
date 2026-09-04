package cn.jstxjf_.rte.gui.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;

public final class ImGuiMouseRouter {

    public interface Handler {
        boolean onCanvasDown(double mouseX, double mouseY, int button);

        boolean onCanvasDrag(double mouseX, double mouseY, int button, double dragX, double dragY);

        boolean onCanvasUp(double mouseX, double mouseY, int button);

        boolean onCanvasScroll(double mouseX, double mouseY, double amount);
    }

    private final boolean[] pressed = new boolean[3];

    public void poll(Handler handler) {
        final ImGuiIO io = ImGui.getIO();
        final float scale = (float) net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScale();
        if (scale <= 0) {
            return;
        }
        final double mouseX = io.getMousePosX() / scale;
        final double mouseY = io.getMousePosY() / scale;
        final boolean captured = io.getWantCaptureMouse();

        for (int button = 0; button < pressed.length; button++) {
            final boolean down = io.getMouseDown(button);
            if (down && !pressed[button]) {
                if (captured) {
                    continue;
                }
                pressed[button] = true;
                handler.onCanvasDown(mouseX, mouseY, button);
            } else if (!down && pressed[button]) {
                pressed[button] = false;
                handler.onCanvasUp(mouseX, mouseY, button);
            } else if (down && pressed[button]) {
                final double dragX = io.getMouseDeltaX() / scale;
                final double dragY = io.getMouseDeltaY() / scale;
                if (dragX != 0 || dragY != 0) {
                    handler.onCanvasDrag(mouseX, mouseY, button, dragX, dragY);
                }
            }
        }

        if (!captured) {
            final float wheel = io.getMouseWheel();
            if (wheel != 0) {
                handler.onCanvasScroll(mouseX, mouseY, wheel);
            }
        }
    }

    public void reset() {
        java.util.Arrays.fill(pressed, false);
    }
}
