package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.data.RailModelRegistry;
import cn.jstxjf_.rte.network.PacketUpdateHoldingItem;
import cn.jstxjf_.rte.network.PacketUpdateRail;
import cn.jstxjf_.rte.render.RailPicker;
import org.mtr.core.data.Rail;
import net.minecraft.core.BlockPos;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mod.InitClient;

import java.util.Map;
import java.util.Objects;

public final class RailBrushHelper {

    private RailBrushHelper() {
    }

    public static boolean applyBrushToPickedRail(
        BlockPos posStart,
        BlockPos posEnd,
        Rail pickedRail,
        CompoundTag railBrushProp,
        boolean isBatchApply
    ) {
        if (railBrushProp == null || posStart == null || posEnd == null || pickedRail == null) {
            return false;
        }

        final RailExtraSupplier pickedExtra = ((Object) pickedRail) instanceof RailExtraSupplier
            ? (RailExtraSupplier) (Object) pickedRail
            : null;
        if (pickedExtra == null) {
            return false;
        }

        Rail antiRail = RailRebuildHelper.findClientRail(posEnd, posStart);

        boolean propertyUpdated = false;

        if (railBrushProp.contains("ModelKeys")) {
            final java.util.List<String> values = splitModelKeys(railBrushProp.getString("ModelKeys"));
            cn.jstxjf_.rte.RTE.LOGGER.info("[RailModels] applying {} model(s) to rail: {}",
                values.size(), values);
            if (!values.equals(pickedExtra.getModelKeys())) {
                pickedExtra.setModelKeys(values);
                if (((Object) antiRail) instanceof RailExtraSupplier) {
                    ((RailExtraSupplier) (Object) antiRail).setModelKeys(values);
                }
                propertyUpdated = true;
            }
        } else if (railBrushProp.contains("ModelKey")) {
            final String value = normalizeModelKey(railBrushProp.getString("ModelKey"));
            if (!Objects.equals(value, pickedExtra.getModelKey())) {
                pickedExtra.setModelKey(value);
                if (((Object) antiRail) instanceof RailExtraSupplier) {
                    ((RailExtraSupplier) (Object) antiRail).setModelKey(value);
                }
                propertyUpdated = true;
            }
        }
        if (railBrushProp.contains("VerticalCurveRadius")) {
            final float value = railBrushProp.getFloat("VerticalCurveRadius");
            if (value != pickedExtra.getVerticalCurveRadius()) {
                pickedExtra.setVerticalCurveRadius(value);
                if (((Object) antiRail) instanceof RailExtraSupplier) {
                    ((RailExtraSupplier) (Object) antiRail).setVerticalCurveRadius(value);
                }
                propertyUpdated = true;
            }
        }
        if (railBrushProp.contains("RollAngleMap")) {
            try {
                final Map<Double, Float> raw = cn.jstxjf_.rte.util.DoubleFloatMapSerializer.deserialize(
                    railBrushProp.getString("RollAngleMap"));
                final Map<Double, Float> rollAngleMap = new java.util.HashMap<>();
                final double length = pickedExtra.getLength();
                for (final Map.Entry<Double, Float> entry : raw.entrySet()) {
                    rollAngleMap.put(entry.getKey() * length, entry.getValue());
                }
                pickedExtra.setRollAngleMap(rollAngleMap);
                if (((Object) antiRail) instanceof RailExtraSupplier) {
                    ((RailExtraSupplier) (Object) antiRail).setRollAngleMap(new java.util.HashMap<>(rollAngleMap));
                }
                propertyUpdated = true;
            } catch (Exception ignored) {
            }
        }
        if (railBrushProp.contains("RollingOffset")) {
            try {
                final float value = railBrushProp.getFloat("RollingOffset");
                if (Math.abs(value - pickedExtra.getRollingOffset()) > 0.00001F) {
                    pickedExtra.setRollingOffset(value);
                    if (((Object) antiRail) instanceof RailExtraSupplier) {
                        ((RailExtraSupplier) (Object) antiRail).setRollingOffset(value);
                    }
                    propertyUpdated = true;
                }
            } catch (Exception ignored) {
            }
        }
        if (railBrushProp.contains("RailType")) {
            final String value = railBrushProp.getString("RailType");
            pickedExtra.setRailType(value);
            if (((Object) antiRail) instanceof RailExtraSupplier) {
                ((RailExtraSupplier) (Object) antiRail).setRailType(value);
            }
            propertyUpdated = true;
        }
        if (railBrushProp.contains("SpeedLimitKph")) {
            final int value = railBrushProp.getInt("SpeedLimitKph");
            pickedExtra.setSpeedLimitKph(value);
            if (((Object) antiRail) instanceof RailExtraSupplier) {
                ((RailExtraSupplier) (Object) antiRail).setSpeedLimitKph(value);
            }
            propertyUpdated = true;
        }
        if (railBrushProp.contains("OpeningDirection")) {
            final int value = railBrushProp.getInt("OpeningDirection");
            pickedExtra.setOpeningDirection(value);
            if (((Object) antiRail) instanceof RailExtraSupplier) {
                ((RailExtraSupplier) (Object) antiRail).setOpeningDirection(value);
            }
            propertyUpdated = true;
        }
        if (railBrushProp.contains("CollisionEnabled")) {
            applyCollision(pickedExtra, railBrushProp, false);
            if (((Object) antiRail) instanceof RailExtraSupplier antiExtra) {
                applyCollision(antiExtra, railBrushProp, true);
            }
            propertyUpdated = true;
        }

        if (railBrushProp.contains("RenderReversed")) {
            final boolean value = railBrushProp.getBoolean("RenderReversed");
            if (value != pickedExtra.getRenderReversed()) {
                pickedExtra.setRenderReversed(value);
                if (((Object) antiRail) instanceof RailExtraSupplier) {
                    ((RailExtraSupplier) (Object) antiRail).setRenderReversed(!value);
                }
                propertyUpdated = true;
            }
        } else if (isBatchApply && !propertyUpdated) {
            pickedExtra.setRenderReversed(!pickedExtra.getRenderReversed());
        }

        if (propertyUpdated || isBatchApply) {
            if (antiRail != null) {
                try {
                    cn.jstxjf_.rte.RTEClient.railRenderDispatcher.evictRailByHexId(antiRail.getHexId());
                } catch (Exception ignored) {}
            }
            PacketUpdateRail.sendUpdateC2S(pickedRail);
            return true;
        }
        return false;
    }

