package io.github.foundationgames.sandwichable.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Criteria.class)
public interface CriteriaAccess {
    @Invoker("register")
    static <T extends Criterion<?>> T sandwichable$register(T criterion) {
        throw new AssertionError("something important broke");
    }
}
