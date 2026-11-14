package cn.jstxjf_.rte.mapping.mtr;

import cn.jstxjf_.rte.mapping.holder.SoundEvent;

import java.util.function.Supplier;

public final class SoundEvents {

	public static final Supplier<SoundEvent> TICKET_BARRIER = () -> new SoundEvent(org.mtr.registry.SoundEvents.TICKET_BARRIER.get());
	public static final Supplier<SoundEvent> TICKET_BARRIER_CONCESSIONARY = () -> new SoundEvent(org.mtr.registry.SoundEvents.TICKET_BARRIER_CONCESSIONARY.get());

	private SoundEvents() {
	}
}
