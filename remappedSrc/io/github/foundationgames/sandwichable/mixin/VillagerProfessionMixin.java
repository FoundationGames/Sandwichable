package io.github.foundationgames.sandwichable.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VillagerProfession.class)
public interface VillagerProfessionMixin {
    @Invoker("<init>")
    static VillagerProfession create(String id, PointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, SoundEvent workSound) {
        throw new AssertionError("empty body");
    }
}
