package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.RTEClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.client.VehicleRidingMovement;
import org.mtr.core.data.Vehicle;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RenderUtil {

    public static PoseStack commonPoseStack = null;
    public static MultiBufferSource commonVertexConsumers = null;
    public static org.mtr.mapping.holder.Vector3d mtrRenderOffset = null;
    public static double bodyOscillation;
    public static boolean bodyOscillationValid;

    public static double runningSeconds;
    public static double frameSeconds;
    private static long lastFrameNanos = 0;

    public static void updateElapsedTicks() {
        final long now = System.nanoTime();
        if (lastFrameNanos == 0) {
            lastFrameNanos = now;
        }
        double delta = (now - lastFrameNanos) / 1.0e9;
        lastFrameNanos = now;
        if (Minecraft.getInstance().isPaused() || delta < 0) {
            delta = 0;
        } else if (delta > 0.5) {
            delta = 0.5;
        }
        frameSeconds = delta;
        runningSeconds += delta;
        cn.jstxjf_.rte.scripting.util.TimingUtil.runningSeconds = runningSeconds;
    }

    public static boolean shouldSkipRenderTrain(Vehicle train) {
        if (!ClientConfig.enableTrainRender) return true;
        if (ClientConfig.hideRidingTrain) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                return VehicleRidingMovement.isRiding(train.getId());
            }
        }
        return false;
    }

    public static String getRenderStatusMessage() {
        return "\n=== RTE Rendering Status ===\n"
                + "Draw Calls: " + RTEClient.drawContext.drawCallCount
                + ", Batches: " + RTEClient.drawContext.batchCount
                + "\n"
                + "Faces: " + RTEClient.drawContext.singleFaceCount + " non-instanced"
                + ", " + RTEClient.drawContext.instancedFaceCount + " instanced"
                + ", " + (RTEClient.drawContext.singleFaceCount + RTEClient.drawContext.instancedFaceCount) + " total"
                + "\n"
                + "Faces via Blaze3D: " + RTEClient.drawContext.blazeFaceCount
                + "\n"
                + "Uploaded Models: " + RTEClient.modelManager.uploadedVertArrays.size()
                + " (" + RTEClient.modelManager.vaoCount + " VAOs, "
                + RTEClient.modelManager.vboCount + " VBOs)"
                ;
    }

    public static int parseHexColor(String src) {
        if (src.length() > 6) {
            return Integer.reverseBytes(Integer.parseInt(src, 16));
        } else {
            return Integer.reverseBytes((Integer.parseInt(src, 16) << 8 | 0xFF));
        }
    }

    public static void displayStatusMessage(String msg) {
        if (Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.displayClientMessage(TextHelper.literal(
            String.format("[%s] %s", LocalTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME), msg)
        ).data, false);
    }
}
