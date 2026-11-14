package cn.jstxjf_.rte.client.gui;

import cn.jstxjf_.rte.item.CompoundCreatorLogic;
import imgui.ImDrawList;
import imgui.ImGui;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class SlicePreview3D {

    private static final int[][] FACE_VERTS = {
        {0, 1, 2, 3},
        {5, 4, 7, 6},
        {4, 0, 3, 7},
        {1, 5, 6, 2},
        {4, 5, 1, 0},
        {3, 2, 6, 7},
    };

    private static final float[] FACE_SHADE = {0.78F, 0.62F, 0.70F, 0.70F, 1.00F, 0.48F};

    private float yaw = 0.7F;
    private float pitch = 0.45F;
    private float zoom = 1.0F;
    private int depthBlocks = 12;

    public int depthBlocks() {
        return depthBlocks;
    }

    public void draw(CompoundCreatorLogic.SliceTask task,
                     List<CompoundCreatorLogic.Lump> baseLumps,
                     String title) {
        final float viewWidth = Math.max(300, ImGui.getContentRegionAvailX() - 8);
        final float viewHeight = 300;
        final float originX = ImGui.getCursorScreenPosX();
        final float originY = ImGui.getCursorScreenPosY();

        final ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(originX, originY, originX + viewWidth, originY + viewHeight, 0xE0141418);
        drawList.addRect(originX, originY, originX + viewWidth, originY + viewHeight, 0xFF42423A);

        ImGui.invisibleButton("###slice_preview3d", viewWidth, viewHeight);
        if (ImGui.isItemActive()) {
            if (ImGui.getIO().getMouseDown(1)) {
                zoom = Math.max(0.2F, Math.min(6F, zoom * (1 - ImGui.getIO().getMouseDeltaY() * 0.01F)));
            } else {
                yaw += ImGui.getIO().getMouseDeltaX() * 0.01F;
                pitch += ImGui.getIO().getMouseDeltaY() * 0.01F;
                pitch = Math.max(-1.4F, Math.min(1.4F, pitch));
            }
        }

        final List<float[]> quads = buildQuads(task, baseLumps);
        if (quads.isEmpty()) {
            drawList.addText(originX + 12, originY + 12, 0xFF9A9A9A,
                Component.translatable("gui.rte.compound_creator.preview3d.empty").getString());
        } else {
            paint(drawList, quads, originX, originY, viewWidth, viewHeight);
        }

        final float[] zoomHolder = {zoom};
        ImGui.setNextItemWidth(180);
        if (ImGui.sliderFloat(Component.translatable("gui.rte.compound_creator.preview3d.zoom").getString()
                + "###slice_p3d_zoom", zoomHolder, 0.2F, 6F, "%.1fx")) {
            zoom = zoomHolder[0];
        }
        ImGui.sameLine();
        final imgui.type.ImInt depthHolder = new imgui.type.ImInt(depthBlocks);
        ImGui.setNextItemWidth(120);
        if (ImGui.inputInt(Component.translatable("gui.rte.compound_creator.preview3d.depth").getString()
                + "###slice_p3d_depth", depthHolder)) {
            depthBlocks = Math.max(1, Math.min(64, depthHolder.get()));
        }
        ImGui.textDisabled("  " + Component.translatable("gui.rte.compound_creator.preview3d.hint").getString());
    }

    private List<float[]> buildQuads(CompoundCreatorLogic.SliceTask task,
                                     List<CompoundCreatorLogic.Lump> baseLumps) {
        final List<float[]> quads = new ArrayList<>();
        final int width = Math.max(1, task.width);
        final int height = Math.max(1, task.height);

        for (int z = 0; z < depthBlocks; z++) {
            final List<CompoundCreatorLogic.Lump> lumps = task.hasSegments()
                ? task.lumpsAt(z) : baseLumps;
            if (lumps == null) {
                continue;
            }
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
                    addCube(quads, width / 2F - 0.5F - x, height / 2F - 0.5F - y, z, colourOf(lump));
                }
            }
        }
        return quads;
    }

    private static int colourOf(CompoundCreatorLogic.Lump lump) {
        try {
            final int rgb = lump.blockState.data.getMapColor(
                net.minecraft.client.Minecraft.getInstance().level,
                net.minecraft.core.BlockPos.ZERO).col;
            return rgb == 0 ? 0x9A9A9A : rgb;
        } catch (Throwable ignored) {
            return 0x9A9A9A;
        }
    }

    private static void addCube(List<float[]> quads, float cx, float cy, float cz, int rgb) {
        final float h = 0.5F;
        final float[][] corner = {
            {cx - h, cy - h, cz - h}, {cx + h, cy - h, cz - h},
            {cx + h, cy + h, cz - h}, {cx - h, cy + h, cz - h},
            {cx - h, cy - h, cz + h}, {cx + h, cy - h, cz + h},
            {cx + h, cy + h, cz + h}, {cx - h, cy + h, cz + h},
        };
        for (int face = 0; face < 6; face++) {
            final float[] quad = new float[14];
            for (int c = 0; c < 4; c++) {
                final float[] v = corner[FACE_VERTS[face][c]];
                quad[c * 3] = v[0];
                quad[c * 3 + 1] = v[1];
                quad[c * 3 + 2] = v[2];
            }
            quad[12] = rgb;
            quad[13] = face;
            quads.add(quad);
        }
    }

    private void paint(ImDrawList drawList, List<float[]> quads,
                       float originX, float originY, float viewWidth, float viewHeight) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (final float[] quad : quads) {
            for (int c = 0; c < 4; c++) {
                minX = Math.min(minX, quad[c * 3]);
                minY = Math.min(minY, quad[c * 3 + 1]);
                minZ = Math.min(minZ, quad[c * 3 + 2]);
                maxX = Math.max(maxX, quad[c * 3]);
                maxY = Math.max(maxY, quad[c * 3 + 1]);
                maxZ = Math.max(maxZ, quad[c * 3 + 2]);
            }
        }
        final float centerX = (minX + maxX) / 2F;
        final float centerY = (minY + maxY) / 2F;
        final float centerZ = (minZ + maxZ) / 2F;
        final float extent = Math.max(1, Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)));
        final float scale = Math.min(viewWidth, viewHeight) * 0.8F / extent * zoom;

        final float cosYaw = (float) Math.cos(yaw);
        final float sinYaw = (float) Math.sin(yaw);
        final float cosPitch = (float) Math.cos(pitch);
        final float sinPitch = (float) Math.sin(pitch);
        final float screenCx = originX + viewWidth / 2F;
        final float screenCy = originY + viewHeight / 2F;

        final List<float[]> projected = new ArrayList<>(quads.size());
        for (final float[] quad : quads) {
            final float[] out = new float[11];
            float depth = 0;
            for (int c = 0; c < 4; c++) {
                final float wx = quad[c * 3] - centerX;
                final float wy = quad[c * 3 + 1] - centerY;
                final float wz = quad[c * 3 + 2] - centerZ;
                final float rx = wx * cosYaw - wz * sinYaw;
                final float rz = wx * sinYaw + wz * cosYaw;
                final float ry = wy * cosPitch - rz * sinPitch;
                final float rd = wy * sinPitch + rz * cosPitch;
                out[c * 2] = screenCx + rx * scale;
                out[c * 2 + 1] = screenCy - ry * scale;
                depth += rd;
            }
            out[8] = depth / 4F;
            out[9] = quad[12];
            out[10] = quad[13];
            projected.add(out);
        }
        projected.sort((a, b) -> Float.compare(a[8], b[8]));

        drawList.pushClipRect(originX, originY, originX + viewWidth, originY + viewHeight, true);
        for (final float[] q : projected) {
            final int rgb = (int) q[9];
            final float shade = FACE_SHADE[((int) q[10]) % 6];
            final int r = (int) (((rgb >> 16) & 0xFF) * shade);
            final int g = (int) (((rgb >> 8) & 0xFF) * shade);
            final int b = (int) ((rgb & 0xFF) * shade);
            drawList.addQuadFilled(q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7],
                0xFF000000 | (b << 16) | (g << 8) | r);
        }
        drawList.popClipRect();
    }
}
