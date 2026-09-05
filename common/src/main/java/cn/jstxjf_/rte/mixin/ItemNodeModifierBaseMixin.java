package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.RailAngleExtra;
import cn.jstxjf_.rte.data.RailCalculator;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.item.ItemNodeModifierBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = ItemNodeModifierBase.class, remap = false)
public abstract class ItemNodeModifierBaseMixin {

    @Shadow(remap = false)
    protected boolean isConnector;

    @Shadow(remap = false)
    protected abstract void onConnect(World world, ItemStack stack, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, Angle facingStart, Angle facingEnd, @Nullable ServerPlayerEntity player);

    @Shadow(remap = false)
    protected abstract void onRemove(World world, BlockPos posStart, BlockPos posEnd, @Nullable ServerPlayerEntity player);

    @Inject(method = "onEndClick", at = @At("HEAD"), cancellable = true, remap = false)
    private void onEndClick(ItemUsageContext context, BlockPos posEnd, CompoundTag compoundTag, CallbackInfo info) {
        if (!cn.jstxjf_.rte.util.CompatArbiter.owns(cn.jstxjf_.rte.util.CompatArbiter.Domain.NODE_PLACEMENT)) {
            return;
        }
        final World world = context.getWorld();
        final BlockPos posStart = context.getBlockPos();
        final BlockState stateStart = world.getBlockState(posStart);
        final BlockState stateEnd = world.getBlockState(posEnd);

        final BlockEntityDirectNode beStart = getBlockEntity(world, posStart);
        final BlockEntityDirectNode beEnd = getBlockEntity(world, posEnd);
        final boolean anyDirectBlock =
            world.getBlockState(posStart).getBlock().data instanceof BlockDirectNode
                || world.getBlockState(posEnd).getBlock().data instanceof BlockDirectNode;
        if (beStart == null && beEnd == null && !anyDirectBlock) {
            return;
        }

        if (!(stateStart.getBlock().data instanceof BlockNode blockNodeStart) ||
            !(stateEnd.getBlock().data instanceof BlockNode blockNodeEnd)) {
            return;
        }

        if (blockNodeStart.transportMode != blockNodeEnd.transportMode) {
            return;
        }

        if (compoundTag.contains("transport_mode") && !blockNodeStart.transportMode.toString().equals(compoundTag.getString("transport_mode"))) {
            return;
        }

        final PlayerEntity player = context.getPlayer();
        if (player == null || !ServerPlayerEntity.isInstance(player)) {
            return;
        }
        final ServerPlayerEntity serverPlayer = ServerPlayerEntity.cast(player);

        if (isConnector) {
            if (posStart.equals(posEnd)) {
                info.cancel();
                return;
            }

            final float angleDifference = (float) Math.toDegrees(Math.atan2(
                posEnd.getZ() - posStart.getZ(), posEnd.getX() - posStart.getX()));

            if (beStart != null && beEnd != null) {
                if (!beStart.isBound() && !beEnd.isBound()) {
                    beStart.bind(beEnd);
                    serverPlayer.sendMessage(new Text(TextHelper.translatable("gui.rte.direct_node.success_bind").data), true);
                }
            }

            final boolean startIsDirect = stateStart.getBlock().data instanceof BlockDirectNode;
            final boolean endIsDirect = stateEnd.getBlock().data instanceof BlockDirectNode;

            boolean s1 = !startIsDirect || (beStart != null && beStart.isBound());
            boolean s2 = !endIsDirect || (beEnd != null && beEnd.isBound());

            double radStart = Math.toRadians(BlockNode.getAngle(stateStart));
            double radEnd = Math.toRadians(BlockNode.getAngle(stateEnd));

            if (beStart != null && beStart.isBound()) {
                radStart = beStart.getAngleRadians();
            }
            if (beEnd != null && beEnd.isBound()) {
                radEnd = beEnd.getAngleRadians();
            }

            if (s1 && !s2) {
                Double deg = RailCalculator.calculateMaxRadiusAngle(
                    posStart.getX(), posStart.getZ(), posEnd.getX(), posEnd.getZ(),
                    radStart);
                if (deg == null) {
                    deg = (double) angleDifference;
                }
                if (beEnd != null) {
                    beEnd.bind(deg);
                }
                radEnd = normalizeRadians(Math.toRadians(deg));
                s2 = true;
                serverPlayer.sendMessage(new Text(TextHelper.translatable("gui.rte.direct_node.success_bind").data), true);
            } else if (s2 && !s1) {
                Double deg = RailCalculator.calculateMaxRadiusAngle(
                    posEnd.getX(), posEnd.getZ(), posStart.getX(), posStart.getZ(),
                    radEnd);
                if (deg == null) {
                    deg = (double) angleDifference + 180;
                }
                if (beStart != null) {
                    beStart.bind(deg);
                }
                radStart = normalizeRadians(Math.toRadians(deg));
                s1 = true;
                serverPlayer.sendMessage(new Text(TextHelper.translatable("gui.rte.direct_node.success_bind").data), true);
            }

            if (!s1 && !s2) {
                final double chord = Math.toRadians(angleDifference);
                radStart = chord;
                radEnd = chord;
                if (beStart != null) {
                    beStart.bind(angleDifference);
                }
                if (beEnd != null) {
                    beEnd.bind(angleDifference);
                }
                s1 = true;
                s2 = true;
                serverPlayer.sendMessage(new Text(TextHelper.translatable("gui.rte.direct_node.success_bind").data), true);
            }

            if (s1 && s2) {
                radStart = faceToward(posStart, posEnd, radStart);
                radEnd = faceToward(posStart, posEnd, radEnd);

                final double chordDist = Math.sqrt(
                    Math.pow(posEnd.getX() - posStart.getX(), 2) + Math.pow(posEnd.getZ() - posStart.getZ(), 2));
                final double maxReasonableLength = chordDist * 4 + 32;
                double bestStart = radStart;
                double bestEnd = radEnd;
                double bestLength = Double.MAX_VALUE;
                final double[][] candidateOrder = {
                    {radStart, radEnd},
                    {radStart, normalizeRadians(radEnd + Math.PI)},
                    {normalizeRadians(radStart + Math.PI), radEnd},
                    {normalizeRadians(radStart + Math.PI), normalizeRadians(radEnd + Math.PI)}
                };
                for (final double[] candidate : candidateOrder) {
                    final double length = testRailLength(posStart, candidate[0], posEnd, candidate[1], blockNodeStart.transportMode);
                    if (length <= maxReasonableLength) {
                        bestLength = length;
                        bestStart = candidate[0];
                        bestEnd = candidate[1];
                        break;
                    }
                }

                final double finalRadStart = bestStart;
                final double finalRadEnd = bestEnd;
                RailAngleExtra.withAngles(finalRadStart, finalRadEnd, (preciseStart, preciseEnd) -> {
                    onConnect(world, context.getStack(), blockNodeStart.transportMode,
                        stateStart, stateEnd, posStart, posEnd, preciseStart, preciseEnd, serverPlayer);
                });
                boolean connected = isConnected(world.getBlockState(posStart)) && isConnected(world.getBlockState(posEnd));
                if (!connected) {
                    for (final double[] candidate : candidateOrder) {
                        final Angle quantizedStart = Angle.fromAngle((float) Math.toDegrees(candidate[0]));
                        final Angle quantizedEnd = Angle.fromAngle((float) Math.toDegrees(candidate[1]));
                        onConnect(world, context.getStack(), blockNodeStart.transportMode,
                            stateStart, stateEnd, posStart, posEnd, quantizedStart, quantizedEnd, serverPlayer);
                        if (isConnected(world.getBlockState(posStart)) && isConnected(world.getBlockState(posEnd))) {
                            break;
                        }
                    }
                }
                if (beStart != null) {
                    beStart.scheduleUpdateRailwayData();
                }
                if (beEnd != null) {
                    beEnd.scheduleUpdateRailwayData();
                }
            } else {
                serverPlayer.sendMessage(new Text(TextHelper.translatable("gui.rte.direct_node.unbound").data), true);
            }
        } else {
            onRemove(world, posStart, posEnd, serverPlayer);
        }

        compoundTag.remove("transport_mode");
        info.cancel();
    }

