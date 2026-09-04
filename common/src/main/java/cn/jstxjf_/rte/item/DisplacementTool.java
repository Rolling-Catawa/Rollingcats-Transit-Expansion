package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.RTE;
import org.mtr.core.Main;
import org.mtr.core.data.Data;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.simulation.Simulator;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mod.Init;
import org.mtr.mod.block.BlockNode;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DisplacementTool extends ItemExtension {

    private static Field initMainField;
    private static Field worldIdListField;
    private static Field mainSimulatorsField;

    public DisplacementTool(ItemSettings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        final World world = context.getWorld();
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!(context.getWorld().getBlockState(context.getBlockPos()).getBlock().data instanceof BlockNode)) {
            return ActionResult.PASS;
        }

        final ServerPlayerEntity player = ServerPlayerEntity.cast(context.getPlayer());
        if (player == null) {
            return ActionResult.PASS;
        }

        try {
            final Simulator simulator = findSimulator(player);
            if (simulator == null) {
                return ActionResult.SUCCESS;
            }

            final Position startPosition = Init.blockPosToPosition(context.getBlockPos());
            final Map<Position, Rail> connectedRails = simulator.positionsToRail.get(startPosition);
            if (connectedRails == null || connectedRails.isEmpty()) {
                return ActionResult.SUCCESS;
            }

            final net.minecraft.server.level.ServerPlayer nativePlayer = (net.minecraft.server.level.ServerPlayer) player.data;
            final net.minecraft.core.BlockPos clickedPos = context.getBlockPos().data;
            final Map.Entry<Position, Rail> selectedEntry = connectedRails.entrySet().stream()
                    .min(Comparator.comparingDouble(entry -> net.minecraft.util.Mth.degreesDifferenceAbs(
                            (float) -Math.toDegrees(Math.atan2(entry.getKey().getX() - clickedPos.getX(), entry.getKey().getZ() - clickedPos.getZ())),
                            nativePlayer.getYRot()
                    )))
                    .orElse(null);
            if (selectedEntry == null) {
                return ActionResult.SUCCESS;
            }

            final Rail rail = selectedEntry.getValue();
            final Position endPosition = selectedEntry.getKey();
            final cn.jstxjf_.rte.data.RailExtraSupplier railExtra = (cn.jstxjf_.rte.data.RailExtraSupplier) (Object) rail;
            final Position railPosition1 = railExtra.getExtraPosition1();
            final boolean clickedIsRailPosition1 = railPosition1 != null && railPosition1.equals(startPosition);
            final double railLength = rail.railMath.getLength();
            final org.mtr.core.tool.Vector startVector = railExtra.railMathPositionFromP1(clickedIsRailPosition1 ? 0 : railLength);
            final org.mtr.core.tool.Vector endVector = railExtra.railMathPositionFromP1(clickedIsRailPosition1 ? railLength : 0);
            final net.minecraft.world.phys.Vec3 relativeOffset = nativePlayer.position().subtract(startVector.x, startVector.y, startVector.z);
            final double rotation = rail.getStartAngle(endPosition).getOpposite().angleRadians - rail.getStartAngle(startPosition).angleRadians;
            final net.minecraft.world.phys.Vec3 displacedPosition = relativeOffset.yRot((float) -rotation).add(endVector.x, endVector.y, endVector.z);

            nativePlayer.server.execute(() -> nativePlayer.teleportTo(
                    nativePlayer.serverLevel(),
                    displacedPosition.x,
                    displacedPosition.y,
                    displacedPosition.z,
                    nativePlayer.getYRot() + (float) Math.toDegrees(rotation),
                    nativePlayer.getXRot()
            ));
        } catch (Exception e) {
            RTE.LOGGER.error("Failed displacing player along rail", e);
        }

        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private static Simulator findSimulator(ServerPlayerEntity player) throws Exception {
        if (initMainField == null) {
            initMainField = Init.class.getDeclaredField("main");
            initMainField.setAccessible(true);
        }
        if (worldIdListField == null) {
            worldIdListField = Init.class.getDeclaredField("WORLD_ID_LIST");
            worldIdListField.setAccessible(true);
        }
        if (mainSimulatorsField == null) {
            mainSimulatorsField = Main.class.getDeclaredField("simulators");
            mainSimulatorsField.setAccessible(true);
        }

        final Main main = (Main) initMainField.get(null);
        if (main == null) {
            return null;
        }

        final List<String> worldIds = (List<String>) worldIdListField.get(null);
        final String worldId = Init.getWorldId(player.getEntityWorld());
        final int worldIndex = worldIds.indexOf(worldId);
        if (worldIndex < 0) {
            return null;
        }

        final List<Simulator> simulators = (List<Simulator>) mainSimulatorsField.get(main);
        return worldIndex < simulators.size() ? simulators.get(worldIndex) : null;
    }
}
