package io.github.foundationgames.sandwichable.plugin.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.SandwichableRecipes;
import io.github.foundationgames.sandwichable.recipe.special.AncientGrainBreadSliceRecipe;
import io.github.foundationgames.sandwichable.recipe.special.ToastedAncientGrainBreadSliceRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.List;

public class SandwichableEMI implements EmiPlugin {
	public static final EmiRecipeCategory CUTTING_CATEGORY = new EmiRecipeCategory(Util.id("cutting"), EmiStack.of(new ItemStack(BlocksRegistry.OAK_CUTTING_BOARD).getItem()));
	public static final EmiRecipeCategory TOASTING_CATEGORY = new EmiRecipeCategory(Util.id("toasting"), EmiStack.of(new ItemStack(BlocksRegistry.TOASTER).getItem()));

	@Override
	public void register(EmiRegistry registry) {
		registry.addCategory(CUTTING_CATEGORY);
		registry.addCategory(TOASTING_CATEGORY);
		registry.addWorkstation(CUTTING_CATEGORY, EmiStack.of(new ItemStack(BlocksRegistry.OAK_CUTTING_BOARD).getItem()));
		registry.addWorkstation(TOASTING_CATEGORY, EmiStack.of(new ItemStack(BlocksRegistry.TOASTER).getItem()));
		registry.getRecipeManager().listAllOfType(SandwichableRecipes.CUTTING_RECIPE).forEach((cuttingRecipe -> {
			if (!(cuttingRecipe instanceof AncientGrainBreadSliceRecipe)) {
				registry.addRecipe(new EmiCuttingRecipe(cuttingRecipe.getId(), cuttingRecipe.getInput(), cuttingRecipe.getOutput()));
			}
		}));
		registry.getRecipeManager().listAllOfType(SandwichableRecipes.TOASTING_RECIPE).forEach((toastingRecipe -> {
			if (!(toastingRecipe instanceof ToastedAncientGrainBreadSliceRecipe)) {
				registry.addRecipe(new EmiToastingRecipe(toastingRecipe.getId(), toastingRecipe.getInput(), toastingRecipe.getOutput()));
			}
		}));
		registry.addRecipe(new EmiCraftingRecipe(List.of(EmiStack.of(ItemsRegistry.ANCIENT_GRAIN.getDefaultStack()), EmiStack.of(ItemsRegistry.ANCIENT_GRAIN.getDefaultStack()), EmiStack.of(ItemsRegistry.ANCIENT_GRAIN.getDefaultStack())), EmiStack.of(ItemsRegistry.ANCIENT_GRAIN_BREAD.getDefaultStack()), Util.id("crafting_special_ancientgrainbread"), true));
		registry.addRecipe(new EmiCuttingRecipe(Util.id("cutting_special_ancientgrainbreadslice"), Ingredient.ofItems(ItemsRegistry.ANCIENT_GRAIN_BREAD), ItemsRegistry.ANCIENT_GRAIN_BREAD_SLICE.getDefaultStack()));
		registry.addRecipe(new EmiToastingRecipe(Util.id("toasting_special_toastedancientgrainbreadslice"), Ingredient.ofItems(ItemsRegistry.ANCIENT_GRAIN_BREAD_SLICE), ItemsRegistry.TOASTED_ANCIENT_GRAIN_BREAD_SLICE.getDefaultStack()));
	}
}
