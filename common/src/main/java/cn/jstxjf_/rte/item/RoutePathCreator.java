package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import cn.jstxjf_.rte.gui.RoutePathCreatorScreen;
import cn.jstxjf_.rte.network.PacketUpdateHoldingItem;
import org.mtr.core.data.PathData;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.tool.Angle;
import org.mtr.core.serializer.JsonReader;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.com.google.gson.JsonParser;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.holder.TooltipContext;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockNode;
import org.mtr.mod.client.MinecraftClientData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoutePathCreator extends ItemExtension {

    public static final String TAG_LAST_POS = "rte_last_pos";
    public static final String TAG_PATH_COUNT = "rte_path_count";
    public static final String TAG_PATH_PREFIX = "rte_path_";
    public static final String TAG_ROUTE_NAME = "rte_route_name";
    public static final String TAG_ROUTE_COLOR = "rte_route_color";
    private static final String DEFAULT_ROUTE_NAME = "Route Pro Max";
    private static final int DEFAULT_ROUTE_COLOR = 0xFFFFFF;
    private static final long TURN_BACK_DWELL_TIME_MILLIS = 500;

    public RoutePathCreator(ItemSettings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
            return ActionResult.SUCCESS;
        }

        final PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.SUCCESS;
        }

        final BlockPos clickedPos = context.getBlockPos();
        final ItemStack stack = context.getStack();
        final CompoundTag tag = stack.getOrCreateTag();
        ensureDefaults(tag);

        if (!(context.getWorld().getBlockState(clickedPos).getBlock().data instanceof BlockNode)) {
            return ActionResult.SUCCESS;
        }

        if (tag.contains(TAG_LAST_POS)) {
            final BlockPos lastPos = BlockPos.fromLong(tag.getLong(TAG_LAST_POS));
            final Rail rail = findRail(lastPos, clickedPos);
            if (rail == null) {
                sendMessage(player, "gui.rte.rail_path_creator.error.no_rail");
                return ActionResult.SUCCESS;
            }
            if (isOneWayRail(rail)) {
                sendMessage(player, "gui.rte.rail_path_creator.error.one_way");
                return ActionResult.SUCCESS;
            }

            final PlatformMatch platformMatch = findPlatformMatch(lastPos, clickedPos);
            final List<PathData> pathData = readPath(tag);
            if (pathData.isEmpty() && platformMatch.platformId == 0) {
                tag.remove(TAG_LAST_POS);
                syncHoldingItem(context.getHand());
                sendMessage(player, "gui.rte.rail_path_creator.error.must_start_with_platform");
                return ActionResult.SUCCESS;
            }

            PathData newPathData = new PathData(
                rail,
                platformMatch.platformId,
                rail.canTurnBack() ? TURN_BACK_DWELL_TIME_MILLIS : platformMatch.dwellTime,
                0,
                toPosition(lastPos),
                toPosition(clickedPos)
            );

            if (!pathData.isEmpty()) {
                final PathData previous = pathData.get(pathData.size() - 1);
                if (previous.isOppositeRail(newPathData) && pathData.size() == 1) {
                    sendMessage(player, "gui.rte.rail_path_creator.error.cannot_turn_back_at_start");
                    return ActionResult.SUCCESS;
                }
                if (previous.isOppositeRail(newPathData)
                        && !(previous.getRail().canTurnBack() || previous.getRail().isPlatform() || newPathData.getRail().canTurnBack() || newPathData.getRail().isPlatform())) {
                    sendMessage(player, "gui.rte.rail_path_creator.error.illegal_turn_back");
                    return ActionResult.SUCCESS;
                }
                if (previous.isOppositeRail(newPathData)) {
                    newPathData = copyPathDataWithDwellTime(newPathData, 0);
                    if (previous.getDwellTime() == 0) {
                        pathData.set(pathData.size() - 1, copyPathDataWithDwellTime(previous, TURN_BACK_DWELL_TIME_MILLIS));
                    }
                }
            }

            pathData.add(newPathData);
            final PathValidationError validationError = validatePathProgress(pathData);
            if (validationError != null) {
                pathData.remove(pathData.size() - 1);
                writePath(pathData, tag);
                syncHoldingItem(context.getHand());
                sendMessage(player, validationError.translationKey);
                return ActionResult.SUCCESS;
            }
            writePath(pathData, tag);
            sendMessage(player, "gui.rte.rail_path_creator.success.path", pathData.size());
        } else {
            sendMessage(player, "gui.rte.rail_path_creator.success.pos");
        }

        tag.putLong(TAG_LAST_POS, clickedPos.asLong());
        syncHoldingItem(context.getHand());
        return ActionResult.SUCCESS;
    }

    @Override
    public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            return;
        }

        RoutePathCreatorScreen.open(hand == Hand.MAIN_HAND);
    }

    @Override
    public void addTooltips(ItemStack stack, World world, List<MutableText> list, TooltipContext tooltipContext) {
        final CompoundTag tag = stack.getTag();
        if (tag == null) {
            list.add(TextHelper.translatable("tooltip.rte.route_path_creator.empty"));
            return;
        }

        final List<PathData> pathData = readPath(tag);
        if (pathData.isEmpty()) {
            list.add(TextHelper.translatable("tooltip.rte.route_path_creator.empty"));
            return;
        }

        list.add(TextHelper.literal("Route: " + getRouteName(tag)));
        list.add(TextHelper.literal(String.format("Color: #%06X", getRouteColor(tag))));
        if (tag.contains(TAG_LAST_POS)) {
            list.add(TextHelper.literal("Last node: " + formatBlockPos(BlockPos.fromLong(tag.getLong(TAG_LAST_POS)))));
        }

        final int maxEntries = Math.min(pathData.size(), 4);
        for (int i = 0; i < maxEntries; i++) {
            final PathData pathDatum = pathData.get(i);
            list.add(TextHelper.literal(String.format(
                "[%d] %s -> %s | %.1fm | %.1fs",
                i + 1,
                formatPosition(pathDatum.getOrderedPosition1()),
                formatPosition(pathDatum.getOrderedPosition2()),
                pathDatum.getRailLength(),
                pathDatum.getDwellTime() / 1000D
            )));
        }

        if (pathData.size() > maxEntries) {
            list.add(TextHelper.literal("... +" + (pathData.size() - maxEntries) + " more"));
        }
    }

    public static void ensureDefaults(CompoundTag tag) {
        if (!tag.contains(TAG_ROUTE_NAME)) {
            tag.putString(TAG_ROUTE_NAME, DEFAULT_ROUTE_NAME);
        }
        if (!tag.contains(TAG_ROUTE_COLOR)) {
            tag.putInt(TAG_ROUTE_COLOR, DEFAULT_ROUTE_COLOR);
        }
    }

    public static String getRouteName(CompoundTag tag) {
        ensureDefaults(tag);
        final String routeName = tag.getString(TAG_ROUTE_NAME);
        return routeName == null || routeName.isBlank() ? DEFAULT_ROUTE_NAME : routeName;
    }

    public static int getRouteColor(CompoundTag tag) {
        ensureDefaults(tag);
        return tag.getInt(TAG_ROUTE_COLOR);
    }

    public static void setRouteName(CompoundTag tag, String routeName) {
        tag.putString(TAG_ROUTE_NAME, routeName == null || routeName.isBlank() ? DEFAULT_ROUTE_NAME : routeName);
    }

    public static void setRouteColor(CompoundTag tag, int color) {
        tag.putInt(TAG_ROUTE_COLOR, color & 0xFFFFFF);
    }

    public static List<PathData> readPath(CompoundTag tag) {
        final List<PathData> pathData = new ArrayList<>();
        final int count = tag.getInt(TAG_PATH_COUNT);
        for (int i = 0; i < count; i++) {
            final String json = tag.getString(TAG_PATH_PREFIX + i);
            if (json == null || json.isBlank()) {
                continue;
            }
            try {
                pathData.add(new PathData(new JsonReader(JsonParser.parseString(json))));
            } catch (Exception ignored) {
            }
        }
        return pathData;
    }

    public static void writePath(List<PathData> pathData, CompoundTag tag) {
        final int oldCount = tag.getInt(TAG_PATH_COUNT);
        tag.putInt(TAG_PATH_COUNT, pathData.size());
        for (int i = 0; i < pathData.size(); i++) {
            tag.putString(TAG_PATH_PREFIX + i, Utilities.getJsonObjectFromData(pathData.get(i)).toString());
        }
        for (int i = pathData.size(); i < oldCount; i++) {
            tag.remove(TAG_PATH_PREFIX + i);
        }
    }

    public static RailSelection findRailSelection(BlockPos clickedPos, float playerYaw) {
        final Map<Position, Rail> railsAtPos = MinecraftClientData.getInstance().positionsToRail.get(toPosition(clickedPos));
        if (railsAtPos == null || railsAtPos.isEmpty()) {
            return null;
        }

        final Optional<Map.Entry<Position, Rail>> closestEntry = railsAtPos.entrySet().stream().min(Comparator.comparingDouble(entry ->
            net.minecraft.util.Mth.degreesDifferenceAbs(
                (float) -Math.toDegrees(Math.atan2(entry.getKey().getX() - clickedPos.getX(), entry.getKey().getZ() - clickedPos.getZ())),
                playerYaw
            )
        ));
        if (closestEntry.isEmpty()) {
            return null;
        }

        final BlockPos posEnd = new BlockPos(
            (int) closestEntry.get().getKey().getX(),
            (int) closestEntry.get().getKey().getY(),
            (int) closestEntry.get().getKey().getZ()
        );
        return new RailSelection(clickedPos, posEnd, closestEntry.get().getValue());
    }

    public static Position toPosition(BlockPos blockPos) {
        return new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static String formatPosition(Position position) {
        return String.format("(%d, %d, %d)", position.getX(), position.getY(), position.getZ());
    }

    public static String formatBlockPos(BlockPos blockPos) {
        return String.format("(%d, %d, %d)", blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private static Rail findRail(BlockPos posStart, BlockPos posEnd) {
        final Map<Position, Rail> railsAtPos = MinecraftClientData.getInstance().positionsToRail.get(toPosition(posStart));
        if (railsAtPos == null) {
            return null;
        }
        return railsAtPos.get(toPosition(posEnd));
    }

    private static PlatformMatch findPlatformMatch(BlockPos pos1, BlockPos pos2) {
        for (Platform platform : MinecraftClientData.getInstance().platforms) {
            if (platform.containsPos(toPosition(pos1)) && platform.containsPos(toPosition(pos2))) {
                return new PlatformMatch(platform.getId(), platform.getDwellTime());
            }
        }
        return new PlatformMatch(0, 0);
    }

    public static PathValidationError validatePathProgress(List<PathData> pathData) {
        if (pathData.isEmpty()) {
            return null;
        }
        if (pathData.get(0).getSavedRailBaseId() == 0) {
            return new PathValidationError("gui.rte.rail_path_creator.error.must_start_with_platform");
        }

        boolean lastTurnBack = false;
        Angle lastEndAngle = null;

        for (int i = 0; i < pathData.size(); i++) {
            final PathData current = pathData.get(i);
            final Rail currentRail = current.getRail();
            if (isOneWayRail(currentRail)) {
                return new PathValidationError("gui.rte.rail_path_creator.error.one_way");
            }

            final OrderedAngles orderedAngles = getOrderedAngles(current);
            if (i > 0) {
                final PathData previous = pathData.get(i - 1);
                if (previous.isOppositeRail(current)) {
                    if (i == 1) {
                        return new PathValidationError("gui.rte.rail_path_creator.error.cannot_turn_back_at_start");
                    }
                    if (!(previous.getRail().canTurnBack() || previous.getRail().isPlatform() || currentRail.canTurnBack() || currentRail.isPlatform())) {
                        return new PathValidationError("gui.rte.rail_path_creator.error.illegal_turn_back");
                    }
                    if (lastTurnBack) {
                        return new PathValidationError("gui.rte.rail_path_creator.error.repeatedly_turn_back");
                    }
                    lastTurnBack = true;
                } else {
                    lastTurnBack = false;
                    if (lastEndAngle != null && orderedAngles != null && getAngleDifference(lastEndAngle, orderedAngles.startAngle) < 135) {
                        return new PathValidationError("gui.rte.rail_path_creator.error.angle");
                    }
                }
            }

            if (orderedAngles != null) {
                lastEndAngle = orderedAngles.endAngle;
            }
        }

        return null;
    }

    private static void syncHoldingItem(Hand hand) {
        InitClient.REGISTRY_CLIENT.sendPacketToServer(new PacketUpdateHoldingItem(hand));
    }

    private static void sendMessage(PlayerEntity player, String key, Object... args) {
        player.sendMessage(new Text(TextHelper.translatable(key, args).data), true);
    }

    public static final class RailSelection {
        public final BlockPos posStart;
        public final BlockPos posEnd;
        public final Rail rail;

        private RailSelection(BlockPos posStart, BlockPos posEnd, Rail rail) {
            this.posStart = posStart;
            this.posEnd = posEnd;
            this.rail = rail;
        }
    }

    private static final class PlatformMatch {
        private final long platformId;
        private final long dwellTime;

        private PlatformMatch(long platformId, long dwellTime) {
            this.platformId = platformId;
            this.dwellTime = dwellTime;
        }
    }

    public static final class PathValidationError {
        public final String translationKey;

        private PathValidationError(String translationKey) {
            this.translationKey = translationKey;
        }
    }

    private static boolean isOneWayRail(Rail rail) {
        return rail.getSpeedLimitKilometersPerHour(false) == 0 || rail.getSpeedLimitKilometersPerHour(true) == 0;
    }

    private static OrderedAngles getOrderedAngles(PathData pathData) {
        final Rail rail = pathData.getRail();
        if (!((Object) rail instanceof RailExtraSupplier extra)) {
            return null;
        }

        final Position orderedStart = pathData.getOrderedPosition1();
        final Position orderedEnd = pathData.getOrderedPosition2();
        if (samePosition(orderedStart, extra.getExtraPosition1()) && samePosition(orderedEnd, extra.getExtraPosition2())) {
            return new OrderedAngles(extra.getExtraAngle1(), extra.getExtraAngle2());
        }
        if (samePosition(orderedStart, extra.getExtraPosition2()) && samePosition(orderedEnd, extra.getExtraPosition1())) {
            return new OrderedAngles(extra.getExtraAngle2(), extra.getExtraAngle1());
        }
        return null;
    }

    private static boolean samePosition(Position left, Position right) {
        return left != null && right != null
            && left.getX() == right.getX()
            && left.getY() == right.getY()
            && left.getZ() == right.getZ();
    }

    private static float getAngleDifference(Angle first, Angle second) {
        final float difference = Math.abs(first.angleDegrees - second.angleDegrees) % 360;
        return difference > 180 ? 360 - difference : difference;
    }

    private static final class OrderedAngles {
        private final Angle startAngle;
        private final Angle endAngle;

        private OrderedAngles(Angle startAngle, Angle endAngle) {
            this.startAngle = startAngle;
            this.endAngle = endAngle;
        }
    }

    private static PathData copyPathDataWithDwellTime(PathData pathData, long dwellTime) {
        return new PathData(
            pathData.getRail(),
            pathData.getSavedRailBaseId(),
            dwellTime,
            pathData.getStopIndex(),
            pathData.getOrderedPosition1(),
            pathData.getOrderedPosition2()
        );
    }
}
