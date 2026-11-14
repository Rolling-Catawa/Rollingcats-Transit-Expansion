package cn.jstxjf_.rte.compat;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.mtr.mapping.holder.BlockState;

public final class MtrNodes {

    //#if MC>=12101
    public static final BooleanProperty IS_CONNECTED = org.mtr.block.BlockNode.IS_CONNECTED;
    //#else
    public static final BooleanProperty IS_CONNECTED = org.mtr.mod.block.BlockNode.IS_CONNECTED.data;
    //#endif

    private MtrNodes() {
    }

    public static float getAngle(BlockState state) {
        //#if MC>=12101
        return org.mtr.block.BlockNode.getAngle(state.data);
        //#else
        return org.mtr.mod.block.BlockNode.getAngle(state);
        //#endif
    }
}
