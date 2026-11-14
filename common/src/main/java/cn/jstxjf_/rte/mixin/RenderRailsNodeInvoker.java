package cn.jstxjf_.rte.mixin;

import org.mtr.mod.render.RenderRails;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BooleanSupplier;

@Mixin(value = RenderRails.class, remap = false)
public interface RenderRailsNodeInvoker {

    @Invoker("renderNode")
    //#if MC>=12101
    static void rte$invokeRenderNode(net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.BlockPos blockPos, BooleanSupplier shouldRender, int light) {
    //#else
    static void rte$invokeRenderNode(org.mtr.mapping.holder.BlockState blockState, org.mtr.mapping.holder.BlockPos blockPos, BooleanSupplier shouldRender, int light) {
    //#endif
        throw new AssertionError();
    }
}
