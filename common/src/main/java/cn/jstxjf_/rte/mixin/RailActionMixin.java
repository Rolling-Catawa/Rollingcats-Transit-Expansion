package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.data.RailExtraSupplier;
import org.mtr.core.data.Rail;
import org.mtr.core.data.RailMath;
import org.mtr.core.tool.Vector;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Blocks;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.SlabType;
import org.mtr.mapping.mapper.SlabBlockExtension;
import org.mtr.mod.Init;
import org.mtr.mod.data.RailAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(value = RailAction.class, remap = false)
public abstract class RailActionMixin {

    @Shadow(remap = false)
    protected abstract boolean create(boolean includeMiddle, Consumer<Vector> consumer);

    @Shadow(remap = false)
    private boolean isSlab;

    @Shadow(remap = false)
    private BlockState state;

    @Shadow(remap = false)
    private ServerWorld serverWorld;

    @Shadow(remap = false)
    private Rail rail;

    @Shadow(remap = false)
    private static boolean canPlace(ServerWorld serverWorld, BlockPos pos) {
        return true;
    }

    @Unique
    private final Map<Long, Integer> rte$placedState = new HashMap<>();

    @Overwrite(remap = false)
    private boolean createBridge() {
        return this.create(false, editPos -> {
            final double refY = editPos.y;
            final BlockPos pos = Init.newBlockPos(editPos.x, refY, editPos.z);
            final boolean isTopHalf = refY - Math.floor(refY) >= 0.5;

            final BlockPos placePos;
            final BlockPos airPos;
            final BlockState placeState;
            if (this.isSlab && isTopHalf) {
                placePos = pos;
                airPos = pos.up();
                placeState = this.state.with(new Property<>(SlabBlockExtension.TYPE), SlabType.BOTTOM.data);
            } else {
                placePos = pos.down();
                airPos = pos;
                placeState = this.isSlab ? this.state.with(new Property<>(SlabBlockExtension.TYPE), SlabType.DOUBLE.data) : this.state;
            }

            final int slabPriority = isTopHalf ? 2 : 1;
            final long placePosLong = placePos.data.asLong();
            if (rte$placedState.getOrDefault(placePosLong, 0) < slabPriority && canPlace(serverWorld, placePos)) {
                serverWorld.setBlockState(placePos, placeState);
                rte$placedState.put(placePosLong, slabPriority);
            }

            final long airPosLong = airPos.data.asLong();
            if (rte$placedState.getOrDefault(airPosLong, 0) < 3 && canPlace(serverWorld, airPos)) {
                serverWorld.setBlockState(airPos, Blocks.getAirMapped().getDefaultState());
                rte$placedState.put(airPosLong, 3);
            }
        });
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/mtr/core/data/RailMath;getLength()D"))
    private double rte$getLength(RailMath railMath) {
        return ((Object) rail) instanceof RailExtraSupplier supplier ? supplier.getLength() : railMath.getLength();
    }

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lorg/mtr/core/data/RailMath;getPosition(DZ)Lorg/mtr/core/tool/Vector;"))
    private Vector rte$getPosition(RailMath railMath, double value, boolean reverse) {
        if (((Object) rail) instanceof RailExtraSupplier supplier) {
            final net.minecraft.world.phys.Vec3 vector = supplier.getPosition(value);
            return new Vector(vector.x, vector.y, vector.z);
        }
        return railMath.getPosition(value, reverse);
    }

}
