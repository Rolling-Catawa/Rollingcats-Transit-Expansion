package cn.jstxjf_.rte.mapping.mapper;

import cn.jstxjf_.rte.mapping.annotation.MappedMethod;
import cn.jstxjf_.rte.mapping.holder.Identifier;
import cn.jstxjf_.rte.mapping.holder.SoundEvent;

public final class SoundHelper {

	@MappedMethod
	public static SoundEvent createSoundEvent(Identifier identifier) {
		return new SoundEvent(net.minecraft.sounds.SoundEvent.createVariableRangeEvent(identifier.data));
	}
}
