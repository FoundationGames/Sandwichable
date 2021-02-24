package io.github.foundationgames.sandwichable.block.entity.renderer;

import io.github.foundationgames.sandwichable.block.entity.SandwichTableBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class SandwichTableBlockEntityRenderer extends BlockEntityRenderer<SandwichTableBlockEntity> {

    public SandwichTableBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SandwichTableBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        matrices.push();

        matrices.translate(0.5, 1.017, 0.3772);
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        blockEntity.getSandwich().render(matrices, vertexConsumers, lightAbove, overlay);
        /*matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((90)));
        for (ItemStack stack : foodList) {
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);
            matrices.translate(0.0, 0.0, -0.034);
        }*/

        matrices.pop();
    }
}
