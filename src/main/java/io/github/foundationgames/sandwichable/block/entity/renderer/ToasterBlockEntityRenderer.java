package io.github.foundationgames.sandwichable.block.entity.renderer;

import io.github.foundationgames.sandwichable.block.ToasterBlock;
import io.github.foundationgames.sandwichable.block.entity.ToasterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import java.util.Objects;

public class ToasterBlockEntityRenderer implements BlockEntityRenderer<ToasterBlockEntity> {

    public ToasterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super();
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
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rot));
        matrices.translate(0, 0, -0.5);
        matrices.translate(0, 0, 0.41);
        MinecraftClient.getInstance().getItemRenderer().renderItem(items.get(0), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers,0);
        matrices.translate(0, 0, 0.18);
        MinecraftClient.getInstance().getItemRenderer().renderItem(items.get(1), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers,0);

        matrices.pop();
    }
}
