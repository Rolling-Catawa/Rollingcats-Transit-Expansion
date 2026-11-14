package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.holder.DefaultParticleType;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.SimpleParticleType;

public final class ParticleTypeRegistryObject extends SupplierRegistryObject<DefaultParticleType, SimpleParticleType> {

	ParticleTypeRegistryObject(RegistrySupplier<SimpleParticleType> supplier) {
		super(supplier, DefaultParticleType::new);
	}
}
