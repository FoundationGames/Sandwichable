package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.entity.SandwichTableBlockEntity;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class SandwichTableBlockEntityRenderer extends BlockEntityRenderer<SandwichTableBlockEntity> {

    public SandwichTableBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SandwichTableBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        matrices.push();

        DefaultedList<ItemStack> foodList = blockEntity.getFoodList();

        matrices.translate(0.5, 1.017, 0.3772);
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((90)));
        for(int i=0; i < foodList.size(); i++) {
            MinecraftClient.getInstance().getItemRenderer().renderItem(foodList.get(i), ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);
            matrices.translate(0.0, 0.0, -0.034);
        }

        matrices.pop();
    }
}
