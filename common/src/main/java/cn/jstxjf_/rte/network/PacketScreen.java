package cn.jstxjf_.rte.network;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.gui.*;
import org.mtr.mod.Init;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public class PacketScreen extends PacketHandler {

    public static final int SCREEN_EYE_CANDY = 0;
    public static final int SCREEN_BRUSH_EDIT_RAIL = 1;
    public static final int SCREEN_COMPOUND_CREATOR = 2;
    public static final int SCREEN_DIRECT_NODE = 3;
    public static final int SCREEN_ROUTE_PATH_CREATOR = 5;

    private final long posLong;
    private final int screenId;
    private final int extra; // e.g., hand ordinal for item screens

    public PacketScreen(PacketBufferReceiver receiver) {
        this.posLong = receiver.readLong();
        this.screenId = receiver.readInt();
        this.extra = receiver.readInt();
    }

    public PacketScreen(BlockPos pos, int screenId) {
        this.posLong = pos.asLong();
        this.screenId = screenId;
        this.extra = 0;
    }

    public PacketScreen(int screenId, int extra) {
        this.posLong = 0;
        this.screenId = screenId;
        this.extra = extra;
    }

    @Override
    public void write(PacketBufferSender sender) {
        sender.writeLong(posLong);
        sender.writeInt(screenId);
        sender.writeInt(extra);
    }

    @Override
    public void runServer(MinecraftServer server, ServerPlayerEntity player) {
        // Forward to client
        Init.REGISTRY.sendPacketToClient(player, this);
    }

    @Override
    public void runClient() {
        ClientScreenHandler.openScreen(posLong, screenId, extra);
    }

    public static void sendScreenToClient(ServerPlayerEntity player, BlockPos pos, int screenId) {
        Init.REGISTRY.sendPacketToClient(player, new PacketScreen(pos, screenId));
    }

    public static void sendItemScreenToClient(ServerPlayerEntity player, int screenId, InteractionHand hand) {
        Init.REGISTRY.sendPacketToClient(player, new PacketScreen(screenId, hand.ordinal()));
    }

    private static class ClientScreenHandler {
        static void openScreen(long posLong, int screenId, int extra) {
            Minecraft mc = Minecraft.getInstance();
            mc.execute(() -> {
                net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.of(posLong);
                InteractionHand hand = extra == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                switch (screenId) {
                    case SCREEN_EYE_CANDY:
                        mc.setScreen(EyeCandyScreen.createScreen(pos, null));
                        break;
                    case SCREEN_BRUSH_EDIT_RAIL: {
                        Screen brushScreen = BrushEditRailScreen.createScreen(null);
                        if (brushScreen != null) mc.setScreen(brushScreen.data);
                        break;
                    }
                    case SCREEN_COMPOUND_CREATOR: {
                        Hand mtrHand = extra == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND;
                        mc.setScreen(cn.jstxjf_.rte.client.gui.CompoundCreatorScreen.createScreen(null, mtrHand).data);
                        break;
                    }
                    case SCREEN_DIRECT_NODE:
                        mc.setScreen(DirectNodeScreen.createScreen(mc.level, pos, null));
                        break;
                    case SCREEN_ROUTE_PATH_CREATOR:
                        mc.setScreen(new RoutePathCreatorScreen(hand, null));
                        break;
                    default:
                        RTE.LOGGER.warn("Unknown screen ID: {}", screenId);
                        break;
                }
            });
        }
    }
}
