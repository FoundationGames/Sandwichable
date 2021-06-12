package io.github.foundationgames.sandwichable.villager;

import com.google.common.collect.ImmutableMap;
import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import io.github.foundationgames.sandwichable.villager.SandwichMakerProfession.SellableSandwiches;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SandwichMakerProfession {

    public static final PointOfInterestType SANDWICH_MAKER_POI = PointOfInterestHelper.register(
            Util.id("sandwich_maker_poi"),
            1,
            1,
            BlocksRegistry.SANDWICH_TABLE
    );
    public static final VillagerProfession SANDWICH_MAKER = VillagerProfessionBuilder.create()
            .id(Util.id("sandwich_maker"))
            .workstation(SANDWICH_MAKER_POI)
            .workSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM)
            .build()
    ;

    public static void init() {
        Registry.register(Registry.VILLAGER_PROFESSION, Util.id("sandwich_maker"), SANDWICH_MAKER);
        TradeOffers.PROFESSION_TO_LEVELED_TRADE.put(
                SANDWICH_MAKER, Util.copyToFastUtilMap(ImmutableMap.of(
                1,
                    new TradeOffers.Factory[]{
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(Items.WHEAT, 20, 16, 2),
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(Items.BREAD, 6, 12, 2),
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(ItemsRegistry.TOMATO, 18, 16, 2),
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(ItemsRegistry.LETTUCE_HEAD, 14, 16, 2),
                        new SandwichMakerProfession.SellItemFactory(ItemsRegistry.BREAD_SLICE, 1, 10, 16, 1)
                    },
                2,
                    new TradeOffers.Factory[]{
                        new SandwichMakerProfession.SellSandwichFactory(5, SellableSandwiches.APPLE.getItems(), 6, 7),
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(Items.BUCKET, 1, 12, 5),
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(Items.PORKCHOP, 18, 12, 3),
                        new SandwichMakerProfession.SellItemFactory(ItemsRegistry.CHEESE_SLICE_REGULAR, 2, 10, 4, 1),
                    },
                3,
                    new TradeOffers.Factory[]{
                        new SandwichMakerProfession.SellSandwichFactory(10, SellableSandwiches.BACON_LETTUCE_TOMATO.getItems(), 6, 8),
                        new SandwichMakerProfession.SellSandwichFactory(10, SellableSandwiches.CHICKEN_CHEESE.getItems(), 6, 8),
                        new SandwichMakerProfession.SellItemFactory(ItemsRegistry.TOASTED_BREAD_SLICE, 1, 7, 16, 1),
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(Items.COOKED_BEEF, 14, 12, 5),
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(Items.POTATO, 20, 12, 4)
                    },
                4,
                    new TradeOffers.Factory[]{
                        new SandwichMakerProfession.SellSandwichFactory(16, SellableSandwiches.MEAT_LOVERS.getItems(), 6, 12),
                        new SandwichMakerProfession.SellSandwichFactory(16, SellableSandwiches.VEGETABLE.getItems(), 6, 12),
                        new SandwichMakerProfession.SellItemFactory(Items.CHARCOAL, 1, 3, 12, 5),
                        new SandwichMakerProfession.BuyForOneEmeraldFactory(Items.CARROT, 20, 16, 5)
                    },
                5,
                    new TradeOffers.Factory[]{
                        new SandwichMakerProfession.SellSandwichFactory(25, SellableSandwiches.GOLDEN_APPLE.getItems(), 6, 17),
                        new SandwichMakerProfession.SellCheeseFactory(4, 12, 6),
                    }
            ))
        );
    }

    static class BuyForOneEmeraldFactory implements TradeOffers.Factory {
        private final Item buy;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public BuyForOneEmeraldFactory(ItemConvertible item, int price, int maxUses, int experience) {
            this.buy = item.asItem();
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05F;
        }

        public TradeOffer create(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(this.buy, this.price);
            return new TradeOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.experience, this.multiplier);
        }
    }

    static class SellItemFactory implements TradeOffers.Factory {
        private final ItemStack sell;
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
            this(new ItemStack(block), price, count, maxUses, experience);
        }

        public SellItemFactory(Item item, int price, int count, int experience) {
            this((ItemStack)(new ItemStack(item)), price, count, 12, experience);
        }

        public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
            this(new ItemStack(item), price, count, maxUses, experience);
        }

        public SellItemFactory(ItemStack itemStack, int price, int count, int maxUses, int experience) {
            this(itemStack, price, count, maxUses, experience, 0.05F);
        }

        public SellItemFactory(ItemStack itemStack, int price, int count, int maxUses, int experience, float multiplier) {
            this.sell = itemStack;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public TradeOffer create(Entity entity, Random random) {
            return new TradeOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
        }
    }

    static class SellCheeseFactory implements TradeOffers.Factory {
        final int price;
        final int experience;
        final int maxUses;
        final Item[] cheeses = {ItemsRegistry.CHEESE_WHEEL_REGULAR, ItemsRegistry.CHEESE_WHEEL_CREAMY, ItemsRegistry.CHEESE_WHEEL_INTOXICATING, ItemsRegistry.CHEESE_WHEEL_SOUR};
        final Random random;

        public SellCheeseFactory(int price, int uses, int exp) {
            this.maxUses = uses;
            this.experience = exp;
            this.price = price;
            this.random = new Random();
        }

        @Nullable
        public TradeOffer create(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(cheeses[this.random.nextInt(cheeses.length)], 1);
            return new TradeOffer(new ItemStack(Items.EMERALD, this.price), itemStack, maxUses, this.experience, 0.2F);
        }
    }

    static class SellSandwichFactory implements TradeOffers.Factory {
        final DefaultedList<ItemStack> items;
        final int price;
        final int experience;
        final int maxUses;

        public SellSandwichFactory(int price, DefaultedList<ItemStack> sandwichItems, int uses, int exp) {
            this.items = sandwichItems.size() == 128 ? sandwichItems : DefaultedList.ofSize(128, ItemStack.EMPTY);
            this.maxUses = uses;
            this.experience = exp;
            this.price = price;
        }

        @Nullable
        public TradeOffer create(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(BlocksRegistry.SANDWICH, 1);
            itemStack.putSubTag("BlockEntityTag", Inventories.writeNbt(new NbtCompound(), this.items));
            return new TradeOffer(new ItemStack(Items.EMERALD, this.price), itemStack, maxUses, this.experience, 0.2F);
        }
    }

    enum SellableSandwiches {

        APPLE(new Item[]{Items.BREAD, Items.APPLE, Items.BREAD}),
        BACON_LETTUCE_TOMATO(new Item[]{ItemsRegistry.BREAD_SLICE, ItemsRegistry.BACON_STRIPS, ItemsRegistry.LETTUCE_LEAF, ItemsRegistry.TOMATO_SLICE, ItemsRegistry.BREAD_SLICE}),
        CHICKEN_CHEESE(new Item[]{ItemsRegistry.TOASTED_BREAD_SLICE, ItemsRegistry.CHEESE_SLICE_REGULAR, Items.COOKED_CHICKEN, ItemsRegistry.LETTUCE_LEAF, ItemsRegistry.TOASTED_BREAD_SLICE}),
        MEAT_LOVERS(new Item[]{Items.BREAD, ItemsRegistry.BACON_STRIPS, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_PORKCHOP, Items.BREAD}),
        VEGETABLE(new Item[]{Items.BREAD, ItemsRegistry.LETTUCE_LEAF, Items.CARROT, Items.BEETROOT, Items.BAKED_POTATO, ItemsRegistry.TOMATO_SLICE, Items.BREAD}),
        GOLDEN_APPLE(new Item[]{Items.BREAD, Items.GOLDEN_APPLE, Items.BREAD});

        DefaultedList<ItemStack> list;

        SellableSandwiches(Item[] items) {
            this.list = listFrom(items);
        }

        public DefaultedList<ItemStack> getItems() {
            return list;
        }

        private static DefaultedList<ItemStack> listFrom(Item[] items) {
            DefaultedList<ItemStack> list = DefaultedList.ofSize(128, ItemStack.EMPTY);
            for (int i = 0; i < items.length; i++) {
                list.set(i, new ItemStack(items[i], 1));
            }
            return list;
        }
    }

    static {
    }
}
