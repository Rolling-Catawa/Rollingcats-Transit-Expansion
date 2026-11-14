package cn.jstxjf_.rte.mapping.holder;

public abstract class EntityAbstractMapping extends net.minecraft.world.entity.Entity {

    public cn.jstxjf_.rte.mapping.holder.World getEntityWorld2() {
        return new cn.jstxjf_.rte.mapping.holder.World(super.getCommandSenderWorld());
    }

    public final net.minecraft.world.level.Level getCommandSenderWorld() {
        final var v0 = this.getEntityWorld2();
        return (v0 == null ? null : ((net.minecraft.world.level.Level) v0.data));
    }

    protected void removePassenger2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        super.removePassenger(((net.minecraft.world.entity.Entity) p0.data));
    }

    protected final void removePassenger(net.minecraft.world.entity.Entity p0) {
        this.removePassenger2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public void updatePositionAndAngles2(double p0, double p1, double p2, float p3, float p4) {
        super.absMoveTo(p0, p1, p2, p3, p4);
    }

    public final void absMoveTo(double p0, double p1, double p2, float p3, float p4) {
        this.updatePositionAndAngles2(p0, p1, p2, p3, p4);
    }

    public boolean hasPassenger2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return super.hasPassenger(((net.minecraft.world.entity.Entity) p0.data));
    }

    public final boolean hasPassenger(net.minecraft.world.entity.Entity p0) {
        return this.hasPassenger2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public void setSilent2(boolean p0) {
        super.setSilent(p0);
    }

    public final void setSilent(boolean p0) {
        this.setSilent2(p0);
    }

    public static void setRenderDistanceMultiplier2(double p0) {
        net.minecraft.world.entity.Entity.setViewScale(p0);
    }

    public void tick2() {
        super.tick();
    }

    public final void tick() {
        this.tick2();
    }

    public boolean isOnFire2() {
        return super.isOnFire();
    }

    public final boolean isOnFire() {
        return this.isOnFire2();
    }

    protected cn.jstxjf_.rte.mapping.holder.SoundEvent getSwimSound2() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(super.getSwimSound());
    }

    protected final net.minecraft.sounds.SoundEvent getSwimSound() {
        final var v0 = this.getSwimSound2();
        return (v0 == null ? null : ((net.minecraft.sounds.SoundEvent) v0.data));
    }

    public void addVelocity2(double p0, double p1, double p2) {
        super.push(p0, p1, p2);
    }

    public final void push(double p0, double p1, double p2) {
        this.addVelocity2(p0, p1, p2);
    }

    public void setSprinting2(boolean p0) {
        super.setSprinting(p0);
    }

    public final void setSprinting(boolean p0) {
        this.setSprinting2(p0);
    }

    public void setCustomName2(cn.jstxjf_.rte.mapping.holder.Text p0) {
        super.setCustomName((p0 == null ? null : ((net.minecraft.network.chat.Component) p0.data)));
    }

    public final void setCustomName(net.minecraft.network.chat.Component p0) {
        this.setCustomName2((p0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(p0)));
    }

    public double offsetX2(double p0) {
        return super.getX(p0);
    }

    public final double getX(double p0) {
        return this.offsetX2(p0);
    }

    public double offsetZ2(double p0) {
        return super.getZ(p0);
    }

    public final double getZ(double p0) {
        return this.offsetZ2(p0);
    }

    public boolean isSwimming2() {
        return super.isSwimming();
    }

    public final boolean isSwimming() {
        return this.isSwimming2();
    }

    public void updatePosition2(double p0, double p1, double p2) {
        super.absMoveTo(p0, p1, p2);
    }

    public final void absMoveTo(double p0, double p1, double p2) {
        this.updatePosition2(p0, p1, p2);
    }

    public void baseTick2() {
        super.baseTick();
    }

    public final void baseTick() {
        this.baseTick2();
    }

    public void setUuid2(java.util.UUID p0) {
        super.setUUID(p0);
    }

    public final void setUUID(java.util.UUID p0) {
        this.setUuid2(p0);
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getCameraPosVec2(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(super.getEyePosition(p0));
    }

    public float getPitch2(float p0) {
        return super.getViewXRot(p0);
    }

    public final float getViewXRot(float p0) {
        return this.getPitch2(p0);
    }

    public double getBodyY2(double p0) {
        return super.getY(p0);
    }

    public final double getY(double p0) {
        return this.getBodyY2(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interactAt2(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1, cn.jstxjf_.rte.mapping.holder.Hand p2) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(super.interactAt(((net.minecraft.world.entity.player.Player) p0.data), ((net.minecraft.world.phys.Vec3) p1.data), p2.data));
    }

    public final net.minecraft.world.InteractionResult interactAt(net.minecraft.world.entity.player.Player p0, net.minecraft.world.phys.Vec3 p1, net.minecraft.world.InteractionHand p2) {
        final var v0 = this.interactAt2(new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p0), new cn.jstxjf_.rte.mapping.holder.Vector3d(p1), cn.jstxjf_.rte.mapping.holder.Hand.convert(p2));
        return (v0 == null ? null : v0.data);
    }

    public boolean isInvisible2() {
        return super.isInvisible();
    }

    public final boolean isInvisible() {
        return this.isInvisible2();
    }

    public final float getWidth2() {
        return super.getBbWidth();
    }

    public void playSound2(cn.jstxjf_.rte.mapping.holder.SoundEvent p0, float p1, float p2) {
        super.playSound(((net.minecraft.sounds.SoundEvent) p0.data), p1, p2);
    }

    public final void playSound(net.minecraft.sounds.SoundEvent p0, float p1, float p2) {
        this.playSound2(new cn.jstxjf_.rte.mapping.holder.SoundEvent(p0), p1, p2);
    }

    public static double getRenderDistanceMultiplier2() {
        return net.minecraft.world.entity.Entity.getViewScale();
    }

    public cn.jstxjf_.rte.mapping.holder.MinecraftServer getServer2() {
        final var v0 = super.getServer();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.MinecraftServer(v0));
    }

    public final net.minecraft.server.MinecraftServer getServer() {
        final var v0 = this.getServer2();
        return (v0 == null ? null : ((net.minecraft.server.MinecraftServer) v0.data));
    }

    public float applyMirror2(cn.jstxjf_.rte.mapping.holder.Mirror p0) {
        return super.mirror(p0.data);
    }

    public final float mirror(net.minecraft.world.level.block.Mirror p0) {
        return this.applyMirror2(cn.jstxjf_.rte.mapping.holder.Mirror.convert(p0));
    }

    public boolean isDescending2() {
        return super.isDescending();
    }

    public final boolean isDescending() {
        return this.isDescending2();
    }

    public cn.jstxjf_.rte.mapping.holder.Text getDisplayName2() {
        return new cn.jstxjf_.rte.mapping.holder.Text(super.getDisplayName());
    }

    public final net.minecraft.network.chat.Component getDisplayName() {
        final var v0 = this.getDisplayName2();
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public double squaredDistanceTo2(double p0, double p1, double p2) {
        return super.distanceToSqr(p0, p1, p2);
    }

    public final double distanceToSqr(double p0, double p1, double p2) {
        return this.squaredDistanceTo2(p0, p1, p2);
    }

    public double squaredDistanceTo2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return super.distanceToSqr(((net.minecraft.world.entity.Entity) p0.data));
    }

    public final double distanceToSqr(net.minecraft.world.entity.Entity p0) {
        return this.squaredDistanceTo2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public double squaredDistanceTo2(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return super.distanceToSqr(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public final double distanceToSqr(net.minecraft.world.phys.Vec3 p0) {
        return this.squaredDistanceTo2(new cn.jstxjf_.rte.mapping.holder.Vector3d(p0));
    }

    public boolean canBeSpectated2(cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity p0) {
        return super.broadcastToPlayer(((net.minecraft.server.level.ServerPlayer) p0.data));
    }

    public final boolean broadcastToPlayer(net.minecraft.server.level.ServerPlayer p0) {
        return this.canBeSpectated2(new cn.jstxjf_.rte.mapping.holder.ServerPlayerEntity(p0));
    }

    public void setHeadYaw2(float p0) {
        super.setYHeadRot(p0);
    }

    public final void setYHeadRot(float p0) {
        this.setHeadYaw2(p0);
    }

    public void setOnGround2(boolean p0) {
        super.setOnGround(p0);
    }

    public final void setOnGround(boolean p0) {
        this.setOnGround2(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag writeNbt2(cn.jstxjf_.rte.mapping.holder.CompoundTag p0) {
        return new cn.jstxjf_.rte.mapping.holder.CompoundTag(super.saveWithoutId(((net.minecraft.nbt.CompoundTag) p0.data)));
    }

    public final net.minecraft.nbt.CompoundTag saveWithoutId(net.minecraft.nbt.CompoundTag p0) {
        final var v0 = this.writeNbt2(new cn.jstxjf_.rte.mapping.holder.CompoundTag(p0));
        return (v0 == null ? null : ((net.minecraft.nbt.CompoundTag) v0.data));
    }

    public float getEffectiveExplosionResistance2(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, cn.jstxjf_.rte.mapping.holder.FluidState p4, float p5) {
        return super.getBlockExplosionResistance(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), ((net.minecraft.world.level.material.FluidState) p4.data), p5);
    }

    public final float getBlockExplosionResistance(net.minecraft.world.level.Explosion p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2, net.minecraft.world.level.block.state.BlockState p3, net.minecraft.world.level.material.FluidState p4, float p5) {
        return this.getEffectiveExplosionResistance2(new cn.jstxjf_.rte.mapping.holder.Explosion(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.BlockState(p3), new cn.jstxjf_.rte.mapping.holder.FluidState(p4), p5);
    }

    public boolean hasCustomName2() {
        return super.hasCustomName();
    }

    public final boolean hasCustomName() {
        return this.hasCustomName2();
    }

    public boolean isAttackable2() {
        return super.isAttackable();
    }

    public final boolean isAttackable() {
        return this.isAttackable2();
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d method_309502(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(super.getPosition(p0));
    }

    protected abstract void initDataTracker2();

    protected final void defineSynchedData() {
        this.initDataTracker2();
    }

    protected cn.jstxjf_.rte.mapping.holder.Text getDefaultName2() {
        return new cn.jstxjf_.rte.mapping.holder.Text(super.getTypeName());
    }

    protected final net.minecraft.network.chat.Component getTypeName() {
        final var v0 = this.getDefaultName2();
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getVehicle2() {
        final var v0 = super.getVehicle();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Entity(v0));
    }

    public final net.minecraft.world.entity.Entity getVehicle() {
        final var v0 = this.getVehicle2();
        return (v0 == null ? null : ((net.minecraft.world.entity.Entity) v0.data));
    }

    public double method_292412() {
        return super.getFluidJumpThreshold();
    }

    public final double getFluidJumpThreshold() {
        return this.method_292412();
    }

    public int hashCode2() {
        return super.hashCode();
    }

    public final int hashCode() {
        return this.hashCode2();
    }

    public boolean startRiding2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return super.startRiding(((net.minecraft.world.entity.Entity) p0.data));
    }

    public final boolean startRiding(net.minecraft.world.entity.Entity p0) {
        return this.startRiding2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public boolean startRiding2(cn.jstxjf_.rte.mapping.holder.Entity p0, boolean p1) {
        return super.startRiding(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public final boolean startRiding(net.minecraft.world.entity.Entity p0, boolean p1) {
        return this.startRiding2(new cn.jstxjf_.rte.mapping.holder.Entity(p0), p1);
    }

    protected void setFlag2(int p0, boolean p1) {
        super.setSharedFlag(p0, p1);
    }

    protected final void setSharedFlag(int p0, boolean p1) {
        this.setFlag2(p0, p1);
    }

    public void updateSwimming2() {
        super.updateSwimming();
    }

    public final void updateSwimming() {
        this.updateSwimming2();
    }

    public void setNoGravity2(boolean p0) {
        super.setNoGravity(p0);
    }

    public final void setNoGravity(boolean p0) {
        this.setNoGravity2(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Text getName2() {
        return new cn.jstxjf_.rte.mapping.holder.Text(super.getName());
    }

    public final net.minecraft.network.chat.Component getName() {
        final var v0 = this.getName2();
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public void onPlayerCollision2(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        super.playerTouch(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public final void playerTouch(net.minecraft.world.entity.player.Player p0) {
        this.onPlayerCollision2(new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.HitResult raycast2(double p0, float p1, boolean p2) {
        return new cn.jstxjf_.rte.mapping.holder.HitResult(super.pick(p0, p1, p2));
    }

    public final net.minecraft.world.phys.HitResult pick(double p0, float p1, boolean p2) {
        final var v0 = this.raycast2(p0, p1, p2);
        return (v0 == null ? null : ((net.minecraft.world.phys.HitResult) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d updatePassengerForDismount2(cn.jstxjf_.rte.mapping.holder.LivingEntity p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(super.getDismountLocationForPassenger(((net.minecraft.world.entity.LivingEntity) p0.data)));
    }

    public final net.minecraft.world.phys.Vec3 getDismountLocationForPassenger(net.minecraft.world.entity.LivingEntity p0) {
        final var v0 = this.updatePassengerForDismount2(new cn.jstxjf_.rte.mapping.holder.LivingEntity(p0));
        return (v0 == null ? null : ((net.minecraft.world.phys.Vec3) v0.data));
    }

    public void setInvulnerable2(boolean p0) {
        super.setInvulnerable(p0);
    }

    public final void setInvulnerable(boolean p0) {
        this.setInvulnerable2(p0);
    }

    protected boolean getFlag2(int p0) {
        return super.getSharedFlag(p0);
    }

    protected final boolean getSharedFlag(int p0) {
        return this.getFlag2(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getRootVehicle2() {
        return new cn.jstxjf_.rte.mapping.holder.Entity(super.getRootVehicle());
    }

    public final net.minecraft.world.entity.Entity getRootVehicle() {
        final var v0 = this.getRootVehicle2();
        return (v0 == null ? null : ((net.minecraft.world.entity.Entity) v0.data));
    }

    public java.lang.String getUuidAsString2() {
        return super.getStringUUID();
    }

    public final java.lang.String getStringUUID() {
        return this.getUuidAsString2();
    }

    public boolean isSilent2() {
        return super.isSilent();
    }

    public final boolean isSilent() {
        return this.isSilent2();
    }

    public void setSwimming2(boolean p0) {
        super.setSwimming(p0);
    }

    public final void setSwimming(boolean p0) {
        this.setSwimming2(p0);
    }

    public void checkDespawn2() {
        super.checkDespawn();
    }

    public final void checkDespawn() {
        this.checkDespawn2();
    }

    protected boolean canStartRiding2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return super.canRide(((net.minecraft.world.entity.Entity) p0.data));
    }

    protected final boolean canRide(net.minecraft.world.entity.Entity p0) {
        return this.canStartRiding2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public final float getHeight2() {
        return super.getBbHeight();
    }

    protected void playStepSound2(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        super.playStepSound(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    protected final void playStepSound(net.minecraft.core.BlockPos p0, net.minecraft.world.level.block.state.BlockState p1) {
        this.playStepSound2(new cn.jstxjf_.rte.mapping.holder.BlockPos(p0), new cn.jstxjf_.rte.mapping.holder.BlockState(p1));
    }

    protected void playSwimSound2(float p0) {
        super.playSwimSound(p0);
    }

    protected final void playSwimSound(float p0) {
        this.playSwimSound2(p0);
    }

    public boolean isPushable2() {
        return super.isPushable();
    }

    public final boolean isPushable() {
        return this.isPushable2();
    }

    public boolean isSneaking2() {
        return super.isShiftKeyDown();
    }

    public final boolean isShiftKeyDown() {
        return this.isSneaking2();
    }

    public double getRandomBodyY2() {
        return super.getRandomY();
    }

    public final double getRandomY() {
        return this.getRandomBodyY2();
    }

    public void tickRiding2() {
        super.rideTick();
    }

    public final void rideTick() {
        this.tickRiding2();
    }

    public boolean addScoreboardTag2(java.lang.String p0) {
        return super.addTag(p0);
    }

    public final boolean addTag(java.lang.String p0) {
        return this.addScoreboardTag2(p0);
    }

    public EntityAbstractMapping(cn.jstxjf_.rte.mapping.holder.EntityType<?> p0, cn.jstxjf_.rte.mapping.holder.World p1) {
        super(((net.minecraft.world.entity.EntityType) p0.data), ((net.minecraft.world.level.Level) p1.data));
    }

    public void changeLookDirection2(double p0, double p1) {
        super.turn(p0, p1);
    }

    public final void turn(double p0, double p1) {
        this.changeLookDirection2(p0, p1);
    }

    public boolean canExplosionDestroyBlock2(cn.jstxjf_.rte.mapping.holder.Explosion p0, cn.jstxjf_.rte.mapping.holder.BlockView p1, cn.jstxjf_.rte.mapping.holder.BlockPos p2, cn.jstxjf_.rte.mapping.holder.BlockState p3, float p4) {
        return super.shouldBlockExplode(((net.minecraft.world.level.Explosion) p0.data), ((net.minecraft.world.level.BlockGetter) p1.data), ((net.minecraft.core.BlockPos) p2.data), ((net.minecraft.world.level.block.state.BlockState) p3.data), p4);
    }

    public final boolean shouldBlockExplode(net.minecraft.world.level.Explosion p0, net.minecraft.world.level.BlockGetter p1, net.minecraft.core.BlockPos p2, net.minecraft.world.level.block.state.BlockState p3, float p4) {
        return this.canExplosionDestroyBlock2(new cn.jstxjf_.rte.mapping.holder.Explosion(p0), new cn.jstxjf_.rte.mapping.holder.BlockView(p1), new cn.jstxjf_.rte.mapping.holder.BlockPos(p2), new cn.jstxjf_.rte.mapping.holder.BlockState(p3), p4);
    }

    public void updateTrackedHeadRotation2(float p0, int p1) {
        super.lerpHeadTo(p0, p1);
    }

    public final void lerpHeadTo(float p0, int p1) {
        this.updateTrackedHeadRotation2(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getHorizontalFacing2() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(super.getDirection());
    }

    public final net.minecraft.core.Direction getDirection() {
        final var v0 = this.getHorizontalFacing2();
        return (v0 == null ? null : v0.data);
    }

    public final double getY2() {
        return super.getY();
    }

    public final double getX2() {
        return super.getX();
    }

    public final double getZ2() {
        return super.getZ();
    }

    public boolean hasPermissionLevel2(int p0) {
        return super.hasPermissions(p0);
    }

    public final boolean hasPermissions(int p0) {
        return this.hasPermissionLevel2(p0);
    }

    public double getEyeY2() {
        return super.getEyeY();
    }

    public final double getEyeY() {
        return this.getEyeY2();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos2() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(super.blockPosition());
    }

    public final net.minecraft.core.BlockPos blockPosition() {
        final var v0 = this.getBlockPos2();
        return (v0 == null ? null : ((net.minecraft.core.BlockPos) v0.data));
    }

    public double getParticleZ2(double p0) {
        return super.getRandomZ(p0);
    }

    public final double getRandomZ(double p0) {
        return this.getParticleZ2(p0);
    }

    public double getParticleX2(double p0) {
        return super.getRandomX(p0);
    }

    public final double getRandomX(double p0) {
        return this.getParticleX2(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.ActionResult interact2(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0, cn.jstxjf_.rte.mapping.holder.Hand p1) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(super.interact(((net.minecraft.world.entity.player.Player) p0.data), p1.data));
    }

    public final net.minecraft.world.InteractionResult interact(net.minecraft.world.entity.player.Player p0, net.minecraft.world.InteractionHand p1) {
        final var v0 = this.interact2(new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p0), cn.jstxjf_.rte.mapping.holder.Hand.convert(p1));
        return (v0 == null ? null : v0.data);
    }

    public java.lang.String getEntityName2() {
        return super.getScoreboardName();
    }

    public final java.lang.String getScoreboardName() {
        return this.getEntityName2();
    }

    public void updateVelocity2(float p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        super.moveRelative(p0, ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public final void moveRelative(float p0, net.minecraft.world.phys.Vec3 p1) {
        this.updateVelocity2(p0, new cn.jstxjf_.rte.mapping.holder.Vector3d(p1));
    }

    public void setCustomNameVisible2(boolean p0) {
        super.setCustomNameVisible(p0);
    }

    public final void setCustomNameVisible(boolean p0) {
        this.setCustomNameVisible2(p0);
    }

    public float distanceTo2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return super.distanceTo(((net.minecraft.world.entity.Entity) p0.data));
    }

    public final float distanceTo(net.minecraft.world.entity.Entity p0) {
        return this.distanceTo2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public boolean isAlive2() {
        return super.isAlive();
    }

    public final boolean isAlive() {
        return this.isAlive2();
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getOppositeRotationVector2(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(super.getUpVector(p0));
    }

    public java.util.UUID getUuid2() {
        return super.getUUID();
    }

    public final java.util.UUID getUUID() {
        return this.getUuid2();
    }

    protected void pushOutOfBlocks2(double p0, double p1, double p2) {
        super.moveTowardsClosestSpace(p0, p1, p2);
    }

    protected final void moveTowardsClosestSpace(double p0, double p1, double p2) {
        this.pushOutOfBlocks2(p0, p1, p2);
    }

    public boolean removeScoreboardTag2(java.lang.String p0) {
        return super.removeTag(p0);
    }

    public final boolean removeTag(java.lang.String p0) {
        return this.removeScoreboardTag2(p0);
    }

    protected void addPassenger2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        super.addPassenger(((net.minecraft.world.entity.Entity) p0.data));
    }

    protected final void addPassenger(net.minecraft.world.entity.Entity p0) {
        this.addPassenger2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public void kill2() {
        super.kill();
    }

    public final void kill() {
        this.kill2();
    }

    public void slowMovement2(cn.jstxjf_.rte.mapping.holder.BlockState p0, cn.jstxjf_.rte.mapping.holder.Vector3d p1) {
        super.makeStuckInBlock(((net.minecraft.world.level.block.state.BlockState) p0.data), ((net.minecraft.world.phys.Vec3) p1.data));
    }

    public final void makeStuckInBlock(net.minecraft.world.level.block.state.BlockState p0, net.minecraft.world.phys.Vec3 p1) {
        this.slowMovement2(new cn.jstxjf_.rte.mapping.holder.BlockState(p0), new cn.jstxjf_.rte.mapping.holder.Vector3d(p1));
    }

    public float getEyeHeight2(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return super.getEyeHeight(p0.data);
    }

    protected void fall2(double p0, boolean p1, cn.jstxjf_.rte.mapping.holder.BlockState p2, cn.jstxjf_.rte.mapping.holder.BlockPos p3) {
        super.checkFallDamage(p0, p1, ((net.minecraft.world.level.block.state.BlockState) p2.data), ((net.minecraft.core.BlockPos) p3.data));
    }

    protected final void checkFallDamage(double p0, boolean p1, net.minecraft.world.level.block.state.BlockState p2, net.minecraft.core.BlockPos p3) {
        this.fall2(p0, p1, new cn.jstxjf_.rte.mapping.holder.BlockState(p2), new cn.jstxjf_.rte.mapping.holder.BlockPos(p3));
    }

    protected int getBurningDuration2() {
        return super.getFireImmuneTicks();
    }

    protected final int getFireImmuneTicks() {
        return this.getBurningDuration2();
    }

    protected cn.jstxjf_.rte.mapping.holder.Vector3d adjustMovementForPiston2(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(super.limitPistonMovement(((net.minecraft.world.phys.Vec3) p0.data)));
    }

    protected final net.minecraft.world.phys.Vec3 limitPistonMovement(net.minecraft.world.phys.Vec3 p0) {
        final var v0 = this.adjustMovementForPiston2(new cn.jstxjf_.rte.mapping.holder.Vector3d(p0));
        return (v0 == null ? null : ((net.minecraft.world.phys.Vec3) v0.data));
    }

    public boolean isSpectator2() {
        return super.isSpectator();
    }

    public final boolean isSpectator() {
        return this.isSpectator2();
    }

    public cn.jstxjf_.rte.mapping.holder.Direction getMovementDirection2() {
        return cn.jstxjf_.rte.mapping.holder.Direction.convert(super.getMotionDirection());
    }

    public final net.minecraft.core.Direction getMotionDirection() {
        final var v0 = this.getMovementDirection2();
        return (v0 == null ? null : v0.data);
    }

    public float applyRotation2(cn.jstxjf_.rte.mapping.holder.BlockRotation p0) {
        return super.rotate(p0.data);
    }

    public final float rotate(net.minecraft.world.level.block.Rotation p0) {
        return this.applyRotation2(cn.jstxjf_.rte.mapping.holder.BlockRotation.convert(p0));
    }

    public void setInvisible2(boolean p0) {
        super.setInvisible(p0);
    }

    public final void setInvisible(boolean p0) {
        this.setInvisible2(p0);
    }

    public void stopRiding2() {
        super.stopRiding();
    }

    public final void stopRiding() {
        this.stopRiding2();
    }

    public boolean isInvisibleTo2(cn.jstxjf_.rte.mapping.holder.PlayerEntity p0) {
        return super.isInvisibleTo(((net.minecraft.world.entity.player.Player) p0.data));
    }

    public final boolean isInvisibleTo(net.minecraft.world.entity.player.Player p0) {
        return this.isInvisibleTo2(new cn.jstxjf_.rte.mapping.holder.PlayerEntity(p0));
    }

    public void handleStatus2(byte p0) {
        super.handleEntityEvent(p0);
    }

    public final void handleEntityEvent(byte p0) {
        this.handleStatus2(p0);
    }

    public boolean isInLava2() {
        return super.isInLava();
    }

    public final boolean isInLava() {
        return this.isInLava2();
    }

    public boolean isInvulnerable2() {
        return super.isInvulnerable();
    }

    public final boolean isInvulnerable() {
        return this.isInvulnerable2();
    }

    public boolean isInRange2(cn.jstxjf_.rte.mapping.holder.Entity p0, double p1) {
        return super.closerThan(((net.minecraft.world.entity.Entity) p0.data), p1);
    }

    public final boolean closerThan(net.minecraft.world.entity.Entity p0, double p1) {
        return this.isInRange2(new cn.jstxjf_.rte.mapping.holder.Entity(p0), p1);
    }

    public final cn.jstxjf_.rte.mapping.holder.Vector3d getRotationVec2(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(super.getViewVector(p0));
    }

    public void refreshPositionAndAngles2(double p0, double p1, double p2, float p3, float p4) {
        super.moveTo(p0, p1, p2, p3, p4);
    }

    public final void moveTo(double p0, double p1, double p2, float p3, float p4) {
        this.refreshPositionAndAngles2(p0, p1, p2, p3, p4);
    }

    public void refreshPositionAndAngles2(cn.jstxjf_.rte.mapping.holder.BlockPos p0, float p1, float p2) {
        super.moveTo(((net.minecraft.core.BlockPos) p0.data), p1, p2);
    }

    public final void moveTo(net.minecraft.core.BlockPos p0, float p1, float p2) {
        this.refreshPositionAndAngles2(new cn.jstxjf_.rte.mapping.holder.BlockPos(p0), p1, p2);
    }

    public float getYaw2(float p0) {
        return super.getViewYRot(p0);
    }

    public final float getViewYRot(float p0) {
        return this.getYaw2(p0);
    }

    public boolean isCustomNameVisible2() {
        return super.isCustomNameVisible();
    }

    public final boolean isCustomNameVisible() {
        return this.isCustomNameVisible2();
    }

    public void refreshPositionAfterTeleport2(double p0, double p1, double p2) {
        super.moveTo(p0, p1, p2);
    }

    public final void moveTo(double p0, double p1, double p2) {
        this.refreshPositionAfterTeleport2(p0, p1, p2);
    }

    public void refreshPositionAfterTeleport2(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        super.moveTo(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public final void moveTo(net.minecraft.world.phys.Vec3 p0) {
        this.refreshPositionAfterTeleport2(new cn.jstxjf_.rte.mapping.holder.Vector3d(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.EntityPose getPose2() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(super.getPose());
    }

    public final net.minecraft.world.entity.Pose getPose() {
        final var v0 = this.getPose2();
        return (v0 == null ? null : v0.data);
    }

    public void setVelocity2(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        super.setDeltaMovement(((net.minecraft.world.phys.Vec3) p0.data));
    }

    public final void setDeltaMovement(net.minecraft.world.phys.Vec3 p0) {
        this.setVelocity2(new cn.jstxjf_.rte.mapping.holder.Vector3d(p0));
    }

    public void setVelocity2(double p0, double p1, double p2) {
        super.setDeltaMovement(p0, p1, p2);
    }

    public final void setDeltaMovement(double p0, double p1, double p2) {
        this.setVelocity2(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getPos2() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(super.position());
    }

    public final net.minecraft.world.phys.Vec3 position() {
        final var v0 = this.getPos2();
        return (v0 == null ? null : ((net.minecraft.world.phys.Vec3) v0.data));
    }

    public cn.jstxjf_.rte.mapping.holder.SoundCategory getSoundCategory2() {
        return cn.jstxjf_.rte.mapping.holder.SoundCategory.convert(super.getSoundSource());
    }

    public final net.minecraft.sounds.SoundSource getSoundSource() {
        final var v0 = this.getSoundCategory2();
        return (v0 == null ? null : v0.data);
    }

    protected boolean canAddPassenger2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        return super.canAddPassenger(((net.minecraft.world.entity.Entity) p0.data));
    }

    protected final boolean canAddPassenger(net.minecraft.world.entity.Entity p0) {
        return this.canAddPassenger2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.Text getCustomName2() {
        final var v0 = super.getCustomName();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.Text(v0));
    }

    public final net.minecraft.network.chat.Component getCustomName() {
        final var v0 = this.getCustomName2();
        return (v0 == null ? null : ((net.minecraft.network.chat.Component) v0.data));
    }

    public boolean doesNotCollide2(double p0, double p1, double p2) {
        return super.isFree(p0, p1, p2);
    }

    public final boolean isFree(double p0, double p1, double p2) {
        return this.doesNotCollide2(p0, p1, p2);
    }

    public java.util.Set<java.lang.String> getCommandTags2() {
        return super.getTags();
    }

    public final java.util.Set<java.lang.String> getTags() {
        return this.getCommandTags2();
    }

    public void pushAwayFrom2(cn.jstxjf_.rte.mapping.holder.Entity p0) {
        super.push(((net.minecraft.world.entity.Entity) p0.data));
    }

    public final void push(net.minecraft.world.entity.Entity p0) {
        this.pushAwayFrom2(new cn.jstxjf_.rte.mapping.holder.Entity(p0));
    }

    public boolean collidesWithStateAtPos2(cn.jstxjf_.rte.mapping.holder.BlockPos p0, cn.jstxjf_.rte.mapping.holder.BlockState p1) {
        return super.isColliding(((net.minecraft.core.BlockPos) p0.data), ((net.minecraft.world.level.block.state.BlockState) p1.data));
    }

    public final boolean isColliding(net.minecraft.core.BlockPos p0, net.minecraft.world.level.block.state.BlockState p1) {
        return this.collidesWithStateAtPos2(new cn.jstxjf_.rte.mapping.holder.BlockPos(p0), new cn.jstxjf_.rte.mapping.holder.BlockState(p1));
    }

    public boolean shouldRender2(double p0, double p1, double p2) {
        return super.shouldRender(p0, p1, p2);
    }

    public final boolean shouldRender(double p0, double p1, double p2) {
        return this.shouldRender2(p0, p1, p2);
    }

    public boolean shouldRender2(double p0) {
        return super.shouldRenderAtSqrDistance(p0);
    }

    public final boolean shouldRenderAtSqrDistance(double p0) {
        return this.shouldRender2(p0);
    }

    public void setPose2(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        super.setPose(p0.data);
    }

    public final void setPose(net.minecraft.world.entity.Pose p0) {
        this.setPose2(cn.jstxjf_.rte.mapping.holder.EntityPose.convert(p0));
    }

    protected void setRotation2(float p0, float p1) {
        super.setRot(p0, p1);
    }

    protected final void setRot(float p0, float p1) {
        this.setRotation2(p0, p1);
    }

    public final float getStandingEyeHeight2() {
        return super.getEyeHeight();
    }

    protected static cn.jstxjf_.rte.mapping.holder.Vector3d getPassengerDismountOffset2(double p0, double p1, float p2) {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(net.minecraft.world.entity.Entity.getCollisionHorizontalEscapeVector(p0, p1, p2));
    }

    public void setBodyYaw2(float p0) {
        super.setYBodyRot(p0);
    }

    public final void setYBodyRot(float p0) {
        this.setBodyYaw2(p0);
    }

    protected cn.jstxjf_.rte.mapping.holder.BlockState getLandingBlockState2() {
        return new cn.jstxjf_.rte.mapping.holder.BlockState(super.getBlockStateOnLegacy());
    }

    protected final net.minecraft.world.level.block.state.BlockState getBlockStateOnLegacy() {
        final var v0 = this.getLandingBlockState2();
        return (v0 == null ? null : ((net.minecraft.world.level.block.state.BlockState) v0.data));
    }

    public boolean isSprinting2() {
        return super.isSprinting();
    }

    public final boolean isSprinting() {
        return this.isSprinting2();
    }

    protected int getPermissionLevel2() {
        return super.getPermissionLevel();
    }

    protected final int getPermissionLevel() {
        return this.getPermissionLevel2();
    }

    public java.lang.String getUuidStringMapped() {
        return this.stringUUID;
    }

    public void setUuidStringMapped(java.lang.String p0) {
        this.stringUUID = p0;
    }

    public float getFallDistanceMapped() {
        return this.fallDistance;
    }

    public void setFallDistanceMapped(float p0) {
        this.fallDistance = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.Random getRandomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Random(this.random);
    }

    public java.util.UUID getUuidMapped() {
        return this.uuid;
    }

    public void setUuidMapped(java.util.UUID p0) {
        this.uuid = p0;
    }

    public boolean getHorizontalCollisionMapped() {
        return this.horizontalCollision;
    }

    public void setHorizontalCollisionMapped(boolean p0) {
        this.horizontalCollision = p0;
    }

    public boolean getNoClipMapped() {
        return this.noPhysics;
    }

    public void setNoClipMapped(boolean p0) {
        this.noPhysics = p0;
    }

    public boolean getVerticalCollisionMapped() {
        return this.verticalCollision;
    }

    public void setVerticalCollisionMapped(boolean p0) {
        this.verticalCollision = p0;
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getMovementMultiplierMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(this.stuckSpeedMultiplier);
    }

    public void setMovementMultiplierMapped(cn.jstxjf_.rte.mapping.holder.Vector3d p0) {
        this.stuckSpeedMultiplier = ((net.minecraft.world.phys.Vec3) p0.data);
    }
}
