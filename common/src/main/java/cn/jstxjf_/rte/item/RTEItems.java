package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTECreativeModeTabs;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Item;
import org.mtr.mod.item.ItemBridgeCreator;
import org.mtr.mapping.registry.ItemRegistryObject;

public class RTEItems {

    public static final ItemRegistryObject BRIDGE_CREATOR_1 = RTE.REGISTRY.registerItem(
        new Identifier(RTE.MOD_ID, "bridge_creator_1"),
        settings -> new Item(new ItemBridgeCreator(1, settings)),
        RTECreativeModeTabs.CORE
    );

    public static final ItemRegistryObject BRIDGE_CREATOR_2 = RTE.REGISTRY.registerItem(
        new Identifier(RTE.MOD_ID, "bridge_creator_2"),
        settings -> new Item(new ItemBridgeCreator(2, settings)),
        RTECreativeModeTabs.CORE
    );

    public static final ItemRegistryObject BRIDGE_CREATOR_3 = RTE.REGISTRY.registerItem(
        new Identifier(RTE.MOD_ID, "bridge_creator_3"),
        settings -> new Item(new ItemBridgeCreator(3, settings)),
        RTECreativeModeTabs.CORE
    );

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

    public static void init() {}
}
