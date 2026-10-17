package cn.jstxjf_.rte.bridge.network;

import net.minecraft.server.level.ServerPlayer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mod.Init;

import java.util.function.Function;

public final class RteNetwork {

    private RteNetwork() {
    }

    public static <T extends RtePacket> void register(Class<T> type, Function<RtePacketReader, T> factory) {
        Init.REGISTRY.registerPacket(type, receiver -> factory.apply(MtrPacketBuffers.reader(receiver)));
    }

    public static void sendToPlayer(ServerPlayer player, RtePacket packet) {
        if (player != null) {
            Init.REGISTRY.sendPacketToClient(new ServerPlayerEntity(player), packet);
        }
    }
}
