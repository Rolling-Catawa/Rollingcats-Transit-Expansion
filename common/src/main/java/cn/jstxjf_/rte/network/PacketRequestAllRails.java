package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.simulation.Simulator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class PacketRequestAllRails extends RtePacket {

    private static final int PAGE_SIZE = 256;
    private static final int MAX_SAMPLES = 32;

    private final int page;

    public PacketRequestAllRails(RtePacketReader receiver) {
        this.page = receiver.readInt();
    }

    public PacketRequestAllRails(int page) {
        this.page = page;
    }

    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeInt(page);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        if (!cn.jstxjf_.rte.RTEConfig.allowFullRailSync || page < 0) {
            return;
        }
        server.execute(() -> {
            if (player == null) {
                return;
            }
            try {
                final Simulator simulator =
                    cn.jstxjf_.rte.bridge.MtrBridge.simulator(player.serverLevel());
                if (simulator == null) {
                    return;
                }
                final LinkedHashMap<String, Rail> unique = new LinkedHashMap<>();
                for (final var byPosition : simulator.positionsToRail.values()) {
                    for (final Rail rail : byPosition.values()) {
                        if (rail != null) {
                            unique.putIfAbsent(rail.getHexId(), rail);
                        }
                    }
                }
                final List<Rail> all = new ArrayList<>(unique.values());
                all.sort(Comparator.comparing(Rail::getHexId));
                final long fromLong = (long) page * PAGE_SIZE;
                if (fromLong > Integer.MAX_VALUE) {
                    return;
                }
                final int from = (int) fromLong;
                if (from >= all.size()) {
                    cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketAllRails(page, false, List.of()));
                    return;
                }
                final int to = Math.min(all.size(), from + PAGE_SIZE);
                final List<PacketAllRails.RailLine> lines = new ArrayList<>(to - from);
                for (int i = from; i < to; i++) {
                    final PacketAllRails.RailLine line = sample(all.get(i));
                    if (line != null) {
                        lines.add(line);
                    }
                }
                cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketAllRails(page, to < all.size(), lines));
            } catch (Exception ignored) {
            }
        });
    }

    private static PacketAllRails.RailLine sample(Rail rail) {
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
            return null;
        }
        try {
            final Position p1 = extra.getExtraPosition1();
            final Position p2 = extra.getExtraPosition2();
            if (p1 == null || p2 == null) {
                return null;
            }
            final double length = extra.getLength();
            final boolean straight = !Double.isFinite(length) || length <= 0;
            final int samples = straight ? 1 : (int) Math.max(1, Math.min(MAX_SAMPLES, Math.ceil(length / 6)));
            final float[] xs = new float[samples + 1];
            final float[] zs = new float[samples + 1];
            for (int i = 0; i <= samples; i++) {
                double x;
                double z;
                if (straight) {
                    x = (i == 0 ? p1.getX() : p2.getX()) + 0.5;
                    z = (i == 0 ? p1.getZ() : p2.getZ()) + 0.5;
                } else {
                    final Vec3 position = extra.getPosition(length * i / samples);
                    x = position.x;
                    z = position.z;
                }
                if (!Double.isFinite(x) || !Double.isFinite(z)) {
                    return null;
                }
                xs[i] = (float) x;
                zs[i] = (float) z;
            }
            return new PacketAllRails.RailLine(rail.getHexId(), 0, xs, zs);
        } catch (Exception e) {
            return null;
        }
    }
}
