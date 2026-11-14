package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.mechanism.Mechanism;
import cn.jstxjf_.rte.mechanism.RailScriptRuntime;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mod.block.BlockNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockNode.class, remap = false)
public abstract class BlockNodeMechanismUseMixin {

    //#if MC>=12101
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$mechanismUse(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<net.minecraft.world.InteractionResult> cir) {
        final ActionResult result = rte$mechanismUseHandle(new BlockState(state), new World(world), new BlockPos(pos), new PlayerEntity(player));
        if (result != null) {
            cir.setReturnValue(result.data);
        }
    }
    //#else
    @Inject(method = "onUse2", at = @At("HEAD"), cancellable = true, require = 0)
    private void rte$mechanismUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        final ActionResult result = rte$mechanismUseHandle(state, world, pos, player);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
    //#endif

    private ActionResult rte$mechanismUseHandle(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient() || player == null || RailScriptRuntime.GATE_INDEX.isEmpty()
            || !(player.data instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
            || !serverPlayer.getMainHandItem().isEmpty()
            || !(world.data instanceof net.minecraft.server.level.ServerLevel level)) {
            return null;
        }
        final net.minecraft.core.BlockPos nativePos = new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ());
        final RailScriptRuntime.LevelState levelState = RailScriptRuntime.get(level.getServer()).levelState(level);
        final Mechanism mechanism = levelState.mechanismAt(nativePos);
        if (mechanism == null) {
            return null;
        }
        levelState.deliverUse(mechanism.deckRailId, serverPlayer, nativePos);
        return ActionResult.SUCCESS;
    }

}
