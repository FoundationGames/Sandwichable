package io.github.foundationgames.sandwichable.advancement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.foundationgames.sandwichable.util.Sandwich;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class CollectSandwichCriterion extends AbstractCriterion<CollectSandwichCriterion.Conditions> {
    public static final Identifier ID = Util.id("collect_sandwich");
    private static final Sandwich cache = new Sandwich();

    @Override
    protected CollectSandwichCriterion.Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
       JsonArray arr = JsonHelper.getArray(obj, "foods");
       List<ItemPredicate> foods = new ArrayList<>();
       for (JsonElement e : arr) {
           foods.add(ItemPredicate.fromJson(e));
       }
       return new Conditions(playerPredicate, foods);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ItemStack sandwich) {
        if (sandwich.hasTag() && sandwich.getTag().contains("BlockEntityTag")) {
            cache.addFromNbt(sandwich.getSubTag("BlockEntityTag"));
            this.test(player, conditions -> conditions.test(cache));
        }
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final List<ItemPredicate> foods;

        public Conditions(EntityPredicate.Extended playerPredicate, List<ItemPredicate> foods) {
            super(ID, playerPredicate);
            this.foods = foods;
        }

        public boolean test(Sandwich sandwich) {
            for (ItemPredicate pred : this.foods) {
                boolean pass = false;
                for (ItemStack stack : sandwich.getFoodList()) {
                    if (pred.test(stack)) pass = true;
                }
                if (!pass) return false;
            }
            return true;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            JsonArray arr = new JsonArray();
            for (ItemPredicate pred : this.foods) {
                arr.add(pred.toJson());
            }
            obj.add("foods", arr);
            return obj;
        }
    }
}
