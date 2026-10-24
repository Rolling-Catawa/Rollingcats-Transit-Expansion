package cn.jstxjf_.rte;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TestBlock extends Block {
    public TestBlock() {
        super(BlockBehaviour.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.STONE));
    }
}
