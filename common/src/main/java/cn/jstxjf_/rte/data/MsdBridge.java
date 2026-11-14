package cn.jstxjf_.rte.data;

//#if MC>=11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
import cn.jstxjf_.rte.compat.BuiltInRegistries;
//#endif
import cn.jstxjf_.rte.RTE;
import org.mtr.mapping.holder.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public final class MsdBridge {

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            try {
                Class.forName("top.mcmtr.core.data.Catenary");
                loaded = true;
            } catch (ClassNotFoundException e) {
                loaded = false;
            }
        }
        return loaded;
    }

    public static List<Object[]> findCatenaryNodesNear(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos center, int radius) {
        final List<Object[]> result = new ArrayList<>();
        if (!isLoaded()) {
            return result;
        }
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    final net.minecraft.core.BlockPos probe = center.offset(dx, dy, dz);
                    final net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(probe);
                    if (blockEntity instanceof top.mcmtr.mod.blocks.BlockNodeBase.BlockNodeBaseEntity nodeEntity) {
                        final CompoundTag tag = new CompoundTag();
                        nodeEntity.writeCompoundTag(tag);
                        result.add(new Object[]{probe.immutable(), tag.data.toString()});
                    }
                }
            }
        }
        return result;
    }

    public static void serverPlaceAndLink(net.minecraft.server.level.ServerLevel level, List<long[]> nodePlacements,
                                          List<String> nodeTags, long sourceStatePos, List<long[]> links, String typeName) {
        if (!isLoaded()) {
            return;
        }
        try {
            final net.minecraft.core.BlockPos sourcePos = net.minecraft.core.BlockPos.of(sourceStatePos);
            final net.minecraft.world.level.block.state.BlockState sourceState = level.getBlockState(sourcePos);
            if (!(sourceState.getBlock() instanceof top.mcmtr.mod.blocks.BlockNodeBase)) {
                return;
            }

            for (int i = 0; i < nodePlacements.size(); i++) {
                final net.minecraft.core.BlockPos target = net.minecraft.core.BlockPos.of(nodePlacements.get(i)[0]);
                final net.minecraft.world.level.block.state.BlockState existing = level.getBlockState(target);
                if (existing.getBlock() instanceof top.mcmtr.mod.blocks.BlockNodeBase) {
                    continue;
                }
                if (!existing.isAir() && !cn.jstxjf_.rte.compat.McCompat.canBeReplaced(existing)) {
                    continue;
                }
                level.setBlockAndUpdate(target, sourceState);
                final net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(target);
                if (blockEntity instanceof top.mcmtr.mod.blocks.BlockNodeBase.BlockNodeBaseEntity nodeEntity) {
                    try {
                        nodeEntity.readCompoundTag(new CompoundTag(net.minecraft.nbt.TagParser.parseTag(nodeTags.get(i))));
                    } catch (Exception ignored) {
                    }
                    blockEntity.setChanged();
                    level.sendBlockUpdated(target, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                }
            }

            final top.mcmtr.core.data.CatenaryType catenaryType = parseCatenaryType(typeName);
            if (catenaryType == null) {
                return;
            }

            final org.mtr.mapping.holder.ServerWorld serverWorld = new org.mtr.mapping.holder.ServerWorld(level);
            for (final long[] link : links) {
                final net.minecraft.core.BlockPos posA = net.minecraft.core.BlockPos.of(link[0]);
                final net.minecraft.core.BlockPos posB = net.minecraft.core.BlockPos.of(link[1]);
                if (posA.distSqr(posB) > cn.jstxjf_.rte.data.CatenaryPlan.MAX_SPAN_METERS
                        * cn.jstxjf_.rte.data.CatenaryPlan.MAX_SPAN_METERS) {
                    RTE.LOGGER.warn("[MsdBridge] skipped catenary span of {} blocks between {} and {}",
                        String.format("%.1f", Math.sqrt(posA.distSqr(posB))), posA, posB);
                    continue;
                }
                final top.mcmtr.core.data.OffsetPosition offsetA = offsetAt(level, posA);
                final top.mcmtr.core.data.OffsetPosition offsetB = offsetAt(level, posB);
                if (offsetA == null || offsetB == null) {
                    continue;
                }
                final org.mtr.core.data.Position positionA = new org.mtr.core.data.Position(posA.getX(), posA.getY(), posA.getZ());
                final org.mtr.core.data.Position positionB = new org.mtr.core.data.Position(posB.getX(), posB.getY(), posB.getZ());
                if (!top.mcmtr.core.data.Catenary.verifyPosition(positionA, positionB, offsetA, offsetB)) {
                    continue;
                }
                final top.mcmtr.core.data.Catenary catenary =
                    new top.mcmtr.core.data.Catenary(positionA, positionB, offsetA, offsetB, catenaryType);
                top.mcmtr.mod.packet.MSDPacketUpdateData.sendDirectlyToServerCatenary(serverWorld, catenary);
                markConnected(level, posA);
                markConnected(level, posB);
            }
        } catch (Throwable e) {
            RTE.LOGGER.error("[MsdBridge] place/link failed", e);
        }
    }

    public static boolean verifyPlannedSpan(int ax, int ay, int az, double aox, double aoy, double aoz,
                                            int bx, int by, int bz, double box, double boy, double boz) {
        if (!isLoaded()) {
            return true;
        }
        try {
            return top.mcmtr.core.data.Catenary.verifyPosition(
                new org.mtr.core.data.Position(ax, ay, az),
                new org.mtr.core.data.Position(bx, by, bz),
                new top.mcmtr.core.data.OffsetPosition(aox, aoy, aoz),
                new top.mcmtr.core.data.OffsetPosition(box, boy, boz));
        } catch (Throwable e) {
            return true;
        }
    }

    public static int serverPlacePlanned(net.minecraft.server.level.ServerLevel level,
                                         List<long[]> placements, List<double[]> offsets,
                                         List<String> blockIds, List<Integer> facings,
                                         List<Double> rotations, List<long[]> links, String typeName) {
        if (!isLoaded()) {
            return 0;
        }
        int linked = 0;
        try {
            for (int i = 0; i < placements.size(); i++) {
                final net.minecraft.world.level.block.Block block = BuiltInRegistries.BLOCK
                    .get(new net.minecraft.resources.ResourceLocation("msd", blockIds.get(i)));
                if (!(block instanceof top.mcmtr.mod.blocks.BlockNodeBase)) {
                    continue;
                }
                net.minecraft.world.level.block.state.BlockState nodeState = block.defaultBlockState();
                nodeState = withFacing(nodeState, facings.get(i));

                final net.minecraft.core.BlockPos target = net.minecraft.core.BlockPos.of(placements.get(i)[0]);
                final net.minecraft.world.level.block.state.BlockState existing = level.getBlockState(target);
                if (!(existing.getBlock() instanceof top.mcmtr.mod.blocks.BlockNodeBase)) {
                    if (!existing.isAir() && !cn.jstxjf_.rte.compat.McCompat.canBeReplaced(existing)) {
                        continue;
                    }
                    level.setBlockAndUpdate(target, nodeState);
                }
                final net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(target);
                if (blockEntity instanceof top.mcmtr.mod.blocks.BlockNodeBase.BlockNodeBaseEntity nodeEntity) {
                    final double[] offset = offsets.get(i);
                    final top.mcmtr.core.data.OffsetPosition offsetPosition =
                        new top.mcmtr.core.data.OffsetPosition(offset[0], offset[1], offset[2]);
                    if (nodeEntity instanceof top.mcmtr.mod.blocks.BlockCatenaryWithModel.BlockCatenaryWithModelEntity modelEntity) {
                        modelEntity.setOffsetPosition(offsetPosition,
                            new top.mcmtr.core.data.OffsetPosition(0, rotations.get(i), 0));
                    } else {
                        nodeEntity.setOffsetPosition(offsetPosition);
                    }
                    blockEntity.setChanged();
                    level.sendBlockUpdated(target, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                }
            }

            final top.mcmtr.core.data.CatenaryType catenaryType = parseCatenaryType(typeName);
            if (catenaryType == null) {
                return 0;
            }

            final org.mtr.mapping.holder.ServerWorld serverWorld = new org.mtr.mapping.holder.ServerWorld(level);
            for (final long[] link : links) {
                final net.minecraft.core.BlockPos posA = net.minecraft.core.BlockPos.of(link[0]);
                final net.minecraft.core.BlockPos posB = net.minecraft.core.BlockPos.of(link[1]);
                if (posA.distSqr(posB) > CatenaryPlan.MAX_SPAN_METERS * CatenaryPlan.MAX_SPAN_METERS) {
                    RTE.LOGGER.warn("[MsdBridge] skipped planned span of {} blocks between {} and {}",
                        String.format("%.1f", Math.sqrt(posA.distSqr(posB))), posA, posB);
                    continue;
                }
                final top.mcmtr.core.data.OffsetPosition offsetA = offsetAt(level, posA);
                final top.mcmtr.core.data.OffsetPosition offsetB = offsetAt(level, posB);
                if (offsetA == null || offsetB == null) {
                    continue;
                }
                final org.mtr.core.data.Position positionA = new org.mtr.core.data.Position(posA.getX(), posA.getY(), posA.getZ());
                final org.mtr.core.data.Position positionB = new org.mtr.core.data.Position(posB.getX(), posB.getY(), posB.getZ());
                if (!top.mcmtr.core.data.Catenary.verifyPosition(positionA, positionB, offsetA, offsetB)) {
                    continue;
                }
                top.mcmtr.mod.packet.MSDPacketUpdateData.sendDirectlyToServerCatenary(serverWorld,
                    new top.mcmtr.core.data.Catenary(positionA, positionB, offsetA, offsetB, catenaryType));
                markConnected(level, posA);
                markConnected(level, posB);
                linked++;
            }
        } catch (Throwable e) {
            RTE.LOGGER.error("[MsdBridge] planned place failed", e);
        }
        return linked;
    }

    private static net.minecraft.world.level.block.state.BlockState withFacing(
        net.minecraft.world.level.block.state.BlockState state, int facingIndex) {
        try {
            for (final net.minecraft.world.level.block.state.properties.Property<?> property : state.getProperties()) {
                if (property instanceof net.minecraft.world.level.block.state.properties.DirectionProperty directionProperty
                    && "facing".equals(property.getName())) {
                    final net.minecraft.core.Direction direction = net.minecraft.core.Direction.from2DDataValue(facingIndex & 3);
                    if (directionProperty.getPossibleValues().contains(direction)) {
                        return state.setValue(directionProperty, direction);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return state;
    }

    private static top.mcmtr.core.data.CatenaryType parseCatenaryType(String typeName) {
        try {
            final top.mcmtr.core.data.CatenaryType type = top.mcmtr.core.data.CatenaryType.valueOf(typeName);
            return type == top.mcmtr.core.data.CatenaryType.NONE ? null : type;
        } catch (Exception e) {
            return null;
        }
    }

    private static top.mcmtr.core.data.OffsetPosition offsetAt(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos) {
        final net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof top.mcmtr.mod.blocks.BlockNodeBase.BlockNodeBaseEntity nodeEntity) {
            return nodeEntity.getOffsetPosition();
        }
        return null;
    }

    private static void markConnected(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos) {
        try {
            final net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof top.mcmtr.mod.blocks.BlockNodeBase) {
                level.setBlockAndUpdate(pos, state.setValue(top.mcmtr.mod.blocks.BlockNodeBase.IS_CONNECTED.data, true));
            }
        } catch (Exception ignored) {
        }
    }

    private MsdBridge() {
    }
}
