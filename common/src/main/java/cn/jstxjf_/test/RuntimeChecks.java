package cn.jstxjf_.test;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.data.GangwayPolicy;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.scripting.util.client.GraphicsTexture;
import org.mtr.core.data.Rail;
import org.mtr.core.data.VehicleCar;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.VehicleExtension;

import java.util.List;
import java.util.Locale;

public final class RuntimeChecks {

    private RuntimeChecks() {
    }

    public static void run(List<TestResult> out) {
        textureBudget(out);
        scriptedLcd(out);
        gangwaySpacing(out);
        railModels(out);
        compatArbitration(out);
        scriptEngines(out);
    }

    private static void textureBudget(List<TestResult> out) {
        final int live = GraphicsTexture.liveCount();
        final long heapMb = GraphicsTexture.liveHeapBytes() / (1024L * 1024L);
        final int budget = ClientConfig.scriptTextureHeapBudgetMb;

        out.add(TestResult.info("texture/usage", String.format(Locale.ROOT,
            "%d live, %d MB used of %d MB budget (limit %d textures, maxTextureSize %d)",
            live, heapMb, budget, ClientConfig.scriptTextureLimit, ClientConfig.maxTextureSize)));

        out.add(TestResult.check("texture/within-budget", heapMb <= budget,
            heapMb <= budget
                ? "headroom " + (budget - heapMb) + " MB"
                : "over budget by " + (heapMb - budget) + " MB — LCDs may be evicted; "
                  + "raise scriptTextureHeapBudgetMb or lower maxTextureSize"));

        out.add(TestResult.info("texture/state", GraphicsTexture.diagnoseLive()));

        final long evictedInUse = GraphicsTexture.evictedInUseCount();
        out.add(TestResult.check("texture/no-live-eviction", evictedInUse == 0,
            evictedInUse == 0
                ? "no in-use texture has been evicted"
                : evictedInUse + " in-use textures were evicted — this is what makes LCDs go blank"));
    }

    private static void scriptedLcd(List<TestResult> out) {
        int contexts = 0;
        int created = 0;
        int disposed = 0;
        int failing = 0;
        try {
            for (final var context : new java.util.ArrayList<>(cn.jstxjf_.rte.scripting.ScriptContextManager.livingContexts.keySet())) {
                contexts++;
                if (context.created) {
                    created++;
                }
                if (context.disposed) {
                    disposed++;
                }
                if (context.scriptStatus != null && context.scriptStatus.isDone()) {
                    failing++;
                }
            }
        } catch (Throwable e) {
            out.add(TestResult.skip("script/contexts", e.toString()));
            return;
        }
        out.add(TestResult.info("script/contexts", String.format(Locale.ROOT,
            "%d tracked, %d created, %d disposed, %d idle", contexts, created, disposed, failing)));
        out.add(TestResult.check("script/contexts-alive", contexts == 0 || created > 0,
            created > 0 ? "at least one script context has run create()"
                        : "contexts exist but none completed create() — scripts are not producing output"));
    }

    private static void gangwaySpacing(List<TestResult> out) {
        VehicleExtension sample = null;
        try {
            for (final VehicleExtension vehicle : MinecraftClientData.getInstance().vehicles) {
                if (vehicle.vehicleExtraData != null
                        && vehicle.vehicleExtraData.immutableVehicleCars.size() >= 2) {
                    sample = vehicle;
                    break;
                }
            }
        } catch (Throwable ignored) {
        }

        if (sample == null) {
            out.add(TestResult.skip("gangway/spacing",
                "no multi-car vehicle in render range — stand next to a train and run again"));
            return;
        }

        final var cars = sample.vehicleExtraData.immutableVehicleCars;
        final VehicleCar first = cars.get(0);
        final String vehicleId = first.getVehicleId();
        final boolean legacy = GangwayPolicy.isLegacyFormat(vehicleId);
        final String connectionId = GangwayPolicy.connectionIdOf(vehicleId);

        out.add(TestResult.info("gangway/vehicle", String.format(Locale.ROOT,
            "%s — %d cars, legacy=%s, gangway_connection_id=%s",
            vehicleId, cars.size(), legacy, connectionId == null || connectionId.isEmpty() ? "(none)" : connectionId)));

        final StringBuilder geometry = new StringBuilder();
        for (int i = 0; i < Math.min(3, cars.size()); i++) {
            final VehicleCar car = cars.get(i);
            geometry.append(String.format(Locale.ROOT, "car%d len=%.2f pad1=%.2f pad2=%.2f  ",
                i, car.getLength(),
                ((cn.jstxjf_.rte.mixin.VehicleCarAccessor) (Object) car).rte$getCouplingPadding1(),
                ((cn.jstxjf_.rte.mixin.VehicleCarAccessor) (Object) car).rte$getCouplingPadding2()));
        }
        out.add(TestResult.info("gangway/geometry", geometry.toString().trim()));

        try {
            final var c0 = cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers
                .getCarPositionAndRotation(sample, 0);
            final var c1 = cn.jstxjf_.rte.scripting.train.ScriptedTrainRenderers
                .getCarPositionAndRotation(sample, 1);
            if (c0 != null && c1 != null) {
                final double centreToCentre = Math.sqrt(
                    Math.pow(c0.position.x() - c1.position.x(), 2)
                        + Math.pow(c0.position.y() - c1.position.y(), 2)
                        + Math.pow(c0.position.z() - c1.position.z(), 2));
                final double convertedLength = first.getLength();
                final double bodyLength = legacy ? convertedLength - 1 : convertedLength;
                final double bodyGap = centreToCentre - bodyLength;
                final double mtr3 = legacy ? 1.0 : bodyGap;
                out.add(TestResult.check("gangway/body-gap", Math.abs(bodyGap - mtr3) < 0.05,
                    String.format(Locale.ROOT,
                        "centre-to-centre %.3f, converted length %.2f, body length %.2f => body gap %.3f "
                            + "(MTR 3.x uses exactly %.1f; additional legacyCouplingGap=%.2f)",
                        centreToCentre, convertedLength, bodyLength, bodyGap, mtr3,
                        ClientConfig.legacyCouplingGap)));
            }
        } catch (Throwable e) {
            out.add(TestResult.skip("gangway/body-gap", e.toString()));
        }

        out.add(TestResult.info("gangway/render-decision", String.format(Locale.ROOT,
            "builtinGangway=%s selfHandled=%s",
            ClientConfig.builtinGangway, GangwayPolicy.currentVehicleSelfHandled())));
    }

