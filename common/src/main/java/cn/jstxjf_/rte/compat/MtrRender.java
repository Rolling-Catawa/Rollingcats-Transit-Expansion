package cn.jstxjf_.rte.compat;

import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.MoreRenderLayers;
import org.mtr.mod.render.QueuedRenderLayer;

import java.util.function.BiConsumer;

public final class MtrRender {

    private MtrRender() {
    }

    public static void scheduleRender(Identifier identifier, boolean priority, QueuedRenderLayer queuedRenderLayer, BiConsumer<GraphicsHolder, Vector3d> callback) {
        //#if MC>=12101
        MainRenderer.scheduleRender(identifier.data, priority, queuedRenderLayer, (matrixStack, vertexConsumer, offset) -> callback.accept(GraphicsHolder.of(matrixStack, vertexConsumer), new Vector3d(offset)));
        //#else
        MainRenderer.scheduleRender(identifier, priority, queuedRenderLayer, callback);
        //#endif
    }

    public static void drawTexture(GraphicsHolder graphicsHolder,
                                   double x1, double y1, double z1, double x2, double y2, double z2,
                                   double x3, double y3, double z3, double x4, double y4, double z4,
                                   Vector3d offset, float u1, float v1, float u2, float v2, Direction facing, int color, int light) {
        //#if MC>=12101
        IDrawing.drawTexture(graphicsHolder.matrixStack(), graphicsHolder.vertexConsumer(), x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, offset.data, u1, v1, u2, v2, facing.data, color, light);
        //#else
        IDrawing.drawTexture(graphicsHolder, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, offset, u1, v1, u2, v2, facing, color, light);
        //#endif
    }

    public static net.minecraft.client.renderer.RenderType exterior(Identifier texture) {
        //#if MC>=12101
        return MoreRenderLayers.getExterior(texture.data);
        //#else
        return MoreRenderLayers.getExterior(texture).data;
        //#endif
    }

    public static net.minecraft.client.renderer.RenderType interior(Identifier texture) {
        //#if MC>=12101
        return MoreRenderLayers.getInterior(texture.data);
        //#else
        return MoreRenderLayers.getInterior(texture).data;
        //#endif
    }
}
