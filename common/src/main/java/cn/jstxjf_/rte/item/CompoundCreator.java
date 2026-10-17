package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.mixin.RailActionModuleAccessor;

import cn.jstxjf_.rte.network.RTEPackets;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.*;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.Init;
import org.mtr.mod.data.RailType;
import org.mtr.mod.item.ItemNodeModifierBase;
import org.mtr.mod.packet.PacketDeleteData;
import org.mtr.mod.packet.PacketUpdateData;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CompoundCreator extends ItemNodeModifierBase {
    
    public CompoundCreator(ItemSettings itemSettings) {
        super(true, false, false, true, itemSettings);
    }
    
    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if (clickCondition(context)) {
            return super.useOnBlock2(context);
        } else {
            if (!context.getWorld().isClient()) {
                PlayerEntity user = context.getPlayer();
                if (ServerPlayerEntity.isInstance(user)) {
                     RTEPackets.sendOpenCompoundCreatorScreen((net.minecraft.server.level.ServerPlayer) ServerPlayerEntity.cast(user).data, context.getHand());
                }
            }
            return ActionResult.SUCCESS;
        }
    }
    
    @Override
    public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            if (ServerPlayerEntity.isInstance(user)) {
                RTEPackets.sendOpenCompoundCreatorScreen((net.minecraft.server.level.ServerPlayer) ServerPlayerEntity.cast(user).data, hand);
            }
        }
    }
    
    @Override
    protected void onConnect(World world, ItemStack stack, TransportMode transportMode, 
                             BlockState stateStart, BlockState stateEnd,
                             BlockPos posStart, BlockPos posEnd,
                             Angle facingStart, Angle facingEnd,
                             ServerPlayerEntity player) {
        if (player == null) return;
        
        final List<CompoundCreatorLogic.Task> tasks = CompoundCreatorLogic.getTasks(stack);
        if (tasks.isEmpty()) {
            player.sendMessage(new Text(TextHelper.translatable("gui.rte.compound_creator.msg.no_tasks").data), true);
            return;
        }

        ItemNodeModifierBase.getRail(world, posStart, posEnd, player, rail -> {
            if (rail == null) {
                ItemNodeModifierBase.getRail(world, posEnd, posStart, player, reversedRail -> {
                    if (reversedRail == null) {
                        player.sendMessage(new Text(TextHelper.translatable("gui.rte.compound_creator.msg.rail_not_found").data), true);
                        return;
                    }
                    executeCompoundTasks(world, player, reversedRail, tasks);
                });
                return;
            }
            executeCompoundTasks(world, player, rail, tasks);
        });
    }
    
    private void executeCompoundTasks(World world, ServerPlayerEntity player, Rail rail, List<CompoundCreatorLogic.Task> tasks) {
        final ServerWorld serverWorld = new ServerWorld((net.minecraft.server.level.ServerLevel) world.data);
        final boolean[] moduleFound = {false};
        final boolean[] queuedAction = {false};

        Init.getRailActionModule(serverWorld, railActionModule -> {
            moduleFound[0] = true;
            final RailActionModuleAccessor accessor = (RailActionModuleAccessor) railActionModule;
            for (final CompoundCreatorLogic.Task task : tasks) {
                if (task instanceof CompoundCreatorLogic.SliceTask sliceTask) {
                    accessor.getRailActions().add(new SliceRailAction(serverWorld, player, rail, sliceTask));
                    queuedAction[0] = true;
                } else if (task instanceof CompoundCreatorLogic.RailModifierTask modTask) {
                    executeRailModifierTask(world, player, rail, modTask);
                }
            }
            if (queuedAction[0]) {
                accessor.invokeBroadcastUpdate();
            }
        });

        if (!moduleFound[0]) {
            for (final CompoundCreatorLogic.Task task : tasks) {
                if (task instanceof CompoundCreatorLogic.SliceTask sliceTask) {
                    executeSliceTask(world, player, rail, sliceTask);
                } else if (task instanceof CompoundCreatorLogic.RailModifierTask modTask) {
                    executeRailModifierTask(world, player, rail, modTask);
                }
            }
        }
    }

    @Override
    protected void onRemove(World world, BlockPos posStart, BlockPos posEnd, ServerPlayerEntity player) {
    }
    
    private void executeSliceTask(World world, ServerPlayerEntity player, Rail rail, CompoundCreatorLogic.SliceTask task) {
        RailExtraSupplier railExtra = (RailExtraSupplier) (Object) rail;
        double length = railExtra.getLength();
        double distance = task.start;
        double endDistance = task.length != null ? task.start + task.length : length;
        double increment = Math.max(0.1, task.increment);
        
        int width = task.width;
        int height = task.height;
        
        while (distance < endDistance && distance < length) {
            Vec3 pos = railExtra.getPosition(distance);
            Vec3 nextPos = railExtra.getPosition(Math.min(distance + 0.1, length));
            
            float yaw = 0, pitch = 0, roll = 0;
            if (task.useYaw) {
                yaw = (float) Math.atan2(nextPos.x - pos.x, nextPos.z - pos.z);
            }
            if (task.usePitch) {
                double dx = nextPos.x - pos.x;
                double dz = nextPos.z - pos.z;
                double hDist = Math.sqrt(dx * dx + dz * dz);
                pitch = (float) Math.atan2(nextPos.y - pos.y, hDist);
            }
            if (task.useRoll && ((Object)rail) instanceof RailExtraSupplier) {
                roll = RailExtraSupplier.getRollAngle(rail, distance);
            }
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int idx = y * width + x;
                    if (idx >= task.lumps.size()) continue;
                    
                    CompoundCreatorLogic.Lump lump = task.lumps.get(idx);
                    if (lump.blockState == null) continue;
                    
                    int offsetX = x - width / 2;
                    int offsetY = y - height / 2;
                    
                    double bx = pos.x + offsetX * Math.cos(yaw);
                    double by = pos.y + offsetY;
                    double bz = pos.z + offsetX * Math.sin(yaw);
                    
                    BlockPos blockPos = new BlockPos((int)Math.floor(bx), (int)Math.floor(by), (int)Math.floor(bz));
                    
                    if (!lump.replacement && !world.getBlockState(blockPos).isAir()) continue;
                    
                    world.setBlockState(blockPos, lump.blockState, 3);
                }
            }
            
            distance += increment;
        }
        
        player.sendMessage(new Text(TextHelper.translatable("gui.rte.compound_creator.msg.slice_done").data), true);
    }
    
    private void executeRailModifierTask(World world, ServerPlayerEntity player, Rail rail, CompoundCreatorLogic.RailModifierTask task) {
        final RailExtraSupplier targetExtra = (RailExtraSupplier) (Object) rail;
        final Rail templateRail = CompoundCreatorLogic.RailModifierTask.copyRail(task.rail);
        final RailExtraSupplier templateExtra = (RailExtraSupplier) (Object) templateRail;
        Position position1 = targetExtra.getExtraPosition1();
        Position position2 = targetExtra.getExtraPosition2();
        Angle angle1 = targetExtra.getExtraAngle1();
        Angle angle2 = targetExtra.getExtraAngle2();

        if (position1 == null || position2 == null || angle1 == null || angle2 == null) {
            player.sendMessage(new Text(TextHelper.translatable("gui.rte.compound_creator.msg.modifier_failed").data), true);
            return;
        }

        if (task.isReversed) {
            final Position tempPosition = position1;
            position1 = position2;
            position2 = tempPosition;

            final Angle tempAngle = angle1;
            angle1 = angle2;
            angle2 = tempAngle;
        }

        final Rail replacement = createReplacementRail(targetExtra, templateRail, templateExtra, position1, angle1, position2, angle2, task.isOneWay);

        replacement.copySignalColors(rail);

        if (((Object) replacement) instanceof RailExtraSupplier replacementExtra && ((Object) templateRail) instanceof RailExtraSupplier tplExtra) {
            replacementExtra.partialCopyFrom(tplExtra);
            replacementExtra.setRenderReversed(task.isReversed);
            replacementExtra.setRailType(tplExtra.getRailType());
            replacementExtra.setSpeedLimitKph(tplExtra.getSpeedLimitKph());
            restoreTargetGeometry(replacementExtra, targetExtra, task.isReversed);
        }

        if (!replacement.isValid()) {
            player.sendMessage(new Text(TextHelper.translatable("gui.mtr.invalid_orientation").data), true);
            return;
        }

        final Rail finalReplacement = RailRebuildHelper.rebuildFromCurrentState(replacement);
        if (!finalReplacement.isValid()) {
            player.sendMessage(new Text(TextHelper.translatable("gui.mtr.invalid_orientation").data), true);
            return;
        }

        final ServerWorld serverWorld = ServerWorld.cast(world);
        final Rail originalReverse = targetExtra.getTransposition();
        final Rail replacementReverse = ((RailExtraSupplier)(Object)finalReplacement).getTransposition();
        final Set<String> deletedRailIds = new LinkedHashSet<>();
        if (!rail.getHexId().equals(finalReplacement.getHexId()) && deletedRailIds.add(rail.getHexId())) {
            PacketDeleteData.sendDirectlyToServerRailId(serverWorld, rail.getHexId());
        }
        if (!originalReverse.getHexId().equals(replacementReverse.getHexId()) && deletedRailIds.add(originalReverse.getHexId())) {
            PacketDeleteData.sendDirectlyToServerRailId(serverWorld, originalReverse.getHexId());
        }
        PacketUpdateData.sendDirectlyToServerRail(serverWorld, finalReplacement);
        if (!finalReplacement.getHexId().equals(replacementReverse.getHexId())) {
            PacketUpdateData.sendDirectlyToServerRail(serverWorld, replacementReverse);
        }
        player.sendMessage(new Text(TextHelper.translatable("gui.rte.compound_creator.msg.modifier_applied").data), true);
    }

    private static void restoreTargetGeometry(RailExtraSupplier replacement, RailExtraSupplier target, boolean reversed) {
        replacement.setExtraAngle1Rad(reversed ? target.getExtraAngle2Rad() : target.getExtraAngle1Rad());
        replacement.setExtraAngle2Rad(reversed ? target.getExtraAngle1Rad() : target.getExtraAngle2Rad());
        replacement.setNodeXOffset1(reversed ? target.getNodeXOffset2() : target.getNodeXOffset1());
        replacement.setNodeYOffset1(reversed ? target.getNodeYOffset2() : target.getNodeYOffset1());
        replacement.setNodeZOffset1(reversed ? target.getNodeZOffset2() : target.getNodeZOffset1());
        replacement.setNodeXOffset2(reversed ? target.getNodeXOffset1() : target.getNodeXOffset2());
        replacement.setNodeYOffset2(reversed ? target.getNodeYOffset1() : target.getNodeYOffset2());
        replacement.setNodeZOffset2(reversed ? target.getNodeZOffset1() : target.getNodeZOffset2());
        replacement.setNodePitch1(reversed ? target.getNodePitch2() : target.getNodePitch1());
        replacement.setNodePitch2(reversed ? target.getNodePitch1() : target.getNodePitch2());
        final int pathMode = target.getPathMode();
        final cn.jstxjf_.rte.data.BezierCurve bezier = target.getBezierCurve();
        if (pathMode == RailExtraSupplier.PATH_MODE_AUTO_BEZIER) {
            replacement.setPathModeRaw(pathMode);
            replacement.regenerateAutoBezier();
        } else if (RailExtraSupplier.usesBezierCurve(pathMode) && bezier != null) {
            replacement.setPathModeRaw(pathMode);
            replacement.setBezier(reversed ? bezier.getTransposition() : bezier.copy());
        } else {
            replacement.setPathModeRaw(RailExtraSupplier.PATH_MODE_ORIGINAL);
        }
    }

    private static Rail createReplacementRail(
        RailExtraSupplier targetExtra,
        Rail templateRail,
        RailExtraSupplier templateExtra,
        Position position1,
        Angle angle1,
        Position position2,
        Angle angle2,
        boolean isOneWay
    ) {
        final ObjectArrayList<String> styles = new ObjectArrayList<>(templateExtra.getExtraStyles());
        final RailType railType = RailRebuildHelper.parseRailType(RailRebuildHelper.getStoredOrInferredRailTypeName(templateRail));
        final long speedLimit = Math.max(1, RailRebuildHelper.getStoredOrCurrentSpeedLimitKph(templateRail));

        if (railType == RailType.PLATFORM || templateRail.isPlatform()) {
            return Rail.newPlatformRail(position1, angle1, position2, angle2, templateExtra.getExtraShape(), templateExtra.getExtraVerticalRadius(), styles, targetExtra.getExtraTransportMode());
        }
        if (railType == RailType.SIDING || templateRail.isSiding()) {
            return Rail.newSidingRail(position1, angle1, position2, angle2, templateExtra.getExtraShape(), templateExtra.getExtraVerticalRadius(), styles, targetExtra.getExtraTransportMode());
        }
        if (railType == RailType.TURN_BACK || templateRail.canTurnBack()) {
            return Rail.newTurnBackRail(position1, angle1, position2, angle2, templateExtra.getExtraShape(), templateExtra.getExtraVerticalRadius(), styles, targetExtra.getExtraTransportMode());
        }

        final Rail.Shape shape = railType == null ? templateExtra.getExtraShape() : railType.railShape;
        final boolean canAccelerate = railType == null ? templateRail.canAccelerate() : railType.canAccelerate;
        final boolean canConnectRemotely = railType == RailType.RUNWAY || templateRail.canConnectRemotely();
        final boolean canHaveSignal = railType == null ? templateExtra.getExtraCanHaveSignal() : railType.hasSignal;

        return Rail.newRail(
            position1,
            angle1,
            position2,
            angle2,
            shape,
            templateExtra.getExtraVerticalRadius(),
            styles,
            speedLimit,
            isOneWay ? 0 : speedLimit,
            false,
            false,
            canAccelerate,
            canConnectRemotely,
            canHaveSignal,
            targetExtra.getExtraTransportMode()
        );
    }
}
