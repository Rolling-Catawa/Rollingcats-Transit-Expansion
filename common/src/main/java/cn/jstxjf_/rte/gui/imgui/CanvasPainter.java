package cn.jstxjf_.rte.gui.imgui;

import imgui.ImDrawList;
import imgui.ImGui;

import java.util.List;
import java.util.Locale;

public final class CanvasPainter {

    private CanvasPainter() {
    }

    public static float guiScale() {
        return (float) net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScale();
    }

    public static int toImColor(int argb) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    public static void fillRect(double x1, double y1, double x2, double y2, int argb) {
        final float s = guiScale();
        ImGui.getBackgroundDrawList().addRectFilled(
            (float) (x1 * s), (float) (y1 * s), (float) (x2 * s), (float) (y2 * s), toImColor(argb));
    }

    public static void drawLine(double x1, double y1, double x2, double y2, int argb, float thickness) {
        final float s = guiScale();
        ImGui.getBackgroundDrawList().addLine(
            (float) (x1 * s), (float) (y1 * s), (float) (x2 * s), (float) (y2 * s),
            toImColor(argb), Math.max(1f, thickness * s * 0.5f));
    }

    public static void drawCircle(double cx, double cy, double radius, int argb) {
        final float s = guiScale();
        ImGui.getBackgroundDrawList().addCircleFilled(
            (float) (cx * s), (float) (cy * s), (float) (radius * s), toImColor(argb), 20);
    }

    public static void drawPolyline(List<double[]> points, int argb, float thickness) {
        if (points == null || points.size() < 2) {
            return;
        }
        for (int i = 0; i + 1 < points.size(); i++) {
            final double[] a = points.get(i);
            final double[] b = points.get(i + 1);
            drawLine(a[0], a[1], b[0], b[1], argb, thickness);
        }
    }

    public static void drawBackground(int left, int top, int right, int bottom) {
        fillRect(left, top, right, bottom, 0x44000000);
        fillRect(left, top, right, top + 1, 0x66FFFFFF);
        fillRect(left, bottom - 1, right, bottom, 0x66FFFFFF);
        fillRect(left, top, left + 1, bottom, 0x66FFFFFF);
        fillRect(right - 1, top, right, bottom, 0x66FFFFFF);
    }

    public static void drawGrid(int left, int top, int right, int bottom, int centerX, int centerY, double pixelsPerBlock) {
        if (pixelsPerBlock < 1.5) {
            return;
        }
        double minorStep = 1;
        while (minorStep * pixelsPerBlock < 14) {
            minorStep *= minorStep % 3 == 0 ? 2.5 : 2;
        }
        final double majorStep = minorStep * 5;

        drawGridLines(left, top, right, bottom, centerX, centerY, minorStep * pixelsPerBlock, 0x0EFFFFFF, 1f);
        drawGridLines(left, top, right, bottom, centerX, centerY, majorStep * pixelsPerBlock, 0x28FFFFFF, 1f);

        drawLine(centerX, top, centerX, bottom, 0x66FF7070, 1.6f);
        drawLine(left, centerY, right, centerY, 0x6670B0FF, 1.6f);

        if (majorStep * pixelsPerBlock >= 46) {
            drawRuler(left, top, right, centerX, majorStep * pixelsPerBlock, majorStep);
        }
        drawScaleBar(left, bottom, pixelsPerBlock);
    }

    private static void drawGridLines(int left, int top, int right, int bottom,
                                      int centerX, int centerY, double stepPixels, int color, float thickness) {
        if (stepPixels < 6) {
            return;
        }
        for (double px = centerX + stepPixels; px < right; px += stepPixels) drawLine(px, top, px, bottom, color, thickness);
        for (double px = centerX - stepPixels; px > left; px -= stepPixels) drawLine(px, top, px, bottom, color, thickness);
        for (double py = centerY + stepPixels; py < bottom; py += stepPixels) drawLine(left, py, right, py, color, thickness);
        for (double py = centerY - stepPixels; py > top; py -= stepPixels) drawLine(left, py, right, py, color, thickness);
    }

    private static void drawRuler(int left, int top, int right, int centerX, double stepPixels, double stepMeters) {
        final float s = guiScale();
        final ImDrawList drawList = ImGui.getBackgroundDrawList();
        final int color = toImColor(0x77FFFFFF);
        int index = 0;
        for (double px = centerX; px < right - 24; px += stepPixels, index++) {
            if (index > 0) {
                drawList.addText((float) ((px + 3) * s), (top + 3) * s, color,
                    String.format(Locale.ROOT, "%.0f", index * stepMeters));
            }
        }
        index = 0;
        for (double px = centerX - stepPixels; px > left + 24; px -= stepPixels) {
            index++;
            drawList.addText((float) ((px + 3) * s), (top + 3) * s, color,
                String.format(Locale.ROOT, "-%.0f", index * stepMeters));
        }
    }

    public static void drawScaleBar(int left, int bottom, double pixelsPerBlock) {
        if (pixelsPerBlock <= 0) {
            return;
        }
        final float s = guiScale();
        double meters = 10;
        while (meters * pixelsPerBlock < 40) meters *= 2;
        while (meters * pixelsPerBlock > 160) meters /= 2;
        final double barPixels = meters * pixelsPerBlock;
        final double y = bottom - 10;
        fillRect(left + 8, y, left + 8 + barPixels, y + 1.5, 0xCCFFFFFF);
        fillRect(left + 8, y - 3, left + 9.5, y + 4, 0xCCFFFFFF);
        fillRect(left + 8 + barPixels - 1.5, y - 3, left + 8 + barPixels, y + 4, 0xCCFFFFFF);
        ImGui.getBackgroundDrawList().addText((float) ((left + 12) * s), (float) ((y - 13) * s),
            toImColor(0xCCFFFFFF), String.format(Locale.ROOT, "%.0f m", meters));
    }

    public static int toolbarFlags() {
        return imgui.flag.ImGuiWindowFlags.NoTitleBar
            | imgui.flag.ImGuiWindowFlags.NoResize
            | imgui.flag.ImGuiWindowFlags.NoMove
            | imgui.flag.ImGuiWindowFlags.AlwaysAutoResize
            | imgui.flag.ImGuiWindowFlags.NoScrollbar
            | imgui.flag.ImGuiWindowFlags.NoNavFocus;
    }
}
