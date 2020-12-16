package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.ShrubBlock;
import io.github.foundationgames.sandwichable.blocks.entity.container.BottleCrateScreenHandler;
import io.github.foundationgames.sandwichable.blocks.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.blocks.entity.container.screen.BottleCrateScreen;
import io.github.foundationgames.sandwichable.blocks.entity.container.screen.DesalinatorScreen;
import io.github.foundationgames.sandwichable.blocks.entity.renderer.*;
import io.github.foundationgames.sandwichable.entity.EntitiesRegistry;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import io.github.foundationgames.sandwichable.entity.render.SandwichTableMinecartEntityRenderer;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.SpreadRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
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

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : GrassColors.getColor(0.5D, 1.0D), BlocksRegistry.SHRUB.asItem());

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if(stack.getOrCreateTag().getString("spreadType") != null) {
                if(SpreadRegistry.INSTANCE.fromString(stack.getOrCreateTag().getString("spreadType")) != null) {
                    return SpreadRegistry.INSTANCE.fromString(stack.getOrCreateTag().getString("spreadType")).getColor(stack);
                }
            }
            return 0xFFFFFF;
        },
        ItemsRegistry.SPREAD);

        ScreenRegistry.<DesalinatorScreenHandler, DesalinatorScreen>register(Sandwichable.DESALINATOR_HANDLER, DesalinatorScreen::new);
        ScreenRegistry.<BottleCrateScreenHandler, BottleCrateScreen>register(Sandwichable.BOTTLE_CRATE_HANDLER, BottleCrateScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.SHRUB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.POTTED_SHRUB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.LETTUCE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.TOMATOES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.CUCUMBERS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.ONIONS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlocksRegistry.PICKLE_JAR, RenderLayer.getCutout());

        EntityRendererRegistry.INSTANCE.register(EntitiesRegistry.SANDWICH_TABLE_MINECART, (dispatcher, context) -> new SandwichTableMinecartEntityRenderer(dispatcher));

        ClientSidePacketRegistry.INSTANCE.register(Util.id("sync_sandwich_table_cart"), (ctx, buf) -> {
            Entity e = ctx.getPlayer().getEntityWorld().getEntityById(buf.readInt());
            CompoundTag tag = buf.readCompoundTag();
            ctx.getTaskQueue().execute(() -> {
                if(e instanceof SandwichTableMinecartEntity) {
                    ((SandwichTableMinecartEntity)e).readSandwichTableData(tag);
                }
            });
        });

        FabricModelPredicateProviderRegistry.register(ItemsRegistry.SPREAD, Util.id("loaf_shape"), (stack, world, entity) -> {
            if(stack.getOrCreateTag().contains("onLoaf")) return stack.getOrCreateTag().getBoolean("onLoaf") ? 1 : 0;
            return 0;
        });
    }
}
