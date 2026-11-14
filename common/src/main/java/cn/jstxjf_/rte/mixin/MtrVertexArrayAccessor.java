package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.render.object.VertexArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VertexArray.class, remap = false)
public interface MtrVertexArrayAccessor {

    @Accessor("id")
    int rte$getId();
}
