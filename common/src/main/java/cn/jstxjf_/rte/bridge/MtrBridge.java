package cn.jstxjf_.rte.bridge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.World;
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.packet.PacketDeleteData;
import org.mtr.mod.packet.PacketUpdateData;

public final class MtrBridge {

    private MtrBridge() {
    }

    public static Position position(BlockPos pos) {
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockPos blockPos(Position position) {
        return new BlockPos((int) position.getX(), (int) position.getY(), (int) position.getZ());
    }

    public static String worldId(Level level) {
        return Init.getWorldId(new World(level));
    }

    public static void sendRailToServer(ServerLevel level, Rail rail) {
        PacketUpdateData.sendDirectlyToServerRail(new ServerWorld(level), rail);
    }

    public static void deleteRailOnServer(ServerLevel level, String railId) {
        PacketDeleteData.sendDirectlyToServerRailId(new ServerWorld(level), railId);
    }

    public static boolean isNode(BlockState state) {
        return ((Object) state.getBlock()) instanceof BlockNode;
    }

    public static float nodeAngle(BlockState state) {
        return BlockNode.getAngle(new org.mtr.mapping.holder.BlockState(state));
    }
}
