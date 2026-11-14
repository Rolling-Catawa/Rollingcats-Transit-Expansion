package cn.jstxjf_.rte.neoforge;

import cn.jstxjf_.rte.KeyMappings;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.RTECreativeModeEntries;
import cn.jstxjf_.rte.client.gui.RTEConfigScreen;
import cn.jstxjf_.rte.gui.TrainScreen;
import cn.jstxjf_.rte.util.TrainCollisionManager;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = RTE.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RTENeoForgeClient {

    private RTENeoForgeClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModList.get().getModContainerById(RTE.MOD_ID).ifPresent(container -> container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (IConfigScreenFactory) (modContainer, parent) -> RTEConfigScreen.createScreen(parent)
            ));
            RTEClient.init();
        });
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyMappings.RAIL_EDIT_KEY);
        event.register(KeyMappings.DEBUG_KEY);
        event.register(KeyMappings.TRAIN_SCREEN);
        event.register(KeyMappings.CONFIG_SCREEN);
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (CreativeModeTabs.TOOLS_AND_UTILITIES.equals(event.getTabKey())) {
            RTECreativeModeEntries.getToolsAndUtilitiesItems().forEach(event::accept);
        }
    }

    @EventBusSubscriber(modid = RTE.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class GameEvents {

        private GameEvents() {
        }

        @SubscribeEvent
        public static void onClientTickPre(ClientTickEvent.Pre event) {
            TrainCollisionManager.clientTick();
        }

        @SubscribeEvent
        public static void onClientTickPost(ClientTickEvent.Post event) {
            RTEClient.tick();
            TrainScreen.tick();
        }
    }
}
