package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import cn.jstxjf_.rte.mechanism.Mechanism;
import cn.jstxjf_.rte.mechanism.RailPose;
import cn.jstxjf_.rte.mechanism.RailScriptRuntime;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PacketMechanismMessage extends RtePacket {

    public static final int MAX_MESSAGE_CHARS = 4096;
    private static final double SENDER_RANGE = 256;

    private final boolean toTrain;
    private final String group;
    private final long trainId;
    private final String message;

    private PacketMechanismMessage(boolean toTrain, String group, long trainId, String message) {
        this.toTrain = toTrain;
        this.group = group;
        this.trainId = trainId;
        this.message = message.length() > MAX_MESSAGE_CHARS ? message.substring(0, MAX_MESSAGE_CHARS) : message;
    }

    public static PacketMechanismMessage toTrain(String group, long trainId, String message) {
        return new PacketMechanismMessage(true, group, trainId, message);
    }

    public static PacketMechanismMessage toMechanism(String group, long trainId, String message) {
        return new PacketMechanismMessage(false, group, trainId, message == null ? "" : message);
    }

    public PacketMechanismMessage(RtePacketReader reader) {
        toTrain = reader.readBoolean();
        group = reader.readString();
        trainId = reader.readLong();
        final String text = reader.readString();
        message = text.length() > MAX_MESSAGE_CHARS ? text.substring(0, MAX_MESSAGE_CHARS) : text;
    }

    @Override
    protected void write(RtePacketWriter writer) {
        writer.writeBoolean(toTrain);
        writer.writeString(group);
        writer.writeLong(trainId);
        writer.writeString(message);
    }

    @Override
    protected void handleClient() {
        if (toTrain) {
            cn.jstxjf_.rte.mechanism.client.ClientMechanisms.deliverToTrain(group, trainId, message);
        }
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        if (toTrain || player == null) {
            return;
        }
        server.execute(() -> {
            final RailScriptRuntime runtime = RailScriptRuntime.get(server);
            if (!runtime.messageLimiter.tryAcquire(trainId, System.currentTimeMillis())) {
                return;
            }
            final RailScriptRuntime.LevelState level = runtime.levelState(cn.jstxjf_.rte.compat.McCompat.serverLevel(player));
            final Mechanism mechanism = RailScriptRuntime.GATE_INDEX.values().stream()
                .filter(candidate -> candidate.group.equals(group)).findFirst().orElse(null);
            final RailPose deck = mechanism == null ? null : mechanism.candidates.get(mechanism.deckRailId);
            if (deck == null || player.distanceToSqr(deck.midX(), deck.midY(), deck.midZ()) > SENDER_RANGE * SENDER_RANGE) {
                return;
            }
            if (!level.deliverFromTrain(group, trainId, message)) {
                RTE.LOGGER.debug("[RailScript] message to unknown mechanism {}", group);
            }
        });
    }
}
