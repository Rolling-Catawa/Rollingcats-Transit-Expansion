package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mod.resource.OptimizedModelWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = OptimizedModelWrapper.class, remap = false)
public interface OptimizedModelWrapperInvoker {
    @Invoker("<init>")
    static OptimizedModelWrapper rte$create(OptimizedModel optimizedModel) {
        throw new AssertionError();
    }
}
