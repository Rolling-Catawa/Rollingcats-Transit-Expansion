package cn.jstxjf_.rte.data;

import org.mtr.mapping.holder.World;

public interface RailActionsModuleExtraSupplier {
    World getWorld();
    void sendUpdateS2C();
}
