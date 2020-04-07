package io.github.foundationgames.sandwichable.recipe;

import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ToastingRecipe implements Recipe<BasicInventory> {

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
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean matches(BasicInventory inv, World world) {
        return input.test(inv.getInvStack(0));
    }

    @Override
    public ItemStack craft(BasicInventory inv) {
        return ItemStack.EMPTY;
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

        public static final String ID = "toaster_recipe";
    }
}
