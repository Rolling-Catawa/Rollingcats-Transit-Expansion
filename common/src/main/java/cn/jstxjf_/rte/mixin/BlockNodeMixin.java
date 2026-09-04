package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode;
import cn.jstxjf_.rte.gui.DirectNodeScreen;
import cn.jstxjf_.rte.render.RailPicker;
import cn.jstxjf_.rte.util.RailBrushHelper;
import org.mtr.mapping.holder.*;
import org.mtr.mod.Items;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.core.data.Rail;
import org.mtr.core.data.Position;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.Mth;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Mixin(value = BlockNode.class, remap = false)
public abstract class BlockNodeMixin {

    @Inject(method = "onUse2", at = @At("HEAD"), cancellable = true)
    private void rte$onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient()) return;
        if (player == null || !player.isHolding(Items.BRUSH.get())) return;

        net.minecraft.world.level.block.entity.BlockEntity blockEntity = world.data.getBlockEntity(
            new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ()));

        if (blockEntity instanceof BlockEntityDirectNode directNode) {
            boolean isConnected = false;
            try {
                isConnected = state.data.getValue(BlockNode.IS_CONNECTED.data);
            } catch (Exception ignored) {}

            if (!isConnected) {
                net.minecraft.client.gui.screens.Screen screen = DirectNodeScreen.createScreen(
                    directNode, () -> null);
                if (screen != null) {
                    net.minecraft.client.Minecraft.getInstance().setScreen(screen);
                }
                cir.setReturnValue(ActionResult.SUCCESS);
                return;
            }
        }

        Position posCore = new Position(pos.getX(), pos.getY(), pos.getZ());
        var railsAtPos = MinecraftClientData.getInstance().positionsToRail.get(posCore);
        if (railsAtPos == null || railsAtPos.isEmpty()) return;

        final float yaw = ((net.minecraft.world.entity.player.Player) player.data).getYRot();
        Optional<Map.Entry<Position, Rail>> closestEntry = railsAtPos.entrySet().stream().min(Comparator.comparingDouble(entry ->
            Mth.degreesDifferenceAbs((float) -Math.toDegrees(Math.atan2(entry.getKey().getX() - pos.getX(), entry.getKey().getZ() - pos.getZ())), yaw)
        ));
        if (closestEntry.isEmpty()) return;

        Rail pickedRail = closestEntry.get().getValue();
        Position endPos = closestEntry.get().getKey();
        BlockPos pickedPosStart = pos;
        BlockPos pickedPosEnd = new BlockPos((int)endPos.getX(), (int)endPos.getY(), (int)endPos.getZ());

        RailPicker.pickedRail = pickedRail;
        RailPicker.pickedPosStart = pickedPosStart;
        RailPicker.pickedPosEnd = pickedPosEnd;

        if (player.isSneaking()) {
            Screen screen = cn.jstxjf_.rte.gui.BrushEditRailScreen.createScreen(
                pickedRail, pickedPosStart, pickedPosEnd, null);
            if (screen != null) {
                MinecraftClient.getInstance().openScreen(screen);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        } else {
            CompoundTag brushTag = RailBrushHelper.getBrushTag();
            RailBrushHelper.applyBrushToPickedRail(pickedPosStart, pickedPosEnd, pickedRail, brushTag, true);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
