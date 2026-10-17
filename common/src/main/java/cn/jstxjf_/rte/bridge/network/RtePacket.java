package cn.jstxjf_.rte.bridge.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferSender;

import javax.annotation.Nullable;

public abstract class RtePacket extends PacketHandler {

    protected abstract void write(RtePacketWriter writer);

    protected void handleServer(MinecraftServer server, @Nullable ServerPlayer player) {
    }

    protected void handleClient() {
    }

    @Override
    public final void write(PacketBufferSender sender) {
        write(MtrPacketBuffers.writer(sender));
    }

    @Override
    public final void runServer(org.mtr.mapping.holder.MinecraftServer server, ServerPlayerEntity player) {
        handleServer(server.data, player == null ? null : (ServerPlayer) player.data);
    }

    @Override
    public final void runClient() {
        handleClient();
    }
}
