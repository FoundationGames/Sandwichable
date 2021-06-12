package io.github.foundationgames.sandwichable.block.entity.renderer;

import io.github.foundationgames.sandwichable.block.entity.PickleJarBlockEntity;
import io.github.foundationgames.sandwichable.block.entity.PickleJarFluid;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class PickleJarBlockEntityRenderer implements BlockEntityRenderer<PickleJarBlockEntity> {
    private final CucumberModel cucumberModel;
    private final CucumberModel pickledCucumberModel;
    private final FluidModel fluidModel;

    public PickleJarBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        cucumberModel = new CucumberModel(ctx.getLayerModelPart(LayerModelRegistry.CUCUMBER));
        pickledCucumberModel = new CucumberModel(ctx.getLayerModelPart(LayerModelRegistry.PICKLED_CUCUMBER));
        fluidModel = new FluidModel(ctx.getLayerModelPart(LayerModelRegistry.PICKLE_JAR_FLUID));
    }

    @Override
    public void render(PickleJarBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, -0.69F, 0.5F);
        CucumberModel cucumber = blockEntity.areItemsPickled() ? cucumberModel : pickledCucumberModel;
        Identifier cucumberTex = new Identifier("sandwichable", "textures/entity/pickle_jar/cucumber.png");
        for (int i = 0; i < blockEntity.getItemCount(); i++) {
            matrices.translate(0.0F, 0.0F, 0.17F);
            cucumber.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(cucumberTex)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.translate(0.0F, 0.0F, -0.17F);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        }
        for (int i = 0; i < blockEntity.getItemCount(); i++) {
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90));
        }
        matrices.pop();
        matrices.push();
        Identifier fluidTex = new Identifier("sandwichable", "textures/entity/pickle_jar/pickle_jar_fluid.png");

        float progressToFloat = (float)blockEntity.getPickleProgress() / PickleJarBlockEntity.pickleTime;
        float r = 1.0F;
        float g = 1.0F;
        float b = 1.0F;
        if(blockEntity.getFluid() == PickleJarFluid.WATER) {
            r = 0.2F; g = 0.5F; b = 1.0F;
        }
        if(blockEntity.getFluid() == PickleJarFluid.PICKLING_BRINE) {
            r = 0.2F+(progressToFloat*0.2F); g = 0.5F+(progressToFloat*0.5F); b = 1.0F-(progressToFloat*0.5F);
        }
        if(blockEntity.getFluid() == PickleJarFluid.PICKLED_BRINE) {
            r = 0.4F; g = 1.0F; b = 0.5F;
        }
        if(blockEntity.getFluid() != PickleJarFluid.AIR) {
            fluidModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(fluidTex)), light, overlay, r, g, b, 0.69F);
        }
        matrices.pop();
    }

    public static class FluidModel extends Model {
        ModelPart fluid;

        public FluidModel(ModelPart root) {
            super(RenderLayer::getEntitySolid);
            this.fluid = root;
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            fluid.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("fluid", ModelPartBuilder.create().uv(0,0)
                    .cuboid(3.001F, 1.0F, 3.001F, 9.998F, 9.0F, 9.998F),
                    ModelTransform.pivot(0.0f, 0.0f, -1.0f));
            return TexturedModelData.of(modelData, 64, 32);
        }
    }
    public static class CucumberModel extends Model {
        ModelPart cucumberBottom;
        ModelPart cucumberTop;

        public CucumberModel(ModelPart root) {
            super(RenderLayer::getEntitySolid);
            cucumberBottom = root.getChild("bottom");
            cucumberTop = root.getChild("top");

            this.cucumberBottom.roll = 0.1745F;
            this.cucumberTop.roll = -0.1745F;
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            this.cucumberBottom.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.cucumberTop.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }

        public static TexturedModelData getTexturedModelData(boolean pickled) {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            modelPartData.addChild("top", ModelPartBuilder.create().uv(0, pickled ? 9 : 0)
                    .cuboid(-1.5F, -3.0F, -1.5F, 3, 6, 3),
                    ModelTransform.pivot(-0.5F, 15.65F, 0.5F));
            modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0,pickled ? 9 : 0)
                    .cuboid(-1.5F, -3.0F, -1.5F, 3, 6, 3),
                    ModelTransform.pivot(-0.5F, 21.0F, 0.5F));
            return TexturedModelData.of(modelData, 64, 32);
        }
    }
}
