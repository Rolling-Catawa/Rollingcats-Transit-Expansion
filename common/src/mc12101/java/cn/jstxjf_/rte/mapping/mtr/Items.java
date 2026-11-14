package cn.jstxjf_.rte.mapping.mtr;

import cn.jstxjf_.rte.mapping.holder.Item;

import java.util.function.Supplier;

public final class Items {

	public static final Supplier<Item> BRUSH = () -> new Item(org.mtr.registry.Items.BRUSH.get());

	private Items() {
	}
}
