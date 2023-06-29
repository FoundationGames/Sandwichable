package io.github.foundationgames.sandwichable.recipe.special;

import io.github.foundationgames.sandwichable.items.BiomeVariantItem;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.SandwichableRecipes;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AncientGrainBreadSliceRecipe extends CuttingRecipe.Special {
    public AncientGrainBreadSliceRecipe(Identifier id) {
        super(id);
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(ItemsRegistry.ANCIENT_GRAIN_BREAD_SLICE, 4);
    }

    @Override
    public ItemStack craft(SimpleInventory inv, DynamicRegistryManager registryManager) {
        var stack = getOutput(registryManager);
        BiomeVariantItem.copyBiome(inv.getStack(0), stack);

        return stack;
    }

    @Override
    public boolean matches(SimpleInventory inv, World world) {
        return inv.getStack(0).isOf(ItemsRegistry.ANCIENT_GRAIN_BREAD);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SandwichableRecipes.ANCIENT_GRAIN_BREAD_SLICE;
    }
}
