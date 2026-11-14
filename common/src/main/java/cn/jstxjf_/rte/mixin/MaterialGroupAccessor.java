package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.vertex.CapturingVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Consumer;

@Mixin(value = OptimizedModel.MaterialGroup.class, remap = false)
public interface MaterialGroupAccessor {
    @Accessor("materialProperties")
    MaterialProperties rte$getMaterialProperties();

    @Accessor("modelPartConsumers")
    List<Consumer<CapturingVertexConsumer>> rte$getModelPartConsumers();
}
