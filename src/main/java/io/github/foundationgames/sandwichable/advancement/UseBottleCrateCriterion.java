package io.github.foundationgames.sandwichable.advancement;

import com.google.gson.JsonObject;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UseBottleCrateCriterion extends AbstractCriterion<UseBottleCrateCriterion.Conditions> {
    public static final Identifier ID = Util.id("use_bottle_crate");

    @Override
    protected UseBottleCrateCriterion.Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new UseBottleCrateCriterion.Conditions(playerPredicate);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions(LootContextPredicate playerPredicate) {
            super(ID, playerPredicate);
        }
    }
}
