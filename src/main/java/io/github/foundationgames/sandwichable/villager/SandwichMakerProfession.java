package io.github.foundationgames.sandwichable.villager;

import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class SandwichMakerProfession {

    private static final Identifier SANDWICH_MAKER_POI_ID = Util.id("sandwich_maker_poi");
    public static final PointOfInterestType SANDWICH_MAKER_POI = PointOfInterestHelper.register(
            SANDWICH_MAKER_POI_ID,
            1,
            1,
            BlocksRegistry.SANDWICH_TABLE
    );

    public static final VillagerProfession SANDWICH_MAKER = VillagerProfessionBuilder.create()
            .id(Util.id("sandwich_maker"))
            .workstation(RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, SANDWICH_MAKER_POI_ID))
            .workSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM)
            .build();

    public static void init() {
        Registry.register(Registry.VILLAGER_PROFESSION, Util.id("sandwich_maker"), SANDWICH_MAKER);
        TradeOfferHelper.registerVillagerOffers(SANDWICH_MAKER, 1, factories -> {
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.WHEAT, 20, 16, 2));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.BREAD, 6, 12, 2));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(ItemsRegistry.TOMATO, 18, 16, 2));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(ItemsRegistry.LETTUCE_HEAD, 14, 16, 2));
            factories.add(new TradeOffers.SellItemFactory(ItemsRegistry.BREAD_SLICE, 1, 10, 16, 1));
        });
        TradeOfferHelper.registerVillagerOffers(SANDWICH_MAKER, 2, factories -> {
            factories.add(new SandwichMakerProfession.SellSandwichFactory(5, SellableSandwiches.APPLE.getItems(), 6, 7));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.BUCKET, 1, 12, 5));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.PORKCHOP, 18, 12, 3));
            factories.add(new TradeOffers.SellItemFactory(ItemsRegistry.CHEESE_SLICE_REGULAR, 2, 10, 4, 1));
        });
        TradeOfferHelper.registerVillagerOffers(SANDWICH_MAKER, 3, factories -> {
            factories.add(new SandwichMakerProfession.SellSandwichFactory(10, SellableSandwiches.BACON_LETTUCE_TOMATO.getItems(), 6, 8));
            factories.add(new SandwichMakerProfession.SellSandwichFactory(10, SellableSandwiches.CHICKEN_CHEESE.getItems(), 6, 8));
            factories.add(new TradeOffers.SellItemFactory(ItemsRegistry.TOASTED_BREAD_SLICE, 1, 7, 16, 1));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.COOKED_BEEF, 14, 12, 5));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.POTATO, 20, 12, 4));
        });
        TradeOfferHelper.registerVillagerOffers(SANDWICH_MAKER, 4, factories -> {
            factories.add(new SandwichMakerProfession.SellSandwichFactory(16, SellableSandwiches.MEAT_LOVERS.getItems(), 6, 12));
            factories.add(new SandwichMakerProfession.SellSandwichFactory(16, SellableSandwiches.VEGETABLE.getItems(), 6, 12));
            factories.add(new TradeOffers.SellItemFactory(Items.CHARCOAL, 1, 3, 12, 5));
            factories.add(new TradeOffers.BuyForOneEmeraldFactory(Items.CARROT, 20, 16, 5));
        });
        TradeOfferHelper.registerVillagerOffers(SANDWICH_MAKER, 5, factories -> {
            factories.add(new SandwichMakerProfession.SellSandwichFactory(25, SellableSandwiches.GOLDEN_APPLE.getItems(), 6, 17));
            factories.add(new SandwichMakerProfession.SellCheeseFactory(4, 12, 6));
        });
    }

    public static class SellCheeseFactory implements TradeOffers.Factory {
        final int price;
        final int experience;
        final int maxUses;
        final Item[] cheeses = {ItemsRegistry.CHEESE_WHEEL_REGULAR, ItemsRegistry.CHEESE_WHEEL_CREAMY, ItemsRegistry.CHEESE_WHEEL_INTOXICATING, ItemsRegistry.CHEESE_WHEEL_SOUR};

        public SellCheeseFactory(int price, int uses, int exp) {
            this.maxUses = uses;
            this.experience = exp;
            this.price = price;
        }

        @Nullable
        public TradeOffer create(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(cheeses[random.nextInt(cheeses.length)], 1);
            return new TradeOffer(new ItemStack(Items.EMERALD, this.price), itemStack, maxUses, this.experience, 0.2F);
        }
    }

    public static class SellSandwichFactory implements TradeOffers.Factory {
        final int price;
        final Item[] items;
        final int experience;
        final int maxUses;

        public SellSandwichFactory(int price, Item[] items, int uses, int exp) {
            this.items = items;
            this.maxUses = uses;
            this.experience = exp;
            this.price = price;
        }

        @Nullable
        public TradeOffer create(Entity entity, Random random) {
            ItemStack stack = new ItemStack(BlocksRegistry.SANDWICH, 1);
            DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(items.length);
            defaultedList.addAll(Arrays.stream(items).map(ItemStack::new).toList());
            stack.setSubNbt("BlockEntityTag", Inventories.writeNbt(new NbtCompound(), defaultedList));
            return new TradeOffer(new ItemStack(Items.EMERALD, this.price), stack, maxUses, this.experience, 0.2F);
        }
    }

    public enum SellableSandwiches {
        APPLE(new Item[]{Items.BREAD, Items.APPLE, Items.BREAD}),
        BACON_LETTUCE_TOMATO(new Item[]{ItemsRegistry.BREAD_SLICE, ItemsRegistry.BACON_STRIPS, ItemsRegistry.LETTUCE_LEAF, ItemsRegistry.TOMATO_SLICE, ItemsRegistry.BREAD_SLICE}),
        CHICKEN_CHEESE(new Item[]{ItemsRegistry.TOASTED_BREAD_SLICE, ItemsRegistry.CHEESE_SLICE_REGULAR, Items.COOKED_CHICKEN, ItemsRegistry.LETTUCE_LEAF, ItemsRegistry.TOASTED_BREAD_SLICE}),
        MEAT_LOVERS(new Item[]{Items.BREAD, ItemsRegistry.BACON_STRIPS, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_PORKCHOP, Items.BREAD}),
        VEGETABLE(new Item[]{Items.BREAD, ItemsRegistry.LETTUCE_LEAF, Items.CARROT, Items.BEETROOT, Items.BAKED_POTATO, ItemsRegistry.TOMATO_SLICE, Items.BREAD}),
        GOLDEN_APPLE(new Item[]{Items.BREAD, Items.GOLDEN_APPLE, Items.BREAD});

        Item[] items;

        SellableSandwiches(Item[] items) {
            this.items = items;
        }

        public Item[] getItems() {
            return items;
        }
    }
}
