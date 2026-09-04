package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.item.CompoundCreatorLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public final class SliceGhostRenderer {

    private static CompoundCreatorLogic.SliceTask task;
    private static int depthBlocks = 12;
    private static boolean flip = false;

    private SliceGhostRenderer() {
    }

    public static void show(CompoundCreatorLogic.SliceTask sliceTask, int depth, boolean mirrored) {
        task = sliceTask;
        depthBlocks = Math.max(1, Math.min(64, depth));
        flip = mirrored;
    }

    public static void hide() {
        task = null;
    }

    public static boolean isActive() {
        return task != null;
    }

    public static void render(PoseStack matrices, MultiBufferSource vertexConsumers) {
        final CompoundCreatorLogic.SliceTask current = task;
        if (current == null) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        final Direction facing = minecraft.player.getDirection();
        final Direction lateral = flip ? facing.getClockWise() : facing.getCounterClockWise();
        final net.minecraft.core.BlockPos origin = minecraft.player.blockPosition();

        final VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.lines());
        final Matrix4f matrix = matrices.last().pose();
        final Matrix3f normal = matrices.last().normal();

        final int width = Math.max(1, current.width);
        final int height = Math.max(1, current.height);

        for (int z = 0; z < depthBlocks; z++) {
            final List<CompoundCreatorLogic.Lump> lumps = current.hasSegments()
                ? current.lumpsAt(z) : current.lumps;
            if (lumps == null) {
                continue;
            }
            final boolean segmentEdge = current.hasSegments() && isSegmentStart(current, z);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final int index = y * width + x;
                    if (index >= lumps.size()) {
                        continue;
                    }
                    final CompoundCreatorLogic.Lump lump = lumps.get(index);
                    if (lump == null || lump.blockState == null) {
                        continue;
                    }
                    final net.minecraft.core.BlockPos cell = origin
                        .relative(facing, z)
                        .relative(lateral, width / 2 - x)
                        .above(height / 2 - y);
                    final int alpha = segmentEdge ? 220 : 130;
                    final int red = segmentEdge ? 255 : 90;
                    final int green = segmentEdge ? 190 : 230;
                    final int blue = segmentEdge ? 60 : 180;
                    box(buffer, matrix, normal, cell, red, green, blue, alpha);
                }
            }
        }
    }

    private static boolean isSegmentStart(CompoundCreatorLogic.SliceTask current, int z) {
        final double cycle = current.segmentCycleLength();
        if (cycle <= 0) {
            return false;
        }
        double position = z % cycle;
        for (final CompoundCreatorLogic.Segment segment : current.segments) {
            if (position < 0.5) {
                return true;
            }
            position -= Math.max(0.1, segment.length);
            if (position < 0) {
                return false;
            }
        }
        return false;
    }

    private static void box(VertexConsumer buffer, Matrix4f matrix, Matrix3f normal,
                            net.minecraft.core.BlockPos pos, int r, int g, int b, int a) {
        final float x1 = pos.getX() + 0.08F;
        final float y1 = pos.getY() + 0.08F;
        final float z1 = pos.getZ() + 0.08F;
        final float x2 = pos.getX() + 0.92F;
        final float y2 = pos.getY() + 0.92F;
        final float z2 = pos.getZ() + 0.92F;

        line(buffer, matrix, normal, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(buffer, matrix, normal, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(buffer, matrix, normal, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(buffer, matrix, normal, x1, y1, z2, x1, y1, z1, r, g, b, a);

        line(buffer, matrix, normal, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(buffer, matrix, normal, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(buffer, matrix, normal, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(buffer, matrix, normal, x1, y2, z2, x1, y2, z1, r, g, b, a);

        line(buffer, matrix, normal, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(buffer, matrix, normal, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(buffer, matrix, normal, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(buffer, matrix, normal, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private static void line(VertexConsumer buffer, Matrix4f matrix, Matrix3f normal,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             int r, int g, int b, int a) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        final float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length > 0) {
            dx /= length;
            dy /= length;
            dz /= length;
        }
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(normal, dx, dy, dz).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(normal, dx, dy, dz).endVertex();
    }
}
