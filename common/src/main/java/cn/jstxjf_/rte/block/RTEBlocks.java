package cn.jstxjf_.rte.block;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTECreativeModeTabs;
import cn.jstxjf_.rte.item.BlockItemDirectNode;
import cn.jstxjf_.rte.item.BlockItemFreeNode;
import cn.jstxjf_.rte.item.BlockItemEyeCandy;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.block.BlockTicketBarrier;

public class RTEBlocks {
    public static final BlockRegistryObject DIRECT_NODE = RTE.REGISTRY.registerBlockWithBlockItem(
        new Identifier(RTE.MOD_ID, "direct_node"),
        () -> new Block(new BlockDirectNode()),
        BlockItemDirectNode::new,
        RTECreativeModeTabs.CORE
    );

    public static final BlockRegistryObject FREE_NODE = RTE.REGISTRY.registerBlockWithBlockItem(
        new Identifier(RTE.MOD_ID, "free_node"),
        () -> new Block(new BlockFreeNode()),
        BlockItemFreeNode::new,
        RTECreativeModeTabs.CORE
    );

    public static final BlockRegistryObject EYE_CANDY = RTE.REGISTRY.registerBlockWithBlockItem(
        new Identifier(RTE.MOD_ID, "eye_candy"),
        () -> new Block(new BlockEyeCandy()),
        BlockItemEyeCandy::new,
        RTECreativeModeTabs.EYE_CANDY
    );

    public static final BlockRegistryObject STATISTIC_TURNSTILE = RTE.REGISTRY.registerBlockWithBlockItem(
        new Identifier(RTE.MOD_ID, "statistic_turnstile"),
        () -> new Block(new BlockTicketBarrier(true)),
        RTECreativeModeTabs.CORE
    );

    public static void init() {}
}
