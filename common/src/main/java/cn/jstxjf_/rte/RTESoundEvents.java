package cn.jstxjf_.rte;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.SoundEventRegistryObject;

public final class RTESoundEvents {

    public static final SoundEventRegistryObject BELL =
        RTE.REGISTRY.registerSoundEvent(new Identifier(RTE.MOD_ID, "bell"));

    private RTESoundEvents() {
    }

    public static void init() {
    }
}
