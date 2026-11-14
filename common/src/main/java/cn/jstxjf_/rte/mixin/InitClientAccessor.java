package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mod.InitClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InitClient.class, remap = false)
public interface InitClientAccessor {

    @Accessor("lastClientWorld")
    static void rte$setLastClientWorld(ClientWorld world) {
        throw new AssertionError();
    }
}
