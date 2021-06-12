package io.github.foundationgames.sandwichable.rei;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SandwichableREI implements REIClientPlugin {

    public static final CategoryIdentifier<CuttingBoardDisplay> CUTTING_BOARD_CATEGORY = CategoryIdentifier.of(Util.id("cutting_board"));
    public static final CategoryIdentifier<ToastingDisplay> TOASTING_CATEGORY = CategoryIdentifier.of(Util.id("toasting"));

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
        BlocksRegistry.CUTTING_BOARDS.forEach((board) -> {
            registry.addWorkstations(CUTTING_BOARD_CATEGORY, EntryStacks.of(board));
        });
        registry.add(new ToastingCategory());
        registry.addWorkstations(TOASTING_CATEGORY, EntryStacks.of(BlocksRegistry.TOASTER));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(ToastingRecipe.class, ToastingDisplay::new);
        registry.registerFiller(CuttingRecipe.class, CuttingBoardDisplay::new);
    }
}
