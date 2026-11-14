package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.mechanism.RailScriptPolicy;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.mtr.core.data.Rail;

import java.util.ArrayList;
import java.util.List;

public class PacketAssignRailScript extends RtePacket {

    private final List<String> railIds;
    private final String group;
    private final String scriptId;

    public PacketAssignRailScript(List<String> railIds, String group, String scriptId) {
        this.railIds = railIds;
        this.group = group == null ? "" : group.trim();
        this.scriptId = scriptId == null ? "" : scriptId.trim();
    }

    public PacketAssignRailScript(RtePacketReader reader) {
        final int count = Math.max(0, Math.min(256, reader.readInt()));
        railIds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            railIds.add(reader.readString());
        }
        group = reader.readString();
        scriptId = reader.readString();
    }

    @Override
    protected void write(RtePacketWriter writer) {
        writer.writeInt(railIds.size());
        railIds.forEach(writer::writeString);
        writer.writeString(group);
        writer.writeString(scriptId);
    }

    @Override
    protected void handleServer(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final org.mtr.core.simulation.Simulator simulator;
            try {
                simulator = cn.jstxjf_.rte.bridge.MtrBridge.simulator(cn.jstxjf_.rte.compat.McCompat.serverLevel(player));
            } catch (Exception e) {
                return;
            }
            if (simulator == null) {
                return;
            }
            simulator.run(() -> {
                final List<Rail> rails = new ArrayList<>();
                for (final String id : railIds) {
                    final Rail rail = simulator.railIdMap.get(id);
                    if (rail != null) {
                        rails.add(rail);
                    }
                }
                server.execute(() -> apply(server, player, rails));
            });
        });
    }

    private void apply(MinecraftServer server, ServerPlayer player, List<Rail> rails) {
        for (final Rail rail : rails) {
            final String decision = RailScriptPolicy.decideAssign(player.hasPermissions(2), player.isCreative(),
                cn.jstxjf_.rte.util.RailEditPermission.allowedRail(player, rail), group, scriptId);
            if (!decision.equals(RailScriptPolicy.OK)) {
                RTE.LOGGER.warn("[RailScript] assign on {} from {} refused: {}", rail.getHexId(), player.getGameProfile().getName(), decision);
                continue;
            }
            if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
                continue;
            }
            extra.setMechanismGroup(group);
            extra.setRailScriptId(group.isEmpty() ? "" : scriptId);
            extra.setMechanismTransform(cn.jstxjf_.rte.mechanism.MechanismTransform.IDENTITY);
            extra.setMechanismHidden(false);
            cn.jstxjf_.rte.bridge.MtrBridge.sendRailToServer(cn.jstxjf_.rte.compat.McCompat.serverLevel(player), rail);
        }
    }
}
