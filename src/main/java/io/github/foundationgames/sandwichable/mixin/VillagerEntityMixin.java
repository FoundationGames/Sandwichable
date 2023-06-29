package io.github.foundationgames.sandwichable.mixin;

import com.google.common.collect.ImmutableSet;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.Set;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {
    @Mutable @Shadow @Final private static Set<Item> GATHERABLE_ITEMS;

    static {
        var items = new HashSet<>(GATHERABLE_ITEMS);
        items.add(ItemsRegistry.TOMATO_SEEDS);
        items.add(ItemsRegistry.TOMATO);
        items.add(ItemsRegistry.LETTUCE_SEEDS);
        items.add(ItemsRegistry.LETTUCE_HEAD);
        items.add(ItemsRegistry.CUCUMBER_SEEDS);
        items.add(ItemsRegistry.CUCUMBER);
        items.add(ItemsRegistry.ONION_SEEDS);
        items.add(ItemsRegistry.ONION);
        items.add(ItemsRegistry.ANCIENT_GRAIN_SEEDS);
        GATHERABLE_ITEMS = ImmutableSet.copyOf(items);
    }
}
