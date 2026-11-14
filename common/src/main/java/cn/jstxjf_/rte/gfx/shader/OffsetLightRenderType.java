package cn.jstxjf_.rte.gfx.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class OffsetLightRenderType extends RenderType {

    private OffsetLightRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                  boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    static RenderType offsetBeaconBeam(ResourceLocation texture, boolean translucent) {
        return create(
            "rte_light_stage", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES,
            256, false, true,
            CompositeState.builder()
                .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(translucent ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                .setWriteMaskState(translucent ? COLOR_WRITE : COLOR_DEPTH_WRITE)
                .setLayeringState(POLYGON_OFFSET_LAYERING)
                .createCompositeState(false));
    }
}
