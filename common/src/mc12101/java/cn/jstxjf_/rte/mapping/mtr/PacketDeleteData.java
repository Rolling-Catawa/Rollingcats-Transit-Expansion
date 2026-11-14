package cn.jstxjf_.rte.mapping.mtr;

import cn.jstxjf_.rte.mapping.holder.ServerWorld;
import org.mtr.core.data.Position;

public final class PacketDeleteData {

	private PacketDeleteData() {
	}

	public static void sendDirectlyToServerRailId(ServerWorld serverWorld, String railId) {
		org.mtr.packet.PacketDeleteData.sendDirectlyToServerRailId(serverWorld.data, railId);
	}

	public static void sendDirectlyToServerRailNodePosition(ServerWorld serverWorld, Position railNodePosition) {
		org.mtr.packet.PacketDeleteData.sendDirectlyToServerRailNodePosition(serverWorld.data, railNodePosition);
	}
}
