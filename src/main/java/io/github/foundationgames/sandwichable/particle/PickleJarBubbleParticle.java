package io.github.foundationgames.sandwichable.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

public class PickleJarBubbleParticle extends SpriteBillboardParticle {
    private BlockState cachedState;

    public PickleJarBubbleParticle(ClientWorld clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z);
        this.velocityY = 0.009f + (this.random.nextFloat() * 0.003f);
        this.scale = 0.04f + (this.random.nextFloat() * 0.03f);
        this.maxAge = 45;
        this.colorAlpha = 1.0f;

        this.cachedState = clientWorld.getBlockState(new BlockPos(x, y, z));
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
    public void buildGeometry(VertexConsumer ignored, Camera camera, float tickDelta) {
        VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(new Identifier(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE.getNamespace(), SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE.getPath()), true));
        Vec3d cameraPos = camera.getPos();
        float x = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.x);
        float y = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.y);
        float z = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.z);

        Quaternion q = camera.getRotation();
        /*Vector3f rot = new Vector3f(-1.0F, -1.0F, 0.0F);
        rot.rotate(q);*/
        Vector3f[] vertices = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float size = this.getSize(tickDelta);
        for (int i = 0; i < 4; i++) {
            Vector3f v = vertices[i];
            v.rotate(q);
            v.scale(size);
            //v.add(x, y, z);
            //v.rotate(Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw()));
            //v.rotate(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        }

        MatrixStack matrices = new MatrixStack();

        matrices.push();

        float u0 = this.getMinU();
        float u1 = this.getMaxU();
        float v0 = this.getMinV();
        float v1 = this.getMaxV();
        int c = this.getColorMultiplier(tickDelta);
        int o = OverlayTexture.DEFAULT_UV;

        matrices.translate(x, y, z);
        Matrix4f matrix = matrices.peek().getModel();
        vertexConsumer.vertex(matrix, vertices[0].getX(), vertices[0].getY(), vertices[0].getZ()).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).texture(u1, v1).overlay(o).light(c).normal(1, 0, 1).next();
        vertexConsumer.vertex(matrix, vertices[1].getX(), vertices[1].getY(), vertices[1].getZ()).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).texture(u1, v0).overlay(o).light(c).normal(1, 0, 0).next();
        vertexConsumer.vertex(matrix, vertices[2].getX(), vertices[2].getY(), vertices[2].getZ()).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).texture(u0, v0).overlay(o).light(c).normal(0, 0, 0).next();
        vertexConsumer.vertex(matrix, vertices[3].getX(), vertices[3].getY(), vertices[3].getZ()).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).texture(u0, v1).overlay(o).light(c).normal(0, 0, 1).next();
        vertexConsumers.draw();

        matrices.pop();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
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
