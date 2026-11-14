package cn.jstxjf_.rte.mapping.holder;

public final class ParticleTypes extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.core.particles.ParticleTypes> {

    public ParticleTypes(net.minecraft.core.particles.ParticleTypes p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.ParticleTypes cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new ParticleTypes((net.minecraft.core.particles.ParticleTypes) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.core.particles.ParticleTypes;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public ParticleTypes() {
        super(new net.minecraft.core.particles.ParticleTypes());
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getItemSlimeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.ITEM_SLIME);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getAshMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.ASH);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFishingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FISHING);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getTotemOfUndyingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFireworkMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FIREWORK);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getDamageIndicatorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.DAMAGE_INDICATOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getSquidInkMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.SQUID_INK);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getComposterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.COMPOSTER);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getBubblePopMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.BUBBLE_POP);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getExplosionEmitterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getNautilusMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.NAUTILUS);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getWitchMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.WITCH);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getSplashMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.SPLASH);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getEnchantedHitMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getPortalMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.PORTAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getSpitMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.SPIT);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFallingObsidianTearMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FALLING_OBSIDIAN_TEAR);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getSweepAttackMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getLandingLavaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.LANDING_LAVA);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFallingLavaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FALLING_LAVA);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getSoulFireFlameMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getCampfireSignalSmokeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_SIGNAL_SMOKE);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getReversePortalMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getExplosionMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.EXPLOSION);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getItemSnowballMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.ITEM_SNOWBALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getBubbleColumnUpMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.BUBBLE_COLUMN_UP);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getEndRodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.END_ROD);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFallingNectarMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FALLING_NECTAR);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getHeartMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.HEART);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getPoofMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.POOF);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getHappyVillagerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getDrippingHoneyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.DRIPPING_HONEY);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFlashMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FLASH);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getDrippingWaterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.DRIPPING_WATER);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getEffectMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.EFFECT);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getWarpedSporeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.WARPED_SPORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getSoulMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.SOUL);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getNoteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.NOTE);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getCampfireCosySmokeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getDrippingLavaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.DRIPPING_LAVA);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getCritMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.CRIT);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getCloudMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.CLOUD);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getEnchantMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.ENCHANT);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getLavaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.LAVA);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getUnderwaterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.UNDERWATER);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getDolphinMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.DOLPHIN);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getSmokeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.SMOKE);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getBubbleMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.BUBBLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getCrimsonSporeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.CRIMSON_SPORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getLargeSmokeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFlameMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FLAME);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getMyceliumMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.MYCELIUM);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getInstantEffectMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.INSTANT_EFFECT);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFallingHoneyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FALLING_HONEY);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getDragonBreathMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getDrippingObsidianTearMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.DRIPPING_OBSIDIAN_TEAR);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getLandingHoneyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.LANDING_HONEY);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getRainMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.RAIN);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getAngryVillagerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getFallingWaterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.FALLING_WATER);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getLandingObsidianTearMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.LANDING_OBSIDIAN_TEAR);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getElderGuardianMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.ELDER_GUARDIAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getCurrentDownMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.CURRENT_DOWN);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getWhiteAshMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.WHITE_ASH);
    }

    public static cn.jstxjf_.rte.mapping.holder.DefaultParticleType getSneezeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.DefaultParticleType(net.minecraft.core.particles.ParticleTypes.SNEEZE);
    }
}
