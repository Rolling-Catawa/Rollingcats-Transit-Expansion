package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.holder.BlockEntityType;
import cn.jstxjf_.rte.mapping.mapper.BlockEntityExtension;
import dev.architectury.registry.registries.RegistrySupplier;

public final class BlockEntityTypeRegistryObject<T extends BlockEntityExtension> extends SupplierRegistryObject<BlockEntityType<T>, net.minecraft.world.level.block.entity.BlockEntityType<T>> {

	BlockEntityTypeRegistryObject(RegistrySupplier<net.minecraft.world.level.block.entity.BlockEntityType<T>> supplier) {
		super(supplier, BlockEntityType::new);
	}
}
