package io.github.foundationgames.sandwichable.plugin.rei;

import com.google.common.collect.ImmutableList;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Collections;
import java.util.List;

public class CuttingBoardDisplay implements Display {

    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> results;

    public CuttingBoardDisplay(CuttingRecipe recipe) {
        this(recipe.getInput(), recipe.getOutputStack());
    }

    public CuttingBoardDisplay(Ingredient input, ItemStack result) {
        ImmutableList.Builder<EntryIngredient> b = new ImmutableList.Builder<>();
        for(ItemStack i : input.getMatchingStacks()) b.add(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, i)));
        this.inputs = b.build();
        this.results = Collections.singletonList(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, result)));
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
        return CuttingBoardCategory.ID;
    }
}
