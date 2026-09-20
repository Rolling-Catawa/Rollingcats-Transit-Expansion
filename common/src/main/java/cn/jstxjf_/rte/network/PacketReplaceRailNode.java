package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.block.BlockDirectNode;
import cn.jstxjf_.rte.block.RTEBlocks;
import org.mtr.core.data.TransportMode;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockNode;

import java.util.function.Supplier;

public class PacketReplaceRailNode extends PacketHandler {

    private final BlockPos pos;
    private final float angle;
    private final boolean skipRebuild;
    private final boolean fittingNode;

    public PacketReplaceRailNode(PacketBufferReceiver receiver) {
        pos = BlockPos.fromLong(receiver.readLong());
        angle = receiver.readFloat();
        skipRebuild = receiver.readBoolean();
        fittingNode = receiver.readBoolean();
    }

    public PacketReplaceRailNode(BlockPos pos, float angle, boolean skipRebuild, boolean fittingNode) {
        this.pos = pos;
        this.angle = angle;
        this.skipRebuild = skipRebuild;
        this.fittingNode = fittingNode;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeLong(pos.asLong());
        sender.writeFloat(angle);
        sender.writeBoolean(skipRebuild);
        sender.writeBoolean(fittingNode);
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        final boolean noRebuild = this.skipRebuild;
        final boolean fitting = this.fittingNode;
        server.execute(() -> {
            if (player == null) {
                return;
            }

            final net.minecraft.server.level.ServerLevel level = ((net.minecraft.server.level.ServerPlayer) player.data).serverLevel();
            final net.minecraft.core.BlockPos nativePos = new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ());
            final net.minecraft.world.level.block.state.BlockState nativeState = level.getBlockState(nativePos);
            if (!(nativeState.getBlock() instanceof BlockNode blockNode) || blockNode.transportMode != TransportMode.TRAIN) {
                return;
            }

            replaceNode(new World(level), pos, angle, new BlockState(nativeState), !noRebuild, fitting);
        });
    }

    public static void sendUpdateC2S(World world, BlockPos pos, BlockState state) {
        sendUpdateC2S(world, pos, state, () -> null);
    }

    public static void sendUpdateC2S(World world, BlockPos pos, float angle) {
        sendUpdateC2S(world, pos, angle, false, false);
    }

    public static void sendUpdateC2S(World world, BlockPos pos, float angle, boolean skipRebuild) {
        sendUpdateC2S(world, pos, angle, skipRebuild, false);
    }

    public static void sendUpdateC2S(World world, BlockPos pos, float angle, boolean skipRebuild, boolean fittingNode) {
        if (world == null || pos == null) {
            return;
        }
        final BlockState state = world.getBlockState(pos);
        if (state == null || !(state.getBlock().data instanceof BlockNode blockNode) || blockNode.transportMode != TransportMode.TRAIN) {
            return;
        }
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketReplaceRailNode(pos, angle, skipRebuild, fittingNode));
        if (world.isClient()) {
            replaceNode(world, pos, angle, state, !skipRebuild, fittingNode);
        }
    }

    public static void sendUpdateC2S(World world, BlockPos pos, BlockState state, Supplier<?> parentSupplier) {
        if (world == null || state == null || !(state.getBlock().data instanceof BlockNode blockNode) || blockNode.transportMode != TransportMode.TRAIN) {
            return;
        }

        final float angle = BlockNode.getAngle(state);
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketReplaceRailNode(pos, angle, false, false));

        if (!world.isClient()) {
            return;
        }

        replaceNode(world, pos, angle, state, true, false);
        ClientHandler.openDirectNodeScreen(pos, parentSupplier);
    }

    private static void replaceNode(World world, BlockPos pos, float angle, BlockState sourceState, boolean triggerRebuild, boolean fittingNode) {
        final BlockState directState = withConnectedState(RTEBlocks.DIRECT_NODE.get().getDefaultState(), sourceState);
        final net.minecraft.core.BlockPos nativePos = new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ());
        world.data.setBlockAndUpdate(nativePos, directState.data);

        final BlockDirectNode.BlockEntityDirectNode blockEntity = new BlockDirectNode.BlockEntityDirectNode(pos, directState);
        world.data.setBlockEntity((net.minecraft.world.level.block.entity.BlockEntity) (Object) blockEntity);

        final CompoundTag tag = new CompoundTag();
        tag.putDouble(BlockDirectNode.BlockEntityDirectNode.KEY_ANGLE, angle);
        if (fittingNode) {
            tag.putBoolean(BlockDirectNode.BlockEntityDirectNode.KEY_FITTING_NODE, true);
        }
        blockEntity.readCompoundTag(tag, triggerRebuild);
        ((net.minecraft.world.level.block.entity.BlockEntity) (Object) blockEntity).setChanged();
        if (!world.isClient()) {
            blockEntity.sendUpdateC2S();
        }
    }

    private static BlockState withConnectedState(BlockState directState, BlockState sourceState) {
        if (directState == null || sourceState == null || BlockNode.IS_CONNECTED.data == null) {
            return directState;
        }
        try {
            return new BlockState(directState.data.setValue(BlockNode.IS_CONNECTED.data, sourceState.data.getValue(BlockNode.IS_CONNECTED.data)));
        } catch (Exception ignored) {
            return directState;
        }
    }

    /**
     * Keep every reference to client-only GUI classes out of the packet class that
     * is eagerly loaded while a dedicated server registers its packet handlers.
     */
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
            minecraft.setScreen(cn.jstxjf_.rte.gui.DirectNodeScreen.createScreen(
                    minecraft.level,
                    new net.minecraft.core.BlockPos(pos.getX(), pos.getY(), pos.getZ()),
                    screenSupplier
            ));
        }
    }
}
