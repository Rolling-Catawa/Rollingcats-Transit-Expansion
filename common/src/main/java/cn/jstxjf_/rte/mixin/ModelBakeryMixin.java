package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.EyeCandyRegistry;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Set;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/DefaultedRegistry;keySet()Ljava/util/Set;"
        )
    )
    private Set<ResourceLocation> rte$includeEyeCandyItemModels(DefaultedRegistry<Item> registry) {
        final Set<ResourceLocation> keys = new HashSet<>(registry.keySet());
        keys.addAll(EyeCandyRegistry.getModelLocations());
        return keys;
    }
}
