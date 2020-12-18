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
import net.minecraft.util.collection.DefaultedList;

public class SandwichBlockEntityRenderer extends BlockEntityRenderer<SandwichBlockEntity> {

    public SandwichBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SandwichBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        matrices.push();

        matrices.translate(0.5, 0.017, 0.3772);
        blockEntity.getSandwich().render(matrices, vertexConsumers, light, overlay);

        matrices.pop();
    }
}
