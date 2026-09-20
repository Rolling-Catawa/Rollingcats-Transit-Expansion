package cn.jstxjf_.rte.mixin;

import org.mtr.mod.render.StoredMatrixTransformations;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Stack;

/**
 * Restores the Matrices#setIdentity method used by MTR 3 resource-pack
 * scripts. JCM 2.2.4 exposes its own Matrices implementation but omits that
 * legacy API, which otherwise makes every such vehicle fail while parsing.
 */
@Pseudo
@Mixin(targets = "com.lx862.mtrscripting.core.util.Matrices", remap = false)
public abstract class JcmMatricesLegacyMixin {

    @Shadow
    @Final
    private Stack<StoredMatrixTransformations> storedMatrixTransformations;

    public void setIdentity() {
        if (storedMatrixTransformations.isEmpty()) {
            storedMatrixTransformations.push(new StoredMatrixTransformations());
        } else {
            storedMatrixTransformations.set(
                storedMatrixTransformations.size() - 1,
                new StoredMatrixTransformations()
            );
        }
    }
}
