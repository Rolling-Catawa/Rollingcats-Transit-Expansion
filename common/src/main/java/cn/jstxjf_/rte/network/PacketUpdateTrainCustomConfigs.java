package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.data.TrainExtraSupplier;
import cn.jstxjf_.rte.util.StringMapSerializer;
import org.mtr.core.Main;
import org.mtr.core.data.Siding;
import org.mtr.core.data.Vehicle;
import org.mtr.core.simulation.Simulator;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;
import org.mtr.mod.Init;
import org.mtr.mod.InitClient;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PacketUpdateTrainCustomConfigs extends PacketHandler {

    private final long sidingId;
    private final long vehicleId;
    private final String serializedConfigs;
    private static Field initMainField;
    private static Field worldIdListField;
    private static Field mainSimulatorsField;
    private static Field sidingVehiclesField;

    public PacketUpdateTrainCustomConfigs(PacketBufferReceiver receiver) {
        this.sidingId = receiver.readLong();
        this.vehicleId = receiver.readLong();
        this.serializedConfigs = receiver.readString();
    }

    public PacketUpdateTrainCustomConfigs(long sidingId, long vehicleId, Map<String, String> customConfigs) {
        this.sidingId = sidingId;
        this.vehicleId = vehicleId;
        String str = "";
        try {
            str = StringMapSerializer.serializeToString(customConfigs);
        } catch (Exception e) {
            RTE.LOGGER.error("Failed serializing train custom configs", e);
        }
        this.serializedConfigs = str;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeLong(sidingId);
        sender.writeLong(vehicleId);
        sender.writeString(serializedConfigs);
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            try {
                Map<String, String> customConfigs = StringMapSerializer.deserialize(serializedConfigs);
                if (customConfigs == null) customConfigs = new HashMap<>();
                Vehicle vehicle = findVehicle(player, sidingId, vehicleId);
                if (vehicle instanceof TrainExtraSupplier supplier) {
                    supplier.setCustomConfigs(customConfigs);
                    supplier.isConfigsChanged(true);
                }
            } catch (Exception e) {
                RTE.LOGGER.error("Failed applying train custom configs", e);
            }
        });
    }

    public static void sendUpdateC2S(Vehicle vehicle, Map<String, String> customConfigs) {
        if (customConfigs == null) return;
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketUpdateTrainCustomConfigs(vehicle.vehicleExtraData.getSidingId(), vehicle.getId(), customConfigs));
    }

    @SuppressWarnings("unchecked")
    private static Vehicle findVehicle(ServerPlayerEntity player, long sidingId, long vehicleId) throws Exception {
        if (initMainField == null) {
            initMainField = Init.class.getDeclaredField("main");
            initMainField.setAccessible(true);
        }
        if (worldIdListField == null) {
            worldIdListField = Init.class.getDeclaredField("WORLD_ID_LIST");
            worldIdListField.setAccessible(true);
        }
        if (mainSimulatorsField == null) {
            mainSimulatorsField = Main.class.getDeclaredField("simulators");
            mainSimulatorsField.setAccessible(true);
        }
        if (sidingVehiclesField == null) {
            sidingVehiclesField = Siding.class.getDeclaredField("vehicles");
            sidingVehiclesField.setAccessible(true);
        }

        final Main main = (Main) initMainField.get(null);
        if (main == null) {
            return null;
        }

        final List<String> worldIds = (List<String>) worldIdListField.get(null);
        final String worldId = Init.getWorldId(new World(player.getEntityWorld().data));
        final int worldIndex = worldIds.indexOf(worldId);
        if (worldIndex < 0) {
            return null;
        }

        final List<Simulator> simulators = (List<Simulator>) mainSimulatorsField.get(main);
        if (worldIndex >= simulators.size()) {
            return null;
        }

        final Simulator simulator = simulators.get(worldIndex);
        final Siding siding = simulator.sidingIdMap.get(sidingId);
        if (siding == null) {
            return null;
        }

        final Set<Vehicle> vehicles = (Set<Vehicle>) sidingVehiclesField.get(siding);
        for (final Vehicle vehicle : vehicles) {
            if (vehicle.getId() == vehicleId) {
                return vehicle;
            }
        }
        return null;
    }
}

