package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.mixin.VehicleResourceLegacyAccessor;
import org.mtr.mod.resource.VehicleResource;

import java.util.ArrayDeque;
import java.util.Collection;

public final class VehicleWarmup {

    private static final int VEHICLES_PER_TICK = 2;
    private static final int MAX_ATTEMPTS = 1200;
    private static final ArrayDeque<Entry> PENDING = new ArrayDeque<>();
    private static int completed;

    private VehicleWarmup() {
    }

    public static void schedule(Collection<VehicleResource> vehicleResources) {
        PENDING.clear();
        completed = 0;
        for (final VehicleResource vehicleResource : vehicleResources) {
            if (vehicleResource.shouldPreload && ((VehicleResourceLegacyAccessor) (Object) vehicleResource).rte$getExtraModelsSupplier() == null) {
                PENDING.addLast(new Entry(vehicleResource));
            }
        }
        if (!PENDING.isEmpty()) {
            RTE.LOGGER.info("[Warmup] {} preloaded vehicles queued for background build", PENDING.size());
        }
    }

    public static void clear() {
        PENDING.clear();
    }

    public static void tick() {
        final int rounds = Math.min(VEHICLES_PER_TICK, PENDING.size());
        for (int i = 0; i < rounds; i++) {
            final Entry entry = PENDING.pollFirst();
            if (entry == null) {
                return;
            }
            final boolean ready;
            try {
                ready = entry.vehicleResource.getCachedVehicleResource(0, 0, true) != null;
            } catch (Throwable e) {
                RTE.LOGGER.warn("[Warmup] dropping {}: {}", entry.vehicleResource.getId(), e.toString());
                continue;
            }
            if (ready) {
                completed++;
                if (PENDING.isEmpty()) {
                    RTE.LOGGER.info("[Warmup] finished, {} vehicles ready", completed);
                }
            } else if (++entry.attempts < MAX_ATTEMPTS) {
                PENDING.addLast(entry);
            }
        }
    }

    private static final class Entry {
        private final VehicleResource vehicleResource;
        private int attempts;

        private Entry(VehicleResource vehicleResource) {
            this.vehicleResource = vehicleResource;
        }
    }
}
