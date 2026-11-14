package cn.jstxjf_.rte.mapping.holder;

public final class ServerPlayerEntity extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.level.ServerPlayer> {

    public ServerPlayerEntity(net.minecraft.server.level.ServerPlayer p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ServerPlayerEntity((net.minecraft.server.level.ServerPlayer) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.level.ServerPlayer;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isPushable() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isPushable();
    }

    public cn.jstxjf_.rte.mapping.holder.World getEntityWorld() {
        return new cn.jstxjf_.rte.mapping.holder.World(((net.minecraft.server.level.ServerPlayer) this.data).getCommandSenderWorld());
    }

    public boolean isUsingItem() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isUsingItem();
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d applyMovementInput(cn.jstxjf_.rte.mapping.holder.Vector3d p0, float p1) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.server.level.ServerPlayer) this.data).handleRelativeFrictionAndCalculateMovement(((net.minecraft.world.phys.Vec3) p0.data), p1));
    }

    public void setNearbySongPlaying(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setRecordPlayingNearby(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public void increaseStat(cn.jstxjf_.rte.mapping.holder.Identifier p0, int p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).awardStat(((net.minecraft.resources.ResourceLocation) p0.data), p1);
    }

    public void updatePositionAndAngles(double p0, double p1, double p2, float p3, float p4) {
        ((net.minecraft.server.level.ServerPlayer) this.data).absMoveTo(p0, p1, p2, p3, p4);
    }

    public boolean canPlaceOn(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Direction p1, cn.jstxjf_.rte.mapping.holder.ItemStack p2) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).mayUseItemAt(((net.minecraft.core.BlockPos) p0.data), p1.data, ((net.minecraft.world.item.ItemStack) p2.data));
    }

    public void updateTrackedHeadRotation(float p0, int p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).lerpHeadTo(p0, p1);
    }

    public final int getStingerCount() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getStingerCount();
    }

    public boolean hasPassenger(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).hasPassenger(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void setSilent(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setSilent(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getOffHandStack() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.server.level.ServerPlayer) this.data).getOffhandItem());
    }

    public static void setRenderDistanceMultiplier(double p0) {
        net.minecraft.server.level.ServerPlayer.setViewScale(p0);
    }

    public int getScore() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getScore();
    }

    public boolean isOnFire() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isOnFire();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getStackInHand(cn.jstxjf_.rte.mapping.holder.Hand p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.server.level.ServerPlayer) this.data).getItemInHand(p0.data));
    }

    public void addVelocity(double p0, double p1, double p2) {
        ((net.minecraft.server.level.ServerPlayer) this.data).push(p0, p1, p2);
    }

    public void copyFrom(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0, boolean p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).restoreFrom(((net.minecraft.server.level.ServerPlayer) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getShoulderEntityLeft() {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.server.level.ServerPlayer) this.data).getShoulderEntityLeft());
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.SoundEvent p0, cn.jstxjf_.rte.mapping.holder.SoundCategory p1, float p2, float p3) {
        ((net.minecraft.server.level.ServerPlayer) this.data).playNotifySound(((net.minecraft.sounds.SoundEvent) p0.data), p1.data, p2, p3);
    }

    public void setCustomName(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setCustomName((p0 == null ? null : ((net.minecraft.network.chat.Component) p0.data)));
    }

    public double offsetX(double p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getX(p0);
    }

    public double offsetZ(double p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getZ(p0);
    }

    public void startFallFlying() {
        ((net.minecraft.server.level.ServerPlayer) this.data).startFallFlying();
    }

    public void setJumping(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setJumping(p0);
    }

    public void updatePosition(double p0, double p1, double p2) {
        ((net.minecraft.server.level.ServerPlayer) this.data).absMoveTo(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getProjectileType(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.server.level.ServerPlayer) this.data).getProjectile(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public void setUuid(java.util.UUID p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setUUID(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.server.level.ServerPlayer) this.data).getDisplayName());
    }

    public float getPitch(float p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getViewXRot(p0);
    }

    public double getBodyY(double p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getY(p0);
    }

    public void tick() {
        ((net.minecraft.server.level.ServerPlayer) this.data).tick();
    }

    public double getAttackDistanceScalingFactor(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getVisibilityPercent((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interactAt(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.Hand p2) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.server.level.ServerPlayer) this.data).interactAt(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), p2.data));
    }

    public boolean isHolding(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isHolding(((net.minecraft.world.item.Item) p0.data));
    }

    public boolean isInvisible() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isInvisible();
    }

    public cn.jstxjf_.rte.mapping.holder.Hand getActiveHand() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(((net.minecraft.server.level.ServerPlayer) this.data).getUsedItemHand());
    }

    public void setExperiencePoints(int p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setExperiencePoints(p0);
    }

    public boolean isBaby() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isBaby();
    }

    public final float getWidth() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getBbWidth();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack eatFood(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.server.level.ServerPlayer) this.data).eat(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.item.ItemStack) p1.data)));
    }

    public static double getRenderDistanceMultiplier() {
        return net.minecraft.server.level.ServerPlayer.getViewScale();
    }

    public void applyEnchantmentCosts(cn.jstxjf_.rte.mapping.holder.ItemStack p0, int p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).onEnchantmentPerformed(((net.minecraft.world.item.ItemStack) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer() {
        final var v0 = ((net.minecraft.server.level.ServerPlayer) this.data).getServer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.MinecraftServer(v0));
    }

    public float applyMirror(cn.jstxjf_.rte.mapping.holder.Mirror p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).mirror(p0.data);
    }

    public boolean isDescending() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isDescending();
    }

    public boolean isSwimming() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isSwimming();
    }

    public final void setStingerCount(int p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setStingerCount(p0);
    }

    public boolean isFallFlying() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isFallFlying();
    }

    public double squaredDistanceTo(double p0, double p1, double p2) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).distanceToSqr(p0, p1, p2);
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).distanceToSqr(((net.minecraft.world.entity.Entity) p0.data));
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).distanceToSqr(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public boolean canEquip(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).canTakeItem(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public void setOnGround(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setOnGround(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag writeNbt(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.server.level.ServerPlayer) this.data).saveWithoutId(((net.minecraft.nbt.CompoundTag) p0.data)));
    }

    public void swingHand(cn.jstxjf_.rte.mapping.holder.Hand p0, boolean p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).swing(p0.data, p1);
    }

    public float getEffectiveExplosionResistance(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, cn.jstxjf_.rte.mapping.holder.FluidState p4, float p5) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getBlockExplosionResistance(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), ((net.minecraft.world.level.material.FluidState) p4.data), p5);
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.SoundEvent p0, float p1, float p2) {
        ((net.minecraft.server.level.ServerPlayer) this.data).playSound(((net.minecraft.sounds.SoundEvent) p0.data), p1, p2);
    }

    public void teleport(cn.jstxjf_.rte.mapping.holder.ServerWorld p0, double p1, double p2, double p3, float p4, float p5) {
        ((net.minecraft.server.level.ServerPlayer) this.data).teleportTo(((net.minecraft.server.level.ServerLevel) p0.data), p1, p2, p3, p4, p5);
    }

    public boolean hasCustomName() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).hasCustomName();
    }

    public boolean canHarvest(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).hasCorrectToolForDrops(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public void updateSwimming() {
        ((net.minecraft.server.level.ServerPlayer) this.data).updateSwimming();
    }

    public void tickRiding() {
        ((net.minecraft.server.level.ServerPlayer) this.data).rideTick();
    }

    public boolean isAttackable() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isAttackable();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getMainHandStack() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.server.level.ServerPlayer) this.data).getMainHandItem());
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.server.level.ServerPlayer) this.data).getName());
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getVehicle() {
        final var v0 = ((net.minecraft.server.level.ServerPlayer) this.data).getVehicle();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public double method_29241() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getFluidJumpThreshold();
    }

    public void updateInput(float p0, float p1, boolean p2, boolean p3) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setPlayerInput(p0, p1, p2, p3);
    }

    public void setHeadYaw(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setYHeadRot(p0);
    }

    public ServerPlayerEntity(cn.jstxjf_.rte.mapping.holder.MinecraftServer p0, cn.jstxjf_.rte.mapping.holder.ServerWorld p1, com.mojang.authlib.GameProfile p2, net.minecraft.server.level.ClientInformation p3) {
        super(new net.minecraft.server.level.ServerPlayer(((net.minecraft.server.MinecraftServer) p0.data), ((net.minecraft.server.level.ServerLevel) p1.data), p2, p3));
    }

    public int hashCode() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).hashCode();
    }

    public float getHealth() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getHealth();
    }

    public boolean startRiding(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).startRiding(((net.minecraft.world.entity.Entity) p0.data));
    }

    public float getBlockBreakingSpeed(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getDestroySpeed(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getSleepingDirection() {
        final var v0 = ((net.minecraft.server.level.ServerPlayer) this.data).getBedOrientation();
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.Direction.convert(v0));
    }

    public void baseTick() {
        ((net.minecraft.server.level.ServerPlayer) this.data).baseTick();
    }

    public void setNoGravity(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setNoGravity(p0);
    }

    public void setScore(int p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setScore(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.HitResult raycast(double p0, float p1, boolean p2) {
        return new cn.jstxjf_.rte.mapping.holder.HitResult(((net.minecraft.server.level.ServerPlayer) this.data).pick(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d updatePassengerForDismount(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.server.level.ServerPlayer) this.data).getDismountLocationForPassenger(((net.minecraft.world.entity.LivingEntity) p0.data)));
    }

    public void setInvulnerable(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setInvulnerable(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getRootVehicle() {
        return new cn.jstxjf_.rte.mapping.holder.Entity(((net.minecraft.server.level.ServerPlayer) this.data).getRootVehicle());
    }

    public java.lang.String getUuidAsString() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getStringUUID();
    }

    public void setSprinting(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setSprinting(p0);
    }

    public boolean isSilent() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isSilent();
    }

    public void setSwimming(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setSwimming(p0);
    }

    public void checkDespawn() {
        ((net.minecraft.server.level.ServerPlayer) this.data).checkDespawn();
    }

    public final float getHeight() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getBbHeight();
    }

    public int getSleepTimer() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getSleepTimer();
    }

    public cn.jstxjf_.rte.mapping.holder.ServerWorld getServerWorld() {
        return new cn.jstxjf_.rte.mapping.holder.ServerWorld(((net.minecraft.server.level.ServerPlayer) this.data).serverLevel());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d applyFluidMovingSpeed(double p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Vector3d p2) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.server.level.ServerPlayer) this.data).getFluidFallingAdjustedMovement(p0, p1, ((net.minecraft.world.phys.Vec3) p2.data)));
    }

    public boolean isSneaking() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isShiftKeyDown();
    }

    public double getRandomBodyY() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getRandomY();
    }

    public boolean addScoreboardTag(java.lang.String p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).addTag(p0);
    }

    public boolean teleport(double p0, double p1, double p2, boolean p3) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).randomTeleport(p0, p1, p2, p3);
    }

    public boolean canExplosionDestroyBlock(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, float p4) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).shouldBlockExplode(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), p4);
    }

    public boolean startRiding(cn.jstxjf_.rte.mapping.holder.Entity p0, boolean p1) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).startRiding(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public void incrementStat(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).awardStat(((net.minecraft.resources.ResourceLocation) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getHorizontalFacing() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.server.level.ServerPlayer) this.data).getDirection());
    }

    public void stopFallFlying() {
        ((net.minecraft.server.level.ServerPlayer) this.data).stopFallFlying();
    }

    public void heal(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).heal(p0);
    }

    public void setAttacker(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setLastHurtByMob((p0 == null ? null : ((net.minecraft.world.entity.LivingEntity) p0.data)));
    }

    public float getLuck() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getLuck();
    }

    public boolean isSpectator() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isSpectator();
    }

    public final void setStuckArrowCount(int p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setArrowCount(p0);
    }

    public void setBodyYaw(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setYBodyRot(p0);
    }

    public final double getY() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getY();
    }

    public long getLastActionTime() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getLastActionTime();
    }

    public final double getX() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getX();
    }

    public final double getZ() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getZ();
    }

    public boolean hasPermissionLevel(int p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).hasPermissions(p0);
    }

    public double getEyeY() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getEyeY();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.server.level.ServerPlayer) this.data).blockPosition());
    }

    public double getParticleZ(double p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getRandomZ(p0);
    }

    public double getParticleX(double p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getRandomX(p0);
    }

    public void stopRiding() {
        ((net.minecraft.server.level.ServerPlayer) this.data).stopRiding();
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interact(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.server.level.ServerPlayer) this.data).interact(((net.minecraft.world.entity.player.Player) p0.data), p1.data));
    }

    public void updateVelocity(float p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).moveRelative(p0, ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public void setCustomNameVisible(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setCustomNameVisible(p0);
    }

    public void sendPickup(cn.jstxjf_.rte.mapping.holder.Entity p0, int p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).take(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public boolean isBlocking() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isBlocking();
    }

    public void travel(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).travel(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public float distanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).distanceTo(((net.minecraft.world.entity.Entity) p0.data));
    }

    public java.util.UUID getUuid() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getUUID();
    }

    public void pushAwayFrom(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).push(((net.minecraft.world.entity.Entity) p0.data));
    }

    public boolean removeScoreboardTag(java.lang.String p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).removeTag(p0);
    }

    public float getAbsorptionAmount() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getAbsorptionAmount();
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interact(cn.jstxjf_.rte.mapping.holder.Entity p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.server.level.ServerPlayer) this.data).interactOn(((net.minecraft.world.entity.Entity) p0.data), p1.data));
    }

    public void stopUsingItem() {
        ((net.minecraft.server.level.ServerPlayer) this.data).releaseUsingItem();
    }

    public boolean isBlockBreakingRestricted(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.GameMode p2) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).blockActionRestricted(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2.data);
    }

    public float getYaw(float p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getViewYRot(p0);
    }

    public boolean giveItemStack(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).addItem(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public boolean canConsume(boolean p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).canEat(p0);
    }

    public float getEyeHeight(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getEyeHeight(p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandom() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.server.level.ServerPlayer) this.data).getRandom());
    }

    public void setReducedDebugInfo(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setReducedDebugInfo(p0);
    }

    public void setMovementSpeed(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setSpeed(p0);
    }

    public void handleStatus(byte p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).handleEntityEvent(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d positionInPortal(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.server.level.ServerPlayer.resetForwardDirectionOfRelativePortalPosition(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public boolean isSleeping() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isSleeping();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getMovementDirection() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.server.level.ServerPlayer) this.data).getMotionDirection());
    }

    public float applyRotation(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).rotate(p0.data);
    }

    public void slowMovement(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).makeStuckInBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public com.mojang.authlib.GameProfile getGameProfile() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getGameProfile();
    }

    public cn.jstxjf_.rte.mapping.holder.Box getBoundingBox(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return new cn.jstxjf_.rte.mapping.holder.Box(((net.minecraft.server.level.ServerPlayer) this.data).getLocalBoundsForPose(p0.data));
    }

    public void setInvisible(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setInvisible(p0);
    }

    public boolean isCreative() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isCreative();
    }

    public void wakeUp(boolean p0, boolean p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).stopSleepInBed(p0, p1);
    }

    public boolean isInvisibleTo(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isInvisibleTo(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public void setHealth(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setHealth(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Scoreboard getScoreboard() {
        return new cn.jstxjf_.rte.mapping.holder.Scoreboard(((net.minecraft.server.level.ServerPlayer) this.data).getScoreboard());
    }

    public void setStackInHand(cn.jstxjf_.rte.mapping.holder.Hand p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setItemInHand(p0.data, ((net.minecraft.world.item.ItemStack) p1.data));
    }

    public boolean isInLava() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isInLava();
    }

    public void useBook(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).openItemGui(((net.minecraft.world.item.ItemStack) p0.data), p1.data);
    }

    public boolean isInvulnerable() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isInvulnerable();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getActiveItem() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.server.level.ServerPlayer) this.data).getUseItem());
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getSoundCategory() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(((net.minecraft.server.level.ServerPlayer) this.data).getSoundSource());
    }

    public boolean canTarget(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).canAttack(((net.minecraft.world.entity.LivingEntity) p0.data));
    }

    public boolean canTarget(cn.jstxjf_.rte.mapping.holder.EntityType<?> p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).canAttackType(((net.minecraft.world.entity.EntityType) p0.data));
    }

    public void refreshPositionAndAngles(double p0, double p1, double p2, float p3, float p4) {
        ((net.minecraft.server.level.ServerPlayer) this.data).moveTo(p0, p1, p2, p3, p4);
    }

    public void refreshPositionAndAngles(cn.jstxjf_.rte.mapping.holder.BlockPos p0, float p1, float p2) {
        ((net.minecraft.server.level.ServerPlayer) this.data).moveTo(((net.minecraft.core.BlockPos) p0.data), p1, p2);
    }

    public void kill() {
        ((net.minecraft.server.level.ServerPlayer) this.data).kill();
    }

    public boolean isCustomNameVisible() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isCustomNameVisible();
    }

    public void sendMessage(cn.jstxjf_.rte.mapping.holder.Text p0, boolean p1) {
        ((net.minecraft.server.level.ServerPlayer) this.data).displayClientMessage(((net.minecraft.network.chat.Component) p0.data), p1);
    }

    public boolean shouldDamagePlayer(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).canHarmPlayer(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public void refreshPositionAfterTeleport(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).moveTo(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.EntityPose getPose() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(((net.minecraft.server.level.ServerPlayer) this.data).getPose());
    }

    public final float getMaxHealth() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getMaxHealth();
    }

    public void attack(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).attack(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void setVelocity(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setDeltaMovement(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public void setVelocity(double p0, double p1, double p2) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setDeltaMovement(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.server.level.ServerPlayer) this.data).position());
    }

    public cn.jstxjf_.rte.mapping.holder.Text getCustomName() {
        final var v0 = ((net.minecraft.server.level.ServerPlayer) this.data).getCustomName();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(v0));
    }

    public boolean dropSelectedItem(boolean p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).drop(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.SoundEvent getEatSound(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(((net.minecraft.server.level.ServerPlayer) this.data).getEatingSound(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public void refreshPositionAfterTeleport(double p0, double p1, double p2) {
        ((net.minecraft.server.level.ServerPlayer) this.data).moveTo(p0, p1, p2);
    }

    public boolean doesNotCollide(double p0, double p1, double p2) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isFree(p0, p1, p2);
    }

    public java.util.Set<java.lang.String> getCommandTags() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getTags();
    }

    public boolean isAlive() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isAlive();
    }

    public boolean collidesWithStateAtPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isColliding(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public boolean shouldRender(double p0, double p1, double p2) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).shouldRender(p0, p1, p2);
    }

    public boolean shouldRender(double p0) {
        return ((net.minecraft.server.level.ServerPlayer) this.data).shouldRenderAtSqrDistance(p0);
    }

    public void setPose(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).setPose(p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getShoulderEntityRight() {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.server.level.ServerPlayer) this.data).getShoulderEntityRight());
    }

    public final float getStandingEyeHeight() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).getEyeHeight();
    }

    public boolean isSprinting() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).isSprinting();
    }

    public cn.jstxjf_.rte.mapping.holder.Hand getPreferredHandMapped() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(((net.minecraft.server.level.ServerPlayer) this.data).swingingArm);
    }

    public void setPreferredHandMapped(cn.jstxjf_.rte.mapping.holder.Hand p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).swingingArm = p0.data;
    }

    public float getBodyYawMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).yBodyRot;
    }

    public void setBodyYawMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).yBodyRot = p0;
    }

    public float getExperienceProgressMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).experienceProgress;
    }

    public void setExperienceProgressMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).experienceProgress = p0;
    }

    public int getTotalExperienceMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).totalExperience;
    }

    public void setTotalExperienceMapped(int p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).totalExperience = p0;
    }

    public float getSidewaysSpeedMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).xxa;
    }

    public void setSidewaysSpeedMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).xxa = p0;
    }

    public float getForwardSpeedMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).zza;
    }

    public void setForwardSpeedMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).zza = p0;
    }

    public float getFallDistanceMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).fallDistance;
    }

    public void setFallDistanceMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).fallDistance = p0;
    }

    public float getHeadYawMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).yHeadRot;
    }

    public void setHeadYawMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).yHeadRot = p0;
    }

    public int getDefaultMaxHealthMapped() {
        java.util.Objects.requireNonNull(((net.minecraft.server.level.ServerPlayer) this.data));
        return 20;
    }

    public float getUpwardSpeedMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).yya;
    }

    public void setUpwardSpeedMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).yya = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MinecraftServer(((net.minecraft.server.level.ServerPlayer) this.data).server);
    }

    public float getPrevBodyYawMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).yBodyRotO;
    }

    public void setPrevBodyYawMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).yBodyRotO = p0;
    }

    public int getHurtTimeMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).hurtTime;
    }

    public void setHurtTimeMapped(int p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).hurtTime = p0;
    }

    public boolean getHorizontalCollisionMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).horizontalCollision;
    }

    public void setHorizontalCollisionMapped(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).horizontalCollision = p0;
    }

    public boolean getNoClipMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).noPhysics;
    }

    public void setNoClipMapped(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).noPhysics = p0;
    }

    public boolean getVerticalCollisionMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).verticalCollision;
    }

    public void setVerticalCollisionMapped(boolean p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).verticalCollision = p0;
    }

    public float getPrevHeadYawMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).yHeadRotO;
    }

    public void setPrevHeadYawMapped(float p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).yHeadRotO = p0;
    }

    public int getDeathTimeMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).deathTime;
    }

    public void setDeathTimeMapped(int p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).deathTime = p0;
    }

    public int getExperienceLevelMapped() {
        return ((net.minecraft.server.level.ServerPlayer) this.data).experienceLevel;
    }

    public void setExperienceLevelMapped(int p0) {
        ((net.minecraft.server.level.ServerPlayer) this.data).experienceLevel = p0;
    }
}
