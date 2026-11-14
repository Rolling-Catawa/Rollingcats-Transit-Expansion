package cn.jstxjf_.rte;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TestBlock extends Block {
    public TestBlock() {
        super(cn.jstxjf_.rte.compat.McCompat.stoneProperties());
    }
}
