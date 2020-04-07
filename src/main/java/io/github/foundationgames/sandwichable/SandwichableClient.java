package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.ShrubBlock;
import io.github.foundationgames.sandwichable.blocks.entity.renderer.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.potion.PotionUtil;

public class SandwichableClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.SANDWICHTABLE_BLOCKENTITY, SandwichTableBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.SANDWICH_BLOCKENTITY, SandwichBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.CUTTINGBOARD_BLOCKENTITY, CuttingBoardBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.TOASTER_BLOCKENTITY, ToasterBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.BASIN_BLOCKENTITY, BasinBlockEntityRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> !state.get(ShrubBlock.SNIPPED) ? BiomeColors.getGrassColor(view, pos) : FoliageColors.getDefaultColor(), BlocksRegistry.SHRUB, BlocksRegistry.POTTED_SHRUB);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? FoliageColors.getDefaultColor() : FoliageColors.getSpruceColor(), BlocksRegistry.SHRUB.asItem());

        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.SHRUB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.POTTED_SHRUB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.LETTUCE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.TOMATOES, RenderLayer.getCutout());

        if(FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            System.out.println("FOUND REI");
        }
    }
}
