package io.github.foundationgames.sandwichable.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class PickleJarBubbleParticle extends SpriteBillboardParticle {
    public PickleJarBubbleParticle(ClientWorld clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z);
        this.velocityY = 0.009f + (this.random.nextFloat() * 0.003f);
        this.scale = 0.02f + (this.random.nextFloat() * 0.01f);
        this.maxAge = 45;
        this.colorAlpha = 1.0f;
    }

    @Override
    public void tick() {
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        double xo = this.x - Math.floor(this.x);
        double yo = this.y - Math.floor(this.y);
        double zo = this.z - Math.floor(this.z);
        if(xo > 3.5d/16d && xo < 12.5d/16d && zo > 3.5d/16d && zo < 12.5d/16d && yo < 10d/16d) {
            this.velocityY += 0.002D;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
        } else {
            markDead();
        }
        if(this.maxAge-- <= 0) markDead();
    }

    @Override
    public ParticleTextureSheet getType() {
        return Particles.BLENDED;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double dx, double dy, double dz) {
            PickleJarBubbleParticle r = new PickleJarBubbleParticle(clientWorld, x, y, z);
            r.setSprite(this.spriteProvider);
            return r;
        }
    }
}
