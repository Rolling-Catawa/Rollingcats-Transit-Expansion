package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.holder.*;
import org.mtr.mod.item.ItemBlockClickingBase;
import org.mtr.mod.item.ItemRailModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemBlockClickingBase.class, remap = false)
public abstract class ItemBlockClickingBaseMixin {

    @Inject(method = "useOnBlock2", at = @At("HEAD"))
    private void rte$useOnBlock2(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!((Object) this instanceof ItemRailModifier)) return;
        if (context == null) return;

        final ItemStack stack = context.getStack();
        if (stack == null || stack.isEmpty()) return;
        final CompoundTag tag = stack.getOrCreateTag();
        cn.jstxjf_.rte.data.RailPathModeState.lastPathMode = tag.contains("rte_path_mode") ? tag.getInt("rte_path_mode") : tag.getInt("path_mode");
    }
}
