package io.github.foundationgames.sandwichable.plugin;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.foundationgames.sandwichable.config.ConfigInABarrel;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SandwichableModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigInABarrel.screen(SandwichableConfig.class, parent);
    }
}
