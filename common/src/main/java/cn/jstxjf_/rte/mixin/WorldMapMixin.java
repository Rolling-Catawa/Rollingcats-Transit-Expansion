package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.holder.ChunkManager;
import org.mtr.mod.screen.WorldMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldMap.class, remap = false)
public abstract class WorldMapMixin {

    @Redirect(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lorg/mtr/mapping/holder/ChunkManager;isChunkLoaded(II)Z"),
        require = 0
    )
    private boolean rte$keepUnloadedChunks(ChunkManager chunkManager, int chunkX, int chunkZ) {
        return true;
    }
}
