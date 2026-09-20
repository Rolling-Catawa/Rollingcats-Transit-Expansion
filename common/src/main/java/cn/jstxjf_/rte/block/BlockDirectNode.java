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
        private double xOffset = 0;
        private double yOffset = 0;
        private double zOffset = 0;
        private double pitch = 0;
        private int attachmentFace = org.mtr.mapping.holder.Direction.UP.getId();
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
        public static final String KEY_X_OFFSET = "x_offset";
        public static final String KEY_Y_OFFSET = "y_offset";
        public static final String KEY_Z_OFFSET = "z_offset";
        public static final String KEY_PITCH = "pitch";
        public static final String KEY_ATTACHMENT_FACE = "attachment_face";
        public static final String KEY_FITTING_NODE = "fitting_node";
        
        public BlockEntityDirectNode(BlockPos pos, BlockState state) {
            this(pos, state, false);
        }

        protected BlockEntityDirectNode(BlockPos pos, BlockState state, boolean freeNode) {
            super(freeNode ? RTEBlockEntityTypes.FREE_NODE.get() : RTEBlockEntityTypes.DIRECT_NODE.get(), pos, state);
        }

        public void bind(BlockEntityDirectNode other) {
            if (isBound()) return;
            BlockPos thi = getPos2();
            BlockPos oth = other.getPos2();
            final double rawAngle = Math.toDegrees(Math.atan2(
                    oth.getZ() + other.getZOffset() - thi.getZ() - zOffset,
                    oth.getX() + other.getXOffset() - thi.getX() - xOffset));
            logDebug("bind pair self={} other={} rawDeg={} normalizedDeg={}", thi, oth, rawAngle, normalize(rawAngle));
            bind(rawAngle);
            other.bind(this);
        }

        public void bind(double angleDegrees) {
            setAngleLocal(angleDegrees);
            sendUpdateC2S();
        }

        public void setAngleLocal(double angleDegrees) {
            final double rawAngle = angleDegrees;
            angleDegrees = normalize(angleDegrees);
            logDebug("bind direct pos={} rawDeg={} normalizedDeg={} oldDeg={}", getPos2(), rawAngle, angleDegrees, angle);
            this.angle = angleDegrees;
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

        public double getYOffset() {
            return yOffset;
        }

        public double getXOffset() {
            return xOffset;
        }

        public double getZOffset() {
            return zOffset;
        }

        public double getPitchDegrees() {
            return pitch;
        }

        public int getAttachmentFace() {
            return attachmentFace;
        }

        public void setXOffsetLocal(double value) {
            xOffset = sanitizeOffset(value);
        }

        public void setYOffsetLocal(double value) {
            yOffset = sanitizeOffset(value);
        }

        public void setZOffsetLocal(double value) {
            zOffset = sanitizeOffset(value);
        }

        public void setOffsetsLocal(double x, double y, double z) {
            xOffset = sanitizeOffset(x);
            yOffset = sanitizeOffset(y);
            zOffset = sanitizeOffset(z);
        }

        public void setPitchLocal(double value) {
            pitch = normalizePitch(value);
        }

        public void setAttachmentFaceLocal(int value) {
            attachmentFace = org.mtr.mapping.holder.Direction.byId(value).getId();
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
            boolean geometryChanged = false;
            if (compoundTag.contains(KEY_ANGLE)) {
                final double newAngle = normalize(compoundTag.getDouble(KEY_ANGLE));
                final boolean changed = Double.isNaN(angle) || Math.abs(newAngle - angle) > 1E-4;
                logDebug("read pos={} tagDeg={} oldDeg={} changed={} triggerRebuild={}", getPos2(), newAngle, angle, changed, triggerRebuild);
                angle = newAngle;
                geometryChanged |= changed;
            } else {
                logDebug("read pos={} unbound oldDeg={}", getPos2(), angle);
                angle = Double.NaN;
            }
            final double newXOffset = compoundTag.contains(KEY_X_OFFSET) ? sanitizeOffset(compoundTag.getDouble(KEY_X_OFFSET)) : 0;
            final double newYOffset = compoundTag.contains(KEY_Y_OFFSET) ? sanitizeOffset(compoundTag.getDouble(KEY_Y_OFFSET)) : 0;
            final double newZOffset = compoundTag.contains(KEY_Z_OFFSET) ? sanitizeOffset(compoundTag.getDouble(KEY_Z_OFFSET)) : 0;
            final double newPitch = compoundTag.contains(KEY_PITCH) ? normalizePitch(compoundTag.getDouble(KEY_PITCH)) : 0;
            geometryChanged |= Math.abs(newXOffset - xOffset) > 1E-4;
            geometryChanged |= Math.abs(newYOffset - yOffset) > 1E-4;
            geometryChanged |= Math.abs(newZOffset - zOffset) > 1E-4;
            geometryChanged |= Math.abs(newPitch - pitch) > 1E-4;
            xOffset = newXOffset;
            yOffset = newYOffset;
            zOffset = newZOffset;
            pitch = newPitch;
            attachmentFace = compoundTag.contains(KEY_ATTACHMENT_FACE)
                    ? org.mtr.mapping.holder.Direction.byId(compoundTag.getInt(KEY_ATTACHMENT_FACE)).getId()
                    : org.mtr.mapping.holder.Direction.UP.getId();
            if (compoundTag.contains(KEY_FITTING_NODE)) {
                fittingNode = compoundTag.getBoolean(KEY_FITTING_NODE);
            }
            if (geometryChanged && triggerRebuild) {
                scheduleUpdateRailwayData();
            }
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            if (isBound()) {
                compoundTag.putDouble(KEY_ANGLE, angle);
            }
            if (Math.abs(xOffset) > 1E-6) {
                compoundTag.putDouble(KEY_X_OFFSET, xOffset);
            }
            if (Math.abs(yOffset) > 1E-6) {
                compoundTag.putDouble(KEY_Y_OFFSET, yOffset);
            }
            if (Math.abs(zOffset) > 1E-6) {
                compoundTag.putDouble(KEY_Z_OFFSET, zOffset);
            }
            if (Math.abs(pitch) > 1E-6) {
                compoundTag.putDouble(KEY_PITCH, pitch);
            }
            if (attachmentFace != org.mtr.mapping.holder.Direction.UP.getId()) {
                compoundTag.putInt(KEY_ATTACHMENT_FACE, attachmentFace);
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

        private static double sanitizeOffset(double value) {
            return Double.isFinite(value) ? Math.max(-64, Math.min(64, value)) : 0;
        }

        private static double normalizePitch(double value) {
            if (!Double.isFinite(value)) return 0;
            value %= 360;
            if (value <= -180) value += 360;
            if (value > 180) value -= 360;
            return value;
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
                    final Position pos1 = forwardExtra.getExtraPosition1();
                    final Position pos2 = forwardExtra.getExtraPosition2();
                    if (pos1 == null || pos2 == null) continue;

                    final NodeTransform ownTransform = new NodeTransform(xOffset, yOffset, zOffset, pitch,
                            this instanceof BlockFreeNode.BlockEntityFreeNode);
                    final NodeTransform targetTransform = getRailTransform(targetPosition, serverWorld);
                    final NodeTransform desiredTransform1 = pos1.equals(fromPosition) ? ownTransform : targetTransform;
                    final NodeTransform desiredTransform2 = pos2.equals(fromPosition) ? ownTransform : targetTransform;
                    final boolean transformsUnchanged = hasSameNodeTransforms(
                            forwardExtra, desiredTransform1, desiredTransform2);
                    final boolean needsSpatialBezier = requiresSpatialBezier(desiredTransform1, desiredTransform2);

                    final double targetRawRadians = getRailAngleRadians(targetPosition, serverWorld);
                    if (Double.isNaN(targetRawRadians)) {
                        logDebug("target angle nan from={} target={} rail={}", fromPosition, targetPosition, forward.getHexId());
                        continue;
                    }

                    if (RailExtraSupplier.usesBezierCurve(forwardExtra.getPathMode()) && !needsSpatialBezier) {
                        if (!transformsUnchanged || needsSpatialBezier && forwardExtra.getPathMode() != RailExtraSupplier.PATH_MODE_AUTO_BEZIER) {
                            setRailNodeTransforms(forwardExtra, desiredTransform1, desiredTransform2);
                            if (needsSpatialBezier) {
                                forwardExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_AUTO_BEZIER);
                                forwardExtra.regenerateAutoBezier();
                            }
                            applyServerRailUpdate(serverWorld, forward, forward);
                        }
                        continue;
                    }

                    final float outwardDeg1 = (float) Math.toDegrees(Math.atan2(
                        pos2.getZ() + desiredTransform2.z - pos1.getZ() - desiredTransform1.z,
                        pos2.getX() + desiredTransform2.x - pos1.getX() - desiredTransform1.x));
                    final float outwardDeg2 = (float) Math.toDegrees(Math.atan2(
                        pos1.getZ() + desiredTransform1.z - pos2.getZ() - desiredTransform2.z,
                        pos1.getX() + desiredTransform1.x - pos2.getX() - desiredTransform2.x));

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
                    if (!org.mtr.core.tool.Angle.similarFacing(outwardDeg2, (float) Math.toDegrees(angleAtPos2)))
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
                        if (!transformsUnchanged || needsSpatialBezier
                                && forwardExtra.getPathMode() != RailExtraSupplier.PATH_MODE_AUTO_BEZIER) {
                            setRailNodeTransforms(forwardExtra, desiredTransform1, desiredTransform2);
                            if (needsSpatialBezier) {
                                forwardExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_AUTO_BEZIER);
                                forwardExtra.regenerateAutoBezier();
                            }
                            applyServerRailUpdate(serverWorld, forward, forward);
                        } else {
                            logDebug("rail unchanged id={} pos={}", forward.getHexId(), getPos2());
                        }
                        continue;
                    }
                    final Rail rebuilt = rebuildRail(forward, rad1, rad2);
                    if (rebuilt == null) {
                        if (!transformsUnchanged || needsSpatialBezier) {
                            setRailNodeTransforms(forwardExtra, desiredTransform1, desiredTransform2);
                            if (needsSpatialBezier) {
                                forwardExtra.setExtraAngle1Rad(rad1);
                                forwardExtra.setExtraAngle2Rad(rad2);
                                forwardExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_AUTO_BEZIER);
                                forwardExtra.regenerateAutoBezier();
                                RailAngleExtra.ensureCarrierRailMath(forward, forwardExtra.getLength(), rad1);
                            }
                            applyServerRailUpdate(serverWorld, forward, forward);
                        }
                        logDebug("rail rebuild null id={} pos={}", forward.getHexId(), getPos2());
                        continue;
                    }
                    final RailExtraSupplier rebuiltExtra = (RailExtraSupplier) (Object) rebuilt;
                    setRailNodeTransforms(rebuiltExtra, desiredTransform1, desiredTransform2);
                    if (needsSpatialBezier) {
                        rebuiltExtra.setPathModeRaw(RailExtraSupplier.PATH_MODE_AUTO_BEZIER);
                        rebuiltExtra.regenerateAutoBezier();
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

        private static NodeTransform getRailTransform(Position position, ServerWorld world) {
            final BlockPos blockPos = new BlockPos((int) position.getX(), (int) position.getY(), (int) position.getZ());
            final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(blockPos);
            return blockEntity != null && blockEntity.data instanceof BlockEntityDirectNode directNode
                    ? new NodeTransform(directNode.getXOffset(), directNode.getYOffset(), directNode.getZOffset(), directNode.getPitchDegrees(),
                            directNode instanceof BlockFreeNode.BlockEntityFreeNode)
                    : NodeTransform.ZERO;
        }

        private static boolean hasSameNodeTransforms(RailExtraSupplier rail, NodeTransform transform1, NodeTransform transform2) {
            return Math.abs(rail.getNodeXOffset1() - transform1.x) <= 1E-4
                    && Math.abs(rail.getNodeYOffset1() - transform1.y) <= 1E-4
                    && Math.abs(rail.getNodeZOffset1() - transform1.z) <= 1E-4
                    && Math.abs(rail.getNodeXOffset2() - transform2.x) <= 1E-4
                    && Math.abs(rail.getNodeYOffset2() - transform2.y) <= 1E-4
                    && Math.abs(rail.getNodeZOffset2() - transform2.z) <= 1E-4
                    && Math.abs(rail.getNodePitch1() - transform1.pitch) <= 1E-4
                    && Math.abs(rail.getNodePitch2() - transform2.pitch) <= 1E-4;
        }

        private static void setRailNodeTransforms(RailExtraSupplier rail, NodeTransform transform1, NodeTransform transform2) {
            rail.setNodeXOffset1(transform1.x);
            rail.setNodeYOffset1(transform1.y);
            rail.setNodeZOffset1(transform1.z);
            rail.setNodeXOffset2(transform2.x);
            rail.setNodeYOffset2(transform2.y);
            rail.setNodeZOffset2(transform2.z);
            rail.setNodePitch1(transform1.pitch);
            rail.setNodePitch2(transform2.pitch);
        }

        private static boolean requiresSpatialBezier(NodeTransform transform1, NodeTransform transform2) {
            return transform1.requiresSpatialBezier() || transform2.requiresSpatialBezier();
        }

        private static final class NodeTransform {
            private static final NodeTransform ZERO = new NodeTransform(0, 0, 0, 0, false);
            private final double x;
            private final double y;
            private final double z;
            private final double pitch;
            private final boolean free;

            private NodeTransform(double x, double y, double z, double pitch, boolean free) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.pitch = pitch;
                this.free = free;
            }

            private boolean requiresSpatialBezier() {
                return free || Math.abs(x) > 1E-6 || Math.abs(z) > 1E-6 || Math.abs(pitch) > 1E-6;
            }
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
