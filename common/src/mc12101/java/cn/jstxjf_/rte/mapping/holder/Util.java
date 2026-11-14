package cn.jstxjf_.rte.mapping.holder;

public final class Util extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.Util> {

    public Util(net.minecraft.Util p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Util cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Util((net.minecraft.Util) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.Util;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static <T extends java.lang.Comparable<T>> java.lang.String getValueAsString(cn.jstxjf_.rte.mapping.holder.Property<T> p0, java.lang.Object p1) {
        return net.minecraft.Util.getPropertyName(((net.minecraft.world.level.block.state.properties.Property) p0.data), p1);
    }

    public static java.lang.String getInnermostMessage(java.lang.Throwable p0) {
        return net.minecraft.Util.describeError(p0);
    }

    public static com.mojang.datafixers.types.Type<?> getChoiceType(com.mojang.datafixers.DSL.TypeReference p0, java.lang.String p1) {
        final var v0 = net.minecraft.Util.fetchChoiceType(p0, p1);
        return (v0 == null ? null : v0);
    }

    public static java.util.function.Consumer<java.lang.String> addPrefix(java.lang.String p0, java.util.function.Consumer<java.lang.String> p1) {
        return net.minecraft.Util.prefix(p0, p1);
    }

    public static com.mojang.serialization.DataResult<int[]> decodeFixedLengthArray(java.util.stream.IntStream p0, int p1) {
        return net.minecraft.Util.fixedSize(p0, p1);
    }

    public static <K, V> java.util.stream.Collector<java.util.Map.Entry<? extends K, ? extends V>, ?, java.util.Map<K, V>> toMap() {
        return net.minecraft.Util.toMap();
    }

    public static void throwUnchecked(java.lang.Throwable p0) {
        net.minecraft.Util.throwAsRuntime(p0);
    }

    public static void backupAndReplace(java.nio.file.Path p0, java.nio.file.Path p1, java.nio.file.Path p2) {
        net.minecraft.Util.safeReplaceFile(p0, p1, p2);
    }

    public Util() {
        super(new net.minecraft.Util());
    }

    public static int moveCursor(java.lang.String p0, int p1, int p2) {
        return net.minecraft.Util.offsetByCodepoints(p0, p1, p2);
    }

    public static java.util.stream.Stream<java.lang.String> getJVMFlags() {
        return net.minecraft.Util.getVmArguments();
    }

    public static void startTimerHack() {
        net.minecraft.Util.startTimerHackThread();
    }

    public static <T> java.util.Optional<T> ifPresentOrElse(java.util.Optional<T> p0, java.util.function.Consumer<T> p1, java.lang.Runnable p2) {
        return net.minecraft.Util.ifElse(p0, p1, p2);
    }

    public static <T> T make(java.util.function.Supplier<T> p0) {
        return net.minecraft.Util.make(p0);
    }

    public static <T> T getLast(java.util.List<T> p0) {
        return net.minecraft.Util.lastOf(p0);
    }

    public static <T extends java.lang.Throwable> T throwOrPause(T p0) {
        return net.minecraft.Util.pauseInIde(p0);
    }

    public static int getRandom(int[] p0, cn.jstxjf_.rte.mapping.holder.Random p1) {
        return net.minecraft.Util.getRandom(p0, ((net.minecraft.util.RandomSource) p1.data));
    }

    public static <T> T getRandom(T[] p0, cn.jstxjf_.rte.mapping.holder.Random p1) {
        return net.minecraft.Util.getRandom(p0, ((net.minecraft.util.RandomSource) p1.data));
    }

    public static void shutdownExecutors() {
        net.minecraft.Util.shutdownExecutors();
    }

    public static cn.jstxjf_.rte.mapping.holder.OperatingSystem getOperatingSystem() {
        return cn.jstxjf_.rte.mapping.holder.OperatingSystem.convert(net.minecraft.Util.getPlatform());
    }

    public static java.lang.Runnable debugRunnable(java.lang.Runnable p0, java.util.function.Supplier<java.lang.String> p1) {
        return net.minecraft.Util.name(p0, p1);
    }

    public static void relativeCopy(java.nio.file.Path p0, java.nio.file.Path p1, java.nio.file.Path p2) throws java.io.IOException {
        net.minecraft.Util.copyBetweenDirs(p0, p1, p2);
    }

    public static java.lang.String createTranslationKey(java.lang.String p0, cn.jstxjf_.rte.mapping.holder.Identifier p1) {
        return net.minecraft.Util.makeDescriptionId(p0, (p1 == null ? null : ((net.minecraft.resources.ResourceLocation) p1.data)));
    }

    public static java.util.UUID getNilUuidMapped() {
        return net.minecraft.Util.NIL_UUID;
    }
}
