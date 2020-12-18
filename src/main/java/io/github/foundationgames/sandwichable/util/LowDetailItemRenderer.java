package io.github.foundationgames.sandwichable.util;

import javafx.beans.property.BooleanProperty;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public final class LowDetailItemRenderer {
    public static final Map<ItemConvertible, Integer> ITEM_COLOR_MAP = new HashMap<>();

    public static void renderItem(ItemConvertible i, int light, int overlay, MatrixStack matrices, VertexConsumer vertices, boolean top, boolean bottom) {
        int c = ITEM_COLOR_MAP.get(i);
        float r = (float)((c >> 16) & 0xFF) / 255;
        float g = (float)((c >> 8) & 0xFF) / 255;
        float b = (float)(c & 0xFF) / 255;
        matrices.push();
        matrices.scale(1f / 32, 1f / 32, 1f / 32);
        matrices.translate(0, 4, -0.5);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
        for (int j = 0; j < 4; j++) {
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
            matrices.translate(0, -8, 8);
            quad(vertices, matrices, light, overlay, r, g, b, 0, 0, 1, 16, true);
            matrices.translate(0, 8, -8);
        }
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90));
        matrices.translate(-8, -8, -1);
        if(bottom) quad(vertices, matrices, light, overlay, r, g, b, 0, 0, 16, 16, false);
        if(top) {
            matrices.translate(0, 0, 1);
            quad(vertices, matrices, light, overlay, r, g, b, 0, 0, 16, 16, true);
        }
        /*matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.translate(0, -4, -0.5);
        matrices.scale(32, 32, 32);*/
        matrices.pop();
    }

    private static void quad(VertexConsumer consumer, MatrixStack matrices, int light, int overlay, float r, float g, float b, int x, int y, int width, int height, boolean reversed) {
        Matrix4f matrix4f = matrices.peek().getModel();
        if(!reversed) {
            consumer.vertex(matrix4f, (float)x, (float)height+y, (float)0).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal((float)x, (float)height+y, (float)0).next();
            consumer.vertex(matrix4f, (float)width+x, (float)height+y, (float)0).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal((float)width+x, (float)height+y, (float)0).next();
            consumer.vertex(matrix4f, (float)width+x, (float)y, (float)0).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal((float)width+x, (float)y, (float)0).next();
            consumer.vertex(matrix4f, (float)x, (float)y, (float)0).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal((float)x, (float)y, (float)0).next();
        } else {
            consumer.vertex(matrix4f, (float)x, (float)y, (float)0).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal((float)x, (float)y, (float)0).next();
            consumer.vertex(matrix4f, (float)width+x, (float)y, (float)0).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal((float)width+x, (float)y, (float)0).next();
            consumer.vertex(matrix4f, (float)width+x, (float)height+y, (float)0).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal((float)width+x, (float)height+y, (float)0).next();
            consumer.vertex(matrix4f, (float)x, (float)height+y, (float)0).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal((float)x, (float)height+y, (float)0).next();
        }
    }
}
