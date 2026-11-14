package cn.jstxjf_.rte.render;

//#if MC>=11903
import org.joml.Matrix3f;
//#else
import com.mojang.math.Matrix3f;
//#endif
//#if MC>=11903
import org.joml.Matrix4f;
//#else
import com.mojang.math.Matrix4f;
//#endif
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.mtr.core.data.Position;

import java.util.List;

public final class RailChainHandleRenderer {

    private static final float HANDLE_SIZE = 0.45f;
    private static final float CANDIDATE_SIZE = 0.3f;

    private RailChainHandleRenderer() {
    }

    public static void render(PoseStack matrices, MultiBufferSource vertexConsumers) {
        if (!RailChainHandles.isActive()) {
            return;
        }
        final VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.lines());
        final Matrix4f mat = matrices.last().pose();
        final Matrix3f normal = matrices.last().normal();

        final List<double[]> points = RailChainHandles.chainPoints();
        for (int i = 0; i + 1 < points.size(); i++) {
            final double[] a = points.get(i);
            final double[] b = points.get(i + 1);
            line(buffer, mat, normal, a[0], a[1], a[2], b[0], b[1], b[2], 90, 200, 255, 200);
        }

        final Position start = RailChainHandles.tip(true);
        final Position end = RailChainHandles.tip(false);
        final boolean dragging = RailChainHandles.isDragging();
        final boolean draggingStart = RailChainHandles.isDraggingStart();

        drawHandle(buffer, mat, normal, start,
            dragging && draggingStart ? 255 : 120, 255, dragging && draggingStart ? 120 : 200);
        drawHandle(buffer, mat, normal, end,
            dragging && !draggingStart ? 255 : 255, dragging && !draggingStart ? 255 : 170, 120);

        if (!dragging) {
            return;
        }

        final Position hovered = RailChainHandles.hoveredCandidate();
        for (final Position candidate : RailChainHandles.candidates()) {
            final boolean picked = candidate.equals(hovered);
            final boolean trim = RailChainHandles.isTrimTarget(candidate);
            final boolean ok = RailChainHandles.isCommittable(candidate);
            final int r = ok ? (trim ? 255 : 140) : 255;
            final int g = ok ? (trim ? 170 : 255) : 70;
            final int b = ok ? (trim ? 90 : 160) : 70;
            box(buffer, mat, normal,
                candidate.getX() + 0.5, candidate.getY() + 0.5, candidate.getZ() + 0.5,
                picked ? HANDLE_SIZE : CANDIDATE_SIZE,
                r, g, b, picked ? 255 : 120);
        }

        final Position tip = RailChainHandles.tip(draggingStart);
        if (hovered != null && tip != null) {
            final boolean ok = RailChainHandles.isCommittable(hovered);
            line(buffer, mat, normal,
                tip.getX() + 0.5, tip.getY() + 0.5, tip.getZ() + 0.5,
                hovered.getX() + 0.5, hovered.getY() + 0.5, hovered.getZ() + 0.5,
                ok ? 255 : 255, ok ? 255 : 70, ok ? 140 : 70, 255);
        }
    }

    private static void drawHandle(VertexConsumer buffer, Matrix4f mat, Matrix3f normal,
                                   Position position, int r, int g, int b) {
        if (position == null) {
            return;
        }
        box(buffer, mat, normal,
            position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5,
            HANDLE_SIZE, r, g, b, 255);
    }

    private static void box(VertexConsumer buffer, Matrix4f mat, Matrix3f normal,
                            double cx, double cy, double cz, float half,
                            int r, int g, int b, int a) {
        final double x1 = cx - half, x2 = cx + half;
        final double y1 = cy - half, y2 = cy + half;
        final double z1 = cz - half, z2 = cz + half;
        final double[][] edges = {
            {x1, y1, z1, x2, y1, z1}, {x2, y1, z1, x2, y1, z2}, {x2, y1, z2, x1, y1, z2}, {x1, y1, z2, x1, y1, z1},
            {x1, y2, z1, x2, y2, z1}, {x2, y2, z1, x2, y2, z2}, {x2, y2, z2, x1, y2, z2}, {x1, y2, z2, x1, y2, z1},
            {x1, y1, z1, x1, y2, z1}, {x2, y1, z1, x2, y2, z1}, {x2, y1, z2, x2, y2, z2}, {x1, y1, z2, x1, y2, z2}
        };
        for (final double[] edge : edges) {
            line(buffer, mat, normal, edge[0], edge[1], edge[2], edge[3], edge[4], edge[5], r, g, b, a);
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
        cn.jstxjf_.rte.compat.VertexCompat.of(buffer).vertex(mat, (float) x1, (float) y1, (float) z1).color(r, g, b, a).normal(normal, dx, dy, dz).endVertex();
        cn.jstxjf_.rte.compat.VertexCompat.of(buffer).vertex(mat, (float) x2, (float) y2, (float) z2).color(r, g, b, a).normal(normal, dx, dy, dz).endVertex();
    }
}
