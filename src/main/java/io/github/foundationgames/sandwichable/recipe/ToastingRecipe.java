package io.github.foundationgames.sandwichable.recipe;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ToastingRecipe implements Recipe<SimpleInventory> {

    private final Ingredient input;
    private final ItemStack output;
    private final Identifier id;

    public ToastingRecipe(Ingredient input, ItemStack output, Identifier id) {
        this.input = input;
        this.output = output;
        this.id = id;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    public ItemStack getOutputStack() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean matches(SimpleInventory inv, World world) {
        return input.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inv, DynamicRegistryManager registryManager) {
        return getOutput(registryManager);
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ToastingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ToastingRecipe> {
        private Type() {}
        public static final ToastingRecipe.Type INSTANCE = new ToastingRecipe.Type();

        public static final String ID = "toasting_recipe";
    }


    public static abstract class Special extends ToastingRecipe {
        public Special(Identifier id) {
            super(Ingredient.EMPTY, ItemStack.EMPTY, id);
        }

        @Override
        public abstract ItemStack getOutput(DynamicRegistryManager registryManager);

        @Override
        public abstract ItemStack craft(SimpleInventory inv, DynamicRegistryManager registryManager);

        @Override
        public abstract boolean matches(SimpleInventory inv, World world);

        @Override
        public abstract boolean fits(int width, int height);

        @Override
        public abstract RecipeSerializer<?> getSerializer();
    }
}
