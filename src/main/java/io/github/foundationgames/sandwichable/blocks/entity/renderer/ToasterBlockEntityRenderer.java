package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.ToasterBlock;
import io.github.foundationgames.sandwichable.blocks.entity.ToasterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.Objects;

public class ToasterBlockEntityRenderer implements BlockEntityRenderer<ToasterBlockEntity> {

    public ToasterBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(ToasterBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        DefaultedList<ItemStack> items = blockEntity.getItems();
        Direction dir = Direction.NORTH;
        if(blockEntity.getWorld().getBlockState(blockEntity.getPos()).getBlock() instanceof ToasterBlock) {
            dir = Objects.requireNonNull(blockEntity.getToasterFacing());
            if(blockEntity.isToasting() || blockEntity.getWorld().getBlockState(blockEntity.getPos()).get(ToasterBlock.ON)) {
                matrices.translate(0, -0.11, 0);
            }
        }
        matrices.translate(0.5, 0.46, 0.5);
        int rot = switch (dir) {
            case NORTH -> 270;
            case SOUTH -> 90;
            case WEST -> 0;
            case EAST -> 180;
            default -> 45;
        };
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot));
        matrices.translate(0, 0, -0.5);
        matrices.translate(0, 0, 0.41);
        MinecraftClient.getInstance().getItemRenderer().renderItem(items.get(0), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 346746554);
        matrices.translate(0, 0, 0.18);
        MinecraftClient.getInstance().getItemRenderer().renderItem(items.get(1), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 834871346);

        matrices.pop();
    }
}
