package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.AreaBase;
import org.mtr.core.data.SavedRailBase;
import org.mtr.mod.screen.SavedRailScreenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SavedRailScreenBase.class, remap = false)
public interface SavedRailScreenBaseAccessor {

    @Accessor("savedRailBase")
    SavedRailBase<?, ? extends AreaBase<?, ?>> rte$getSavedRailBase();
}
