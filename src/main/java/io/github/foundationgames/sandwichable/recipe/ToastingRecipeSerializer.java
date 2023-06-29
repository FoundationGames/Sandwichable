package io.github.foundationgames.sandwichable.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ToastingRecipeSerializer implements RecipeSerializer<ToastingRecipe> {

    private ToastingRecipeSerializer() {}

    public static final ToastingRecipeSerializer INSTANCE = new ToastingRecipeSerializer();
    public static final Identifier ID = new Identifier("sandwichable:toasting_recipe");

    @Override
    public ToastingRecipe read(Identifier id, JsonObject json) {
        ToastingRecipeJsonFormat recipeJson = new Gson().fromJson(json, ToastingRecipeJsonFormat.class);

        if(recipeJson.input == null || recipeJson.output == null) { throw new JsonSyntaxException("Missing Attributes in Toasting Recipe!"); }
        Ingredient input = Ingredient.fromJson(recipeJson.getInput());
        Item output = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.getOutput())).orElseThrow(() -> new JsonSyntaxException("The Item " + recipeJson.output + " does not exist!"));
        ItemStack outputStack = new ItemStack(output, 1);

        return new ToastingRecipe(input, outputStack, id);
    }

    @Override
    public ToastingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();

        return new ToastingRecipe(input, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, ToastingRecipe recipe) {
        recipe.getInput().write(buf);
        buf.writeItemStack(recipe.getOutputStack());
    }
}
