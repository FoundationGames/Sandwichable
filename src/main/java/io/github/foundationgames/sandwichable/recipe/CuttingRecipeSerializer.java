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

public class CuttingRecipeSerializer implements RecipeSerializer<CuttingRecipe> {

    private CuttingRecipeSerializer() {}

    public static final CuttingRecipeSerializer INSTANCE = new CuttingRecipeSerializer();
    public static final Identifier ID = new Identifier("sandwichable:cutting_recipe");

    @Override
    public CuttingRecipe read(Identifier id, JsonObject json) {
        CuttingRecipeJsonFormat recipeJson = new Gson().fromJson(json, CuttingRecipeJsonFormat.class);

        if(recipeJson.input == null || recipeJson.outputItem == null) { throw new JsonSyntaxException("Missing Attributes in Cutting Recipe!"); }
        Ingredient input = Ingredient.fromJson(recipeJson.getInput());
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.getOutputItemId())).orElseThrow(() -> new JsonSyntaxException("The Item " + recipeJson.outputItem + " does not exist!"));
        ItemStack outputStack = new ItemStack(outputItem, recipeJson.getOutputCount());

        return new CuttingRecipe(input, outputStack, id);
    }

    @Override
    public CuttingRecipe read(Identifier id, PacketByteBuf buf) {

        Ingredient input = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();

        return new CuttingRecipe(input, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, CuttingRecipe recipe) {
        recipe.getInput().write(buf);
        buf.writeItemStack(recipe.getOutputStack());
    }
}
