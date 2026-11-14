package cn.jstxjf_.rte.mapping.mtr;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.mapping.holder.BlockPos;
import cn.jstxjf_.rte.mapping.holder.ServerWorld;
import cn.jstxjf_.rte.mapping.holder.World;
import cn.jstxjf_.rte.mapping.registry.Registry;
import org.apache.logging.log4j.Logger;
import org.mtr.MTR;
import org.mtr.core.data.Position;
import org.mtr.data.RailActionModule;

import java.util.function.Consumer;

public final class Init {

	public static final Logger LOGGER = MTR.LOGGER;
	public static final Registry REGISTRY = RTE.REGISTRY;

	private Init() {
	}

	public static void getRailActionModule(ServerWorld serverWorld, Consumer<RailActionModule> consumer) {
		MTR.getRailActionModule(serverWorld.data, consumer);
	}

	public static BlockPos positionToBlockPos(Position position) {
		return new BlockPos(MTR.positionToBlockPos(position));
	}

	public static Position blockPosToPosition(BlockPos blockPos) {
		return MTR.blockPosToPosition(blockPos.data);
	}

	public static BlockPos newBlockPos(double x, double y, double z) {
		return new BlockPos(net.minecraft.core.BlockPos.containing(x, y, z));
	}

	public static String getWorldId(World world) {
		return MTR.getWorldId(world.data);
	}
}
