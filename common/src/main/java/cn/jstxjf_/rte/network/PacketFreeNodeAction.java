package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.block.BlockFreeNode;
import cn.jstxjf_.rte.util.FreeNodeManager;
import org.mtr.mapping.holder.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PacketFreeNodeAction extends RtePacket {

    public static final int COPY = 0;
    public static final int DELETE = 1;
    public static final int USE_CONNECTOR = 2;
    public static final int PASTE = 3;
    public static final int PASTE_RESULT = 4;
    public static final int DELETE_RESULT = 5;

    private final int action;
    private final BlockPos anchor;
    private final double x;
    private final double y;
    private final double z;
    private final double angle;
    private final double pitch;
    private final int face;
    private final boolean fitting;

    public PacketFreeNodeAction(RtePacketReader receiver) {
        action = receiver.readInt();
        anchor = BlockPos.fromLong(receiver.readLong());
        x = receiver.readDouble();
        y = receiver.readDouble();
        z = receiver.readDouble();
        angle = receiver.readDouble();
        pitch = receiver.readDouble();
        face = receiver.readInt();
        fitting = receiver.readBoolean();
    }

    public PacketFreeNodeAction(int action, net.minecraft.core.BlockPos anchor) {
        this(action, anchor, 0, 0, 0, Double.NaN, 0, 0, false);
    }

    public PacketFreeNodeAction(int action, net.minecraft.core.BlockPos anchor, double x, double y, double z,
                                double angle, double pitch, int face, boolean fitting) {
        this.action = action;
        this.anchor = new BlockPos(anchor);
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
        this.pitch = pitch;
        this.face = face;
        this.fitting = fitting;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeInt(action);
        sender.writeLong(anchor.asLong());
        sender.writeDouble(x);
        sender.writeDouble(y);
        sender.writeDouble(z);
        sender.writeDouble(angle);
        sender.writeDouble(pitch);
        sender.writeInt(face);
        sender.writeBoolean(fitting);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final net.minecraft.server.level.ServerPlayer nativePlayer = player;
            final net.minecraft.core.BlockPos nativeAnchor = new net.minecraft.core.BlockPos(anchor.getX(), anchor.getY(), anchor.getZ());
            final net.minecraft.world.item.Item freeNodeItem = cn.jstxjf_.rte.block.RTEBlocks.freeNode().asItem();
            final net.minecraft.world.item.Item brushItem = org.mtr.mod.Items.BRUSH.get().data;
            if (action == PASTE) {
                final net.minecraft.world.phys.Vec3 point = new net.minecraft.world.phys.Vec3(x, y, z);
                if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)
                        && nativePlayer.distanceToSqr(point) <= 4096) {
                    final net.minecraft.core.BlockPos created = FreeNodeManager.paste(
                            nativePlayer.serverLevel(), nativePlayer, point, angle, pitch, face, fitting);
                    if (created != null) {
                        cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketFreeNodeAction(PASTE_RESULT, created));
                    }
                }
                return;
            }
            if (!(nativePlayer.serverLevel().getBlockEntity(nativeAnchor) instanceof BlockFreeNode.BlockEntityFreeNode node)) {
                if (action == DELETE) {
                    sendDeleteResult(player, nativeAnchor, false);
                }
                return;
            }
            final net.minecraft.world.phys.Vec3 point = FreeNodeManager.getPoint(nativeAnchor, node);
            if (nativePlayer.distanceToSqr(point) > 4096) {
                if (action == DELETE) {
                    sendDeleteResult(player, nativeAnchor, false);
                }
                return;
            }
            if (action == USE_CONNECTOR) {
                final net.minecraft.world.InteractionHand hand;
                if (nativePlayer.getMainHandItem().getItem() instanceof org.mtr.mod.item.ItemNodeModifierBase) {
                    hand = net.minecraft.world.InteractionHand.MAIN_HAND;
                } else if (nativePlayer.getOffhandItem().getItem() instanceof org.mtr.mod.item.ItemNodeModifierBase) {
                    hand = net.minecraft.world.InteractionHand.OFF_HAND;
                } else {
                    return;
                }
                final net.minecraft.world.item.context.UseOnContext context = new net.minecraft.world.item.context.UseOnContext(
                        nativePlayer, hand, new net.minecraft.world.phys.BlockHitResult(
                        net.minecraft.world.phys.Vec3.atCenterOf(nativeAnchor),
                        net.minecraft.core.Direction.from3DDataValue(node.getAttachmentFace()), nativeAnchor, false));
                nativePlayer.getItemInHand(hand).useOn(context);
                return;
            }
            final boolean holdingTool = nativePlayer.getMainHandItem().is(freeNodeItem) || nativePlayer.getOffhandItem().is(freeNodeItem)
                    || nativePlayer.getMainHandItem().is(brushItem) || nativePlayer.getOffhandItem().is(brushItem);
            if (!holdingTool) {
                if (action == DELETE) {
                    sendDeleteResult(player, nativeAnchor, false);
                }
                return;
            }
            if (action == COPY) {
                FreeNodeManager.copy(nativePlayer.serverLevel(), nativePlayer, nativeAnchor);
            } else if (action == DELETE) {
                final net.minecraft.server.level.ServerLevel level = nativePlayer.serverLevel();
                if (!level.mayInteract(nativePlayer, nativeAnchor)) {
                    sendDeleteResult(player, nativeAnchor, false);
                    return;
                }
                cn.jstxjf_.rte.bridge.MtrBridge.deleteRailNodeOnServer(level, nativeAnchor);
                final boolean success = level.destroyBlock(nativeAnchor, !nativePlayer.getAbilities().instabuild, nativePlayer);
                sendDeleteResult(player, nativeAnchor, success);
            }
        });
    }

    private static void sendDeleteResult(ServerPlayer player, net.minecraft.core.BlockPos anchor, boolean success) {
        cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketFreeNodeAction(
                DELETE_RESULT, anchor, 0, 0, 0, Double.NaN, 0, 0, success));
    }

    public static void send(int action, net.minecraft.core.BlockPos anchor) {
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketFreeNodeAction(action, anchor));
    }

    public static void sendPaste(net.minecraft.core.BlockPos anchor, double x, double y, double z,
                                 double angle, double pitch, int face, boolean fitting) {
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketFreeNodeAction(
                PASTE, anchor, x, y, z, angle, pitch, face, fitting));
    }

    @Override
    protected void handleClient() {
        if (action == PASTE_RESULT) {
            ClientHandler.select(new net.minecraft.core.BlockPos(anchor.getX(), anchor.getY(), anchor.getZ()));
        } else if (action == DELETE_RESULT) {
            ClientHandler.deleteResult(new net.minecraft.core.BlockPos(anchor.getX(), anchor.getY(), anchor.getZ()), fitting);
        }
    }

    private static final class ClientHandler {
        private static void select(net.minecraft.core.BlockPos anchor) {
            net.minecraft.client.Minecraft.getInstance().execute(() ->
                    cn.jstxjf_.rte.render.FreeNodeEditor.selectPasted(anchor));
        }

        private static void deleteResult(net.minecraft.core.BlockPos anchor, boolean success) {
            net.minecraft.client.Minecraft.getInstance().execute(() ->
                    cn.jstxjf_.rte.render.FreeNodeEditor.onDeleteResult(anchor, success));
        }
    }
}
