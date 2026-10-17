package cn.jstxjf_.rte.bridge.network;

import org.mtr.mod.InitClient;

public final class RteClientNetwork {

    private RteClientNetwork() {
    }

    public static void sendToServer(RtePacket packet) {
        InitClient.REGISTRY_CLIENT.sendPacketToServer(packet);
    }
}
