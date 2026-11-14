package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTE;
import com.mojang.blaze3d.platform.NativeImage;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Mixin(NativeImage.class)
public class NativeImageDownscaleMixin {

    private static final AtomicLong rte$savedBytes = new AtomicLong();
    private static final AtomicInteger rte$downscaled = new AtomicInteger();
    private static final AtomicLong rte$spentNanos = new AtomicLong();
    private static final AtomicLong rte$nextReportMillis = new AtomicLong();

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
        final int components = source.format().components();
        final long sourceAddress = ((NativeImageAccessor) (Object) source).rte$getPixels();
        if (sourceAddress == 0 || components <= 0) {
            return;
        }

        final long started = System.nanoTime();
        final NativeImage scaled;
        try {
            scaled = new NativeImage(source.format(), targetWidth, targetHeight, false);
        } catch (Throwable e) {
            RTE.LOGGER.warn("[TextureLimit] could not downscale {}x{}: {}", width, height, e.toString());
            return;
        }
        final long targetAddress = ((NativeImageAccessor) (Object) scaled).rte$getPixels();
        if (targetAddress == 0) {
            scaled.close();
            return;
        }
        for (int y = 0; y < targetHeight; y++) {
            final long sourceRow = sourceAddress + (long) y * factor * width * components;
            final long targetRow = targetAddress + (long) y * targetWidth * components;
            if (components == 4) {
                for (int x = 0; x < targetWidth; x++) {
                    MemoryUtil.memPutInt(targetRow + (long) x * 4, MemoryUtil.memGetInt(sourceRow + (long) x * factor * 4));
                }
            } else {
                for (int x = 0; x < targetWidth; x++) {
                    final long from = sourceRow + (long) x * factor * components;
                    final long to = targetRow + (long) x * components;
                    for (int c = 0; c < components; c++) {
                        MemoryUtil.memPutByte(to + c, MemoryUtil.memGetByte(from + c));
                    }
                }
            }
        }

        rte$spentNanos.addAndGet(System.nanoTime() - started);
        rte$savedBytes.addAndGet(((long) width * height - (long) targetWidth * targetHeight) * components);
        final int count = rte$downscaled.incrementAndGet();
        final long now = System.currentTimeMillis();
        final long next = rte$nextReportMillis.get();
        if (now >= next && rte$nextReportMillis.compareAndSet(next, now + 5000)) {
            RTE.LOGGER.info("[TextureLimit] downscaled {} texture(s), saved {} MB, spent {} ms (limit {})",
                count, rte$savedBytes.get() / 1048576L, rte$spentNanos.get() / 1000000L, limit);
        }
        source.close();
        cir.setReturnValue(scaled);
    }
}
