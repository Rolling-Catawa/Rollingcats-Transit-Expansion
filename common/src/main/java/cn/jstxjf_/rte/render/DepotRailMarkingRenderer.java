package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.data.DepotRailRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public final class DepotRailMarkingRenderer {

    private static final double LIFT = 0.35;

    private DepotRailMarkingRenderer() {
    }

    public static void render(PoseStack matrices, MultiBufferSource vertexConsumers) {
        if (!DepotRailMarking.isActive()) {
            return;
        }
        final VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.lines());
        final Matrix4f mat = matrices.last().pose();
        final Matrix3f normal = matrices.last().normal();
        final String hovered = DepotRailMarking.hoveredRailId();

        for (final DepotRailMarking.RailSegment segment : DepotRailMarking.segments()) {
            final DepotRailRules.Rule rule = DepotRailMarking.ruleOf(segment.railId);
            final boolean isHovered = segment.railId.equals(hovered);
            if (rule == DepotRailRules.Rule.NONE && !isHovered) {
                continue;
            }
            final int r;
            final int g;
            final int b;
            switch (rule) {
                case VIA -> {
                    r = 90;
                    g = 255;
                    b = 120;
                }
                case BANNED -> {
                    r = 255;
                    g = 80;
                    b = 80;
                }
                default -> {
                    r = 210;
                    g = 210;
                    b = 210;
                }
            }
            final int alpha = isHovered ? 255 : 170;
            final List<double[]> points = segment.points;
            for (int i = 0; i + 1 < points.size(); i++) {
                final double[] a = points.get(i);
                final double[] c = points.get(i + 1);
                line(buffer, mat, normal,
                    a[0], a[1] + LIFT, a[2],
                    c[0], c[1] + LIFT, c[2], r, g, b, alpha);
            }
        }
    }

    private static void line(VertexConsumer buffer, Matrix4f mat, Matrix3f normal,
                             double x1, double y1, double z1, double x2, double y2, double z2,
                             int r, int g, int b, int a) {
        float dx = (float) (x2 - x1);
        float dy = (float) (y2 - y1);
        float dz = (float) (z2 - z1);
        final float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1e-6) {
            return;
        }
        dx /= length;
        dy /= length;
        dz /= length;
        buffer.vertex(mat, (float) x1, (float) y1, (float) z1).color(r, g, b, a).normal(normal, dx, dy, dz).endVertex();
        buffer.vertex(mat, (float) x2, (float) y2, (float) z2).color(r, g, b, a).normal(normal, dx, dy, dz).endVertex();
    }
}
