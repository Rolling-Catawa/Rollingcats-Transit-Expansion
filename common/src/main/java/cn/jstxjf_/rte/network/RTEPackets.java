package cn.jstxjf_.rte.network;

import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mod.Init;

public class RTEPackets {
    
    public static void init() {
        Init.REGISTRY.registerPacket(PacketUpdateRail.class, PacketUpdateRail::new);
        Init.REGISTRY.registerPacket(PacketSetDepotRailRule.class, PacketSetDepotRailRule::new);
        Init.REGISTRY.registerPacket(PacketRequestRoutePathColors.class, PacketRequestRoutePathColors::new);
        Init.REGISTRY.registerPacket(PacketRoutePathColors.class, PacketRoutePathColors::new);
        Init.REGISTRY.registerPacket(PacketOpenCompoundCreator.class, PacketOpenCompoundCreator::new);
        Init.REGISTRY.registerPacket(PacketUpdateBlockEntity.class, PacketUpdateBlockEntity::new);
        Init.REGISTRY.registerPacket(PacketUpdateHoldingItem.class, PacketUpdateHoldingItem::new);
        Init.REGISTRY.registerPacket(PacketUpdateTrainCustomConfigs.class, PacketUpdateTrainCustomConfigs::new);
        Init.REGISTRY.registerPacket(PacketReplaceRailNode.class, PacketReplaceRailNode::new);
        Init.REGISTRY.registerPacket(PacketDeleteRail.class, PacketDeleteRail::new);
        Init.REGISTRY.registerPacket(PacketVersionCheck.class, PacketVersionCheck::new);
        Init.REGISTRY.registerPacket(PacketScreen.class, PacketScreen::new);
        Init.REGISTRY.registerPacket(PacketPlaceParallelNodes.class, PacketPlaceParallelNodes::new);
        Init.REGISTRY.registerPacket(PacketPlaceTrackside.class, PacketPlaceTrackside::new);
        Init.REGISTRY.registerPacket(PacketPlaceCatenary.class, PacketPlaceCatenary::new);
        Init.REGISTRY.registerPacket(PacketPlanCatenary.class, PacketPlanCatenary::new);
        Init.REGISTRY.registerPacket(PacketRequestVehicleMarkers.class, PacketRequestVehicleMarkers::new);
        Init.REGISTRY.registerPacket(PacketVehicleMarkers.class, PacketVehicleMarkers::new);
        Init.REGISTRY.registerPacket(PacketRequestVehiclePath.class, PacketRequestVehiclePath::new);
        Init.REGISTRY.registerPacket(PacketVehiclePath.class, PacketVehiclePath::new);
        Init.REGISTRY.registerPacket(PacketRequestAllRails.class, PacketRequestAllRails::new);
        Init.REGISTRY.registerPacket(PacketAllRails.class, PacketAllRails::new);
        Init.REGISTRY.eventRegistry.registerPlayerJoin((minecraftServer, serverPlayerEntity) -> PacketVersionCheck.sendVersionCheckS2C(serverPlayerEntity));
    }
    
    public static void sendOpenCompoundCreatorScreen(ServerPlayerEntity player, Hand hand) {
        Init.REGISTRY.sendPacketToClient(player, new PacketOpenCompoundCreator(hand));
    }
}
