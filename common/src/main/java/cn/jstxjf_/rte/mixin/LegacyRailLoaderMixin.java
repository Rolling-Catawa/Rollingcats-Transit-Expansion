package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.LegacyRailExtraMigration;
import org.mtr.core.data.Rail;
import org.mtr.legacy.data.LegacyRailLoader;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(value = LegacyRailLoader.class, remap = false)
public abstract class LegacyRailLoaderMixin {

    @Inject(method = "load", at = @At("TAIL"))
    private static void rte$restoreLegacyRailExtras(Path savePath, ObjectArraySet<Rail> rails, boolean threadedFileLoading, CallbackInfo ci) {
        try {
            LegacyRailExtraMigration.apply(savePath, rails);
        } catch (Exception e) {
            cn.jstxjf_.rte.RTE.LOGGER.error("[LegacyRailMigration] failed for {}", savePath, e);
        }
    }
}
