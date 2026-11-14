package cn.jstxjf_.rte.mixin;

import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "org.mtr.mapping.registry.EventRegistryClient$1", remap = false)
public abstract class EventRegistryClientReloadMixin {

    @Shadow(remap = false)
    public abstract void method_14491(ResourceManager resourceManager);

    public void onResourceManagerReload(ResourceManager resourceManager) {
        method_14491(resourceManager);
    }
}
