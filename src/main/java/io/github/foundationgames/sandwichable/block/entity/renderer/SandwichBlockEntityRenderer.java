package io.github.foundationgames.sandwichable.block.entity.renderer;

import io.github.foundationgames.sandwichable.block.entity.SandwichBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class SandwichBlockEntityRenderer implements BlockEntityRenderer<SandwichBlockEntity> {

    public SandwichBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(SandwichBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        matrices.push();

        matrices.translate(0.5, 0.017, 0.3772);
        blockEntity.getSandwich().render(matrices, vertexConsumers, light, overlay);

        matrices.pop();
    }
}
