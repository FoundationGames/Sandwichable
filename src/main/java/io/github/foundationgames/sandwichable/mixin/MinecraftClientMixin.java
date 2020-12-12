package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.entity.EntitiesRegistry;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    private static AbstractMinecartEntity cache = null;

    /*@ModifyVariable(method = "doItemPick", at = @At(value = "NEW", target = "Lnet/minecraft/item/ItemStack", shift = At.Shift.BEFORE, ordinal = 3), print = true)
    private Item replaceSandwichMinecart(Item old) {
        return old;
    }*/
}
