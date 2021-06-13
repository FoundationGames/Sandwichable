package io.github.foundationgames.sandwichable.block.entity.renderer;

import io.github.foundationgames.sandwichable.block.entity.BasinBlockEntity;
import io.github.foundationgames.sandwichable.block.entity.BasinContent;
import io.github.foundationgames.sandwichable.block.entity.BasinContentType;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BasinBlockEntityRenderer implements BlockEntityRenderer<BasinBlockEntity> {
    private final CheeseModel cheeseModel;
    private final MilkModel milkModel;

    public BasinBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        cheeseModel = new CheeseModel(ctx.getLayerModelPart(LayerModelRegistry.CHEESE));
        milkModel = new MilkModel(ctx.getLayerModelPart(LayerModelRegistry.MILK));
    }

    @Override
    public void render(BasinBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        Identifier tex = new Identifier("sandwichable", "textures/entity/basin/milk.png");
        Identifier cheeseTex = blockEntity.getContent().getCheeseType().getTexture();
        if(blockEntity.getContent() == BasinContent.MILK) {
            milkModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(tex)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        else if(blockEntity.getContent().getContentType() == BasinContentType.FERMENTING_MILK) {
            float progressToFloatInv = 1.0F - (float)blockEntity.getFermentProgress() / BasinBlockEntity.fermentTime;
            cheeseModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(cheeseTex)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            milkModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(tex)), light, overlay, 1.0F, 1.0F, 1.0F, progressToFloatInv);
        }
        else if(blockEntity.getContent().getContentType() == BasinContentType.CHEESE) {
            cheeseModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(cheeseTex)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        matrices.pop();
    }

    public static class CheeseModel extends Model {
        ModelPart cheese;

        public CheeseModel(ModelPart root) {
            super(RenderLayer::getEntitySolid);
            this.cheese = root;
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            cheese.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("cheese", ModelPartBuilder.create().uv(0,0)
                    .cuboid(3.0F, 2.0F, 3.0F, 10, 4.0F, 10),
                    ModelTransform.pivot(0.0f, 0.0f, 0.0f));
            return TexturedModelData.of(modelData, 64, 32);
        }
    }

    public static class MilkModel extends Model {
        ModelPart milk;

        public MilkModel(ModelPart root) {
            super(RenderLayer::getEntityTranslucent);
            this.milk = root;
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            milk.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("milk", ModelPartBuilder.create().uv(0,0)
                    .cuboid(3.0F, 2.0F, 3.0F, 10, 4.0F, 10),
                    ModelTransform.pivot(0.0f, 0.0f, 0.0f));
            return TexturedModelData.of(modelData, 64, 32);
        }
    }
}
