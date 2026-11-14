package cn.jstxjf_.rte.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import cn.jstxjf_.rte.mapping.annotation.MappedMethod;
import cn.jstxjf_.rte.mapping.holder.*;
import cn.jstxjf_.rte.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.List;

public class SlabBlockExtension extends SlabBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public SlabBlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText(net.minecraft.world.item.ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltipList, TooltipFlag options) {
		appendTooltipHelper(new ItemStack(stack), null, tooltipList, new cn.jstxjf_.rte.mapping.holder.TooltipContext(options));
	}

	@MappedMethod
	@Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
		properties.add(new Property<>(TYPE));
		properties.add(new Property<>(WATERLOGGED));
	}

	@MappedMethod
	public static SlabType getType(BlockState state) {
		return SlabType.convert(state.data.getValue(TYPE));
	}
}
