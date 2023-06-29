package io.github.foundationgames.sandwichable.blocks.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.items.BiomeVariantItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.math.BlockPos;

public class CopyWorldBiomeLootFunction extends ConditionalLootFunction {
    protected CopyWorldBiomeLootFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if (context.hasParameter(LootContextParameters.ORIGIN)) {
            var origin = context.get(LootContextParameters.ORIGIN);
            BiomeVariantItem.setBiome(stack, context.getWorld().getBiome(BlockPos.ofFloored(origin)));
        }

        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return Sandwichable.COPY_WORLD_BIOME;
    }

    public static class Serializer extends net.minecraft.loot.function.ConditionalLootFunction.Serializer<CopyWorldBiomeLootFunction> {
        public Serializer() {
        }

        public void toJson(JsonObject json, CopyWorldBiomeLootFunction function, JsonSerializationContext ctx) {
            super.toJson(json, function, ctx);
        }

        public CopyWorldBiomeLootFunction fromJson(JsonObject json, JsonDeserializationContext ctx, LootCondition[] conditions) {
            return new CopyWorldBiomeLootFunction(conditions);
        }
    }
}
