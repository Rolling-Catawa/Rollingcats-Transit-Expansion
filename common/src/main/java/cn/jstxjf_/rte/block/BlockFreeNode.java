package cn.jstxjf_.rte.block;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockRenderType;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.LivingEntity;
import org.mtr.mapping.holder.ShapeContext;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityExtension;

public class BlockFreeNode extends BlockDirectNode {

    private static final ThreadLocal<PlacementData> PLACEMENT_DATA = new ThreadLocal<>();

    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getEntityblockAnimatedMapped();
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final BlockPos blockPos = context.getBlockPos();
        final Vec3 hitPos = cn.jstxjf_.rte.bridge.MtrTypes.vec(context.getHitPos());
        final Direction side = context.getSide();
        PLACEMENT_DATA.set(new PlacementData(
                hitPos.x - blockPos.getX() - 0.5,
                hitPos.y - blockPos.getY() - 0.125,
                hitPos.z - blockPos.getZ() - 0.5,
                side == Direction.UP ? 0 : side == Direction.DOWN ? 180 : -90,
                side.getId()
        ));
        return super.getPlacementState2(context);
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced2(world, pos, state, placer, itemStack);
        final PlacementData placementData = PLACEMENT_DATA.get();
        PLACEMENT_DATA.remove();
        if (placementData == null) return;
        final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.data instanceof BlockEntityFreeNode freeNode) {
            freeNode.setOffsetsLocal(placementData.x, placementData.y, placementData.z);
            freeNode.setPitchLocal(placementData.pitch);
            freeNode.setAttachmentFaceLocal(placementData.face);
            freeNode.sendUpdateC2S();
        }
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityFreeNode(pos, state);
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    private record PlacementData(double x, double y, double z, double pitch, int face) {
    }

    public static class BlockEntityFreeNode extends BlockEntityDirectNode {
        public BlockEntityFreeNode(BlockPos pos, BlockState state) {
            super(pos, state, true);
        }
    }
}
