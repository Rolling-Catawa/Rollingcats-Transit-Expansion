package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.vertex.VertexAttributeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VertexAttributeState.class, remap = false)
public interface MtrVertexAttributeStateAccessor {

    @Accessor("color")
    Integer rte$getColor();

    @Accessor("lightmapUV")
    Integer rte$getLightmapUV();

    @Accessor("overlayUV")
    Integer rte$getOverlayUV();

    @Accessor("position")
    Vector3f rte$getPosition();

    @Accessor("textureU")
    Float rte$getTextureU();

    @Accessor("textureV")
    Float rte$getTextureV();

    @Accessor("normal")
    Vector3f rte$getNormal();

    @Accessor("matrix4f")
    Matrix4f rte$getMatrix();
}
