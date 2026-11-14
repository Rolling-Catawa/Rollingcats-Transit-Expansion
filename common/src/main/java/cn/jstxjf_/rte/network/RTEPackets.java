package cn.jstxjf_.rte.network;

import net.minecraft.server.level.ServerPlayer;
import org.mtr.mapping.holder.Hand;

public class RTEPackets {
    
    public static void init() {
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketUpdateRail.class, PacketUpdateRail::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketSetDepotRailRule.class, PacketSetDepotRailRule::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRequestRoutePathColors.class, PacketRequestRoutePathColors::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRoutePathColors.class, PacketRoutePathColors::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketOpenCompoundCreator.class, PacketOpenCompoundCreator::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketUpdateBlockEntity.class, PacketUpdateBlockEntity::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketFreeNodeAction.class, PacketFreeNodeAction::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketUpdateHoldingItem.class, PacketUpdateHoldingItem::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketUpdateTrainCustomConfigs.class, PacketUpdateTrainCustomConfigs::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketReplaceRailNode.class, PacketReplaceRailNode::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketDeleteRail.class, PacketDeleteRail::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketVersionCheck.class, PacketVersionCheck::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketScreen.class, PacketScreen::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketPlaceParallelNodes.class, PacketPlaceParallelNodes::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketPlaceTrackside.class, PacketPlaceTrackside::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketPlaceCatenary.class, PacketPlaceCatenary::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketPlanCatenary.class, PacketPlanCatenary::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRequestVehicleMarkers.class, PacketRequestVehicleMarkers::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketVehicleMarkers.class, PacketVehicleMarkers::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRequestVehiclePath.class, PacketRequestVehiclePath::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketVehiclePath.class, PacketVehiclePath::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRequestAllRails.class, PacketRequestAllRails::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketAllRails.class, PacketAllRails::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketGizmoState.class, PacketGizmoState::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketMechanismState.class, PacketMechanismState::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRailScriptSources.class, PacketRailScriptSources::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRailScriptErrors.class, PacketRailScriptErrors::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketSaveRailScript.class, PacketSaveRailScript::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketAssignRailScript.class, PacketAssignRailScript::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRequestRailScript.class, PacketRequestRailScript::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketRailScriptContent.class, PacketRailScriptContent::new);
        cn.jstxjf_.rte.bridge.network.RteNetwork.register(PacketMechanismMessage.class, PacketMechanismMessage::new);
        cn.jstxjf_.rte.RTE.REGISTRY.eventRegistry.registerPlayerJoin((minecraftServer, serverPlayerEntity) -> PacketVersionCheck.sendVersionCheckS2C((net.minecraft.server.level.ServerPlayer) serverPlayerEntity.data));
        cn.jstxjf_.rte.RTE.REGISTRY.eventRegistry.registerPlayerJoin((minecraftServer, serverPlayerEntity) -> PacketRailScriptSources.sendAll((net.minecraft.server.level.ServerPlayer) serverPlayerEntity.data));
    }
    
    public static void sendOpenCompoundCreatorScreen(ServerPlayer player, Hand hand) {
        cn.jstxjf_.rte.bridge.network.RteNetwork.sendToPlayer(player, new PacketOpenCompoundCreator(hand));
    }
}
