package io.github.foundationgames.sandwichable.plugin.rei;

import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Collections;
import java.util.List;

public class ToastingDisplay implements Display {

    private List<EntryIngredient> inputs;
    private List<EntryIngredient> results;

    public ToastingDisplay(ToastingRecipe recipe) {
        this(recipe.getInput(), recipe.getOutput());
    }

    public ToastingDisplay(Ingredient input, ItemStack result) {
        this.inputs = Collections.singletonList(EntryIngredients.ofIngredient(input));
        this.results = Collections.singletonList(EntryIngredients.of(result));
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.results;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ToastingCategory.ID;
    }
}
