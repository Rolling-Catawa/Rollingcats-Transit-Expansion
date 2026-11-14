import cn.jstxjf_.rte.util.RailEditRules;
import cn.jstxjf_.rte.mechanism.MechanismTransform;
import cn.jstxjf_.rte.mechanism.RailPose;
import cn.jstxjf_.rte.mechanism.RailScriptStore;
import cn.jstxjf_.rte.mechanism.Mechanism;
import cn.jstxjf_.rte.mechanism.MechanismRegistry;
import cn.jstxjf_.rte.mechanism.MechanismGate;
import cn.jstxjf_.rte.mechanism.RailScriptPolicy;
import cn.jstxjf_.rte.mechanism.MechanismEvent;
import cn.jstxjf_.rte.mechanism.MessageRateLimiter;
import cn.jstxjf_.rte.mechanism.TrainProximityTracker;

import java.nio.file.Files;
import java.nio.file.Path;

import java.lang.reflect.Method;

public class RailScriptsTest {

    private static int passed = 0;

    public static void main(String[] args) throws Exception {
        for (final Method method : RailScriptsTest.class.getDeclaredMethods()) {
            if (method.getName().startsWith("test") && method.getParameterCount() == 0) {
                try {
                    method.invoke(null);
                    passed++;
                } catch (java.lang.reflect.InvocationTargetException e) {
                    System.err.println("FAIL " + method.getName() + ": " + e.getCause());
                    e.getCause().printStackTrace();
                    System.exit(1);
                }
            }
        }
        System.out.println("RAIL SCRIPTS: " + passed + " passed");
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void near(double actual, double expected, double epsilon, String message) {
        if (!(Math.abs(actual - expected) <= epsilon)) {
            throw new AssertionError(message + " expected " + expected + " got " + actual);
        }
    }

    static void testPermissionRules() {
        check(RailEditRules.allowed(true, false, 1e9), "op anywhere");
        check(RailEditRules.allowed(false, true, 100), "builder nearby");
        check(!RailEditRules.allowed(false, true, 128 * 128 + 1), "builder too far");
        check(!RailEditRules.allowed(false, false, 0), "adventure denied");
    }

    static void testApplyRotatesAroundPivot() {
        final MechanismTransform t = new MechanismTransform(10, 0, 10, 90, 0, 0, 0, 0, 0);
        final double[] p = t.apply(11, 0, 10);
        near(p[0], 10, 1e-9, "x");
        near(p[1], 0, 1e-9, "y");
        near(p[2], 11, 1e-9, "z");
    }

    static void testBetweenMapsEndpoints() {
        final RailPose from = new RailPose(0, 0, 0, 10, 0, 0);
        final RailPose to = new RailPose(5, 0, -5, 5, 0, 5);
        final MechanismTransform t = MechanismTransform.between(from, to);
        final double[] a = t.apply(0, 0, 0);
        final double[] b = t.apply(10, 0, 0);
        final boolean forward = Math.abs(a[2] + 5) < 1e-6 && Math.abs(b[2] - 5) < 1e-6;
        final boolean backward = Math.abs(a[2] - 5) < 1e-6 && Math.abs(b[2] + 5) < 1e-6;
        near(a[0], 5, 1e-6, "a.x");
        near(b[0], 5, 1e-6, "b.x");
        check(forward || backward, "endpoints mapped");
    }

    static void testTransformBetweenDifferentLengths() {
        final RailPose from = new RailPose(0, 64, 0, 10, 64, 0);
        final RailPose to = new RailPose(0, 64, 3, 0, 64, 17);
        final MechanismTransform t = MechanismTransform.between(from, to);
        for (final String value : t.encode().split(",")) {
            check(Double.isFinite(Double.parseDouble(value)), "finite " + value);
        }
        final double[] mid = t.apply(from.midX(), from.midY(), from.midZ());
        near(mid[0], to.midX(), 1e-6, "mid x");
        near(mid[2], to.midZ(), 1e-6, "mid z");
    }

    static void testLerpShortestArc() {
        final MechanismTransform a = new MechanismTransform(0, 0, 0, 350, 0, 0, 0, 0, 0);
        final MechanismTransform b = new MechanismTransform(0, 0, 0, 10, 0, 0, 0, 0, 0);
        near(a.lerp(b, 0.5).yaw, 0, 1e-9, "yaw");
    }

    static void testAlignedTolerance() {
        check(new MechanismTransform(0, 0, 0, 0, 0, 0, 0.049, 0, 0).isAligned(), "0.049 aligned");
        check(!new MechanismTransform(0, 0, 0, 0, 0, 0, 0.051, 0, 0).isAligned(), "0.051 not aligned");
        check(!new MechanismTransform(0, 0, 0, 0.6, 0, 0, 0, 0, 0).isAligned(), "0.6 deg not aligned");
        check(new MechanismTransform(0, 0, 0, 0.4, 0, 0, 0, 0, 0).isAligned(), "0.4 deg aligned");
    }

    static void testEncodeRoundTrip() {
        final MechanismTransform t = new MechanismTransform(1.5, 2, 3, 45, -10, 5, 0.25, 1, -2);
        check(MechanismTransform.decode(t.encode()).equals(t), "round trip");
        check(MechanismTransform.decode("garbage").isIdentity(), "garbage identity");
    }

    static Path tempDir() throws Exception {
        return Files.createTempDirectory("rte-store-");
    }

    static void testStoreRoundTrip() throws Exception {
        final Path dir = tempDir();
        final RailScriptStore store = new RailScriptStore(dir.resolve("rail_scripts"));
        for (final RailScriptStore.Part part : RailScriptStore.Part.values()) {
            store.write("turntable", part, "text " + part);
            check(store.read("turntable", part).orElse("").equals("text " + part), "read " + part);
        }
    }

    static void testRejectsTraversal() throws Exception {
        final Path dir = tempDir();
        final RailScriptStore store = new RailScriptStore(dir.resolve("rail_scripts"));
        for (final String bad : new String[]{"../x", "A", "a/b", "a".repeat(65), "", null}) {
            check(!RailScriptStore.isValidId(bad), "invalid " + bad);
            boolean thrown = false;
            try {
                store.write(bad, RailScriptStore.Part.SERVER, "x");
            } catch (IllegalArgumentException e) {
                thrown = true;
            }
            check(thrown, "write rejected " + bad);
        }
        check(!Files.exists(dir.resolve("x.server.js")), "nothing outside");
    }

    static void testRejectsOversize() throws Exception {
        final RailScriptStore store = new RailScriptStore(tempDir().resolve("rail_scripts"));
        boolean thrown = false;
        try {
            store.write("big", RailScriptStore.Part.SERVER, "a".repeat(RailScriptStore.MAX_BYTES + 1));
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        check(thrown, "oversize rejected");
    }

    static void testListSorted() throws Exception {
        final RailScriptStore store = new RailScriptStore(tempDir().resolve("rail_scripts"));
        store.write("zeta", RailScriptStore.Part.SERVER, "");
        store.write("alpha", RailScriptStore.Part.CLIENT, "");
        store.write("meta_only", RailScriptStore.Part.META, "{}");
        check(store.list().equals(java.util.List.of("alpha", "zeta")), "list " + store.list());
    }

    static MechanismRegistry sampleRegistry() {
        final MechanismRegistry registry = new MechanismRegistry();
        registry.rebuild(java.util.List.of(
            new MechanismRegistry.RailInfo("b", "tt", "turntable", new RailPose(0, 0, -5, 0, 0, 5)),
            new MechanismRegistry.RailInfo("a", "tt", "", new RailPose(-5, 0, 0, 5, 0, 0)),
            new MechanismRegistry.RailInfo("c", "", "turntable", new RailPose(0, 0, 0, 1, 0, 0)),
            new MechanismRegistry.RailInfo("d", "bridge", "drawbridge", new RailPose(0, 0, 0, 8, 0, 0))));
        return registry;
    }

    static void testRegistryGroupsRails() {
        final MechanismRegistry registry = sampleRegistry();
        check(registry.all().size() == 2, "two mechanisms");
        final Mechanism tt = registry.get("tt");
        check(tt.candidateIds().equals(java.util.List.of("a", "b")), "sorted candidates " + tt.candidateIds());
        check(tt.scriptId.equals("turntable"), "script id");
        check(tt.activeRailId.equals("a"), "first active");
        check(registry.byRail("b") == tt, "by rail");
        check(registry.byRail("c") == null, "ungrouped ignored");
    }

    static void testRestoresFinishedAnimation() {
        final MechanismRegistry registry = sampleRegistry();
        final Mechanism tt = registry.get("tt");
        final MechanismTransform goal = MechanismTransform.between(tt.candidates.get("a"), tt.candidates.get("b"));
        tt.targetRailId = "b";
        tt.animate(goal, 1000, 5000, true);
        check(tt.isMoving(2000), "moving mid way");
        final MechanismRegistry restored = new MechanismRegistry();
        restored.loadJson(registry.toJson());
        restored.rebuild(java.util.List.of(
            new MechanismRegistry.RailInfo("a", "tt", "turntable", new RailPose(-5, 0, 0, 5, 0, 0)),
            new MechanismRegistry.RailInfo("b", "tt", "", new RailPose(0, 0, -5, 0, 0, 5))));
        final Mechanism back = restored.get("tt");
        check(!back.isMoving(2000), "not moving after restore");
        check(back.sample(2000).equals(goal), "at goal");
        check(back.activeRailId.equals("b"), "active is target");
        check(back.deckRailId.equals("a"), "deck kept");
    }

    static void testStateJsonPreserved() {
        final MechanismRegistry registry = sampleRegistry();
        registry.get("bridge").stateJson = "{\"open\":true}";
        registry.get("bridge").locked = true;
        final MechanismRegistry restored = new MechanismRegistry();
        restored.loadJson(registry.toJson());
        check(restored.get("bridge").stateJson.equals("{\"open\":true}"), "state");
        check(restored.get("bridge").locked, "locked");
    }

    static void testMatrixColumnsMatchApply() {
        final MechanismTransform t = new MechanismTransform(3, 64, -7, 37, 12, -5, 1.5, 0.25, -2);
        final double[] m = t.matrixColumns();
        final double[][] points = {{0, 0, 0}, {3, 64, -7}, {10.5, 70, 2}};
        for (final double[] p : points) {
            final double[] expected = t.apply(p[0], p[1], p[2]);
            for (int row = 0; row < 3; row++) {
                final double actual = m[row] * p[0] + m[3 + row] * p[1] + m[6 + row] * p[2] + m[9 + row];
                near(actual, expected[row], 1e-9, "row " + row);
            }
        }
    }

    static void testGateRules() {
        final Mechanism tt = sampleRegistry().get("tt");
        check(!MechanismGate.blocks(tt, "a", false, 0), "active aligned passes");
        check(MechanismGate.blocks(tt, "b", false, 0), "inactive blocks");
        check(!MechanismGate.blocks(tt, "c", false, 0), "foreign rail ignored");
        tt.locked = true;
        check(MechanismGate.blocks(tt, "a", false, 0), "locked blocks");
        check(!MechanismGate.blocks(tt, "a", true, 0), "train on deck not blocked");
        tt.locked = false;
        tt.animate(new MechanismTransform(0, 0, 0, 90, 0, 0, 0, 0, 0), 1000, 2000, false);
        check(MechanismGate.blocks(tt, "a", false, 1500), "moving blocks");
        check(MechanismGate.blocks(tt, "a", false, 5000), "misaligned blocks");
        check(MechanismGate.state(tt, 1500).equals("moving"), "state moving");
        check(MechanismGate.state(tt, 5000).equals("misaligned"), "state misaligned");
    }

    static void testRefusesWhileCrossing() {
        check(MechanismGate.decideRotate(true, true).equals("train_crossing"), "crossing");
        check(MechanismGate.decideRotate(false, false).equals("unknown_rail"), "unknown");
        check(MechanismGate.decideRotate(false, true).equals("ok"), "ok");
    }

    static void testRateLimiter() {
        final MessageRateLimiter limiter = new MessageRateLimiter(5, 1000);
        for (int i = 0; i < 5; i++) {
            check(limiter.tryAcquire(7, 100 + i), "allowed " + i);
        }
        check(!limiter.tryAcquire(7, 500), "sixth denied");
        check(limiter.tryAcquire(8, 500), "other key allowed");
        check(limiter.tryAcquire(7, 1101), "after window");
    }

    static void testProximityEdges() {
        final TrainProximityTracker tracker = new TrainProximityTracker();
        java.util.List<MechanismEvent> events = tracker.update(java.util.List.of(new TrainProximityTracker.TrainSample(1, "a", 200, false, false, false)), 300);
        check(events.size() == 1 && events.get(0).type() == MechanismEvent.Type.APPROACH, "approach " + events);
        events = tracker.update(java.util.List.of(new TrainProximityTracker.TrainSample(1, "a", 150, false, false, false)), 300);
        check(events.isEmpty(), "approach once " + events);
        events = tracker.update(java.util.List.of(new TrainProximityTracker.TrainSample(1, "a", 0, true, false, false)), 300);
        check(events.size() == 1 && events.get(0).type() == MechanismEvent.Type.ENTER, "enter " + events);
        events = tracker.update(java.util.List.of(new TrainProximityTracker.TrainSample(1, "a", 0, true, true, true)), 300);
        check(events.size() == 1 && events.get(0).type() == MechanismEvent.Type.STOP, "stop " + events);
        events = tracker.update(java.util.List.of(new TrainProximityTracker.TrainSample(1, "a", 0, true, true, true)), 300);
        check(events.isEmpty(), "stop once " + events);
        check(tracker.trainsOn().contains(1L), "on");
        events = tracker.update(java.util.List.of(new TrainProximityTracker.TrainSample(1, "a", -1, false, false, false)), 300);
        check(events.size() == 1 && events.get(0).type() == MechanismEvent.Type.LEAVE, "leave " + events);
        events = tracker.update(java.util.List.of(new TrainProximityTracker.TrainSample(2, "b", 0, true, false, false)), 300);
        check(events.size() == 1 && events.get(0).type() == MechanismEvent.Type.ENTER, "other enter " + events);
        events = tracker.update(java.util.List.of(), 300);
        check(events.size() == 1 && events.get(0).type() == MechanismEvent.Type.LEAVE && events.get(0).trainId() == 2, "vanished leaves " + events);
    }

    static void testPitchFollowsHeading() {
        final MechanismTransform t = new MechanismTransform(0, 0, 0, 0, 90, 0, 0, 0, 0, 90);
        final double[] p = t.apply(0, 0, 10);
        near(p[0], 0, 1e-9, "x");
        near(p[1], 10, 1e-9, "lifted");
        near(p[2], 0, 1e-9, "z");
        final MechanismTransform flat = new MechanismTransform(0, 0, 0, 30, 0, 0, 0, 0, 0, 90);
        final MechanismTransform plain = new MechanismTransform(0, 0, 0, 30, 0, 0, 0, 0, 0);
        final double[] a = flat.apply(3, 1, 4);
        final double[] b = plain.apply(3, 1, 4);
        near(a[0], b[0], 1e-9, "heading ignored without tilt x");
        near(a[2], b[2], 1e-9, "heading ignored without tilt z");
    }

    static void testSaveRejectedForNonOp() {
        check(RailScriptPolicy.decideSave(false, false, "turntable", "x").equals("denied"), "non op denied");
        check(RailScriptPolicy.decideSave(false, true, "turntable", "x").equals("ok"), "singleplayer ok");
        check(RailScriptPolicy.decideSave(true, false, "../x", "x").equals("bad_id"), "bad id");
        check(RailScriptPolicy.decideSave(true, false, "big", "a".repeat(262145)).equals("too_large"), "too large");
    }

    static void testAssignRules() {
        check(RailScriptPolicy.decideAssign(true, false, false, "tt", "turntable").equals("ok"), "op anywhere");
        check(RailScriptPolicy.decideAssign(false, true, true, "tt", "turntable").equals("ok"), "creative nearby");
        check(RailScriptPolicy.decideAssign(false, true, false, "tt", "turntable").equals("denied"), "creative far");
        check(RailScriptPolicy.decideAssign(false, false, true, "tt", "turntable").equals("denied"), "survival denied");
        check(RailScriptPolicy.decideAssign(true, false, false, "tt", "Bad Id").equals("bad_id"), "bad script id");
        check(RailScriptPolicy.decideAssign(true, false, false, "", "").equals("ok"), "clear allowed");
    }
}
