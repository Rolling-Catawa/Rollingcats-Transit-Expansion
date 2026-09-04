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
        return results;
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
