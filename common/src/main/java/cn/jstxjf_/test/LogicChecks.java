package cn.jstxjf_.test;

import cn.jstxjf_.rte.item.CompoundCreatorLogic;
import cn.jstxjf_.rte.scripting.ScriptUseBox;

import java.util.ArrayList;
import java.util.List;

public final class LogicChecks {

    private LogicChecks() {
    }

    public static void run(List<TestResult> out) {
        sliceSegmentCycle(out);
        sliceSerialisationRoundTrip(out);
        namedBlockStateRoundTrip(out);
        useBoxIntersection(out);
        engineOverrideResolution(out);
    }

    private static void sliceSegmentCycle(List<TestResult> out) {
        final CompoundCreatorLogic.SliceTask task = new CompoundCreatorLogic.SliceTask();
        task.segments.add(new CompoundCreatorLogic.Segment(lumpsOf(task, "A"), 3));
        task.segments.add(new CompoundCreatorLogic.Segment(lumpsOf(task, "B"), 5));

        final double cycle = task.segmentCycleLength();
        out.add(TestResult.check("slice/cycle-length", cycle == 8,
            "expected 8, got " + cycle));

        final boolean ok = task.lumpsAt(0) == task.segments.get(0).lumps
            && task.lumpsAt(2.9) == task.segments.get(0).lumps
            && task.lumpsAt(3) == task.segments.get(1).lumps
            && task.lumpsAt(7.9) == task.segments.get(1).lumps
            && task.lumpsAt(8) == task.segments.get(0).lumps
            && task.lumpsAt(-1) == task.segments.get(1).lumps;
        out.add(TestResult.check("slice/segment-lookup", ok,
            ok ? "0..3 -> A, 3..8 -> B, wraps and handles negatives"
               : "segment boundaries or wrap-around are wrong"));
    }

    private static List<CompoundCreatorLogic.Lump> lumpsOf(CompoundCreatorLogic.SliceTask task, String tag) {
        final List<CompoundCreatorLogic.Lump> lumps = new ArrayList<>();
        for (int i = 0; i < task.width * task.height; i++) {
            lumps.add(new CompoundCreatorLogic.Lump(null, true));
        }
        return lumps;
    }

    private static void sliceSerialisationRoundTrip(List<TestResult> out) {
        try {
            final CompoundCreatorLogic.SliceTask task = new CompoundCreatorLogic.SliceTask();
            task.name = "selftest";
            task.increment = 0.5;
            task.pierInterval = 8;
            task.pierDepth = 20;
            task.segments.add(new CompoundCreatorLogic.Segment(lumpsOf(task, "A"), 3));
            task.segments.add(new CompoundCreatorLogic.Segment(lumpsOf(task, "B"), 5));

            final CompoundCreatorLogic.SliceTask restored =
                new CompoundCreatorLogic.SliceTask(task.toCompoundTag());

            final boolean ok = restored.segments.size() == 2
                && restored.segments.get(0).length == 3
                && restored.segments.get(1).length == 5
                && restored.pierInterval == 8
                && restored.pierDepth == 20;
            out.add(TestResult.check("slice/nbt-round-trip", ok,
                ok ? "segments, pier interval and depth survive save/load"
                   : "restored " + restored.segments.size() + " segments, pier "
                     + restored.pierInterval + "/" + restored.pierDepth));
        } catch (Throwable e) {
            out.add(TestResult.fail("slice/nbt-round-trip", e.toString()));
        }
    }

    private static void namedBlockStateRoundTrip(List<TestResult> out) {
        try {
            final org.mtr.mapping.holder.BlockState state = new org.mtr.mapping.holder.BlockState(
                net.minecraft.world.level.block.Blocks.OAK_STAIRS.defaultBlockState()
                    .setValue(net.minecraft.world.level.block.StairBlock.FACING,
                        net.minecraft.core.Direction.EAST)
                    .setValue(net.minecraft.world.level.block.StairBlock.WATERLOGGED, true));

            final String described = CompoundCreatorLogic.Lump.describeState(state);
            final org.mtr.mapping.holder.BlockState parsed = CompoundCreatorLogic.Lump.parseState(described);

            final boolean ok = parsed != null && parsed.data == state.data;
            out.add(TestResult.check("slice/block-name-round-trip", ok,
                ok ? described : "described as " + described + ", parsed back differently"));
        } catch (Throwable e) {
            out.add(TestResult.fail("slice/block-name-round-trip", e.toString()));
        }
    }

    private static void useBoxIntersection(List<TestResult> out) {
        final ScriptUseBox box = new ScriptUseBox("probe", 0, -1, 0, -1, 1, 2, 1);

        final double hit = box.intersect(0, 1, -5, 0, 0, 1, 10);
        out.add(TestResult.check("use-box/head-on", hit >= 3.9 && hit <= 4.1,
            "ray from z=-5 toward +z should enter at 4.0, got " + round(hit)));

        final double miss = box.intersect(0, 5, -5, 0, 0, 1, 10);
        out.add(TestResult.check("use-box/miss-above", miss < 0,
            "ray above the box must miss, got " + round(miss)));

        final double short_ = box.intersect(0, 1, -5, 0, 0, 1, 2);
        out.add(TestResult.check("use-box/range-limit", short_ < 0,
            "hit beyond max distance must be rejected, got " + round(short_)));

        final ScriptUseBox swapped = new ScriptUseBox("swapped", 0, 1, 2, 1, -1, 0, -1);
        out.add(TestResult.check("use-box/corner-order",
            swapped.minX == -1 && swapped.maxY == 2,
            "corners given in reverse order must normalise"));
    }

    private static void engineOverrideResolution(List<TestResult> out) {
        final java.util.Map<String, String> saved =
            cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy.snapshot();
        try {
            final java.util.Map<String, String> overrides = new java.util.LinkedHashMap<>();
            overrides.put("train_alpha", "graal");
            overrides.put("wh_*", "graal");
            overrides.put("wh_special_*", "rhino");
            cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy.restore(overrides);

            final var exact = cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy
                .resolve("train_alpha", "rhino", "");
            final var wildcard = cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy
                .resolve("wh_05a02", "rhino", "");
            final var longest = cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy
                .resolve("wh_special_1", "rhino", "");
            final var fallback = cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy
                .resolve("unrelated", "rhino", "");

            final boolean ok =
                exact == cn.jstxjf_.rte.scripting.engine.ScriptEngineType.GRAAL
                && wildcard == cn.jstxjf_.rte.scripting.engine.ScriptEngineType.GRAAL
                && longest == cn.jstxjf_.rte.scripting.engine.ScriptEngineType.RHINO
                && fallback == cn.jstxjf_.rte.scripting.engine.ScriptEngineType.RHINO;
            out.add(TestResult.check("engine/override-precedence", ok,
                ok ? "exact > longest wildcard > global"
                   : "exact=" + exact + " wildcard=" + wildcard
                     + " longest=" + longest + " fallback=" + fallback));
        } catch (Throwable e) {
            out.add(TestResult.fail("engine/override-precedence", e.toString()));
        } finally {
            cn.jstxjf_.rte.scripting.engine.ScriptEnginePolicy.restore(saved);
        }
    }

    private static String round(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }
}
