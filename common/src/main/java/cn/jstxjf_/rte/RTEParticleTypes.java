package cn.jstxjf_.rte;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.ParticleTypeRegistryObject;

public final class RTEParticleTypes {

    public static final ParticleTypeRegistryObject STEAM_SMOKE = RTE.REGISTRY.registerParticleType(new Identifier(RTE.MOD_ID, "steam_smoke"), true);

    private RTEParticleTypes() {
    }

    public static net.minecraft.core.particles.ParticleType<?> steamSmoke() {
        return STEAM_SMOKE.get().data;
    }

    public static void init() {
    }
}
