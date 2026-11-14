package cn.jstxjf_.rte.fabric;

import cn.jstxjf_.rte.client.gui.RTEConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RTEConfigScreen::createScreen;
    }
}
