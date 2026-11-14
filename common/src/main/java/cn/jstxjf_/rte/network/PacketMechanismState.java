package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import cn.jstxjf_.rte.mechanism.Mechanism;
import cn.jstxjf_.rte.mechanism.MechanismGate;

import java.util.ArrayList;
import java.util.List;

public class PacketMechanismState extends RtePacket {

    public final String dimension;
    public final String group;
    public final String scriptId;
    public final List<String> candidates;
    public final String activeRail;
    public final String deckRail;
    public final String targetRail;
    public final String start;
    public final String target;
    public final long remainingMillis;
    public final long durationMillis;
    public final boolean easeInOut;
    public final boolean locked;
    public final String state;
    public final String synced;

    private PacketMechanismState(String dimension, Mechanism mechanism, String synced, long now) {
        synchronized (mechanism) {
            this.dimension = dimension;
            this.group = mechanism.group;
            this.scriptId = mechanism.scriptId;
            this.candidates = mechanism.candidateIds();
            this.activeRail = mechanism.activeRailId;
            this.deckRail = mechanism.deckRailId;
            this.targetRail = mechanism.targetRailId;
            this.start = mechanism.start.encode();
            this.target = mechanism.target.encode();
            this.remainingMillis = mechanism.remainingMillis(now);
            this.durationMillis = mechanism.animDurationMillis;
            this.easeInOut = mechanism.easeInOut;
            this.locked = mechanism.locked;
            this.state = MechanismGate.state(mechanism, now);
            this.synced = synced == null ? "" : synced;
        }
    }

    public static PacketMechanismState of(String dimension, Mechanism mechanism, String synced, long now) {
        return new PacketMechanismState(dimension, mechanism, synced, now);
    }

    public PacketMechanismState(RtePacketReader reader) {
        dimension = reader.readString();
        group = reader.readString();
        scriptId = reader.readString();
        final int count = Math.max(0, Math.min(256, reader.readInt()));
        candidates = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            candidates.add(reader.readString());
        }
        activeRail = reader.readString();
        deckRail = reader.readString();
        targetRail = reader.readString();
        start = reader.readString();
        target = reader.readString();
        remainingMillis = reader.readLong();
        durationMillis = reader.readLong();
        easeInOut = reader.readBoolean();
        locked = reader.readBoolean();
        state = reader.readString();
        synced = reader.readString();
    }

    @Override
    protected void write(RtePacketWriter writer) {
        writer.writeString(dimension);
        writer.writeString(group);
        writer.writeString(scriptId);
        writer.writeInt(candidates.size());
        candidates.forEach(writer::writeString);
        writer.writeString(activeRail);
        writer.writeString(deckRail);
        writer.writeString(targetRail);
        writer.writeString(start);
        writer.writeString(target);
        writer.writeLong(remainingMillis);
        writer.writeLong(durationMillis);
        writer.writeBoolean(easeInOut);
        writer.writeBoolean(locked);
        writer.writeString(state);
        writer.writeString(synced);
    }

    @Override
    protected void handleClient() {
        cn.jstxjf_.rte.mechanism.client.ClientMechanisms.accept(this);
    }
}
