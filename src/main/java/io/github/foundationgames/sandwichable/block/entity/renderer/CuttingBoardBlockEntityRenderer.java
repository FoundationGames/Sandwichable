package io.github.foundationgames.sandwichable.block.entity.renderer;

import io.github.foundationgames.sandwichable.block.CuttingBoardBlock;
import io.github.foundationgames.sandwichable.block.entity.CuttingBoardBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import java.util.Objects;

public class CuttingBoardBlockEntityRenderer extends BlockEntityRenderer<CuttingBoardBlockEntity> {

    public CuttingBoardBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CuttingBoardBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = blockEntity.getItem();
        Direction dir = Direction.NORTH;
        if(blockEntity.getWorld().getBlockState(blockEntity.getPos()).getBlock() instanceof CuttingBoardBlock) {
            dir = Objects.requireNonNull(blockEntity.getWorld()).getBlockState(blockEntity.getPos()).get(Properties.HORIZONTAL_FACING);
        }
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
        for (int i = 0; i < stack.getCount(); i++) {
            matrices.push();
            matrices.translate(0.5, 0.08, 0.5);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((270)));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)(rotation + (i * 36))));
            matrices.translate(0.0, -0.117, i * 0.03124);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
            matrices.pop();
        }
        float knifePush = (float)Math.min(stack.getCount(), 8) / 8;
        float knifeCut = blockEntity.getKnifeAnimationTicks() > 0 ? (5 - Math.abs(((blockEntity.getKnifeAnimationTicks() - tickDelta) - 5))) / 5 : 0;
        matrices.push();
        matrices.translate(0.5, 0.4 - (knifeCut * 0.13), 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        matrices.translate(-0.05 - (knifePush * 0.23) - (knifePush > 0 ? 0.08 : 0) + (knifeCut * 0.13), 0, 0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(189 + (knifePush * 40) - (knifeCut * 35)));
        MinecraftClient.getInstance().getItemRenderer().renderItem(blockEntity.getKnife(), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
        matrices.pop();
    }
}
