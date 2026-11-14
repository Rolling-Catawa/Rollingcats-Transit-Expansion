package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.MapRailArchive;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClearLevelMixin {

    //#if MC>=12101
    @Inject(method = "clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"), require = 0)
    //#else
    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"), require = 0)
    //#endif
    private void rte$flushMapArchiveOnLeave(CallbackInfo ci) {
        MapRailArchive.flush();
    }

    @Inject(method = "close", at = @At("HEAD"), require = 0)
    private void rte$flushMapArchiveOnClose(CallbackInfo ci) {
        MapRailArchive.flush();
    }
}
