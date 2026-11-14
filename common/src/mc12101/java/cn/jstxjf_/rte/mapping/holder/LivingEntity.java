package cn.jstxjf_.rte.mapping.holder;

public final class LivingEntity extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.entity.LivingEntity> {

    public LivingEntity(net.minecraft.world.entity.LivingEntity p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.LivingEntity cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new LivingEntity((net.minecraft.world.entity.LivingEntity) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.entity.LivingEntity;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isPushable() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isPushable();
    }

    public cn.jstxjf_.rte.mapping.holder.World getEntityWorld() {
        return new cn.jstxjf_.rte.mapping.holder.World(((net.minecraft.world.entity.LivingEntity) this.data).getCommandSenderWorld());
    }

    public boolean isUsingItem() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isUsingItem();
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d applyMovementInput(cn.jstxjf_.rte.mapping.holder.Vector3d p0, float p1) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.LivingEntity) this.data).handleRelativeFrictionAndCalculateMovement(((net.minecraft.world.phys.Vec3) p0.data), p1));
    }

    public void setNearbySongPlaying(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setRecordPlayingNearby(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public void tickRiding() {
        ((net.minecraft.world.entity.LivingEntity) this.data).rideTick();
    }

    public void updatePositionAndAngles(double p0, double p1, double p2, float p3, float p4) {
        ((net.minecraft.world.entity.LivingEntity) this.data).absMoveTo(p0, p1, p2, p3, p4);
    }

    public void updateTrackedHeadRotation(float p0, int p1) {
        ((net.minecraft.world.entity.LivingEntity) this.data).lerpHeadTo(p0, p1);
    }

    public final int getStingerCount() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getStingerCount();
    }

    public boolean hasPassenger(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).hasPassenger(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void setSilent(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setSilent(p0);
    }

    public static void setRenderDistanceMultiplier(double p0) {
        net.minecraft.world.entity.LivingEntity.setViewScale(p0);
    }

    public boolean isOnFire() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isOnFire();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getStackInHand(cn.jstxjf_.rte.mapping.holder.Hand p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.entity.LivingEntity) this.data).getItemInHand(p0.data));
    }

    public void addVelocity(double p0, double p1, double p2) {
        ((net.minecraft.world.entity.LivingEntity) this.data).push(p0, p1, p2);
    }

    public void setCustomName(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setCustomName((p0 == null ? null : ((net.minecraft.network.chat.Component) p0.data)));
    }

    public double offsetX(double p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getX(p0);
    }

    public double offsetZ(double p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getZ(p0);
    }

    public void setJumping(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setJumping(p0);
    }

    public boolean isSwimming() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isSwimming();
    }

    public void updatePosition(double p0, double p1, double p2) {
        ((net.minecraft.world.entity.LivingEntity) this.data).absMoveTo(p0, p1, p2);
    }

    public void setUuid(java.util.UUID p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setUUID(p0);
    }

    public float getPitch(float p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getViewXRot(p0);
    }

    public double getBodyY(double p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getY(p0);
    }

    public double getAttackDistanceScalingFactor(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getVisibilityPercent((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interactAt(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.Hand p2) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.entity.LivingEntity) this.data).interactAt(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), p2.data));
    }

    public boolean isHolding(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isHolding(((net.minecraft.world.item.Item) p0.data));
    }

    public boolean isInvisible() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isInvisible();
    }

    public cn.jstxjf_.rte.mapping.holder.Hand getActiveHand() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(((net.minecraft.world.entity.LivingEntity) this.data).getUsedItemHand());
    }

    public boolean isBaby() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isBaby();
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.SoundEvent p0, float p1, float p2) {
        ((net.minecraft.world.entity.LivingEntity) this.data).playSound(((net.minecraft.sounds.SoundEvent) p0.data), p1, p2);
    }

    public static double getRenderDistanceMultiplier() {
        return net.minecraft.world.entity.LivingEntity.getViewScale();
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer() {
        final var v0 = ((net.minecraft.world.entity.LivingEntity) this.data).getServer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.MinecraftServer(v0));
    }

    public float applyMirror(cn.jstxjf_.rte.mapping.holder.Mirror p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).mirror(p0.data);
    }

    public boolean isDescending() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isDescending();
    }

    public final void setStingerCount(int p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setStingerCount(p0);
    }

    public boolean isFallFlying() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isFallFlying();
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.entity.LivingEntity) this.data).getDisplayName());
    }

    public double squaredDistanceTo(double p0, double p1, double p2) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).distanceToSqr(p0, p1, p2);
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).distanceToSqr(((net.minecraft.world.entity.Entity) p0.data));
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).distanceToSqr(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public boolean canBeSpectated(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).broadcastToPlayer(((net.minecraft.server.level.ServerPlayer) p0.data));
    }

    public void setOnGround(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setOnGround(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag writeNbt(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.world.entity.LivingEntity) this.data).saveWithoutId(((net.minecraft.nbt.CompoundTag) p0.data)));
    }

    public void swingHand(cn.jstxjf_.rte.mapping.holder.Hand p0, boolean p1) {
        ((net.minecraft.world.entity.LivingEntity) this.data).swing(p0.data, p1);
    }

    public float getEffectiveExplosionResistance(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, cn.jstxjf_.rte.mapping.holder.FluidState p4, float p5) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getBlockExplosionResistance(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), ((net.minecraft.world.level.material.FluidState) p4.data), p5);
    }

    public boolean hasCustomName() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).hasCustomName();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getProjectileType(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.entity.LivingEntity) this.data).getProjectile(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public boolean isAttackable() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isAttackable();
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getVehicle() {
        final var v0 = ((net.minecraft.world.entity.LivingEntity) this.data).getVehicle();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public double method_29241() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getFluidJumpThreshold();
    }

    public void setHeadYaw(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setYHeadRot(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack eatFood(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.world.entity.LivingEntity) this.data).eat(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.item.ItemStack) p1.data)));
    }

    public int hashCode() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).hashCode();
    }

    public float getHealth() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getHealth();
    }

    public boolean startRiding(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).startRiding(((net.minecraft.world.entity.Entity) p0.data));
    }

    public boolean startRiding(cn.jstxjf_.rte.mapping.holder.Entity p0, boolean p1) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).startRiding(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getSleepingDirection() {
        final var v0 = ((net.minecraft.world.entity.LivingEntity) this.data).getBedOrientation();
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.Direction.convert(v0));
    }

    public void baseTick() {
        ((net.minecraft.world.entity.LivingEntity) this.data).baseTick();
    }

    public void updateSwimming() {
        ((net.minecraft.world.entity.LivingEntity) this.data).updateSwimming();
    }

    public void setNoGravity(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setNoGravity(p0);
    }

    public boolean canEquip(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).canTakeItem(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.entity.LivingEntity) this.data).getName());
    }

    public cn.jstxjf_.rte.mapping.holder.HitResult raycast(double p0, float p1, boolean p2) {
        return new cn.jstxjf_.rte.mapping.holder.HitResult(((net.minecraft.world.entity.LivingEntity) this.data).pick(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d updatePassengerForDismount(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.LivingEntity) this.data).getDismountLocationForPassenger(((net.minecraft.world.entity.LivingEntity) p0.data)));
    }

    public void setInvulnerable(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setInvulnerable(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getRootVehicle() {
        return new cn.jstxjf_.rte.mapping.holder.Entity(((net.minecraft.world.entity.LivingEntity) this.data).getRootVehicle());
    }

    public java.lang.String getUuidAsString() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getStringUUID();
    }

    public void tick() {
        ((net.minecraft.world.entity.LivingEntity) this.data).tick();
    }

    public void setSprinting(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setSprinting(p0);
    }

    public boolean isSilent() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isSilent();
    }

    public void setSwimming(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setSwimming(p0);
    }

    public void checkDespawn() {
        ((net.minecraft.world.entity.LivingEntity) this.data).checkDespawn();
    }

    public final float getHeight() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getBbHeight();
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d applyFluidMovingSpeed(double p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Vector3d p2) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.LivingEntity) this.data).getFluidFallingAdjustedMovement(p0, p1, ((net.minecraft.world.phys.Vec3) p2.data)));
    }

    public boolean isSneaking() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isShiftKeyDown();
    }

    public double getRandomBodyY() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getRandomY();
    }

    public boolean addScoreboardTag(java.lang.String p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).addTag(p0);
    }

    public boolean teleport(double p0, double p1, double p2, boolean p3) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).randomTeleport(p0, p1, p2, p3);
    }

    public boolean canExplosionDestroyBlock(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, float p4) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).shouldBlockExplode(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), p4);
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getHorizontalFacing() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.entity.LivingEntity) this.data).getDirection());
    }

    public void heal(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).heal(p0);
    }

    public void setAttacker(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setLastHurtByMob((p0 == null ? null : ((net.minecraft.world.entity.LivingEntity) p0.data)));
    }

    public final void setStuckArrowCount(int p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setArrowCount(p0);
    }

    public void setBodyYaw(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setYBodyRot(p0);
    }

    public final double getY() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getY();
    }

    public final double getX() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getX();
    }

    public final double getZ() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getZ();
    }

    public boolean hasPermissionLevel(int p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).hasPermissions(p0);
    }

    public double getEyeY() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getEyeY();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.entity.LivingEntity) this.data).blockPosition());
    }

    public double getParticleZ(double p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getRandomZ(p0);
    }

    public double getParticleX(double p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getRandomX(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interact(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.entity.LivingEntity) this.data).interact(((net.minecraft.world.entity.player.Player) p0.data), p1.data));
    }

    public java.lang.String getEntityName() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getScoreboardName();
    }

    public void updateVelocity(float p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        ((net.minecraft.world.entity.LivingEntity) this.data).moveRelative(p0, ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public void setCustomNameVisible(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setCustomNameVisible(p0);
    }

    public boolean isBlocking() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isBlocking();
    }

    public float distanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).distanceTo(((net.minecraft.world.entity.Entity) p0.data));
    }

    public java.util.UUID getUuid() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getUUID();
    }

    public void pushAwayFrom(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).push(((net.minecraft.world.entity.Entity) p0.data));
    }

    public boolean removeScoreboardTag(java.lang.String p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).removeTag(p0);
    }

    public void stopUsingItem() {
        ((net.minecraft.world.entity.LivingEntity) this.data).releaseUsingItem();
    }

    public float getYaw(float p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getViewYRot(p0);
    }

    public void slowMovement(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        ((net.minecraft.world.entity.LivingEntity) this.data).makeStuckInBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public float getEyeHeight(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getEyeHeight(p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandom() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.world.entity.LivingEntity) this.data).getRandom());
    }

    public void setMovementSpeed(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setSpeed(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d positionInPortal(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.entity.LivingEntity.resetForwardDirectionOfRelativePortalPosition(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public boolean isSleeping() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isSleeping();
    }

    public boolean isSpectator() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isSpectator();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getMovementDirection() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.entity.LivingEntity) this.data).getMotionDirection());
    }

    public float applyRotation(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).rotate(p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.Box getBoundingBox(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return new cn.jstxjf_.rte.mapping.holder.Box(((net.minecraft.world.entity.LivingEntity) this.data).getLocalBoundsForPose(p0.data));
    }

    public void setInvisible(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setInvisible(p0);
    }

    public boolean isInvisibleTo(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isInvisibleTo(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public void setHealth(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setHealth(p0);
    }

    public void setStackInHand(cn.jstxjf_.rte.mapping.holder.Hand p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setItemInHand(p0.data, ((net.minecraft.world.item.ItemStack) p1.data));
    }

    public boolean isInLava() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isInLava();
    }

    public boolean isInvulnerable() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isInvulnerable();
    }

    public void sendPickup(cn.jstxjf_.rte.mapping.holder.Entity p0, int p1) {
        ((net.minecraft.world.entity.LivingEntity) this.data).take(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public boolean isInRange(cn.jstxjf_.rte.mapping.holder.Entity p0, double p1) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).closerThan(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public void stopRiding() {
        ((net.minecraft.world.entity.LivingEntity) this.data).stopRiding();
    }

    public boolean canTarget(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).canAttack(((net.minecraft.world.entity.LivingEntity) p0.data));
    }

    public boolean canTarget(cn.jstxjf_.rte.mapping.holder.EntityType<?> p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).canAttackType(((net.minecraft.world.entity.EntityType) p0.data));
    }

    public void handleStatus(byte p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).handleEntityEvent(p0);
    }

    public void refreshPositionAndAngles(double p0, double p1, double p2, float p3, float p4) {
        ((net.minecraft.world.entity.LivingEntity) this.data).moveTo(p0, p1, p2, p3, p4);
    }

    public void refreshPositionAndAngles(cn.jstxjf_.rte.mapping.holder.BlockPos p0, float p1, float p2) {
        ((net.minecraft.world.entity.LivingEntity) this.data).moveTo(((net.minecraft.core.BlockPos) p0.data), p1, p2);
    }

    public void kill() {
        ((net.minecraft.world.entity.LivingEntity) this.data).kill();
    }

    public boolean isCustomNameVisible() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isCustomNameVisible();
    }

    public void travel(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).travel(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public void refreshPositionAfterTeleport(double p0, double p1, double p2) {
        ((net.minecraft.world.entity.LivingEntity) this.data).moveTo(p0, p1, p2);
    }

    public void refreshPositionAfterTeleport(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).moveTo(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.EntityPose getPose() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(((net.minecraft.world.entity.LivingEntity) this.data).getPose());
    }

    public final float getMaxHealth() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getMaxHealth();
    }

    public void setVelocity(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setDeltaMovement(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public void setVelocity(double p0, double p1, double p2) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setDeltaMovement(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.LivingEntity) this.data).position());
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getSoundCategory() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(((net.minecraft.world.entity.LivingEntity) this.data).getSoundSource());
    }

    public cn.jstxjf_.rte.mapping.holder.Text getCustomName() {
        final var v0 = ((net.minecraft.world.entity.LivingEntity) this.data).getCustomName();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.SoundEvent getEatSound(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(((net.minecraft.world.entity.LivingEntity) this.data).getEatingSound(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public boolean doesNotCollide(double p0, double p1, double p2) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isFree(p0, p1, p2);
    }

    public java.util.Set<java.lang.String> getCommandTags() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getTags();
    }

    public boolean isAlive() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isAlive();
    }

    public boolean collidesWithStateAtPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isColliding(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public boolean shouldRender(double p0, double p1, double p2) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).shouldRender(p0, p1, p2);
    }

    public boolean shouldRender(double p0) {
        return ((net.minecraft.world.entity.LivingEntity) this.data).shouldRenderAtSqrDistance(p0);
    }

    public void setPose(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).setPose(p0.data);
    }

    public final float getStandingEyeHeight() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getEyeHeight();
    }

    public float getAbsorptionAmount() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).getAbsorptionAmount();
    }

    public boolean isSprinting() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).isSprinting();
    }

    public cn.jstxjf_.rte.mapping.holder.Hand getPreferredHandMapped() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(((net.minecraft.world.entity.LivingEntity) this.data).swingingArm);
    }

    public void setPreferredHandMapped(cn.jstxjf_.rte.mapping.holder.Hand p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).swingingArm = p0.data;
    }

    public float getBodyYawMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).yBodyRot;
    }

    public void setBodyYawMapped(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).yBodyRot = p0;
    }

    public float getSidewaysSpeedMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).xxa;
    }

    public void setSidewaysSpeedMapped(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).xxa = p0;
    }

    public float getForwardSpeedMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).zza;
    }

    public void setForwardSpeedMapped(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).zza = p0;
    }

    public float getFallDistanceMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).fallDistance;
    }

    public void setFallDistanceMapped(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).fallDistance = p0;
    }

    public float getHeadYawMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).yHeadRot;
    }

    public void setHeadYawMapped(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).yHeadRot = p0;
    }

    public int getDefaultMaxHealthMapped() {
        java.util.Objects.requireNonNull(((net.minecraft.world.entity.LivingEntity) this.data));
        return 20;
    }

    public float getUpwardSpeedMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).yya;
    }

    public void setUpwardSpeedMapped(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).yya = p0;
    }

    public float getPrevBodyYawMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).yBodyRotO;
    }

    public void setPrevBodyYawMapped(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).yBodyRotO = p0;
    }

    public int getHurtTimeMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).hurtTime;
    }

    public void setHurtTimeMapped(int p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).hurtTime = p0;
    }

    public boolean getHorizontalCollisionMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).horizontalCollision;
    }

    public void setHorizontalCollisionMapped(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).horizontalCollision = p0;
    }

    public boolean getNoClipMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).noPhysics;
    }

    public void setNoClipMapped(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).noPhysics = p0;
    }

    public boolean getVerticalCollisionMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).verticalCollision;
    }

    public void setVerticalCollisionMapped(boolean p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).verticalCollision = p0;
    }

    public float getPrevHeadYawMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).yHeadRotO;
    }

    public void setPrevHeadYawMapped(float p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).yHeadRotO = p0;
    }

    public int getDeathTimeMapped() {
        return ((net.minecraft.world.entity.LivingEntity) this.data).deathTime;
    }

    public void setDeathTimeMapped(int p0) {
        ((net.minecraft.world.entity.LivingEntity) this.data).deathTime = p0;
    }
}
