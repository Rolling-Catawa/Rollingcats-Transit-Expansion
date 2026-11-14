package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.annotation.MappedMethod;
import cn.jstxjf_.rte.mapping.holder.MinecraftServer;
import cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity;
import cn.jstxjf_.rte.mapping.tool.DummyClass;
import cn.jstxjf_.rte.mapping.tool.PacketBufferSender;

public abstract class PacketHandler extends DummyClass {

	@MappedMethod
	public abstract void write(PacketBufferSender packetBufferSender);

	@MappedMethod
	public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
	}

	@MappedMethod
	public void runClient() {
	}
}