    private BlockEntityDirectNode getBlockEntity(World world, BlockPos pos) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity != null && entity.data instanceof BlockDirectNode.BlockEntityDirectNode) {
             return (BlockDirectNode.BlockEntityDirectNode) entity.data;
        }
        return null;
    }

    private boolean isConnected(BlockState state) {
        try {
            return state.data.getValue(BlockNode.IS_CONNECTED.data);
        } catch (Exception e) {
            return false;
        }
    }

    private double normalizeRadians(double rad) {
        rad = rad % (2 * Math.PI);
        if (rad < 0) rad += 2 * Math.PI;
        return rad;
    }

    private double testRailLength(BlockPos posStart, double radStart, BlockPos posEnd, double radEnd, TransportMode transportMode) {
        final double[] length = {Double.MAX_VALUE};
        try {
            final org.mtr.core.data.Position position1 = org.mtr.mod.Init.blockPosToPosition(posStart);
            final org.mtr.core.data.Position position2 = org.mtr.mod.Init.blockPosToPosition(posEnd);
            RailAngleExtra.withAngles(radStart, radEnd, (angle1, angle2) -> {
                try {
                    final org.mtr.core.data.Rail testRail = org.mtr.core.data.Rail.newRail(
                        position1, angle1, position2, angle2,
                        org.mtr.core.data.Rail.Shape.QUADRATIC, 0,
                        new org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<>(),
                        20, 20, false, false, true, false, true, transportMode);
                    if (testRail.isValid()) {
                        final double railLength = testRail.railMath.getLength();
                        if (Double.isFinite(railLength) && railLength > 0.01) {
                            length[0] = railLength;
                        }
                    }
                } catch (Exception ignored) {
                }
            });
        } catch (Exception ignored) {
        }
        return length[0];
    }

    private double faceToward(BlockPos from, BlockPos to, double radians) {
        final float direction = (float) Math.toDegrees(Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX()));
        return normalizeRadians(similarFacing(direction, (float) Math.toDegrees(radians)) ? radians : radians + Math.PI);
    }

    private double faceAway(BlockPos from, BlockPos to, double radians) {
        final float direction = (float) Math.toDegrees(Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX()));
        return normalizeRadians(similarFacing(direction, (float) Math.toDegrees(radians)) ? radians + Math.PI : radians);
    }

    private boolean similarFacing(float angle1, float angle2) {
        float diff = Math.abs(angle1 - angle2) % 360;
        if (diff > 180) diff = 360 - diff;
        return diff < 90;
    }
}
