package cn.jstxjf_.rte.mixin;

import org.mtr.mod.render.StoredMatrixTransformations;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Stack;

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
