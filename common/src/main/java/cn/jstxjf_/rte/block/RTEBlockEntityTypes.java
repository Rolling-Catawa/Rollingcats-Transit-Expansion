package cn.jstxjf_.rte.block;

import cn.jstxjf_.rte.RTE;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;

public class RTEBlockEntityTypes {
    public static final BlockEntityTypeRegistryObject<BlockDirectNode.BlockEntityDirectNode> DIRECT_NODE = 
        RTE.REGISTRY.registerBlockEntityType(
            new Identifier(RTE.MOD_ID, "direct_node"), 
            BlockDirectNode.BlockEntityDirectNode::new, 
            RTEBlocks.DIRECT_NODE::get
        );

    public static final BlockEntityTypeRegistryObject<BlockFreeNode.BlockEntityFreeNode> FREE_NODE =
        RTE.REGISTRY.registerBlockEntityType(
            new Identifier(RTE.MOD_ID, "free_node"),
            BlockFreeNode.BlockEntityFreeNode::new,
            RTEBlocks.FREE_NODE::get
        );

    public static final BlockEntityTypeRegistryObject<BlockEyeCandy.BlockEntityEyeCandy> EYE_CANDY =
        RTE.REGISTRY.registerBlockEntityType(
            new Identifier(RTE.MOD_ID, "eye_candy"),
            BlockEyeCandy.BlockEntityEyeCandy::new,
            RTEBlocks.EYE_CANDY::get
        );

    public static net.minecraft.world.level.block.entity.BlockEntityType<?> directNodeType() {
        return DIRECT_NODE.get().data;
    }

    public static net.minecraft.world.level.block.entity.BlockEntityType<?> freeNodeType() {
        return FREE_NODE.get().data;
    }

    public static net.minecraft.world.level.block.entity.BlockEntityType<?> eyeCandyType() {
        return EYE_CANDY.get().data;
    }

    public static void init() {}
}
