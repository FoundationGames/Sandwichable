package io.github.foundationgames.sandwichable.recipe.special;

import com.google.common.base.Objects;
import io.github.foundationgames.sandwichable.items.BiomeVariantItem;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.SandwichableRecipes;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AncientGrainBreadRecipe extends SpecialCraftingRecipe {
    public AncientGrainBreadRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        List<ItemStack> matches = new ArrayList<>();
        for (int y = 0; y < inventory.getHeight(); y++) {
            if (matches.size() > 0 && matches.size() < 3) {
                return false;
            }
            for (int x = 0; x < inventory.getWidth(); x++) {
                var invStack = inventory.getStack((y * inventory.getWidth()) + x);
                if (matches.size() >= 3) {
                    if (!invStack.isEmpty()) {
                        return false;
                    }
                } else {
                    if (invStack.isOf(ItemsRegistry.ANCIENT_GRAIN)) {
                        matches.add(invStack);
                    } else if (invStack.isEmpty()) {
                        if (matches.size() > 0) {
                            break;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        if (matches.size() == 3) {
            for (var match : matches) {
                if (!Objects.equal(match.getNbt(), matches.get(0).getNbt())) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        var result = new ItemStack(ItemsRegistry.ANCIENT_GRAIN_BREAD);

        for (int y = 0; y < inventory.getHeight(); y++) {
            for (int x = 0; x < inventory.getWidth(); x++) {
                var stack = inventory.getStack((y * inventory.getWidth()) + x);

                if (stack.isOf(ItemsRegistry.ANCIENT_GRAIN)) {
                    BiomeVariantItem.copyBiome(stack, result);

                    return result;
                }
            }
        }

        return result;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SandwichableRecipes.ANCIENT_GRAIN_BREAD;
    }
}
