package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.ShrubBlock;
import io.github.foundationgames.sandwichable.blocks.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.blocks.entity.container.screen.DesalinatorScreen;
import io.github.foundationgames.sandwichable.blocks.entity.renderer.*;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SpreadRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.TranslatableText;

public class SandwichableClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.SANDWICHTABLE_BLOCKENTITY, SandwichTableBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.SANDWICH_BLOCKENTITY, SandwichBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.CUTTINGBOARD_BLOCKENTITY, CuttingBoardBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.TOASTER_BLOCKENTITY, ToasterBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.BASIN_BLOCKENTITY, BasinBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.PICKLEJAR_BLOCKENTITY, PickleJarBlockEntityRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> !state.get(ShrubBlock.SNIPPED) ? BiomeColors.getGrassColor(view, pos) : FoliageColors.getDefaultColor(), BlocksRegistry.SHRUB, BlocksRegistry.POTTED_SHRUB);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? FoliageColors.getDefaultColor() : FoliageColors.getSpruceColor(), BlocksRegistry.SHRUB.asItem());

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if(stack.getOrCreateTag().getString("spreadType") != null) {
                if(SpreadRegistry.INSTANCE.deserialize(stack.getOrCreateTag().getString("spreadType")) != null) {
                    return SpreadRegistry.INSTANCE.deserialize(stack.getOrCreateTag().getString("spreadType")).getColor();
                }
            }
            return 0xFFFFFF;
        },
        ItemsRegistry.SPREAD);

        ScreenProviderRegistry.INSTANCE.<DesalinatorScreenHandler>registerFactory(Util.id("desalinator"), (container) -> new DesalinatorScreen(container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.sandwichable.desalinator")));

        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.SHRUB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.POTTED_SHRUB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.LETTUCE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.TOMATOES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.CUCUMBERS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.ONIONS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.PICKLE_JAR, RenderLayer.getCutout());

        if(FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            System.out.println("FOUND REI");
        }
    }
}
