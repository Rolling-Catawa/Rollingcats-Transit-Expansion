package cn.jstxjf_.rte.fabric.client;

import cn.jstxjf_.rte.KeyMappings;
import cn.jstxjf_.rte.RTECreativeModeEntries;
import cn.jstxjf_.rte.block.RTEBlockEntityTypes;
import cn.jstxjf_.rte.render.block.BlockEntityDirectNodeRenderer;
import cn.jstxjf_.rte.render.block.BlockEntityEyeCandyRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
//#if MC>=11903
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
//#endif

public final class RTEFabricClient implements ClientModInitializer {
    @Override
    @SuppressWarnings("unchecked")
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(KeyMappings.RAIL_EDIT_KEY);
        KeyBindingHelper.registerKeyBinding(KeyMappings.DEBUG_KEY);
        KeyBindingHelper.registerKeyBinding(KeyMappings.TRAIN_SCREEN);
        KeyBindingHelper.registerKeyBinding(KeyMappings.CONFIG_SCREEN);
        ClientTickEvents.START_CLIENT_TICK.register(client -> cn.jstxjf_.rte.util.TrainCollisionManager.clientTick());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            cn.jstxjf_.rte.RTEClient.tick();
            cn.jstxjf_.rte.gui.TrainScreen.tick();
        });
        BlockEntityRendererRegistry.register(
            (net.minecraft.world.level.block.entity.BlockEntityType<cn.jstxjf_.rte.block.BlockDirectNode.BlockEntityDirectNode>)
                RTEBlockEntityTypes.directNodeType(),
            ctx -> new BlockEntityDirectNodeRenderer(new org.mtr.mapping.mapper.BlockEntityRenderer.Argument(ctx))
        );
        BlockEntityRendererRegistry.register(
            (net.minecraft.world.level.block.entity.BlockEntityType) RTEBlockEntityTypes.freeNodeType(),
            ctx -> new BlockEntityDirectNodeRenderer(new org.mtr.mapping.mapper.BlockEntityRenderer.Argument(ctx))
        );
        BlockEntityRendererRegistry.register(
            (net.minecraft.world.level.block.entity.BlockEntityType<cn.jstxjf_.rte.block.BlockEyeCandy.BlockEntityEyeCandy>)
                RTEBlockEntityTypes.eyeCandyType(),
            ctx -> new BlockEntityEyeCandyRenderer(new org.mtr.mapping.mapper.BlockEntityRenderer.Argument(ctx))
        );

        cn.jstxjf_.rte.RTEClient.init();

        //#if MC>=11903
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries ->
            RTECreativeModeEntries.getToolsAndUtilitiesItems().forEach(entries::accept)
        );

        //#if MC>=12101
        java.util.Optional.of(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, cn.jstxjf_.rte.RTECreativeModeTabs.EYE_CANDY.identifier))
        //#else
        net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB
            .getResourceKey(cn.jstxjf_.rte.RTECreativeModeTabs.EYE_CANDY.creativeModeTab)
        //#endif
            .ifPresent(tabKey -> ItemGroupEvents.modifyEntriesEvent(tabKey).register(entries -> {
                final java.util.List<net.minecraft.world.item.ItemStack> stacks = new java.util.ArrayList<>();
                cn.jstxjf_.rte.item.BlockItemEyeCandy.Client.fillItemCategory(stacks);
                stacks.forEach(entries::accept);
            }));
        //#endif
    }
}
