package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.block.ShrubBlock;
import io.github.foundationgames.sandwichable.block.entity.container.BottleCrateScreenHandler;
import io.github.foundationgames.sandwichable.block.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.block.entity.container.screen.BottleCrateScreen;
import io.github.foundationgames.sandwichable.block.entity.container.screen.DesalinatorScreen;
import io.github.foundationgames.sandwichable.block.entity.renderer.*;
import io.github.foundationgames.sandwichable.compat.CompatModuleManager;
import io.github.foundationgames.sandwichable.entity.EntitiesRegistry;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import io.github.foundationgames.sandwichable.entity.render.SandwichTableMinecartEntityRenderer;
import io.github.foundationgames.sandwichable.fluid.FluidsRegistry;
import io.github.foundationgames.sandwichable.particle.Particles;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.SpreadRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.devtech.arrp.api.RRPCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Function;

public class SandwichableClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.SANDWICHTABLE_BLOCKENTITY, SandwichTableBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.SANDWICH_BLOCKENTITY, SandwichBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.CUTTINGBOARD_BLOCKENTITY, CuttingBoardBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.TOASTER_BLOCKENTITY, ToasterBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.BASIN_BLOCKENTITY, BasinBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlocksRegistry.PICKLEJAR_BLOCKENTITY, PickleJarBlockEntityRenderer::new);

        RRPCallback.EVENT.register(a -> a.add(CompatModuleManager.ASSETS));

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

        EntityRendererRegistry.INSTANCE.register(EntitiesRegistry.SANDWICH_TABLE_MINECART, SandwichTableMinecartEntityRenderer::new);

        ClientSidePacketRegistry.INSTANCE.register(Util.id("sync_sandwich_table_cart"), (ctx, buf) -> {
            Entity e = ctx.getPlayer().getEntityWorld().getEntityById(buf.readInt());
            NbtCompound tag = buf.readNbt();
            ctx.getTaskQueue().execute(() -> {
                if(e instanceof SandwichTableMinecartEntity) {
                    ((SandwichTableMinecartEntity)e).readSandwichTableData(tag);
                }
            });
        });

        ClientSidePacketRegistry.INSTANCE.register(Util.id("cutting_board_particles"), (ctx, buf) -> {
            ItemStack stack = buf.readItemStack();
            int top = buf.readInt();
            int layers = buf.readInt();
            BlockPos pos = buf.readBlockPos();
            Random random = new Random();
            World world = MinecraftClient.getInstance().world;
            ctx.getTaskQueue().execute(() -> {
                for (int i = 0; i < layers; i++) {
                    for (int j = 0; j < 2 + random.nextInt(2); j++) {
                        double x = pos.getX() + 0.5 + ((random.nextDouble() - 0.5) / 3);
                        double y = pos.getY() + 0.094 + ((top - i) * 0.03124);
                        double z = pos.getZ() + 0.5 + ((random.nextDouble() - 0.5) / 3);
                        world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), x, y, z, 0, (random.nextDouble() + 1.0) * 0.066, 0);
                    }
                }
            });
        });

        FabricModelPredicateProviderRegistry.register(ItemsRegistry.SPREAD, Util.id("loaf_shape"), (stack, world, entity, seed) -> {
            if(stack.getOrCreateTag().contains("onLoaf")) return stack.getOrCreateTag().getBoolean("onLoaf") ? 1 : 0;
            return 0;
        });

        FabricModelPredicateProviderRegistry.register(Util.id("sandwich_state"), (stack, world, entity, seed) -> {
            if(entity == null && stack.getOrCreateTag().contains("s")) return stack.getOrCreateTag().getInt("s");
            return 0;
        });

        setupPickleBrine();

        Particles.init();

        LayerModelRegistry.init();
    }

    private static void setupPickleBrine() {
        Fluid still = FluidsRegistry.PICKLE_BRINE;
        Fluid flowing = FluidsRegistry.PICKLE_BRINE_FLOWING;
        Identifier fluidTexture = Util.id("pickle_brine");
        Identifier stillId = new Identifier(fluidTexture.getNamespace(), "block/" + fluidTexture.getPath());
        Identifier flowingId = new Identifier(fluidTexture.getNamespace(), "block/" + fluidTexture.getPath() + "_flow");
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(stillId);
            registry.register(flowingId);
        });
        Identifier fluidId = Registry.FLUID.getId(still);
        Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        Sprite[] sprites = { null, null };

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return listenerId;
            }

            @Override
            public void reload(ResourceManager resourceManager) {
                final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                sprites[0] = atlas.apply(stillId);
                sprites[1] = atlas.apply(flowingId);
            }
        });

        FluidRenderHandler renderHandler = new FluidRenderHandler() {
            @Override
            public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
                return sprites;
            }

            @Override
            public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
                // possible TODO
                // THIS CODE DOESNT WORK???
                /* ERROR:
                 * java.lang.ArrayIndexOutOfBoundsException: -833
                 *      at net.minecraft.client.render.chunk.ChunkRendererRegion.getBlockState(ChunkRendererRegion.java:103)
                 *      at io.github.foundationgames.sandwichable.SandwichableClient$2.getFluidColor(SandwichableClient.java:181)
                 */
                // Meant to be to blend with water color
                /*int rad = (int)(MinecraftClient.getInstance().options.biomeBlendRadius * 0.64);
                if(rad == 0) return 0x65ff6e;
                CuboidBlockIterator iter = new CuboidBlockIterator(pos.getX() - rad, pos.getY() - rad, pos.getZ() - rad, pos.getX() + rad, pos.getY() + rad, pos.getZ() + rad);
                int r = 101;
                int g = 255;
                int b = 110;
                int i = 0;
                int c;
                for(BlockPos.Mutable mpos = new BlockPos.Mutable(); iter.step(); i++) {
                    mpos.set(iter.getX(), Math.min(Math.max(iter.getY(), view.getHeight() - 1), 0), iter.getZ());
                    if(view.getBlockState(mpos).isOf(Blocks.WATER)) {
                        c = BiomeColors.getWaterColor(view, mpos);
                        r += (c >> 16) & 0xFF;
                        g += (c >> 8) & 0xFF;
                        b += c & 0xFF;
                    } else {
                        r += 101;
                        g += 255;
                        b += 110;
                    }
                }
                return ((r / i & 0xFF) << 16 | (g / i & 0xFF) << 8 | b / i & 0xFF);*/
                return 0x65ff6e;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidsRegistry.PICKLE_BRINE, FluidsRegistry.PICKLE_BRINE_FLOWING);
    }
}