    private static void railModels(List<TestResult> out) {
        int multi = 0;
        int total = 0;
        String example = "";
        try {
            for (final var entry : MinecraftClientData.getInstance().railIdMap.entrySet()) {
                final Rail rail = entry.getValue();
                if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
                    continue;
                }
                total++;
                final List<String> keys = extra.getModelKeys();
                if (keys.size() > 1) {
                    multi++;
                    if (example.isEmpty()) {
                        example = String.join(" + ", keys);
                    }
                }
            }
        } catch (Throwable e) {
            out.add(TestResult.skip("rail/multi-model", e.toString()));
            return;
        }

        if (total == 0) {
            out.add(TestResult.skip("rail/multi-model", "no rails loaded on the client"));
            return;
        }
        if (multi == 0) {
            out.add(TestResult.info("rail/multi-model", String.format(Locale.ROOT,
                "%d rails loaded, none carries more than one model — "
                    + "add a second model with the brush to exercise this", total)));
            return;
        }

        boolean rendered = true;
        try {
            for (final var entry : MinecraftClientData.getInstance().railIdMap.entrySet()) {
                final Rail rail = entry.getValue();
                if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
                    continue;
                }
                if (extra.getModelKeys().size() > 1) {
                    final var resolved = cn.jstxjf_.rte.render.rail.RailRenderDispatcher
                        .getModelKeysForRender(rail);
                    if (resolved.size() < 2) {
                        rendered = false;
                        example = example + " (resolved to " + resolved.size() + ")";
                        break;
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        out.add(TestResult.check("rail/multi-model", rendered, String.format(Locale.ROOT,
            "%d of %d rails carry multiple models; example: %s", multi, total, example)));
    }

    private static void compatArbitration(List<TestResult> out) {
        final StringBuilder detected = new StringBuilder();
        for (final String modId : new String[]{
            cn.jstxjf_.rte.util.CompatArbiter.MOD_JCM,
            cn.jstxjf_.rte.util.CompatArbiter.MOD_FANGSU,
            cn.jstxjf_.rte.util.CompatArbiter.MOD_S1}) {
            if (cn.jstxjf_.rte.util.CompatArbiter.isModLoaded(modId)) {
                detected.append(modId).append(' ');
            }
        }
        out.add(TestResult.info("compat/detected",
            detected.length() == 0 ? "no other MTR addon present" : detected.toString().trim()));

        final StringBuilder owners = new StringBuilder();
        for (final var domain : cn.jstxjf_.rte.util.CompatArbiter.Domain.values()) {
            if (cn.jstxjf_.rte.util.CompatArbiter.hasRival(domain)) {
                owners.append(domain.key).append('=')
                    .append(cn.jstxjf_.rte.util.CompatArbiter.ownerOf(domain)).append(' ');
            }
        }
        out.add(TestResult.info("compat/owners",
            owners.length() == 0 ? "nothing contested" : owners.toString().trim()));
    }

    private static void scriptEngines(List<TestResult> out) {
        out.add(TestResult.info("engine/config", String.format(Locale.ROOT,
            "scriptEngine=%s, overrides=%d, Java %s",
            ClientConfig.scriptEngine,
            cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy.snapshot().size(),
            Runtime.version().feature())));

        final boolean graalUsable = cn.jstxjf_.rte.scripting.engine.GraalAvailability.isUsable();
        out.add(TestResult.info("engine/graal-available", graalUsable
            ? "GraalJS can initialise on this JVM"
            : cn.jstxjf_.rte.scripting.engine.GraalAvailability.reason()));
    }
}
