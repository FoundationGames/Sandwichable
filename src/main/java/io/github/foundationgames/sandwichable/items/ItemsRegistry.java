package io.github.foundationgames.sandwichable.items;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ItemsRegistry {

    public static final FoodComponent BREADSLICE = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.5F).build();
    public static final FoodComponent TOASTEDBREADSLICE = (new FoodComponent.Builder()).hunger(3).saturationModifier(0.8F).build();
    public static final FoodComponent LETTUCEHEAD = (new FoodComponent.Builder()).hunger(5).saturationModifier(1.2F).snack().build();
    public static final FoodComponent LETTUCELEAF = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.3F).snack().build();
    public static final FoodComponent CHEESEWHEEL = (new FoodComponent.Builder()).hunger(9).saturationModifier(6.9F).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 400, 5), 1.0F).build();
    public static final FoodComponent CHEESESLICE = (new FoodComponent.Builder()).hunger(3).saturationModifier(0.6F).snack().build();
    public static final FoodComponent TOMATO_FOOD = (new FoodComponent.Builder()).hunger(4).saturationModifier(3.3F).build();
    public static final FoodComponent TOMATOSLICE = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.7F).build();
    public static final FoodComponent BACON = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.7F).build();
    public static final FoodComponent PORKCUTS = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.3F).build();
    public static final FoodComponent BURNTFOOD = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build();

    public static final Item BREAD_SLICE = new Item(new Item.Settings().food(BREADSLICE).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item TOASTED_BREAD_SLICE = new Item(new Item.Settings().food(TOASTEDBREADSLICE).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item LETTUCE_HEAD = new Item(new Item.Settings().food(LETTUCEHEAD).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item LETTUCE_LEAF = new Item(new Item.Settings().food(LETTUCELEAF).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item CHEESE_WHEEL = new Item(new Item.Settings().food(CHEESEWHEEL).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item CHEESE_SLICE = new Item(new Item.Settings().food(CHEESESLICE).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item TOMATO = new Item(new Item.Settings().food(TOMATO_FOOD).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item TOMATO_SLICE = new Item(new Item.Settings().food(TOMATOSLICE).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item BACON_STRIPS = new Item(new Item.Settings().food(BACON).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item PORK_CUTS = new Item(new Item.Settings().food(PORKCUTS).group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item BURNT_FOOD = new Item(new Item.Settings().food(BURNTFOOD).group(Sandwichable.SANDWICHABLE_ITEMS));

    public static final Item KITCHEN_KNIFE = new Item(new Item.Settings().maxDamage(5).group(Sandwichable.SANDWICHABLE_ITEMS));

    public static final Item LETTUCE_SEEDS = new AliasedBlockItem(BlocksRegistry.LETTUCE, new Item.Settings().group(Sandwichable.SANDWICHABLE_ITEMS));
    public static final Item TOMATO_SEEDS = new AliasedBlockItem(BlocksRegistry.TOMATOES, new Item.Settings().group(Sandwichable.SANDWICHABLE_ITEMS));

    public static void init() {
        registerItem(KITCHEN_KNIFE, "kitchen_knife");
        registerItem(BREAD_SLICE, "bread_slice");
        registerItem(TOASTED_BREAD_SLICE, "toasted_bread_slice");
        registerItem(LETTUCE_HEAD, "lettuce_head");
        registerItem(LETTUCE_LEAF, "lettuce_leaf");
        registerItem(CHEESE_WHEEL, "cheese_wheel");
        registerItem(CHEESE_SLICE, "cheese_slice");
        registerItem(TOMATO, "tomato");
        registerItem(TOMATO_SLICE, "tomato_slice");
        registerItem(PORK_CUTS, "pork_cuts");
        registerItem(BACON_STRIPS, "bacon_strips");
        registerItem(LETTUCE_SEEDS, "lettuce_seeds");
        registerItem(TOMATO_SEEDS, "tomato_seeds");
        registerItem(BURNT_FOOD, "burnt_food");
    }

    private static void registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, Util.id(name), item);
    }
}
