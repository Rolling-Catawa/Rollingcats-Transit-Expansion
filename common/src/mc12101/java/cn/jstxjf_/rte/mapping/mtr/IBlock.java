package cn.jstxjf_.rte.mapping.mtr;

import cn.jstxjf_.rte.mapping.holder.BlockState;
import cn.jstxjf_.rte.mapping.holder.Direction;
import cn.jstxjf_.rte.mapping.holder.DirectionProperty;
import cn.jstxjf_.rte.mapping.holder.Property;

public final class IBlock {

	private IBlock() {
	}

	public static Direction getStatePropertySafe(BlockState state, DirectionProperty property) {
		return Direction.convert(org.mtr.block.IBlock.getStatePropertySafe(state.data, property.data));
	}

	public static <T extends Comparable<T>> T getStatePropertySafe(BlockState state, Property<T> property) {
		return org.mtr.block.IBlock.getStatePropertySafe(state.data, property.data);
	}
}
