package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.mapper.BlockItemExtension;

import java.util.List;

public class BlockItemDirectNode extends BlockItemExtension {

    public BlockItemDirectNode(Block block, ItemSettings itemSettings) {
        super(block, itemSettings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        final CompoundTag tag = stack.getTagElement("BlockEntityTag");
        if (tag != null && tag.contains(BlockEntityDirectNode.KEY_ANGLE)) {
            list.add(Component.translatable("tooltip.rte.direct_node.bound", tag.getDouble(BlockEntityDirectNode.KEY_ANGLE)));
        } else {
            list.add(Component.translatable("tooltip.rte.direct_node.unbound"));
        }
    }
}
