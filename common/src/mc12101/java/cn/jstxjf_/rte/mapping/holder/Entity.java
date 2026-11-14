package cn.jstxjf_.rte.mapping.holder;

public final class Entity extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.entity.Entity> {

    public Entity(net.minecraft.world.entity.Entity p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Entity cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Entity((net.minecraft.world.entity.Entity) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.entity.Entity;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.World getEntityWorld() {
        return new cn.jstxjf_.rte.mapping.holder.World(((net.minecraft.world.entity.Entity) this.data).getCommandSenderWorld());
    }

    public void updatePositionAndAngles(double p0, double p1, double p2, float p3, float p4) {
        ((net.minecraft.world.entity.Entity) this.data).absMoveTo(p0, p1, p2, p3, p4);
    }

    public boolean hasPassenger(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.Entity) this.data).hasPassenger(((net.minecraft.world.entity.Entity) p0.data));
    }

    public void setSilent(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).setSilent(p0);
    }

    public static void setRenderDistanceMultiplier(double p0) {
        net.minecraft.world.entity.Entity.setViewScale(p0);
    }

    public void tick() {
        ((net.minecraft.world.entity.Entity) this.data).tick();
    }

    public boolean isOnFire() {
        return ((net.minecraft.world.entity.Entity) this.data).isOnFire();
    }

    public void addVelocity(double p0, double p1, double p2) {
        ((net.minecraft.world.entity.Entity) this.data).push(p0, p1, p2);
    }

    public void setSprinting(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).setSprinting(p0);
    }

    public void setCustomName(cn.jstxjf_.rte.mapping.holder.Text p0) {
        ((net.minecraft.world.entity.Entity) this.data).setCustomName((p0 == null ? null : ((net.minecraft.network.chat.Component) p0.data)));
    }

    public double offsetX(double p0) {
        return ((net.minecraft.world.entity.Entity) this.data).getX(p0);
    }

    public double offsetZ(double p0) {
        return ((net.minecraft.world.entity.Entity) this.data).getZ(p0);
    }

    public boolean isSwimming() {
        return ((net.minecraft.world.entity.Entity) this.data).isSwimming();
    }

    public void updatePosition(double p0, double p1, double p2) {
        ((net.minecraft.world.entity.Entity) this.data).absMoveTo(p0, p1, p2);
    }

    public void baseTick() {
        ((net.minecraft.world.entity.Entity) this.data).baseTick();
    }

    public void setUuid(java.util.UUID p0) {
        ((net.minecraft.world.entity.Entity) this.data).setUUID(p0);
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getCameraPosVec(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.Entity) this.data).getEyePosition(p0));
    }

    public float getPitch(float p0) {
        return ((net.minecraft.world.entity.Entity) this.data).getViewXRot(p0);
    }

    public double getBodyY(double p0) {
        return ((net.minecraft.world.entity.Entity) this.data).getY(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interactAt(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.Hand p2) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.entity.Entity) this.data).interactAt(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), p2.data));
    }

    public boolean isInvisible() {
        return ((net.minecraft.world.entity.Entity) this.data).isInvisible();
    }

    public final float getWidth() {
        return ((net.minecraft.world.entity.Entity) this.data).getBbWidth();
    }

    public void playSound(cn.jstxjf_.rte.mapping.holder.SoundEvent p0, float p1, float p2) {
        ((net.minecraft.world.entity.Entity) this.data).playSound(((net.minecraft.sounds.SoundEvent) p0.data), p1, p2);
    }

    public static double getRenderDistanceMultiplier() {
        return net.minecraft.world.entity.Entity.getViewScale();
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer() {
        final var v0 = ((net.minecraft.world.entity.Entity) this.data).getServer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.MinecraftServer(v0));
    }

    public float applyMirror(cn.jstxjf_.rte.mapping.holder.Mirror p0) {
        return ((net.minecraft.world.entity.Entity) this.data).mirror(p0.data);
    }

    public boolean isDescending() {
        return ((net.minecraft.world.entity.Entity) this.data).isDescending();
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.entity.Entity) this.data).getDisplayName());
    }

    public double squaredDistanceTo(double p0, double p1, double p2) {
        return ((net.minecraft.world.entity.Entity) this.data).distanceToSqr(p0, p1, p2);
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.Entity) this.data).distanceToSqr(((net.minecraft.world.entity.Entity) p0.data));
    }

    public double squaredDistanceTo(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return ((net.minecraft.world.entity.Entity) this.data).distanceToSqr(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public boolean canBeSpectated(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0) {
        return ((net.minecraft.world.entity.Entity) this.data).broadcastToPlayer(((net.minecraft.server.level.ServerPlayer) p0.data));
    }

    public void setHeadYaw(float p0) {
        ((net.minecraft.world.entity.Entity) this.data).setYHeadRot(p0);
    }

    public void setOnGround(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).setOnGround(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag writeNbt(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(((net.minecraft.world.entity.Entity) this.data).saveWithoutId(((net.minecraft.nbt.CompoundTag) p0.data)));
    }

    public float getEffectiveExplosionResistance(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, cn.jstxjf_.rte.mapping.holder.FluidState p4, float p5) {
        return ((net.minecraft.world.entity.Entity) this.data).getBlockExplosionResistance(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), ((net.minecraft.world.level.material.FluidState) p4.data), p5);
    }

    public boolean hasCustomName() {
        return ((net.minecraft.world.entity.Entity) this.data).hasCustomName();
    }

    public boolean isAttackable() {
        return ((net.minecraft.world.entity.Entity) this.data).isAttackable();
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d method_30950(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.Entity) this.data).getPosition(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getVehicle() {
        final var v0 = ((net.minecraft.world.entity.Entity) this.data).getVehicle();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public double method_29241() {
        return ((net.minecraft.world.entity.Entity) this.data).getFluidJumpThreshold();
    }

    public int hashCode() {
        return ((net.minecraft.world.entity.Entity) this.data).hashCode();
    }

    public boolean startRiding(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.Entity) this.data).startRiding(((net.minecraft.world.entity.Entity) p0.data));
    }

    public boolean startRiding(cn.jstxjf_.rte.mapping.holder.Entity p0, boolean p1) {
        return ((net.minecraft.world.entity.Entity) this.data).startRiding(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public void updateSwimming() {
        ((net.minecraft.world.entity.Entity) this.data).updateSwimming();
    }

    public void setNoGravity(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).setNoGravity(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName() {
        return new cn.jstxjf_.rte.mapping.holder.Text(((net.minecraft.world.entity.Entity) this.data).getName());
    }

    public void onPlayerCollision(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        ((net.minecraft.world.entity.Entity) this.data).playerTouch(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.HitResult raycast(double p0, float p1, boolean p2) {
        return new cn.jstxjf_.rte.mapping.holder.HitResult(((net.minecraft.world.entity.Entity) this.data).pick(p0, p1, p2));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d updatePassengerForDismount(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.Entity) this.data).getDismountLocationForPassenger(((net.minecraft.world.entity.LivingEntity) p0.data)));
    }

    public void setInvulnerable(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).setInvulnerable(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getRootVehicle() {
        return new cn.jstxjf_.rte.mapping.holder.Entity(((net.minecraft.world.entity.Entity) this.data).getRootVehicle());
    }

    public java.lang.String getUuidAsString() {
        return ((net.minecraft.world.entity.Entity) this.data).getStringUUID();
    }

    public boolean isSilent() {
        return ((net.minecraft.world.entity.Entity) this.data).isSilent();
    }

    public void setSwimming(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).setSwimming(p0);
    }

    public void checkDespawn() {
        ((net.minecraft.world.entity.Entity) this.data).checkDespawn();
    }

    public final float getHeight() {
        return ((net.minecraft.world.entity.Entity) this.data).getBbHeight();
    }

    public boolean isPushable() {
        return ((net.minecraft.world.entity.Entity) this.data).isPushable();
    }

    public boolean isSneaking() {
        return ((net.minecraft.world.entity.Entity) this.data).isShiftKeyDown();
    }

    public double getRandomBodyY() {
        return ((net.minecraft.world.entity.Entity) this.data).getRandomY();
    }

    public void tickRiding() {
        ((net.minecraft.world.entity.Entity) this.data).rideTick();
    }

    public boolean addScoreboardTag(java.lang.String p0) {
        return ((net.minecraft.world.entity.Entity) this.data).addTag(p0);
    }

    public void changeLookDirection(double p0, double p1) {
        ((net.minecraft.world.entity.Entity) this.data).turn(p0, p1);
    }

    public boolean canExplosionDestroyBlock(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, float p4) {
        return ((net.minecraft.world.entity.Entity) this.data).shouldBlockExplode(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), p4);
    }

    public void updateTrackedHeadRotation(float p0, int p1) {
        ((net.minecraft.world.entity.Entity) this.data).lerpHeadTo(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getHorizontalFacing() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.entity.Entity) this.data).getDirection());
    }

    public final double getY() {
        return ((net.minecraft.world.entity.Entity) this.data).getY();
    }

    public final double getX() {
        return ((net.minecraft.world.entity.Entity) this.data).getX();
    }

    public final double getZ() {
        return ((net.minecraft.world.entity.Entity) this.data).getZ();
    }

    public boolean hasPermissionLevel(int p0) {
        return ((net.minecraft.world.entity.Entity) this.data).hasPermissions(p0);
    }

    public double getEyeY() {
        return ((net.minecraft.world.entity.Entity) this.data).getEyeY();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.world.entity.Entity) this.data).blockPosition());
    }

    public double getParticleZ(double p0) {
        return ((net.minecraft.world.entity.Entity) this.data).getRandomZ(p0);
    }

    public double getParticleX(double p0) {
        return ((net.minecraft.world.entity.Entity) this.data).getRandomX(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interact(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(((net.minecraft.world.entity.Entity) this.data).interact(((net.minecraft.world.entity.player.Player) p0.data), p1.data));
    }

    public java.lang.String getEntityName() {
        return ((net.minecraft.world.entity.Entity) this.data).getScoreboardName();
    }

    public void updateVelocity(float p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        ((net.minecraft.world.entity.Entity) this.data).moveRelative(p0, ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public void setCustomNameVisible(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).setCustomNameVisible(p0);
    }

    public float distanceTo(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return ((net.minecraft.world.entity.Entity) this.data).distanceTo(((net.minecraft.world.entity.Entity) p0.data));
    }

    public boolean isAlive() {
        return ((net.minecraft.world.entity.Entity) this.data).isAlive();
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getOppositeRotationVector(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.Entity) this.data).getUpVector(p0));
    }

    public java.util.UUID getUuid() {
        return ((net.minecraft.world.entity.Entity) this.data).getUUID();
    }

    public boolean removeScoreboardTag(java.lang.String p0) {
        return ((net.minecraft.world.entity.Entity) this.data).removeTag(p0);
    }

    public void kill() {
        ((net.minecraft.world.entity.Entity) this.data).kill();
    }

    public void slowMovement(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        ((net.minecraft.world.entity.Entity) this.data).makeStuckInBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public float getEyeHeight(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return ((net.minecraft.world.entity.Entity) this.data).getEyeHeight(p0.data);
    }

    public boolean isSpectator() {
        return ((net.minecraft.world.entity.Entity) this.data).isSpectator();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getMovementDirection() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(((net.minecraft.world.entity.Entity) this.data).getMotionDirection());
    }

    public float applyRotation(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return ((net.minecraft.world.entity.Entity) this.data).rotate(p0.data);
    }

    public void setInvisible(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).setInvisible(p0);
    }

    public void stopRiding() {
        ((net.minecraft.world.entity.Entity) this.data).stopRiding();
    }

    public boolean isInvisibleTo(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return ((net.minecraft.world.entity.Entity) this.data).isInvisibleTo(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public void handleStatus(byte p0) {
        ((net.minecraft.world.entity.Entity) this.data).handleEntityEvent(p0);
    }

    public boolean isInLava() {
        return ((net.minecraft.world.entity.Entity) this.data).isInLava();
    }

    public boolean isInvulnerable() {
        return ((net.minecraft.world.entity.Entity) this.data).isInvulnerable();
    }

    public boolean isInRange(cn.jstxjf_.rte.mapping.holder.Entity p0, double p1) {
        return ((net.minecraft.world.entity.Entity) this.data).closerThan(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getRotationVec(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.Entity) this.data).getViewVector(p0));
    }

    public void refreshPositionAndAngles(double p0, double p1, double p2, float p3, float p4) {
        ((net.minecraft.world.entity.Entity) this.data).moveTo(p0, p1, p2, p3, p4);
    }

    public void refreshPositionAndAngles(cn.jstxjf_.rte.mapping.holder.BlockPos p0, float p1, float p2) {
        ((net.minecraft.world.entity.Entity) this.data).moveTo(((net.minecraft.core.BlockPos) p0.data), p1, p2);
    }

    public float getYaw(float p0) {
        return ((net.minecraft.world.entity.Entity) this.data).getViewYRot(p0);
    }

    public boolean isCustomNameVisible() {
        return ((net.minecraft.world.entity.Entity) this.data).isCustomNameVisible();
    }

    public void refreshPositionAfterTeleport(double p0, double p1, double p2) {
        ((net.minecraft.world.entity.Entity) this.data).moveTo(p0, p1, p2);
    }

    public void refreshPositionAfterTeleport(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.world.entity.Entity) this.data).moveTo(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.EntityPose getPose() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(((net.minecraft.world.entity.Entity) this.data).getPose());
    }

    public void setVelocity(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        ((net.minecraft.world.entity.Entity) this.data).setDeltaMovement(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public void setVelocity(double p0, double p1, double p2) {
        ((net.minecraft.world.entity.Entity) this.data).setDeltaMovement(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.world.entity.Entity) this.data).position());
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getSoundCategory() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(((net.minecraft.world.entity.Entity) this.data).getSoundSource());
    }

    public cn.jstxjf_.rte.mapping.holder.Text getCustomName() {
        final var v0 = ((net.minecraft.world.entity.Entity) this.data).getCustomName();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(v0));
    }

    public boolean doesNotCollide(double p0, double p1, double p2) {
        return ((net.minecraft.world.entity.Entity) this.data).isFree(p0, p1, p2);
    }

    public java.util.Set<java.lang.String> getCommandTags() {
        return ((net.minecraft.world.entity.Entity) this.data).getTags();
    }

    public void pushAwayFrom(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        ((net.minecraft.world.entity.Entity) this.data).push(((net.minecraft.world.entity.Entity) p0.data));
    }

    public boolean collidesWithStateAtPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return ((net.minecraft.world.entity.Entity) this.data).isColliding(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public boolean shouldRender(double p0, double p1, double p2) {
        return ((net.minecraft.world.entity.Entity) this.data).shouldRender(p0, p1, p2);
    }

    public boolean shouldRender(double p0) {
        return ((net.minecraft.world.entity.Entity) this.data).shouldRenderAtSqrDistance(p0);
    }

    public void setPose(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        ((net.minecraft.world.entity.Entity) this.data).setPose(p0.data);
    }

    public final float getStandingEyeHeight() {
        return ((net.minecraft.world.entity.Entity) this.data).getEyeHeight();
    }

    public void setBodyYaw(float p0) {
        ((net.minecraft.world.entity.Entity) this.data).setYBodyRot(p0);
    }

    public boolean isSprinting() {
        return ((net.minecraft.world.entity.Entity) this.data).isSprinting();
    }

    public float getFallDistanceMapped() {
        return ((net.minecraft.world.entity.Entity) this.data).fallDistance;
    }

    public void setFallDistanceMapped(float p0) {
        ((net.minecraft.world.entity.Entity) this.data).fallDistance = p0;
    }

    public boolean getHorizontalCollisionMapped() {
        return ((net.minecraft.world.entity.Entity) this.data).horizontalCollision;
    }

    public void setHorizontalCollisionMapped(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).horizontalCollision = p0;
    }

    public boolean getNoClipMapped() {
        return ((net.minecraft.world.entity.Entity) this.data).noPhysics;
    }

    public void setNoClipMapped(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).noPhysics = p0;
    }

    public boolean getVerticalCollisionMapped() {
        return ((net.minecraft.world.entity.Entity) this.data).verticalCollision;
    }

    public void setVerticalCollisionMapped(boolean p0) {
        ((net.minecraft.world.entity.Entity) this.data).verticalCollision = p0;
    }
}
