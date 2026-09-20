package cn.jstxjf_.rte.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.ItemSettings;

import java.util.List;

public class BlockItemFreeNode extends BlockItemDirectNode {

    public BlockItemFreeNode(Block block, ItemSettings itemSettings) {
        super(block, itemSettings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("tooltip.rte.free_node"));
    }
}
