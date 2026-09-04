package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.mixin.ItemRailModifierAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.mod.data.RailType;
import org.mtr.mod.item.ItemRailModifier;

public final class RailItemLookup {

    private RailItemLookup() {
    }

    private record Candidate(Item item, RailType railType, boolean oneWay) {}

    private static java.util.List<Candidate> candidates;

    private static java.util.List<Candidate> candidates() {
        if (candidates == null) {
            final java.util.List<Candidate> collected = new java.util.ArrayList<>();
            for (final Item item : BuiltInRegistries.ITEM) {
                if (!(item instanceof ItemRailModifier)) {
                    continue;
                }
                try {
                    final ItemRailModifierAccessor accessor = (ItemRailModifierAccessor) item;
                    final RailType railType = accessor.rte$getRailType();
                    if (railType != null) {
                        collected.add(new Candidate(item, railType, accessor.rte$isOneWay()));
                    }
                } catch (Exception ignored) {
                }
            }
            candidates = collected;
        }
        return candidates;
    }

    public static ItemStack stackForRail(Rail rail) {
        if (rail == null) {
            return ItemStack.EMPTY;
        }
        final long speed1 = rail.getSpeedLimitKilometersPerHour(false);
        final long speed2 = rail.getSpeedLimitKilometersPerHour(true);
        final boolean oneWay = (speed1 <= 0) != (speed2 <= 0);
        final long speed = Math.max(speed1, speed2);

        for (final Candidate candidate : candidates()) {
            if (matches(candidate.railType(), candidate.oneWay(), rail, oneWay, speed)) {
                return withRailStyle(new ItemStack(candidate.item()), rail);
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean matches(RailType railType, boolean itemOneWay, Rail rail, boolean oneWay, long speed) {
        if (itemOneWay != oneWay) {
            return false;
        }
        if (rail.isPlatform()) {
            return railType == RailType.PLATFORM;
        }
        if (rail.isSiding()) {
            return railType == RailType.SIDING;
        }
        if (rail.canTurnBack()) {
            return railType == RailType.TURN_BACK;
        }
        if (railType.isSavedRail || railType == RailType.TURN_BACK) {
            return false;
        }
        if (railType.speedLimit != speed || railType.canAccelerate != rail.canAccelerate()) {
            return false;
        }
        return allowedForTransportMode(rail.getTransportMode(), railType);
    }

    private static boolean allowedForTransportMode(TransportMode transportMode, RailType railType) {
        final boolean cableCarType = railType == RailType.CABLE_CAR || railType == RailType.CABLE_CAR_STATION;
        final boolean airplaneType = railType == RailType.RUNWAY || railType == RailType.AIRPLANE_DUMMY;
        if (transportMode == TransportMode.CABLE_CAR) {
            return cableCarType;
        }
        if (transportMode == TransportMode.AIRPLANE) {
            return airplaneType;
        }
        return !cableCarType && !airplaneType;
    }

    private static ItemStack withRailStyle(ItemStack stack, Rail rail) {
        if (!(((Object) rail) instanceof RailExtraSupplier extra)) {
            return stack;
        }
        final int pathMode;
        try {
            pathMode = extra.getPathMode();
        } catch (Exception e) {
            return stack;
        }
        if (pathMode == RailExtraSupplier.PATH_MODE_ORIGINAL) {
            return stack;
        }
        stack.getOrCreateTag().putInt("path_mode", pathMode);
        stack.getOrCreateTag().putInt("rte_path_mode", pathMode);
        return stack;
    }
}
