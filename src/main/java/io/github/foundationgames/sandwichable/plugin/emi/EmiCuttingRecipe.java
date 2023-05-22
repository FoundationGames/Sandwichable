package io.github.foundationgames.sandwichable.plugin.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EmiCuttingRecipe implements EmiRecipe {
	private final Identifier id;
	private final EmiIngredient input;
	private final EmiStack output;
	private static final List<EmiIngredient> KNIVES = List.of(EmiIngredient.of(Arrays.stream(Util.getConfig().itemOptions.knives).map(p -> Identifier.tryParse(p.itemId)).filter(Objects::nonNull).map(Registry.ITEM::getOrEmpty).filter(Optional::isPresent).map(Optional::get).map(Item::getDefaultStack).map(EmiStack::of).toList()));

	public EmiCuttingRecipe(Identifier id, Ingredient input, ItemStack result) {
		this.id = id;
		this.input = EmiIngredient.of(input);
		this.output = EmiStack.of(result);
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return SandwichableEMI.CUTTING_CATEGORY;
	}

	@Override
	public @Nullable Identifier getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(input);
	}

	@Override
	public List<EmiIngredient> getCatalysts() {
		return KNIVES;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(output);
	}

	@Override
	public int getDisplayWidth() {
		return 134;
	}

	@Override
	public int getDisplayHeight() {
		return 40;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(Util.id("textures/gui/cutting_recipe_rei.png"), 20, 0, 94, 39, 0, 0);
		widgets.addSlot(input, 29, 8);
		widgets.addSlot(EmiIngredient.of(KNIVES), 43, 14).drawBack(false);
		widgets.addSlot(output, 92, 11).drawBack(false).recipeContext(this);
	}
}
