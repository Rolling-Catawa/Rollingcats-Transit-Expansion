package cn.jstxjf_.rte.gui;

import cn.jstxjf_.rte.data.CurveFitting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

class YModeGraphView {

    double centerAlong;
    double centerY;
    double scale = 4.0;

    private int viewWidth = 1;
    private int viewHeight = 1;

    void setViewport(int width, int height) {
        viewWidth = width;
        viewHeight = height;
    }

    private int canvasWidth() {
        return viewWidth - 128;
    }

    void initView(double railLen, double y1, double y2) {
        int cw = canvasWidth();
        centerAlong = railLen / 2.0;
        centerY = (y1 + y2) / 2.0;
        scale = GuiDrawUtil.clamp(cw * 0.8 / Math.max(railLen, 1), 0.5, 50);
    }

    int[] toScreen(double along, double y) {
        int cw = canvasWidth();
        int sx = (int) (cw / 2.0 + (along - centerAlong) * scale);
        int sy = (int) (viewHeight / 2.0 - (y - centerY) * scale);
        return new int[]{sx, sy};
    }

    double[] toGraph(double mouseX, double mouseY) {
        int cw = canvasWidth();
        double along = centerAlong + (mouseX - cw / 2.0) / scale;
        double y = centerY - (mouseY - viewHeight / 2.0) / scale;
        return new double[]{along, y};
    }

    void pan(double dragX, double dragY) {
        centerAlong -= dragX / scale;
        centerY += dragY / scale;
    }

    void zoom(double amount) {
        double factor = amount > 0 ? 1.15 : 1.0 / 1.15;
        scale = GuiDrawUtil.clamp(scale * factor, 0.5, 50);
    }

    void renderGraph(GuiGraphics graphics, Font font, double railLen, double y1, double y2) {
        int cw = canvasWidth();

        double blocksVisibleH = cw / scale;
        double gridStep = GuiDrawUtil.computeGridStep(blocksVisibleH);

        double startAlong = centerAlong - cw / 2.0 / scale;
        double endAlong = centerAlong + cw / 2.0 / scale;
        double startY = centerY - viewHeight / 2.0 / scale;
        double endY = centerY + viewHeight / 2.0 / scale;

        double gridAlongStart = Math.floor(startAlong / gridStep) * gridStep;
        for (double a = gridAlongStart; a <= endAlong; a += gridStep) {
            int[] s = toScreen(a, 0);
            if (s[0] >= 0 && s[0] < cw) {
                GuiDrawUtil.fillRect(s[0], 0, s[0] + 1, viewHeight, 0x22FFFFFF);
                String label = String.format(java.util.Locale.ROOT, "%.0f", a);
                GuiDrawUtil.drawText(label, s[0] + 2, viewHeight - 12, 0xFF999999);
            }
        }
        double gridYStart = Math.floor(startY / gridStep) * gridStep;
        for (double y = gridYStart; y <= endY; y += gridStep) {
            int[] s = toScreen(0, y);
            if (s[1] >= 0 && s[1] < viewHeight) {
                GuiDrawUtil.fillRect(0, s[1], cw, s[1] + 1, 0x22FFFFFF);
                String label = String.format(java.util.Locale.ROOT, "%.0f", y);
                GuiDrawUtil.drawText(label, 4, s[1] - 10, 0xFF999999);
            }
        }

        int[] s1 = toScreen(0, y1);
        int[] s2 = toScreen(railLen, y2);
        GuiDrawUtil.drawLine(graphics, s1[0], s1[1], s2[0], s2[1], 0x44FFFFFF);

        GuiDrawUtil.drawCircle(graphics, s1[0], s1[1], 6, 0xFF00FF00);
        GuiDrawUtil.drawCircle(graphics, s2[0], s2[1], 6, 0xFFFF4444);

        GuiDrawUtil.drawDashedHLine(graphics, 0, cw, s1[1], 0x4400FF00);
        GuiDrawUtil.drawDashedHLine(graphics, 0, cw, s2[1], 0x44FF4444);
        GuiDrawUtil.drawText(String.format(java.util.Locale.ROOT, "Y=%.1f", y1), 4, s1[1] - 10, 0xFF66FF66);
        GuiDrawUtil.drawText(String.format(java.util.Locale.ROOT, "Y=%.1f", y2), 4, s2[1] - 10, 0xFFFF6666);
    }

    void renderSnapIndicator(GuiGraphics graphics, int mouseX, int mouseY, double railLen, double y1, double y2, double snapRadius) {
        double[] graph = toGraph(mouseX, mouseY);
        double along = graph[0];
        if (along < snapRadius) {
            int[] s = toScreen(0, y1);
            GuiDrawUtil.drawCircle(graphics, s[0], s[1], 10, 0x6600FF00);
        } else if (along > railLen - snapRadius) {
            int[] s = toScreen(railLen, y2);
            GuiDrawUtil.drawCircle(graphics, s[0], s[1], 10, 0x66FF4444);
        }
    }

    void renderPoints(GuiGraphics graphics, List<CurveFitting.DrawPoint> points, int color, double railLen) {
        if (points.isEmpty()) return;
        CurveFitting.DrawPoint prev = null;
        for (CurveFitting.DrawPoint point : points) {
            double along = point.t() * railLen;
            int[] s = toScreen(along, point.y());
            GuiDrawUtil.fillRect(s[0] - 2, s[1] - 2, s[0] + 3, s[1] + 3, color);
            if (prev != null) {
                double prevAlong = prev.t() * railLen;
                int[] sp = toScreen(prevAlong, prev.y());
                GuiDrawUtil.drawLine(graphics, sp[0], sp[1], s[0], s[1], color);
            }
            prev = point;
        }
    }
}
