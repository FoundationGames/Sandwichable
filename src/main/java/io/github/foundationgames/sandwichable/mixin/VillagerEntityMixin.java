package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {
    @ModifyArg(
            method = "<clinit>()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableSet;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"
            ),
            index = 6
    )
    private static Object[] sandwichable$addSeeds(Object[] old) {
        Object[] items = new Object[old.length + 4];
        System.arraycopy(old, 0, items, 0, old.length);
        items[items.length - 1] = ItemsRegistry.TOMATO_SEEDS;
        items[items.length - 2] = ItemsRegistry.LETTUCE_SEEDS;
        items[items.length - 3] = ItemsRegistry.CUCUMBER_SEEDS;
        items[items.length - 4] = ItemsRegistry.ONION_SEEDS;
        return items;
    }
}
