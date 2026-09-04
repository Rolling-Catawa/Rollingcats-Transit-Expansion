package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackCompatibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(PackCompatibility.class)
public class PackCompatibilityMixin {

    private static final Set<Integer> rte$reportedFormats = new HashSet<>();

    @Inject(method = "forFormat", at = @At("RETURN"), cancellable = true, require = 0)
    private static void rte$acceptLegacyPackFormats(int format, PackType packType,
                                                    CallbackInfoReturnable<PackCompatibility> cir) {
        if (!ClientConfig.acceptLegacyPackFormats || packType != PackType.CLIENT_RESOURCES) {
            return;
        }
        if (cir.getReturnValue() == PackCompatibility.COMPATIBLE) {
            return;
        }
        if (rte$reportedFormats.add(format)) {
            RTE.LOGGER.info("[PackCompat] accepting resource pack format {} declared for an older game version", format);
        }
        cir.setReturnValue(PackCompatibility.COMPATIBLE);
    }
}
