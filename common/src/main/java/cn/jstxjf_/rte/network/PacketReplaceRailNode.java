package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.block.RTEBlocks;
import org.mtr.core.data.TransportMode;
import org.mtr.mapping.holder.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.mtr.mod.block.BlockNode;

import java.util.function.Supplier;

public class PacketReplaceRailNode extends RtePacket {

    private final BlockPos pos;
    private final float angle;
    private final boolean skipRebuild;
    private final boolean fittingNode;

    public PacketReplaceRailNode(RtePacketReader receiver) {
        pos = BlockPos.of(receiver.readLong());
        angle = receiver.readFloat();
        skipRebuild = receiver.readBoolean();
        fittingNode = receiver.readBoolean();
    }

    public PacketReplaceRailNode(BlockPos pos, float angle, boolean skipRebuild, boolean fittingNode) {
        this.pos = pos.immutable();
        this.angle = angle;
        this.skipRebuild = skipRebuild;
        this.fittingNode = fittingNode;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeLong(pos.asLong());
        sender.writeFloat(angle);
        sender.writeBoolean(skipRebuild);
        sender.writeBoolean(fittingNode);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        final boolean noRebuild = this.skipRebuild;
        final boolean fitting = this.fittingNode;
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final net.minecraft.server.level.ServerLevel level = player.serverLevel();
            final BlockState state = level.getBlockState(pos);
            if (!isTrainNode(state)) {
                return;
            }
            replaceNode(level, pos, angle, state, !noRebuild, fitting);
        });
    }

    public static void sendUpdateC2S(Level level, BlockPos pos, BlockState state) {
        sendUpdateC2S(level, pos, state, () -> null);
    }

    public static void sendUpdateC2S(Level level, BlockPos pos, float angle) {
        sendUpdateC2S(level, pos, angle, false, false);
    }

    public static void sendUpdateC2S(Level level, BlockPos pos, float angle, boolean skipRebuild) {
        sendUpdateC2S(level, pos, angle, skipRebuild, false);
    }

    public static void sendUpdateC2S(Level level, BlockPos pos, float angle, boolean skipRebuild, boolean fittingNode) {
        if (level == null || pos == null) {
            return;
        }
        final BlockState state = level.getBlockState(pos);
        if (!isTrainNode(state)) {
            return;
        }
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketReplaceRailNode(pos, angle, skipRebuild, fittingNode));
        if (level.isClientSide()) {
            replaceNode(level, pos, angle, state, !skipRebuild, fittingNode);
        }
    }

    public static void sendUpdateC2S(Level level, BlockPos pos, BlockState state, Supplier<?> parentSupplier) {
        if (level == null || !isTrainNode(state)) {
            return;
        }

        final float angle = cn.jstxjf_.rte.bridge.MtrBridge.nodeAngle(state);
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketReplaceRailNode(pos, angle, false, false));

        if (!level.isClientSide()) {
            return;
        }

        replaceNode(level, pos, angle, state, true, false);
        ClientHandler.openDirectNodeScreen(pos, parentSupplier);
    }

    private static boolean isTrainNode(BlockState state) {
        return state != null && state.getBlock() instanceof BlockNode blockNode && blockNode.transportMode == TransportMode.TRAIN;
    }

    private static void replaceNode(Level level, BlockPos pos, float angle, BlockState sourceState, boolean triggerRebuild, boolean fittingNode) {
        final BlockState directState = withConnectedState(RTEBlocks.directNode().defaultBlockState(), sourceState);
        level.setBlockAndUpdate(pos, directState);

        final BlockDirectNode.BlockEntityDirectNode blockEntity = new BlockDirectNode.BlockEntityDirectNode(
                cn.jstxjf_.rte.bridge.MtrTypes.blockPos(pos), new org.mtr.mapping.holder.BlockState(directState));
        level.setBlockEntity((net.minecraft.world.level.block.entity.BlockEntity) (Object) blockEntity);

        final CompoundTag tag = new CompoundTag();
        tag.putDouble(BlockDirectNode.BlockEntityDirectNode.KEY_ANGLE, angle);
        if (fittingNode) {
            tag.putBoolean(BlockDirectNode.BlockEntityDirectNode.KEY_FITTING_NODE, true);
        }
        blockEntity.readCompoundTag(tag, triggerRebuild);
        ((net.minecraft.world.level.block.entity.BlockEntity) (Object) blockEntity).setChanged();
        if (!level.isClientSide()) {
            blockEntity.sendUpdateC2S();
        }
    }

    private static BlockState withConnectedState(BlockState directState, BlockState sourceState) {
        if (directState == null || sourceState == null || BlockNode.IS_CONNECTED.data == null) {
            return directState;
        }
        try {
            return directState.setValue(BlockNode.IS_CONNECTED.data, sourceState.getValue(BlockNode.IS_CONNECTED.data));
        } catch (Exception ignored) {
            return directState;
        }
    }

    private static final class ClientHandler {

        private static void openDirectNodeScreen(BlockPos pos, Supplier<?> parentSupplier) {
            final net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }
            final Supplier<net.minecraft.client.gui.screens.Screen> screenSupplier = () -> {
                final Object parent = parentSupplier == null ? null : parentSupplier.get();
                return parent instanceof net.minecraft.client.gui.screens.Screen screen ? screen : null;
            };
            minecraft.setScreen(cn.jstxjf_.rte.gui.DirectNodeScreen.createScreen(minecraft.level, pos, screenSupplier));
        }
    }
}
