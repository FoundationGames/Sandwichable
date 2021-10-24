package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.entity.BasinBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.BasinContent;
import io.github.foundationgames.sandwichable.blocks.entity.BasinContentType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BasinBlockEntityRenderer implements BlockEntityRenderer<BasinBlockEntity> {

    public BasinBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BasinBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        Identifier tex = new Identifier("sandwichable", "textures/entity/basin/milk.png");
        Identifier cheeseTex = blockEntity.getContent().getCheeseType().getTexture();
        //CheeseModel cheeseModel = new CheeseModel(64, 32);
        //MilkModel milkModel = new MilkModel(64, 32);
        if(blockEntity.getContent() == BasinContent.MILK) {
            //milkModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(tex)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        else if(blockEntity.getContent().getContentType() == BasinContentType.FERMENTING_MILK) {
            float progressToFloatInv = 1.0F - (float)blockEntity.getFermentProgress() / BasinBlockEntity.fermentTime;
            //cheeseModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(cheeseTex)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            //milkModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(tex)), light, overlay, 1.0F, 1.0F, 1.0F, progressToFloatInv);
        }
        else if(blockEntity.getContent().getContentType() == BasinContentType.CHEESE) {
            //cheeseModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(cheeseTex)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        matrices.pop();
    }

    /*
    private static class CheeseModel extends Model {
        ModelPart cheese;

        public CheeseModel(int texWidth, int texHeight) {
            super(RenderLayer::getEntitySolid);
            this.textureHeight=texHeight;
            this.textureWidth=texWidth;
            this.cheese = new ModelPart(this);
            this.cheese.addCuboid(3.0F, 2.0F, 3.0F, 10, 4.0F, 10);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            cheese.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }

    private static class MilkModel extends Model {
        ModelPart milk;

        public MilkModel(int texWidth, int texHeight) {
            super(RenderLayer::getEntityTranslucent);
            this.textureHeight=texHeight;
            this.textureWidth=texWidth;
            this.milk = new ModelPart(this);
            this.milk.addCuboid(3.0F, 2.0F, 3.0F, 10, 4.0F, 10);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            milk.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }
     */
}
