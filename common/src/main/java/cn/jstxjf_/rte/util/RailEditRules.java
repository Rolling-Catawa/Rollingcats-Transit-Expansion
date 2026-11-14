package cn.jstxjf_.rte.util;

public final class RailEditRules {

    public static final double MAX_EDIT_DISTANCE = 128;

    private RailEditRules() {
    }

    public static boolean allowed(boolean op, boolean mayBuild, double distanceSq) {
        return op || (mayBuild && distanceSq <= MAX_EDIT_DISTANCE * MAX_EDIT_DISTANCE);
    }
}
