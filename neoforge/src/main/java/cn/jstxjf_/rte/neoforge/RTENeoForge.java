package cn.jstxjf_.rte.neoforge;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(RTE.MOD_ID)
public final class RTENeoForge {
    public RTENeoForge(IEventBus modEventBus) {
        RTE.init();
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> RTEConfig.load(event.getServer()));
    }
}
