package cn.jstxjf_.rte.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.function.Predicate;

public final class ReplayCompat {

    private ReplayCompat() {
    }

    public static boolean isInReplay() {
        final Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null
            && minecraft.player.getClass().getName().toLowerCase(Locale.ROOT).contains("replaymod");
    }

    public static boolean recordedPlayerHolds(Predicate<ItemStack> test) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !isInReplay()) {
            return false;
        }
        for (final Player player : minecraft.level.players()) {
            if (player == minecraft.player) {
                continue;
            }
            if (test.test(player.getMainHandItem()) || test.test(player.getOffhandItem())) {
                return true;
            }
        }
        return false;
    }
}
