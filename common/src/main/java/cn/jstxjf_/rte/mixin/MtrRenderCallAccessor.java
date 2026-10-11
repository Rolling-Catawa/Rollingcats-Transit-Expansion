package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.vertex.VertexAttributeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "org.mtr.mapping.render.batch.BatchManager$RenderCall", remap = false)
public interface MtrRenderCallAccessor {

    @Accessor("vertexArray")
    VertexArray rte$getVertexArray();

    @Accessor("vertexAttributeState")
    VertexAttributeState rte$getVertexAttributeState();

    @Invoker("draw")
    void rte$draw();
}
