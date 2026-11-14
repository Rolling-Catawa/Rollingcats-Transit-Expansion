package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.mechanism.RailScriptRuntime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourRedstoneMixin {

    @Inject(method = "isSignalSource", at = @At("HEAD"), cancellable = true)
    private void rte$mechanismSignalSource(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!RailScriptRuntime.REDSTONE_OUTPUTS.isEmpty() && ((Object) this) instanceof org.mtr.mod.block.BlockNode) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void rte$mechanismSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (RailScriptRuntime.REDSTONE_OUTPUTS.isEmpty() || !(((Object) this) instanceof org.mtr.mod.block.BlockNode) || !(getter instanceof Level level)) {
            return;
        }
        final Integer power = RailScriptRuntime.REDSTONE_OUTPUTS.get(RailScriptRuntime.redstoneKey(
            level.dimension().location().toString(), pos.getX(), pos.getY(), pos.getZ()));
        if (power != null) {
            cir.setReturnValue(power);
        }
    }
}
