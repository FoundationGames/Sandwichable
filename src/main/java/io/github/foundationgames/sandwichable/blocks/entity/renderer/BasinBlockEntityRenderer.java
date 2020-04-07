package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.entity.BasinBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BasinBlockEntityRenderer extends BlockEntityRenderer<BasinBlockEntity> {

    private final ModelPart cheese;
    private final ModelPart milk;

    public BasinBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);

        this.cheese = new ModelPart(64, 32, 0, 14);
        this.cheese.addCuboid(3.0F, 2.0F, 3.0F, 10, 4.0F, 10);
        this.milk = new ModelPart(64, 32, 0, 0);
        this.milk.addCuboid(3.0F, 2.0F, 3.0F, 10, 4.01F, 10);
    }

    @Override
    public void render(BasinBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        Identifier tex = new Identifier("sandwichable", "textures/entity/basin/cheese_milk.png");
        Identifier text = new Identifier("textures/entity/conduit/base.png");
        VertexConsumer cheeseVertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(tex));
        VertexConsumer milkVertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(tex));

        if(blockEntity.hasMilk() && !blockEntity.isFermenting()) {
            this.milk.render(matrices, cheeseVertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        else if(blockEntity.isFermenting() && !blockEntity.hasCheese()) {
            float progressToFloat = (float)blockEntity.getFermentProgress() / BasinBlockEntity.fermentTime;
            float progressToFloatInv = 1.0F - progressToFloat;
            this.cheese.render(matrices, cheeseVertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            this.milk.render(matrices, milkVertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, progressToFloatInv);
        }
        else if(blockEntity.hasCheese()) {
            this.cheese.render(matrices, cheeseVertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrices.pop();
    }
}
