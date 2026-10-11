package cn.jstxjf_.rte.mixin;

import org.mtr.mod.render.RenderRails;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BooleanSupplier;

@Mixin(value = RenderRails.class, remap = false)
public interface RenderRailsNodeInvoker {

    @Invoker("renderNode")
    static void rte$invokeRenderNode(org.mtr.mapping.holder.BlockState blockState, org.mtr.mapping.holder.BlockPos blockPos, BooleanSupplier shouldRender, int light) {
        throw new AssertionError();
    }
}
