package cn.jstxjf_.rte.gui;

import net.minecraft.client.gui.GuiGraphics;

final class GuiDrawUtil {

    private GuiDrawUtil() {
    }

    static void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        cn.jstxjf_.rte.gui.imgui.CanvasPainter.drawLine(x1, y1, x2, y2, color, 2f);
    }

    static void drawDashedHLine(GuiGraphics graphics, int x1, int x2, int y, int color) {
        for (int x = x1; x < x2; x += 8) {
            final int end = Math.min(x + 4, x2);
            cn.jstxjf_.rte.gui.imgui.CanvasPainter.fillRect(x, y, end, y + 1, color);
        }
    }

    static void drawCircle(GuiGraphics graphics, int cx, int cy, int r, int color) {
        cn.jstxjf_.rte.gui.imgui.CanvasPainter.drawCircle(cx, cy, r, color);
    }

    static void fillRect(double x1, double y1, double x2, double y2, int color) {
        cn.jstxjf_.rte.gui.imgui.CanvasPainter.fillRect(x1, y1, x2, y2, color);
    }

    static void drawText(String text, double x, double y, int color) {
        final float s = cn.jstxjf_.rte.gui.imgui.CanvasPainter.guiScale();
        imgui.ImGui.getBackgroundDrawList().addText((float) (x * s), (float) (y * s),
            cn.jstxjf_.rte.gui.imgui.CanvasPainter.toImColor(color), text);
    }

    static double distPointToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double lenSq = dx * dx + dy * dy;
        if (lenSq < 1) return Math.sqrt((px - x1) * (px - x1) + (py - y1) * (py - y1));
        double t = clamp(((px - x1) * dx + (py - y1) * dy) / lenSq, 0, 1);
        double projX = x1 + t * dx;
        double projY = y1 + t * dy;
        return Math.sqrt((px - projX) * (px - projX) + (py - projY) * (py - projY));
    }

    static double computeGridStep(double blocksVisible) {
        double raw = blocksVisible / 8.0;
        if (raw <= 0) return 1;
        double exp = Math.floor(Math.log10(raw));
        double magnitude = Math.pow(10, exp);
        double residual = raw / magnitude;
        if (residual <= 1.5) return magnitude;
        if (residual <= 3.5) return 2 * magnitude;
        if (residual <= 7.5) return 5 * magnitude;
        return 10 * magnitude;
    }

    static float smoothStep(float t) {
        return t * t * (3 - 2 * t);
    }

    static float lerpAngle(float a, float b, float t) {
        float diff = b - a;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;
        return a + diff * t;
    }

    static double lerp(double a, double b, double t) {
        return a + (b - a) * clamp(t, 0, 1);
    }

    static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
