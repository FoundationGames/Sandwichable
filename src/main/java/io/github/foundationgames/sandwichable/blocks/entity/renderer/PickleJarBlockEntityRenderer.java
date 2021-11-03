package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.entity.PickleJarBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.PickleJarFluid;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class PickleJarBlockEntityRenderer implements BlockEntityRenderer<PickleJarBlockEntity> {
    public static final Identifier TEX_CUCUMBER = Util.id("textures/entity/pickle_jar/cucumber.png");
    public static final Identifier TEX_FLUID = Util.id("textures/entity/pickle_jar/pickle_jar_fluid.png");

    private final FluidModel fluid;
    private final CucumberModel cucumber;

    public PickleJarBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.fluid = new FluidModel(context);
        this.cucumber = new CucumberModel(context);
    }

    @Override
    public void render(PickleJarBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5F, -0.69F, 0.5F);
        for (int i = 0; i < blockEntity.getItemCount(); i++) {
            matrices.translate(0.0F, 0.0F, 0.17F);
            this.cucumber.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEX_CUCUMBER)), light, overlay, blockEntity.areItemsPickled());
            matrices.translate(0.0F, 0.0F, -0.17F);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        }
        for (int i = 0; i < blockEntity.getItemCount(); i++) {
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90));
        }
        matrices.pop();
        matrices.push();
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
            this.fluid.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(TEX_FLUID)), light, overlay, r, g, b, 0.69F);
        }
        matrices.pop();
    }

    public static class FluidModel extends Model {
        public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Util.id("pickle_jar/fluid"), "main");

        private final ModelPart part;

        public FluidModel(BlockEntityRendererFactory.Context ctx) {
            super(RenderLayer::getEntityTranslucent);
            this.part = ctx.getLayerModelPart(MODEL_LAYER).getChild("main");
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            part.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }

        public static TexturedModelData createModelData() {
            var data = new ModelData();
            data.getRoot().addChild("main",
                    ModelPartBuilder.create().cuboid(3.001f, 1f, 3.001f, 9.998f, 9f, 9.998f),
                    ModelTransform.NONE
            );
            return TexturedModelData.of(data, 64, 32);
        }
    }

    public static class CucumberModel extends Model {
        public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Util.id("pickle_jar/cucumber"), "main");

        private final ModelPart cucumberTop;
        private final ModelPart cucumberBottom;
        private final ModelPart pickleTop;
        private final ModelPart pickleBottom;

        public CucumberModel(BlockEntityRendererFactory.Context ctx) {
            super(RenderLayer::getEntitySolid);
            var root = ctx.getLayerModelPart(MODEL_LAYER);
            this.cucumberTop = root.getChild("cucumber_top");
            this.cucumberBottom = root.getChild("cucumber_bottom");
            this.pickleTop = root.getChild("pickle_top");
            this.pickleBottom = root.getChild("pickle_bottom");
            System.out.println();
        }

        /**
         * Does not account for pickle state
         */
        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            this.cucumberTop.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
            this.cucumberBottom.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        }

        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, boolean pickled) {
            if (pickled) {
                this.pickleTop.render(matrices, vertexConsumer, light, overlay);
                this.pickleBottom.render(matrices, vertexConsumer, light, overlay);
            } else {
                this.cucumberTop.render(matrices, vertexConsumer, light, overlay);
                this.cucumberBottom.render(matrices, vertexConsumer, light, overlay);
            }
        }

        public static TexturedModelData createModelData() {
            var data = new ModelData();
            data.getRoot().addChild("cucumber_top",
                    ModelPartBuilder.create().cuboid(-1.5F, -3.0F, -1.475F, 3, 6, 2.99F),
                    ModelTransform.of(-0.5F, 15.65F, 0.5F, 0, 0, -0.1745F)
            );
            data.getRoot().addChild("cucumber_bottom",
                    ModelPartBuilder.create().cuboid(-1.5F, -3.0F, -1.5F, 3, 6, 3),
                    ModelTransform.of(-0.5F, 21.0F, 0.5F, 0, 0, 0.1745F)
            );
            data.getRoot().addChild("pickle_top",
                    ModelPartBuilder.create().uv(0, 9).cuboid(-1.5F, -3.0F, -1.475F, 3, 6, 2.99F),
                    ModelTransform.of(-0.5F, 15.65F, 0.5F, 0, 0, -0.1745F)
            );
            data.getRoot().addChild("pickle_bottom",
                    ModelPartBuilder.create().uv(0, 9).cuboid(-1.5F, -3.0F, -1.5F, 3, 6, 3),
                    ModelTransform.of(-0.5F, 21.0F, 0.5F, 0, 0, 0.1745F)
            );
            return TexturedModelData.of(data, 32, 32);
        }
    }
}
