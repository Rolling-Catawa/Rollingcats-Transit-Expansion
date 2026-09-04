package cn.jstxjf_.rte;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class RTECreativeModeEntries {

    private static final String[] TOOLS_AND_UTILITIES_ITEM_IDS = {
        "direct_node",
        "bridge_creator_1",
        "bridge_creator_2",
        "bridge_creator_3",
        "compound_creator",
        "displacement_tool",
        "rail_path_editor",
        "route_path_creator",
        "eye_candy",
        "statistic_turnstile"
    };

    private RTECreativeModeEntries() {
    }

    public static List<Item> getToolsAndUtilitiesItems() {
        final List<Item> items = new ArrayList<>(TOOLS_AND_UTILITIES_ITEM_IDS.length);
        for (final String itemId : TOOLS_AND_UTILITIES_ITEM_IDS) {
            final Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(RTE.MOD_ID, itemId));
            if (item != Items.AIR) {
                items.add(item);
            }
        }
        return items;
    }
}
