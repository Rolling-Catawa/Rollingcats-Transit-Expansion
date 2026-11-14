package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.compat.MtrNodes;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode;
import cn.jstxjf_.rte.block.BlockFreeNode;
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

    //#if MC>=12101
    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$getOutlineShape2(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.BlockGetter world, net.minecraft.core.BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context, CallbackInfoReturnable<net.minecraft.world.phys.shapes.VoxelShape> cir) {
        if ((Object) this instanceof BlockFreeNode) {
            cir.setReturnValue(net.minecraft.world.phys.shapes.Shapes.empty());
        }
    }
    //#else
    @Inject(method = "getOutlineShape2", at = @At("HEAD"), cancellable = true)
    private void rte$getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if ((Object) this instanceof BlockFreeNode) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }
    //#endif

    //#if MC>=12101
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$onUse2(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<net.minecraft.world.InteractionResult> cir) {
        final ActionResult result = rte$onUse2Handle(new BlockState(state), new World(world), new BlockPos(pos), new PlayerEntity(player));
        if (result != null) {
            cir.setReturnValue(result.data);
        }
    }
    //#else
    @Inject(method = "onUse2", at = @At("HEAD"), cancellable = true)
    private void rte$onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        final ActionResult result = rte$onUse2Handle(state, world, pos, player);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
    //#endif

    private ActionResult rte$onUse2Handle(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClient()) return null;
        if (player == null || !player.isHolding(Items.BRUSH.get())) return null;

        net.minecraft.world.level.block.entity.BlockEntity blockEntity = world.data.getBlockEntity(
            new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ()));

        if (blockEntity instanceof BlockEntityDirectNode directNode) {
            boolean isConnected = false;
            try {
                isConnected = state.data.getValue(MtrNodes.IS_CONNECTED);
            } catch (Exception ignored) {}

            if (!isConnected) {
                net.minecraft.client.gui.screens.Screen screen = DirectNodeScreen.createScreen(
                    directNode, () -> null);
                if (screen != null) {
                    net.minecraft.client.Minecraft.getInstance().setScreen(screen);
                }
                return ActionResult.SUCCESS;
            }
        }

        Position posCore = new Position(pos.getX(), pos.getY(), pos.getZ());
        var railsAtPos = MinecraftClientData.getInstance().positionsToRail.get(posCore);
        if (railsAtPos == null || railsAtPos.isEmpty()) return null;

        final float yaw = ((net.minecraft.world.entity.player.Player) player.data).getYRot();
        Optional<Map.Entry<Position, Rail>> closestEntry = railsAtPos.entrySet().stream().min(Comparator.comparingDouble(entry ->
            Mth.degreesDifferenceAbs((float) -Math.toDegrees(Math.atan2(entry.getKey().getX() - pos.getX(), entry.getKey().getZ() - pos.getZ())), yaw)
        ));
        if (closestEntry.isEmpty()) return null;

        Rail pickedRail = closestEntry.get().getValue();
        Position endPos = closestEntry.get().getKey();
        net.minecraft.core.BlockPos pickedPosStart = pos.data;
        net.minecraft.core.BlockPos pickedPosEnd = new net.minecraft.core.BlockPos((int)endPos.getX(), (int)endPos.getY(), (int)endPos.getZ());

        RailPicker.pickedRail = pickedRail;
        RailPicker.pickedPosStart = pickedPosStart;
        RailPicker.pickedPosEnd = pickedPosEnd;

        if (player.isSneaking()) {
            Screen screen = cn.jstxjf_.rte.gui.BrushEditRailScreen.createScreen(
                pickedRail, pickedPosStart, pickedPosEnd, null);
            if (screen != null) {
                MinecraftClient.getInstance().openScreen(screen);
            }
            return ActionResult.SUCCESS;
        } else {
            CompoundTag brushTag = RailBrushHelper.getBrushTag();
            RailBrushHelper.applyBrushToPickedRail(pickedPosStart, pickedPosEnd, pickedRail, brushTag, true);
            return ActionResult.SUCCESS;
        }
    }

}
