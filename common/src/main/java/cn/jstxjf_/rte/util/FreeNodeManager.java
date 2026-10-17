package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.block.BlockFreeNode;
import cn.jstxjf_.rte.block.RTEBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class FreeNodeManager {

    private static final int ANCHOR_RADIUS = 8;

    private FreeNodeManager() {
    }

    public static boolean place(ServerLevel level, Vec3 point, net.minecraft.core.Direction face, Player player, net.minecraft.world.item.ItemStack stack) {
        if (level == null || point == null || face == null || player == null || stack == null) {
            return false;
        }
        final net.minecraft.core.BlockPos anchor = findAnchor(level, net.minecraft.core.BlockPos.containing(
                point.x + face.getStepX() * 0.001,
                point.y + face.getStepY() * 0.001,
                point.z + face.getStepZ() * 0.001), player, stack);
        if (anchor == null || !create(level, anchor, point, face, null)) {
            return false;
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return true;
    }

    public static boolean copy(ServerLevel level, Player player, net.minecraft.core.BlockPos sourceAnchor) {
        if (level == null || player == null || sourceAnchor == null
                || !(level.getBlockEntity(sourceAnchor) instanceof BlockFreeNode.BlockEntityFreeNode source)) {
            return false;
        }
        final net.minecraft.world.item.ItemStack stack = editorStack(player);
        if (stack.isEmpty()) {
            return false;
        }
        final boolean consume = stack.is(RTEBlocks.freeNode().asItem());
        final Vec3 point = getPoint(sourceAnchor, source);
        final net.minecraft.core.BlockPos anchor = findAnchor(level, net.minecraft.core.BlockPos.containing(point), player, stack);
        if (anchor == null) {
            return false;
        }
        final net.minecraft.core.Direction face = net.minecraft.core.Direction.from3DDataValue(source.getAttachmentFace());
        final boolean created = create(level, anchor, point, face, source);
        if (created && consume && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return created;
    }

    public static net.minecraft.core.BlockPos paste(ServerLevel level, Player player, Vec3 point, double angle, double pitch, int faceId, boolean fitting) {
        if (level == null || player == null || point == null) {
            return null;
        }
        final net.minecraft.world.item.ItemStack stack = editorStack(player);
        if (stack.isEmpty()) {
            return null;
        }
        final boolean consume = stack.is(RTEBlocks.freeNode().asItem());
        final net.minecraft.core.Direction face = net.minecraft.core.Direction.from3DDataValue(faceId);
        final net.minecraft.core.BlockPos anchor = findAnchor(level, net.minecraft.core.BlockPos.containing(point), player, stack);
        if (anchor == null || !create(level, anchor, point, face, null)) {
            return null;
        }
        if (level.getBlockEntity(anchor) instanceof BlockFreeNode.BlockEntityFreeNode node) {
            node.setPitchLocal(pitch);
            node.setAttachmentFaceLocal(faceId);
            if (Double.isFinite(angle)) {
                node.setAngleLocal(angle);
            }
            node.setFittingNode(fitting);
            node.sendUpdateC2S();
        }
        if (consume && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return anchor;
    }

    public static Vec3 getPoint(net.minecraft.core.BlockPos anchor, BlockFreeNode.BlockEntityFreeNode node) {
        return new Vec3(anchor.getX() + 0.5 + node.getXOffset(), anchor.getY() + 0.125 + node.getYOffset(), anchor.getZ() + 0.5 + node.getZOffset());
    }

    private static boolean create(ServerLevel level, net.minecraft.core.BlockPos anchor, Vec3 point,
                                  net.minecraft.core.Direction face, BlockFreeNode.BlockEntityFreeNode source) {
        final net.minecraft.world.level.block.state.BlockState state = RTEBlocks.freeNode().defaultBlockState();
        if (!level.setBlockAndUpdate(anchor, state)) {
            return false;
        }
        if (!(level.getBlockEntity(anchor) instanceof BlockFreeNode.BlockEntityFreeNode node)) {
            level.removeBlock(anchor, false);
            return false;
        }
        node.setOffsetsLocal(point.x - anchor.getX() - 0.5, point.y - anchor.getY() - 0.125, point.z - anchor.getZ() - 0.5);
        node.setPitchLocal(source == null ? automaticPitch(face) : source.getPitchDegrees());
        node.setAttachmentFaceLocal(face.get3DDataValue());
        if (source != null) {
            if (source.isBound()) {
                node.setAngleLocal(source.getAngleDegrees());
            }
            node.setFittingNode(source.isFittingNode());
        }
        node.sendUpdateC2S();
        return true;
    }

    private static double automaticPitch(net.minecraft.core.Direction face) {
        if (face == net.minecraft.core.Direction.DOWN) {
            return 180;
        }
        return face == net.minecraft.core.Direction.UP ? 0 : -90;
    }

    private static net.minecraft.world.item.ItemStack editorStack(Player player) {
        final net.minecraft.world.item.Item freeNode = RTEBlocks.freeNode().asItem();
        final net.minecraft.world.item.Item brush = org.mtr.mod.Items.BRUSH.get().data;
        if (player.getMainHandItem().is(freeNode) || player.getMainHandItem().is(brush)) {
            return player.getMainHandItem();
        }
        if (player.getOffhandItem().is(freeNode) || player.getOffhandItem().is(brush)) {
            return player.getOffhandItem();
        }
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    private static net.minecraft.core.BlockPos findAnchor(ServerLevel level, net.minecraft.core.BlockPos origin,
                                                          Player player, net.minecraft.world.item.ItemStack stack) {
        for (int radius = 0; radius <= ANCHOR_RADIUS; radius++) {
            for (int y = -radius; y <= radius; y++) {
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z)) != radius) {
                            continue;
                        }
                        final net.minecraft.core.BlockPos candidate = origin.offset(x, y, z);
                        if (level.getBlockState(candidate).isAir() && level.mayInteract(player, candidate)
                                && player.mayUseItemAt(candidate, net.minecraft.core.Direction.UP, stack)) {
                            return candidate.immutable();
                        }
                    }
                }
            }
        }
        return null;
    }
}
