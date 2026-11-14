package cn.jstxjf_.rte.block;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEConfig;
import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.data.EyeCandyProperties;
import cn.jstxjf_.rte.data.EyeCandyRegistry;
import cn.jstxjf_.rte.data.ShapeSerializer;
import cn.jstxjf_.rte.scripting.ScriptHolderBase;
import cn.jstxjf_.rte.scripting.eyecandy.EyeCandyScriptContext;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.network.PacketUpdateBlockEntity;
import cn.jstxjf_.rte.network.util.StringMapSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.StringUtils;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.BlockWithEntity;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.Items;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.TicketSystem;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.function.ToIntFunction;

public class BlockEyeCandy extends BlockExtension implements BlockWithEntity, DirectionHelper {

    public static final org.mtr.mapping.holder.DirectionProperty FACING = DirectionHelper.FACING;
    //#if MC>=12101
    public static final EnumProperty<TicketSystem.EnumTicketBarrierOpen> OPEN = new EnumProperty<>(org.mtr.block.BlockTicketBarrier.OPEN);
    //#else
    public static final EnumProperty<TicketSystem.EnumTicketBarrierOpen> OPEN = org.mtr.mod.block.BlockTicketBarrier.OPEN;
    //#endif
    public static final org.mtr.mapping.holder.IntegerProperty LEVEL = org.mtr.mapping.holder.IntegerProperty.of("level", 0, 15);
    public static final ToIntFunction<net.minecraft.world.level.block.state.BlockState> LIGHT_EMISSION = state -> state.getValue(LEVEL.data);

    public BlockEyeCandy() {
        super(new BlockSettings(cn.jstxjf_.rte.compat.McCompat.defaultProperties()
            .strength(2).lightLevel(LIGHT_EMISSION).dynamicShape()));
    }

    public BlockEyeCandy(BlockSettings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        return getDefaultState2()
            .with(new Property<>(FACING.data), context.getPlayerFacing().data)
            .with(new Property<>(LEVEL.data), 0)
            .with(new Property<>(OPEN.data), TicketSystem.EnumTicketBarrierOpen.CLOSED);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(LEVEL);
        properties.add(OPEN);
    }

