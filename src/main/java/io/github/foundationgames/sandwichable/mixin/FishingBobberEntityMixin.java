package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.fluids.FluidsRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends Entity {
    private static final Identifier BRINE_LOOT_FISHING = Util.id("gameplay/brine_fishing");

    public FishingBobberEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        throw new AssertionError("accessed dummy constructor");
    }

    @ModifyVariable(method = "use", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/loot/LootManager;getTable(Lnet/minecraft/util/Identifier;)Lnet/minecraft/loot/LootTable;", shift = At.Shift.AFTER, ordinal = 0), index = 5)
    public LootTable changeLootTable(LootTable old) {
        BlockState state = this.world.getBlockState(this.getBlockPos());
        System.out.println(state);
        if(state.isOf(BlocksRegistry.PICKLE_BRINE)) {
            System.out.println("is pickel brin");
            return this.world.getServer().getLootManager().getTable(BRINE_LOOT_FISHING);
        }
        return old;
    }
}
