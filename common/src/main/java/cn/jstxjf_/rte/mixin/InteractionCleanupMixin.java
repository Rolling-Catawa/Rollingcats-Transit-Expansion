package cn.jstxjf_.rte.mixin;

//#if MC>=11904
import net.minecraft.world.entity.Interaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Interaction.class)
public abstract class InteractionCleanupMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void rte$removeLegacyFreeNodeProxy(CallbackInfo ci) {
        final Interaction interaction = (Interaction) (Object) this;
        if (interaction.getTags().contains("rte_free_node_hitbox")) {
            interaction.discard();
        }
    }
}
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "net.minecraft.world.entity.Interaction")
public abstract class InteractionCleanupMixin {
}
//#endif
