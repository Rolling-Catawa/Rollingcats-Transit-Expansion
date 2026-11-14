package cn.jstxjf_.rte.mapping.holder;

public final class SoundEvents extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.sounds.SoundEvents> {

    public SoundEvents(net.minecraft.sounds.SoundEvents p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvents cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new SoundEvents((net.minecraft.sounds.SoundEvents) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.sounds.SoundEvents;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public SoundEvents() {
        super(new net.minecraft.sounds.SoundEvents());
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getWeatherRainMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.WEATHER_RAIN);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getAmbientUnderwaterExitMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.AMBIENT_UNDERWATER_EXIT);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getUiCartographyTableTakeResultMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getUiLoomSelectPatternMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.UI_LOOM_SELECT_PATTERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getAmbientUnderwaterLoopAdditionsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getUiLoomTakeResultMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.UI_LOOM_TAKE_RESULT);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getUiToastOutMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.UI_TOAST_OUT);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getUiToastInMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.UI_TOAST_IN);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getEntityExperienceOrbPickupMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getAmbientUnderwaterEnterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.AMBIENT_UNDERWATER_ENTER);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getAmbientUnderwaterLoopAdditionsUltraRareMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getAmbientUnderwaterLoopAdditionsRareMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getAmbientUnderwaterLoopMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.AMBIENT_UNDERWATER_LOOP);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getItemBucketFillMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.BUCKET_FILL);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getUiStonecutterSelectRecipeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.UI_STONECUTTER_SELECT_RECIPE);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getUiToastChallengeCompleteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getWeatherRainAboveMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.WEATHER_RAIN_ABOVE);
    }

    public static cn.jstxjf_.rte.mapping.holder.SoundEvent getUiStonecutterTakeResultMapped() {
        return new cn.jstxjf_.rte.mapping.holder.SoundEvent(net.minecraft.sounds.SoundEvents.UI_STONECUTTER_TAKE_RESULT);
    }
}
