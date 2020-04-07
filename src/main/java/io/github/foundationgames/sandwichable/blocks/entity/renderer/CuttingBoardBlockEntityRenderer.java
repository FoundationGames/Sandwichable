package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.entity.CuttingBoardBlockEntity;
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
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class CuttingBoardBlockEntityRenderer extends BlockEntityRenderer<CuttingBoardBlockEntity> {

    public CuttingBoardBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CuttingBoardBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        ItemStack item = blockEntity.getItem();
        Direction dir = Objects.requireNonNull(blockEntity.getWorld()).getBlockState(blockEntity.getPos()).get(Properties.HORIZONTAL_FACING);
        int rotation = 40;
        switch (dir) {
            case NORTH:
                rotation = 0; break;
            case SOUTH:
                rotation = 180; break;
            case EAST:
                rotation = 270; break;
            case WEST:
                rotation = 90; break;
        }
        matrices.translate(0.5, 0.08, 0.5);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((270)));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((rotation)));
        matrices.translate(0.0, -0.117, 0.0);
        MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
        matrices.pop();
    }
}
