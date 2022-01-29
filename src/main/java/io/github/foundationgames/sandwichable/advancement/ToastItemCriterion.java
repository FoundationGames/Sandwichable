package io.github.foundationgames.sandwichable.advancement;

import com.google.gson.JsonObject;
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
import org.jetbrains.annotations.Nullable;

public class ToastItemCriterion extends AbstractCriterion<ToastItemCriterion.Conditions> {
    public static final Identifier ID = Util.id("toast_item");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        if (obj.has("result")) {
            return new Conditions(playerPredicate, ItemPredicate.fromJson(obj.get("result")));
        }
        return new Conditions(playerPredicate, null);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, conditions -> conditions.test(stack));
    }

    public static class Conditions extends AbstractCriterionConditions {
        @Nullable private final ItemPredicate result;

        public Conditions(EntityPredicate.Extended playerPredicate, @Nullable ItemPredicate result) {
            super(ID, playerPredicate);
            this.result = result;
        }

        public boolean test(ItemStack stack) {
            if (result == null) {
                return true;
            }
            return result.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject obj = super.toJson(predicateSerializer);
            if (this.result != null) {
                obj.add("result", this.result.toJson());
            }
            return obj;
        }
    }
}
