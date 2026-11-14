package cn.jstxjf_.rte.mixin;

import org.mtr.core.data.Depot;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Route;
import org.mtr.core.data.Siding;
import org.mtr.core.path.SidingPathFinder;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Depot.class, remap = false)
public interface DepotAccessor {

    @Accessor("routes")
    ObjectArrayList<Route> rte$getRoutes();

    @Accessor("path")
    ObjectArrayList<PathData> rte$getPath();

    @Accessor("sidingPathFinders")
    ObjectArrayList<SidingPathFinder> rte$getSidingPathFinders();

    @Accessor("generatingSidingIds")
    LongAVLTreeSet rte$getGeneratingSidingIds();

    @Invoker("updateGenerationStatus")
    void rte$invokeUpdateGenerationStatus(Depot.GeneratedStatus generatedStatus, long failedStartId, long failedEndId, String message);
}
