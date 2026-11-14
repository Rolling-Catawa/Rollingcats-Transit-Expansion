package cn.jstxjf_.rte.mixin;

import cn.jstxjf_.rte.render.RailPicker;
import cn.jstxjf_.rte.util.RailItemLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftPickRailMixin {

    private static final double RTE_PICK_RANGE = 48;
    private static final double RTE_PICK_TOLERANCE = 0.75;

    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void rte$pickRail(CallbackInfo ci) {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            return;
        }

        final RailPicker.RayHit hit;
        try {
            hit = RailPicker.rayPickRail(RTE_PICK_RANGE, RTE_PICK_TOLERANCE);
        } catch (Exception e) {
            return;
        }
        if (hit == null) {
            return;
        }

        final HitResult blockHit = minecraft.hitResult;
        if (blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            final double blockDistance = blockHit.getLocation().distanceTo(player.getEyePosition(1f));
            if (blockDistance <= hit.distance()) {
                return;
            }
        }

        final ItemStack stack;
        try {
            stack = RailItemLookup.stackForRail(hit.rail());
        } catch (Exception e) {
            return;
        }
        if (stack.isEmpty()) {
            return;
        }

        final Inventory inventory = player.getInventory();
        if (player.getAbilities().instabuild) {
            inventory.setPickedItem(stack);
            minecraft.gameMode.handleCreativeModeItemAdd(
                player.getItemInHand(InteractionHand.MAIN_HAND), 36 + inventory.selected);
            ci.cancel();
            return;
        }

        final int slot = inventory.findSlotMatchingItem(stack);
        if (slot < 0) {
            return;
        }
        if (Inventory.isHotbarSlot(slot)) {
            inventory.selected = slot;
        } else {
            minecraft.gameMode.handlePickItem(slot);
        }
        ci.cancel();
    }
}
