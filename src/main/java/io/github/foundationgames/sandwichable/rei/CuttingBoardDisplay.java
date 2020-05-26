package io.github.foundationgames.sandwichable.rei;

import com.google.common.collect.ImmutableList;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class CuttingBoardDisplay implements RecipeDisplay {

    private List<List<EntryStack>> inputs;
    private List<EntryStack> results;
    private CuttingRecipe display;

    public CuttingBoardDisplay(CuttingRecipe recipe) {
        this(recipe.getInput(), recipe.getOutput());
        this.display = recipe;
    }

    public CuttingBoardDisplay(Ingredient input, ItemStack result) {
        this.inputs = ImmutableList.of(Collections.singletonList(EntryStack.create(input.getMatchingStacksClient()[0])));
        this.results = Collections.singletonList(EntryStack.create(result));
    }

    @Override
    public List<List<EntryStack>> getInputEntries() {
        return this.inputs;
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return this.results;
    }

    @Override
    public Identifier getRecipeCategory() {
        return SandwichableREI.CUTTING_BOARD_CATEGORY;
    }
}
