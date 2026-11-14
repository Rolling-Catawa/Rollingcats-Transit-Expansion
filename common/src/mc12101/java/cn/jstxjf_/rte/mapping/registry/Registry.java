package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.holder.Block;
import cn.jstxjf_.rte.mapping.holder.BlockPos;
import cn.jstxjf_.rte.mapping.holder.BlockState;
import cn.jstxjf_.rte.mapping.holder.Identifier;
import cn.jstxjf_.rte.mapping.holder.Item;
import cn.jstxjf_.rte.mapping.holder.ItemSettings;
import cn.jstxjf_.rte.mapping.holder.ItemStack;
import cn.jstxjf_.rte.mapping.holder.MinecraftServer;
import cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity;
import cn.jstxjf_.rte.mapping.mapper.BlockEntityExtension;
import cn.jstxjf_.rte.mapping.mapper.BlockItemExtension;
import cn.jstxjf_.rte.mapping.tool.HolderBase;
import cn.jstxjf_.rte.mapping.tool.PacketBufferReceiver;
import cn.jstxjf_.rte.mapping.tool.PacketBufferSender;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry {

	public static final ResourceLocation PACKET_CHANNEL = ResourceLocation.fromNamespaceAndPath("rte", "packet");

	private final DeferredRegister<net.minecraft.world.level.block.Block> blocks = DeferredRegister.create("rte", Registries.BLOCK);
	private final DeferredRegister<net.minecraft.world.item.Item> items = DeferredRegister.create("rte", Registries.ITEM);
	private final DeferredRegister<BlockEntityType<?>> blockEntityTypes = DeferredRegister.create("rte", Registries.BLOCK_ENTITY_TYPE);
	private final DeferredRegister<ParticleType<?>> particleTypes = DeferredRegister.create("rte", Registries.PARTICLE_TYPE);
	private final DeferredRegister<SoundEvent> soundEvents = DeferredRegister.create("rte", Registries.SOUND_EVENT);
	private final DeferredRegister<CreativeModeTab> creativeModeTabs = DeferredRegister.create("rte", Registries.CREATIVE_MODE_TAB);
	private final List<CreativeModeTabHolder> creativeModeTabHolders = new ArrayList<>();
	final Map<String, Function<PacketBufferReceiver, ? extends PacketHandler>> packets = new HashMap<>();
	public final EventRegistry eventRegistry = new EventRegistry();
	private boolean packetsRegistered;

	public void init() {
		creativeModeTabHolders.forEach(holder -> creativeModeTabs.register(holder.identifier, () -> CreativeTabRegistry.create(builder -> builder
			.title(Component.translatable(String.format("itemGroup.%s.%s", holder.identifier.getNamespace(), holder.identifier.getPath())))
			.icon(() -> holder.iconSupplier.get().data)
			.displayItems((parameters, output) -> holder.itemSuppliers.forEach(itemSupplier -> output.accept(itemSupplier.get().data))))));
		blocks.register();
		items.register();
		blockEntityTypes.register();
		particleTypes.register();
		soundEvents.register();
		creativeModeTabs.register();
		setupPackets();
	}

	public BlockRegistryObject registerBlock(Identifier identifier, Supplier<Block> supplier) {
		return new BlockRegistryObject(blocks.register(identifier.data, () -> supplier.get().data));
	}

	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, CreativeModeTabHolder... creativeModeTabHolders) {
		return registerBlockWithBlockItem(identifier, supplier, BlockItemExtension::new, creativeModeTabHolders);
	}

	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, BiFunction<Block, ItemSettings, BlockItemExtension> function, CreativeModeTabHolder... creativeModeTabHolders) {
		final BlockRegistryObject blockRegistryObject = registerBlock(identifier, supplier);
		final RegistrySupplier<net.minecraft.world.item.Item> item = items.register(identifier.data, () -> function.apply(blockRegistryObject.get(), new ItemSettings()));
		final ItemRegistryObject itemRegistryObject = new ItemRegistryObject(item);
		for (final CreativeModeTabHolder creativeModeTabHolder : creativeModeTabHolders) {
			creativeModeTabHolder.itemSuppliers.add(itemRegistryObject::get);
		}
		return blockRegistryObject;
	}

	public ItemRegistryObject registerItem(Identifier identifier, Function<ItemSettings, Item> function, CreativeModeTabHolder... creativeModeTabHolders) {
		final ItemRegistryObject itemRegistryObject = new ItemRegistryObject(items.register(identifier.data, () -> function.apply(new ItemSettings()).data));
		for (final CreativeModeTabHolder creativeModeTabHolder : creativeModeTabHolders) {
			creativeModeTabHolder.itemSuppliers.add(itemRegistryObject::get);
		}
		return itemRegistryObject;
	}

	@SafeVarargs
	public final <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(Identifier identifier, BiFunction<BlockPos, BlockState, T> function, Supplier<Block>... blockSuppliers) {
		return new BlockEntityTypeRegistryObject<>(blockEntityTypes.register(identifier.data, () -> BlockEntityType.Builder.<T>of((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blockSuppliers, net.minecraft.world.level.block.Block[]::new)).build(null)));
	}

	public ParticleTypeRegistryObject registerParticleType(Identifier identifier) {
		return registerParticleType(identifier, false);
	}

	public ParticleTypeRegistryObject registerParticleType(Identifier identifier, boolean alwaysSpawn) {
		return new ParticleTypeRegistryObject(particleTypes.register(identifier.data, () -> new SimpleParticleType(alwaysSpawn) {
		}));
	}

	public CreativeModeTabHolder createCreativeModeTabHolder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
		final CreativeModeTabHolder creativeModeTabHolder = new CreativeModeTabHolder(identifier.data, iconSupplier);
		creativeModeTabHolders.add(creativeModeTabHolder);
		return creativeModeTabHolder;
	}

	public SoundEventRegistryObject registerSoundEvent(Identifier identifier) {
		return new SoundEventRegistryObject(soundEvents.register(identifier.data, () -> SoundEvent.createVariableRangeEvent(identifier.data)));
	}

	public void registerCommand(String command, Consumer<CommandBuilder<?>> buildCommand, String... redirects) {
		CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, selection) -> {
			final CommandBuilder<LiteralArgumentBuilder<CommandSourceStack>> commandBuilder = new CommandBuilder<>(Commands.literal(command));
			buildCommand.accept(commandBuilder);
			final LiteralCommandNode<CommandSourceStack> literalCommandNode = dispatcher.register(commandBuilder.argumentBuilder);
			for (final String redirect : redirects) {
				dispatcher.register(Commands.literal(redirect).redirect(literalCommandNode));
			}
		});
	}

	private void setupPackets() {
		if (packetsRegistered) {
			return;
		}
		packetsRegistered = true;
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, PACKET_CHANNEL, (buffer, context) -> receive(buffer, context, false));
		if (Platform.getEnvironment() == Env.CLIENT) {
			NetworkManager.registerReceiver(NetworkManager.Side.S2C, PACKET_CHANNEL, (buffer, context) -> receive(buffer, context, true));
		} else {
			NetworkManager.registerS2CPayloadType(PACKET_CHANNEL);
		}
	}

	private void receive(RegistryFriendlyByteBuf buffer, NetworkManager.PacketContext context, boolean client) {
		final ByteBuf copy = Unpooled.copiedBuffer(buffer);
		PacketBufferReceiver.receive(copy, packetBufferReceiver -> {
			final Function<PacketBufferReceiver, ? extends PacketHandler> getPacketInstance = packets.get(packetBufferReceiver.readString());
			if (getPacketInstance != null) {
				final PacketHandler packetHandler = getPacketInstance.apply(packetBufferReceiver);
				if (client) {
					packetHandler.runClient();
				} else if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
					packetHandler.runServer(new MinecraftServer(serverPlayer.server), new ServerPlayerEntity(serverPlayer));
				}
			}
		}, context::queue);
	}

	public <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBufferReceiver, T> getInstance) {
		packets.put(classObject.getName(), getInstance);
	}

	public <T extends PacketHandler> void sendPacketToClient(ServerPlayerEntity serverPlayerEntity, T data) {
		final PacketBufferSender packetBufferSender = new PacketBufferSender(Unpooled::buffer);
		packetBufferSender.writeString(data.getClass().getName());
		data.write(packetBufferSender);
		final ServerPlayer player = serverPlayerEntity.data;
		packetBufferSender.send(byteBuf -> NetworkManager.sendToPlayer(player, PACKET_CHANNEL, new RegistryFriendlyByteBuf(byteBuf, player.registryAccess())), player.server::execute);
	}
}
