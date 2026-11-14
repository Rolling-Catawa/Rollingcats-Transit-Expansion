package cn.jstxjf_.rte.mapping.holder;

public enum EntityPose {
    STANDING(net.minecraft.world.entity.Pose.STANDING),
    FALL_FLYING(net.minecraft.world.entity.Pose.FALL_FLYING),
    SLEEPING(net.minecraft.world.entity.Pose.SLEEPING),
    SWIMMING(net.minecraft.world.entity.Pose.SWIMMING),
    SPIN_ATTACK(net.minecraft.world.entity.Pose.SPIN_ATTACK),
    CROUCHING(net.minecraft.world.entity.Pose.CROUCHING),
    LONG_JUMPING(net.minecraft.world.entity.Pose.LONG_JUMPING),
    DYING(net.minecraft.world.entity.Pose.DYING),
    CROAKING(net.minecraft.world.entity.Pose.CROAKING),
    USING_TONGUE(net.minecraft.world.entity.Pose.USING_TONGUE),
    SITTING(net.minecraft.world.entity.Pose.SITTING),
    ROARING(net.minecraft.world.entity.Pose.ROARING),
    SNIFFING(net.minecraft.world.entity.Pose.SNIFFING),
    EMERGING(net.minecraft.world.entity.Pose.EMERGING),
    DIGGING(net.minecraft.world.entity.Pose.DIGGING),
    SLIDING(net.minecraft.world.entity.Pose.SLIDING),
    SHOOTING(net.minecraft.world.entity.Pose.SHOOTING),
    INHALING(net.minecraft.world.entity.Pose.INHALING);

    public final net.minecraft.world.entity.Pose data;

    EntityPose(net.minecraft.world.entity.Pose data) {
        this.data = data;
    }

    public static EntityPose convert(net.minecraft.world.entity.Pose data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.EntityPose p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.EntityPose getCrouchingMapped() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(net.minecraft.world.entity.Pose.CROUCHING);
    }

    public static cn.jstxjf_.rte.mapping.holder.EntityPose getSpinAttackMapped() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(net.minecraft.world.entity.Pose.SPIN_ATTACK);
    }

    public static cn.jstxjf_.rte.mapping.holder.EntityPose getDyingMapped() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(net.minecraft.world.entity.Pose.DYING);
    }

    public static cn.jstxjf_.rte.mapping.holder.EntityPose getStandingMapped() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(net.minecraft.world.entity.Pose.STANDING);
    }

    public static cn.jstxjf_.rte.mapping.holder.EntityPose getSwimmingMapped() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(net.minecraft.world.entity.Pose.SWIMMING);
    }

    public static cn.jstxjf_.rte.mapping.holder.EntityPose getSleepingMapped() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(net.minecraft.world.entity.Pose.SLEEPING);
    }

    public static cn.jstxjf_.rte.mapping.holder.EntityPose getFallFlyingMapped() {
        return cn.jstxjf_.rte.mapping.holder.EntityPose.convert(net.minecraft.world.entity.Pose.FALL_FLYING);
    }
}
