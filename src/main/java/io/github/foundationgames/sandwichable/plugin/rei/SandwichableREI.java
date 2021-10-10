package io.github.foundationgames.sandwichable.plugin.rei;

import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.impl.ScreenHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SandwichableREI implements REIPluginV0 {

    public static final Identifier CUTTING_BOARD_CATEGORY = Util.id("cutting_board");
    public static final Identifier TOASTING_CATEGORY = Util.id("toasting");

    private static final Identifier CUTTING_TEXTURE = Util.id("textures/gui/cutting_recipe_rei.png");
    private static final Identifier CUTTING_TEXTURE_DARK = Util.id("textures/gui/cutting_recipe_rei_dark.png");

    private static final Identifier TOASTING_TEXTURE = Util.id("textures/gui/toasting_recipe_rei.png");
    private static final Identifier TOASTING_TEXTURE_DARK = Util.id("textures/gui/toasting_recipe_rei_dark.png");

    @Override
    public Identifier getPluginIdentifier() {
        return Util.id("sandwichable_plugin");
    }

    public static Identifier getCuttingGUITexture() {
        return ScreenHelper.isDarkModeEnabled() ? CUTTING_TEXTURE_DARK : CUTTING_TEXTURE;
    }

    public static Identifier getToastingGUITexture() {
        return ScreenHelper.isDarkModeEnabled() ? TOASTING_TEXTURE_DARK : TOASTING_TEXTURE;
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new CuttingBoardCategory());
        recipeHelper.registerCategory(new ToastingCategory());
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(CUTTING_BOARD_CATEGORY, CuttingRecipe.class, CuttingBoardDisplay::new);
        recipeHelper.registerRecipes(TOASTING_CATEGORY, ToastingRecipe.class, ToastingDisplay::new);
    }
}
