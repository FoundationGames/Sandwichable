package io.github.foundationgames.sandwichable.plugin.rei;

import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.SandwichableRecipes;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SandwichableREI implements REIClientPlugin {
    public static final Identifier CUTTING_BOARD_CATEGORY = Util.id("cutting_board");
    public static final Identifier TOASTING_CATEGORY = Util.id("toasting");

    private static final Identifier CUTTING_TEXTURE = Util.id("textures/gui/cutting_recipe_rei.png");
    private static final Identifier CUTTING_TEXTURE_DARK = Util.id("textures/gui/cutting_recipe_rei_dark.png");

    private static final Identifier TOASTING_TEXTURE = Util.id("textures/gui/toasting_recipe_rei.png");
    private static final Identifier TOASTING_TEXTURE_DARK = Util.id("textures/gui/toasting_recipe_rei_dark.png");

    public static Identifier getCuttingGUITexture() {
        return REIRuntime.getInstance().isDarkThemeEnabled() ? CUTTING_TEXTURE_DARK : CUTTING_TEXTURE;
    }

    public static Identifier getToastingGUITexture() {
        return REIRuntime.getInstance().isDarkThemeEnabled() ? TOASTING_TEXTURE_DARK : TOASTING_TEXTURE;
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CuttingBoardCategory());
        registry.add(new ToastingCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(CuttingRecipe.class, SandwichableRecipes.CUTTING_RECIPE, CuttingBoardDisplay::new);
        registry.registerRecipeFiller(ToastingRecipe.class, SandwichableRecipes.TOASTING_RECIPE, ToastingDisplay::new);
    }
}
