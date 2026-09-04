package cn.jstxjf_.rte;

import cn.jstxjf_.rte.item.RTEItems;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.ItemConvertible;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.registry.CreativeModeTabHolder;

public final class RTECreativeModeTabs {

    public static final CreativeModeTabHolder CORE = RTE.REGISTRY.createCreativeModeTabHolder(
        new Identifier(RTE.MOD_ID, "core"),
        () -> new ItemStack(new ItemConvertible(RTEItems.COMPOUND_CREATOR.get().data))
    );

    public static final CreativeModeTabHolder EYE_CANDY = RTE.REGISTRY.createCreativeModeTabHolder(
        new Identifier(RTE.MOD_ID, "eye_candy"),
        () -> new ItemStack(new ItemConvertible(cn.jstxjf_.rte.block.RTEBlocks.EYE_CANDY.get().data))
    );

    private RTECreativeModeTabs() {
    }
}
