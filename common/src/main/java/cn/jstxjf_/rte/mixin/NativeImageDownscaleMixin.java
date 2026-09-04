package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import com.mojang.blaze3d.platform.NativeImage;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

@Mixin(NativeImage.class)
public class NativeImageDownscaleMixin {

    private static Field rte$pixelsField;
    private static boolean rte$pixelsResolved;
    private static long rte$savedBytes;
    private static int rte$downscaled;
    private static long rte$spentNanos;
    private static long rte$nextReportMillis;

    private static Field rte$resolvePixelsField() {
        if (rte$pixelsResolved) {
            return rte$pixelsField;
        }
        rte$pixelsResolved = true;
        try {
            rte$pixelsField = NativeImage.class.getDeclaredField("pixels");
        } catch (NoSuchFieldException ignored) {
            for (final Field field : NativeImage.class.getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                if (field.getType() == long.class && !Modifier.isStatic(modifiers)) {
                    rte$pixelsField = field;
                    break;
                }
            }
        }
        if (rte$pixelsField != null) {
            rte$pixelsField.setAccessible(true);
        }
        return rte$pixelsField;
    }

    private static IntBuffer rte$pixelsOf(NativeImage image) {
        try {
            final Field field = rte$resolvePixelsField();
            if (field == null) {
                return null;
            }
            final long address = (long) field.get(image);
            if (address == 0) {
                return null;
            }
            final ByteBuffer bytes = MemoryUtil.memByteBuffer(address,
                image.getWidth() * image.getHeight() * 4);
            return bytes == null ? null : bytes.order(ByteOrder.nativeOrder()).asIntBuffer();
        } catch (Throwable e) {
            return null;
        }
    }

    @Inject(
        method = "read(Lcom/mojang/blaze3d/platform/NativeImage$Format;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/platform/NativeImage;",
        at = @At("RETURN"),
        cancellable = true,
        require = 0
    )
    private static void rte$downscaleOversized(NativeImage.Format format, ByteBuffer buffer,
                                               CallbackInfoReturnable<NativeImage> cir) {
        final int limit = ClientConfig.maxTextureSize;
        if (limit <= 0) {
            return;
        }
        final NativeImage source = cir.getReturnValue();
        if (source == null) {
            return;
        }
        final int width = source.getWidth();
        final int height = source.getHeight();
        if (width <= limit && height <= limit) {
            return;
        }
        int factor = 1;
        while ((width / factor > limit || height / factor > limit)
                && width / (factor * 2) >= 1 && height / (factor * 2) >= 1) {
            factor *= 2;
        }
        final int targetWidth = Math.max(1, width / factor);
        final int targetHeight = Math.max(1, height / factor);

        final long started = System.nanoTime();
        final IntBuffer sourcePixels = rte$pixelsOf(source);
        if (sourcePixels == null) {
            return;
        }
        NativeImage scaled = null;
        try {
            scaled = new NativeImage(source.format(), targetWidth, targetHeight, false);
            final IntBuffer targetPixels = rte$pixelsOf(scaled);
            if (targetPixels == null) {
                scaled.close();
                return;
            }
            for (int y = 0; y < targetHeight; y++) {
                final int sourceRow = y * factor * width;
                final int targetRow = y * targetWidth;
                for (int x = 0; x < targetWidth; x++) {
                    targetPixels.put(targetRow + x, sourcePixels.get(sourceRow + x * factor));
                }
            }
        } catch (Throwable e) {
            if (scaled != null) {
                scaled.close();
            }
            RTE.LOGGER.warn("[TextureLimit] could not downscale {}x{}: {}", width, height, e.toString());
            return;
        }

        rte$spentNanos += System.nanoTime() - started;
        rte$savedBytes += ((long) width * height - (long) targetWidth * targetHeight) * 4L;
        rte$downscaled++;
        final long now = System.currentTimeMillis();
        if (now >= rte$nextReportMillis) {
            rte$nextReportMillis = now + 5000;
            RTE.LOGGER.info("[TextureLimit] downscaled {} texture(s), saved {} MB, spent {} ms (limit {})",
                rte$downscaled, rte$savedBytes / 1048576L, rte$spentNanos / 1000000L, limit);
        }
        source.close();
        cir.setReturnValue(scaled);
    }
}
