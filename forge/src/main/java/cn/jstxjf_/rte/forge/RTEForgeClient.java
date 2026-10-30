package cn.jstxjf_.rte.forge;

import cn.jstxjf_.rte.KeyMappings;
import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.block.RTEBlockEntityTypes;
import cn.jstxjf_.rte.RTECreativeModeEntries;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.client.gui.RTEConfigScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RTE.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RTEForgeClient {

    private RTEForgeClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> RTEConfigScreen.createScreen(parent))
            );
            RTEClient.init();
        });
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyMappings.RAIL_EDIT_KEY);
        event.register(KeyMappings.DEBUG_KEY);
        event.register(KeyMappings.TRAIN_SCREEN);
        event.register(KeyMappings.CONFIG_SCREEN);
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (CreativeModeTabs.TOOLS_AND_UTILITIES.equals(event.getTabKey())) {
            RTECreativeModeEntries.getToolsAndUtilitiesItems().forEach(event::accept);
            return;
        }
        try {
            final Object holder = Class.forName("cn.jstxjf_.rte.RTECreativeModeTabs").getField("EYE_CANDY").get(null);
            final Object identifier = holder.getClass().getField("identifier").get(holder);
            if (identifier != null && identifier.equals(event.getTabKey().location())) {
                final java.util.List<net.minecraft.world.item.ItemStack> stacks = new java.util.ArrayList<>();
                Class.forName("cn.jstxjf_.rte.item.BlockItemEyeCandy$Client")
                    .getMethod("fillItemCategory", java.util.List.class)
                    .invoke(null, stacks);
                stacks.forEach(event::accept);
            }
        } catch (Exception e) {
            RTE.LOGGER.error("Failed filling RTE eye candy creative tab", e);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        final BlockEntityType directNode = getBlockEntityType("DIRECT_NODE");
        final BlockEntityType freeNode = getBlockEntityType("FREE_NODE");
        final BlockEntityType eyeCandy = getBlockEntityType("EYE_CANDY");
        if (directNode != null) {
            event.registerBlockEntityRenderer(directNode, ctx -> createRenderer("cn.jstxjf_.rte.render.block.BlockEntityDirectNodeRenderer", ctx));
        }
        if (freeNode != null) {
            event.registerBlockEntityRenderer(freeNode, ctx -> createRenderer("cn.jstxjf_.rte.render.block.BlockEntityDirectNodeRenderer", ctx));
        }
        if (eyeCandy != null) {
            event.registerBlockEntityRenderer(eyeCandy, ctx -> createRenderer("cn.jstxjf_.rte.render.block.BlockEntityEyeCandyRenderer", ctx));
        }
    }

    private static BlockEntityType getBlockEntityType(String fieldName) {
        try {
            final Object registrySupplier = RTEBlockEntityTypes.class.getField(fieldName).get(null);
            final Object registryObject = registrySupplier.getClass().getMethod("get").invoke(registrySupplier);
            return (BlockEntityType) registryObject.getClass().getField("data").get(registryObject);
        } catch (Exception e) {
            RTE.LOGGER.error("Failed getting Forge block entity type {}", fieldName, e);
            return null;
        }
    }

    private static BlockEntityRenderer createRenderer(String className, Object context) {
        try {
            final Class<?> argumentClass = Class.forName("org.mtr.mapping.mapper.BlockEntityRenderer$Argument");
            Object argument = null;
            for (final java.lang.reflect.Constructor<?> constructor : argumentClass.getConstructors()) {
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isInstance(context)) {
                    argument = constructor.newInstance(context);
                    break;
                }
            }
            if (argument == null) {
                argument = argumentClass.getConstructors()[0].newInstance(context);
            }
            return (BlockEntityRenderer) Class.forName(className).getConstructor(argumentClass).newInstance(argument);
        } catch (Exception e) {
            RTE.LOGGER.error("Failed creating Forge block entity renderer {}", className, e);
            return new BlockEntityRenderer<>() {
                @Override
                public void render(BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                }
            };
        }
    }

    @Mod.EventBusSubscriber(modid = RTE.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeTickEvents {

        private ForgeTickEvents() {
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                cn.jstxjf_.rte.util.TrainCollisionManager.clientTick();
            } else if (event.phase == TickEvent.Phase.END) {
                RTEClient.tick();
                cn.jstxjf_.rte.gui.TrainScreen.tick();
            }
        }

    }
}
