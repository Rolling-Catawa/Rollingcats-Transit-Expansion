package cn.jstxjf_.rte.mixin;

import org.mtr.mod.data.RailType;
import org.mtr.mod.item.ItemRailModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemRailModifier.class, remap = false)
public interface ItemRailModifierAccessor {

    @Accessor("railType")
    RailType rte$getRailType();

    @Accessor("isOneWay")
    boolean rte$isOneWay();
}
