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
        REGISTRY.eventRegistry.registerEndWorldTick(world -> cn.jstxjf_.rte.mechanism.RailScriptRuntime.tick((net.minecraft.server.level.ServerLevel) world.data));
        REGISTRY.eventRegistry.registerEndWorldTick(world -> cn.jstxjf_.rte.data.RailTrainTriggers.tick((net.minecraft.server.level.ServerLevel) world.data));
        REGISTRY.eventRegistry.registerServerStopping(server -> cn.jstxjf_.rte.mechanism.RailScriptRuntime.onServerStopping(server.data));
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
            commandBuilder.then("rails", railsBuilder -> {
                railsBuilder.then("check", checkBuilder -> checkBuilder.executes(context -> {
                    if (!(context.getWorld().data instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                        context.sendFailure("commands.rte.rails.failed");
                        return 0;
                    }
                    final java.util.List<org.mtr.core.data.Rail> dangling = cn.jstxjf_.rte.util.RailRepairUtil.findDanglingRails(serverLevel);
                    if (dangling == null) {
                        context.sendFailure("commands.rte.rails.failed");
                        return 0;
                    }
                    for (int i = 0; i < Math.min(10, dangling.size()); i++) {
                        final cn.jstxjf_.rte.data.RailExtraSupplier extra = (cn.jstxjf_.rte.data.RailExtraSupplier) (Object) dangling.get(i);
                        context.sendSuccess("commands.rte.rails.check.line", false,
                            cn.jstxjf_.rte.util.RailRepairUtil.describe(extra.getExtraPosition1()),
                            cn.jstxjf_.rte.util.RailRepairUtil.describe(extra.getExtraPosition2()));
                    }
                    context.sendSuccess("commands.rte.rails.check.done", false, dangling.size());
                    return 1;
                }));
                railsBuilder.then("clean", cleanBuilder -> cleanBuilder.executes(context -> {
                    if (!(context.getWorld().data instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                        context.sendFailure("commands.rte.rails.failed");
                        return 0;
                    }
                    final int removed = cn.jstxjf_.rte.util.RailRepairUtil.cleanDanglingRails(serverLevel);
                    if (removed < 0) {
                        context.sendFailure("commands.rte.rails.failed");
                        return 0;
                    }
                    context.sendSuccess("commands.rte.rails.clean.done", true, removed);
                    return 1;
                }));
            });
            commandBuilder.then("railscripts", scriptsBuilder -> {
                scriptsBuilder.then("reload", reloadBuilder -> reloadBuilder.executes(context -> {
                    if (!(context.getWorld().data instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                        return 0;
                    }
                    cn.jstxjf_.rte.mechanism.RailScriptRuntime.get(serverLevel.getServer()).reloadAll();
                    context.sendSuccess("commands.rte.railscripts.reloaded", true);
                    return 1;
                }));
                scriptsBuilder.then("list", listBuilder -> listBuilder.executes(context -> {
                    if (!(context.getWorld().data instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
                        return 0;
                    }
                    final cn.jstxjf_.rte.mechanism.RailScriptRuntime runtime = cn.jstxjf_.rte.mechanism.RailScriptRuntime.get(serverLevel.getServer());
                    context.sendSuccess("commands.rte.railscripts.scripts", false, String.join(", ", runtime.store.list()));
                    runtime.levelState(serverLevel).debugRows().forEach((group, row) ->
                        context.sendSuccess("commands.rte.railscripts.mechanism", false, group, String.valueOf(row)));
                    return 1;
                }));
            });
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
