package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.mixin.SidingAccessor;
import org.mtr.core.data.Depot;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Rail;
import org.mtr.core.data.Siding;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.Init;
import org.mtr.mod.data.RailType;

import java.util.HashMap;
import java.util.Map;

public class PacketRequestRoutePathColors extends PacketHandler {

    public PacketRequestRoutePathColors(PacketBufferReceiver receiver) {
    }

    public PacketRequestRoutePathColors() {
    }

    @Override
    public void write(PacketBufferSender sender) {
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            if (player == null) {
                return;
            }
            final Map<String, Integer> railColors = new HashMap<>();
            try {
                final org.mtr.core.simulation.Simulator simulator =
                    cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode.findSimulator(
                        new org.mtr.mapping.holder.World(
                            ((net.minecraft.server.level.ServerPlayer) player.data).serverLevel()));
                if (simulator == null) {
                    return;
                }
                for (final Siding siding : simulator.sidings) {
                    final Depot depot = siding.area;
                    if (depot == null) {
                        continue;
                    }
                    final Integer color = colorOfDepot(simulator, depot);
                    if (color == null) {
                        continue;
                    }
                    final SidingAccessor accessor = (SidingAccessor) (Object) siding;
                    collect(accessor.rte$getPathMainRoute(), color, railColors);
                    collect(accessor.rte$getPathSidingToMainRoute(), color, railColors);
                    collect(accessor.rte$getPathMainRouteToSiding(), color, railColors);
                }
            } catch (Exception e) {
                cn.jstxjf_.rte.RTE.LOGGER.error("[RoutePathColors] failed to collect", e);
                return;
            }
            cn.jstxjf_.rte.RTE.LOGGER.info("[RoutePathColors] sending {} rails to {}",
                railColors.size(), player.getName().getString());
            Init.REGISTRY.sendPacketToClient(player, new PacketRoutePathColors(railColors));
        });
    }

    private static Integer colorOfDepot(org.mtr.core.simulation.Simulator simulator, Depot depot) {
        try {
            for (final long routeId : depot.getRouteIds()) {
                final org.mtr.core.data.Route route = simulator.routeIdMap.get(routeId);
                if (route != null) {
                    return 0xFF000000 | (route.getColor() & 0xFFFFFF);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static void collect(ObjectArrayList<PathData> path, int color, Map<String, Integer> target) {
        if (path == null) {
            return;
        }
        for (final PathData pathData : path) {
            try {
                final Rail rail = pathData.getRail();
                if (rail != null) {
                    target.putIfAbsent(rail.getHexId(), color);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
