package io.github.foundationgames.sandwichable.entity.render;

import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SandwichTableMinecartEntityRenderer extends MinecartEntityRenderer<SandwichTableMinecartEntity> {
    public SandwichTableMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(SandwichTableMinecartEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();

        //DefaultedList<ItemStack> foodList = entity.getFoodList();

        double d = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        double m = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
        Vec3d vec3d = entity.snapPositionToRail(d, e, m);
        float o = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
        if (vec3d != null) {
            Vec3d vec3d2 = entity.snapPositionToRailWithOffset(d, e, m, 0.30000001192092896D);
            Vec3d vec3d3 = entity.snapPositionToRailWithOffset(d, e, m, -0.30000001192092896D);
            if (vec3d2 == null) {
                vec3d2 = vec3d;
            }
            if (vec3d3 == null) {
                vec3d3 = vec3d;
            }
            matrices.translate(vec3d.x - d, (vec3d2.y + vec3d3.y) / 2.0D - e, vec3d.z - m);
            Vec3d vec3d4 = vec3d3.add(-vec3d2.x, -vec3d2.y, -vec3d2.z);
            if (vec3d4.length() != 0.0D) {
                vec3d4 = vec3d4.normalize();
                yaw = (float)(Math.atan2(vec3d4.z, vec3d4.x) * 180.0D / Math.PI);
                o = (float)(Math.atan(vec3d4.y) * 73.0D);
            }
        }

        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        matrices.translate(o * 0.0061, Math.abs(o) * 0.00269, 0);

        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(o));

        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getEntityWorld(), entity.getBlockPos().up());
        matrices.translate(0, 1.048, -0.125);

        if(Util.triggerLowLOD(50, entity.getPos())) entity.getSandwich().renderLowLOD(matrices, vertexConsumers, lightAbove, OverlayTexture.DEFAULT_UV);
        else entity.getSandwich().render(matrices, vertexConsumers, lightAbove, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
}
