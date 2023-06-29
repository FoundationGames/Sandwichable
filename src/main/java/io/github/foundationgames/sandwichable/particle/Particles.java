package io.github.foundationgames.sandwichable.particle;

import io.github.foundationgames.sandwichable.util.BlockLeakParticleDuck;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class Particles {
    public static final DefaultParticleType DRIPPING_BRINE = Registry.register(Registries.PARTICLE_TYPE, Util.id("dripping_brine"), FabricParticleTypes.simple());
    public static final DefaultParticleType FALLING_BRINE = Registry.register(Registries.PARTICLE_TYPE, Util.id("falling_brine"), FabricParticleTypes.simple());
    public static final DefaultParticleType BRINE_SPLASH = Registry.register(Registries.PARTICLE_TYPE, Util.id("brine_splash"), FabricParticleTypes.simple());
    public static final DefaultParticleType BRINE_BUBBLE = Registry.register(Registries.PARTICLE_TYPE, Util.id("brine_bubble"), FabricParticleTypes.simple());
    public static final DefaultParticleType SMALL_BRINE_BUBBLE = Registry.register(Registries.PARTICLE_TYPE, Util.id("small_brine_bubble"), FabricParticleTypes.simple());

    public static void init() {
        ParticleFactoryRegistry.getInstance().register(FALLING_BRINE, withSprite(Particles::createFallingBrine));
        ParticleFactoryRegistry.getInstance().register(DRIPPING_BRINE, withSprite(Particles::createDrippingBrine));
        ParticleFactoryRegistry.getInstance().register(BRINE_SPLASH, WaterSplashParticle.SplashFactory::new);
        ParticleFactoryRegistry.getInstance().register(BRINE_BUBBLE, WaterBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SMALL_BRINE_BUBBLE, BubbleColumnUpParticle.Factory::new);
    }

    public static SpriteBillboardParticle createFallingBrine(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        var particle = BlockLeakParticle.createFallingWater(type, world, x, y, z, velocityX, velocityY, velocityZ);
        if (particle instanceof BlockLeakParticle leak) {
            leak.setColor(0.25f, 1.0f, 0.4f);
            ((BlockLeakParticleDuck)leak).setNextParticle(BRINE_SPLASH);
        }
        return particle;
    }

    public static SpriteBillboardParticle createDrippingBrine(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        var particle = BlockLeakParticle.createDrippingWater(type, world, x, y, z, velocityX, velocityY, velocityZ);
        if (particle instanceof BlockLeakParticle leak) {
            leak.setColor(0.25f, 1.0f, 0.4f);
            ((BlockLeakParticleDuck)leak).setNextParticle(FALLING_BRINE);
        }
        return particle;
    }

    public static <E extends ParticleEffect> ParticleFactoryRegistry.PendingParticleFactory<E> withSprite(ParticleFactory.BlockLeakParticleFactory<E> factory) {
        return sprites -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> {
            var particle = factory.createParticle(parameters, world, x, y, z, velocityX, velocityY, velocityZ);
            if (particle != null) {
                particle.setSprite(sprites);
            }
            return particle;
        };
    }
}
