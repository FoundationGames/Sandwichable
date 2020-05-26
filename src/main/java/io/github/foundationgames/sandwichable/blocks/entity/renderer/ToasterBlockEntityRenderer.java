package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.CuttingBoardBlock;
import io.github.foundationgames.sandwichable.blocks.ToasterBlock;
import io.github.foundationgames.sandwichable.blocks.entity.ToasterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class ToasterBlockEntityRenderer extends BlockEntityRenderer<ToasterBlockEntity> {

    public ToasterBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
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
        int rot = 45;
        switch (dir) {
            case NORTH: rot = 270; break;
            case SOUTH: rot = 90; break;
            case WEST: rot = 0; break;
            case EAST: rot = 180; break;
        }
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rot));
        matrices.translate(0, 0, -0.5);
        matrices.translate(0, 0, 0.41);
        MinecraftClient.getInstance().getItemRenderer().renderItem(items.get(0), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
        matrices.translate(0, 0, 0.18);
        MinecraftClient.getInstance().getItemRenderer().renderItem(items.get(1), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);

        matrices.pop();
    }
}
