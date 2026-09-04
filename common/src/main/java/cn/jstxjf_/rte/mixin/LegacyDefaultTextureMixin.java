package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mod.resource.ModelResourceLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(value = ModelResourceLoader.class, remap = false)
public class LegacyDefaultTextureMixin {

    private static boolean rte$reported = false;

    @Inject(method = "lambda$loadModel$1", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$defaultTextureA(Identifier baseTexture, String basePath, String textureName,
                                            CallbackInfoReturnable<Identifier> cir) {
        rte$resolve(baseTexture, textureName, cir);
    }

    @Inject(method = "lambda$loadModel$3", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rte$defaultTextureB(Identifier baseTexture, String basePath, String textureName,
                                            CallbackInfoReturnable<Identifier> cir) {
        rte$resolve(baseTexture, textureName, cir);
    }

    private static void rte$resolve(Identifier baseTexture, String textureName,
                                    CallbackInfoReturnable<Identifier> cir) {
        if (!ClientConfig.legacyDefaultTextureFallback || baseTexture == null || textureName == null) {
            return;
        }
        final String trimmed = textureName.trim();
        final int slash = Math.max(trimmed.lastIndexOf('/'), trimmed.lastIndexOf('\\'));
        if (slash < 0) {
            return;
        }
        if (!trimmed.substring(slash + 1).toLowerCase(Locale.ROOT).equals("default.png")) {
            return;
        }
        if (!rte$reported) {
            rte$reported = true;
            RTE.LOGGER.info("[LegacyTexture] material texture '{}' treated as the vehicle base texture", trimmed);
        }
        cir.setReturnValue(baseTexture);
    }
}
