package cn.jstxjf_.rte;

import com.google.gson.JsonParser;
import cn.jstxjf_.rte.item.RTEItems;
import cn.jstxjf_.rte.network.RTEPackets;
import org.mtr.mapping.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.jstxjf_.rte.block.RTEBlocks;
import cn.jstxjf_.rte.block.RTEBlockEntityTypes;

public class RTE {
    public static final String MOD_ID = "rte";
    public static final String LEGACY_RESOURCE_NAMESPACE = String.join("", "mtr", "steam", "loco");
    public static final Logger LOGGER = LoggerFactory.getLogger("RTE");
    public static final JsonParser JSON_PARSER = new JsonParser();

    public static final Registry REGISTRY = new Registry();

    public static void init() {
        BuildStamp.logOnce();
        REGISTRY.eventRegistry.registerEndWorldTick(cn.jstxjf_.rte.util.RailRepairUtil::autoSweep);
        REGISTRY.registerCommand("rte", commandBuilder -> {
            commandBuilder.permissionLevel(2);
            commandBuilder.then("repairrails", repairBuilder -> repairBuilder.executes(context -> {
                if (!(context.getWorld().data instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                    context.sendFailure("commands.rte.repair_rails.failed");
                    return 0;
                }
                final int repaired = cn.jstxjf_.rte.util.RailRepairUtil.repairAll(serverLevel);
                if (repaired < 0) {
                    context.sendFailure("commands.rte.repair_rails.failed");
                    return 0;
                }
                context.sendSuccess("commands.rte.repair_rails.done", true, repaired);
                return 1;
            }));
            commandBuilder.then("selftest", testBuilder -> testBuilder.executes(context -> {
                final java.util.List<cn.jstxjf_.test.TestResult> results =
                    cn.jstxjf_.test.RteSelfTest.runAll();
                cn.jstxjf_.test.RteSelfTest.logAll(results);
                for (final cn.jstxjf_.test.TestResult result : results) {
                    context.sendSuccess("commands.rte.selftest.line", false, result.toString());
                }
                context.sendSuccess("commands.rte.selftest.done", true,
                    cn.jstxjf_.test.RteSelfTest.summarise(results));
                return 1;
            }));
            commandBuilder.then("fixoneway", fixBuilder -> fixBuilder.executes(context -> {
                if (!(context.getWorld().data instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                    context.sendFailure("commands.rte.fix_oneway.failed");
                    return 0;
                }
                final int fixed = cn.jstxjf_.rte.util.RailRepairUtil.fixOneWayDirections(serverLevel);
                if (fixed < 0) {
                    context.sendFailure("commands.rte.fix_oneway.failed");
                    return 0;
                }
                context.sendSuccess("commands.rte.fix_oneway.done", true, fixed);
                return 1;
            }));
        });
        RTEBlocks.init();
        RTEBlockEntityTypes.init();
        RTEItems.init();
        RTESoundEvents.init();
        RTEParticleTypes.init();
        RTEPackets.init();
        REGISTRY.init();
    }
    
    public static void initClient() {
    }
}
