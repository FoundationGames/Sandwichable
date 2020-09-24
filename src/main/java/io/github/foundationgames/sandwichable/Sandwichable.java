package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.entity.SandwichTableBlockEntity;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.SandwichableGroupIconBuilder;
import io.github.foundationgames.sandwichable.items.SpreadRegistry;
import io.github.foundationgames.sandwichable.items.spread.SpreadType;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipeSerializer;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipeSerializer;
import io.github.foundationgames.sandwichable.util.Util;
import io.github.foundationgames.sandwichable.villager.SandwichMakerProfession;
import io.github.foundationgames.sandwichable.worldgen.BiomesRegistry;
import io.github.foundationgames.sandwichable.worldgen.ConfiguredFeaturesRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class Sandwichable implements ModInitializer {

    public static final ItemGroup SANDWICHABLE_ITEMS = FabricItemGroupBuilder.build(Util.id("sandwichable"), SandwichableGroupIconBuilder::getIcon);

    public static final Tag<Item> BREADS = TagRegistry.item(Util.id("breads"));
    public static final Tag<Item> SANDWICHABLE_ITEMS_TAG = TagRegistry.item(Util.id("sandwichable_items"));
    public static final Tag<Item> METAL_ITEMS = TagRegistry.item(Util.id("metal_items"));

    @Override
    public void onInitialize() {

        BlocksRegistry.init();
        ItemsRegistry.init();
        SandwichMakerProfession.init();
        SpreadType.init();

        Registry.register(Registry.RECIPE_SERIALIZER, CuttingRecipeSerializer.ID, CuttingRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, Util.id(CuttingRecipe.Type.ID), CuttingRecipe.Type.INSTANCE);

        Registry.register(Registry.RECIPE_SERIALIZER, ToastingRecipeSerializer.ID, ToastingRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, Util.id(ToastingRecipe.Type.ID), ToastingRecipe.Type.INSTANCE);

        ContainerProviderRegistry.INSTANCE.registerFactory(Util.id("desalinator"), (syncId, identifier, player, buf) -> {
            final World world = player.world;
            final BlockPos pos = buf.readBlockPos();
            return world.getBlockState(pos).createScreenHandlerFactory(player.world, pos).createMenu(syncId, player.inventory, player);
        });

        DispenserBehavior foodBehavior = new ItemDispenserBehavior() {
            private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                if(pointer.getWorld().getBlockState(pos).getBlock() == BlocksRegistry.SANDWICH_TABLE) {
                    BlockEntity be = pointer.getWorld().getBlockEntity(pos);
                    if(be instanceof SandwichTableBlockEntity) {
                        if(((SandwichTableBlockEntity)be).getFoodList().get(0).getItem().isIn(Sandwichable.BREADS) || stack.getItem().isIn(Sandwichable.BREADS)) {
                            if(SpreadRegistry.INSTANCE.itemHasSpread(stack.getItem())) {
                                ItemStack spread = new ItemStack(ItemsRegistry.SPREAD, 1);
                                SpreadType type = SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem());
                                type.onPour(stack, spread);
                                spread.getOrCreateTag().putString("spreadType", SpreadRegistry.INSTANCE.serialize(SpreadRegistry.INSTANCE.getSpreadFromItem(stack.getItem())));
                                ((SandwichTableBlockEntity)be).addTopStackFrom(spread);
                                Util.sync((SandwichTableBlockEntity)be, pointer.getWorld());
                                return new ItemStack(type.getResultItem());
                            }
                            else ((SandwichTableBlockEntity)be).addTopStackFrom(stack);
                            Util.sync((SandwichTableBlockEntity)be, pointer.getWorld());
                            return stack;
                        }
                    }
                }
                return this.defaultBehavior.dispense(pointer, stack);
            }
        };
        for(ItemConvertible item : Registry.ITEM) {
            if(item.asItem().isFood() && item.asItem() != BlocksRegistry.SANDWICH.asItem()) {
                DispenserBlock.registerBehavior(item, foodBehavior);
            }
        }
        BiomesRegistry.init();
    }
}
