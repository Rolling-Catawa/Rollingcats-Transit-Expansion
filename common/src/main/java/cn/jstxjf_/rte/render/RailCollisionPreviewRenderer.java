package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.util.RailCollisionManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.mtr.core.data.Rail;

import java.util.Collections;
import java.util.List;

public final class RailCollisionPreviewRenderer {

    private static Rail cachedRail;
    private static long cachedFingerprint;
    private static List<AABB> cachedBoxes = Collections.emptyList();

    private RailCollisionPreviewRenderer() {
    }

    public static void render(PoseStack poseStack, MultiBufferSource buffers) {
        final Rail rail = RailPicker.pickedRail;
        if (!(((Object) rail) instanceof RailExtraSupplier extra) || !extra.isCollisionEnabled()) {
            cachedRail = null;
            cachedBoxes = Collections.emptyList();
            return;
        }
        final long fingerprint = fingerprint(extra);
        if (cachedRail != rail || cachedFingerprint != fingerprint) {
            cachedRail = rail;
            cachedFingerprint = fingerprint;
            cachedBoxes = RailCollisionManager.createBoxes(extra);
        }
        final VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        for (final AABB box : cachedBoxes) {
            LevelRenderer.renderLineBox(poseStack, lines, box, 0.2F, 0.95F, 1F, 0.85F);
        }
    }

    public static void renderDebug(Level level, PoseStack poseStack, MultiBufferSource buffers, Vec3 cameraPos) {
        final double radius = 128;
        final AABB query = new AABB(
            cameraPos.x - radius, cameraPos.y - radius, cameraPos.z - radius,
            cameraPos.x + radius, cameraPos.y + radius, cameraPos.z + radius
        );
        final VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        for (final net.minecraft.world.phys.shapes.VoxelShape shape : RailCollisionManager.getShapes(level, query)) {
            if (!shape.isEmpty()) {
                LevelRenderer.renderLineBox(poseStack, lines, shape.bounds(), 0.2F, 0.95F, 1F, 0.85F);
            }
        }
    }

    private static long fingerprint(RailExtraSupplier extra) {
        long hash = Double.doubleToLongBits(extra.getLength());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionWidth());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionHeight());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionLength());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionSpacing());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionYOffset());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionSideOffset());
        hash = 31 * hash + RailCollisionManager.getColumns(extra).hashCode();
        hash = 31 * hash + extra.getCustomConfigs().getOrDefault(RailCollisionManager.CONFIG_LAYOUT, "").hashCode();
        hash = 31 * hash + extra.getPathMode();
        hash = 31 * hash + System.identityHashCode(extra.getBezierCurve());
        hash = 31 * hash + Double.doubleToLongBits(extra.getExtraAngle1Rad());
        hash = 31 * hash + Double.doubleToLongBits(extra.getExtraAngle2Rad());
        hash = 31 * hash + Double.doubleToLongBits(extra.getNodeXOffset1());
        hash = 31 * hash + Double.doubleToLongBits(extra.getNodeYOffset1());
        hash = 31 * hash + Double.doubleToLongBits(extra.getNodeZOffset1());
        hash = 31 * hash + Double.doubleToLongBits(extra.getNodeXOffset2());
        hash = 31 * hash + Double.doubleToLongBits(extra.getNodeYOffset2());
        hash = 31 * hash + Double.doubleToLongBits(extra.getNodeZOffset2());
        hash = 31 * hash + Double.doubleToLongBits(extra.getNodePitch1());
        hash = 31 * hash + Double.doubleToLongBits(extra.getNodePitch2());
        hash = 31 * hash + Float.floatToIntBits(extra.getVerticalCurveRadius());
        hash = 31 * hash + Float.floatToIntBits(extra.getRollingOffset());
        return 31 * hash + (extra.getRollAngleMap() == null ? 0 : extra.getRollAngleMap().hashCode());
    }
}
