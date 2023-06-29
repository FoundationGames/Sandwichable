package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.util.BlockLeakParticleDuck;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/client/particle/BlockLeakParticle$ContinuousFalling")
public abstract class ContinuousFallingParticleMixin implements BlockLeakParticleDuck{
    @Mutable @Shadow @Final protected ParticleEffect nextParticle;

    @Override
    public void setNextParticle(ParticleEffect effect) {
        this.nextParticle = effect;
    }
}
