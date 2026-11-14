package cn.jstxjf_.rte.mapping.mtr;

import cn.jstxjf_.rte.mapping.holder.ActionResult;
import cn.jstxjf_.rte.mapping.holder.BlockPos;
import cn.jstxjf_.rte.mapping.holder.BlockState;
import cn.jstxjf_.rte.mapping.holder.Hand;
import cn.jstxjf_.rte.mapping.holder.ItemSettings;
import cn.jstxjf_.rte.mapping.holder.ItemStack;
import cn.jstxjf_.rte.mapping.holder.ItemUsageContext;
import cn.jstxjf_.rte.mapping.holder.PlayerEntity;
import cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity;
import cn.jstxjf_.rte.mapping.holder.World;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.mtr.core.data.Rail;
import org.mtr.core.data.TransportMode;
import org.mtr.core.tool.Angle;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class ItemNodeModifierBase extends org.mtr.item.ItemNodeModifierBase {

	public ItemNodeModifierBase(boolean forNonContinuousMovementNode, boolean forContinuousMovementNode, boolean forAirplaneNode, boolean isConnector, ItemSettings itemSettings) {
		super(forNonContinuousMovementNode, forContinuousMovementNode, forAirplaneNode, isConnector, itemSettings.data);
	}

	public ActionResult useOnBlock2(ItemUsageContext context) {
		return ActionResult.convert(super.useOn(context.data));
	}

	@Override
	public final net.minecraft.world.InteractionResult useOn(UseOnContext context) {
		return useOnBlock2(new ItemUsageContext(context)).data;
	}

	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}

	@Override
	public final InteractionResultHolder<net.minecraft.world.item.ItemStack> use(Level world, Player user, InteractionHand hand) {
		useWithoutResult(new World(world), new PlayerEntity(user), Hand.convert(hand));
		return super.use(world, user, hand);
	}

	protected boolean clickCondition(ItemUsageContext context) {
		return super.clickCondition(context.data);
	}

	@Override
	protected final boolean clickCondition(UseOnContext context) {
		return clickCondition(new ItemUsageContext(context));
	}

	protected abstract void onConnect(World world, ItemStack stack, TransportMode transportMode, BlockState stateStart, BlockState stateEnd, BlockPos posStart, BlockPos posEnd, Angle facingStart, Angle facingEnd, @Nullable ServerPlayerEntity player);

	protected abstract void onRemove(World world, BlockPos posStart, BlockPos posEnd, @Nullable ServerPlayerEntity player);

	@Override
	protected final void onConnect(Level world, net.minecraft.world.item.ItemStack stack, TransportMode transportMode, net.minecraft.world.level.block.state.BlockState stateStart, net.minecraft.world.level.block.state.BlockState stateEnd, net.minecraft.core.BlockPos posStart, net.minecraft.core.BlockPos posEnd, Angle facingStart, Angle facingEnd, @Nullable ServerPlayer player) {
		onConnect(new World(world), new ItemStack(stack), transportMode, new BlockState(stateStart), new BlockState(stateEnd), new BlockPos(posStart), new BlockPos(posEnd), facingStart, facingEnd, player == null ? null : new ServerPlayerEntity(player));
	}

	@Override
	protected final void onRemove(Level world, net.minecraft.core.BlockPos posStart, net.minecraft.core.BlockPos posEnd, @Nullable ServerPlayer player) {
		onRemove(new World(world), new BlockPos(posStart), new BlockPos(posEnd), player == null ? null : new ServerPlayerEntity(player));
	}

	public static void getRail(World world, BlockPos blockPos1, BlockPos blockPos2, @Nullable ServerPlayerEntity serverPlayerEntity, Consumer<Rail> consumer) {
		org.mtr.item.ItemNodeModifierBase.getRail(world.data, blockPos1.data, blockPos2.data, serverPlayerEntity == null ? null : serverPlayerEntity.data, consumer);
	}
}
