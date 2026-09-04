package cn.jstxjf_.rte.client.gui;

import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Screen;

import java.util.function.Supplier;

public final class RailPathEditorScreen {

    private RailPathEditorScreen() {
    }

    public static Screen createScreen(BlockPos posStart, BlockPos posEnd, Rail rail, Supplier<Screen> parent) {
        return cn.jstxjf_.rte.gui.RailPathEditorScreen.createScreen(posStart, posEnd, rail, parent);
    }
}
