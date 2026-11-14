package cn.jstxjf_.rte.bridge;

import cn.jstxjf_.rte.compat.MtrNodes;

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

    //#if MC>=12101
    private static final Class<?> MTR_ENTRY_CLASS = org.mtr.MTR.class;
    //#else
    private static final Class<?> MTR_ENTRY_CLASS = Init.class;
    //#endif

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
        return MtrNodes.getAngle(new org.mtr.mapping.holder.BlockState(state));
    }

    public static void deleteRailNodeOnServer(ServerLevel level, BlockPos pos) {
        PacketDeleteData.sendDirectlyToServerRailNodePosition(new ServerWorld(level), position(pos));
    }

    private static java.lang.reflect.Field initMainField;
    private static java.lang.reflect.Field worldIdListField;
    private static java.lang.reflect.Field mainSimulatorsField;

    @SuppressWarnings("unchecked")
    public static org.mtr.core.simulation.Simulator simulator(Level level) throws Exception {
        if (level == null) {
            return null;
        }
        if (initMainField == null) {
            initMainField = MTR_ENTRY_CLASS.getDeclaredField("main");
            initMainField.setAccessible(true);
        }
        if (worldIdListField == null) {
            worldIdListField = MTR_ENTRY_CLASS.getDeclaredField("WORLD_ID_LIST");
            worldIdListField.setAccessible(true);
        }
        if (mainSimulatorsField == null) {
            mainSimulatorsField = org.mtr.core.Main.class.getDeclaredField("simulators");
            mainSimulatorsField.setAccessible(true);
        }
        final org.mtr.core.Main main = (org.mtr.core.Main) initMainField.get(null);
        if (main == null) {
            return null;
        }
        final java.util.List<String> worldIds = (java.util.List<String>) worldIdListField.get(null);
        final int worldIndex = worldIds.indexOf(worldId(level));
        if (worldIndex < 0) {
            return null;
        }
        final java.util.List<org.mtr.core.simulation.Simulator> simulators = (java.util.List<org.mtr.core.simulation.Simulator>) mainSimulatorsField.get(main);
        return worldIndex < simulators.size() ? simulators.get(worldIndex) : null;
    }
}
