package cn.jstxjf_.rte.mapping.holder;

public final class ClientPlayerEntity extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.player.LocalPlayer> {

    public ClientPlayerEntity(net.minecraft.client.player.LocalPlayer p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ClientPlayerEntity cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ClientPlayerEntity((net.minecraft.client.player.LocalPlayer) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.player.LocalPlayer;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public boolean isPushable() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isPushable();
    }

    public float getPitch(float p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getViewXRot(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.World getEntityWorld() {
        return new cn.jstxjf_.rte.mapping.holder.World(((net.minecraft.client.player.LocalPlayer) this.data).getCommandSenderWorld());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d applyMovementInput(cn.jstxjf_.rte.mapping.holder.Vector3d p0, float p1) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.player.LocalPlayer) this.data).handleRelativeFrictionAndCalculateMovement(((net.minecraft.world.phys.Vec3) p0.data), p1));
    }

    public void setNearbySongPlaying(cn.jstxjf_.rte.mapping.holder.BlockPos p0, boolean p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setRecordPlayingNearby(((net.minecraft.core.BlockPos) p0.data), p1);
    }

    public void increaseStat(cn.jstxjf_.rte.mapping.holder.Identifier p0, int p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).awardStat(((net.minecraft.resources.ResourceLocation) p0.data), p1);
    }

    public void updatePositionAndAngles(double p0, double p1, double p2, float p3, float p4) {
        ((net.minecraft.client.player.LocalPlayer) this.data).absMoveTo(p0, p1, p2, p3, p4);
    }

    public boolean canPlaceOn(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.Direction p1, cn.jstxjf_.rte.mapping.holder.ItemStack p2) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).mayUseItemAt(((net.minecraft.core.BlockPos) p0.data), p1.data, ((net.minecraft.world.item.ItemStack) p2.data));
    }

    public void updateTrackedHeadRotation(float p0, int p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).lerpHeadTo(p0, p1);
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.SoundEvent p0, float p1, float p2) {
        ((net.minecraft.client.player.LocalPlayer) this.data).playSound(((net.minecraft.sounds.SoundEvent) p0.data), p1, p2);
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.SoundEvent p0, cn.jstxjf_.rte.mapping.holder.SoundCategory p1, float p2, float p3) {
        ((net.minecraft.client.player.LocalPlayer) this.data).playNotifySound(((net.minecraft.sounds.SoundEvent) p0.data), p1.data, p2, p3);
    }

    public void applyEnchantmentCosts(cn.jstxjf_.rte.mapping.holder.ItemStack p0, int p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).onEnchantmentPerformed(((net.minecraft.world.item.ItemStack) p0.data), p1);
    }

    public final int getStingerCount() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getStingerCount();
    }

    public boolean hasPassenger(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).hasPassenger(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void setSilent(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setSilent(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getOffHandStack() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.client.player.LocalPlayer) this.data).getOffhandItem());
    }

    public static void setRenderDistanceMultiplier(double p0) {
        net.minecraft.client.player.LocalPlayer.setViewScale(p0);
    }

    public int getScore() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getScore();
    }

    public boolean isOnFire() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isOnFire();
    }

    public boolean isCreative() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isCreative();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getStackInHand(cn.jstxjf_.rte.mapping.holder.Hand p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.client.player.LocalPlayer) this.data).getItemInHand(p0.data));
    }

    public void addVelocity(double p0, double p1, double p2) {
        ((net.minecraft.client.player.LocalPlayer) this.data).push(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getShoulderEntityLeft() {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.client.player.LocalPlayer) this.data).getShoulderEntityLeft());
    }

    public void setCustomName(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setCustomName((p0 == null ? null : ((net.minecraft.network.chat.Component) p0.data)));
    }

    public double offsetX(double p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getX(p0);
    }

    public double offsetZ(double p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getZ(p0);
    }

    public void startFallFlying() {
        ((net.minecraft.client.player.LocalPlayer) this.data).startFallFlying();
    }

    public void setJumping(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setJumping(p0);
    }

    public void updatePosition(double p0, double p1, double p2) {
        ((net.minecraft.client.player.LocalPlayer) this.data).absMoveTo(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getProjectileType(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.client.player.LocalPlayer) this.data).getProjectile(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public void setUuid(java.util.UUID p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setUUID(p0);
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getCameraPosVec(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.player.LocalPlayer) this.data).getEyePosition(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.client.player.LocalPlayer) this.data).getDisplayName());
    }

    public double getBodyY(double p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getY(p0);
    }

    public double getAttackDistanceScalingFactor(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getVisibilityPercent((p0 == null ? null : ((net.minecraft.world.entity.Entity) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interactAt(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.Hand p2) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.client.player.LocalPlayer) this.data).interactAt(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), p2.data));
    }

    public boolean isHolding(cn.jstxjf_.rte.mapping.holder.Item p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isHolding(((net.minecraft.world.item.Item) p0.data));
    }

    public boolean isInvisible() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isInvisible();
    }

    public void heal(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).heal(p0);
    }

    public boolean isBaby() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isBaby();
    }

    public boolean isSpectator() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isSpectator();
    }

    public final float getWidth() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getBbWidth();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack eatFood(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.client.player.LocalPlayer) this.data).eat(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.world.item.ItemStack) p1.data)));
    }

    public static double getRenderDistanceMultiplier() {
        return net.minecraft.client.player.LocalPlayer.getViewScale();
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer() {
        final var v0 = ((net.minecraft.client.player.LocalPlayer) this.data).getServer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.MinecraftServer(v0));
    }

    public float applyMirror(cn.jstxjf_.rte.mapping.holder.Mirror p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).mirror(p0.data);
    }

    public boolean isDescending() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isDescending();
    }

    public boolean isSwimming() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isSwimming();
    }

    public final void setStingerCount(int p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setStingerCount(p0);
    }

    public boolean isFallFlying() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isFallFlying();
    }

    public void tick() {
        ((net.minecraft.client.player.LocalPlayer) this.data).tick();
    }

    public double squaredDistanceTo(double p0, double p1, double p2) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).distanceToSqr(p0, p1, p2);
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).distanceToSqr(((net.minecraft.world.entity.Entity) p0.data));
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).distanceToSqr(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public boolean canBeSpectated(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).broadcastToPlayer(((net.minecraft.server.level.ServerPlayer) p0.data));
    }

    public boolean canEquip(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).canTakeItem(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public void setOnGround(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setOnGround(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag writeNbt(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.client.player.LocalPlayer) this.data).saveWithoutId(((net.minecraft.nbt.CompoundTag) p0.data)));
    }

    public void swingHand(cn.jstxjf_.rte.mapping.holder.Hand p0, boolean p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).swing(p0.data, p1);
    }

    public float getEffectiveExplosionResistance(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, cn.jstxjf_.rte.mapping.holder.FluidState p4, float p5) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getBlockExplosionResistance(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), ((net.minecraft.world.level.material.FluidState) p4.data), p5);
    }

    public boolean hasCustomName() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).hasCustomName();
    }

    public boolean canHarvest(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).hasCorrectToolForDrops(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public void updateSwimming() {
        ((net.minecraft.client.player.LocalPlayer) this.data).updateSwimming();
    }

    public boolean isAttackable() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isAttackable();
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getMainHandStack() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.client.player.LocalPlayer) this.data).getMainHandItem());
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d method_30950(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.player.LocalPlayer) this.data).getPosition(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.client.player.LocalPlayer) this.data).getName());
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getVehicle() {
        final var v0 = ((net.minecraft.client.player.LocalPlayer) this.data).getVehicle();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public double method_29241() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getFluidJumpThreshold();
    }

    public void setHeadYaw(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setYHeadRot(p0);
    }

    public int hashCode() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).hashCode();
    }

    public float getHealth() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getHealth();
    }

    public boolean startRiding(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).startRiding(((net.minecraft.world.entity.Entity) p0.data));
    }

    public float getBlockBreakingSpeed(cn.jstxjf_.rte.mapping.holder.BlockState p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getDestroySpeed(((net.minecraft.world.level.block.state.BlockState) p0.data));
    }

    public void tickRiding() {
        ((net.minecraft.client.player.LocalPlayer) this.data).rideTick();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getSleepingDirection() {
        final var v0 = ((net.minecraft.client.player.LocalPlayer) this.data).getBedOrientation();
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.Direction.convert(v0));
    }

    public boolean isUsingItem() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isUsingItem();
    }

    public void baseTick() {
        ((net.minecraft.client.player.LocalPlayer) this.data).baseTick();
    }

    public boolean isSneaking() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isShiftKeyDown();
    }

    public void setNoGravity(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setNoGravity(p0);
    }

    public void setScore(int p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setScore(p0);
    }

    public void setExperience(float p0, int p1, int p2) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setExperienceValues(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.HitResult raycast(double p0, float p1, boolean p2) {
        return new cn.jstxjf_.rte.mapping.holder.HitResult(((net.minecraft.client.player.LocalPlayer) this.data).pick(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d updatePassengerForDismount(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.player.LocalPlayer) this.data).getDismountLocationForPassenger(((net.minecraft.world.entity.LivingEntity) p0.data)));
    }

    public void setInvulnerable(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setInvulnerable(p0);
    }

    public ClientPlayerEntity(cn.jstxjf_.rte.mapping.holder.MinecraftClient p0, cn.jstxjf_.rte.mapping.holder.ClientWorld p1, cn.jstxjf_.rte.mapping.holder.ClientPlayNetworkHandler p2, net.minecraft.stats.StatsCounter p3, net.minecraft.client.ClientRecipeBook p4, boolean p5, boolean p6) {
        super(new net.minecraft.client.player.LocalPlayer(((net.minecraft.client.Minecraft) p0.data), ((net.minecraft.client.multiplayer.ClientLevel) p1.data), ((net.minecraft.client.multiplayer.ClientPacketListener) p2.data), p3, p4, p5, p6));
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getRootVehicle() {
        return new cn.jstxjf_.rte.mapping.holder.Entity(((net.minecraft.client.player.LocalPlayer) this.data).getRootVehicle());
    }

    public java.lang.String getUuidAsString() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getStringUUID();
    }

    public void setSprinting(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setSprinting(p0);
    }

    public boolean isSilent() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isSilent();
    }

    public void setSwimming(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setSwimming(p0);
    }

    public boolean startRiding(cn.jstxjf_.rte.mapping.holder.Entity p0, boolean p1) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).startRiding(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public void checkDespawn() {
        ((net.minecraft.client.player.LocalPlayer) this.data).checkDespawn();
    }

    public final float getHeight() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getBbHeight();
    }

    public int getSleepTimer() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getSleepTimer();
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d applyFluidMovingSpeed(double p0, boolean p1, cn.jstxjf_.rte.mapping.holder.Vector3d p2) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.player.LocalPlayer) this.data).getFluidFallingAdjustedMovement(p0, p1, ((net.minecraft.world.phys.Vec3) p2.data)));
    }

    public double getRandomBodyY() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getRandomY();
    }

    public boolean addScoreboardTag(java.lang.String p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).addTag(p0);
    }

    public boolean teleport(double p0, double p1, double p2, boolean p3) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).randomTeleport(p0, p1, p2, p3);
    }

    public boolean canExplosionDestroyBlock(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, float p4) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).shouldBlockExplode(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), p4);
    }

    public void incrementStat(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).awardStat(((net.minecraft.resources.ResourceLocation) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getHorizontalFacing() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.client.player.LocalPlayer) this.data).getDirection());
    }

    public cn.jstxjf_.rte.mapping.holder.Hand getActiveHand() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(((net.minecraft.client.player.LocalPlayer) this.data).getUsedItemHand());
    }

    public void stopFallFlying() {
        ((net.minecraft.client.player.LocalPlayer) this.data).stopFallFlying();
    }

    public void setAttacker(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setLastHurtByMob((p0 == null ? null : ((net.minecraft.world.entity.LivingEntity) p0.data)));
    }

    public float getLuck() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getLuck();
    }

    public final void setStuckArrowCount(int p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setArrowCount(p0);
    }

    public void setBodyYaw(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setYBodyRot(p0);
    }

    public final double getY() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getY();
    }

    public final double getX() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getX();
    }

    public final double getZ() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getZ();
    }

    public boolean hasPermissionLevel(int p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).hasPermissions(p0);
    }

    public double getEyeY() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getEyeY();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.client.player.LocalPlayer) this.data).blockPosition());
    }

    public double getParticleZ(double p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getRandomZ(p0);
    }

    public double getParticleX(double p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getRandomX(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interact(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.client.player.LocalPlayer) this.data).interact(((net.minecraft.world.entity.player.Player) p0.data), p1.data));
    }

    public void updateVelocity(float p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).moveRelative(p0, ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public void setCustomNameVisible(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setCustomNameVisible(p0);
    }

    public boolean isBlocking() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isBlocking();
    }

    public float distanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).distanceTo(((net.minecraft.world.entity.Entity) p0.data));
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getOppositeRotationVector(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.player.LocalPlayer) this.data).getUpVector(p0));
    }

    public java.util.UUID getUuid() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getUUID();
    }

    public void pushAwayFrom(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).push(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void wakeUp(boolean p0, boolean p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).stopSleepInBed(p0, p1);
    }

    public void handleStatus(byte p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).handleEntityEvent(p0);
    }

    public float getAbsorptionAmount() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getAbsorptionAmount();
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interact(cn.jstxjf_.rte.mapping.holder.Entity p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.client.player.LocalPlayer) this.data).interactOn(((net.minecraft.world.entity.Entity) p0.data), p1.data));
    }

    public void stopUsingItem() {
        ((net.minecraft.client.player.LocalPlayer) this.data).releaseUsingItem();
    }

    public boolean isBlockBreakingRestricted(cn.jstxjf_.rte.mapping.holder.World p0, cn.jstxjf_.rte.mapping.holder.BlockPos p1, cn.jstxjf_.rte.mapping.holder.GameMode p2) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).blockActionRestricted(((net.minecraft.world.level.Level) p0.data), ((net.minecraft.core.BlockPos) p1.data), p2.data);
    }

    public boolean giveItemStack(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).addItem(((net.minecraft.world.item.ItemStack) p0.data));
    }

    public boolean canConsume(boolean p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).canEat(p0);
    }

    public float getEyeHeight(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getEyeHeight(p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandom() {
        return new cn.jstxjf_.rte.mapping.holder.Random(((net.minecraft.client.player.LocalPlayer) this.data).getRandom());
    }

    public void setReducedDebugInfo(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setReducedDebugInfo(p0);
    }

    public void setMovementSpeed(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setSpeed(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Vector3d positionInPortal(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.client.player.LocalPlayer.resetForwardDirectionOfRelativePortalPosition(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    public boolean isSleeping() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isSleeping();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getMovementDirection() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.client.player.LocalPlayer) this.data).getMotionDirection());
    }

    public void travel(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).travel(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public float applyRotation(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).rotate(p0.data);
    }

    public void slowMovement(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).makeStuckInBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public com.mojang.authlib.GameProfile getGameProfile() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getGameProfile();
    }

    public cn.jstxjf_.rte.mapping.holder.Box getBoundingBox(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return new cn.jstxjf_.rte.mapping.holder.Box(((net.minecraft.client.player.LocalPlayer) this.data).getLocalBoundsForPose(p0.data));
    }

    public void setInvisible(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setInvisible(p0);
    }

    public boolean isInvisibleTo(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isInvisibleTo(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public void setHealth(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setHealth(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Scoreboard getScoreboard() {
        return new cn.jstxjf_.rte.mapping.holder.Scoreboard(((net.minecraft.client.player.LocalPlayer) this.data).getScoreboard());
    }

    public void setStackInHand(cn.jstxjf_.rte.mapping.holder.Hand p0, cn.jstxjf_.rte.mapping.holder.ItemStack p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setItemInHand(p0.data, ((net.minecraft.world.item.ItemStack) p1.data));
    }

    public boolean isInLava() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isInLava();
    }

    public boolean isInvulnerable() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isInvulnerable();
    }

    public void sendMessage(cn.jstxjf_.rte.mapping.holder.Text p0, boolean p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).displayClientMessage(((net.minecraft.network.chat.Component) p0.data), p1);
    }

    public cn.jstxjf_.rte.mapping.holder.ItemStack getActiveItem() {
        return new cn.jstxjf_.rte.mapping.holder.ItemStack(((net.minecraft.client.player.LocalPlayer) this.data).getUseItem());
    }

    public void sendPickup(cn.jstxjf_.rte.mapping.holder.Entity p0, int p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).take(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public void stopRiding() {
        ((net.minecraft.client.player.LocalPlayer) this.data).stopRiding();
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getSoundCategory() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(((net.minecraft.client.player.LocalPlayer) this.data).getSoundSource());
    }

    public boolean canTarget(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).canAttack(((net.minecraft.world.entity.LivingEntity) p0.data));
    }

    public boolean canTarget(cn.jstxjf_.rte.mapping.holder.EntityType<?> p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).canAttackType(((net.minecraft.world.entity.EntityType) p0.data));
    }

    public boolean isAutoJumpEnabled() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isAutoJumpEnabled();
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getRotationVec(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.player.LocalPlayer) this.data).getViewVector(p0));
    }

    public void refreshPositionAndAngles(double p0, double p1, double p2, float p3, float p4) {
        ((net.minecraft.client.player.LocalPlayer) this.data).moveTo(p0, p1, p2, p3, p4);
    }

    public void refreshPositionAndAngles(cn.jstxjf_.rte.mapping.holder.BlockPos p0, float p1, float p2) {
        ((net.minecraft.client.player.LocalPlayer) this.data).moveTo(((net.minecraft.core.BlockPos) p0.data), p1, p2);
    }

    public void kill() {
        ((net.minecraft.client.player.LocalPlayer) this.data).kill();
    }

    public void useBook(cn.jstxjf_.rte.mapping.holder.ItemStack p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        ((net.minecraft.client.player.LocalPlayer) this.data).openItemGui(((net.minecraft.world.item.ItemStack) p0.data), p1.data);
    }

    public boolean isCustomNameVisible() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isCustomNameVisible();
    }

    public void refreshPositionAfterTeleport(double p0, double p1, double p2) {
        ((net.minecraft.client.player.LocalPlayer) this.data).moveTo(p0, p1, p2);
    }

    public void refreshPositionAfterTeleport(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).moveTo(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.EntityPose getPose() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(((net.minecraft.client.player.LocalPlayer) this.data).getPose());
    }

    public final float getMaxHealth() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getMaxHealth();
    }

    public void setVelocity(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setDeltaMovement(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public void setVelocity(double p0, double p1, double p2) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setDeltaMovement(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.player.LocalPlayer) this.data).position());
    }

    public cn.jstxjf_.rte.mapping.holder.Text getCustomName() {
        final var v0 = ((net.minecraft.client.player.LocalPlayer) this.data).getCustomName();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(v0));
    }

    public cn.jstxjf_.rte.mapping.holder.SoundEvent getEatSound(cn.jstxjf_.rte.mapping.holder.ItemStack p0) {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(((net.minecraft.client.player.LocalPlayer) this.data).getEatingSound(((net.minecraft.world.item.ItemStack) p0.data)));
    }

    public boolean doesNotCollide(double p0, double p1, double p2) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isFree(p0, p1, p2);
    }

    public java.util.Set<java.lang.String> getCommandTags() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getTags();
    }

    public boolean isAlive() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isAlive();
    }

    public boolean collidesWithStateAtPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isColliding(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public boolean shouldRender(double p0, double p1, double p2) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).shouldRender(p0, p1, p2);
    }

    public boolean shouldRender(double p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).shouldRenderAtSqrDistance(p0);
    }

    public void setPose(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).setPose(p0.data);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag getShoulderEntityRight() {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.client.player.LocalPlayer) this.data).getShoulderEntityRight());
    }

    public boolean dropSelectedItem(boolean p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).drop(p0);
    }

    public boolean shouldDamagePlayer(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).canHarmPlayer(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public float getYaw(float p0) {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getViewYRot(p0);
    }

    public final float getStandingEyeHeight() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).getEyeHeight();
    }

    public boolean isSprinting() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).isSprinting();
    }

    public void attack(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).attack(((net.minecraft.world.entity.Entity) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Hand getPreferredHandMapped() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(((net.minecraft.client.player.LocalPlayer) this.data).swingingArm);
    }

    public void setPreferredHandMapped(cn.jstxjf_.rte.mapping.holder.Hand p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).swingingArm = p0.data;
    }

    public float getBodyYawMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).yBodyRot;
    }

    public void setBodyYawMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).yBodyRot = p0;
    }

    public float getExperienceProgressMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).experienceProgress;
    }

    public void setExperienceProgressMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).experienceProgress = p0;
    }

    public int getTotalExperienceMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).totalExperience;
    }

    public void setTotalExperienceMapped(int p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).totalExperience = p0;
    }

    public float getSidewaysSpeedMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).xxa;
    }

    public void setSidewaysSpeedMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).xxa = p0;
    }

    public float getForwardSpeedMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).zza;
    }

    public void setForwardSpeedMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).zza = p0;
    }

    public float getFallDistanceMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).fallDistance;
    }

    public void setFallDistanceMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).fallDistance = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.ClientWorld getClientWorldMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ClientWorld(((net.minecraft.client.player.LocalPlayer) this.data).clientLevel);
    }

    public float getHeadYawMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).yHeadRot;
    }

    public void setHeadYawMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).yHeadRot = p0;
    }

    public int getDefaultMaxHealthMapped() {
        java.util.Objects.requireNonNull(((net.minecraft.client.player.LocalPlayer) this.data));
        return 20;
    }

    public float getUpwardSpeedMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).yya;
    }

    public void setUpwardSpeedMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).yya = p0;
    }

    public float getPrevBodyYawMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).yBodyRotO;
    }

    public void setPrevBodyYawMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).yBodyRotO = p0;
    }

    public int getHurtTimeMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).hurtTime;
    }

    public void setHurtTimeMapped(int p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).hurtTime = p0;
    }

    public boolean getHorizontalCollisionMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).horizontalCollision;
    }

    public void setHorizontalCollisionMapped(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).horizontalCollision = p0;
    }

    public boolean getNoClipMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).noPhysics;
    }

    public void setNoClipMapped(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).noPhysics = p0;
    }

    public boolean getVerticalCollisionMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).verticalCollision;
    }

    public void setVerticalCollisionMapped(boolean p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).verticalCollision = p0;
    }

    public float getPrevHeadYawMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).yHeadRotO;
    }

    public void setPrevHeadYawMapped(float p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).yHeadRotO = p0;
    }

    public int getDeathTimeMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).deathTime;
    }

    public void setDeathTimeMapped(int p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).deathTime = p0;
    }

    public int getExperienceLevelMapped() {
        return ((net.minecraft.client.player.LocalPlayer) this.data).experienceLevel;
    }

    public void setExperienceLevelMapped(int p0) {
        ((net.minecraft.client.player.LocalPlayer) this.data).experienceLevel = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.ClientPlayNetworkHandler getNetworkHandlerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.ClientPlayNetworkHandler(((net.minecraft.client.player.LocalPlayer) this.data).connection);
    }
}
