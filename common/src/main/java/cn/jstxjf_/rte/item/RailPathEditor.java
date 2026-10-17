package cn.jstxjf_.rte.item;

import cn.jstxjf_.rte.gui.RailPathEditorScreen;
import cn.jstxjf_.rte.network.PacketUpdateHoldingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Screen;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.mapper.TextHelper;
import cn.jstxjf_.rte.util.RailRebuildHelper;
import org.mtr.core.data.Position;
import org.mtr.mod.InitClient;
import org.mtr.mod.client.MinecraftClientData;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class RailPathEditor extends ItemExtension {

    private static final String KEY_START = "start";
    private static final String KEY_END = "end";

    public RailPathEditor(ItemSettings settings) {
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

        final RailSelection railSelection = findRail(context.getBlockPos(), ((net.minecraft.world.entity.player.Player) player.data).getYRot());
        if (railSelection == null) {
            return ActionResult.SUCCESS;
        }

        final ItemStack stack = context.getStack();
        final CompoundTag tag = stack.getOrCreateTag();
        tag.putLong(KEY_START, railSelection.posStart.asLong());
        tag.putLong(KEY_END, railSelection.posEnd.asLong());

        player.sendMessage(new Text(TextHelper.translatable(
            "tooltip.rte.rail_path_editor.success_update",
            railSelection.posStart.getX(), railSelection.posStart.getY(), railSelection.posStart.getZ(),
            railSelection.posEnd.getX(), railSelection.posEnd.getY(), railSelection.posEnd.getZ()
        ).data), true);
        cn.jstxjf_.rte.bridge.network.RteClientNetwork.sendToServer(new PacketUpdateHoldingItem(context.getHand()));
        return ActionResult.SUCCESS;
    }

    @Override
    public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            return;
        }

        final ItemStack stack = user.getStackInHand(hand);
        if (stack.isEmpty()) {
            return;
        }

        final CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(KEY_START) || !tag.contains(KEY_END)) {
            user.sendMessage(new Text(TextHelper.translatable("tooltip.rte.rail_path_editor.no_data").data), true);
            return;
        }

        final BlockPos posStart = BlockPos.fromLong(tag.getLong(KEY_START));
        final BlockPos posEnd = BlockPos.fromLong(tag.getLong(KEY_END));
        final Rail rail = findRail(posStart, posEnd);
        if (rail == null) {
            user.sendMessage(new Text(TextHelper.translatable("tooltip.rte.rail_path_editor.data_not_found").data), true);
            return;
        }

        final Screen screen = RailPathEditorScreen.createScreen(posStart, posEnd, rail, null);
        if (screen != null) {
            MinecraftClient.getInstance().openScreen(screen);
        }
    }

    private static RailSelection findRail(BlockPos clickedPos, float playerYaw) {
        final Position posStart = new Position(clickedPos.getX(), clickedPos.getY(), clickedPos.getZ());
        final var railsAtPos = MinecraftClientData.getInstance().positionsToRail.get(posStart);
        if (railsAtPos == null || railsAtPos.isEmpty()) {
            return null;
        }

        final Optional<Map.Entry<Position, Rail>> closestEntry = railsAtPos.entrySet().stream().min(Comparator.comparingDouble(entry ->
            Mth.degreesDifferenceAbs((float) -Math.toDegrees(Math.atan2(entry.getKey().getX() - clickedPos.getX(), entry.getKey().getZ() - clickedPos.getZ())), playerYaw)
        ));
        if (closestEntry.isEmpty()) {
            return null;
        }

        final BlockPos posEnd = new BlockPos((int) closestEntry.get().getKey().getX(), (int) closestEntry.get().getKey().getY(), (int) closestEntry.get().getKey().getZ());
        return new RailSelection(clickedPos, posEnd, closestEntry.get().getValue());
    }

    private static Rail findRail(BlockPos posStart, BlockPos posEnd) {
        return RailRebuildHelper.findClientRail(posStart, posEnd);
    }

    private static final class RailSelection {
        private final BlockPos posStart;
        private final BlockPos posEnd;
        private final Rail rail;

        private RailSelection(BlockPos posStart, BlockPos posEnd, Rail rail) {
            this.posStart = posStart;
            this.posEnd = posEnd;
            this.rail = rail;
        }
    }
}
