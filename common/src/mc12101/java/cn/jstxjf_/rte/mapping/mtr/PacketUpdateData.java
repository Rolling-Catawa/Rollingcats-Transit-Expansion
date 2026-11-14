package cn.jstxjf_.rte.mapping.mtr;

import cn.jstxjf_.rte.mapping.holder.ServerWorld;
import org.mtr.core.data.Rail;
import org.mtr.core.operation.UpdateDataRequest;

public final class PacketUpdateData {

	private final UpdateDataRequest updateDataRequest;

	public PacketUpdateData(UpdateDataRequest updateDataRequest) {
		this.updateDataRequest = updateDataRequest;
	}

	public void sendToServer() {
		org.mtr.registry.RegistryClient.sendPacketToServer(new org.mtr.packet.PacketUpdateData(updateDataRequest));
	}

	public static void sendDirectlyToServerRail(ServerWorld serverWorld, Rail rail) {
		org.mtr.packet.PacketUpdateData.sendDirectlyToServerRail(serverWorld.data, rail);
	}
}
