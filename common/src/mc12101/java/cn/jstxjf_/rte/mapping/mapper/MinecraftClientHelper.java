package cn.jstxjf_.rte.mapping.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import cn.jstxjf_.rte.mapping.annotation.MappedMethod;
import cn.jstxjf_.rte.mapping.holder.Entity;
import cn.jstxjf_.rte.mapping.holder.EntityAbstractMapping;
import cn.jstxjf_.rte.mapping.tool.DummyClass;

import java.io.File;
import java.util.function.Consumer;

public final class MinecraftClientHelper extends DummyClass {

	@MappedMethod
	public static int getRenderDistance() {
		return Minecraft.getInstance().options.renderDistance().get();
	}

	@MappedMethod
	public static File getResourcePackDirectory() {
		return Minecraft.getInstance().getResourcePackDirectory().toFile();
	}

	@MappedMethod
	public static void getEntities(Consumer<Entity> consumer) {
		final ClientLevel clientWorld = Minecraft.getInstance().level;
		if (clientWorld != null) {
			clientWorld.entitiesForRendering().forEach(entity -> consumer.accept(new Entity(entity)));
		}
	}

	@MappedMethod
	public static void addEntity(EntityAbstractMapping entity) {
		final ClientLevel clientWorld = Minecraft.getInstance().level;
		if (clientWorld != null) {
			clientWorld.addEntity(entity);
		}
	}
}
