package io.github.foundationgames.sandwichable.util;

import io.github.foundationgames.sandwichable.blocks.entity.CuttingBoardBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public abstract class CuttingBoardItemModel {
    public abstract void render(CuttingBoardBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
