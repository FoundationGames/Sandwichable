package io.github.foundationgames.sandwichable.plugin.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiToastingRecipe implements EmiRecipe {
	public static final Text TOASTING_TIME = Text.translatable("category.sandwichable.toasting.time");
	private final Identifier id;
	private final EmiIngredient input;
	private final EmiStack output;

	public EmiToastingRecipe(Identifier id, Ingredient input, ItemStack result) {
		this.id = id;
		this.input = EmiIngredient.of(input);
		this.output = EmiStack.of(result);
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return SandwichableEMI.TOASTING_CATEGORY;
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
		widgets.addTexture(Util.id("textures/gui/toasting_recipe_rei.png"), 19, 10, 30, 22, 0, 0);
		widgets.addTexture(Util.id("textures/gui/toasting_recipe_rei.png"), 85, 10, 30, 22, 30, 0);
		widgets.addFillingArrow(56, 10, 12000);
		widgets.addText(TOASTING_TIME, 51, 30, -12566464, false);
		widgets.addSlot(input, 25, 7).drawBack(false);
		widgets.addSlot(output, 91, 7).drawBack(false).recipeContext(this);
	}
}
