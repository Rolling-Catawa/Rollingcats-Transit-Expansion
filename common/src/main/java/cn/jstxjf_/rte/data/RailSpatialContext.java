package cn.jstxjf_.rte.data;

public final class RailSpatialContext {

    private static final ThreadLocal<java.util.function.Consumer<RailExtraSupplier>> CONFIGURATOR = new ThreadLocal<>();

    private RailSpatialContext() {
    }

    public static void run(java.util.function.Consumer<RailExtraSupplier> configurator, Runnable action) {
        if (configurator == null) {
            action.run();
            return;
        }
        final java.util.function.Consumer<RailExtraSupplier> previous = CONFIGURATOR.get();
        CONFIGURATOR.set(configurator);
        try {
            action.run();
        } finally {
            if (previous == null) CONFIGURATOR.remove();
            else CONFIGURATOR.set(previous);
        }
    }

    public static boolean apply(RailExtraSupplier rail) {
        final java.util.function.Consumer<RailExtraSupplier> configurator = CONFIGURATOR.get();
        if (configurator == null) return false;
        configurator.accept(rail);
        return true;
    }
}
