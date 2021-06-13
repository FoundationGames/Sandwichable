package io.github.foundationgames.sandwichable.block.entity.renderer;

import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class LayerModelRegistry {
    private static final Identifier MAIN = Util.id("main");
    public static final EntityModelLayer CHEESE = new EntityModelLayer(MAIN, "cheese");
    public static final EntityModelLayer MILK = new EntityModelLayer(MAIN, "milk");
    public static final EntityModelLayer CUCUMBER = new EntityModelLayer(MAIN, "cucumber");
    public static final EntityModelLayer PICKLED_CUCUMBER = new EntityModelLayer(MAIN, "pickled_cucumber");
    public static final EntityModelLayer PICKLE_JAR_FLUID = new EntityModelLayer(MAIN, "pickle_jar_fluid");

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(CHEESE, BasinBlockEntityRenderer.CheeseModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MILK, BasinBlockEntityRenderer.MilkModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(CUCUMBER, () -> PickleJarBlockEntityRenderer.CucumberModel.getTexturedModelData(false));
        EntityModelLayerRegistry.registerModelLayer(PICKLED_CUCUMBER, () -> PickleJarBlockEntityRenderer.CucumberModel.getTexturedModelData(true));
        EntityModelLayerRegistry.registerModelLayer(PICKLE_JAR_FLUID, PickleJarBlockEntityRenderer.FluidModel::getTexturedModelData);
    }
}
