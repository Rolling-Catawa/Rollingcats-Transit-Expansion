package cn.jstxjf_.rte.fabric;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class RTEFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RTE.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            RTEConfig.load(server);
        });
    }
}
