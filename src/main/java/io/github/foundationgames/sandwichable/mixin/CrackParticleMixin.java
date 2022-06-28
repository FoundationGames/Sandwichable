package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.items.TintedParticle;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrackParticle.class)
public abstract class CrackParticleMixin extends SpriteBillboardParticle {
    protected CrackParticleMixin(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/world/ClientWorld;DDDDDDLnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void sandwichable$tintItemParticles(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof TintedParticle tinted) {
            int color = tinted.getParticleColor(world, stack);
            this.red = (float)Util.getRed(color) / 255;
            this.green = (float)Util.getGreen(color) / 255;
            this.blue = (float)Util.getBlue(color) / 255;
        }
    }
}
