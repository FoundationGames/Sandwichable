package io.github.foundationgames.sandwichable.rei;

import com.google.common.collect.ImmutableList;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class ToastingDisplay implements Display {

    private List<EntryIngredient> inputs;
    private EntryStack<ItemStack> result;
    private ToastingRecipe display;

    public ToastingDisplay(ToastingRecipe recipe) {
        this(recipe.getInput(), recipe.getOutput());
        this.display = recipe;
    }

    public ToastingDisplay(Ingredient input, ItemStack result) {
        var builder = EntryIngredient.builder();
        for(ItemStack i : input.getMatchingStacksClient()) builder.add(EntryStacks.of(i));
        this.inputs = ImmutableList.of(builder.build());
        this.result = EntryStacks.of(result);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.singletonList(EntryIngredient.of(this.result));
    }

    public List<EntryStack<ItemStack>> getDisplayOutputEntries() {
        return Collections.singletonList(this.result);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SandwichableREI.TOASTING_CATEGORY;
    }
}
