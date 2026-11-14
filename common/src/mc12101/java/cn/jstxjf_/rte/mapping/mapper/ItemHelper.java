package cn.jstxjf_.rte.mapping.mapper;

import net.minecraft.network.chat.Component;
import cn.jstxjf_.rte.mapping.annotation.MappedMethod;
import cn.jstxjf_.rte.mapping.holder.ItemStack;
import cn.jstxjf_.rte.mapping.holder.MutableText;
import cn.jstxjf_.rte.mapping.holder.TooltipContext;
import cn.jstxjf_.rte.mapping.holder.World;
import cn.jstxjf_.rte.mapping.tool.DummyInterface;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface ItemHelper extends DummyInterface {

	@MappedMethod
	default void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
	}

	@Deprecated
	default void appendTooltipHelper(ItemStack stack, @Nullable World world, List<Component> tooltipList, TooltipContext options) {
		final List<MutableText> newTooltipList = new ArrayList<>();
		addTooltips(stack, world, newTooltipList, options);
		addTooltips(stack, world, newTooltipList, (net.minecraft.world.item.Item.TooltipContext) null);
		newTooltipList.forEach(mutableText -> tooltipList.add(mutableText.data));
	}

	default void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, @Nullable net.minecraft.world.item.Item.TooltipContext options) {
	}
}
