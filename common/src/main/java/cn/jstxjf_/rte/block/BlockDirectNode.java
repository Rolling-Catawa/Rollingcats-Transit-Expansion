package cn.jstxjf_.rte.block;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.network.PacketUpdateBlockEntity;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.Main;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.mod.block.BlockNode;
import org.mtr.core.data.TransportMode;
import org.mtr.core.simulation.Simulator;
import org.mtr.mapping.holder.BlockRenderType;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockWithEntity;
import org.mtr.mod.Init;
import org.mtr.mod.packet.PacketDeleteData;
import org.mtr.mod.packet.PacketUpdateData;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BlockDirectNode extends BlockNode implements BlockWithEntity {

    public BlockDirectNode() {
        super(TransportMode.TRAIN);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getEntityblockAnimatedMapped();
    }
    
    @Override
    public BlockEntityExtension createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityDirectNode(pos, state);
    }
    
    public static class BlockEntityDirectNode extends BlockEntityExtension {
        private static Field initMainField;
        private static Field worldIdListField;
        private static Field mainSimulatorsField;
        private static final double ANGLE_EPSILON = 1E-6;

        private double angle = Double.NaN;
        private boolean fittingNode = false;
        private boolean isRebuilding = false;
        private int rebuildRetryCount = 0;
        private static final int MAX_REBUILD_RETRIES = 20;
        private static final java.util.concurrent.ScheduledExecutorService RETRY_EXECUTOR =
            java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "RTE-DirectNode-Retry");
                t.setDaemon(true);
                return t;
            });
        public static final String KEY_ANGLE = "angle";
        public static final String KEY_FITTING_NODE = "fitting_node";
        
        public BlockEntityDirectNode(BlockPos pos, BlockState state) {
            super(RTEBlockEntityTypes.DIRECT_NODE.get(), pos, state);
        }

        public void bind(BlockEntityDirectNode other) {
            if (isBound()) return;
            BlockPos thi = getPos2();
            BlockPos oth = other.getPos2();
            final double rawAngle = Math.toDegrees(Math.atan2(oth.getZ() - thi.getZ(), oth.getX() - thi.getX()));
            logDebug("bind pair self={} other={} rawDeg={} normalizedDeg={}", thi, oth, rawAngle, normalize(rawAngle));
            bind(rawAngle);
            other.bind(this);
        }

        public void bind(double angleDegrees) {
            final double rawAngle = angleDegrees;
            angleDegrees = normalize(angleDegrees);
            logDebug("bind direct pos={} rawDeg={} normalizedDeg={} oldDeg={}", getPos2(), rawAngle, angleDegrees, angle);
            this.angle = angleDegrees;
            sendUpdateC2S();
        }

        public boolean isBound() {
            return !Double.isNaN(angle);
        }

        public void unbind() {
            if (isConnected()) return;
            this.angle = Double.NaN;
            sendUpdateC2S();
        }

        public boolean isConnected() {
            try {
                final net.minecraft.world.level.block.entity.BlockEntity nativeBlockEntity = (net.minecraft.world.level.block.entity.BlockEntity)(Object)this;
                final net.minecraft.world.level.Level level = nativeBlockEntity.getLevel();
                if (level == null) {
                    return false;
                }
                org.mtr.mapping.holder.BlockState state = new org.mtr.mapping.holder.World(level).getBlockState(getPos2());
                if (state.getBlock().data instanceof BlockDirectNode &&
                    org.mtr.mod.block.BlockNode.IS_CONNECTED.data != null &&
                    state.data.getValue(org.mtr.mod.block.BlockNode.IS_CONNECTED.data)) {
                    return true;
                }
                return hasRailAtPosition(level, getPos2());
            } catch (Exception e) {
                return false;
            }
        }
        
        public double getAngleRadians() {
            return Math.toRadians(angle);
        }

        public double getAngleDegrees() {
            return angle;
        }

        public boolean isFittingNode() {
            return fittingNode;
        }

        public void setFittingNode(boolean fitting) {
            this.fittingNode = fitting;
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            readCompoundTag(compoundTag, true);
        }

        public void readCompoundTag(CompoundTag compoundTag, boolean triggerRebuild) {
            if (compoundTag.contains(KEY_ANGLE)) {
                final double newAngle = normalize(compoundTag.getDouble(KEY_ANGLE));
                final boolean changed = Double.isNaN(angle) || Math.abs(newAngle - angle) > 1E-4;
                logDebug("read pos={} tagDeg={} oldDeg={} changed={} triggerRebuild={}", getPos2(), newAngle, angle, changed, triggerRebuild);
                angle = newAngle;
                if (changed && triggerRebuild) {
                    scheduleUpdateRailwayData();
                }
            } else {
                logDebug("read pos={} unbound oldDeg={}", getPos2(), angle);
                angle = Double.NaN;
            }
            if (compoundTag.contains(KEY_FITTING_NODE)) {
                fittingNode = compoundTag.getBoolean(KEY_FITTING_NODE);
            }
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            if (isBound()) {
                compoundTag.putDouble(KEY_ANGLE, angle);
            }
            if (fittingNode) {
                compoundTag.putBoolean(KEY_FITTING_NODE, true);
            }
        }
        
        public static double normalize(double angle) {
            while (angle < 0D) angle += 360D;
            while (angle >= 360D) angle -= 360D;
            return angle;
        }

        public static double normalizeRadians(double radians) {
            radians = radians % (2 * Math.PI);
            if (radians < 0) radians += 2 * Math.PI;
            return radians;
        }

        public void scheduleUpdateRailwayData() {
            final net.minecraft.world.level.block.entity.BlockEntity nativeBlockEntity = (net.minecraft.world.level.block.entity.BlockEntity) (Object) this;
            if (nativeBlockEntity.getLevel() instanceof net.minecraft.server.level.ServerLevel nativeServerLevel) {
                final net.minecraft.server.MinecraftServer server = nativeServerLevel.getServer();
                server.execute(this::updateRailwayData);
            }
        }

        public void sendUpdateC2S() {
            final net.minecraft.world.level.block.entity.BlockEntity nativeBlockEntity = (net.minecraft.world.level.block.entity.BlockEntity) (Object) this;
            if (nativeBlockEntity.getLevel() != null && nativeBlockEntity.getLevel().isClientSide()) {
                PacketUpdateBlockEntity.sendUpdateC2S(this);
            } else if (nativeBlockEntity.getLevel() instanceof net.minecraft.server.level.ServerLevel nativeServerLevel) {
                markDirty2();
                nativeServerLevel.getChunkSource().blockChanged(nativeBlockEntity.getBlockPos());
                nativeServerLevel.sendBlockUpdated(nativeBlockEntity.getBlockPos(), nativeBlockEntity.getBlockState(), nativeBlockEntity.getBlockState(), 3);
            } else {
                markDirty2();
            }
        }

        private void updateRailwayData() {
            if (isRebuilding) return;
            if (!isBound() || !isConnected()) {
                logDebug("skip update pos={} bound={} connected={}", getPos2(), isBound(), isConnected());
                return;
            }

            final net.minecraft.world.level.block.entity.BlockEntity nativeBlockEntity = (net.minecraft.world.level.block.entity.BlockEntity) (Object) this;
            if (!(nativeBlockEntity.getLevel() instanceof net.minecraft.server.level.ServerLevel nativeServerLevel)) {
                return;
            }

            isRebuilding = true;
            final ServerWorld serverWorld = new ServerWorld(nativeServerLevel);
            try {
                final Simulator simulator = findSimulator(new World(nativeServerLevel));
                if (simulator == null) {
                    logDebug("simulator missing pos={} retry={}/{}", getPos2(), rebuildRetryCount, MAX_REBUILD_RETRIES);
                    if (rebuildRetryCount < MAX_REBUILD_RETRIES) {
                        rebuildRetryCount++;
                        final net.minecraft.server.MinecraftServer srv = nativeServerLevel.getServer();
                        RETRY_EXECUTOR.schedule(() -> srv.execute(this::updateRailwayData), 300, java.util.concurrent.TimeUnit.MILLISECONDS);
                    } else {
                        rebuildRetryCount = 0;
                        RTE.LOGGER.warn("[DirectNode] Gave up waiting for simulator at {}", getPos2());
                    }
                    return;
                }
                final Position fromPosition = Init.blockPosToPosition(getPos2());
                final List<Rail> matchedRails = new ArrayList<>();
                final List<Position> matchedTargets = new ArrayList<>();
                for (final Rail rail : simulator.rails) {
                    if (!(((Object) rail) instanceof RailExtraSupplier extra)) continue;
                    final Position p1 = extra.getExtraPosition1();
                    final Position p2 = extra.getExtraPosition2();
                    if (fromPosition.equals(p1)) {
                        matchedRails.add(rail);
                        matchedTargets.add(p2);
                    } else if (fromPosition.equals(p2)) {
                        matchedRails.add(rail);
                        matchedTargets.add(p1);
                    }
                }
                if (matchedRails.isEmpty()) {
                    logDebug("no matched rail pos={} retry={}/{} simulatorRails={}", getPos2(), rebuildRetryCount, MAX_REBUILD_RETRIES, simulator.rails.size());
                    if (rebuildRetryCount < MAX_REBUILD_RETRIES) {
                        rebuildRetryCount++;
                        final net.minecraft.server.MinecraftServer srv = nativeServerLevel.getServer();
                        RETRY_EXECUTOR.schedule(() -> srv.execute(this::updateRailwayData), 300, java.util.concurrent.TimeUnit.MILLISECONDS);
                    } else {
                        rebuildRetryCount = 0;
                        RTE.LOGGER.warn("[DirectNode] Rail not found at {} after max retries", getPos2());
                    }
                    return;
                }
                rebuildRetryCount = 0;
                logDebug("matched rails pos={} count={} angleDeg={}", getPos2(), matchedRails.size(), angle);

                final double angleFromRadians = getAngleRadians();
                for (int i = 0; i < matchedRails.size(); i++) {
                    final Rail forward = matchedRails.get(i);
                    final Position targetPosition = matchedTargets.get(i);
                    final RailExtraSupplier forwardExtra = (RailExtraSupplier)(Object)forward;

                    final double targetRawRadians = getRailAngleRadians(targetPosition, serverWorld);
                    if (Double.isNaN(targetRawRadians)) {
                        logDebug("target angle nan from={} target={} rail={}", fromPosition, targetPosition, forward.getHexId());
                        continue;
                    }

                    if (RailExtraSupplier.usesBezierCurve(forwardExtra.getPathMode())) {
                        logDebug("skip custom path rail id={} pathMode={} pos={}", forward.getHexId(), forwardExtra.getPathMode(), getPos2());
                        continue;
                    }

                    final Position pos1 = forwardExtra.getExtraPosition1();
                    final Position pos2 = forwardExtra.getExtraPosition2();
                    if (pos1 == null || pos2 == null) continue;

                    final float outwardDeg1 = (float) Math.toDegrees(Math.atan2(
                        pos2.getZ() - pos1.getZ(),
                        pos2.getX() - pos1.getX()));
                    final float outwardDeg2 = (float) Math.toDegrees(Math.atan2(
                        pos1.getZ() - pos2.getZ(),
                        pos1.getX() - pos2.getX()));

                    double angleAtPos1, angleAtPos2;
                    if (pos1.equals(fromPosition)) {
                        angleAtPos1 = angleFromRadians;
                        angleAtPos2 = targetRawRadians;
                    } else {
                        angleAtPos1 = targetRawRadians;
                        angleAtPos2 = angleFromRadians;
                    }

                    if (!org.mtr.core.tool.Angle.similarFacing(outwardDeg1, (float) Math.toDegrees(angleAtPos1)))
                        angleAtPos1 += Math.PI;
                    if (!org.mtr.core.tool.Angle.similarFacing(outwardDeg1, (float) Math.toDegrees(angleAtPos2)))
                        angleAtPos2 += Math.PI;

                    final double rad1 = normalizeRadians(angleAtPos1);
                    final double rad2 = normalizeRadians(angleAtPos2);
                    logDebug(
                        "rail calc id={} from={} target={} pos1={} pos2={} outward1={} outward2={} fromDeg={} targetRawDeg={} rad1Deg={} rad2Deg={} old1Deg={} old2Deg={}",
                        forward.getHexId(),
                        fromPosition,
                        targetPosition,
                        pos1,
                        pos2,
                        outwardDeg1,
                        outwardDeg2,
                        Math.toDegrees(angleFromRadians),
                        Math.toDegrees(targetRawRadians),
                        Math.toDegrees(rad1),
                        Math.toDegrees(rad2),
                        Math.toDegrees(forwardExtra.getExtraAngle1Rad()),
                        Math.toDegrees(forwardExtra.getExtraAngle2Rad())
                    );
                    if (hasSameExtraAngles(forwardExtra, rad1, rad2)) {
                        logDebug("rail unchanged id={} pos={}", forward.getHexId(), getPos2());
                        continue;
                    }
                    final Rail rebuilt = rebuildRail(forward, rad1, rad2);
                    if (rebuilt == null) {
                        logDebug("rail rebuild null id={} pos={}", forward.getHexId(), getPos2());
                        continue;
                    }
                    logDebug("rail rebuilt oldId={} newId={} valid={} pos={}", forward.getHexId(), rebuilt.getHexId(), rebuilt.isValid(), getPos2());
                    applyServerRailUpdate(serverWorld, forward, rebuilt);
                }
            } catch (Exception e) {
                RTE.LOGGER.warn("Failed to rebuild rails for direct node at " + getPos2() + ": " + e.getMessage());
            } finally {
                isRebuilding = false;
            }
        }

        private static double getRailAngleRadians(Position position, ServerWorld world) {
            final BlockPos blockPos = new BlockPos((int) position.getX(), (int) position.getY(), (int) position.getZ());
            final BlockState state = world.getBlockState(blockPos);
            if (!(state.getBlock().data instanceof BlockNode)) {
                return Double.NaN;
            }

            final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity != null && blockEntity.data instanceof BlockEntityDirectNode directNode && directNode.isBound()) {
                logDebug("target direct angle pos={} deg={}", blockPos, directNode.getAngleDegrees());
                return directNode.getAngleRadians();
            }
            final double vanillaDeg = BlockNode.getAngle(state);
            logDebug("target vanilla angle pos={} deg={}", blockPos, vanillaDeg);
            return Math.toRadians(vanillaDeg);
        }

        private static boolean hasSameExtraAngles(RailExtraSupplier rail, double rad1, double rad2) {
            return sameAngle(rail.getExtraAngle1Rad(), rad1) && sameAngle(rail.getExtraAngle2Rad(), rad2);
        }

        private static boolean sameAngle(double angle1, double angle2) {
            if (!Double.isFinite(angle1) || !Double.isFinite(angle2)) {
                return false;
            }
            double diff = (angle1 - angle2) % (Math.PI * 2);
            if (diff <= -Math.PI) {
                diff += Math.PI * 2;
            } else if (diff > Math.PI) {
                diff -= Math.PI * 2;
            }
            return Math.abs(diff) <= ANGLE_EPSILON;
        }

        private static Rail rebuildRail(Rail source, double rad1, double rad2) {
            if (!Double.isFinite(rad1) || !Double.isFinite(rad2)) {
                return null;
            }
            final RailExtraSupplier extra = (RailExtraSupplier) (Object) source;
            final Position position1 = extra.getExtraPosition1();
            final Position position2 = extra.getExtraPosition2();
            if (position1 == null || position2 == null) {
                return null;
            }
            final Rail[] rebuiltHolder = {null};
            RailAngleExtra.withAngles(rad1, rad2, (angle1, angle2) ->
                rebuiltHolder[0] = RailRebuildHelper.rebuildWithEndpoints(source, position1, angle1, position2, angle2)
            );
            final Rail rebuilt = rebuiltHolder[0];
            if (rebuilt == null || rebuilt == source) {
                logDebug("rebuild rejected sourceId={} rebuiltNull={} same={} valid={}", source.getHexId(), rebuilt == null, rebuilt == source, rebuilt != null && rebuilt.isValid());
                return null;
            }
            final RailExtraSupplier rebuiltExtra = (RailExtraSupplier) (Object) rebuilt;
            rebuiltExtra.setExtraAngle1Rad(rad1);
            rebuiltExtra.setExtraAngle2Rad(rad2);
            if (rebuiltExtra.getPathMode() == RailExtraSupplier.PATH_MODE_AUTO_BEZIER) {
                RailAngleExtra.rebuildRailMath(rebuilt, rad1, rad2);
                rebuiltExtra.regenerateAutoBezier();
            } else if (rebuiltExtra.getBezierCurve() != null) {
                RailAngleExtra.rebuildRailMathFromBezier(rebuilt, rebuiltExtra.getBezierCurve());
            } else {
                RailAngleExtra.rebuildRailMath(rebuilt, rad1, rad2);
            }
            if (!rebuilt.isValid()) {
                logDebug("rebuild rejected sourceId={} rebuiltNull={} same={} valid={}", source.getHexId(), false, false, false);
                return null;
            }
            return rebuilt;
        }

        private static void applyServerRailUpdate(ServerWorld serverWorld, Rail original, Rail rebuilt) {
            final Rail originalReverse = ((RailExtraSupplier)(Object)original).getTransposition();
            final Rail rebuiltReverse = ((RailExtraSupplier)(Object)rebuilt).getTransposition();
            logDebug(
                "apply update oldId={} newId={} oldReverseId={} newReverseId={} deleteOld={} deleteOldReverse={}",
                original.getHexId(),
                rebuilt.getHexId(),
                originalReverse.getHexId(),
                rebuiltReverse.getHexId(),
                !original.getHexId().equals(rebuilt.getHexId()),
                !originalReverse.getHexId().equals(rebuiltReverse.getHexId())
            );
            final Set<String> deletedRailIds = new LinkedHashSet<>();
            if (!original.getHexId().equals(rebuilt.getHexId()) && deletedRailIds.add(original.getHexId())) {
                PacketDeleteData.sendDirectlyToServerRailId(serverWorld, original.getHexId());
            }
            if (!originalReverse.getHexId().equals(rebuiltReverse.getHexId()) && deletedRailIds.add(originalReverse.getHexId())) {
                PacketDeleteData.sendDirectlyToServerRailId(serverWorld, originalReverse.getHexId());
            }
            PacketUpdateData.sendDirectlyToServerRail(serverWorld, rebuilt);
            if (!rebuilt.getHexId().equals(rebuiltReverse.getHexId())) {
                PacketUpdateData.sendDirectlyToServerRail(serverWorld, rebuiltReverse);
            }
        }

        @SuppressWarnings("unchecked")
        public static Simulator findSimulator(World world) throws Exception {
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
            final int worldIndex = worldIds.indexOf(Init.getWorldId(world));
            if (worldIndex < 0) {
                return null;
            }

            final List<Simulator> simulators = (List<Simulator>) mainSimulatorsField.get(main);
            return worldIndex < simulators.size() ? simulators.get(worldIndex) : null;
        }

        private static void logDebug(String message, Object... args) {
        }

        private static boolean hasRailAtPosition(net.minecraft.world.level.Level level, BlockPos pos) {
            final Position position = Init.blockPosToPosition(pos);
            if (level.isClientSide()) {
                try {
                    final var railsAt = org.mtr.mod.client.MinecraftClientData.getInstance().positionsToRail.get(position);
                    return railsAt != null && !railsAt.isEmpty();
                } catch (Throwable ignored) {
                    return false;
                }
            }
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                try {
                    final Simulator simulator = findSimulator(new World(serverLevel));
                    if (simulator == null) {
                        return false;
                    }
                    for (final Rail rail : simulator.rails) {
                        if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
                            continue;
                        }
                        if (position.equals(extra.getExtraPosition1()) || position.equals(extra.getExtraPosition2())) {
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                    return false;
                }
            }
            return false;
        }
    }
}
