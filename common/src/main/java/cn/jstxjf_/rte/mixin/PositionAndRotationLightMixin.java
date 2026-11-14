package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.util.LightSampler;
import net.minecraft.client.Minecraft;
import org.mtr.core.tool.Vector;
import org.mtr.mod.render.PositionAndRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PositionAndRotation.class, remap = false)
public abstract class PositionAndRotationLightMixin {

    @Inject(method = "getLight", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$sampleOutsideSolidBlocks(Vector position, CallbackInfoReturnable<Integer> cir) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.level == null || position == null) {
            return;
        }
        cir.setReturnValue(LightSampler.packed(minecraft.level, position.x(), position.y() + 1, position.z()));
    }
}