    @Nonnull
    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player == null) {
            return ActionResult.PASS;
        }

        if (player.isHolding(Items.BRUSH.get())) {
            if (world.isClient()) {
                final Minecraft minecraft = Minecraft.getInstance();
                cn.jstxjf_.rte.gui.EyeCandyScreen.open(pos.data, minecraft.screen);
            }
            return ActionResult.SUCCESS;
        }

        if (world.isClient()) {
            final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null && blockEntity.data instanceof BlockEntityEyeCandy eyeCandy) {
                eyeCandy.tryCallUseFunctionAsync((net.minecraft.world.entity.player.Player) player.data);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Nonnull
    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state, world, pos, true);
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state, world, pos, false);
    }

    @Nonnull
    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public void onEntityCollision2(BlockState state, World world, BlockPos blockPos, Entity entity) {
        final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity == null || !(blockEntity.data instanceof BlockEntityEyeCandy eyeCandy) || !eyeCandy.isTicketBarrier()) {
            return;
        }

        if (!world.isClient() && PlayerEntity.isInstance(entity)) {
            final Direction facing = IBlock.getStatePropertySafe(state, FACING);
            final net.minecraft.world.phys.Vec3 playerPosRotated = cn.jstxjf_.rte.bridge.MtrTypes.vec(entity.getPos()
                .subtract(blockPos.getX() + 0.5, 0, blockPos.getZ() + 0.5)
                .rotateY((float) Math.toRadians(facing.asRotation())));
            final TicketSystem.EnumTicketBarrierOpen open = IBlock.getStatePropertySafe(state, new Property<>(OPEN.data));

            if ((open == TicketSystem.EnumTicketBarrierOpen.OPEN || open == TicketSystem.EnumTicketBarrierOpen.OPEN_CONCESSIONARY) && playerPosRotated.z > 0) {
                world.setBlockState(blockPos, state.with(new Property<>(OPEN.data), TicketSystem.EnumTicketBarrierOpen.CLOSED));
            } else if (open == TicketSystem.EnumTicketBarrierOpen.CLOSED && playerPosRotated.z < 0) {
                final BlockPos blockPosCopy = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                world.setBlockState(blockPosCopy, state.with(new Property<>(OPEN.data), TicketSystem.EnumTicketBarrierOpen.PENDING));
                TicketSystem.passThrough(
                    //#if MC>=12101
                    world.data,
                    blockPosCopy.data,
                    (net.minecraft.world.entity.player.Player) entity.data,
                    eyeCandy.isEntrance,
                    !eyeCandy.isEntrance,
                    org.mtr.registry.SoundEvents.TICKET_BARRIER.get(),
                    org.mtr.registry.SoundEvents.TICKET_BARRIER_CONCESSIONARY.get(),
                    org.mtr.registry.SoundEvents.TICKET_BARRIER.get(),
                    org.mtr.registry.SoundEvents.TICKET_BARRIER_CONCESSIONARY.get(),
                    //#else
                    world,
                    blockPosCopy,
                    PlayerEntity.cast(entity),
                    eyeCandy.isEntrance,
                    !eyeCandy.isEntrance,
                    org.mtr.mod.SoundEvents.TICKET_BARRIER.get(),
                    org.mtr.mod.SoundEvents.TICKET_BARRIER_CONCESSIONARY.get(),
                    org.mtr.mod.SoundEvents.TICKET_BARRIER.get(),
                    org.mtr.mod.SoundEvents.TICKET_BARRIER_CONCESSIONARY.get(),
                    //#endif
                    null,
                    false,
                    newOpen -> {
                        world.setBlockState(blockPosCopy, state.with(new Property<>(OPEN.data), newOpen));
                        if (newOpen != TicketSystem.EnumTicketBarrierOpen.CLOSED && !hasScheduledBlockTick(world, blockPosCopy, new Block(this))) {
                            scheduleBlockTick(world, blockPosCopy, new Block(this), 40);
                        }
                    }
                );
            }
        }
    }

    @Override
    public void scheduledTick2(BlockState state, ServerWorld world, BlockPos pos, org.mtr.mapping.holder.Random random) {
        world.setBlockState(pos, state.with(new Property<>(OPEN.data), TicketSystem.EnumTicketBarrierOpen.CLOSED));
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getEntityblockAnimatedMapped();
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityEyeCandy(pos, state);
    }

    private static int rte$shapeRotation(BlockState state) {
        final float modelYaw = 180F - IBlock.getStatePropertySafe(state, FACING).asRotation();
        int rotation = Math.round(-modelYaw) % 360;
        if (rotation < 0) {
            rotation += 360;
        }
        return rotation;
    }

    private VoxelShape getShape(BlockState state, BlockView world, BlockPos pos, boolean outline) {
        final org.mtr.mapping.holder.BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.data instanceof BlockEntityEyeCandy eyeCandy) {
            try {
                return new VoxelShape(ShapeSerializer.getShape(outline ? eyeCandy.getShape() : eyeCandy.getCollisionShape(), rte$shapeRotation(state)));
            } catch (Exception e) {
                RTE.LOGGER.warn("Failed to read EyeCandy shape at " + pos + ": " + e.getMessage());
            }
        }
        return outline ? Block.createCuboidShape(0, 0, 0, 16, 16, 16) : Block.createCuboidShape(0, 0, 0, 16, 32, 16);
    }

    public static class BlockEntityEyeCandy extends BlockEntityExtension {

        public String prefabId = null;
        public float translateX = 0, translateY = 0, translateZ = 0;
        public float rotateX = 0, rotateY = 0, rotateZ = 0;
        public float scaleX = 1, scaleY = 1, scaleZ = 1;
        public boolean fullLight = false;
        private Map<String, String> customConfigs;
        private Map<String, ConfigResponder> customResponders;
        public EyeCandyScriptContext scriptContext = null;
        public boolean asPlatform = false;
        public float doorValue = 0;
        public boolean doorTarget = false;
        private String shape = "0, 0, 0, 16, 16, 16";
        private String collisionShape = "0, 0, 0, 0, 0, 0";
        public boolean fixedMatrix = false;
        private int lightLevel = 0;
        public boolean isTicketBarrier = false;
        public boolean isEntrance = false;

        public BlockEntityEyeCandy(BlockPos pos, BlockState state) {
            super(RTEBlockEntityTypes.EYE_CANDY.get(), pos, state);
            customConfigs = new HashMap<>();
            customResponders = new HashMap<>();
        }

        @Override
        public void readCompoundTag(CompoundTag tag) {
            net.minecraft.nbt.CompoundTag compoundTag = tag.data;
            String id = compoundTag.getString("prefabId");
            if (StringUtils.isEmpty(id)) id = null;
            setPrefabId(id);
            fullLight = compoundTag.getBoolean("fullLight");
            try {
                if (compoundTag.contains("customConfigs")) {
                    customConfigs = StringMapSerializer.deserialize(compoundTag.getByteArray("customConfigs"));
                } else if (compoundTag.contains("data")) {
                    customConfigs = StringMapSerializer.deserialize(compoundTag.getByteArray("data"));
                }
            } catch (IOException ignored) {}
            translateX = compoundTag.contains("translateX") ? compoundTag.getFloat("translateX") : 0;
            translateY = compoundTag.contains("translateY") ? compoundTag.getFloat("translateY") : 0;
            translateZ = compoundTag.contains("translateZ") ? compoundTag.getFloat("translateZ") : 0;
            rotateX = compoundTag.contains("rotateX") ? compoundTag.getFloat("rotateX") : 0;
            rotateY = compoundTag.contains("rotateY") ? compoundTag.getFloat("rotateY") : 0;
            rotateZ = compoundTag.contains("rotateZ") ? compoundTag.getFloat("rotateZ") : 0;
            scaleX = compoundTag.contains("scaleX") ? compoundTag.getFloat("scaleX") : 1;
            scaleY = compoundTag.contains("scaleY") ? compoundTag.getFloat("scaleY") : 1;
            scaleZ = compoundTag.contains("scaleZ") ? compoundTag.getFloat("scaleZ") : 1;
            asPlatform = compoundTag.getBoolean("asPlatform");
            shape = compoundTag.contains("shape") ? compoundTag.getString("shape") : "0, 0, 0, 16, 16, 16";
            collisionShape = compoundTag.contains("collisionShape") ? compoundTag.getString("collisionShape") : "0, 0, 0, 0, 0, 0";
            fixedMatrix = compoundTag.getBoolean("fixedMatrix");
            lightLevel = compoundTag.contains("lightLevel") ? compoundTag.getInt("lightLevel") : 0;
            isTicketBarrier = compoundTag.getBoolean("isTicketBarrier");
            isEntrance = compoundTag.getBoolean("isEntrance");
        }

        @Override
        public void writeCompoundTag(CompoundTag tag) {
            net.minecraft.nbt.CompoundTag compoundTag = tag.data;
            compoundTag.putString("prefabId", prefabId == null ? "" : prefabId);
            compoundTag.putBoolean("fullLight", fullLight);
            try {
                compoundTag.putByteArray("customConfigs", StringMapSerializer.serializeToByteArray(customConfigs));
            } catch (IOException ignored) {}
            compoundTag.putFloat("translateX", translateX);
            compoundTag.putFloat("translateY", translateY);
            compoundTag.putFloat("translateZ", translateZ);
            compoundTag.putFloat("rotateX", rotateX);
            compoundTag.putFloat("rotateY", rotateY);
            compoundTag.putFloat("rotateZ", rotateZ);
            compoundTag.putFloat("scaleX", scaleX);
            compoundTag.putFloat("scaleY", scaleY);
            compoundTag.putFloat("scaleZ", scaleZ);
            compoundTag.putBoolean("asPlatform", asPlatform);
            compoundTag.putString("shape", shape);
            compoundTag.putString("collisionShape", collisionShape);
            compoundTag.putBoolean("fixedMatrix", fixedMatrix);
            compoundTag.putInt("lightLevel", lightLevel);
            compoundTag.putBoolean("isTicketBarrier", isTicketBarrier);
            compoundTag.putBoolean("isEntrance", isEntrance);
        }

        public Matrix4f getBaseMatrix() {
            net.minecraft.world.level.block.entity.BlockEntity nativeBE = (net.minecraft.world.level.block.entity.BlockEntity)(Object)this;
            final net.minecraft.core.Direction facing = nativeBE.getBlockState().getValue(FACING.data);
            final Matrix4f matrix = new Matrix4f();
            net.minecraft.core.BlockPos pos = nativeBE.getBlockPos();
            matrix.translate(pos.getX() + 0.5F + translateX, pos.getY() + translateY, pos.getZ() + 0.5F + translateZ);
            matrix.rotateY((float) Math.toRadians(180F - facing.toYRot()));
            matrix.rotateX(rotateX);
            matrix.rotateY(rotateY);
            matrix.rotateZ(rotateZ);
            matrix.scale(scaleX, scaleY, scaleZ);
            return matrix;
        }

        public AABB getRenderBoundingBox() {
            return new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        public void setPrefabId(String new1) {
            String old = prefabId;
            prefabId = new1;
            if ((old == null && prefabId != null) || (old != null && !old.equals(prefabId))) {
                restore();
            } else if (getProperties().script != null && scriptContext == null) {
                scriptContext = new EyeCandyScriptContext(this);
            }
        }

        public void restore() {
            customConfigs.clear();
            customResponders.clear();
            if (scriptContext != null) {
                scriptContext.disposeForReload = true;
            }
            scriptContext = null;
            EyeCandyProperties properties = getProperties();
            setShape(properties.shape);
            setCollisionShape(properties.collisionShape);
            fixedMatrix = properties.fixedMatrix;
            setLightLevel(properties.lightLevel);
            isTicketBarrier = properties.isTicketBarrier;
            isEntrance = properties.isEntrance;
            asPlatform = properties.asPlatform;
            if (properties.script != null) {
                scriptContext = new EyeCandyScriptContext(this);
            }
        }

        public void setLightLevel(int lightLevel) {
            if (lightLevel >= 0 && lightLevel <= 15) {
                this.lightLevel = lightLevel;
            }
        }

        public int getLightLevel() { return lightLevel; }

        public boolean setShape(String shape) {
            try {
                if (ShapeSerializer.isValid(shape, (int) getBlockYRot())) {
                    if (!Objects.equals(this.shape, shape)) { this.shape = shape; return true; }
                }
            } catch (Exception e) { RTE.LOGGER.error("Error setting shape: " + e); }
            return false;
        }

        public boolean setCollisionShape(String collisionShape) {
            try {
                if (ShapeSerializer.isValid(collisionShape, (int) getBlockYRot())) {
                    if (!Objects.equals(this.collisionShape, collisionShape)) { this.collisionShape = collisionShape; return true; }
                }
            } catch (Exception e) { RTE.LOGGER.error("Error setting collision shape: " + e); }
            return false;
        }

        public String getShape() { return shape; }
        public String getCollisionShape() { return collisionShape; }

        public net.minecraft.core.BlockPos getWorldPos() {
            return ((net.minecraft.world.level.block.entity.BlockEntity)(Object)this).getBlockPos();
        }

        public Vector3f getWorldPosVector3f() {
            net.minecraft.core.BlockPos p = getWorldPos();
            return new Vector3f(p.getX(), p.getY(), p.getZ());
        }

        public Vector3f getTransformPosVector3f() {
            net.minecraft.core.BlockPos p = getWorldPos();
            return new Vector3f(p.getX() + translateX, p.getY() + translateY, p.getZ() + translateZ);
        }

        public void sendUpdateC2S() {
            final net.minecraft.world.level.block.entity.BlockEntity nativeBlockEntity = (net.minecraft.world.level.block.entity.BlockEntity) (Object) this;
            if (nativeBlockEntity.getLevel() != null && nativeBlockEntity.getLevel().isClientSide()) {
                PacketUpdateBlockEntity.sendUpdateC2S(this);
            } else {
                this.markDirty2();
            }
        }

        public float getBlockYRot() {
            try {
                return ((net.minecraft.world.level.block.entity.BlockEntity)(Object)this).getBlockState().getValue(FACING.data).toYRot();
            } catch (Exception e) { return 0; }
        }

        public void setDoorValue(float value) { doorValue = value; }
        public void setDoorTarget(boolean target) { doorTarget = target; }
        public boolean isPlatform() { return asPlatform; }
        public boolean isTicketBarrier() { return isTicketBarrier; }
        public boolean isOpened() {
            try {
                final BlockState state = getCachedState2();
                if (state == null) {
                    return false;
                }
                final TicketSystem.EnumTicketBarrierOpen open = IBlock.getStatePropertySafe(state, new Property<>(BlockEyeCandy.OPEN.data));
                return open == TicketSystem.EnumTicketBarrierOpen.OPEN || open == TicketSystem.EnumTicketBarrierOpen.OPEN_CONCESSIONARY;
            } catch (Exception e) {
                return false;
            }
        }

        public EyeCandyProperties getProperties() {
            EyeCandyProperties property = EyeCandyRegistry.getProperty(prefabId);
            return property != null ? property : EyeCandyProperties.DEFAULT;
        }

        public void tryCallUseFunctionAsync(net.minecraft.world.entity.player.Player player) {
            if (scriptContext == null) return;
            EyeCandyProperties prop = getProperties();
            ScriptHolderBase scriptHolder = prop.script;
            if (scriptHolder == null) return;
            final cn.jstxjf_.rte.scripting.ScriptUseEvent event =
                cn.jstxjf_.rte.scripting.ScriptUseResolver.resolve(scriptContext, getBaseMatrix(), player, 0, 5.0);
            scriptHolder.tryCallFunctionAsync("use", scriptContext, null, player, event);
        }

        public Map<String, String> getCustomConfigs() { return customConfigs; }
        public Map<String, ConfigResponder> getCustomResponders() { return customResponders; }
        public String getCustomConfig(String key) { return customConfigs.get(key); }
        public void registerCustomConfig(ConfigResponder responder) {
            ConfigResponder.registerInto(customConfigs, customResponders, responder);
        }
        public void removeCustomConfig(String key) { customConfigs.remove(key); customResponders.remove(key); }
        public void putCustomConfig(String key, String value) { customConfigs.put(key, value); }
    }
}
