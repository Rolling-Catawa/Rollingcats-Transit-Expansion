package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTECreativeModeTabs;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.registry.ItemRegistryObject;

public class RTEItems {

    public static final ItemRegistryObject COMPOUND_CREATOR = RTE.REGISTRY.registerItem(
        new Identifier(RTE.MOD_ID, "compound_creator"),
        settings -> new Item(new CompoundCreator(settings)),
        RTECreativeModeTabs.CORE
    );

    public static final ItemRegistryObject DISPLACEMENT_TOOL = RTE.REGISTRY.registerItem(
        new Identifier(RTE.MOD_ID, "displacement_tool"),
        settings -> new Item(new DisplacementTool(settings)),
        RTECreativeModeTabs.CORE
    );

    public static final ItemRegistryObject RAIL_PATH_EDITOR = RTE.REGISTRY.registerItem(
        new Identifier(RTE.MOD_ID, "rail_path_editor"),
        settings -> new Item(new RailPathEditor(settings)),
        RTECreativeModeTabs.CORE
    );

    public static final ItemRegistryObject ROUTE_PATH_CREATOR = RTE.REGISTRY.registerItem(
        new Identifier(RTE.MOD_ID, "route_path_creator"),
        settings -> new Item(new RoutePathCreator(settings)),
        RTECreativeModeTabs.CORE
    );

    public static net.minecraft.world.item.Item compoundCreator() {
        return COMPOUND_CREATOR.get().data;
    }

    public static net.minecraft.world.item.Item displacementTool() {
        return DISPLACEMENT_TOOL.get().data;
    }

    public static net.minecraft.world.item.Item railPathEditor() {
        return RAIL_PATH_EDITOR.get().data;
    }

    public static net.minecraft.world.item.Item routePathCreator() {
        return ROUTE_PATH_CREATOR.get().data;
    }

    public static void init() {}
}
