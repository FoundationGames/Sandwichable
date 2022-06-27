package io.github.foundationgames.sandwichable.recipe;

import io.github.foundationgames.sandwichable.recipe.special.AncientGrainBreadRecipe;
import io.github.foundationgames.sandwichable.recipe.special.AncientGrainBreadSliceRecipe;
import io.github.foundationgames.sandwichable.recipe.special.ToastedAncientGrainBreadSliceRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;

public class SandwichableRecipes {
    public static final RecipeType<CuttingRecipe> CUTTING_RECIPE = Registry.register(Registry.RECIPE_TYPE, Util.id(CuttingRecipe.Type.ID), CuttingRecipe.Type.INSTANCE);
    public static final RecipeType<ToastingRecipe> TOASTING_RECIPE = Registry.register(Registry.RECIPE_TYPE, Util.id(ToastingRecipe.Type.ID), ToastingRecipe.Type.INSTANCE);

    public static final SpecialRecipeSerializer<AncientGrainBreadRecipe> ANCIENT_GRAIN_BREAD = Registry.register(
            Registry.RECIPE_SERIALIZER, Util.id("crafting_special_ancientgrainbread"), new SpecialRecipeSerializer<>(AncientGrainBreadRecipe::new));
    public static final SpecialRecipeSerializer<AncientGrainBreadSliceRecipe> ANCIENT_GRAIN_BREAD_SLICE = Registry.register(
            Registry.RECIPE_SERIALIZER, Util.id("cutting_special_ancientgrainbreadslice"), new SpecialRecipeSerializer<>(AncientGrainBreadSliceRecipe::new));
    public static final SpecialRecipeSerializer<ToastedAncientGrainBreadSliceRecipe> TOASTED_ANCIENT_GRAIN_BREAD_SLICE = Registry.register(
            Registry.RECIPE_SERIALIZER, Util.id("toasting_special_toastedancientgrainbreadslice"), new SpecialRecipeSerializer<>(ToastedAncientGrainBreadSliceRecipe::new));

    public static void init() {
        Registry.register(Registry.RECIPE_SERIALIZER, CuttingRecipeSerializer.ID, CuttingRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, ToastingRecipeSerializer.ID, ToastingRecipeSerializer.INSTANCE);
    }
}
