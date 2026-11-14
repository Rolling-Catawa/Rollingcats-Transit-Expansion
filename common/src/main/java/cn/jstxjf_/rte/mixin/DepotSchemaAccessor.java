package cn.jstxjf_.rte.mixin;

import org.mtr.core.generated.data.DepotSchema;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DepotSchema.class, remap = false)
public interface DepotSchemaAccessor {

    @Accessor("lastGeneratedFailedSidingCount")
    void rte$setLastGeneratedFailedSidingCount(long value);
}
