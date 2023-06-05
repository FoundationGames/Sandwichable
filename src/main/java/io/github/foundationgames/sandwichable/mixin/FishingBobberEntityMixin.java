package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends Entity {
    private static final Identifier BRINE_LOOT_FISHING = Util.id("gameplay/brine_fishing");

    public FishingBobberEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        throw new AssertionError("accessed dummy constructor");
    }

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootManager;getTable(Lnet/minecraft/util/Identifier;)Lnet/minecraft/loot/LootTable;"))
    public Identifier sandwichable$changeLootTable(Identifier original) {
        if(this.world.getBlockState(this.getBlockPos()).isOf(BlocksRegistry.PICKLE_BRINE)) {
            return BRINE_LOOT_FISHING;
        }
        return original;
    }
}
