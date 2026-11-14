package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Objects;

public class PacketGizmoState extends RtePacket {

    @Nullable
    public final BlockPos anchor;
    public final int hoveredHandle;
    public final int activeHandle;
    public final boolean dragging;
    public final double angleStart;
    public final double rotationDelta;
    public final int axisSigns;

    public PacketGizmoState(RtePacketReader receiver) {
        final boolean present = receiver.readBoolean();
        final long packedAnchor = receiver.readLong();
        anchor = present ? BlockPos.of(packedAnchor) : null;
        hoveredHandle = receiver.readInt();
        activeHandle = receiver.readInt();
        dragging = receiver.readBoolean();
        angleStart = receiver.readDouble();
        rotationDelta = receiver.readDouble();
        axisSigns = receiver.readInt();
    }

    public PacketGizmoState(@Nullable BlockPos anchor, int hoveredHandle, int activeHandle, boolean dragging,
                            double angleStart, double rotationDelta, int axisSigns) {
        this.anchor = anchor == null ? null : anchor.immutable();
        this.hoveredHandle = hoveredHandle;
        this.activeHandle = activeHandle;
        this.dragging = dragging;
        this.angleStart = angleStart;
        this.rotationDelta = rotationDelta;
        this.axisSigns = axisSigns;
    }

    public static PacketGizmoState empty() {
        return new PacketGizmoState(null, 0, 0, false, 0, 0, 0);
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeBoolean(anchor != null);
        sender.writeLong(anchor == null ? 0 : anchor.asLong());
        sender.writeInt(hoveredHandle);
        sender.writeInt(activeHandle);
        sender.writeBoolean(dragging);
        sender.writeDouble(angleStart);
        sender.writeDouble(rotationDelta);
        sender.writeInt(axisSigns);
    }

    @Override
    protected void handleServer(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }
        server.execute(() -> cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, this));
    }

    @Override
    protected void handleClient() {
        ClientHandler.apply(this);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PacketGizmoState that)) {
            return false;
        }
        return hoveredHandle == that.hoveredHandle && activeHandle == that.activeHandle && dragging == that.dragging
            && Double.compare(angleStart, that.angleStart) == 0 && Double.compare(rotationDelta, that.rotationDelta) == 0
            && axisSigns == that.axisSigns && Objects.equals(anchor, that.anchor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anchor, hoveredHandle, activeHandle, dragging, angleStart, rotationDelta, axisSigns);
    }

    private static final class ClientHandler {
        private static void apply(PacketGizmoState state) {
            net.minecraft.client.Minecraft.getInstance().execute(() ->
                cn.jstxjf_.rte.render.FreeNodeEditor.applyRecordedState(state));
        }
    }
}
