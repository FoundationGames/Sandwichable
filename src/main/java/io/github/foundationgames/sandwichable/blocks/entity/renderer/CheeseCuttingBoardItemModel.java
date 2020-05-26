package io.github.foundationgames.sandwichable.blocks.entity.renderer;

import io.github.foundationgames.sandwichable.blocks.entity.CuttingBoardBlockEntity;
import io.github.foundationgames.sandwichable.util.CuttingBoardItemModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CheeseCuttingBoardItemModel extends CuttingBoardItemModel {

    private final ModelPart largeCube;
    private final ModelPart smallCube;

    public CheeseCuttingBoardItemModel() {
        this.largeCube = new ModelPart(32, 32, 0, 0);
        this.largeCube.addCuboid(0, 0, 0, 8, 4, 4);
        this.smallCube = new ModelPart(32, 32, 0, 8);
        this.smallCube.addCuboid(4, 0, 4, 4, 4, 4);
    }

    @Override
    public void render(CuttingBoardBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        Identifier tex = new Identifier("sandwichable", "textures/entity/cutting_board/cheese_wheel.png");
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(tex));
        this.largeCube.render(matrices, vc, light, overlay);
        this.smallCube.render(matrices, vc, light, overlay);
        matrices.pop();
    }
}
