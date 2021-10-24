package io.github.foundationgames.sandwichable.particle;

import io.github.foundationgames.sandwichable.util.BlockLeakParticleDuck;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public final class Particles {
    public static final DefaultParticleType DRIPPING_BRINE = Registry.register(Registry.PARTICLE_TYPE, Util.id("dripping_brine"), FabricParticleTypes.simple());
    public static final DefaultParticleType FALLING_BRINE = Registry.register(Registry.PARTICLE_TYPE, Util.id("falling_brine"), FabricParticleTypes.simple());
    public static final DefaultParticleType BRINE_SPLASH = Registry.register(Registry.PARTICLE_TYPE, Util.id("brine_splash"), FabricParticleTypes.simple());
    public static final DefaultParticleType BRINE_BUBBLE = Registry.register(Registry.PARTICLE_TYPE, Util.id("brine_bubble"), FabricParticleTypes.simple());
    public static final DefaultParticleType SMALL_BRINE_BUBBLE = Registry.register(Registry.PARTICLE_TYPE, Util.id("small_brine_bubble"), FabricParticleTypes.simple());

    public static void init() {
        ParticleFactoryRegistry.getInstance().register(FALLING_BRINE, s -> new BlockLeakParticle.FallingWaterFactory(s) {
            @Override
            public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
                BlockLeakParticle r = (BlockLeakParticle)super.createParticle(parameters, world, x, y, z, velocityX, velocityY, velocityZ);
                r.setColor(0.25f, 1.0f, 0.4f);
                ((BlockLeakParticleDuck)r).setNextParticle(BRINE_SPLASH);
                return r;
            }
        });
        ParticleFactoryRegistry.getInstance().register(DRIPPING_BRINE, s -> new BlockLeakParticle.DrippingWaterFactory(s) {
            @Override
            public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
                BlockLeakParticle r = (BlockLeakParticle)super.createParticle(parameters, world, x, y, z, velocityX, velocityY, velocityZ);
                r.setColor(0.25f, 1.0f, 0.4f);
                ((BlockLeakParticleDuck)r).setNextParticle(FALLING_BRINE);
                return r;
            }
        });
        ParticleFactoryRegistry.getInstance().register(BRINE_SPLASH, WaterSplashParticle.SplashFactory::new);
        ParticleFactoryRegistry.getInstance().register(BRINE_BUBBLE, WaterBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SMALL_BRINE_BUBBLE, BubbleColumnUpParticle.Factory::new);
    }
}
