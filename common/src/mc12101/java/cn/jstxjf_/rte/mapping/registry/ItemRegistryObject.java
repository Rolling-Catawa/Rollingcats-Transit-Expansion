package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.holder.Item;
import dev.architectury.registry.registries.RegistrySupplier;

public final class ItemRegistryObject extends SupplierRegistryObject<Item, net.minecraft.world.item.Item> {

	ItemRegistryObject(RegistrySupplier<net.minecraft.world.item.Item> supplier) {
		super(supplier, Item::new);
	}
}
