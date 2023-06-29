package io.github.foundationgames.sandwichable.recipe.special;

import io.github.foundationgames.sandwichable.items.BiomeVariantItem;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.SandwichableRecipes;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ToastedAncientGrainBreadSliceRecipe extends ToastingRecipe.Special {
    public ToastedAncientGrainBreadSliceRecipe(Identifier id) {
        super(id);
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(ItemsRegistry.TOASTED_ANCIENT_GRAIN_BREAD_SLICE, 1);
    }

    @Override
    public ItemStack craft(SimpleInventory inv, DynamicRegistryManager registryManager) {
        var stack = getOutput(registryManager);

        // The inventory is always a single slot inventory containing the ingredient, not the entire toaster
        BiomeVariantItem.copyBiome(inv.getStack(0), stack);

        return stack;
    }

    @Override
    public boolean matches(SimpleInventory inv, World world) {
        return inv.getStack(0).isOf(ItemsRegistry.ANCIENT_GRAIN_BREAD_SLICE);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SandwichableRecipes.TOASTED_ANCIENT_GRAIN_BREAD_SLICE;
    }
}
