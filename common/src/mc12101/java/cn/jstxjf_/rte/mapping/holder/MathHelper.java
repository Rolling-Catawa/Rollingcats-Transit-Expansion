package cn.jstxjf_.rte.mapping.holder;

public final class MathHelper extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.util.Mth> {

    public MathHelper(net.minecraft.util.Mth p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.MathHelper cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new MathHelper((net.minecraft.util.Mth) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.util.Mth;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static int floorDiv(int p0, int p1) {
        return net.minecraft.util.Mth.floorDiv(p0, p1);
    }

    public static int idealHash(int p0) {
        return net.minecraft.util.Mth.murmurHash3Mixer(p0);
    }

    public static float abs(float p0) {
        return net.minecraft.util.Mth.abs(p0);
    }

    public static int abs(int p0) {
        return net.minecraft.util.Mth.abs(p0);
    }

    public static int log2(int p0) {
        return net.minecraft.util.Mth.log2(p0);
    }

    public static boolean isPowerOfTwo(int p0) {
        return net.minecraft.util.Mth.isPowerOfTwo(p0);
    }

    public static double nextDouble(cn.jstxjf_.rte.mapping.holder.Random p0, double p1, double p2) {
        return net.minecraft.util.Mth.nextDouble(((net.minecraft.util.RandomSource) p0.data), p1, p2);
    }

    public static int hsvToRgb(float p0, float p1, float p2) {
        return net.minecraft.util.Mth.hsvToRgb(p0, p1, p2);
    }

    public static float fastInverseCbrt(float p0) {
        return net.minecraft.util.Mth.fastInvCubeRoot(p0);
    }

    public static float stepAngleTowards(float p0, float p1, float p2) {
        return net.minecraft.util.Mth.rotateIfNecessary(p0, p1, p2);
    }

    public static float stepTowards(float p0, float p1, float p2) {
        return net.minecraft.util.Mth.approach(p0, p1, p2);
    }

    public static double lerp(double p0, double p1, double p2) {
        return net.minecraft.util.Mth.lerp(p0, p1, p2);
    }

    public static float lerp(float p0, float p1, float p2) {
        return net.minecraft.util.Mth.lerp(p0, p1, p2);
    }

    public static int log2DeBruijn(int p0) {
        return net.minecraft.util.Mth.ceillog2(p0);
    }

    public static long lfloor(double p0) {
        return net.minecraft.util.Mth.lfloor(p0);
    }

    public static float stepUnwrappedAngleTowards(float p0, float p1, float p2) {
        return net.minecraft.util.Mth.approachDegrees(p0, p1, p2);
    }

    public static int ceil(double p0) {
        return net.minecraft.util.Mth.ceil(p0);
    }

    public static int ceil(float p0) {
        return net.minecraft.util.Mth.ceil(p0);
    }

    public static double perlinFade(double p0) {
        return net.minecraft.util.Mth.smoothstep(p0);
    }

    public static float sqrt(float p0) {
        return net.minecraft.util.Mth.sqrt(p0);
    }

    public static int packRgb(float p0, float p1, float p2) {
        return net.minecraft.util.Mth.color(p0, p1, p2);
    }

    public static float clamp(float p0, float p1, float p2) {
        return net.minecraft.util.Mth.clamp(p0, p1, p2);
    }

    public static double clamp(double p0, double p1, double p2) {
        return net.minecraft.util.Mth.clamp(p0, p1, p2);
    }

    public static int clamp(int p0, int p1, int p2) {
        return net.minecraft.util.Mth.clamp(p0, p1, p2);
    }

    public static double atan2(double p0, double p1) {
        return net.minecraft.util.Mth.atan2(p0, p1);
    }

    public static int roundUpToMultiple(int p0, int p1) {
        return net.minecraft.util.Mth.roundToward(p0, p1);
    }

    public static double fastInverseSqrt(double p0) {
        return net.minecraft.util.Mth.fastInvSqrt(p0);
    }

    public static float nextFloat(cn.jstxjf_.rte.mapping.holder.Random p0, float p1, float p2) {
        return net.minecraft.util.Mth.nextFloat(((net.minecraft.util.RandomSource) p0.data), p1, p2);
    }

    public static int parseInt(java.lang.String p0, int p1) {
        return net.minecraft.util.Mth.getInt(p0, p1);
    }

    public static java.util.UUID randomUuid() {
        return net.minecraft.util.Mth.createInsecureUUID();
    }

    public static java.util.UUID randomUuid(cn.jstxjf_.rte.mapping.holder.Random p0) {
        return net.minecraft.util.Mth.createInsecureUUID(((net.minecraft.util.RandomSource) p0.data));
    }

    public static int nextInt(cn.jstxjf_.rte.mapping.holder.Random p0, int p1, int p2) {
        return net.minecraft.util.Mth.nextInt(((net.minecraft.util.RandomSource) p0.data), p1, p2);
    }

    public static long hashCode(cn.jstxjf_.rte.mapping.holder.Vector3i p0) {
        return net.minecraft.util.Mth.getSeed(((net.minecraft.core.Vec3i) p0.data));
    }

    public static long hashCode(int p0, int p1, int p2) {
        return net.minecraft.util.Mth.getSeed(p0, p1, p2);
    }

    public static float lerpAngleDegrees(float p0, float p1, float p2) {
        return net.minecraft.util.Mth.rotLerp(p0, p1, p2);
    }

    public static int floor(double p0) {
        return net.minecraft.util.Mth.floor(p0);
    }

    public static int floor(float p0) {
        return net.minecraft.util.Mth.floor(p0);
    }

    public static float sin(float p0) {
        return net.minecraft.util.Mth.sin(p0);
    }

    public static double floorMod(double p0, double p1) {
        return net.minecraft.util.Mth.positiveModulo(p0, p1);
    }

    public static int floorMod(int p0, int p1) {
        return net.minecraft.util.Mth.positiveModulo(p0, p1);
    }

    public static float floorMod(float p0, float p1) {
        return net.minecraft.util.Mth.positiveModulo(p0, p1);
    }

    public static double clampedLerp(double p0, double p1, double p2) {
        return net.minecraft.util.Mth.clampedLerp(p0, p1, p2);
    }

    public static boolean approximatelyEquals(double p0, double p1) {
        return net.minecraft.util.Mth.equal(p0, p1);
    }

    public static boolean approximatelyEquals(float p0, float p1) {
        return net.minecraft.util.Mth.equal(p0, p1);
    }

    public static int binarySearch(int p0, int p1, java.util.function.IntPredicate p2) {
        return net.minecraft.util.Mth.binarySearch(p0, p1, p2);
    }

    public static double absMax(double p0, double p1) {
        return net.minecraft.util.Mth.absMax(p0, p1);
    }

    public static float square(float p0) {
        return net.minecraft.util.Mth.square(p0);
    }

    public static double fractionalPart(double p0) {
        return net.minecraft.util.Mth.frac(p0);
    }

    public static float fractionalPart(float p0) {
        return net.minecraft.util.Mth.frac(p0);
    }

    public static double lerp3(double p0, double p1, double p2, double p3, double p4, double p5, double p6, double p7, double p8, double p9, double p10) {
        return net.minecraft.util.Mth.lerp3(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
    }

    public static double lerp2(double p0, double p1, double p2, double p3, double p4, double p5) {
        return net.minecraft.util.Mth.lerp2(p0, p1, p2, p3, p4, p5);
    }

    public static double wrapDegrees(double p0) {
        return net.minecraft.util.Mth.wrapDegrees(p0);
    }

    public static int wrapDegrees(int p0) {
        return net.minecraft.util.Mth.wrapDegrees(p0);
    }

    public static float wrapDegrees(float p0) {
        return net.minecraft.util.Mth.wrapDegrees(p0);
    }

    public static float cos(float p0) {
        return net.minecraft.util.Mth.cos(p0);
    }

    public static int smallestEncompassingPowerOfTwo(int p0) {
        return net.minecraft.util.Mth.smallestEncompassingPowerOfTwo(p0);
    }

    public static int sign(double p0) {
        return net.minecraft.util.Mth.sign(p0);
    }

    public static float getSquareRootOfTwoMapped() {
        return net.minecraft.util.Mth.SQRT_OF_TWO;
    }
}
