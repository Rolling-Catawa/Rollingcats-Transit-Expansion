package cn.jstxjf_.rte;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyMappings {
    public static final KeyMapping RAIL_EDIT_KEY = new KeyMapping(
            "key.rte.rail_edit",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.rte.keybinding"
    );

    public static final KeyMapping DEBUG_KEY = new KeyMapping(
            "key.rte.debug",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F7,
            "category.rte.keybinding"
    );

    public static final KeyMapping TRAIN_SCREEN = new KeyMapping(
            "key.rte.train_screen",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "category.rte.keybinding"
    );

    public static final KeyMapping CONFIG_SCREEN = new KeyMapping(
            "key.rte.config_screen",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.rte.keybinding"
    );

    public static void register() {
    }
}
