package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RawPackAliases;
import cn.jstxjf_.rte.data.RawPackResources;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

    @Inject(method = "discoverAvailable", at = @At("RETURN"), cancellable = true, require = 0)
    private void rte$addRecoveryPack(CallbackInfoReturnable<Map<String, Pack>> cir) {
        try {
            final Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null) {
                return;
            }
            final PackRepository resourceRepository = minecraft.getResourcePackRepository();
            if (resourceRepository != null && resourceRepository != (Object) this) {
                return;
            }
            RawPackAliases.rebuild(minecraft.getResourcePackDirectory());
            if (RawPackAliases.isEmpty()) {
                return;
            }
            final Pack pack = RawPackResources.createPack();
            if (pack == null) {
                return;
            }
            final Map<String, Pack> augmented = new LinkedHashMap<>(cir.getReturnValue());
            augmented.put(RawPackAliases.PACK_ID, pack);
            cir.setReturnValue(augmented);
        } catch (Throwable e) {
            RTE.LOGGER.warn("[PathRecovery] could not register recovery pack: {}", e.toString());
        }
    }
}
