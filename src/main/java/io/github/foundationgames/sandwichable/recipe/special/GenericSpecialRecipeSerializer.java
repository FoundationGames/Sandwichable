package io.github.foundationgames.sandwichable.recipe.special;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class GenericSpecialRecipeSerializer <I extends Inventory, R extends Recipe<I>> implements RecipeSerializer<R> {
    private final GenericSpecialRecipeSerializer.Factory<I, R> factory;

    public GenericSpecialRecipeSerializer(GenericSpecialRecipeSerializer.Factory<I, R> factory) {
        this.factory = factory;
    }

    public R read(Identifier identifier, JsonObject jsonObject) {
        return this.factory.create(identifier);
    }

    public R read(Identifier identifier, PacketByteBuf packetByteBuf) {
        return this.factory.create(identifier);
    }

    public void write(PacketByteBuf packetByteBuf, R craftingRecipe) {
    }

    @FunctionalInterface
    public interface Factory<I extends Inventory, R extends Recipe<I>> {
        R create(Identifier id);
    }
}
