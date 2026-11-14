package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.bridge.network.RtePacket;
import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import cn.jstxjf_.rte.bridge.network.RtePacketWriter;

import cn.jstxjf_.rte.client.gui.CompoundCreatorScreen;
import net.minecraft.client.Minecraft;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.MinecraftClient;

public class PacketOpenCompoundCreator extends RtePacket {

    private final boolean isMainHand;
    
    public PacketOpenCompoundCreator(Hand hand) {
        isMainHand = hand == Hand.MAIN_HAND;
    }
    
    public PacketOpenCompoundCreator(RtePacketReader receiver) {
        isMainHand = receiver.readBoolean();
    }
    
    @Override
    protected void write(RtePacketWriter sender) {
        sender.writeBoolean(isMainHand);
    }
    
    @Override
    protected void handleClient() {
        Minecraft.getInstance().execute(() -> {
            MinecraftClient.getInstance().openScreen(CompoundCreatorScreen.createScreen(null, isMainHand ? Hand.MAIN_HAND : Hand.OFF_HAND));
        });
    }
}
