package cn.jstxjf_.rte.forge;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEConfig;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RTE.MOD_ID)
public final class RTEForge {
    public RTEForge() {
        EventBuses.registerModEventBus(RTE.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        RTE.init();

        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
            RTEConfig.load(event.getServer());
        });
    }
}
