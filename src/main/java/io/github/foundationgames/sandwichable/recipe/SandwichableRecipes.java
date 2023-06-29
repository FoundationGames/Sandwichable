package io.github.foundationgames.sandwichable.recipe;

import io.github.foundationgames.sandwichable.recipe.special.AncientGrainBreadRecipe;
import io.github.foundationgames.sandwichable.recipe.special.AncientGrainBreadSliceRecipe;
import io.github.foundationgames.sandwichable.recipe.special.GenericSpecialRecipeSerializer;
import io.github.foundationgames.sandwichable.recipe.special.ToastedAncientGrainBreadSliceRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SandwichableRecipes {
    public static final RecipeType<CuttingRecipe> CUTTING_RECIPE = Registry.register(Registries.RECIPE_TYPE, Util.id(CuttingRecipe.Type.ID), CuttingRecipe.Type.INSTANCE);
    public static final RecipeType<ToastingRecipe> TOASTING_RECIPE = Registry.register(Registries.RECIPE_TYPE, Util.id(ToastingRecipe.Type.ID), ToastingRecipe.Type.INSTANCE);

    public static final SpecialRecipeSerializer<AncientGrainBreadRecipe> ANCIENT_GRAIN_BREAD = Registry.register(
            Registries.RECIPE_SERIALIZER, Util.id("crafting_special_ancientgrainbread"), new SpecialRecipeSerializer<>(AncientGrainBreadRecipe::new));
    public static final GenericSpecialRecipeSerializer<SimpleInventory, AncientGrainBreadSliceRecipe> ANCIENT_GRAIN_BREAD_SLICE = Registry.register(
            Registries.RECIPE_SERIALIZER, Util.id("cutting_special_ancientgrainbreadslice"), new GenericSpecialRecipeSerializer<>(AncientGrainBreadSliceRecipe::new));
    public static final GenericSpecialRecipeSerializer<SimpleInventory, ToastedAncientGrainBreadSliceRecipe> TOASTED_ANCIENT_GRAIN_BREAD_SLICE = Registry.register(
            Registries.RECIPE_SERIALIZER, Util.id("toasting_special_toastedancientgrainbreadslice"), new GenericSpecialRecipeSerializer<>(ToastedAncientGrainBreadSliceRecipe::new));

    public static void init() {
        Registry.register(Registries.RECIPE_SERIALIZER, CuttingRecipeSerializer.ID, CuttingRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_SERIALIZER, ToastingRecipeSerializer.ID, ToastingRecipeSerializer.INSTANCE);
    }
}
