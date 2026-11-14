package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RailPicker;
import cn.jstxjf_.rte.util.RailBrushHelper;
import org.mtr.mapping.holder.*;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.item.ItemBrush;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemBrush.class, remap = false)
public abstract class ItemBrushMixin {

    //#if MC>=12101
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void rte$useOnBlock2(net.minecraft.world.item.context.UseOnContext useOnContext, CallbackInfoReturnable<net.minecraft.world.InteractionResult> cir) {
        if (rte$handleUseOnBlock(new ItemUsageContext(useOnContext))) {
            cir.setReturnValue(net.minecraft.world.InteractionResult.SUCCESS);
        }
    }
    //#else
    @Inject(method = "useOnBlock2", at = @At("HEAD"), cancellable = true)
    private void rte$useOnBlock2(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (rte$handleUseOnBlock(context)) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
    //#endif

    private boolean rte$handleUseOnBlock(ItemUsageContext context) {
        if (!cn.jstxjf_.rte.ClientConfig.rteBrushEditing) {
            return false;
        }
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock().data instanceof BlockNode)) return false;

        if (world.isClient()) {
            PlayerEntity player = context.getPlayer();
            RailPicker.pick();
            if (player != null && player.isSneaking()) {
                if (RailPicker.pickedRail != null) {
                    Screen screen = cn.jstxjf_.rte.gui.BrushEditRailScreen.createScreen(null);
                    if (screen != null) {
                        MinecraftClient.getInstance().openScreen(screen);
                    }
                }
            } else {
                if (RailPicker.pickedRail != null) {
                    CompoundTag brushTag = RailBrushHelper.getBrushTag();
                    RailBrushHelper.applyBrushToPickedRail(brushTag, true);
                }
            }
        }
        return true;
    }
}
