package cn.jstxjf_.rte.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mod.client.MinecraftClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientWorldSwitchMixin {

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void rte$resetMtrDataOnLogin(CallbackInfo ci) {
        rte$adoptNewLevel();
    }

    @Inject(method = "handleRespawn", at = @At("TAIL"))
    private void rte$resetMtrDataOnRespawn(CallbackInfo ci) {
        rte$adoptNewLevel();
    }

    @Unique
    private static void rte$adoptNewLevel() {
        final ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        MinecraftClientData.reset();
        //#if MC<12101
        InitClientAccessor.rte$setLastClientWorld(new ClientWorld(level));
        //#endif
    }
}
