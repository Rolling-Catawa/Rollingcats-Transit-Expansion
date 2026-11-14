package cn.jstxjf_.rte.mixin;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mod.data.RailAction;
import org.mtr.mod.data.RailActionModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RailActionModule.class, remap = false)
public interface RailActionModuleAccessor {

    @Accessor("railActions")
    ObjectArrayList<RailAction> getRailActions();

    @Invoker("broadcastUpdate")
    void invokeBroadcastUpdate();
}
