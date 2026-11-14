package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.holder.MinecraftServer;
import cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity;
import cn.jstxjf_.rte.mapping.holder.ServerWorld;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventRegistry {

	EventRegistry() {
	}

	public void registerServerStarting(Consumer<MinecraftServer> consumer) {
		LifecycleEvent.SERVER_STARTING.register(server -> consumer.accept(new MinecraftServer(server)));
	}

	public void registerServerStarted(Consumer<MinecraftServer> consumer) {
		LifecycleEvent.SERVER_STARTED.register(server -> consumer.accept(new MinecraftServer(server)));
	}

	public void registerServerStopping(Consumer<MinecraftServer> consumer) {
		LifecycleEvent.SERVER_STOPPING.register(server -> consumer.accept(new MinecraftServer(server)));
	}

	public void registerServerStopped(Consumer<MinecraftServer> consumer) {
		LifecycleEvent.SERVER_STOPPED.register(server -> consumer.accept(new MinecraftServer(server)));
	}

	public void registerStartServerTick(Runnable runnable) {
		TickEvent.SERVER_PRE.register(server -> runnable.run());
	}

	public void registerEndServerTick(Runnable runnable) {
		TickEvent.SERVER_POST.register(server -> runnable.run());
	}

	public void registerStartWorldTick(Consumer<ServerWorld> consumer) {
		TickEvent.SERVER_LEVEL_PRE.register(level -> consumer.accept(new ServerWorld(level)));
	}

	public void registerEndWorldTick(Consumer<ServerWorld> consumer) {
		TickEvent.SERVER_LEVEL_POST.register(level -> consumer.accept(new ServerWorld(level)));
	}

	public void registerPlayerJoin(BiConsumer<MinecraftServer, ServerPlayerEntity> consumer) {
		PlayerEvent.PLAYER_JOIN.register(player -> consumer.accept(new MinecraftServer(player.server), new ServerPlayerEntity(player)));
	}

	public void registerPlayerDisconnect(BiConsumer<MinecraftServer, ServerPlayerEntity> consumer) {
		PlayerEvent.PLAYER_QUIT.register(player -> consumer.accept(new MinecraftServer(player.server), new ServerPlayerEntity(player)));
	}
}
