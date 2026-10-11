package cn.jstxjf_.rte.render.train;

public final class AsyncLoadGate {

    private static boolean pendingSeen;

    private AsyncLoadGate() {
    }

    public static void markPending() {
        pendingSeen = true;
    }

    public static void reset() {
        pendingSeen = false;
    }

    public static boolean isPending() {
        return pendingSeen;
    }
}
