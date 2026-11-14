package cn.jstxjf_.rte.render.train;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class VehicleLightRenderType extends RenderType {

    private static final Function<ResourceLocation, RenderType> GLOW = Util.memoize(texture -> create(
        "rte_vehicle_light_glow",
        //#if MC>=12101
        DefaultVertexFormat.POSITION_TEX_COLOR,
        //#else
        DefaultVertexFormat.POSITION_COLOR_TEX,
        //#endif
        VertexFormat.Mode.QUADS,
        1024,
        false,
        false,
        CompositeState.builder()
            //#if MC>=12101
            .setShaderState(new ShaderStateShard(net.minecraft.client.renderer.GameRenderer::getPositionTexColorShader))
            //#else
            .setShaderState(POSITION_COLOR_TEX_SHADER)
            //#endif
            .setTextureState(new TextureStateShard(texture, true, false))
            .setTransparencyState(LIGHTNING_TRANSPARENCY)
            .setWriteMaskState(COLOR_WRITE)
            .setCullState(NO_CULL)
            .createCompositeState(false)
    ));

    private static final RenderType EDITOR = create(
        "rte_vehicle_light_editor",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.QUADS,
        4096,
        false,
        false,
        CompositeState.builder()
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(NO_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .setCullState(NO_CULL)
            .createCompositeState(false)
    );

    private VehicleLightRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                   boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    public static RenderType glow(ResourceLocation texture) {
        return GLOW.apply(texture);
    }

    public static RenderType editor() {
        return EDITOR;
    }
}
