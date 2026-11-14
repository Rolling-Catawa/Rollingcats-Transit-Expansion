package cn.jstxjf_.rte.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;

public final class RoutePathCreatorScreen {

    private RoutePathCreatorScreen() {
    }

    public static Screen createScreen(InteractionHand hand, Screen parent) {
        return new cn.jstxjf_.rte.gui.RoutePathCreatorScreen(hand, parent);
    }
}
