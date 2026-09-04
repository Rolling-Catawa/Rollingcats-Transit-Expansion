package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.data.RailExtraSupplier;
import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.core.data.Rail;
import org.mtr.core.data.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.mtr.mod.client.MinecraftClientData;
import java.util.HashSet;
import java.util.Set;

public class RailDistanceRenderer {
    private static Vec3 cameraPos = null;

    private static final int ARGB_WHITE = 0xFFFFFFFF;

    public static void render(PoseStack matrices, MultiBufferSource vertexConsumers) {
        cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        var rails = MinecraftClientData.getInstance().positionsToRail;
        if (rails == null) return;
        Set<String> drawnRails = new HashSet<>();
        synchronized (rails) {
            for (var railMap : rails.values()) {
                for (Rail rail : railMap.values()) {
                    if (!drawnRails.add(getRailPairKey(rail))) {
                        continue;
                    }
                    renderAtNodes(rail, matrices, vertexConsumers);
                }
            }
        }
    }

    private static void renderAtNodes(Rail rail, PoseStack matrices, MultiBufferSource vertexConsumers) {
        final RailExtraSupplier extra = ((Object) rail) instanceof RailExtraSupplier ? (RailExtraSupplier) (Object) rail : null;
        if (extra == null) return;

        final Position p1 = extra.getExtraPosition1();
        final Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return;

        final double length = extra.getLength();
        final double horizontalLength = Math.sqrt(
            Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getZ() - p1.getZ(), 2)
        );

        final String lengthLabel = net.minecraft.client.resources.language.I18n.get("gui.rte.rail_stat.xz_length");
        final String lengthValue = String.valueOf(round(horizontalLength, 3));

        final String radiusLabel;
        final String radiusValue;
        double radius = estimateRadius(extra, length);
        if (radius > 0 && Double.isFinite(radius)) {
            radiusLabel = net.minecraft.client.resources.language.I18n.get("gui.rte.rail_stat.xz_radius");
            radiusValue = String.valueOf(round(radius, 3));
        } else {
            radiusLabel = null;
            radiusValue = null;
        }

        final String offsetLabel = net.minecraft.client.resources.language.I18n.get("gui.rte.rail_stat.xyz_offset");
        final String offsetValue = String.format("(%s, %s, %s)",
            (int)(p2.getX() - p1.getX()),
            (int)(p2.getY() - p1.getY()),
            (int)(p2.getZ() - p1.getZ())
        );

        renderStatsAtPosition(p1, matrices, vertexConsumers, lengthLabel, lengthValue, radiusLabel, radiusValue, offsetLabel, offsetValue);
    }

    private static void renderStatsAtPosition(Position pos, PoseStack matrices, MultiBufferSource vertexConsumers,
                                               String lengthLabel, String lengthValue,
                                               String radiusLabel, String radiusValue,
                                               String offsetLabel, String offsetValue) {
        Vec3 worldPos = new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        if (worldPos.distanceToSqr(cameraPos) > ClientConfig.railDistanceRendererMaxDistanceSqr) return;

        matrices.pushPose();
        matrices.translate((float) worldPos.x, (float) worldPos.y, (float) worldPos.z);

        matrices.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        float scale = 1F / 32F;
        matrices.scale(-scale, -scale, scale);

        Font font = Minecraft.getInstance().font;
        int line = 0;

        line = renderStat(font, matrices, vertexConsumers, offsetLabel, offsetValue, line);
        if (radiusLabel != null && radiusValue != null) {
            line = renderStat(font, matrices, vertexConsumers, radiusLabel, radiusValue, line);
        }
        renderStat(font, matrices, vertexConsumers, lengthLabel, lengthValue, line);

        matrices.popPose();
    }

    private static int renderStat(Font font, PoseStack matrices, MultiBufferSource vertexConsumers,
                                   String label, String value, int line) {
        int newLine = line - 9;
        float xOffset = (float) (-font.width(value) / 2);
        font.drawInBatch(value, xOffset, newLine, ARGB_WHITE, true, matrices.last().pose(), vertexConsumers, Font.DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT);

        matrices.pushPose();
        matrices.scale(0.5F, 0.5F, 0.5F);
        newLine -= 5;
        float labelXOffset = (float) (-font.width(label) / 2);
        font.drawInBatch(label, labelXOffset, newLine * 2, ARGB_WHITE, true, matrices.last().pose(), vertexConsumers, Font.DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT);
        matrices.popPose();

        return newLine - 1;
    }

    private static double estimateRadius(RailExtraSupplier extra, double length) {
        if (length < 1) return 0;
        final Position p1 = extra.getExtraPosition1();
        final Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) return 0;

        double chord = Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getZ() - p1.getZ(), 2));
        if (chord < 0.01) return 0;
        double ratio = length / chord;
        if (ratio < 1.001) return 0;

        double midDist = length / 2.0;
        org.mtr.mapping.holder.Vector3d midPos = extra.getPosition(midDist);
        double midX = (p1.getX() + p2.getX()) / 2.0 + 0.5;
        double midZ = (p1.getZ() + p2.getZ()) / 2.0 + 0.5;
        double sagitta = Math.sqrt(
            Math.pow(midPos.getXMapped() - midX, 2) +
            Math.pow(midPos.getZMapped() - midZ, 2)
        );
        if (sagitta < 0.001 || sagitta / chord < 0.001) return 0;

        return (chord * chord / (8 * sagitta)) + (sagitta / 2);
    }

    private static double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }

    private static String getRailPairKey(Rail rail) {
        if (((Object) rail) instanceof RailExtraSupplier extra && extra.getExtraPosition1() != null && extra.getExtraPosition2() != null) {
            final String p1 = extra.getExtraPosition1().toString();
            final String p2 = extra.getExtraPosition2().toString();
            return p1.compareTo(p2) <= 0 ? p1 + "|" + p2 : p2 + "|" + p1;
        }
        return rail.getHexId();
    }
}
