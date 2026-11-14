package cn.jstxjf_.rte.mixin;

import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShaderInstance.class)
public interface ShaderInstanceAccessor {
    @Accessor("programId")
    int getProgramId();

    @Accessor("lastProgramId")
    static int getLastProgramId() {
        throw new AssertionError();
    }

    @Accessor("lastProgramId")
    static void setLastProgramId(int value) {
        throw new AssertionError();
    }
}
