package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = OptimizedModel.class, remap = false)
public interface OptimizedModelAccessor {
    @Accessor("DEFAULT_MAPPING")
    static VertexAttributeMapping rte$getDefaultMapping() {
        throw new AssertionError();
    }
}