    public static void flushBrushTag() {
        final ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
        if (player == null) {
            return;
        }

        Hand hand = null;
        if (checkItemBrush(player.getStackInHand(Hand.MAIN_HAND))) {
            hand = Hand.MAIN_HAND;
        } else if (checkItemBrush(player.getStackInHand(Hand.OFF_HAND))) {
            hand = Hand.OFF_HAND;
        }

        if (hand != null) {
            cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketUpdateHoldingItem(hand));
        }
    }

    public static void applyBrushToPickedRail(CompoundTag railBrushProp, boolean isBatchApply) {
        if (RailPicker.pickedRail != null) {
            applyBrushToPickedRail(
                RailPicker.pickedPosStart,
                RailPicker.pickedPosEnd,
                RailPicker.pickedRail,
                railBrushProp,
                isBatchApply
            );
        }
    }

    public static CompoundTag getBrushTag() {
        final ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
        if (player == null) {
            return null;
        }

        final ItemStack mainHand = player.getStackInHand(Hand.MAIN_HAND);
        final ItemStack offHand = player.getStackInHand(Hand.OFF_HAND);

        if (checkItemBrush(mainHand)) {
            return getTagElement(mainHand, BRUSH_TAG);
        } else if (checkItemBrush(offHand)) {
            return getTagElement(offHand, BRUSH_TAG);
        }

        return null;
    }

    public static void updateBrushTag(java.util.function.Consumer<CompoundTag> modifier) {
        updateBrushTag(modifier, true);
    }

    public static void updateBrushTag(java.util.function.Consumer<CompoundTag> modifier, boolean flush) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().getPlayerMapped();
        if (player == null) {
            return;
        }

        ItemStack stack = null;
        Hand hand = Hand.MAIN_HAND;

        if (checkItemBrush(player.getStackInHand(Hand.MAIN_HAND))) {
            stack = player.getStackInHand(Hand.MAIN_HAND);
        } else if (checkItemBrush(player.getStackInHand(Hand.OFF_HAND))) {
            stack = player.getStackInHand(Hand.OFF_HAND);
            hand = Hand.OFF_HAND;
        }

        if (stack == null) {
            return;
        }

        final CompoundTag tag = getOrCreateTagElement(stack, BRUSH_TAG);
        modifier.accept(tag);

        applyBrushToPickedRail(tag, false);
        if (flush) {
            flushBrushTag();
        }
    }

    public static String joinModelKeys(java.util.List<String> keys) {
        return keys == null || keys.isEmpty() ? "" : String.join("", keys);
    }

    public static java.util.List<String> splitModelKeys(String packed) {
        final java.util.List<String> keys = new java.util.ArrayList<>();
        if (packed == null || packed.isEmpty()) {
            return keys;
        }
        for (final String part : packed.split("")) {
            final String normalized = normalizeModelKey(part);
            if (!normalized.isEmpty() && !keys.contains(normalized)) {
                keys.add(normalized);
            }
        }
        return keys;
    }

    private static String normalizeModelKey(String key) {
        if (key == null) {
            return "";
        }
        final String value = key.trim();
        if (value.isEmpty()) {
            return "";
        }
        return RailModelRegistry.ELEMENTS.containsKey(value) ? value : "";
    }

    private static boolean checkItemBrush(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        try {
            final net.minecraft.world.item.Item brushItem = org.mtr.mod.Items.BRUSH.get().asItem().data;
            return stack.getItem().data.equals(brushItem);
        } catch (Exception e) {
            return stack.getItem().data.getClass().getSimpleName().equals("ItemBrush");
        }
    }

    public static final String BRUSH_TAG = "RteRailBrush";
    public static final String LEGACY_BRUSH_TAG = "NTERailBrush";

    private static void applyCollision(RailExtraSupplier extra, CompoundTag tag, boolean reverse) {
        extra.setCollisionEnabled(tag.getBoolean("CollisionEnabled"));
        extra.setCollisionWidth(tag.contains("CollisionWidth") ? tag.getFloat("CollisionWidth") : 3F);
        extra.setCollisionHeight(tag.contains("CollisionHeight") ? tag.getFloat("CollisionHeight") : 0.25F);
        extra.setCollisionLength(tag.contains("CollisionLength") ? tag.getFloat("CollisionLength") : 0.75F);
        extra.setCollisionSpacing(tag.contains("CollisionSpacing") ? tag.getFloat("CollisionSpacing") : 0.5F);
        extra.setCollisionYOffset(tag.contains("CollisionYOffset") ? tag.getFloat("CollisionYOffset") : -0.25F);
        final float sideOffset = tag.contains("CollisionSideOffset") ? tag.getFloat("CollisionSideOffset") : 0F;
        extra.setCollisionSideOffset(reverse ? -sideOffset : sideOffset);
        final String columns = tag.contains("CollisionColumns") ? tag.getString("CollisionColumns") : "0";
        extra.getCustomConfigs().put(RailCollisionManager.CONFIG_COLUMNS,
            reverse ? RailCollisionManager.mirrorColumns(columns) : RailCollisionManager.normalizeColumns(columns));
        extra.getCustomConfigs().remove(RailCollisionManager.CONFIG_LAYOUT);
        RailCollisionManager.markDirty();
    }

    private static CompoundTag getTagElement(ItemStack stack, String key) {
        final CompoundTag root = stack.getTag();
        if (root != null && root.contains(key)) {
            return root.getCompound(key);
        }
        if (root != null && BRUSH_TAG.equals(key) && root.contains(LEGACY_BRUSH_TAG)) {
            return root.getCompound(LEGACY_BRUSH_TAG);
        }
        return null;
    }

    private static CompoundTag getOrCreateTagElement(ItemStack stack, String key) {
        final CompoundTag root = stack.getOrCreateTag();
        if (root.contains(key)) {
            return root.getCompound(key);
        }

        final CompoundTag child = new CompoundTag();
        root.data.put(key, child.data);
        return child;
    }
}
