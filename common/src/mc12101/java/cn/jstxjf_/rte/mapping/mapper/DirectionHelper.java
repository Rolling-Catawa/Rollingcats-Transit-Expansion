package cn.jstxjf_.rte.mapping.mapper;

import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import cn.jstxjf_.rte.mapping.holder.DirectionProperty;

public interface DirectionHelper {

	DirectionProperty FACING = new DirectionProperty(HorizontalDirectionalBlock.FACING);
	DirectionProperty FACING_NORMAL = new DirectionProperty(DirectionalBlock.FACING);
}
