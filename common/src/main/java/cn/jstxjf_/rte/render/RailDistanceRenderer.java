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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RailDistanceRenderer {
    private static Vec3 cameraPos = null;

    private static final int ARGB_WHITE = 0xFFFFFFFF;

    public static void render(PoseStack matrices, MultiBufferSource vertexConsumers) {
        cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final Rail rail = RailPicker.pickedRail;
        if (rail != null) renderAtNodes(rail, matrices, vertexConsumers);
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

        final double dx = p2.getX() - p1.getX();
        final double dy = p2.getY() - p1.getY();
        final double dz = p2.getZ() - p1.getZ();
        final List<String[]> rows = new ArrayList<>();
        addRow(rows, "gui.rte.rail_stat.length", meters(length));
        addRow(rows, "gui.rte.rail_stat.xz_length", meters(horizontalLength));
        addRow(rows, "gui.rte.rail_stat.xyz_offset", String.format(Locale.ROOT, "(%s, %s, %s)", number(dx), number(dy), number(dz)));
        addRow(rows, "gui.rte.rail_stat.gradient", String.format(Locale.ROOT, "%s m / %s%%", number(dy), number(horizontalLength < 1E-6 ? 0 : dy / horizontalLength * 100)));

        double radius = estimateRadius(extra, length);
        if (radius > 0 && Double.isFinite(radius)) {
            addRow(rows, "gui.rte.rail_stat.xz_radius", meters(radius));
        }

        addRow(rows, "gui.rte.rail_stat.heading", degrees(extra.getExtraAngle1Rad()) + " / " + degrees(extra.getExtraAngle2Rad()));
        addRow(rows, "gui.rte.rail_stat.pitch", number(extra.getNodePitch1()) + "\u00B0 / " + number(extra.getNodePitch2()) + "\u00B0");
        addRow(rows, "gui.rte.rail_stat.path_mode", pathMode(extra.getPathMode()));

        if (Math.abs(extra.getVerticalCurveRadius()) > 1E-5) {
            addRow(rows, "gui.rte.rail_stat.vertical_radius", meters(extra.getVerticalCurveRadius()));
        }

        final String railType = extra.getRailType();
        final String shape = extra.getExtraShape() == null ? "-" : extra.getExtraShape().name();
        final String transportMode = extra.getExtraTransportMode() == null ? "-" : extra.getExtraTransportMode().name();
        addRow(rows, "gui.rte.rail_stat.rail_type", (railType == null || railType.isBlank() ? shape : railType) + " / " + transportMode);

        final int speed = extra.getSpeedLimitKph();
        addRow(rows, "gui.rte.rail_stat.speed_limit", speed > 0 ? speed + " km/h" : translate("gui.rte.rail_stat.not_set"));

        final List<String> modelKeys = extra.getModelKeys();
        final String models = modelKeys == null || modelKeys.isEmpty() ? translate("gui.rte.rail_stat.default") : abbreviate(String.join(", ", modelKeys), 48);
        addRow(rows, "gui.rte.rail_stat.models", models);

        final Map<Double, Float> rollMap = extra.getRollAngleMap();
        if ((rollMap != null && !rollMap.isEmpty()) || Math.abs(extra.getRollingOffset()) > 1E-5) {
            float minRoll = 0;
            float maxRoll = 0;
            if (rollMap != null && !rollMap.isEmpty()) {
                minRoll = Float.POSITIVE_INFINITY;
                maxRoll = Float.NEGATIVE_INFINITY;
                for (final float value : rollMap.values()) {
                    minRoll = Math.min(minRoll, value);
                    maxRoll = Math.max(maxRoll, value);
                }
            }
            addRow(rows, "gui.rte.rail_stat.roll", number(minRoll) + "\u00B0 \u2026 " + number(maxRoll) + "\u00B0 / " + number(extra.getRollingOffset()) + " m");
        }

        final String collision = extra.isCollisionEnabled()
            ? String.format(Locale.ROOT, "%s \u00D7 %s \u00D7 %s m @ %s m", number(extra.getCollisionWidth()), number(extra.getCollisionHeight()), number(extra.getCollisionLength()), number(extra.getCollisionSpacing()))
            : translate("gui.rte.rail_stat.disabled");
        addRow(rows, "gui.rte.rail_stat.collision", collision);

        renderStatsAtPosition(p1, matrices, vertexConsumers, rows);
    }

    private static void renderStatsAtPosition(Position pos, PoseStack matrices, MultiBufferSource vertexConsumers,
                                               List<String[]> rows) {
        Vec3 worldPos = new Vec3(pos.getX() + 0.5, pos.getY() + 1.3, pos.getZ() + 0.5);
        if (worldPos.distanceToSqr(cameraPos) > ClientConfig.railDistanceRendererMaxDistanceSqr) return;

        matrices.pushPose();
        matrices.translate((float) worldPos.x, (float) worldPos.y, (float) worldPos.z);

        matrices.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        float scale = 1F / 64F;
        matrices.scale(-scale, -scale, scale);

        Font font = Minecraft.getInstance().font;
        final int rowsPerColumn = (rows.size() + 1) / 2;
        final int gap = 14;
        int firstWidth = 0;
        int secondWidth = 0;
        for (int i = 0; i < rows.size(); i++) {
            final int width = font.width(rowText(rows.get(i)));
            if (i < rowsPerColumn) {
                firstWidth = Math.max(firstWidth, width);
            } else {
                secondWidth = Math.max(secondWidth, width);
            }
        }
        final int totalWidth = firstWidth + (secondWidth > 0 ? gap + secondWidth : 0);
        final float left = -totalWidth / 2F;
        final float startY = -(rowsPerColumn - 1) * 11F / 2F;
        for (int i = 0; i < rows.size(); i++) {
            final boolean secondColumn = i >= rowsPerColumn;
            final int row = secondColumn ? i - rowsPerColumn : i;
            final float x = secondColumn ? left + firstWidth + gap : left;
            renderStat(font, matrices, vertexConsumers, rowText(rows.get(i)), x, startY + row * 11F);
        }

        matrices.popPose();
    }

    private static void renderStat(Font font, PoseStack matrices, MultiBufferSource vertexConsumers,
                                   String text, float x, float y) {
        font.drawInBatch(text, x, y, ARGB_WHITE, false, matrices.last().pose(), vertexConsumers, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
    }

    private static String rowText(String[] row) {
        return row[0] + ": " + row[1];
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

    private static void addRow(List<String[]> rows, String key, String value) {
        rows.add(new String[]{translate(key), value});
    }

    private static String translate(String key) {
        return net.minecraft.client.resources.language.I18n.get(key);
    }

    private static String meters(double value) {
        return number(value) + " m";
    }

    private static String number(double value) {
        return String.valueOf(round(value, 3));
    }

    private static String degrees(double radians) {
        if (!Double.isFinite(radians)) return "-";
        double value = Math.toDegrees(radians) % 360;
        if (value < 0) value += 360;
        return number(value) + "\u00B0";
    }

    private static String pathMode(int mode) {
        return translate(switch (mode) {
            case RailExtraSupplier.PATH_MODE_AUTO_BEZIER -> "gui.rte.rail_stat.path.auto_bezier";
            case RailExtraSupplier.PATH_MODE_CURVE_FITTING -> "gui.rte.rail_stat.path.curve_fitting";
            case RailExtraSupplier.PATH_MODE_MANUAL_BEZIER -> "gui.rte.rail_stat.path.manual_bezier";
            default -> "gui.rte.rail_stat.path.original";
        });
    }

    private static String abbreviate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 1) + "\u2026";
    }

}
