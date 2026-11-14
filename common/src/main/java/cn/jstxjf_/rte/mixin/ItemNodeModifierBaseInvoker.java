package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mod.item.ItemNodeModifierBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ItemNodeModifierBase.class, remap = false)
public interface ItemNodeModifierBaseInvoker {

    @Invoker(value = "onRemove", remap = false)
    void rte$invokeOnRemove(World world, BlockPos posStart, BlockPos posEnd, ServerPlayerEntity player);
}
