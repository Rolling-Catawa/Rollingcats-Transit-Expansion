package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.holder.SoundEvent;
import dev.architectury.registry.registries.RegistrySupplier;

public final class SoundEventRegistryObject extends SupplierRegistryObject<SoundEvent, net.minecraft.sounds.SoundEvent> {

	SoundEventRegistryObject(RegistrySupplier<net.minecraft.sounds.SoundEvent> supplier) {
		super(supplier, SoundEvent::new);
	}
}
