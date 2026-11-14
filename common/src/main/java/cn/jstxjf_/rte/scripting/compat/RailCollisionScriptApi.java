package cn.jstxjf_.rte.scripting.compat;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.util.RailCollisionManager;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import net.minecraft.client.Minecraft;
import org.mtr.core.data.Rail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class RailCollisionScriptApi {

    private RailCollisionScriptApi() {
    }

    public static boolean setColumns(Object value, String columns) {
        final Rail rail = resolve(value);
        if (rail == null) return false;
        final String normalized = RailCollisionManager.normalizeColumns(columns);
        schedule(rail, extra -> {
            final Map<String, String> configs = new HashMap<>(extra.getCustomConfigs());
            if (normalized.equals(RailCollisionManager.normalizeColumns(configs.get(RailCollisionManager.CONFIG_COLUMNS)))
                && !configs.containsKey(RailCollisionManager.CONFIG_LAYOUT)) return;
            configs.put(RailCollisionManager.CONFIG_COLUMNS, normalized);
            configs.remove(RailCollisionManager.CONFIG_LAYOUT);
            extra.setCustomConfigs(configs);
        });
        return true;
    }

    public static boolean setLayout(Object value, String layout) {
        final Rail rail = resolve(value);
        if (rail == null) return false;
        final String normalized = RailCollisionManager.normalizeLayout(layout,
            ((RailExtraSupplier) (Object) rail).getLength());
        schedule(rail, extra -> {
            final Map<String, String> configs = new HashMap<>(extra.getCustomConfigs());
            final String current = RailCollisionManager.normalizeLayout(
                configs.get(RailCollisionManager.CONFIG_LAYOUT), extra.getLength());
            if (normalized.equals(current)
                && (normalized.isEmpty() == !configs.containsKey(RailCollisionManager.CONFIG_LAYOUT))) return;
            if (normalized.isEmpty()) configs.remove(RailCollisionManager.CONFIG_LAYOUT);
            else configs.put(RailCollisionManager.CONFIG_LAYOUT, normalized);
            extra.setCustomConfigs(configs);
        });
        return true;
    }

    public static boolean clearLayout(Object value) {
        return setLayout(value, "");
    }

    public static boolean configure(Object value, boolean enabled, double width, double height,
                                    double length, double spacing, double yOffset,
                                    double sideOffset, String columns) {
        final Rail rail = resolve(value);
        if (rail == null) return false;
        final String normalized = RailCollisionManager.normalizeColumns(columns);
        final float normalizedWidth = clamp(width, 0.05F, 32F, 3F);
        final float normalizedHeight = clamp(height, 0.05F, 16F, 0.25F);
        final float normalizedLength = clamp(length, 0.05F, 16F, 0.75F);
        final float normalizedSpacing = clamp(spacing, RailCollisionManager.MIN_SPACING, 16F, 0.5F);
        final float normalizedYOffset = clamp(yOffset, -32F, 32F, -0.25F);
        final float normalizedSideOffset = clamp(sideOffset, -32F, 32F, 0F);
        schedule(rail, extra -> {
            final Map<String, String> configs = new HashMap<>(extra.getCustomConfigs());
            final boolean same = extra.isCollisionEnabled() == enabled
                && extra.getCollisionWidth() == normalizedWidth
                && extra.getCollisionHeight() == normalizedHeight
                && extra.getCollisionLength() == normalizedLength
                && extra.getCollisionSpacing() == normalizedSpacing
                && extra.getCollisionYOffset() == normalizedYOffset
                && extra.getCollisionSideOffset() == normalizedSideOffset
                && normalized.equals(RailCollisionManager.normalizeColumns(configs.get(RailCollisionManager.CONFIG_COLUMNS)))
                && !configs.containsKey(RailCollisionManager.CONFIG_LAYOUT);
            if (same) return;
            extra.setCollisionEnabled(enabled);
            extra.setCollisionWidth(normalizedWidth);
            extra.setCollisionHeight(normalizedHeight);
            extra.setCollisionLength(normalizedLength);
            extra.setCollisionSpacing(normalizedSpacing);
            extra.setCollisionYOffset(normalizedYOffset);
            extra.setCollisionSideOffset(normalizedSideOffset);
            configs.put(RailCollisionManager.CONFIG_COLUMNS, normalized);
            configs.remove(RailCollisionManager.CONFIG_LAYOUT);
            extra.setCustomConfigs(configs);
        });
        return true;
    }

    public static String box(double distance, double sideOffset, double yOffset,
                             double width, double height, double length) {
        return number(distance) + "," + number(sideOffset) + "," + number(yOffset) + ","
            + number(width) + "," + number(height) + "," + number(length);
    }

    private static void schedule(Rail original, Consumer<RailExtraSupplier> mutation) {
        Minecraft.getInstance().execute(() -> {
            Rail rail = original;
            if (((Object) original) instanceof RailExtraSupplier source) {
                final Rail current = RailRebuildHelper.findClientRail(source.getExtraPosition1(), source.getExtraPosition2());
                if (current != null) rail = current;
            }
            if (!(((Object) rail) instanceof RailExtraSupplier extra)) return;
            final long before = fingerprint(extra);
            mutation.accept(extra);
            if (before != fingerprint(extra)) {
                RailCollisionManager.markDirty();
                PacketUpdateRail.sendUpdateC2S(rail);
            }
        });
    }

    private static Rail resolve(Object value) {
        if (value instanceof Rail rail) return rail;
        if (value instanceof RailCompat compat) return compat.data;
        if (value instanceof PathDataCompat path) return path.rail.data;
        return null;
    }

    private static long fingerprint(RailExtraSupplier extra) {
        long hash = extra.isCollisionEnabled() ? 1 : 0;
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionWidth());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionHeight());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionLength());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionSpacing());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionYOffset());
        hash = 31 * hash + Float.floatToIntBits(extra.getCollisionSideOffset());
        return 31 * hash + extra.getCustomConfigs().hashCode();
    }

    private static String number(double value) {
        return BigDecimal.valueOf(Double.isFinite(value) ? value : 0).stripTrailingZeros().toPlainString();
    }

    private static float clamp(double value, float min, float max, float fallback) {
        return Double.isFinite(value) ? (float) Math.max(min, Math.min(max, value)) : fallback;
    }
}
