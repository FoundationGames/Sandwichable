package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.entity.SandwichBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.SandwichTableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public class SandwichBlockEntityRenderer extends BlockEntityRenderer<SandwichBlockEntity> {

    public SandwichBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SandwichBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        matrices.push();

        DefaultedList<ItemStack> foodList = blockEntity.getFoodList();

        matrices.translate(0.5, 0.017, 0.3772);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((90)));
        for(int i=0; i < foodList.size(); i++) {
            MinecraftClient.getInstance().getItemRenderer().renderItem(foodList.get(i), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
            matrices.translate(0.0, 0.0, -0.034);
        }

        matrices.pop();
    }
}
