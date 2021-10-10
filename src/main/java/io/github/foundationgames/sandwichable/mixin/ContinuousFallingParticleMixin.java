package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.util.BlockLeakParticleDuck;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/particle/BlockLeakParticle$ContinuousFalling")
public abstract class ContinuousFallingParticleMixin extends Particle implements BlockLeakParticleDuck {
    @Unique
    private ParticleEffect customNextParticle;

    @Inject(method = "updateVelocity()V", at = @At("HEAD"), cancellable = true)
    public void sandwichable$spawnCustomNextParticle(CallbackInfo ci) {
        if(customNextParticle != null && this.onGround) {
            this.markDead();
            this.world.addParticle(this.customNextParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            ci.cancel();
        }
    }

    @Override
    public void setNextParticle(ParticleEffect effect) {
        customNextParticle = effect;
    }

    protected ContinuousFallingParticleMixin(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
        throw new AssertionError("dummy constructor called");
    }
}
