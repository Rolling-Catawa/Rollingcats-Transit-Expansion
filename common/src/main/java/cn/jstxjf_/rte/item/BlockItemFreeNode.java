package cn.jstxjf_.rte.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.holder.ItemSettings;
import cn.jstxjf_.rte.util.FreeNodeManager;

import java.util.List;

public class BlockItemFreeNode extends BlockItemDirectNode {

    public BlockItemFreeNode(Block block, ItemSettings itemSettings) {
        super(block, itemSettings);
    }

    @Override
    //#if MC>=12101
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, List<Component> list, TooltipFlag flag) {
    //#else
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
    //#endif
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("tooltip.rte.free_node"));
        list.add(Component.translatable("tooltip.rte.free_node.controls"));
    }

    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if (context.getWorld().isClient()) {
            return ActionResult.SUCCESS;
        }
        if (!(context.getWorld().data instanceof net.minecraft.server.level.ServerLevel level) || context.getPlayer() == null) {
            return ActionResult.FAIL;
        }
        final net.minecraft.world.phys.Vec3 hit = cn.jstxjf_.rte.bridge.MtrTypes.vec(context.getHitPos());
        final net.minecraft.world.phys.Vec3 point = new net.minecraft.world.phys.Vec3(hit.x, hit.y, hit.z);
        final net.minecraft.core.Direction face = (net.minecraft.core.Direction) context.getSide().data;
        final net.minecraft.world.entity.player.Player player = (net.minecraft.world.entity.player.Player) context.getPlayer().data;
        final net.minecraft.world.item.ItemStack stack = (net.minecraft.world.item.ItemStack) context.getStack().data;
        return FreeNodeManager.place(level, point, face, player, stack) ? ActionResult.SUCCESS : ActionResult.FAIL;
    }
}
