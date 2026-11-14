package cn.jstxjf_.rte.mapping.registry;

import cn.jstxjf_.rte.mapping.holder.BlockPos;
import cn.jstxjf_.rte.mapping.holder.BlockRenderView;
import cn.jstxjf_.rte.mapping.holder.BlockState;
import cn.jstxjf_.rte.mapping.holder.ClientWorld;
import cn.jstxjf_.rte.mapping.holder.Identifier;
import cn.jstxjf_.rte.mapping.holder.ItemStack;
import cn.jstxjf_.rte.mapping.holder.KeyBinding;
import cn.jstxjf_.rte.mapping.holder.LivingEntity;
import cn.jstxjf_.rte.mapping.holder.RenderLayer;
import cn.jstxjf_.rte.mapping.holder.SpriteProvider;
import cn.jstxjf_.rte.mapping.holder.BlockColorProvider;
import cn.jstxjf_.rte.mapping.holder.ItemColorProvider;
import cn.jstxjf_.rte.mapping.mapper.BlockEntityExtension;
import cn.jstxjf_.rte.mapping.mapper.BlockEntityRenderer;
import cn.jstxjf_.rte.mapping.mapper.ParticleFactoryExtension;
import cn.jstxjf_.rte.mapping.tool.PacketBufferSender;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class RegistryClient {

	public RegistryClient(Registry registry) {
	}

	public void init() {
	}

	public <T extends BlockEntityTypeRegistryObject<U>, U extends BlockEntityExtension> void registerBlockEntityRenderer(T blockEntityType, Function<BlockEntityRenderer.Argument, BlockEntityRenderer<U>> rendererInstance) {
		BlockEntityRendererRegistry.register(blockEntityType.supplier.get(), context -> rendererInstance.apply(new BlockEntityRenderer.Argument(context)));
	}

	public void registerParticleRenderer(ParticleTypeRegistryObject particleTypeRegistryObject, Function<SpriteProvider, ParticleFactoryExtension> factory) {
		ParticleProviderRegistry.register(particleTypeRegistryObject.supplier, spriteSet -> factory.apply(new SpriteProvider(spriteSet)));
	}

	public void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject block) {
		RenderTypeRegistry.register(renderLayer.data, block.get().data);
	}

	public KeyBinding registerKeyBinding(String translationKey, int key, String categoryKey) {
		final KeyMapping keyBinding = new KeyMapping(translationKey, InputConstants.Type.KEYSYM, key, categoryKey);
		KeyMappingRegistry.register(keyBinding);
		return new KeyBinding(keyBinding);
	}

	public void registerBlockColors(BlockColorProvider blockColorProvider, BlockRegistryObject... blocks) {
		final net.minecraft.world.level.block.Block[] newBlocks = new net.minecraft.world.level.block.Block[blocks.length];
		for (int i = 0; i < blocks.length; i++) {
			newBlocks[i] = blocks[i].get().data;
		}
		ColorHandlerRegistry.registerBlockColors((blockState, blockRenderView, blockPos, tintIndex) -> blockColorProvider.getColor2(new BlockState(blockState), blockRenderView == null ? null : new BlockRenderView(blockRenderView), blockPos == null ? null : new BlockPos(blockPos), tintIndex), newBlocks);
	}

	public void registerItemColors(ItemColorProvider itemColorProvider, ItemRegistryObject... items) {
		final net.minecraft.world.item.Item[] newItems = new net.minecraft.world.item.Item[items.length];
		for (int i = 0; i < items.length; i++) {
			newItems[i] = items[i].get().data;
		}
		ColorHandlerRegistry.registerItemColors((itemStack, tintIndex) -> itemColorProvider.getColor2(new ItemStack(itemStack), tintIndex), newItems);
	}

	public void registerItemModelPredicate(ItemRegistryObject item, Identifier identifier, ModelPredicateProvider modelPredicateProvider) {
		ItemPropertiesRegistry.register(item.supplier.get(), identifier.data, (itemStack, clientWorld, livingEntity, seed) -> modelPredicateProvider.call(new ItemStack(itemStack), clientWorld == null ? null : new ClientWorld(clientWorld), livingEntity == null ? null : new LivingEntity(livingEntity)));
	}

	public void setupPackets(Identifier identifier) {
	}

	public <T extends PacketHandler> void sendPacketToServer(T data) {
		final PacketBufferSender packetBufferSender = new PacketBufferSender(Unpooled::buffer);
		packetBufferSender.writeString(data.getClass().getName());
		data.write(packetBufferSender);
		final Minecraft minecraft = Minecraft.getInstance();
		packetBufferSender.send(byteBuf -> {
			if (minecraft.getConnection() != null) {
				NetworkManager.sendToServer(Registry.PACKET_CHANNEL, new RegistryFriendlyByteBuf(byteBuf, minecraft.getConnection().registryAccess()));
			}
		}, minecraft::execute);
	}

	@FunctionalInterface
	public interface ModelPredicateProvider {
		float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity);
	}
}
