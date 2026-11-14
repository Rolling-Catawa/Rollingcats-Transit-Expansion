package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.holder.Block;
import dev.architectury.registry.registries.RegistrySupplier;

public final class BlockRegistryObject extends SupplierRegistryObject<Block, net.minecraft.world.level.block.Block> {

	BlockRegistryObject(RegistrySupplier<net.minecraft.world.level.block.Block> supplier) {
		super(supplier, Block::new);
	}
}
