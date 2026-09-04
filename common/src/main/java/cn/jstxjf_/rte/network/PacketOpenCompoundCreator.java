package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.client.gui.CompoundCreatorScreen;
import net.minecraft.client.Minecraft;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public class PacketOpenCompoundCreator extends PacketHandler {

    private final boolean isMainHand;
    
    public PacketOpenCompoundCreator(Hand hand) {
        isMainHand = hand == Hand.MAIN_HAND;
    }
    
    public PacketOpenCompoundCreator(PacketBufferReceiver receiver) {
        isMainHand = receiver.readBoolean();
    }
    
    @Override
    public void write(PacketBufferSender sender) {
        sender.writeBoolean(isMainHand);
    }
    
    @Override
    public void runClient() {
        Minecraft.getInstance().execute(() -> {
            MinecraftClient.getInstance().openScreen(CompoundCreatorScreen.createScreen(null, isMainHand ? Hand.MAIN_HAND : Hand.OFF_HAND));
        });
    }
}
