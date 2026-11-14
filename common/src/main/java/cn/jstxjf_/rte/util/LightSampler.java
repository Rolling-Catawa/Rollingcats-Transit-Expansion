package cn.jstxjf_.rte.util;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public final class LightSampler {

    private static final int[][] FALLBACK_OFFSETS = {
        {0, 1, 0}, {0, 2, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {0, -1, 0}
    };

    private LightSampler() {
    }

    public static int packed(Level level, double x, double y, double z) {
        return packed(level, cn.jstxjf_.rte.compat.McCompat.blockPos(x, y, z));
    }

    public static int packed(Level level, BlockPos pos) {
        if (level == null) {
            return 0;
        }
        final int block = level.getBrightness(LightLayer.BLOCK, pos);
        final int sky = level.getBrightness(LightLayer.SKY, pos);
        if (!occludes(level, pos)) {
            return LightTexture.pack(block, sky);
        }
        int bestBlock = -1;
        int bestSky = -1;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (final int[] offset : FALLBACK_OFFSETS) {
            cursor.set(pos.getX() + offset[0], pos.getY() + offset[1], pos.getZ() + offset[2]);
            if (occludes(level, cursor)) {
                continue;
            }
            bestBlock = Math.max(bestBlock, level.getBrightness(LightLayer.BLOCK, cursor));
            bestSky = Math.max(bestSky, level.getBrightness(LightLayer.SKY, cursor));
        }
        return bestBlock < 0 ? LightTexture.pack(block, sky) : LightTexture.pack(bestBlock, bestSky);
    }

    private static boolean occludes(Level level, BlockPos pos) {
        try {
            return level.getBlockState(pos).isSolidRender(level, pos);
        } catch (Exception e) {
            return false;
        }
    }
}
