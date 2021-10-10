package io.github.foundationgames.sandwichable.mixin;

import com.google.common.collect.ImmutableSet;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VillagerProfession.class)
public class VillagerProfessionMixin {
    @ModifyArg(
            method = "<clinit>()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/village/VillagerProfession;register(Ljava/lang/String;Lnet/minecraft/world/poi/PointOfInterestType;Lcom/google/common/collect/ImmutableSet;Lcom/google/common/collect/ImmutableSet;Lnet/minecraft/sound/SoundEvent;)Lnet/minecraft/village/VillagerProfession;"
            ),
            index = 2
    )
    private static ImmutableSet<Item> sandwichable$addFarmerPlantables(ImmutableSet<Item> old) {
        ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
        builder.addAll(old);
        builder.add(ItemsRegistry.TOMATO_SEEDS, ItemsRegistry.LETTUCE_SEEDS, ItemsRegistry.CUCUMBER_SEEDS, ItemsRegistry.ONION_SEEDS);
        return builder.build();
    }
}
