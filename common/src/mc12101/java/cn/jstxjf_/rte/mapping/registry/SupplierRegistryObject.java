package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.tool.RegistryObject;
import dev.architectury.registry.registries.RegistrySupplier;

import java.util.function.Consumer;
import java.util.function.Function;

abstract class SupplierRegistryObject<T, V> extends RegistryObject<T> {

	final RegistrySupplier<V> supplier;
	private final Function<V, T> wrap;

	SupplierRegistryObject(RegistrySupplier<V> supplier, Function<V, T> wrap) {
		this.supplier = supplier;
		this.wrap = wrap;
	}

	@Override
	public T get() {
		return wrap.apply(supplier.get());
	}

	@Override
	public boolean isPresent() {
		return supplier.isPresent();
	}

	@Override
	public void ifPresent(Consumer<T> consumer) {
		if (supplier.isPresent()) {
			consumer.accept(get());
		}
	}
}
