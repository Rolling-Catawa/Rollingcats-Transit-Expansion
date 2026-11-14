package cn.jstxjf_.rte.scripting;

//#if MC>=11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
import cn.jstxjf_.rte.compat.BuiltInRegistries;
//#endif
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.scripting.util.WrappedEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class ScriptUseResolver {

    private ScriptUseResolver() {
    }

    public static ScriptUseEvent resolve(AbstractScriptContext context, Matrix4f localToWorld,
                                         Player player, int subIndex, double maxDistance) {
        if (context == null || localToWorld == null || player == null || context.useBoxes.isEmpty()) {
            return null;
        }

        final org.joml.Matrix4f inverse = new org.joml.Matrix4f(localToWorld.asMoj()).invert();
        final Vec3 eye = player.getEyePosition();
        final Vec3 look = player.getViewVector(1.0F);

        final org.joml.Vector3f origin = inverse.transformPosition(
            new org.joml.Vector3f((float) eye.x, (float) eye.y, (float) eye.z));
        final org.joml.Vector3f direction = inverse.transformDirection(
            new org.joml.Vector3f((float) look.x, (float) look.y, (float) look.z)).normalize();

        ScriptUseBox best = null;
        double bestDistance = Double.MAX_VALUE;
        for (final ScriptUseBox box : context.useBoxes.values()) {
            if (box.car != subIndex) {
                continue;
            }
            final double distance = box.intersect(
                origin.x, origin.y, origin.z, direction.x, direction.y, direction.z, maxDistance);
            if (distance >= 0 && distance < bestDistance) {
                bestDistance = distance;
                best = box;
            }
        }
        if (best == null) {
            return null;
        }

        return new ScriptUseEvent(
            best.id, best.car,
            origin.x + direction.x * bestDistance,
            origin.y + direction.y * bestDistance,
            origin.z + direction.z * bestDistance,
            bestDistance,
            direction.x, direction.y, direction.z,
            new WrappedEntity(player),
            itemId(player.getMainHandItem()),
            itemId(player.getOffhandItem()),
            player.isShiftKeyDown());
    }

    public static String itemId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "minecraft:air";
        }
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }
}
