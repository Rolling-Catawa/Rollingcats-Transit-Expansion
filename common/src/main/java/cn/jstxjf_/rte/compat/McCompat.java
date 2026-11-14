package cn.jstxjf_.rte.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class McCompat {

    private McCompat() {
    }

    public static ResourceLocation id(String namespace, String path) {
        //#if MC>=12101
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //#else
        return new ResourceLocation(namespace, path);
        //#endif
    }

    public static ResourceLocation id(String location) {
        //#if MC>=12101
        return ResourceLocation.parse(location);
        //#else
        return new ResourceLocation(location);
        //#endif
    }

    public static net.minecraft.nbt.CompoundTag saveBlockEntity(net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
        //#if MC>=12101
        return blockEntity.saveWithoutMetadata(blockEntity.getLevel() == null ? net.minecraft.core.RegistryAccess.EMPTY : blockEntity.getLevel().registryAccess());
        //#else
        return blockEntity.saveWithoutMetadata();
        //#endif
    }

    public static void loadBlockEntity(net.minecraft.world.level.block.entity.BlockEntity blockEntity, net.minecraft.nbt.CompoundTag tag) {
        //#if MC>=12101
        blockEntity.loadWithComponents(tag, blockEntity.getLevel() == null ? net.minecraft.core.RegistryAccess.EMPTY : blockEntity.getLevel().registryAccess());
        //#else
        blockEntity.load(tag);
        //#endif
    }

    public static ServerLevel serverLevel(ServerPlayer player) {
        //#if MC>=12000
        return player.serverLevel();
        //#else
        return player.getLevel();
        //#endif
    }

    public static Level level(Entity entity) {
        //#if MC>=12000
        return entity.level();
        //#else
        return entity.level;
        //#endif
    }

    public static boolean onGround(Entity entity) {
        //#if MC>=12000
        return entity.onGround();
        //#else
        return entity.isOnGround();
        //#endif
    }

    public static BlockPos blockPos(double x, double y, double z) {
        //#if MC>=11903
        return BlockPos.containing(x, y, z);
        //#else
        return new BlockPos(x, y, z);
        //#endif
    }

    public static BlockPos blockPos(Vec3 position) {
        return blockPos(position.x, position.y, position.z);
    }

    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties defaultProperties() {
        //#if MC>=12000
        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of();
        //#else
        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.STONE);
        //#endif
    }

    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties stoneProperties() {
        //#if MC>=12000
        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.STONE);
        //#else
        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.STONE);
        //#endif
    }

    public static boolean canBeReplaced(net.minecraft.world.level.block.state.BlockState state) {
        //#if MC>=11904
        return state.canBeReplaced();
        //#else
        return state.getMaterial().isReplaceable();
        //#endif
    }

    public static SoundEvent soundEvent(ResourceLocation location) {
        //#if MC>=11903
        return SoundEvent.createVariableRangeEvent(location);
        //#else
        return new SoundEvent(location);
        //#endif
    }
}
