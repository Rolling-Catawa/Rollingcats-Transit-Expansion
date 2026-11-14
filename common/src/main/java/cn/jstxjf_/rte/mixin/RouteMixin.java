package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.IRoute;
import org.mtr.core.data.PathData;
import org.mtr.core.data.RoutePlatformData;
import org.mtr.core.generated.data.RouteSchema;
import org.mtr.core.serializer.ReaderBase;
import org.mtr.core.serializer.WriterBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = RouteSchema.class, remap = false)
public abstract class RouteMixin implements IRoute {

    @Shadow @Final protected org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<RoutePlatformData> routePlatformData;

    @Unique private final List<PathData> rte$pathData = new ArrayList<>();

    @Override
    public List<PathData> getPathData() {
        return new ArrayList<>(rte$pathData);
    }

    @Override
    public void setPathData(List<PathData> pathData) {
        rte$pathData.clear();
        routePlatformData.clear();

        if (pathData == null) {
            return;
        }

        PathData previous = null;
        for (PathData pathDatum : pathData) {
            if (pathDatum == null) {
                continue;
            }

            rte$pathData.add(pathDatum);
            if (pathDatum.getSavedRailBaseId() != 0 && (previous == null || !previous.isOppositeRail(pathDatum))) {
                routePlatformData.add(new RoutePlatformData(pathDatum.getSavedRailBaseId()));
            }
            previous = pathDatum;
        }
    }

    @Inject(method = "updateData", at = @At("TAIL"))
    private void rte$updateData(ReaderBase reader, CallbackInfo ci) {
        reader.iterateReaderArray("ante_path", rte$pathData::clear, child -> rte$pathData.add(new PathData(child)));
        reader.iterateReaderArray("rte_path_data", rte$pathData::clear, child -> rte$pathData.add(new PathData(child)));
        if (!rte$pathData.isEmpty()) {
            setPathData(new ArrayList<>(rte$pathData));
        }
    }

    @Inject(method = "serializeData", at = @At("TAIL"))
    private void rte$serializeData(WriterBase writer, CallbackInfo ci) {
        if (rte$pathData.isEmpty()) {
            return;
        }

        final WriterBase.Array array = writer.writeArray("rte_path_data");
        final WriterBase.Array legacyArray = writer.writeArray("ante_path");
        for (PathData pathDatum : rte$pathData) {
            pathDatum.serializeData(array.writeChild());
            pathDatum.serializeData(legacyArray.writeChild());
        }
    }
}
