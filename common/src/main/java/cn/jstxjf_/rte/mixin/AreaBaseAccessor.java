package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.AreaBase;
import org.mtr.core.data.Siding;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AreaBase.class, remap = false)
public interface AreaBaseAccessor {

    @Accessor("savedRails")
    ObjectArraySet<Siding> rte$getSavedRails();
}
