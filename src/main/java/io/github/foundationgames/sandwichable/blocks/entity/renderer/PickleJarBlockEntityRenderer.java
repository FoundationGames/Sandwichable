package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.entity.PickleJarBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.PickleJarFluid;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class PickleJarBlockEntityRenderer implements BlockEntityRenderer<PickleJarBlockEntity> {
    //private final ModelPart fluid;
    //private final ModelPart cucumberBottom;
    //private final ModelPart cucumberTop;

    public PickleJarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(PickleJarBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, -0.69F, 0.5F);
        //CucumberModel cucumber = new CucumberModel(blockEntity.areItemsPickled());
        Identifier cucumberTex = new Identifier("sandwichable", "textures/entity/pickle_jar/cucumber.png");
        for (int i = 0; i < blockEntity.getItemCount(); i++) {
            matrices.translate(0.0F, 0.0F, 0.17F);
            //cucumber.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(cucumberTex)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.translate(0.0F, 0.0F, -0.17F);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        }
        for (int i = 0; i < blockEntity.getItemCount(); i++) {
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90));
        }
        matrices.pop();
        matrices.push();
        Identifier fluidTex = new Identifier("sandwichable", "textures/entity/pickle_jar/pickle_jar_fluid.png");
        //FluidModel fluidModel = new FluidModel(64, 32);
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
            //fluidModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(fluidTex)), light, overlay, r, g, b, 0.69F);
        }
        matrices.pop();
    }

    /*
    private static class FluidModel extends Model {
        private final ModelPart fluid;

        public FluidModel(int texWidth, int texHeight) {
            super(RenderLayer::getEntitySolid);
            this.textureHeight=texHeight;
            this.textureWidth=texWidth;
            this.fluid = new ModelPart(this);
            this.fluid.addCuboid(3.001F, 1.0F, 3.001F, 9.998F, 9.0F, 9.998F);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            fluid.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }

    private static class CucumberModel extends Model {
        private final ModelPart cucumberBottom;
        private final ModelPart cucumberTop;

        public CucumberModel(boolean isPickled) {
            super(RenderLayer::getEntitySolid);
            this.cucumberBottom = new ModelPart(textureWidth, textureHeight, 0, isPickled ? 9 : 0);
            this.cucumberBottom.setPivot(-0.5F, 21.0F, 0.5F);
            this.cucumberBottom.roll = 0.1745F;
            this.cucumberBottom.addCuboid(-1.5F, -3.0F, -1.5F, 3, 6, 3);
            this.cucumberTop = new ModelPart(textureWidth, textureHeight, 0, isPickled ? 9 : 0);
            this.cucumberTop.setPivot(-0.5F, 15.65F, 0.5F);
            this.cucumberTop.roll = -0.1745F;
            this.cucumberTop.addCuboid(-1.5F, -3.0F, -1.475F, 3, 6, 2.99F);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            this.cucumberBottom.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.cucumberTop.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }
     */

    public static void setupModels() {

    }
}
