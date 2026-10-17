package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.item.ItemBrush;
import org.mtr.mod.item.ItemRailModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemExtension.class, remap = false)
public abstract class ItemRailModifierMixin {

    @Inject(method = "useWithoutResult", at = @At("HEAD"), cancellable = true)
    private void rte$useWithoutResult(World world, PlayerEntity player, Hand hand, CallbackInfo ci) {
        if (!cn.jstxjf_.rte.ClientConfig.overrideMtrScreens) {
            return;
        }
        if (!((Object) this instanceof ItemRailModifier)) return;
        if (world.isClient()) return;

        net.minecraft.world.phys.HitResult hitResult = ((net.minecraft.world.entity.player.Player) player.data)
            .pick(20.0, 0.0f, false);
        if (hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) return;

        if (((net.minecraft.world.entity.player.Player) player.data).isShiftKeyDown()) {
            final cn.jstxjf_.rte.data.RailUndoManager.RailOperation operation =
                cn.jstxjf_.rte.data.RailUndoManager.pop(world.data,
                    ((net.minecraft.world.entity.player.Player) player.data).getUUID());
            if (operation == null) {
                player.sendMessage(new Text(TextHelper.translatable("gui.rte.rail_undo.empty").data), true);
            } else if (ServerPlayerEntity.isInstance(player)) {
                ((ItemNodeModifierBaseInvoker) this).rte$invokeOnRemove(
                    world, cn.jstxjf_.rte.bridge.MtrTypes.blockPos(operation.posStart()), cn.jstxjf_.rte.bridge.MtrTypes.blockPos(operation.posEnd()), ServerPlayerEntity.cast(player));
                player.sendMessage(new Text(TextHelper.translatable("gui.rte.rail_undo.done",
                    operation.posStart().getX() + "," + operation.posStart().getY() + "," + operation.posStart().getZ(),
                    operation.posEnd().getX() + "," + operation.posEnd().getY() + "," + operation.posEnd().getZ()).data), true);
            }
            ci.cancel();
            return;
        }

        ItemStack stack = player.getStackInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        int pathMode = tag.contains("rte_path_mode") ? tag.getInt("rte_path_mode") : tag.getInt("path_mode");
        pathMode = nextPathMode(pathMode);
        tag.putInt("path_mode", pathMode);
        tag.putInt("rte_path_mode", pathMode);
        cn.jstxjf_.rte.data.RailPathModeState.lastPathMode = pathMode;

        String msg = switch (pathMode) {
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> "tooltip.rte.rail.path_mode.bezier";
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> "tooltip.rte.rail.path_mode.manual_bezier";
            case RailExtraSupplier.PATH_MODE_CURVE_FITTING -> "tooltip.rte.rail.path_mode.fitting";
            default -> "tooltip.rte.rail.path_mode.original";
        };
        player.sendMessage(new Text(TextHelper.translatable(msg).data), true);
        ci.cancel();
    }

    @Inject(method = "useWithoutResult", at = @At("HEAD"), cancellable = true)
    private void rte$brushResetOnAirClick(World world, PlayerEntity player, Hand hand, CallbackInfo ci) {
        if (!cn.jstxjf_.rte.ClientConfig.overrideMtrScreens) {
            return;
        }
        if (!((Object) this instanceof ItemBrush)) return;
        if (world.isClient()) return;

        net.minecraft.world.phys.HitResult hitResult = ((net.minecraft.world.entity.player.Player) player.data)
            .pick(20.0, 0.0f, false);
        if (hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) return;

        ItemStack stack = player.getStackInHand(hand);
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return;
        }
        if (tag.contains(cn.jstxjf_.rte.util.RailBrushHelper.BRUSH_TAG)
            || tag.contains(cn.jstxjf_.rte.util.RailBrushHelper.LEGACY_BRUSH_TAG)) {
            tag.data.remove(cn.jstxjf_.rte.util.RailBrushHelper.BRUSH_TAG);
            tag.data.remove(cn.jstxjf_.rte.util.RailBrushHelper.LEGACY_BRUSH_TAG);
            player.sendMessage(new Text(TextHelper.translatable("gui.rte.brush.reset").data), true);
        }
        ci.cancel();
    }

    private static int nextPathMode(int current) {
        return switch (current) {
            case RailExtraSupplier.PATH_MODE_ORIGINAL -> RailExtraSupplier.PATH_MODE_AUTO_BEZIER;
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> RailExtraSupplier.PATH_MODE_MANUAL_BEZIER;
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> RailExtraSupplier.PATH_MODE_CURVE_FITTING;
            default -> RailExtraSupplier.PATH_MODE_ORIGINAL;
        };
    }
}
