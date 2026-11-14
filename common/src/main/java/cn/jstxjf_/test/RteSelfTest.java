package cn.jstxjf_.test;

import cn.jstxjf_.rte.RTE;

import java.util.List;

public final class RteSelfTest {

    private RteSelfTest() {
    }

    public static List<TestResult> runLogicOnly() {
        final List<TestResult> results = TestResult.newList();
        safely(results, "logic", () -> LogicChecks.run(results));
        return results;
    }

    public static List<TestResult> runAll() {
        final List<TestResult> results = TestResult.newList();
        safely(results, "logic", () -> LogicChecks.run(results));
        safely(results, "runtime", () -> RuntimeChecks.run(results));
        safely(results, "mechanism", () -> mechanismChecks(results));
        return results;
    }

    private static void mechanismChecks(List<TestResult> results) {
        final cn.jstxjf_.rte.mechanism.RailScriptRuntime runtime = cn.jstxjf_.rte.mechanism.RailScriptRuntime.current();
        if (runtime == null) {
            results.add(TestResult.skip("mechanism/transformMovesPathPosition", "rail script runtime not started"));
            return;
        }
        final org.mtr.core.simulation.Simulator simulator;
        try {
            simulator = cn.jstxjf_.rte.bridge.MtrBridge.simulator(runtime.server.overworld());
        } catch (Exception e) {
            results.add(TestResult.skip("mechanism/transformMovesPathPosition", e.toString()));
            return;
        }
        final org.mtr.core.data.Rail rail = simulator == null ? null : simulator.railIdMap.values().stream()
            .filter(candidate -> ((cn.jstxjf_.rte.data.RailExtraSupplier) (Object) candidate).getMechanismGroup().isEmpty())
            .findFirst().orElse(null);
        if (rail == null) {
            results.add(TestResult.skip("mechanism/transformMovesPathPosition", "no rail in overworld"));
            return;
        }
        final cn.jstxjf_.rte.data.RailExtraSupplier extra = (cn.jstxjf_.rte.data.RailExtraSupplier) (Object) rail;
        final net.minecraft.world.phys.Vec3 before = extra.getPosition(0);
        final net.minecraft.world.phys.Vec3 mid = extra.getPosition(extra.getLength() / 2);
        final cn.jstxjf_.rte.mechanism.MechanismTransform transform =
            new cn.jstxjf_.rte.mechanism.MechanismTransform(mid.x, mid.y, mid.z, 90, 0, 0, 0, 0, 0);
        extra.setMechanismTransform(transform);
        final net.minecraft.world.phys.Vec3 after;
        try {
            after = extra.getPosition(0);
        } finally {
            extra.setMechanismTransform(cn.jstxjf_.rte.mechanism.MechanismTransform.IDENTITY);
        }
        final double[] expected = transform.apply(before.x, before.y, before.z);
        final double error = Math.abs(after.x - expected[0]) + Math.abs(after.y - expected[1]) + Math.abs(after.z - expected[2]);
        results.add(error < 1E-6
            ? TestResult.pass("mechanism/transformMovesPathPosition", rail.getHexId())
            : TestResult.fail("mechanism/transformMovesPathPosition", "error=" + error));
    }

    private static void safely(List<TestResult> results, String group, Runnable body) {
        try {
            body.run();
        } catch (Throwable e) {
            results.add(TestResult.fail(group + "/crashed", e.toString()));
        }
    }

    public static String summarise(List<TestResult> results) {
        int pass = 0;
        int fail = 0;
        int info = 0;
        int skip = 0;
        for (final TestResult result : results) {
            switch (result.status) {
                case PASS -> pass++;
                case FAIL -> fail++;
                case INFO -> info++;
                case SKIP -> skip++;
            }
        }
        return String.format("%d passed, %d failed, %d informational, %d skipped", pass, fail, info, skip);
    }

    public static void logAll(List<TestResult> results) {
        RTE.LOGGER.info("[SelfTest] ==== RTE self test ====");
        for (final TestResult result : results) {
            if (result.status == TestResult.Status.FAIL) {
                RTE.LOGGER.warn("[SelfTest] {}", result);
            } else {
                RTE.LOGGER.info("[SelfTest] {}", result);
            }
        }
        RTE.LOGGER.info("[SelfTest] {}", summarise(results));
    }
}
