package cn.jstxjf_.rte.render;

import cn.jstxjf_.rte.data.RailGeometryAnalysis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Locale;

public final class PreviewMetrics {

    private static String cacheKey = "";
    private static RailGeometryAnalysis.Analysis cachedAnalysis;
    private static long lastShownMs;

    private PreviewMetrics() {
    }

    public static void report(Position posStart, Position posEnd, Angle angle1, Angle angle2, TransportMode transportMode) {
        try {
            if (transportMode != TransportMode.TRAIN) {
                return;
            }
            final String key = posStart.getX() + "," + posStart.getY() + "," + posStart.getZ()
                + "|" + posEnd.getX() + "," + posEnd.getY() + "," + posEnd.getZ()
                + "|" + angle1 + "|" + angle2;
            final long now = System.currentTimeMillis();
            if (!key.equals(cacheKey)) {
                cacheKey = key;
                final Rail preview = Rail.newRail(posStart, angle1, posEnd, angle2,
                    Rail.Shape.QUADRATIC, 0, new ObjectArrayList<>(), 20, 20,
                    false, false, true, false, false, transportMode);
                cachedAnalysis = RailGeometryAnalysis.analyze(preview);
                lastShownMs = 0;
            }
            if (cachedAnalysis == null || now - lastShownMs < 500) {
                return;
            }
            lastShownMs = now;
            final Minecraft client = Minecraft.getInstance();
            if (client.player == null) {
                return;
            }
            final ChatFormatting ratingColor = switch (cachedAnalysis.rating()) {
                case RailGeometryAnalysis.RATING_GREEN -> ChatFormatting.GREEN;
                case RailGeometryAnalysis.RATING_YELLOW -> ChatFormatting.YELLOW;
                default -> ChatFormatting.RED;
            };
            final String ratingKey = switch (cachedAnalysis.rating()) {
                case RailGeometryAnalysis.RATING_GREEN -> "gui.rte.rail_rating.green";
                case RailGeometryAnalysis.RATING_YELLOW -> "gui.rte.rail_rating.yellow";
                default -> "gui.rte.rail_rating.red";
            };
            client.player.displayClientMessage(
                Component.literal("● ").withStyle(ratingColor)
                    .append(Component.translatable(ratingKey).withStyle(ratingColor))
                    .append(Component.literal(String.format(Locale.ROOT, "  %.1f m | R min %s m | %.1f‰ | ≤%.0f km/h",
                        cachedAnalysis.lengthMeters(),
                        cachedAnalysis.minRadiusMeters() == null ? "∞" : String.format(Locale.ROOT, "%.0f", cachedAnalysis.minRadiusMeters()),
                        cachedAnalysis.maxGradePermille(),
                        cachedAnalysis.suggestedSpeedKph())).withStyle(ChatFormatting.WHITE)),
                true);
        } catch (Throwable ignored) {
        }
    }
}
